/*
 * Marbete Manual — versión corregida.
 *
 * IMPORTANTE: este archivo debe cargarse DESPUÉS de jQuery, select2 y
 * sweetalert, es decir al final del <body> y no en el <head>. Cargarlo en el
 * <head> es lo que producía "Uncaught ReferenceError: $ is not defined":
 * el navegador ejecuta este script antes de que exista jQuery.
 *
 * Ids de la pantalla:
 *   #ProyecMar   proyecto
 *   #ClaveMar    producto (clave)
 *   #loteMar     lote
 */
$(document).ready(function () {

    // Los tres selects los llena el JSP con scriptlets. Aquí solo se les
    // aplica select2 una sola vez, para que el de lote sea buscable: hoy
    // trae todos los lotes de la tabla y pasa de los dos mil renglones.
    $('#ProyecMar, #ClaveMar, #loteMar').select2({ width: '100%' });

    // Selects encadenados: proyecto -> claves de ese proyecto -> lotes de esa clave y proyecto.
    $('#ProyecMar').on('change', function () {
        var proyecto = $(this).val();
        reiniciar('#loteMar', '--Primero elija la clave--');
        if (!tieneValor(proyecto)) {
            reiniciar('#ClaveMar', '--Primero elija el proyecto--');
            return;
        }
        cargarOpciones('#ClaveMar', { accion: 'claves', proyecto: proyecto },
                '--Clave--', 'Este proyecto no tiene claves con lotes.');
    });

    $('#ClaveMar').on('change', function () {
        var proyecto = $('#ProyecMar').val();
        var clave = $(this).val();
        if (!tieneValor(proyecto) || !tieneValor(clave)) {
            reiniciar('#loteMar', '--Primero elija la clave--');
            return;
        }
        cargarOpciones('#loteMar', { accion: 'lotes', proyecto: proyecto, clave: clave },
                '--Lote--', 'Esta clave no tiene lotes en el proyecto.');
    });

    $('#generarMarbete').on('click', function () {

        // Cada variable lee SU propio select.
        // Antes estaban cruzadas: Lote leía #fecha_fin (que no existe en esta
        // pantalla) y Proyecto leía #loteMar. El select #ProyecMar nunca se
        // leía, así que el marbete salía con el proyecto equivocado.
        var proyecto = $('#ProyecMar').val();
        var clave    = $('#ClaveMar').val();
        var lote     = $('#loteMar').val();
        if (!tieneValor(proyecto) || !tieneValor(clave) || !tieneValor(lote)) {
            swal('Selecciona proyecto, producto y lote antes de generar el marbete.');
            return;
        }

        // encodeURIComponent es obligatorio: hay claves de lote con espacios y
        // dos puntos ("REF: 867036", "Sin Serie"). Sin codificar, la URL se
        // rompe y el JSP recibe el lote truncado.
        var url = '../Marbetes/GeneraMarbeteManual.jsp'
                + '?clave='    + encodeURIComponent(clave)
                + '&Proyecto=' + encodeURIComponent(proyecto)
                + '&lote='     + encodeURIComponent(lote);
        

        window.open(url);
    });
});

/**
 * Un <select> cuya opción seleccionada está disabled y sin atributo value
 * devuelve null en .val(), y un id inexistente devuelve undefined.
 * Comparar con !== "" deja pasar los dos casos, que es justo lo que hacía
 * que la validación nunca detuviera nada.
 */
function tieneValor(v) {
    return v !== null && v !== undefined && String(v).trim() !== '';
}

/** Deja el select vacío y deshabilitado con un solo texto de ayuda. */
function reiniciar(selector, texto) {
    $(selector).empty()
            .append($('<option>', { value: '', text: texto, selected: true }))
            .prop('disabled', true)
            .trigger('change.select2');
}

/**
 * Llena un select con [{valor, texto}] de MarbeteManualDatos.jsp.
 * Se construye con $('<option>') para que claves o lotes con comillas o "<" no rompan el HTML.
 */
function cargarOpciones(selector, datos, encabezado, sinDatos) {
    var $sel = $(selector);
    $sel.empty().append($('<option>', { value: '', text: 'Cargando…', selected: true }))
            .prop('disabled', true).trigger('change.select2');
    $.ajax({
        url: 'MarbeteManualDatos.jsp',
        data: datos,
        type: 'GET',
        dataType: 'json',
        cache: false
    }).done(function (lista) {
        if (!lista || lista.length === 0) {
            reiniciar(selector, sinDatos);
            return;
        }
        $sel.empty().append($('<option>', { value: '', text: encabezado, selected: true }));
        $.each(lista, function (i, o) {
            $sel.append($('<option>', { value: o.valor, text: o.texto }));
        });
        $sel.prop('disabled', false).trigger('change.select2');
        // Si solo hay una opción, se elige sola y se encadena el siguiente select.
        if (lista.length === 1) {
            $sel.val(lista[0].valor).trigger('change');
        }
    }).fail(function (xhr) {
        reiniciar(selector, 'No se pudo cargar');
        var msg = (xhr.responseJSON && xhr.responseJSON.error) || 'No se pudo cargar el catálogo. Intente de nuevo.';
        swal(msg);
    });
}

/*
 * ObtenerProyecto() quedó fuera a propósito.
 *
 * El JSP ya llena #ProyecMar con un scriptlet contra tb_proyectos. Llamar
 * además a ../Catalogos?accion=1 agregaba las mismas opciones una segunda vez,
 * así que el desplegable salía duplicado.
 *
 * Si prefieren que el llenado sea por AJAX, quiten el scriptlet del JSP y
 * descomenten esto. Ojo con tres cosas que la versión anterior tenía mal:
 *   - "<option select disabled>" era "selected", no "select";
 *   - .select2() se llamaba dentro del $.each, reinicializándolo en cada
 *     vuelta; va una sola vez al terminar;
 *   - los valores se concatenaban sin comillas ni escape, así que un nombre
 *     con espacios o con "<" rompía el HTML.
 *
 * function ObtenerProyecto() {
 *     var $sel = $('#ProyecMar');
 *     $sel.empty().append($('<option>', { value: '', text: '--Proyecto--', disabled: true, selected: true }));
 *     $.ajax({
 *         url: '../Catalogos',
 *         data: { accion: 1 },
 *         type: 'GET',
 *         dataType: 'json'
 *     }).done(function (data) {
 *         $.each(data, function (i, proyecto) {
 *             $sel.append($('<option>', { value: proyecto.Id, text: proyecto.Nombre }));
 *         });
 *         $sel.select2({ width: '100%' });
 *     }).fail(function () {
 *         swal('No se pudo cargar el catálogo de proyectos.');
 *     });
 * }
 */
