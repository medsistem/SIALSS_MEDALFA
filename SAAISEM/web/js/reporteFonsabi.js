 /* global $row */

function validarForma(forma) {
    var unidadF = forma.unidadMod;
    console.log(unidadF + "unidad");
    if (unidadF.value === "") {
        swal("Seleccionar unidad destino");
        return false;
    }}

    $(document).ready(function(){
        $('#tablaFonsabi tbody tr').each(function(){            
            var unidad = $(this).find("td.unidadN").text();            
            if(unidad !== ""){
                $(this).find(".rowButton").hide();
            }
        });     
    });
     
    $(".rowButton").click(function () {
                var $row = $(this).closest("tr");
                var clave = $row.find("td.clave").text();
                var lote = $row.find("td.lote").text();
                var caducidad = $row.find("td.caducidad").text();
                var existencia = $row.find("td.existencia").text();
                var ubicacion = $row.find("td.ubicacion").text();
                var remision = $row.find("td.remision").text();
                var ordCom = $row.find("td.ordCom").text();
                var ordSum = $row.find("td.ordSum").text();
                var folLot = $row.find("td.folLot").text();
                var unidadN = $row.find("td.unidadN").text();
                 

                $("#claveMod").val(clave);
                $("#loteMod").val(lote);
                $("#caducidadMod").val(caducidad);
                $("#existenciaMod").val(existencia);
                $("#ubicacionMod").val(ubicacion);
                $("#remisionMod").val(remision);
                $("#ordComMod").val(ordCom);
                $("#ordSumMod").val(ordSum);
                $("#folLotMod").val(folLot);
                $("#unidadNMod").val(unidadN);
                console.log(unidadN);
               
                
            });
            

$(document).ready(function () {
    $('#tablaFonsabi').dataTable();
});
       
  

