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
    ubicaciones = c.ubicaciones;
    filtrarUbicaciones();
    var cond = $('eq-condicion');
    c.condiciones.forEach(function (x) { var o = document.createElement('option'); o.value = x; o.textContent = x; cond.appendChild(o); });
    var dl = $('eq-categorias');
    c.categorias.forEach(function (x) { var o = document.createElement('option'); o.value = x; dl.appendChild(o); });
  }).catch(function (e) { estado(e.message, 'error'); });
  // Ubicaciones de ingreso (tb_ubicanueva): generales ('-') y las del proyecto elegido.
  var ubicaciones = [];
  function filtrarUbicaciones() {
    var sel = $('eq-ubicacion'), antes = sel.value, proyecto = $('eq-proyecto').value;
    var lista = ubicaciones.filter(function (u) { return u.proyecto === '-' || u.proyecto === proyecto; });
    opciones(sel, lista, lista.length ? '-- Seleccione --' : 'Sin ubicaciones de ingreso');
    if (lista.some(function (u) { return u.id === antes; })) { sel.value = antes; }
    else if (lista.length) { sel.value = lista[0].id; }
  }
  $('eq-proyecto').addEventListener('change', filtrarUbicaciones);
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

  // Empaque: mismo cálculo que la Entrada Manual (captura.jsp, totalPiezas) ----------------
  function num(id) { var v = parseInt(($(id).value || '').replace(/,/g, ''), 10); return isNaN(v) || v < 0 ? 0 : v; }
  function miles(n) { return n.toLocaleString('es-MX'); }
  function empaque() {
    var tc = num('eq-tarimas-c'), cxt = num('eq-cajas-xt'), pxc = num('eq-piezas-xc'),
        ti = num('eq-tarimas-i'), ci = num('eq-cajas-i'), resto = num('eq-resto');
    var vacio = !(tc || cxt || pxc || ti || ci || resto);
    var cajas = tc * cxt + ci;
    var piezas = vacio ? 1 : cajas * pxc + resto;
    var tarimas = tc + ti;
    if (tarimas === 0 && (resto > 0 || vacio)) { tarimas = vacio ? 0 : 1; }
    $('eq-total-tarimas').textContent = miles(tarimas);
    $('eq-total-cajas').textContent = miles(cajas + (resto > 0 ? 1 : 0));
    $('eq-total-piezas').textContent = miles(piezas);
    // Con una pieza es número de serie; con varias, lote.
    $('eq-serie-etiqueta').textContent = piezas === 1 ? 'No. de serie' : 'Lote';
    $('eq-serie-ayuda').textContent = piezas === 1
      ? 'Si no tiene, use "Generar CB": el código queda también como serie.'
      : 'Son ' + miles(piezas) + ' piezas: escriba el lote; si no tiene, se usa el código de barras.';
    return piezas;
  }
  Array.prototype.forEach.call(document.querySelectorAll('.eq-empaque'), function (el) {
    el.addEventListener('input', empaque);
  });

  // Formatos de Reimpresión de compras (acuse, marbetes de tarimas y de cajas) -----------------
  function ligas(x) {
    var base = { F_FolRemi: x.remision, F_OrdCom: x.oc, fol_gnkl: x.compra };
    function url(pagina, extra) { return CTX + '/' + pagina + '?' + new URLSearchParams(Object.assign({}, base, extra)); }
    return {
      acuse: url('reimpReporte.jsp', { fecha: x.fechaCompra }),
      tarimasC: url('reimp_marbete.jsp', { Ban: 1 }),
      tarimasA: url('reimp_marbete.jsp', { Ban: 2 }),
      cajas: url('reimp_marbeteCajas.jsp', { Ban: 2 })
    };
  }
  function abrir(ev) {
    var v = window.open(this.href, 'sialss-formato-equipo', 'width=1000,height=800,left=60,top=40');
    if (v) { ev.preventDefault(); v.focus(); }
  }
  function mostrarFormatos(x) {
    var l = ligas(x);
    Array.prototype.forEach.call($('eq-formatos').querySelectorAll('a[data-formato]'), function (a) {
      a.href = l[a.getAttribute('data-formato')];
      a.onclick = abrir;
    });
    $('eq-formatos').hidden = false;
  }

  // Últimas entradas ------------------------------------------------------------------
  function celda(t) { var td = document.createElement('td'); td.textContent = t == null ? '' : t; return td; }
  function recientes() {
    pedir({ accion: 'recientes' }).then(function (r) {
      var tb = $('eq-recientes');
      tb.innerHTML = '';
      if (!r.equipos.length) {
        var tr = document.createElement('tr'); var td = celda('Todavía no hay entradas de equipos.');
        td.colSpan = 14; td.className = 'sialss-mr__vacio'; tr.appendChild(td); tb.appendChild(tr);
        return;
      }
      r.equipos.forEach(function (x) {
        var tr = document.createElement('tr');
        [x.fecha, x.clave, x.descripcion, x.serie, x.cb, x.marca, x.modelo, x.categoria, x.condicion, x.costo, x.piezas, x.ubicacion, x.folio]
          .forEach(function (v) { tr.appendChild(celda(v)); });
        var td = document.createElement('td');
        if (x.compra) {
          var l = ligas(x), div = document.createElement('div');
          div.className = 'eq-formatos-fila';
          [['acuse', 'Acuse'], ['tarimasC', 'Tarimas C'], ['tarimasA', 'Tarimas A'], ['cajas', 'Cajas']].forEach(function (f) {
            var a = document.createElement('a');
            a.href = l[f[0]]; a.target = '_blank'; a.rel = 'noopener'; a.textContent = f[1]; a.onclick = abrir;
            div.appendChild(a);
          });
          td.appendChild(div);
        } else {
          td.textContent = 'Sin compra';
        }
        tr.appendChild(td);
        tb.appendChild(tr);
      });
    }).catch(function (e) { estado(e.message, 'error'); });
  }
  recientes();
  empaque();

  // Guardar ------------------------------------------------------------------------
  function limpiarEquipo() {
    ['eq-clave', 'eq-serie', 'eq-cb', 'eq-costo', 'eq-tarima', 'eq-compra', 'eq-garantia', 'eq-accesorios', 'eq-notas',
     'eq-marca-nueva', 'eq-modelo-nuevo', 'eq-tarimas-c', 'eq-cajas-xt', 'eq-piezas-xc', 'eq-tarimas-i', 'eq-cajas-i', 'eq-resto']
      .forEach(function (id) { $(id).value = ''; });
    empaque();
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
      estado('Entrada guardada: ' + miles(r.piezas) + (r.piezas === 1 ? ' pieza, serie ' : ' piezas, lote ') + r.serie + ', CB ' + r.cb
        + ', folio de lote ' + r.folio + ', documento ' + r.documento + ' (OC ' + r.oc + ', remisión ' + r.remision + ').', 'ok');
      mostrarFormatos(r);
      limpiarEquipo();
      recientes();
  empaque();
    }).catch(function (e) { estado(e.message, 'error'); }).then(function () { boton.disabled = false; });
  });
})();
