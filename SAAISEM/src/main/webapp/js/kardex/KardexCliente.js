function dataSelectedAutocomplete(accion, cricterio, item, fechaInicial, fechaFinal)
{
    $.ajax({
        url: context + "/CatalogoController",
        data: {accion: accion, cricterio: cricterio, item: item, fechaFinal:fechaFinal},
        type: 'POST',
        async: true,
        dataType: 'json',
        success: function (data)
        {
            loadDataToDoom(data);

            showKardex(data.header.clave, porClave, '', '', '', fechaInicial, fechaFinal);


        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Clave fuera de catálogo favor de verificar");

        }
    });
}


function loadDataToDoom(data)
{
    var $selectLote = $("#loteKardex");
    var $selectCaducidad = $("#caducidadKardex");

    $selectLote.find('option').remove();
    $('<option>').val("-").text("-Seleccione-").appendTo($selectLote);
    $.each(data.body, function (key, value) {
        $('<option>').val(value.idLote).text(value.lote).appendTo($selectLote);

    });

    $selectCaducidad.find('option').remove();
    $('<option>').val("-").text("-Seleccione-").appendTo($selectCaducidad);
    $.each(data.body, function (key, value) {
        $('<option>').val(value.idLote).text(value.caducidad).appendTo($selectCaducidad);

    });

    $("#claveResult").text(data.header.clave);
    $("#descripcionResult").text(data.header.descripcion);
    $("#existenciaResult").text(formatoNumero(data.header.existencia, 0, ','));
    $("#loteResult").text("");
    $("#caducidad").text("");


}

function showKardex(clave, accion, lote, caducidad, origen, fechaInicial, fechaFinal)
{

    $.ajax({
        url: context + "/KardexController",
        data: {accion: accion, clave: clave, lote: lote, caducidad: caducidad, origen: origen, fechaInicial:fechaInicial, fechaFinal:fechaFinal},
        type: 'POST',
        async: true,
        dataType: 'json',
        beforeSend: function ()
        {
            $("#myModal").modal({show: true});
        },
        success: function (data)
        {

            MostrarKardexIngresos(data.movimeintos);
            $("#myModal").modal('hide');
        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Error Contactar al departamento de sistemas");

        }
    });
}

function showInformationByLote(clave, lote, caducidad, origen, fechaInicial, fechaFinal)
{

    $.ajax({
        url: context + "/CatalogoController",
        data: {accion: infoPorLote, clave: clave, lote: lote, caducidad: caducidad, origen: origen, fechaInicial:fechaInicial, fechaFinal:fechaFinal},
        type: 'POST',
        async: true,
        dataType: 'json',
        success: function (data)
        {
           
            $("#claveResult").text(data.clave);
            $("#descripcionResult").text(data.descripcion);
            $("#existenciaResult").text(formatoNumero(data.existencia, 0, ','));
            $("#loteResult").text(lote);
            $("#caducidadResult").text(caducidad);
            showKardex(clave, porLote, lote, caducidad, origen, fechaInicial, fechaFinal);
        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Error Contactar al departamento de sistemas");

        }
    });

}
function MostrarKardexIngresos(data) {
     $("#example").remove();

    var json = data;
    var aDataSet = [];
    for (var i = 0; i < json.length; i++)
    {
        var noMov = json[i].noMov;
        var usuario = json[i].usuario;
//        var documentoCompra = json[i].documentoCompra;
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
//        var folioReferencia = json[i].folioReferencia;
//        var fechaEnt = json[i].fechaEnt;
        
        aDataSet.push([noMov, usuario, ori, remision, proveedor, folioSalida, puntoEntrega, concepto, clave,
            lote, caducidad, cantidadMovimiento, ubicacion, origen, proyecto, fechaMovimiento,  hora, comentarios]);
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
                {"sTitle": "Hora", "sClass": "center"},
                {"sTitle": "Observ", "sClass": "center"}

            ]
        });


    });


}
