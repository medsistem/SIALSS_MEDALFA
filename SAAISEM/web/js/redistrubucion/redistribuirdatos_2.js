$(function ()
{
    $("#btnSave1").click(function ()
    {
        var lote = $("#loteNuevo").val();
        var caducidad = $("#CaducidadNuevo").val();
        var cantidad = $("#CantidadNuevo").val();
        var cb = $("#CbNuevo").val();
        var marca = $("#marcaNuevo").val();
        var id = $("#idCompraTemporal").val();
        var usuario = $("#UserActual").val();
        if (lote === "")
        {
            alert("Ingresar un lote válido por favor.");
            return false;
        } else if (caducidad === "")
        {
            alert("ingresar una caducidad válida por favor.");
            return false;
        } else if (cantidad <= 0)
        {
            alert("ingresar una cantidad válida por favor.");
            return false;
        } else if (cb === "")
        {
            alert("ingresar una cb válido por favor.");
            return false;
        } else if (marca === "")
        {
            alert("ingresar una marca válida por favor.");
            return false;
        } else
        {
            $.ajax({
                url: "recepcionTransaccional",
                data: {accion: "EditarLotes", id: id, lote: lote, caducidad: caducidad, cantidad: cantidad, cb: cb, marca: marca, usuario: usuario},
                type: 'POST',
                dataType: 'JSON',
                async: true,
                success: function (data)
                {
                    if (data.msj)
                    {
                        alert("Modificación realizada con éxito");
                    } else
                    {
                        alert("Error en la modificación contactar al departamento de sistemas.");
                    }

                    $("#btnCancel").click();
                    location.reload();


                }, error: function (jqXHR, textStatus, errorThrown) {
                    alert("Error en sistema");
                }
            });
        }


    });


    /*REDISTRIBUCION*/

    $("#Redistribucion").click(function ()
    {
        /*
         var CantAnt = document.getElementById('CantAnt').value;
         var CantMov = document.getElementById('CantMov').value;
         var Ubicacion = document.getElementById('F_ClaUbi').value;
         var aClaUbi = document.getElementById('aClaUbi').value;
         var aCbUbica = document.getElementById('aCbUbica').value;
         */
        var CantAnt = $("#CantAnt").val();
        var CantMov = $("#CantMov").val();
        var Ubicacion = $.trim($("#F_ClaUbi").val());
        var aClaUbi = $("#aClaUbi").val();
        var aCbUbica = $("#aCbUbica").val();
        var IdLote = $("#F_IdLote").val();
        var aCbUbica = $("#aCbUbica").val();
        var aCbUbica = $("#aCbUbica").val();
        var ClaveUbica = $("#ClaveUbica").val();
        var ContarBan = $("#F_contarBan").val();
        var contar = 0;
        Ubicacion = Ubicacion.toUpperCase();
        if ((CantMov !== "") && (Ubicacion !== "")) {

            if (parseInt(CantMov) > parseInt(CantAnt)) {
                swal({
                    title: "La Cantidad a mover no puede ser mayor a la cantidad en existencia.!",
                    text: "",
                    type: "error"
                }, function () {
                    document.getElementById('CantMov').focus();
                    return false;
                });
            } else {
                if (parseInt(CantMov) === 0) {
                    swal({
                        title: "La cantidad a mover debe ser mayor a 0.!",
                        text: "",
                        type: "error"
                    }, function () {
                        document.getElementById('CantMov').focus();
                        return false;
                    });
                } else {
                    if (Ubicacion.trim() === aClaUbi.trim() || Ubicacion.trim() === aCbUbica.trim()) {
                        swal({
                            title: "La Ubicación no puede ser igual a la anterior.!",
                            text: "",
                            type: "error"
                        }, function () {
                            document.getElementById('F_ClaUbi').focus();
                            return false;
                        });
//         
                    } else {
                        
                         if (ContarBan > 0) {
                               
                            if (Ubicacion.match(/.*CONTROLADO.*/) || Ubicacion.trim() === "CONTROLADOCDIST" || Ubicacion.trim() === "CONTROLADOPROXCAD" || Ubicacion.trim() === "CONTROLADOURGENTE" || Ubicacion.trim() === "CONCILIACIONINV" || Ubicacion.match(/.*CTR.*/)) {

                    
                        swal({
                            title: "Seguro que desea hacer la redistribución?",
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
                                    $.ajax({
                                        url: "../RedistribucionTransaccional",
                                        data: {accion: "Redistribuir", Ubicacion: Ubicacion, IdLote: IdLote, CantMov: CantMov},
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
                                                    //alert(ClaveUbica);
                                                    //location.reload();
                                                    window.location = "leerInsRedistClave.jsp?ClaPro=" + ClaveUbica + "";
                                                });
                                            } else {
                                                swal("Atención", "Redistribución no aplicada", "error");
                                            }

                                          
                                        }
                                    });
                                });
//                        }
                    
                }else{
                    swal({
                            title: "La Ubicación no valida para controlado.!",
                            text: "",
                            type: "error"
                        }, function () {
                            document.getElementById('F_ClaUbi').focus();
                            return false;
                        });
                }
            }//fin de ubicacion
            
            else{
                 if (!Ubicacion.match(/.*CONTROLADO.*/) && Ubicacion.trim() !== "CONTROLADOPROXCAD" && Ubicacion.trim() !== "CONTROLADOURGENTE" && Ubicacion.trim() !== "CONTROLADOCDIST" || Ubicacion.trim() !== "CONCILIACIONINV" || !Ubicacion.match(/.*CTR.*/)) {
                     
                swal({
                            title: "Seguro que desea hacer la redistribución?",
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
                                    $.ajax({
                                        url: "../RedistribucionTransaccional",
                                        data: {accion: "Redistribuir", Ubicacion: Ubicacion, IdLote: IdLote, CantMov: CantMov},
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
                                                    //alert(ClaveUbica);
                                                    //location.reload();
                                                    window.location = "leerInsRedistClave.jsp?ClaPro=" + ClaveUbica + "";
                                                });
                                            } else {
                                                swal("Atención", "Redistribución no aplicada", "error");
                                            }

                                          
                                        }
                                    });
                                });
            }
            else{
             swal({
                            title: "La Ubicación solo para controlado.!",
                            text: "",
                            type: "error"
                        }, function () {
                            document.getElementById('F_ClaUbi').focus();
                            return false;
                        });
                    }
        }
            
            
                    }
                }
            }
        }//ubicacion null 
        else {
            swal({
                title: "Verificar campos vacíos.!",
                text: "",
                type: "error"
            }, function () {
                document.getElementById('CantMov').focus();
                return false;
            });
        }


        /*
         var ordenCompra=$("#vOrden").val();
         var remision=$("#vRemi").val();
         $.ajax({
         url: "recepcionTransaccional",
         data: {accion: "IngresarRemision", ordenCompra: ordenCompra, remision: remision},
         type: 'POST',
         dataType: 'JSON',
         async: true,
         success: function (data)
         {
         if (data.msj)
         {
         alert("Compra validada con éxito");
         } else
         {
         alert("Error en la modificación contactar al departamento de sistemas.");
         }
         
         
         location.reload();
         
         
         }, error: function (jqXHR, textStatus, errorThrown) {
         alert("Error en sistema");
         }
         });
         
         
         */
    });
});
$('#F_ClaUbi').keyup(function () {
    var descripcion = $('#F_ClaUbi').val();
    $('#F_ClaUbi').autocomplete({
        source: "../JQInvenCiclico?accion=buscaClaUbi&descrip=" + descripcion,
        minLenght: 2,
        select: function (event, ui) {
            $('#F_ClaUbi').val(ui.item.F_ClaUbi);
            return false;
        }
    }).data('ui-autocomplete')._renderItem = function (ul, item) {
        return $('<li>')
                .data('ui-autocomplete-item', item)
                .append('<a>' + item.F_ClaUbi + '</a>')
                .appendTo(ul);
    };
});
$('#F_ClaUbi2').keyup(function () {
    var descripcion = $('#F_ClaUbi2').val();
    $('#F_ClaUbi2').autocomplete({
        source: "../JQInvenCiclico?accion=buscaClaUbi2&descrip=" + descripcion,
        minLenght: 2,
        select: function (event, ui) {
            $('#F_ClaUbi2').val(ui.item.F_ClaUbi2);
            return false;
        }
    }).data('ui-autocomplete')._renderItem = function (ul, item) {
        return $('<li>')
                .data('ui-autocomplete-item', item)
                .append('<a>' + item.F_ClaUbi2 + '</a>')
                .appendTo(ul);
    };
});
