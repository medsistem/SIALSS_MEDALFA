/* Carga de Archivos (jspf/cargaArchivo.jspf). Sin dependencias. Servlet data-servlet (CargaArchivoServlet).
   Mismo flujo que sialss-carga-inventario.js: revisar → confirmar con contraseña (3 intentos) o cancelar. */
(function () {
  'use strict';
  var guion = document.currentScript;
  var URL_CARGA = guion.getAttribute('data-ctx') + '/' + guion.getAttribute('data-servlet');
  var $ = function (id) { return document.getElementById(id); };
  var token = null;
  var columnas = document.querySelectorAll('#ca-muestra').length
    ? $('ca-muestra').closest('table').querySelectorAll('thead th').length : 1;

  function estado(t, tipo) {
    var e = $('ca-estado');
    e.textContent = t || '';
    e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
  }

  function enviar(datos) {
    return fetch(URL_CARGA, {
      method: 'POST', body: datos, credentials: 'same-origin',
      headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
    }).then(function (r) {
      return r.json().catch(function () { return {}; }).then(function (j) {
        if (!r.ok) { var e = new Error(j.error || 'No se pudo completar (' + r.status + ').'); e.datos = j; throw e; }
        return j;
      });
    });
  }

  function cuantos(n, uno, varios) { return n + ' ' + (n === 1 ? uno : varios); }

  function mostrarErrores(lista, total) {
    var ul = $('ca-errores');
    ul.textContent = '';
    lista.forEach(function (t) { var li = document.createElement('li'); li.textContent = t; ul.appendChild(li); });
    if (total > lista.length) {
      var li = document.createElement('li'); li.textContent = '… y ' + cuantos(total - lista.length, 'error más', 'errores más') + '.'; ul.appendChild(li);
    }
    ul.hidden = !lista.length;
  }

  /* Repetidos que no se suben: aviso aparte (no es error), con la lista plegada. */
  function mostrarOmitidos(lista, total) {
    var caja = $('ca-omitidos');
    if (!caja) { return; }
    caja.hidden = !total;
    if (!total) { return; }
    $('ca-omitidos-titulo').textContent = (total === 1 ? 'Se omite ' : 'Se omiten ') + cuantos(total, 'renglón repetido', 'renglones repetidos')
      + ' (ya cargados o repetidos en el archivo). Ver la lista';
    var ul = $('ca-omitidos-lista');
    ul.textContent = '';
    (lista || []).forEach(function (t) { var li = document.createElement('li'); li.textContent = t; ul.appendChild(li); });
    if (total > (lista || []).length) {
      var li = document.createElement('li'); li.textContent = '… y ' + cuantos(total - lista.length, 'más', 'más') + '.'; ul.appendChild(li);
    }
    caja.open = false;
  }

  /* 50 renglones por página con desplazamiento (js/sialss-tabla-paginada.js). */
  function mostrarMuestra(filas, maximo) {
    SialssTablaPaginada.mostrar($('ca-muestra'), filas, columnas, function (f) { return f; }, maximo);
  }

  function reiniciar() {
    token = null;
    $('ca-confirmar').hidden = true;
    $('ca-cancelar').hidden = true;
    $('ca-contrasena').value = '';
  }

  $('ca-revisar').addEventListener('click', function () {
    var archivo = $('ca-archivo').files[0];
    if (!archivo) { estado('Elige el archivo de Excel.', 'error'); return; }
    if (archivo.size === 0) { estado('El archivo ' + archivo.name + ' está vacío (0 bytes). Elige el Excel con tus datos.', 'error'); return; }
    reiniciar();
    mostrarErrores([], 0);
    mostrarOmitidos([], 0);
    var datos = new FormData();
    datos.append('accion', 'revisar');
    datos.append('archivo', archivo);
    var b = this;
    b.disabled = true;
    estado('Revisando el archivo…');
    enviar(datos).then(function (r) {
      mostrarErrores(r.errores, r.totalErrores);
      mostrarOmitidos(r.omitidos, r.totalOmitidos || 0);
      mostrarMuestra(r.muestra, r.muestraMaxima);
      if (r.sinNuevos) {
        estado(r.resumen, 'aviso');
      } else if (r.valida) {
        token = r.token;
        $('ca-resumen').textContent = r.resumen;
        $('ca-confirmar').hidden = false;
        $('ca-cancelar').hidden = false;
        estado('El archivo está correcto: ' + r.resumen + ' Confirma con tu contraseña para guardar.', 'ok');
        $('ca-contrasena').focus();
      } else {
        estado('El archivo tiene ' + cuantos(r.totalErrores, 'error', 'errores') + '. Corrígelo y vuelve a subirlo; no se guardó nada.', 'error');
      }
    }).catch(function (e) { estado(e.message, 'error'); }).then(function () { b.disabled = false; });
  });

  $('ca-confirmar').addEventListener('submit', function (ev) {
    ev.preventDefault();
    var pass = $('ca-contrasena').value;
    if (!pass) { estado('Escribe tu contraseña para confirmar.', 'error'); return; }
    var datos = new URLSearchParams();
    datos.set('accion', 'confirmar');
    datos.set('token', token || '');
    datos.set('contrasena', pass);
    $('ca-contrasena').value = '';
    var b = $('ca-aplicar');
    b.disabled = true;
    estado('Guardando la carga…');
    enviar(datos).then(function (r) {
      reiniciar();
      $('ca-archivo').value = '';
      estado(r.mensaje, 'ok');
    }).catch(function (e) {
      if (e.datos && e.datos.cancelada) { reiniciar(); }
      estado(e.message, 'error');
      if (!$('ca-confirmar').hidden) { $('ca-contrasena').focus(); }
    }).then(function () { b.disabled = false; });
  });

  $('ca-cancelar').addEventListener('click', function () {
    var datos = new URLSearchParams();
    datos.set('accion', 'cancelar');
    enviar(datos).catch(function () { /* nada que deshacer */ });
    reiniciar();
    estado('Carga cancelada; no se guardó nada.');
  });
})();
