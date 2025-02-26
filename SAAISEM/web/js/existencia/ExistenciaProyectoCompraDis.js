$(document).ready(function () {
    ObtenerProyecto();

/*Descargar Archivo*/
    $('#DescargarDis').click(function () {
        var ConsultaT = $('#ConsultaDis').val();
        var Proyecto = $("#Nombre option:selected").val();

        window.open("../ExcelExistenciaProyectoCompraDis?Proyecto=" + Proyecto + "&Tipo=Compra&Consulta=" + ConsultaT + "");
    });

/***************/


function MostrarRegistrosCompraDis() {
    var Consulta = $('#ConsultaDis').val();
    var Proyecto = $("#Nombre").val();
    if (Consulta && Proyecto !== "0") {
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarRegistrosCompraDis", Proyecto: Proyecto, Tipo: Consulta},
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
}

    $("#Nombre").change(function () {
       MostrarRegistrosCompraDis();
    });

    $("#ConsultaDis").change(function () {
       MostrarRegistrosCompraDis();
    });

});

function ObtenerProyecto() {
    $('#Nombre option:gt(0)').remove();
    $("#Nombre").append("<option value=0>Seleccione</option>").select2();
    $('#Nombre option:gt(0)').remove();
    $.ajax({
        url: "../ExistenciaProyecto",
        data: {accion: "obtenerProyectos"},
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


function limpiarTabla() {
    $("#ExistenciasCompras").remove();
}

function MostrarTabla(data) {
    var json = JSON.parse(data);
    var aDataSet = [];
    var Contar, CantidadT, ContarClave;
    for (var i = 0; i < json.length; i++) {
        var Proyecto = json[i].Proyecto;
        var ClaProSS = json[i].ClaPro;
        var Descripcion = json[i].Descripcion;
        var Lote = json[i].Lote;
        var Caducidad = json[i].Caducidad;
        var Cantidad = json[i].Cantidad;
        var Origen = json[i].Origen;
        var Tipo = json[i].Tipo;
        var Estatus = json[i].Estatus;
        var OrdenSuministro = json[i].OrdenSuministro;
       
        Contar = json[i].Contar;
        CantidadT = json[i].CantidadT;
        ContarClave = json[i].ContarClave;


        switch (Tipo) {
              case "1":
                aDataSet.push([ClaProSS, Descripcion, Tipo, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenSuministro]);
                break;
            default:
                aDataSet.push([ClaProSS, Descripcion, Tipo, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenSuministro]);
                break;    
        }
    }

    if (Contar > 0) {
        $("#Registro").css("display", "block");
        $('#total').html(CantidadT);
        $('#totalClave').html(ContarClave);
    } else {
        $("#Registro").css("display", "none");
    }

    $(document).ready(function () {
        switch (Tipo) {
            case "1":             
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasCompras"></table>');
                $('#ExistenciasCompras').dataTable({
                    "aaData": aDataSet, "button": 'aceptar',
                    "bScrollInfinite": true,
                    "bScrollCollapse": true,
                     "scrollX": true,
                     "scrollY": true,
                    "bFooter": true,
                    "bProcessing": true,
                    "sPaginationType": "full_numbers",
                    "bAutoWidth": false,
                    "order": [[0, "desc"]],
                    "aoColumns": [
                        /**{"sTitle": "Proyecto", "sClass": "center"},**/
                        {"sTitle": "Clave", "sClass": "center"},   
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Tipo Insumo", "sClass": "center"}, 
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Estatus", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            default:
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasCompras"></table>');
                $('#ExistenciasCompras').dataTable({
                    "aaData": aDataSet, "button": 'aceptar',
                    "bScrollCollapse": true,
                     "scrollX": true,
                     "scrollY": true,
                    "bFooter": true,
                    "bProcessing": true,
                    "sPaginationType": "full_numbers",
                    "bAutoWidth": false,
                    "order": [[0, "desc"]],
                    "aoColumns": [
                     /**{"sTitle": "Proyecto", "sClass": "center"},**/
                        {"sTitle": "Clave", "sClass": "center"},   
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Tipo Insumo", "sClass": "center"}, 
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Estatus", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
        }
    });
}