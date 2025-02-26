$(document).ready(function () {
    ObtenerProyecto();

    $('#Descargar').click(function () {
        var ConsultaT = $('#consultaAuditoria').val();
        var Proyecto = $("#Nombre option:selected").val();

        window.open("../ExcelExistenciaProyectoAuditoria?Tipo=" + ConsultaT + "");
    });

        
    //Mostrar auditoria
    $('#Mostrar').click(function () {
        var Consulta = $('#consultaAuditoria').val();
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarAuditoria", Tipo: Consulta} ,
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
        MostrarRegistros();
    });

    $("#consultaAuditoria").change(function () {
        limpiarTabla();
        MostrarRegistros();
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
//Cuando seleccionas prpoyecto y tipo 
function MostrarRegistros() {
    var Consulta = $('#consultaAuditoria').val();
    //var Proyecto = $("#Nombre").val();
    if (Consulta) {
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarAuditoria", Tipo: Consulta},
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

function limpiarTabla() {
    $("#ExistenciasAuditoria").remove();
}

function MostrarTabla(data) {
    var json = JSON.parse(data);
    var aDataSet = [];
    var Contar, CantidadT, ContarClave;
    for (var i = 0; i < json.length; i++) {
        var Proyecto = json[i].Proyecto;
        var ClaPro = json[i].ClaPro;
        var ClaProSS = json[i].ClaProSS;
        var Descripcion = json[i].Descripcion;
        var Lote = json[i].Lote;
        var Caducidad = json[i].Caducidad;
        var Cantidad = json[i].Cantidad;
        var Origen = json[i].Origen;
        var Estatus = json[i].Estatus;
        var OrdenCompra = json[i].OrdenCompra;
        var Tipo = json[i].Tipo;
        Contar = json[i].Contar;
        CantidadT = json[i].CantidadT;
        ContarClave = json[i].ContarClave;


        switch (Tipo) {
            case "1":
                aDataSet.push([Proyecto, ClaPro, ClaProSS, Descripcion, Lote, Caducidad, Cantidad, Origen, OrdenCompra]);
                break;
            case "2":
                aDataSet.push([Proyecto, ClaPro, ClaProSS, Descripcion, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenCompra]);
                break;
            default:
                aDataSet.push([Proyecto, ClaPro, ClaProSS, Descripcion, Lote, Caducidad, Cantidad, Origen, OrdenCompra]);
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
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasAuditoria"></table>');
                $('#ExistenciasAuditoria').dataTable({
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
                        {"sTitle": "Proyecto", "sClass": "center"},
                        {"sTitle": "Clave", "sClass": "center"},          
                        {"sTitle": "ClaveSS", "sClass": "center"},
                        {"sTitle": "Descripción", "sClass": "center"},
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de compra", "sClass": "center"}
                    ]
                });
                break;
            case "2":
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasAuditoria"></table>');
                $('#ExistenciasAuditoria').dataTable({
                    "aaData": aDataSet, "button": 'aceptar',
                  
                    "ScrollCollapse": true,
                     "scrollX": true,
                     "scrollY": true,
                   
                    "bFooter": true,
                    "bProcessing": true,
                    "sPaginationType": "full_numbers",
                    "bAutoWidth": false,
                    "order": [[0, "desc"]],
                    "aoColumns": [
                        {"sTitle": "Proyecto", "sClass": "center"},
                        {"sTitle": "Clave", "sClass": "center"},          
                        {"sTitle": "ClaveSS", "sClass": "center"},
                        {"sTitle": "Descripción", "sClass": "center"},
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Estatus", "sClass": "center"},
                        {"sTitle": "Orden de Compra", "sClass": "center"}

                    ]
                });
                
                break;
            default:
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasAuditoria"></table>');
                $('#ExistenciasAuditoria').dataTable({
                    "aaData": aDataSet, "button": 'aceptar',
//                    "bScrollInfinite": true,
                    "bScrollCollapse": true,
                     "scrollX": true,
                     "scrollY": true,
                   
                    "bFooter": true,
                    "bProcessing": true,
                    "sPaginationType": "full_numbers",
                    "bAutoWidth": false,
                    "order": [[0, "desc"]],
                    "aoColumns": [
                        {"sTitle": "Proyecto", "sClass": "center"},
                        {"sTitle": "Clave", "sClass": "center"},          
                        {"sTitle": "ClaveSS", "sClass": "center"},
                        {"sTitle": "Descripción", "sClass": "center"},
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Orden de Compra", "sClass": "center"}
                    ]
                });
                break;
        }
    });
}