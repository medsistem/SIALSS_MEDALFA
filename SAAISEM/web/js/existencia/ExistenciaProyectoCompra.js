$(document).ready(function () {
    ObtenerProyecto();

    $('#Descargar').click(function () {
        var ConsultaT = $('#Consulta').val();
        var Proyecto = $("#Nombre option:selected").val();

        window.open("../ExcelExistenciaProyectoCompra?Proyecto=" + Proyecto + "&Tipo=Compra&Consulta=" + ConsultaT + "");
    });

    $('#Mostrar').click(function () {
        var Consulta = $('#Consulta').val();
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarTodosCompra", Tipo: Consulta},
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

    $("#Consulta").change(function () {
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

function MostrarRegistros() {
    var Consulta = $('#Consulta').val();
    var Proyecto = $("#Nombre").val();
    if (Consulta && Proyecto !== "0") {
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarRegistrosCompra", Proyecto: Proyecto, Tipo: Consulta},
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
        var NomGen = json[i].NomGen;
        var ForFar = json[i].ForFar;
        var concentracion = json[i].concentracion;
        var fuentefinanciamento = json[i].fuentefinanciamento;
        var Presentacion = json[i].Presentacion;
        Contar = json[i].Contar;
        CantidadT = json[i].CantidadT;
        ContarClave = json[i].ContarClave;


        switch (Tipo) {
            /*case "1":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Cantidad, OrdenSuministro]);
                break;
            case "2":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, OrdenSuministro]);
                break;
            case "3":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "4":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "5":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "6":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "7":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "8":
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenSuministro]);
                break;
            default:
                aDataSet.push([Proyecto, ClaProSS, NomGen, Descripcion, ForFar, concentracion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
                */
              case "1":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Cantidad, OrdenSuministro]);
                break;
            case "2":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, OrdenSuministro]);
                break;
            case "3":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "4":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "5":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "6":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "7":
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
                break;
            case "8":
                aDataSet.push([ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenSuministro]);
                break;
            case "15":
                aDataSet.push([ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, Estatus, OrdenSuministro,fuentefinanciamento]);
                break;    
            default:
                aDataSet.push([ ClaProSS, NomGen, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro]);
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
                        {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            case "2":
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasCompras"></table>');
                $('#ExistenciasCompras').dataTable({
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
                         /**{"sTitle": "Proyecto", "sClass": "center"},**/
                        {"sTitle": "Clave", "sClass": "center"},          
                        {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                      /*  {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}

                    ]
                });
                break;
            case "3":
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
                       {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            case "4":
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
                       {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            case "5":
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
                        {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                        /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            case "6":
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
                       {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                         {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
            case "7":
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
                       {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}

                    ]
                });
                break;
            case "8":
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
                        {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Estatus", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}

                    ]
                });
                break;
                case "15":
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
                         {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                        
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Estatus", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"},
                        {"sTitle": "Fuente de Financiamiento", "sClass": "center"}
                    ]
                });
                break;
            default:
                $('#dynamic').html('<table class="table table-striped table-bordered table-condensed"  id="ExistenciasCompras"></table>');
                $('#ExistenciasCompras').dataTable({
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
                        /**{"sTitle": "Proyecto", "sClass": "center"},**/
                        {"sTitle": "Clave", "sClass": "center"},          
                        {"sTitle": "Nombre Generico", "sClass": "center"},
                        {"sTitle": "Descripción Especifica", "sClass": "center"},
                        {"sTitle": "Presentación", "sClass": "center"},
                       /* {"sTitle": "Forma Farmaceutica", "sClass": "center"},
                        {"sTitle": "Concentracion", "sClass": "center"},*/
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},
                        {"sTitle": "Orden de Suministro", "sClass": "center"}
                    ]
                });
                break;
        }
    });
}