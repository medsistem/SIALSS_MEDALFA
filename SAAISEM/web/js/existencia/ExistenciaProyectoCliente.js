$(document).ready(function () {
    ObtenerProyecto();

    $('#Descargar').click(function () {
        var Proyecto = $("#Nombre option:selected").val();
        window.open("../ExcelExistenciaProyectoCliente?Proyecto=" + Proyecto + "&Tipo=");
    });

    $('#Mostrar').click(function () {
        var ProyectoCL = $('#ProyectoCL').val();
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarTodosClientes", ProyectoCL: ProyectoCL},
            type: 'GET',
            async: false,
            success: function (data)
            {
                limpiarTabla();
                MostrarTabla(data);
            }, error: function (jqXHR, textStatus, errorThrown) {
                alert("Error grave contactar con el departamento de sistemas");
            }
        });
        ObtenerProyecto();
    });

    $("#Nombre").change(function () {
        //var Nombre = $("#Nombre option:selected").val();
        MostrarRegistros();
    });

});

function ObtenerProyecto() {
    $('#Nombre option:gt(0)').remove();
    $("#Nombre").append("<option value=0>Seleccione</option>").select2();
    $('#Nombre option:gt(0)').remove();
    var ProyectoCL = $('#ProyectoCL').val();
    $.ajax({
        url: "../ExistenciaProyecto",
        data: {accion: "obtenerProyectosCliente", ProyectoCL: ProyectoCL},
        type: 'GET',
        dataType: 'JSON',
        async: true,
        success: function (data) {
            $.each(data, function (i, valueProyecto) {
                $("#Nombre").append("<option value=" + valueProyecto.Id + ">" + valueProyecto.Nombre + "</option>").select2();
            });
        }
    });
}

function MostrarRegistros() {
    var Proyecto = $("#Nombre").val();
    $.ajax({
        url: "../ExistenciaProyecto",
        data: {accion: "MostrarRegistros", Proyecto: Proyecto},
        type: 'GET',
        async: false,
        success: function (data)
        {
            limpiarTabla();
            MostrarTabla(data);
        }, error: function (jqXHR, textStatus, errorThrown) {
            alert("Error grave contactar con el departamento de sistemas");
        }
    });
}

function limpiarTabla() {
    $("#example").remove();
}

function MostrarTabla(data) {
    var json = JSON.parse(data);
    var aDataSet = [];
    var Contar, CantidadT, ContarClave;
    for (var i = 0; i < json.length; i++) {
        var Proyecto = json[i].Proyecto;
        var ClaPro = json[i].ClaPro;
        var Descripcion = json[i].Descripcion;
        var Lote = json[i].Lote;
        var Caducidad = json[i].Caducidad;
        var Cantidad = json[i].Cantidad;
        var Origen = json[i].Origen;
        Contar = json[i].Contar;
        CantidadT = json[i].CantidadT;
        ContarClave = json[i].ContarClave;


        aDataSet.push([ClaPro, Descripcion, Lote, Caducidad, Cantidad, Origen]);
    }

    if (Contar > 0) {
        $("#Registro").css("display", "block");
        $('#total').html(CantidadT);
        $('#totalClave').html(ContarClave);
    } else {
        $("#Registro").css("display", "none");
    }

    $(document).ready(function () {
        $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="example"></table>');
        $('#example').dataTable({
            "aaData": aDataSet, "button": 'aceptar',
            "bScrollInfinite": true,
            "bScrollCollapse": true,
            //"sScrollY": "600px",
            "bFooter": true,
            "bProcessing": true,
            "sPaginationType": "full_numbers",
            "bAutoWidth": false,
            "order": [[0, "desc"]],
            "aoColumns": [
                 /**{"sTitle": "Proyecto", "sClass": "center"},**/
                {"sTitle": "Clave", "sClass": "center"},
                {"sTitle": "Descripción", "sClass": "center"},
                {"sTitle": "Lote", "sClass": "center"},
                {"sTitle": "Caducidad", "sClass": "center"},
                {"sTitle": "Cantidad", "sClass": "center"},
                {"sTitle": "Origen", "sClass": "center"}
            ]
        });
    });
}