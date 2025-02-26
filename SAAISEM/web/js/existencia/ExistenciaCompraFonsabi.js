$(document).ready(function () {
    ObtenerProyecto();

    $('#Descargar').click(function () {
        var ConsultaT = $('#Consulta').val();
        var Proyecto = $("#Nombre option:selected").val();

        window.open("../ExcelExistenciaCompraFonsabi?Proyecto=" + Proyecto + "&Tipo=Compra&Consulta=" + ConsultaT + "");
    });

    $('#Mostrar').click(function () {
        var Consulta = $('#Consulta').val();
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarTodosCompraFonsabi", Tipo: Consulta},
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
    console.log(Consulta);
    var Proyecto = $("#Nombre").val();
    console.log(Proyecto);
    if (Consulta && Proyecto !== "0") {
        $.ajax({
            url: "../ExistenciaProyecto",
            data: {accion: "MostrarRegistrosCompraFonsabi", Proyecto: Proyecto, Tipo: Consulta},
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
        var Presentacion = json[i].Presentacion;
        var Lote = json[i].Lote;
        var Caducidad = json[i].Caducidad;
        var Cantidad = json[i].Cantidad;
        var Origen = json[i].Origen;
        var OrdenSuministro = json[i].OrdenSuministro;
        var UnidadEntrega = json[i].UnidadEntrega;
        var NomGenrico = json[i].NomGenerico;
        var Tipo = json[i].Tipo;       
        Contar = json[i].Contar;
        CantidadT = json[i].CantidadT;
        ContarClave = json[i].ContarClave;


        switch (Tipo) {               
            case "2":
                aDataSet.push([ClaProSS, NomGenrico, Descripcion, Presentacion, Lote, Caducidad, Cantidad, Origen, OrdenSuministro, UnidadEntrega]);
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
                        {"sTitle": "Clave", "sClass": "center"},     
                        {"sTitle": "Nombre Genérico", "sClass": "center"},     
                        {"sTitle": "Descripción", "sClass": "center"},                       
                        {"sTitle": "Presentación", "sClass": "center"},              
                        {"sTitle": "Lote", "sClass": "center"},
                        {"sTitle": "Caducidad", "sClass": "center"},
                        {"sTitle": "Cantidad", "sClass": "center"},
                        {"sTitle": "Origen", "sClass": "center"},                        
                        {"sTitle": "Orden de Suministro", "sClass": "center"},
                        {"sTitle": "Unidad de entrega", "sClass": "center"}

                    ]
                });
                break;
            
        }
    });
}