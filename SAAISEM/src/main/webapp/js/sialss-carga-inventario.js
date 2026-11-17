/* Carga de inventario (CargaInventarioExcel.jsp). Sin dependencias. Servlet /CargaInventarioExcel. */
(function () {
  'use strict';
  var CTX = document.currentScript.getAttribute('data-ctx');
  var $ = function (id) { return document.getElementById(id); };
  var token = null;

  function estado(t, tipo) {
    var e = $('ci-estado');
    e.textContent = t || '';
    e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
  }

  function enviar(datos) {
    return fetch(CTX + '/CargaInventarioExcel', {
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

  function celda(t) { var td = document.createElement('td'); td.textContent = t == null ? '' : t; return td; }

  function mostrarErrores(lista, total) {
    var ul = $('ci-errores');
    ul.innerHTML = '';
    lista.forEach(function (t) { var li = document.createElement('li'); li.textContent = t; ul.appendChild(li); });
    if (total > lista.length) {
      var li = document.createElement('li'); li.textContent = '… y ' + (total - lista.length) + ' errores más.'; ul.appendChild(li);
    }
    ul.hidden = !lista.length;
  }

  function mostrarMuestra(filas) {
    var tb = $('ci-muestra');
    tb.innerHTML = '';
    if (!filas.length) {
      var tr = document.createElement('tr'); var td = celda('Sin renglones válidos.');
      td.colSpan = 9; td.className = 'sialss-mr__vacio'; tr.appendChild(td); tb.appendChild(tr);
      return;
    }
    filas.forEach(function (x) {
      var tr = document.createElement('tr');
      [x.renglon, x.clave, x.descripcion, x.lote, x.caducidad, x.cantidad, x.ubicacion, x.proyecto, x.tarima]
        .forEach(function (v) { tr.appendChild(celda(v)); });
      tb.appendChild(tr);
    });
  }

  function reiniciar() {
    token = null;
    $('ci-confirmar').hidden = true;
    $('ci-cancelar').hidden = true;
    $('ci-contrasena').value = '';
  }

  $('ci-revisar').addEventListener('click', function () {
    var archivo = $('ci-archivo').files[0];
    if (!archivo) { estado('Elige el archivo de Excel.', 'error'); return; }
    reiniciar();
    mostrarErrores([], 0);
    var datos = new FormData();
    datos.append('accion', 'revisar');
    datos.append('archivo', archivo);
    var b = this;
    b.disabled = true;
    estado('Revisando el archivo…');
    enviar(datos).then(function (r) {
      mostrarErrores(r.errores, r.totalErrores);
      mostrarMuestra(r.muestra);
      if (r.valida) {
        token = r.token;
        $('ci-resumen').textContent = cuantos(r.renglones, 'renglón', 'renglones') + ', ' + cuantos(r.piezas, 'pieza', 'piezas') + '.';
        $('ci-confirmar').hidden = false;
        $('ci-cancelar').hidden = false;
        estado('El archivo está correcto: ' + cuantos(r.renglones, 'renglón', 'renglones') + ' y ' + cuantos(r.piezas, 'pieza', 'piezas')
          + '. Confirma con tu contraseña para guardar.', 'ok');
        $('ci-contrasena').focus();
      } else if (r.totalErrores) {
        estado('El archivo tiene ' + cuantos(r.totalErrores, 'error', 'errores') + '. Corrígelo y vuelve a subirlo; no se guardó nada.', 'error');
      } else {
        estado('El archivo no tiene renglones para cargar.', 'error');
      }
    }).catch(function (e) { estado(e.message, 'error'); }).then(function () { b.disabled = false; });
  });

  $('ci-confirmar').addEventListener('submit', function (ev) {
    ev.preventDefault();
    var pass = $('ci-contrasena').value;
    if (!pass) { estado('Escribe tu contraseña para confirmar.', 'error'); return; }
    var datos = new URLSearchParams();
    datos.set('accion', 'confirmar');
    datos.set('token', token || '');
    datos.set('contrasena', pass);
    $('ci-contrasena').value = '';
    var b = $('ci-aplicar');
    b.disabled = true;
    estado('Guardando la carga…');
    enviar(datos).then(function (r) {
      reiniciar();
      $('ci-archivo').value = '';
      estado('Carga guardada con el documento ' + r.documento + ': ' + cuantos(r.piezas, 'pieza', 'piezas') + ', '
        + cuantos(r.lotesNuevos, 'renglón nuevo', 'renglones nuevos') + ' y ' + cuantos(r.lotesSumados, 'sumado', 'sumados')
        + ' a renglones que ya estaban en esa ubicación (usuario ' + r.usuario + ').', 'ok');
    }).catch(function (e) {
      if (e.datos && e.datos.cancelada) { reiniciar(); }
      estado(e.message, 'error');
      if (!$('ci-confirmar').hidden) { $('ci-contrasena').focus(); }
    }).then(function () { b.disabled = false; });
  });

  $('ci-cancelar').addEventListener('click', function () {
    var datos = new URLSearchParams();
    datos.set('accion', 'cancelar');
    enviar(datos).catch(function () { /* nada que deshacer */ });
    reiniciar();
    estado('Carga cancelada; no se guardó nada.');
  });
})();
