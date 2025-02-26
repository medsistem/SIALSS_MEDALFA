

$(function () {

    obtenerIdreg();

    $("#btnSave1").click(function () {
        var lote = $("#loteNuevo").val();
        var caducidad = $("#CaducidadNuevo").val();
        var cantidad = $("#CantidadNuevo").val();
        var cb = $("#CbNuevo").val();
        var ordenSuministro = $("#ordenSuministroNuevo").val();
        var origen = $("#origen").val();
        var marca = $("#marcaNuevo").val();
       // var marcacomercial = $("#MarcaRNuevo").val();
        var tarimas = $("#tarimasNuevo").val();
        var cajas = $("#cajasNuevo").val();
        var pzacaja = $("#pzacajasNuevo").val();
        var cajasi = $("#cajasiNuevo").val();
        var resto = $("#restoNuevo").val();
        var cantPedido = $("#cantPedido").val();
        var cantCompra = $("#cantCompra").val();
        var cantidadTemp = $("#cantidadTemp").val();
        var fuenteFinanza = $("#fuenteFinanza").val();
        var marcaComercial = $("#MarcaRNuevo").val();
        var unidadFonsabi = $("#unidadFonsabi").val();


        var pesoPieza = $("#pesoPiezaNuevo").val();
        var unidadPesoPieza = $("#unidadPesoPiezaNuevo").val();
        var pesoCaja = $("#pesoCajaNuevo").val();
        var unidadPesoCaja = $("#unidadPesoCajaNuevo").val();
        var pesoConcentrada = $("#pesoConcentradaNuevo").val();
        var unidadPesoConcentrada = $("#unidadPesoConcentradaNuevo").val();
        var pesoTarima = $("#pesoTarimaNuevo").val();
        var unidadPesoTarima = $("#unidadPesoTarimaNuevo").val();

        var altoPieza = $("#altoPiezaNuevo").val();
        var anchoPieza = $("#anchoPiezaNuevo").val();
        var largoPieza = $("#largoPiezaNuevo").val();
        var unidadVolPieza = $("#unidadVolPiezaNuevo").val();
        var altoCaja = $("#altoCajaNuevo").val();
        var anchoCaja = $("#anchoCajaNuevo").val();
        var largoCaja = $("#largoCajaNuevo").val();
        var unidadVolCaja = $("#unidadVolCajaNuevo").val();
        var altoConcentrada = $("#altoConcentradaNuevo").val();
        var anchoConcentrada = $("#anchoConcentradaNuevo").val();
        var largoConcentrada = $("#largoConcentradaNuevo").val();
        var unidadVolConcentrada = $("#unidadVolConcentradaNuevo").val();
        var altoTarima = $("#altoTarimaNuevo").val();
        var anchoTarima = $("#anchoTarimaNuevo").val();
        var largoTarima = $("#largoTarimaNuevo").val();
        var unidadVolTarima = $("#unidadVolTarimaNuevo").val();

        var id = $("#idCompraTemporal").val();
        var idVolumetria = $("#idVolumetria").val();
        var usuario = $("#UserActual").val();
        var costo = $("#costo").val();
        var factorEmpaque = $("#factorEmpaqueNuevo").val();
        var cartaCanje = $("#cartaCanjeNuevo").val();
        var tipoInsumo = $("#tipoInsumoNuevo").val();
        
        console.log("Entra");
        var ingreso = ((parseInt(tarimas) * parseInt(cajas) * parseInt(pzacaja)) + (parseInt(cajasi) * parseInt(pzacaja) + parseInt(resto)) );
        console.log(ingreso);
        var piezas = parseInt(cantidadTemp) + parseInt(cantCompra) + ingreso ;
        console.log(piezas);
        if (piezas > parseInt(cantPedido)){
            alert("Excede la cantidad a recibir, Verificar cantidad");
            return false;
        }

        if (tipoInsumo === "CONTROLADO") {           
            
                var dtFechaActual = new Date();
                var sumYear = parseInt(1);
                dtFechaActual.setFullYear(dtFechaActual.getFullYear() + sumYear);
                var fechaSpl = caducidad.split("/");
                var Caducidad = fechaSpl[2] + "-" + fechaSpl[1] + "-" + fechaSpl[0];              

                if (Date.parse(dtFechaActual) >= Date.parse(Caducidad)) {                    
                    if (cartaCanje === "") {
                        alert("Ingresar Carta Canje");                        
                        return false;
                    }
                }
            }
        

        if (origen === '4' || origen === '5' || origen === '10' || origen === '11' || origen === '14' || origen === '15' || origen === '16'|| origen === '17'|| origen === '18') {

            if (ordenSuministro === "") {
                alert("Falta orden de suministro");
                return false;
            }
            if (origen === '19'){
             if (unidadFonsabi === "" )
                 alert("Falta ingresar unidad");
            }
        }


        if (lote === "") {
            alert("Ingresar un lote válido por favor.");
            return false;
        } else if (caducidad === "") {
            alert("ingresar una caducidad válida por favor.");
            return false;
        } else if (cantidad <= 0) {
            alert("ingresar una cantidad válida por favor.");
            return false;
        } else if (cb === "") {
            alert("ingresar una cb válido por favor.");
            return false;

        } else if (marca === "") {
            alert("ingresar una marca válida por favor.");
            return false;
        } else if (tarimas === "") {
            alert("ingresar Cantidad en Tarima válida por favor.");
            return false;
        } else if (cajas === "") {
            alert("ingresar Cantidad en Cajas válida por favor.");
            return false;
        } else if (pzacaja === "") {
            alert("ingresar Cantidad en Piezas x Cajas válida por favor.");
            return false;
        } else if (cajasi === "") {
            alert("ingresar Cantidad en Cajas x Tarimas Incompletas válida por favor.");
            return false;
        } else if (resto === "") {
            alert("ingresar Cantidad en Resto válida por favor.");
            return false;
        } else if (factorEmpaque === "") {
            alert("ingresar Cantidad en Factor de Empaque válida por favor.");
            return false;
        } else {
          
            $.ajax({
                url: "recepcionTransaccional",
                //data: {accion: "EditarLotes", id: id, lote: lote, caducidad: caducidad, cantidad: cantidad, cb: cb, marca: marca, tarimas: tarimas, cajas: cajas, pzacaja: pzacaja, cajasi: cajasi, resto: resto, usuario: usuario, costo: costo},
               
                data: {accion: "EditarLotes", id: id, lote: lote, caducidad: caducidad, cantidad: cantidad, origen: origen, cb: cb, ordenSuministro: ordenSuministro, marca: marca, marcaComercial: marcaComercial, tarimas: tarimas, cajas: cajas, pzacaja: pzacaja, cajasi: cajasi, resto: resto,
                    usuario: usuario, costo: costo, factorEmpaque: factorEmpaque, pesoPieza: pesoPieza, unidadPesoPieza: unidadPesoPieza, pesoCaja: pesoCaja,
                    unidadPesoCaja: unidadPesoCaja, pesoConcentrada: pesoConcentrada, unidadPesoConcentrada: unidadPesoConcentrada, pesoTarima: pesoTarima, unidadPesoTarima: unidadPesoTarima, altoPieza: altoPieza, anchoPieza: anchoPieza,
                    largoPieza: largoPieza, unidadVolPieza: unidadVolPieza, altoCaja: altoCaja, anchoCaja: anchoCaja, largoCaja: largoCaja, unidadVolCaja: unidadVolCaja, altoConcentrada: altoConcentrada, anchoConcentrada: anchoConcentrada,
                    largoConcentrada: largoConcentrada, unidadVolConcentrada: unidadVolConcentrada, altoTarima: altoTarima, anchoTarima: anchoTarima, largoTarima: largoTarima, unidadVolTarima: unidadVolTarima, idVolumetria: idVolumetria, cartaCanje: cartaCanje, unidadFonsabi: unidadFonsabi},
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
                    alert("Error en sistema al editar");
                }
            });
        }


    });

    $("#validarRemision").click(function () {
        var ordenCompra = $("#vOrden").val();
        var remision = $("#vRemi").val();
        var UbicaN = $("#UbicaN option:selected").val();
        var unidadFon = $("#uFonsabi").val();
        
        console.log(unidadFon + "unidadFonsabi");
//        var MarcaR = $("#MarcaR option:selected").val();
//        var factorEmpaque = $("#factorEmpaqueNuevo").val();
        if ($('#UbicaN').val().trim() === '0') {
            swal({
                title: "¿Seleccione Una Ubicacion?",
                text: "Seleccionar Una Ubicacion Valida:" + UbicaN,
                type: "warning",
                //  showCancelButton: true,
                closeOnConfirm: false,

            });
        } else {

            swal({
                title: "¿Seguro de Validar la Compra?",
                text: "No podrás deshacer este paso, La ubicacion es: " + UbicaN,
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
                            url: "recepcionTransaccional",
                            data: {accion: "IngresarRemision", ordenCompra: ordenCompra, remision: remision, UbicaN: UbicaN, unidadFon: unidadFon},
                            type: 'POST',
                            dataType: 'JSON',
                            async: true,
                            success: function (data) {
                                if (data.msj) {
                                    //alert("Compra validada con éxito");
                                    swal({
                                        title: "Compra validada correctamente!",
                                        text: "",
                                        type: "success"
                                    }, function () {
                                        location.reload();
                                        //window.location = "verificarCompraAuto.jsp?vOrden=" + vOrden + "&vRemi=" + vRemi + "";
                                    });
                                } else {
                                    alert("Error en la Validación contactar al departamento de sistemas.");
                                }
                                location.reload();
                            }, error: function (jqXHR, textStatus, errorThrown) {
                                alert("Error en sistema AL VALIDAR");
                            }
                        });
                    });
        }
    });


});

