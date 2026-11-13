/* Alta de proyecto (ProyectoNuevo.jsp). Sin dependencias. */
(function () {
    'use strict';
    var script = document.currentScript;
    var CTX = script.getAttribute('data-ctx');
    var TOKEN = script.getAttribute('data-token');
    var $ = function (id) { return document.getElementById(id); };
    var forma = $('pr-forma');
    var abrTocada = false;
    var temporizador = null;
    var ultimaValida = false;

    function estado(t, tipo) {
        var e = $('pr-estado');
        e.textContent = t || '';
        e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
    }

    function pedir(params, metodo) {
        var cuerpo = new URLSearchParams(params);
        var op = { method: metodo || 'GET', credentials: 'same-origin', headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' } };
        var url = CTX + '/ProyectoNuevo';
        if (op.method === 'GET') { url += '?' + cuerpo; } else {
            op.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            op.headers['X-SIALSS-Token'] = TOKEN;
            op.body = cuerpo.toString();
        }
        return fetch(url, op).then(function (r) {
            return r.json().catch(function () { return {}; }).then(function (j) {
                if (!r.ok && !j.plan) { throw new Error(j.error || 'Error ' + r.status); }
                j.status = r.status;
                return j;
            });
        });
    }

    function campos() {
        var d = {};
        new FormData(forma).forEach(function (v, k) { d[k] = v; });
        return d;
    }

    function el(tag, texto, clase) {
        var n = document.createElement(tag);
        if (texto !== undefined) { n.textContent = texto; }
        if (clase) { n.className = clase; }
        return n;
    }

    function fila(dl, termino, valor, nota) {
        dl.appendChild(el('dt', termino));
        var dd = el('dd', valor);
        if (nota) { dd.appendChild(el('span', ' ' + nota, 'sialss-proy__nota')); }
        dl.appendChild(dd);
    }

    function pintarPlan(p) {
        var c = $('pr-previa');
        c.textContent = '';
        if (p.errores && p.errores.length) {
            var ul = el('ul', undefined, 'sialss-proy__errores');
            p.errores.forEach(function (e) { ul.appendChild(el('li', e)); });
            c.appendChild(el('p', 'Falta corregir:', 'sialss-proy__titulo-error'));
            c.appendChild(ul);
        }
        var dl = el('dl', undefined, 'sialss-proy__plan');
        fila(dl, 'Id del proyecto', String(p.id), '(el mismo en tb_ubicafact y tb_tipocause)');
        fila(dl, 'Nomenclatura', p.nomenclatura);
        fila(dl, 'Columna en tb_medica', p.campoMedica, p.crearCampoMedica ? '(se crea; no hay columnas libres)'
            : '(columna existente sin proyecto' + (p.insumosYaMarcados ? '; ya tiene ' + p.insumosYaMarcados + ' insumos marcados' : '') + ')');
        fila(dl, 'Índice de facturación', 'tb_indice.' + p.columnaIndice, p.crearColumnaIndice ? '(se crea, inicia en 1)' : '(ya existe)');
        fila(dl, 'Ubicaciones de ingreso', p.ingreso.join(', '), '(tb_ubica y tb_ubicanueva)');
        fila(dl, 'Ubicaciones de salida', p.salida.join(', '), '(tb_ubica)');
        fila(dl, 'tb_ubicafact', p.abreviatura + ' · ' + p.ubicaSql);
        fila(dl, 'tb_tipocause', p.abreviatura);
        c.appendChild(dl);
        ultimaValida = !p.errores || !p.errores.length;
        $('pr-guardar').disabled = !ultimaValida;
    }

    function previa() {
        var d = campos();
        if (!d.nombre.trim() || !d.abreviatura.trim()) {
            $('pr-previa').textContent = '';
            $('pr-previa').appendChild(el('p', 'Escriba el nombre y la abreviatura.', 'sialss-pin__ayuda'));
            $('pr-guardar').disabled = true;
            return;
        }
        d.accion = 'previa';
        pedir(d).then(pintarPlan).catch(function (e) { estado(e.message, 'error'); });
    }

    function programar() {
        clearTimeout(temporizador);
        temporizador = setTimeout(previa, 400);
    }

    forma.addEventListener('input', function (e) {
        if (e.target.id === 'pr-abreviatura') {
            abrTocada = true;
            e.target.value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
        }
        if (e.target.id === 'pr-rfc') { e.target.value = e.target.value.toUpperCase(); }
        programar();
    });

    $('pr-nombre').addEventListener('change', function () {
        if (abrTocada || !this.value.trim()) { return; }
        pedir({ accion: 'sugerir', nombre: this.value }).then(function (r) {
            if (!abrTocada) { $('pr-abreviatura').value = r.abreviatura; previa(); }
        });
    });

    forma.addEventListener('submit', function (ev) {
        ev.preventDefault();
        if (!ultimaValida) { return; }
        var d = campos();
        if (!window.confirm('¿Dar de alta el proyecto "' + d.nombre + '" (' + d.abreviatura + ')?\n\nSe crean sus ubicaciones, su índice y, si hace falta, columnas nuevas en la base. No se puede deshacer desde esta pantalla.')) {
            return;
        }
        d.accion = 'alta';
        $('pr-guardar').disabled = true;
        estado('Guardando…');
        pedir(d, 'POST').then(function (r) {
            if (r.plan) { pintarPlan(r.plan); }
            if (r.status !== 200) { estado(r.error || 'Revise los datos.', 'error'); return; }
            estado(r.mensaje, 'ok');
            pintarProyectos(r.proyectos);
            $('pr-guardar').disabled = true;
        }).catch(function (e) { estado(e.message, 'error'); $('pr-guardar').disabled = false; });
    });

    function pintarProyectos(lista) {
        var t = $('pr-proyectos');
        t.textContent = '';
        lista.forEach(function (p) {
            var tr = el('tr');
            [p.id, p.nombre, p.nomenclatura, p.campo].forEach(function (v) { tr.appendChild(el('td', v)); });
            t.appendChild(tr);
        });
    }

    pedir({ accion: 'base' }).then(function (r) {
        $('pr-qfb1').value = r.responsables['2504'] || '';
        $('pr-qfb2').value = r.responsables['2505'] || '';
        pintarProyectos(r.proyectos);
    }).catch(function (e) { estado(e.message, 'error'); });
})();
