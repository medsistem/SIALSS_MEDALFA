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

    // El PDF se abre en una ventana aparte (siempre la misma, "sialss-etiquetas-pdf").
    // 1) La ventana se abre dentro del clic para que el navegador no la bloquee.
    // 2) La selección se envía con fetch (POST) y el servidor contesta la dirección GET del PDF.
    // 3) La ventana navega a esa dirección. No se envía el formulario a otra ventana porque
    //    algunos navegadores integrados abren el "action" como GET y pierden los datos.
    var NOMBRE_VENTANA = 'sialss-etiquetas-pdf';
    function caracteristicasVentana() {
        var ancho = Math.min(1000, screen.availWidth - 40);
        var alto = Math.min(800, screen.availHeight - 60);
        return 'popup=yes,width=' + ancho + ',height=' + alto
            + ',left=' + Math.round((screen.availWidth - ancho) / 2) + ',top=' + Math.round((screen.availHeight - alto) / 2)
            + ',resizable=yes,scrollbars=yes';
    }
    function mostrarEnlace(url, texto) {
        var e = $('etq-estado');
        e.textContent = texto + ' ';
        e.className = 'sialss-mr__estado sialss-mr__estado--ok';
        var a = document.createElement('a');
        a.href = url; a.target = NOMBRE_VENTANA; a.textContent = 'Abrir PDF';
        e.appendChild(a);
    }

    $('etq-generar').addEventListener('click', function () {
        var boton = this;
        $('etq-claves').value = Array.from(seleccion).join('\n');
        var datos = new URLSearchParams(new FormData($('forma-etq')));
        datos.set('respuesta', 'json');
        var total = seleccion.size;
        var cuantas = total === 1 ? '1 etiqueta' : total + ' etiquetas';

        var ventana = null;
        try { ventana = window.open('', NOMBRE_VENTANA, caracteristicasVentana()); } catch (e) { ventana = null; }
        if (ventana) {
            try {
                ventana.document.title = 'Generando PDF…';
                ventana.document.body.style.cssText = 'font-family:Segoe UI,Arial,sans-serif;padding:24px';
                ventana.document.body.textContent = 'Generando el PDF de ' + cuantas + '…';
            } catch (e) { /* ventana de otro origen: se reutiliza tal cual */ }
        }

        boton.disabled = true;
        estado('Generando el PDF de ' + cuantas + '…');
        fetch(CTX + '/EtiquetasUbicacion', {
            method: 'POST', body: datos, credentials: 'same-origin',
            headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
        }).then(function (r) {
            return r.json().catch(function () { return {}; }).then(function (j) {
                if (!r.ok || !j.url) { throw new Error(j.error || 'No se pudo generar el PDF (' + r.status + ').'); }
                return j;
            });
        }).then(function (j) {
            if (ventana && !ventana.closed) {
                ventana.location.href = j.url;
                ventana.focus();
                estado('Se abrió el PDF en una ventana aparte (' + cuantas + ').', 'ok');
            } else if (window.open(j.url, NOMBRE_VENTANA, caracteristicasVentana())) {
                estado('Se abrió el PDF en una ventana aparte (' + cuantas + ').', 'ok');
            } else {
                mostrarEnlace(j.url, 'El PDF está listo (' + cuantas + '), pero el navegador bloqueó la ventana aparte.');
            }
        }).catch(function (e) {
            if (ventana && !ventana.closed) { ventana.close(); }
            estado(e.message, 'error');
        }).then(function () {
            boton.disabled = seleccion.size === 0 || seleccion.size > MAXIMO;
        });
    });

    pedir({ accion: 'secciones' }).then(function (r) {
        var sel = $('etq-seccion');
        r.secciones.forEach(function (s) {
            var o = document.createElement('option'); o.value = s; o.textContent = s; sel.appendChild(o);
        });
    }).catch(function (e) { estado(e.message, 'error'); });
})();
