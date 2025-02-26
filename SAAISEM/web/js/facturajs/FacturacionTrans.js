
$(document).ready(function () {
    $('#datosfolios').dataTable();

    obtenerIdreg();

    $('#BtnConfirmar').click(function () {
        var Obs = $("#obs").val();
        var Tipo = $("#Tipo").val();
        if (Obs != "") {
            swal({
                title: "¿Seguro de confirmar la Factura?",
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
                            data: {accion: "ConformarFactTemp", Obs: Obs, Tipo: Tipo},
                            type: 'POST',
                            dataType: 'JSON',
                            async: true,
                            success: function (data) {
                                if (data.msj) {
                                    swal({
                                        title: "Redistribución Realizado correctamente!",
                                        text: "",
                                        type: "success"
                                    }, function () {
                                        //location.reload();
                                        window.location = "FacturacionTran?accion=TerminoCaptura";
                                    });

                                } else {
                                    swal("Datos no Registrados", "", "error");
                                    //location.reload();
                                }

                            }
                        });

                    });
        } else {
            swal("Atención", "Favor de llenar campo de observaciones", "warning");
        }
    });

    $('#BtngenerarRemision').click(function () {
        var check = document.getElementsByTagName("chkUniFact");
        alert( "aa"+check.length);
        /*
        var x = document.getElementById("frm1");
        var j = x.length;
        

        var jhon = new Array();
        var jhon1 = new Array();
        alert( check.length);

        for (var i = 0; i < j; i++) {
            if (x.elements[i].id == "check") {
                jhon[i] = x.elements[i].name;
            } else if (x.elements[i].id == "text") {
                jhon1[i] = x.elements[i].name;
            }
        }
        for (var r = 0; r < jhon.length; r++) {
            document.write(jhon[r] + "  " + jhon1[r]);
            document.write("<br />");
        }
        alert(jhon.length);
        alert(jhon1.length);
*/
    });

});

function validaCantidad(e) {
    var cantidadSol = document.getElementById('Cantidad').value;
    document.getElementById('Cant' + e).value = cantidadSol;
    var cantidadAlm = document.getElementById('CantAlm_' + e).value;
    if (parseInt(cantidadSol) > parseInt(cantidadAlm)) {
        //alert('La cantidad a facturar no puede ser mayor a la cantidad de esa ubicación');
        swal("Verificar", "La cantidad a facturar no puede ser mayor a la cantidad seleccionada", "error");
        return false;
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
                                    window.location = "FacturacionTran?accion=RegresarCaptura";
                                } else {
                                    swal("Atención", "Datos no aplicados", "error");
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