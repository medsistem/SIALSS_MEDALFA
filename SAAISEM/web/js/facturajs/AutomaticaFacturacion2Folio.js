
$(document).ready(function () {
    $('#datosfolios').dataTable();
    //obtenerIdreg();
    $('#BtngenerarRemision').click(function () {

        var ban = 0;
        var CantidadAcomulada = 0;
        var Catalogo = $('#Cata').val();
        var Tipo = $('#F_Tipo').val();
        var FecEnt = $('#FecEnt').val();
        var Proyecto = $('#Proyecto').val();
        var agrupacion = $('#categoria').val();
        var OC = $("#OC").val();
        var checkboxValues = "";
        var Obs = "";

        if (FecEnt != "") {
            swal({
                title: "¿Seguro de generar las Remisiones?",
                text: "No podrás deshacer este paso.",
                type: "warning",
                showCancelButton: true,
                //cancelButtonText: "Mejor no",
                closeOnConfirm: false,
                confirmButtonColor: "#DD6B55",
                showLoaderOnConfirm: true,
                confirmButtonText: "Continuar!"
            },
                    function () {
                        $("input:checkbox:checked").each(function () {
                            checkboxValues += $(this).val() + "','";
                            var obs = $("#obs" + $(this).val()).val();
                            sessionStorage.setItem("obs", obs);
                        });
                        checkboxValues = checkboxValues.substring(0, checkboxValues.length - 2);
                        checkboxValues = "'" + checkboxValues;

                        $.ajax({
                            type: "GET",
                            url: "FacturacionTran?accion=obtenerIdRegFactAuto&ClaUni=" + checkboxValues + "&Catalogo=" + Catalogo + "",
                            dataType: "json",
                            async: false,
                            success: function (data) {
                                $.each(data, function (idx, elem) {
                                    var isValidado;
                                    isValidado = elem.validado;
                                    var Cantidad = $("#Cantidad_" + elem.Datos).val();
                                    var Obs = $("#obs" + elem.ClaUni).val();
                                    CantidadAcomulada = parseInt(CantidadAcomulada) + parseInt(Cantidad);

                                    $.ajax({
                                        url: "FacturacionTran",
                                        data: {accion: "ActualizaReqId", ClaUni: elem.ClaUni, ClaPro: elem.ClaPro, IdRegistro: elem.IdReg, Catalogo: Catalogo, Cantidad: Cantidad, Obs: Obs},
                                        type: 'GET',
                                        dataType: 'JSON',
                                        async: true,
                                        success: function (data) {
                                            if (data.msj) {

                                            } else {
                                                swal("Datos no Registrados en Requerimiento", "", "error");
                                                //location.reload();
                                            }

                                        }
                                    });


                                });
                            }
                        });

                       

                        $.ajax({
                            url: "FacturacionTran",
                            data: {accion: "GeneraFolioApart2Folio", ClaUni: checkboxValues, Catalogo: Catalogo, Tipo: Tipo, FecEnt: FecEnt, Proyecto: Proyecto, OC: OC, Obs:sessionStorage.getItem("obs"), agrupacion:agrupacion},
                            type: 'GET',
                            dataType: 'JSON',
                            async: true,
                            success: function (data) {
                                if (data.msj) {
                                    $.ajax({
                                        url: "FacturacionTran",
                                        data: {accion: "GeneraFolio2Folio", ClaUni: checkboxValues, Catalogo: Catalogo, Tipo: Tipo, FecEnt: FecEnt, Proyecto: Proyecto, OC: OC, Obs:sessionStorage.getItem("obs")},
                                        type: 'GET',
                                        dataType: 'JSON',
                                        async: true,
                                        success: function (data) {
                                            if (data.msj) {
                                                swal({
                                                    title: "Remisiones generado correctamente!",
                                                    text: "",
                                                    type: "success"
                                                }, function () {
                                                    window.location = "facturaAtomatica2Folio.jsp";
                                                });
                                            } else {
                                                swal("Datos no Registrados en Remision", "", "error");
                                            }
                                        }
                                    });
                                } else {
                                    swal("Datos no Registrados por Apartado", "", "error");
                                }

                            }
                        });

                    });
        } else {
            swal("Atención", "Favor de llenar campo de Fecha Entrega", "warning");
        }
    });

});