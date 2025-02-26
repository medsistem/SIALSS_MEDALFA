
$(document).ready(function () {
    $('#datosfolios').dataTable();

    obtenerIdreg();

    $('#BtnConfirmar').click(function () {
        var Proyecto = $("#Proyecto").val();
        var FolioS = $("#FolioS").val();
        swal({
            title: "¿Seguro de confirmar la Modificación?",
            text: "No podrás deshacer este paso...",
            type: "warning",
            showCancelButton: true,
            //cancelButtonText: "Mejor no",
            closeOnConfirm: false,
            confirmButtonColor: "#DD6B55",
            showLoaderOnConfirm: true,
            confirmButtonText: "Continuar!"
        },
                function () {
                    //setTimeout(function () {
                    $.ajax({
                        url: "FacturacionTran",
                        data: {accion: "ConformarFactTempFOLIO", Proyecto: Proyecto},
                        type: 'POST',
                        dataType: 'JSON',
                        async: true,
                        success: function (data) {
                            if (data.msj) {
                                swal({
                                    title: "Modificación Realizado correctamente!",
                                    text: "",
                                    type: "success"
                                }, function () {
                                    //location.reload();
                                    window.location = "ModificarFolio?Accion=btnMostrar&Folio=" + FolioS + "";
                                });

                            } else {
                                swal("Modificación no Registrados", "", "error");
                                //location.reload();
                            }

                        }
                    });

                });

    });

    $('#BtngenerarRemision').click(function () {
        //alert("ffffaa");
    });

});

function validaCantidad(e) {
    e = e.split("_")[1];
    var cantidadSol = document.getElementById('Cantidad').value;
    document.getElementById('Cant' + e).value = cantidadSol;
    var cantidadAlm = document.getElementById('CantAlm_' + e).value;
    if (parseInt(cantidadSol) > parseInt(cantidadAlm)) {
        //alert('La cantidad a facturar no puede ser mayor a la cantidad de esa ubicación');
        swal("Verificar", "La cantidad a facturar no puede ser mayor a la cantidad seleccionada", "error");
        return false;
    }
    var isControlado = document.getElementById('controlado' +e).value;
    if (isControlado === 'true') {
        var cadControlado = document.getElementById('cadControlado' + e).value;
        if (cadControlado > 0) {
            alert("El lote que está remisionando es CONTROLADO y está próximo a caducar");
        }

    }
}

function obtenerIdreg() {
    var ClaPro = $('#ClaPro').val();
    var Cantidad = $('#Cantidad').val();

    $.ajax({
        type: "GET",
        url: "FacturacionTran?accion=obtenerIdReg&Clapro=" + ClaPro + "",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data, function (idx, elem) {
                var isValidado;
                isValidado = elem.validado;

                //alert(elem.IdReg);
                $('#BtnAgregar_' + elem.IdReg).click(function () {

                    if (parseInt(Cantidad) > parseInt(elem.Existencia)) {
                        swal("Verificar", "La cantidad a facturar no puede ser mayor a la cantidad seleccionada", "error");
                    } else {
                        $('#BtnAgregar_' + elem.IdReg).prop('disabled', true);
                        $.ajax({
                            url: "FacturacionTran",
                            data: {accion: "RegistrarDatos", IdLote: elem.IdReg, CantMov: Cantidad},
                            type: 'POST',
                            dataType: 'JSON',
                            async: true,
                            success: function (data) {
                                if (data.msj) {
                                    window.location = "FacturacionTran?accion=RegresarCapturaFOLIO";
                                } else {
                                    swal("Atención", "Modificación no aplicados", "error");
                                    location.reload();
                                }

                            }
                        });
                    }

                });
            });
        }
    });

}