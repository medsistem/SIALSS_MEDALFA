
$(function () {

    $("#searchByDay").click(function ()
    {
        var fecha = $("#fechaBusquedaInput").val();

        if (fecha === "")
        {
            swal("Atención", "Favor de ingresar una fecha válida", "warning");

        } else
        {
            showKardex(fecha);
        }


    });



    $("#downloadKardexByFecha").click(function () {
        var fecha = $("#fechaBusquedaInput").val();

        if (fecha === "")
        {
            swal("Atención", "Favor de ingresar una fecha válida", "warning");

        } else
        {
            window.open(context + "/kardex/gnrKardexFecha.jsp?fecha=" + fecha + "");
        }
    });




});

function showKardex(fecha)
{

    $.ajax({
        url: context + "/KardexController",
        data: {accion: 11, fecha: fecha},
        type: 'POST',
        async: true,
        dataType: 'json',
        beforeSend: function ()
        {
            $("#myModal").modal({show: true});
        },
        success: function (data)
        {

            if (data.error) {
                alert("Error Contactar al departamento de sistemas");
                $("#myModal").modal('hide');
            } else {
                MostrarKardexIngresos(data.movimeintos);
                MostrarKardexRedistribucion(data.reabastecimiento);

                $("#myModal").modal('hide');
            }

        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Error Contactar al departamento de sistemas");

        }
    });
}

function MostrarKardexIngresos(data) {


    $("#example").remove();

    if (!data) {
        return;
    }
    var json = data;
    var aDataSet = [];
    for (var i = 0; i < json.length; i++)
    {
        var noMov = json[i].noMov;
        var usuario = json[i].usuario;
        var claveMovimiento = json[i].claveMovimiento;
        var descripcion = json[i].descripcion;
        var ori = json[i].ori;
        var remision = json[i].remision;
        var proveedor = json[i].proveedor;
        var folioSalida = json[i].folioSalida;
        var puntoEntrega = json[i].puntoEntrega;
        var concepto = json[i].concepto;
        var clave = json[i].clave;
        var lote = json[i].lote;
        var caducidad = json[i].caducidad;
        var cantidadMovimiento = json[i].cantidadMovimiento;
        var ubicacion = json[i].ubicacion;
        var origen = json[i].origen;
        var proyecto = json[i].proyecto;
        var fechaMovimiento = json[i].fechaMovimiento;
        var hora = json[i].hora;
        var comentarios = json[i].comentarios;
//        var comentarios= json[i].comentarios;
        var folioReferencia = json[i].folioReferencia;
        var fechaEnt = json[i].fechaEnt;

        aDataSet.push([noMov, usuario, ori, remision,
            proveedor, folioSalida, folioReferencia, puntoEntrega, concepto, clave,
            lote, caducidad, cantidadMovimiento, ubicacion, origen, proyecto, fechaMovimiento,fechaEnt, hora, comentarios]);
    }
    $(document).ready(function () {
        $('#dynamic').html('<table class="table table-borded table-condensed table-striped " width="100%" id="example"></table>');
        $('#example').dataTable({
            "aaData": aDataSet,
            "bAutoWidth": true,
            "aoColumns": [
                {"sTitle": "No. Mov", "sClass": "center"},
                {"sTitle": "Usuario", "sClass": "center"},
                {"sTitle": "Documento", "sClass": "center"},
                {"sTitle": "Remisión"},
                {"sTitle": "Proveedor"},
                {"sTitle": "Folio Salida", "sClass": "center"},
                {"sTitle": "Folio Referencia", "sClass": "center"},
                {"sTitle": "Entrega", "sClass": "center"},
                {"sTitle": "Concepto", "sClass": "center"},
                {"sTitle": "Clave", "sClass": "center"},
                {"sTitle": "Lote", "sClass": "center"},
                {"sTitle": "Caducidad", "sClass": "center"},
                {"sTitle": "Cantidad", "sClass": "center"},
                {"sTitle": "Ubicacion", "sClass": "center"},
                {"sTitle": "Origen", "sClass": "center"},
                {"sTitle": "Proyecto", "sClass": "center"},
                {"sTitle": "Fecha", "sClass": "center"},
                {"sTitle": "Fecha Entrega", "sClass": "center"},
                {"sTitle": "Hora", "sClass": "center"},
                {"sTitle": "Observ", "sClass": "center"}

            ]
        });


    });


}
function MostrarKardexRedistribucion(data) {

    $("#exampleRedistribucion").remove();

    if (!data) {
        return;
    }
    var json = data;
    var aDataSet = [];
    for (var i = 0; i < json.length; i++)
    {
        var noMov = json[i].noMov;
        var usuario = json[i].usuario;
        var concepto = json[i].concepto;
        var clave = json[i].clave;
        var lote = json[i].lote;
        var caducidad = json[i].caducidad;
        var cantidadMovimiento = json[i].cantidadMovimiento;
        var ubicacion = json[i].ubicacion;
        var origen = json[i].origen;
        var proyecto = json[i].proyecto;
        var fechaMovimiento = json[i].fechaMovimiento;
        var hora = json[i].hora;
        var comentarios = json[i].comentarios;

        aDataSet.push([noMov, usuario, concepto, clave,
            lote, caducidad, cantidadMovimiento, ubicacion, origen, proyecto, fechaMovimiento, hora, comentarios]);
    }
    $(document).ready(function () {
        $('#dynamicRedistribucion').html('<table class="table table-borded table-condensed table-striped " width="100%" id="exampleRedistribucion"></table>');
        $('#exampleRedistribucion').dataTable({
            "aaData": aDataSet,
            "bAutoWidth": true,
            "aoColumns": [
                {"sTitle": "No. Mov", "sClass": "center"},
                {"sTitle": "Usuario", "sClass": "center"},
                {"sTitle": "Concepto", "sClass": "center"},
                {"sTitle": "Clave", "sClass": "center"},
                {"sTitle": "Lote", "sClass": "center"},
                {"sTitle": "Caducidad", "sClass": "center"},
                {"sTitle": "Cantidad", "sClass": "center"},
                {"sTitle": "Ubicacion", "sClass": "center"},
                {"sTitle": "Origen", "sClass": "center"},
                {"sTitle": "Proyecto", "sClass": "center"},
                {"sTitle": "Fecha", "sClass": "center"},
                {"sTitle": "Hora", "sClass": "center"},
                {"sTitle": "Observ", "sClass": "center"}

            ]
        });


    });


}