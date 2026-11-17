/* Perfiles de usuario (PerfilesUsuario.jsp). Sin dependencias. Servlet /PerfilesUsuario. */
(function () {
  'use strict';
  var guion = document.currentScript;
  var CTX = guion.getAttribute('data-ctx');
  var TOKEN = guion.getAttribute('data-token');
  var $ = function (id) { return document.getElementById(id); };
  var conEstatus = false;
  var miPerfil = null;

  function estado(t, tipo) {
    var e = $('pf-estado');
    e.textContent = t || '';
    e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
  }

  function pedir(metodo, datos) {
    var op = { method: metodo, credentials: 'same-origin', headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' } };
    if (datos) { op.body = datos; op.headers['X-SIALSS-Token'] = TOKEN; }
    return fetch(CTX + '/PerfilesUsuario', op).then(function (r) {
      return r.json().catch(function () { return {}; }).then(function (j) {
        if (!r.ok) { throw new Error(j.error || 'No se pudo completar (' + r.status + ').'); }
        return j;
      });
    });
  }

  function celda(t) { var td = document.createElement('td'); td.textContent = t == null ? '' : t; return td; }

  function pintar(lista) {
    var tb = $('pf-lista');
    tb.innerHTML = '';
    lista.forEach(function (p) {
      var tr = document.createElement('tr');
      tr.appendChild(celda(p.id));
      tr.appendChild(celda(p.nombre));
      tr.appendChild(celda(p.usuariosActivos + (p.usuarios !== p.usuariosActivos ? ' (de ' + p.usuarios + ')' : '')));
      tr.appendChild(celda(p.activo ? 'Activo' : 'Suspendido'));
      var td = document.createElement('td');
      var propio = String(p.id) === String(miPerfil);
      if (conEstatus && !(p.activo && (p.id === 100 || propio))) {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'sialss-boton' + (p.activo ? '' : ' sialss-boton--primario');
        b.textContent = p.activo ? 'Suspender' : 'Reactivar';
        b.setAttribute('aria-label', (p.activo ? 'Suspender' : 'Reactivar') + ' el perfil ' + p.nombre);
        b.addEventListener('click', function () { cambiar(p, b); });
        td.appendChild(b);
      } else if (p.activo && (p.id === 100 || propio)) {
        td.textContent = p.id === 100 ? 'No se suspende' : 'Es su perfil';
      }
      tr.appendChild(td);
      tb.appendChild(tr);
    });
  }

  function cargar() {
    return pedir('GET').then(function (r) {
      conEstatus = r.conEstatus;
      miPerfil = r.miPerfil;
      $('pf-sin-estatus').hidden = conEstatus;
      pintar(r.perfiles);
    }).catch(function (e) { estado(e.message, 'error'); });
  }

  function cambiar(p, boton) {
    if (p.activo && !window.confirm('¿Suspender el perfil ' + p.nombre + '? Sus ' + p.usuariosActivos
        + ' usuarios activos ya no podrán iniciar sesión.')) { return; }
    var d = new URLSearchParams();
    d.set('accion', 'estatus');
    d.set('id', p.id);
    d.set('activo', String(!p.activo));
    boton.disabled = true;
    pedir('POST', d).then(function (r) { estado(r.mensaje, 'ok'); return cargar(); })
      .catch(function (e) { estado(e.message, 'error'); boton.disabled = false; });
  }

  $('pf-alta').addEventListener('submit', function (ev) {
    ev.preventDefault();
    var nombre = $('pf-nombre').value.trim();
    if (!nombre) { estado('Escribe el nombre del perfil.', 'error'); return; }
    var d = new URLSearchParams();
    d.set('accion', 'alta');
    d.set('nombre', nombre);
    var b = $('pf-guardar');
    b.disabled = true;
    pedir('POST', d).then(function (r) { $('pf-nombre').value = ''; estado(r.mensaje, 'ok'); return cargar(); })
      .catch(function (e) { estado(e.message, 'error'); })
      .then(function () { b.disabled = false; });
  });

  cargar();
})();
