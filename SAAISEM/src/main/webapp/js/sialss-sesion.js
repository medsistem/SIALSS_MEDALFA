/* SIALSS: bloqueo de pantalla por inactividad y cierre de sesión (sin dependencias).
   El servidor decide (mx.medalfa.sialss.seguridad.SesionFilter); este script:
   - avisa al servidor mientras el usuario trabaja (teclado, ratón, toque, desplazamiento),
   - muestra la pantalla de bloqueo al cumplirse el tiempo y pide la contraseña,
   - manda al inicio de sesión cuando se cumple el tiempo de cierre.
   Configuración en el <script>: data-ctx, data-bloqueo (s), data-cierre (s), data-usuario, data-login, data-modo, data-volver. */
(function () {
  'use strict';

  var el = document.currentScript;
  if (!el) { return; }
  var ctx = el.getAttribute('data-ctx') || '';
  var bloqueoMs = (parseInt(el.getAttribute('data-bloqueo'), 10) || 300) * 1000;
  var cierreMs = (parseInt(el.getAttribute('data-cierre'), 10) || 3600) * 1000;
  var usuario = el.getAttribute('data-usuario') || '';
  var paginaLogin = el.getAttribute('data-login') || 'indexalmacen.jsp';
  var modoPagina = el.getAttribute('data-modo') === 'pagina';
  var volver = el.getAttribute('data-volver') || '/main_menu.jsp';

  var CLAVE_ACT = 'sialss.actividad';
  var CLAVE_DESB = 'sialss.desbloqueo';
  var ultima = Date.now();
  var ultimoAviso = 0;
  var bloqueada = false;
  var pendiente = false;   // hubo teclado/ratón/toque desde el último latido
  var dialogo = null;

  function guardar(k, v) { try { localStorage.setItem(k, String(v)); } catch (e) { /* sin almacenamiento */ } }
  function leer(k) { try { return parseInt(localStorage.getItem(k), 10) || 0; } catch (e) { return 0; } }

  function post(datos) {
    var cuerpo = Object.keys(datos).map(function (k) { return encodeURIComponent(k) + '=' + encodeURIComponent(datos[k]); }).join('&');
    return fetch(ctx + '/sesion', {
      method: 'POST', credentials: 'same-origin',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8', 'X-Requested-With': 'XMLHttpRequest' },
      body: cuerpo
    }).then(function (r) { return r.json().catch(function () { return {}; }); });
  }

  function irLogin(motivo) {
    location.href = ctx + '/' + paginaLogin + (motivo ? '?motivo=' + motivo : '');
  }

  function actividad() {
    if (bloqueada) { return; }
    var ahora = Date.now();
    ultima = ahora;
    pendiente = true;
    if (ahora - ultimoAviso > 5000) { guardar(CLAVE_ACT, ahora); ultimoAviso = ahora; }
  }

  function minutos(ms) {
    var m = Math.round(ms / 60000);
    return m + (m === 1 ? ' minuto' : ' minutos');
  }

  function mmss(ms) {
    var s = Math.max(0, Math.round(ms / 1000));
    var m = Math.floor(s / 60);
    s = s % 60;
    return m + ':' + (s < 10 ? '0' : '') + s;
  }

  function construir() {
    var d = document.createElement('div');
    d.className = 'sialss-bloqueo';
    d.setAttribute('role', 'dialog');
    d.setAttribute('aria-modal', 'true');
    d.setAttribute('aria-labelledby', 'sialss-bloqueo-titulo');
    d.innerHTML =
      '<form class="sialss-login sialss-bloqueo__tarjeta" novalidate>' +
      '  <div class="sialss-login__titulo">' +
      '    <img src="' + ctx + '/imagenes/LogoMedalfa.png" alt="Medalfa" width="175" height="34">' +
      '    <div><p class="sialss-login__sistema">SIALSS</p><p class="sialss-login__lema">Sesión bloqueada</p></div>' +
      '  </div>' +
      '  <h1 id="sialss-bloqueo-titulo">Pantalla bloqueada</h1>' +
      '  <p class="sialss-secundario">Por seguridad, la pantalla se bloqueó tras ' + minutos(bloqueoMs) +
      '    sin actividad. Escriba su contraseña o su PIN para continuar.</p>' +
      '  <p class="sialss-bloqueo__usuario"></p>' +
      '  <label for="sialss-bloqueo-pass">Contraseña o PIN</label>' +
      '  <div class="sialss-campo-contrasena">' +
      '    <input type="password" id="sialss-bloqueo-pass" autocomplete="current-password" required>' +
      '    <button type="button" class="sialss-boton-texto" aria-pressed="false" aria-controls="sialss-bloqueo-pass">Mostrar</button>' +
      '  </div>' +
      '  <p class="sialss-mensaje sialss-mensaje--error" role="alert" hidden></p>' +
      '  <button type="submit" class="sialss-boton sialss-boton--primario sialss-boton--ancho">Continuar</button>' +
      '  <p class="sialss-aviso-legal" aria-live="polite"></p>' +
      '  <div class="sialss-login__pie"><span>¿No es usted?</span><a href="' + ctx + '/' + paginaLogin + '">Cerrar sesión</a></div>' +
      '</form>';
    d.querySelector('.sialss-bloqueo__usuario').textContent = usuario ? 'Usuario: ' + usuario : '';
    var campo = d.querySelector('#sialss-bloqueo-pass');
    var ver = d.querySelector('.sialss-boton-texto');
    var error = d.querySelector('.sialss-mensaje');
    var enviar = d.querySelector('button[type=submit]');
    ver.addEventListener('click', function () {
      var mostrar = campo.type === 'password';
      campo.type = mostrar ? 'text' : 'password';
      ver.textContent = mostrar ? 'Ocultar' : 'Mostrar';
      ver.setAttribute('aria-pressed', mostrar ? 'true' : 'false');
      campo.focus();
    });
    d.querySelector('form').addEventListener('submit', function (ev) {
      ev.preventDefault();
      if (!campo.value) { error.textContent = 'Escriba su contraseña o su PIN.'; error.hidden = false; campo.focus(); return; }
      enviar.disabled = true;
      post({ accion: 'desbloquear', contrasena: campo.value }).then(function (r) {
        enviar.disabled = false;
        campo.value = '';
        if (r.ok) { desbloqueada(true); return; }
        if (r.cerrada) { irLogin('inactividad'); return; }
        error.textContent = r.mensaje || 'Contraseña o PIN incorrecto.';
        error.hidden = false;
        if (r.bloqueado) {
          enviar.disabled = true;
          campo.disabled = true;
          setTimeout(function () { location.href = ctx + '/' + (r.login || paginaLogin); }, 4000);
        } else {
          campo.focus();
        }
      }).catch(function () {
        enviar.disabled = false;
        error.textContent = 'No se pudo validar. Revise su conexión e intente de nuevo.';
        error.hidden = false;
      });
    });
    // Mantener el foco dentro del cuadro.
    d.addEventListener('keydown', function (ev) {
      if (ev.key !== 'Tab') { return; }
      var f = d.querySelectorAll('input:not([disabled]), button:not([disabled]), a[href]');
      if (!f.length) { return; }
      var primero = f[0], ultimoEl = f[f.length - 1];
      if (ev.shiftKey && document.activeElement === primero) { ev.preventDefault(); ultimoEl.focus(); }
      else if (!ev.shiftKey && document.activeElement === ultimoEl) { ev.preventDefault(); primero.focus(); }
    });
    return d;
  }

  function bloquear(avisarServidor) {
    if (bloqueada) { return; }
    bloqueada = true;
    pendiente = false;
    // Que el servidor también quede bloqueado en este momento (otras pestañas y páginas).
    if (avisarServidor) { post({ accion: 'bloquear' }).catch(function () { /* el servidor bloquea solo al cumplirse el tiempo */ }); }
    if (!dialogo) { dialogo = construir(); }
    document.body.appendChild(dialogo);
    document.body.classList.add('sialss-bloqueada');
    var error = dialogo.querySelector('.sialss-mensaje');
    error.hidden = true;
    setTimeout(function () { dialogo.querySelector('#sialss-bloqueo-pass').focus(); }, 30);
  }

  function desbloqueada(avisar) {
    if (avisar) { guardar(CLAVE_DESB, Date.now()); }
    if (modoPagina) { location.href = ctx + volver; return; }
    bloqueada = false;
    ultima = Date.now();
    guardar(CLAVE_ACT, ultima);
    document.body.classList.remove('sialss-bloqueada');
    if (dialogo && dialogo.parentNode) { dialogo.parentNode.removeChild(dialogo); }
  }

  function reloj() {
    var ahora = Date.now();
    var otra = leer(CLAVE_ACT);
    if (!bloqueada && otra > ultima) { ultima = otra; }   // actividad en otra pestaña
    var inactivo = ahora - ultima;
    if (inactivo >= cierreMs) { irLogin('inactividad'); return; }
    if (!bloqueada && inactivo >= bloqueoMs) { bloquear(true); }
    if (bloqueada && dialogo) {
      dialogo.querySelector('.sialss-aviso-legal').textContent =
        'Si no la desbloquea, la sesión se cerrará en ' + mmss(cierreMs - inactivo) + '.';
    }
  }

  function latido() {
    if (bloqueada || !pendiente) { return; }   // solo si el usuario hizo algo desde el último latido
    pendiente = false;
    post({ accion: 'latido' }).then(function (r) {
      if (r.cerrada) { irLogin('inactividad'); }
      else if (r.bloqueada) { bloquear(); }
    }).catch(function () { /* sin conexión: se reintenta en el siguiente latido */ });
  }

  ['mousemove', 'mousedown', 'keydown', 'touchstart', 'wheel', 'scroll'].forEach(function (t) {
    window.addEventListener(t, actividad, { passive: true, capture: true });
  });
  window.addEventListener('storage', function (ev) {
    if (ev.key === CLAVE_DESB && bloqueada && !modoPagina) { desbloqueada(false); }
  });
  function iniciar() {
    // Las pantallas con jQuery: si el servidor responde que la sesión está bloqueada o cerrada.
    if (window.jQuery) {
      window.jQuery(document).ajaxError(function (e, xhr) {
        var estado = xhr && xhr.getResponseHeader && xhr.getResponseHeader('X-SIALSS-Sesion');
        if (estado === 'bloqueada') { bloquear(); } else if (estado === 'cerrada') { irLogin('inactividad'); }
      });
    }
    if (modoPagina) {
      bloquear();
      // La cuenta regresiva parte de la última actividad registrada en el servidor.
      post({ accion: 'estado' }).then(function (r) {
        if (r.cerrada) { irLogin('inactividad'); return; }
        if (typeof r.segundosCierre === 'number') { ultima = Date.now() - (cierreMs - r.segundosCierre * 1000); }
      }).catch(function () { /* se queda con la hora de carga */ });
    }
    setInterval(reloj, 1000);
    setInterval(latido, 30000);
  }
  if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', iniciar); } else { iniciar(); }
})();
