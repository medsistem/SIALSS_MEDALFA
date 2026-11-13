/* Menús por perfil y usuario (MenuRoles.jsp). Sin dependencias: habla con el servlet /MenuRoles. */
(function () {
    'use strict';
    var script = document.currentScript;
    var CTX = script.getAttribute('data-ctx');
    var TOKEN = script.getAttribute('data-token');
    var $ = function (id) { return document.getElementById(id); };

    var datos = null;            // respuesta de accion=datos
    var perfilGuardado = {};     // id opción -> true (como está guardado)
    var usuarioGuardado = {};    // id opción -> 'P' | 'B'
    var usuarioActual = null;

    // ------------------------------------------------------------ utilidades
    function estado(texto, tipo) {
        var e = $('mr-estado');
        e.textContent = texto || '';
        e.className = 'sialss-mr__estado' + (tipo ? ' sialss-mr__estado--' + tipo : '');
    }

    function pedir(params, metodo) {
        var cuerpo = new URLSearchParams(params);
        var opciones = { method: metodo || 'GET', headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, credentials: 'same-origin' };
        var url = CTX + '/MenuRoles';
        if (opciones.method === 'GET') {
            url += '?' + cuerpo.toString();
        } else {
            opciones.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            opciones.headers['X-SIALSS-Token'] = TOKEN;
            opciones.body = cuerpo.toString();
        }
        return fetch(url, opciones).then(function (r) {
            return r.json().catch(function () { return {}; }).then(function (j) {
                if (!r.ok) { throw new Error(j.error || 'No se pudo completar la operación (' + r.status + ').'); }
                return j;
            });
        });
    }

    function el(etiqueta, atributos, hijos) {
        var n = document.createElement(etiqueta);
        Object.keys(atributos || {}).forEach(function (k) {
            if (k === 'texto') { n.textContent = atributos[k]; } else { n.setAttribute(k, atributos[k]); }
        });
        (hijos || []).forEach(function (h) { if (h) { n.appendChild(h); } });
        return n;
    }

    function opciones() {
        var r = [];
        datos.modulos.forEach(function (m) { m.opciones.forEach(function (o) { r.push({ m: m.nombre, o: o }); }); });
        return r;
    }

    function hayCambiosPerfil() {
        return Array.prototype.some.call(document.querySelectorAll('#mr-perfil-modulos input[data-op]'), function (c) {
            return c.checked !== !!perfilGuardado[c.getAttribute('data-op')];
        });
    }

    function hayCambiosUsuario() {
        return Array.prototype.some.call(document.querySelectorAll('#mr-usuario-opciones select'), function (s) {
            return s.value !== (usuarioGuardado[s.getAttribute('data-op')] || '');
        });
    }

    function sinGuardar() {
        return datos && (hayCambiosPerfil() || hayCambiosUsuario());
    }

    function confirmarDescartar() {
        return !sinGuardar() || window.confirm('Hay cambios sin guardar. ¿Descartarlos?');
    }

    window.addEventListener('beforeunload', function (e) {
        if (sinGuardar()) { e.preventDefault(); e.returnValue = ''; }
    });

    // ------------------------------------------------------------ carga
    function cargar() {
        estado('Cargando…');
        return pedir({ accion: 'datos', acceso: $('mr-acceso').value }).then(function (d) {
            datos = d;
            $('mr-sin-tablas').hidden = d.usaBase;
            ['mr-perfil-guardar', 'mr-usuario-guardar'].forEach(function (id) { $(id).disabled = !d.usaBase; });
            llenarPerfiles();
            llenarUsuarios('');
            usuarioActual = null;
            mostrarUsuario();
            estado('');
        }).catch(function (e) { estado(e.message, 'error'); });
    }

    // ------------------------------------------------------------ por perfil
    function llenarPerfiles() {
        var sel = $('mr-perfil');
        var previo = sel.value;
        sel.textContent = '';
        datos.perfiles.forEach(function (p) {
            sel.appendChild(el('option', { value: p.id, texto: p.id + ' · ' + p.nombre }));
        });
        if (previo && datos.perfiles.some(function (p) { return p.id === previo; })) { sel.value = previo; }
        else if (datos.yo.acceso === datos.acceso) { sel.value = datos.yo.perfil; }
        pintarPerfil();
    }

    function pintarPerfil() {
        var perfil = $('mr-perfil').value;
        var cont = $('mr-perfil-modulos');
        cont.textContent = '';
        perfilGuardado = {};
        datos.modulos.forEach(function (m, i) {
            var todos = el('input', { type: 'checkbox', 'data-modulo': i, 'aria-label': 'Todas las opciones de ' + m.nombre });
            var cuenta = el('small', { 'data-cuenta': i });
            var legend = el('legend', {}, [el('label', {}, [todos, document.createTextNode(m.nombre), cuenta])]);
            var fs = el('fieldset', { 'class': 'sialss-mr__modulo' }, [legend]);
            m.opciones.forEach(function (o) {
                var tiene = o.perfiles.indexOf(perfil) >= 0;
                if (tiene) { perfilGuardado[o.id] = true; }
                var c = el('input', { type: 'checkbox', 'data-op': o.id, 'data-modulo-de': i });
                c.checked = tiene;
                fs.appendChild(el('label', { 'class': 'sialss-mr__opcion', title: o.url }, [c, el('span', { texto: o.etiqueta })]));
            });
            cont.appendChild(fs);
        });
        resumenPerfil();
    }

    function resumenPerfil() {
        var total = 0, marcadas = 0;
        datos.modulos.forEach(function (m, i) {
            var cajas = document.querySelectorAll('#mr-perfil-modulos input[data-modulo-de="' + i + '"]');
            var n = Array.prototype.filter.call(cajas, function (c) { return c.checked; }).length;
            var todos = document.querySelector('#mr-perfil-modulos input[data-modulo="' + i + '"]');
            todos.checked = n === cajas.length;
            todos.indeterminate = n > 0 && n < cajas.length;
            document.querySelector('#mr-perfil-modulos [data-cuenta="' + i + '"]').textContent = n + ' de ' + cajas.length;
            total += cajas.length;
            marcadas += n;
        });
        $('mr-perfil-resumen').textContent = marcadas + ' de ' + total + ' opciones' + (hayCambiosPerfil() ? ' · cambios sin guardar' : '');
    }

    $('mr-perfil-modulos').addEventListener('change', function (e) {
        var t = e.target;
        if (t.hasAttribute('data-modulo')) {
            document.querySelectorAll('#mr-perfil-modulos input[data-modulo-de="' + t.getAttribute('data-modulo') + '"]')
                .forEach(function (c) { c.checked = t.checked; });
        }
        resumenPerfil();
    });

    function marcarTodo(valor) {
        document.querySelectorAll('#mr-perfil-modulos input[data-op]').forEach(function (c) { c.checked = valor; });
        resumenPerfil();
    }
    $('mr-perfil-todo').addEventListener('click', function () { marcarTodo(true); });
    $('mr-perfil-nada').addEventListener('click', function () { marcarTodo(false); });
    $('mr-perfil-deshacer').addEventListener('click', function () { pintarPerfil(); estado(''); });

    var perfilPrevio = null;
    $('mr-perfil').addEventListener('focus', function () { perfilPrevio = this.value; });
    $('mr-perfil').addEventListener('change', function () {
        if (perfilPrevio !== null && hayCambiosPerfil() && !window.confirm('Hay cambios sin guardar en el perfil anterior. ¿Descartarlos?')) {
            this.value = perfilPrevio;
            return;
        }
        perfilPrevio = this.value;
        pintarPerfil();
        estado('');
    });

    $('mr-perfil-guardar').addEventListener('click', function () {
        var ids = Array.prototype.filter.call(document.querySelectorAll('#mr-perfil-modulos input[data-op]'), function (c) { return c.checked; })
            .map(function (c) { return c.getAttribute('data-op'); });
        var boton = this;
        boton.disabled = true;
        estado('Guardando…');
        pedir({ accion: 'guardarPerfil', acceso: datos.acceso, perfil: $('mr-perfil').value, opciones: ids.join(',') }, 'POST')
            .then(function (r) {
                return cargar().then(function () { estado(r.mensaje, 'ok'); });
            })
            .catch(function (e) { estado(e.message, 'error'); })
            .then(function () { boton.disabled = !datos.usaBase; });
    });

    // ------------------------------------------------------------ por usuario
    function nombrePerfil(id) {
        var p = datos.perfiles.filter(function (x) { return x.id === id; })[0];
        return p ? p.nombre : 'Perfil ' + id;
    }

    function llenarUsuarios(filtro) {
        var sel = $('mr-usuario');
        var previo = sel.value;
        sel.textContent = '';
        sel.appendChild(el('option', { value: '', texto: datos.errorUsuarios ? datos.errorUsuarios : 'Elija un usuario' }));
        var f = filtro.trim().toLowerCase();
        datos.usuarios.filter(function (u) {
            return !f || (u.usuario + ' ' + u.nombre).toLowerCase().indexOf(f) >= 0;
        }).forEach(function (u) {
            sel.appendChild(el('option', { value: u.id, texto: u.usuario + ' · ' + u.nombre + (u.status === 'A' ? '' : ' (status ' + u.status + ')') }));
        });
        sel.disabled = !!datos.errorUsuarios;
        if (previo) { sel.value = previo; }
    }

    $('mr-buscar').addEventListener('input', function () { llenarUsuarios(this.value); });

    $('mr-usuario').addEventListener('change', function () {
        if (!confirmarDescartar()) { this.value = usuarioActual ? usuarioActual.id : ''; return; }
        var id = this.value;
        usuarioActual = datos.usuarios.filter(function (u) { return String(u.id) === id; })[0] || null;
        if (!usuarioActual) { mostrarUsuario(); return; }
        estado('Cargando…');
        pedir({ accion: 'usuario', acceso: datos.acceso, id: id }).then(function (r) {
            usuarioGuardado = r.excepciones || {};
            mostrarUsuario();
            estado('');
        }).catch(function (e) { estado(e.message, 'error'); });
    });

    function marca(texto, tipo) {
        return el('span', { 'class': 'sialss-mr__marca sialss-mr__marca--' + tipo, texto: texto });
    }

    function mostrarUsuario() {
        $('mr-usuario-detalle').hidden = !usuarioActual;
        $('mr-usuario-vacio').hidden = !!usuarioActual;
        if (!usuarioActual) { usuarioGuardado = {}; return; }
        var u = usuarioActual;
        $('mr-usuario-info').textContent = u.usuario + ' · ' + u.nombre + ' · Perfil ' + u.perfil + ' (' + nombrePerfil(u.perfil) + ')'
            + (u.status === 'A' ? '' : ' · status ' + u.status);
        var cuerpo = $('mr-usuario-opciones');
        cuerpo.textContent = '';
        var moduloPrevio = null;
        opciones().forEach(function (x) {
            if (x.m !== moduloPrevio) {
                moduloPrevio = x.m;
                cuerpo.appendChild(el('tr', {}, [el('th', { scope: 'rowgroup', colspan: '4', texto: x.m })]));
            }
            var o = x.o;
            var sel = el('select', { 'data-op': o.id, 'aria-label': 'Ajuste de ' + o.etiqueta });
            [['', 'Según su perfil'], ['P', 'Permitir'], ['B', 'Bloquear']].forEach(function (v) {
                sel.appendChild(el('option', { value: v[0], texto: v[1] }));
            });
            sel.value = usuarioGuardado[o.id] || '';
            var tr = el('tr', { 'data-fila': o.id }, [
                el('td', { texto: o.etiqueta, title: o.url }),
                el('td', {}, [o.perfiles.indexOf(u.perfil) >= 0 ? marca('La tiene', 'si') : marca('No la tiene', 'no')]),
                el('td', {}, [sel]),
                el('td', { 'data-resultado': o.id })
            ]);
            cuerpo.appendChild(tr);
        });
        resultadosUsuario();
    }

    function resultadosUsuario() {
        var u = usuarioActual, permitidas = 0, bloqueadas = 0, visibles = 0;
        opciones().forEach(function (x) {
            var o = x.o;
            var sel = document.querySelector('#mr-usuario-opciones select[data-op="' + o.id + '"]');
            var v = sel.value;
            var ve = v === 'B' ? false : (v === 'P' || o.perfiles.indexOf(u.perfil) >= 0);
            if (v === 'P') { permitidas++; }
            if (v === 'B') { bloqueadas++; }
            if (ve) { visibles++; }
            var celda = document.querySelector('#mr-usuario-opciones [data-resultado="' + o.id + '"]');
            celda.textContent = '';
            celda.appendChild(v === 'B' ? marca('Bloqueada', 'bloq') : (ve ? marca('La ve', 'si') : marca('No la ve', 'no')));
            document.querySelector('#mr-usuario-opciones tr[data-fila="' + o.id + '"]').classList
                .toggle('sialss-mr--cambio', v !== (usuarioGuardado[o.id] || ''));
        });
        $('mr-usuario-resumen').textContent = 'Ve ' + visibles + ' opciones · ' + permitidas + ' permitidas aparte · ' + bloqueadas + ' bloqueadas'
            + (hayCambiosUsuario() ? ' · cambios sin guardar' : '');
    }

    $('mr-usuario-opciones').addEventListener('change', resultadosUsuario);
    $('mr-usuario-limpiar').addEventListener('click', function () {
        document.querySelectorAll('#mr-usuario-opciones select').forEach(function (s) { s.value = ''; });
        resultadosUsuario();
    });
    $('mr-usuario-deshacer').addEventListener('click', function () { mostrarUsuario(); estado(''); });

    $('mr-usuario-guardar').addEventListener('click', function () {
        var permisos = Array.prototype.filter.call(document.querySelectorAll('#mr-usuario-opciones select'), function (s) { return s.value; })
            .map(function (s) { return s.getAttribute('data-op') + ':' + s.value; });
        var boton = this;
        boton.disabled = true;
        estado('Guardando…');
        var id = usuarioActual.id;
        pedir({ accion: 'guardarUsuario', acceso: datos.acceso, id: id, permisos: permisos.join(',') }, 'POST')
            .then(function (r) {
                return pedir({ accion: 'usuario', acceso: datos.acceso, id: id }).then(function (x) {
                    usuarioGuardado = x.excepciones || {};
                    mostrarUsuario();
                    estado(r.mensaje, 'ok');
                });
            })
            .catch(function (e) { estado(e.message, 'error'); })
            .then(function () { boton.disabled = !datos.usaBase; });
    });

    // ------------------------------------------------------------ historial
    function cargarCambios() {
        pedir({ accion: 'bitacora', acceso: $('mr-acceso').value }).then(function (r) {
            var cuerpo = $('mr-cambios');
            cuerpo.textContent = '';
            if (!r.cambios.length) {
                cuerpo.appendChild(el('tr', {}, [el('td', { colspan: '5', texto: datos && !datos.usaBase ? 'El historial empieza cuando existan las tablas de menú.' : 'Sin cambios registrados.' })]));
            }
            r.cambios.forEach(function (c) {
                cuerpo.appendChild(el('tr', {}, [el('td', { texto: c.fecha }), el('td', { texto: c.quien }), el('td', { texto: c.acceso }),
                    el('td', { texto: (c.tipo === 'PERFIL' ? 'Perfil ' : 'Usuario ') + c.destino }), el('td', { texto: c.detalle })]));
            });
        }).catch(function (e) { estado(e.message, 'error'); });
    }

    // ------------------------------------------------------------ pestañas
    var pestanas = ['perfil', 'usuario', 'cambios'];
    function mostrarPestana(nombre) {
        pestanas.forEach(function (p) {
            var activa = p === nombre;
            $('tab-' + p).setAttribute('aria-selected', activa ? 'true' : 'false');
            $('tab-' + p).tabIndex = activa ? 0 : -1;
            $('panel-' + p).hidden = !activa;
        });
        estado('');
        if (nombre === 'cambios') { cargarCambios(); }
    }
    pestanas.forEach(function (p, i) {
        var t = $('tab-' + p);
        t.addEventListener('click', function () { mostrarPestana(p); });
        t.addEventListener('keydown', function (e) {
            var d = e.key === 'ArrowRight' ? 1 : e.key === 'ArrowLeft' ? -1 : 0;
            if (!d) { return; }
            var sig = pestanas[(i + d + pestanas.length) % pestanas.length];
            $('tab-' + sig).focus();
            mostrarPestana(sig);
        });
    });

    var accesoPrevio = $('mr-acceso').value;
    $('mr-acceso').addEventListener('change', function () {
        if (!confirmarDescartar()) { this.value = accesoPrevio; return; }
        accesoPrevio = this.value;
        $('mr-buscar').value = '';
        cargar();
    });

    cargar();
})();
