$(document).ready(function () {
    ObtenerTipoUnidad();

    $('#Descargar').click(function () {
        var fechafactura = $("#fechafactura").val();
        var Proyecto = $("#TipoUnidad option:selected").val();
        window.open("../ExcelFacturado?Proyecto=" + Proyecto + "&Fechafactura=" + fechafactura + "");
    });

    $('#Mostrar').click(function () {
        var fechafactura = $("#fechafactura").val();
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarTodosFact", Fechafactura: fechafactura},
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
        ObtenerTipoUnidad();
    });

    $("#TipoUnidad").change(function () {
        MostrarRegistros();
    });



});

function ObtenerTipoUnidad() {
    $('#TipoUnidad option:gt(0)').remove();
    $("#TipoUnidad").append("<option value=0>Seleccione</option>").select2();
    $('#TipoUnidad option:gt(0)').remove();
    $.ajax({
        url: "../ExistenciaProyecto",
        data: {accion: "obtenerTipoUnidad"},
        type: 'GET',
        dataType: 'JSON',
        async: true,
        success: function (data) {
            $.each(data, function (i, valueTipo) {
                $("#TipoUnidad").append("<option value=" + valueTipo.Id + ">" + valueTipo.Nombre + "</option>").select2();
            });
        }
    });
}

function MostrarRegistros() {
    var Proyecto = $("#TipoUnidad").val();
    var fechafactura = $("#fechafactura").val();
    $.ajax({
        url: "../ExistenciaProyecto",
        data: {accion: "MostrarRegistrosFact", Proyecto: Proyecto, Fechafactura: fechafactura},
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
    for (var i = 0; i < json.length; i++) {
        var Clave = json[i].Clave;
        //var Ubicacion = json[i].Ubicacion;
        var Cantidad = json[i].Cantidad;
        var Tipo = json[i].Tipo;
        var CantidadT = json[i].CantidadT;
        var TipoConsulta = json[i].TipoConsulta;
        if (parseInt(TipoConsulta) == 1) {
            aDataSet.push([Clave, Cantidad, Tipo]);
        } else {
            aDataSet.push([Clave, Cantidad]);
        }
    }
    $("#Registro").css("display", "block");
    $('#total').html(CantidadT);


    $(document).ready(function () {
        if (parseInt(TipoConsulta) == 1) {
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
                    {"sTitle": "Clave", "sClass": "center"},
                    {"sTitle": "Cantidad", "sClass": "center"},
                    {"sTitle": "Tipo Unidad", "sClass": "center"}
                ]
            });
        } else {
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
                    {"sTitle": "Clave", "sClass": "center"},
                    {"sTitle": "Cantidad", "sClass": "center"}
                ]
            });
        }
    });
}