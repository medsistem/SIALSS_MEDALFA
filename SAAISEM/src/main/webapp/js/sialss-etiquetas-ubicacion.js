/* Etiquetas de ubicación (EtiquetasUbicacion.jsp). Sin dependencias. */
(function () {
    'use strict';
    var script = document.currentScript;
    var CTX = script.getAttribute('data-ctx');
    var MAXIMO = parseInt(script.getAttribute('data-maximo'), 10);
    var $ = function (id) { return document.getElementById(id); };
    var seleccion = new Set();
    var visibles = [];

    function estado(t, tipo) {
        var e = $('etq-estado');
        e.textContent = t || '';
        e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
    }

    function pedir(params) {
        return fetch(CTX + '/EtiquetasUbicacion?' + new URLSearchParams(params), {
            headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, credentials: 'same-origin'
        }).then(function (r) {
            return r.json().catch(function () { return {}; }).then(function (j) {
                if (!r.ok) { throw new Error(j.error || 'No se pudo completar la consulta (' + r.status + ').'); }
                return j;
            });
        });
    }

    function celda(texto) { var td = document.createElement('td'); td.textContent = texto; return td; }

    function resumen() {
        var n = seleccion.size;
        $('etq-resumen').textContent = n + (n === 1 ? ' seleccionada' : ' seleccionadas') + (n > MAXIMO ? ' · máximo ' + MAXIMO + ' por PDF' : '');
        $('etq-generar').disabled = n === 0 || n > MAXIMO;
    }

    function pintar(lista, hayMas, maximoLista) {
        visibles = lista;
        var cuerpo = $('etq-lista');
        cuerpo.textContent = '';
        if (!lista.length) {
            var tr = document.createElement('tr');
            var td = celda('No hay ubicaciones con ese filtro.');
            td.colSpan = 4; td.className = 'sialss-mr__vacio';
            tr.appendChild(td); cuerpo.appendChild(tr);
        }
        lista.forEach(function (u) {
            var tr = document.createElement('tr');
            var td = document.createElement('td');
            var c = document.createElement('input');
            c.type = 'checkbox'; c.value = u.clave; c.checked = seleccion.has(u.clave);
            c.setAttribute('aria-label', 'Seleccionar ' + u.descripcion);
            td.appendChild(c); tr.appendChild(td);
            tr.appendChild(celda(u.descripcion)); tr.appendChild(celda(u.clave)); tr.appendChild(celda(u.seccion));
            cuerpo.appendChild(tr);
        });
        estado(lista.length + ' ubicaciones' + (hayMas ? ' (se muestran las primeras ' + maximoLista + '; afine la búsqueda)' : '') + '.');
        resumen();
    }

    function buscar() {
        estado('Buscando…');
        pedir({ accion: 'buscar', seccion: $('etq-seccion').value, texto: $('etq-texto').value })
            .then(function (r) { pintar(r.ubicaciones, r.hayMas, r.maximoLista); })
            .catch(function (e) { estado(e.message, 'error'); });
    }

    $('etq-lista').addEventListener('change', function (e) {
        if (e.target.type !== 'checkbox') { return; }
        if (e.target.checked) { seleccion.add(e.target.value); } else { seleccion.delete(e.target.value); }
        resumen();
    });
    $('etq-todas').addEventListener('click', function () {
        visibles.forEach(function (u) { seleccion.add(u.clave); });
        document.querySelectorAll('#etq-lista input[type=checkbox]').forEach(function (c) { c.checked = true; });
        resumen();
    });
    $('etq-ninguna').addEventListener('click', function () {
        seleccion.clear();
        document.querySelectorAll('#etq-lista input[type=checkbox]').forEach(function (c) { c.checked = false; });
        resumen();
    });
    $('etq-buscar').addEventListener('click', buscar);
    $('etq-texto').addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); buscar(); } });
    $('etq-seccion').addEventListener('change', buscar);

    function mostrarPapel() {
        var tipo = document.querySelector('input[name=tipo]:checked').value;
        $('etq-papel-campo').hidden = tipo !== 'POSTE';
    }
    document.querySelectorAll('input[name=tipo]').forEach(function (r) { r.addEventListener('change', mostrarPapel); });
    mostrarPapel();

    $('etq-generar').addEventListener('click', function () {
        $('etq-claves').value = Array.from(seleccion).join('\n');
        $('forma-etq').submit();
        estado('Se abrió el PDF en otra pestaña (' + seleccion.size + ' etiquetas).', 'ok');
    });

    pedir({ accion: 'secciones' }).then(function (r) {
        var sel = $('etq-seccion');
        r.secciones.forEach(function (s) {
            var o = document.createElement('option'); o.value = s; o.textContent = s; sel.appendChild(o);
        });
    }).catch(function (e) { estado(e.message, 'error'); });
})();