function obtenerIdreg() {
    var vOrden = $('#vOrden').val();
    var vRemi = $('#vRemi').val();


    //alert(vOrden + " / " + vRemi);
    $.ajax({
        type: "GET",
        url: "recepcionTransaccional?accion=obtenerIdReg&vOrden=" + vOrden + "&vRemi=" + vRemi + "",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data, function (idx, elem) {
                var isValidado;
                isValidado = elem.validado;

                //alert(elem.IdReg);
                $('#Validar_' + elem.IdReg).click(function () {
                    var UbicaN = $('#UbicaN option:selected').val();
//                    var MarcaR = $("#MarcaR option:selected").val();

                    if ($('#UbicaN').val().trim() === '0') {
                        swal({
                            title: "¿Seleccione Una Ubicacion?",
                            text: "Seleccionar Una Ubicacion Valida:" + UbicaN,
                            type: "warning",
                            //  showCancelButton: true,
                            closeOnConfirm: false,

                        });
                    } else {

                        swal({
                            title: "¿Seguro de Validar Parcial?",
                            text: "No podrás deshacer este paso, La ubicacion es: " + UbicaN,
                            type: "warning",
                            showCancelButton: true,
                            //cancelButtonText: "Mejor no",
                            closeOnConfirm: false,
                            confirmButtonColor: "#DD6B55",
                            showLoaderOnConfirm: true,
                            confirmButtonText: "Continuar!"
                        },
                                function () {
                                    $('#Validar_' + elem.IdReg).prop('disabled', true);

                                    $.ajax({
                                        url: "recepcionTransaccional",
                                        data: {accion: "RegistrarDatosParcial", IdReg: elem.IdReg, vOrden: vOrden, vRemi: vRemi, UbicaN: UbicaN},
                                        type: 'POST',
                                        dataType: 'JSON',
                                        async: true,
                                        success: function (data) {
                                            if (data.msj) {
                                                swal({
                                                    title: "Validación Parcial Realizado correctamente!",
                                                    text: "",
                                                    type: "success"
                                                }, function () {
                                                    location.reload();
                                                    //window.location = "verificarCompraAuto.jsp?vOrden=" + vOrden + "&vRemi=" + vRemi + "";
                                                });
                                            } else {
                                                swal("Atención", "Datos no aplicados", "error");
                                                $('#Validar_' + elem.IdReg).prop('disabled', false);
                                                //location.reload();
                                            }
                                        }
                                    });
                                });
                    }
                });
            });
        }
    });

}



