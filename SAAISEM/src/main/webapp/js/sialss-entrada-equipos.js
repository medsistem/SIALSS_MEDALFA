/* Entrada de equipos (EntradaEquipos.jsp). Sin dependencias. Servlet /EntradaEquipos. */
(function () {
  'use strict';
  var CTX = document.currentScript.getAttribute('data-ctx');
  var $ = function (id) { return document.getElementById(id); };
  var OTRA = '__otra__';

  function estado(t, tipo) {
    var e = $('eq-estado');
    e.textContent = t || '';
    e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
  }

  function respuesta(r) {
    return r.json().catch(function () { return {}; }).then(function (j) {
      if (!r.ok) { throw new Error(j.error || 'No se pudo completar (' + r.status + ').'); }
      return j;
    });
  }

  function pedir(params) {
    return fetch(CTX + '/EntradaEquipos?' + new URLSearchParams(params), {
      headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, credentials: 'same-origin'
    }).then(respuesta);
  }

  function opciones(select, lista, vacio, conOtra) {
    select.innerHTML = '';
    var o = document.createElement('option');
    o.value = ''; o.textContent = vacio; select.appendChild(o);
    lista.forEach(function (x) {
      var op = document.createElement('option');
      op.value = x.id; op.textContent = x.nombre; select.appendChild(op);
    });
    if (conOtra) {
      var n = document.createElement('option');
      n.value = OTRA; n.textContent = '— Otra (escribir) —'; select.appendChild(n);
    }
  }

  function enlazarOtra(select, texto) {
    select.addEventListener('change', function () {
      var otra = select.value === OTRA;
      texto.hidden = !otra;
      if (otra) { texto.focus(); } else { texto.value = ''; }
    });
  }

  // Catálogos ----------------------------------------------------------------------
  pedir({ accion: 'catalogos' }).then(function (c) {
    opciones($('eq-marca'), c.marcas, '-- Seleccione --', true);
    opciones($('eq-modelo'), c.modelos, '-- Sin modelo --', true);
    opciones($('eq-proyecto'), c.proyectos, '-- Seleccione --');
    opciones($('eq-origen'), c.origenes, '-- Seleccione --');
    opciones($('eq-proveedor'), c.proveedores, '-- Seleccione --');
    var cond = $('eq-condicion');
    c.condiciones.forEach(function (x) { var o = document.createElement('option'); o.value = x; o.textContent = x; cond.appendChild(o); });
    var dl = $('eq-categorias');
    c.categorias.forEach(function (x) { var o = document.createElement('option'); o.value = x; dl.appendChild(o); });
  }).catch(function (e) { estado(e.message, 'error'); });
  enlazarOtra($('eq-marca'), $('eq-marca-nueva'));
  enlazarOtra($('eq-modelo'), $('eq-modelo-nuevo'));

  // Búsqueda de insumo por clave o descripción ------------------------------------------
  var espera = null;
  function elegir(x) {
    $('eq-clave').value = x.clave;
    $('eq-elegido').textContent = x.clave + ' · ' + x.descripcion;
    $('eq-resultados').innerHTML = '';
    $('eq-buscar').value = '';
  }
  $('eq-buscar').addEventListener('input', function () {
    clearTimeout(espera);
    var t = this.value.trim();
    if (t.length < 2) { $('eq-resultados').innerHTML = ''; return; }
    espera = setTimeout(function () {
      pedir({ accion: 'insumos', texto: t }).then(function (r) {
        var ul = $('eq-resultados');
        ul.innerHTML = '';
        if (!r.insumos.length) {
          var li = document.createElement('li'); li.className = 'sialss-mr__vacio'; li.textContent = 'Sin resultados.'; ul.appendChild(li);
          return;
        }
        r.insumos.forEach(function (x) {
          var li = document.createElement('li');
          var b = document.createElement('button');
          b.type = 'button'; b.textContent = x.clave + ' · ' + x.descripcion;
          b.addEventListener('click', function () { elegir(x); $('eq-marca').focus(); });
          li.appendChild(b); ul.appendChild(li);
        });
      }).catch(function (e) { estado(e.message, 'error'); });
    }, 250);
  });
  $('eq-buscar').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      e.preventDefault();
      var primero = $('eq-resultados').querySelector('button');
      if (primero) { primero.click(); }
    }
  });

  // Generar CB (y serie si está vacía) ------------------------------------------------
  $('eq-generar-cb').addEventListener('click', function () {
    var b = this;
    b.disabled = true;
    pedir({ accion: 'cb' }).then(function (r) {
      $('eq-cb').value = r.cb;
      if (!$('eq-serie').value.trim()) { $('eq-serie').value = r.cb; }
      estado('Código generado: ' + r.cb + '.', 'ok');
    }).catch(function (e) { estado(e.message, 'error'); }).then(function () { b.disabled = false; });
  });

  // Últimas entradas ------------------------------------------------------------------
  function celda(t) { var td = document.createElement('td'); td.textContent = t == null ? '' : t; return td; }
  function recientes() {
    pedir({ accion: 'recientes' }).then(function (r) {
      var tb = $('eq-recientes');
      tb.innerHTML = '';
      if (!r.equipos.length) {
        var tr = document.createElement('tr'); var td = celda('Todavía no hay entradas de equipos.');
        td.colSpan = 12; td.className = 'sialss-mr__vacio'; tr.appendChild(td); tb.appendChild(tr);
        return;
      }
      r.equipos.forEach(function (x) {
        var tr = document.createElement('tr');
        [x.fecha, x.clave, x.descripcion, x.serie, x.cb, x.marca, x.modelo, x.categoria, x.condicion, x.costo, x.ubicacion, x.folio]
          .forEach(function (v) { tr.appendChild(celda(v)); });
        tb.appendChild(tr);
      });
    }).catch(function (e) { estado(e.message, 'error'); });
  }
  recientes();

  // Guardar ------------------------------------------------------------------------
  function limpiarEquipo() {
    ['eq-clave', 'eq-serie', 'eq-cb', 'eq-costo', 'eq-tarima', 'eq-compra', 'eq-garantia', 'eq-accesorios', 'eq-notas',
     'eq-marca-nueva', 'eq-modelo-nuevo'].forEach(function (id) { $(id).value = ''; });
    $('eq-elegido').textContent = '';
    $('eq-buscar').focus();
  }
  $('eq-limpiar').addEventListener('click', function () { limpiarEquipo(); estado(''); });

  $('eq-forma').addEventListener('submit', function (ev) {
    ev.preventDefault();
    var faltan = [];
    if (!$('eq-clave').value) { faltan.push('el equipo'); }
    if (!$('eq-marca').value || ($('eq-marca').value === OTRA && !$('eq-marca-nueva').value.trim())) { faltan.push('la marca'); }
    ['eq-proyecto', 'eq-origen', 'eq-proveedor', 'eq-ubicacion'].forEach(function (id) {
      if (!$(id).value.trim()) { faltan.push($(id).labels[0].firstChild.textContent.trim().toLowerCase()); }
    });
    if (faltan.length) { estado('Falta: ' + faltan.join(', ') + '.', 'error'); return; }

    var datos = new URLSearchParams(new FormData(this));
    if (datos.get('marca') === OTRA) { datos.delete('marca'); }
    if (datos.get('modelo') === OTRA) { datos.delete('modelo'); }
    var boton = $('eq-guardar');
    boton.disabled = true;
    estado('Guardando…');
    fetch(CTX + '/EntradaEquipos', {
      method: 'POST', body: datos, credentials: 'same-origin',
      headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
    }).then(respuesta).then(function (r) {
      estado('Equipo guardado: serie ' + r.serie + ', CB ' + r.cb + ', folio de lote ' + r.folio + ', documento ' + r.documento + '.', 'ok');
      limpiarEquipo();
      recientes();
    }).catch(function (e) { estado(e.message, 'error'); }).then(function () { boton.disabled = false; });
  });
})();
