 function esEntero(event) {
        var codigoTecla = (event.which) ? event.which : event.keyCode;
        if (codigoTecla === 45) { // Verifica si se presionó la tecla "-"
            return false; // Si se presionó "-", no se permite
        }z
        if (codigoTecla > 31 && (codigoTecla < 48 || codigoTecla > 57)) {
            return false; // Si no es un dígito, no se permite
        }
        return true;
    }
   
$(document).ready(function () {
    $('#datosfolios').dataTable();

    obtenerIdreg();
    obtenerProyecto();

    $('#btnDevolucion').click(function () {
        var Folio = $("#foliod").val();
        var Obs = $("#Obs").val();
        var Proyecto = $("#proyectod").val();
        var ubicaDevFact = $("#ubicadevFact").val();
       var tipo = $("#tipo").val();
        console.log("tipo de usuario: " + tipo);
        console.log("ubica" + ubicaDevFact);
        if ( tipo === "7"){
           
            if(ubicaDevFact === "0"){                
                 swal("Atención", "Favor de seleccionar la ubicación destino", "warning");                
                return false;
            }}
        if (Obs !== "") {
            swal({
                title: "¿Seguro que deseas aplicar la Devolución?",
                text: "No podrás deshacer este paso...",
                type: "warning",
                showCancelButton: true,
                //cancelButtonText: "Mejor no",
                closeOnConfirm: false,
                confirmButtonColor: "#DD6B55",
                showLoaderOnConfirm: true,
                confirmButtonText: "Adelante!"
            },
                    function () {
                        //setTimeout(function () {
                        $.ajax({
                            type: "GET",
                            //url: "ConsultaDevolucion?accion=validaDevolucionTrans&foliosel=+" + Folio + "&Obs=" + Obs + "&Proyecto=" + Proyecto + "",
                            url: "ConsultaDevolucion",
                            data: {accion: "validaDevolucionTrans", foliosel: Folio, Obs: Obs, Proyecto: Proyecto, ubicaDevFact: ubicaDevFact},
                            dataType: "JSON",
                            async: true,
                            success: function (data) {
                                if (data.msj) {
                                    swal({
                                        title: "Devolución aplicado correctamente!",
                                        text: "",
                                        type: "success"
                                    }, function () {
                                        window.location = "DevolucionesFacturas?Accion=ListaRemision";
                                    });

                                } else {
                                    swal("Datos no Registrados no remisionado", "", "error");
                                    //location.reload();
                                }
                                /*if (jQuery.isEmptyObject(data)) {
                                 swal("Atención", "Devolución no aplicada", "error");
                                 } else {
                                 $.each(data, function (idx, elem) {
                                 if (elem.Validado == "Validado") {
                                 swal({
                                 title: "Devolución aplicado correctamente!",
                                 text: "",
                                 type: "success"
                                 }, function () {
                                 window.location = "DevolucionesFacturas?Accion=btnEliminar&folio=" + Folio + "&Proyecto=" + Proyecto + "";
                                 });
                                 } else {
                                 swal("Atención", "Devolución no aplicada", "error");
                                 }
                                 });
                                 }*/
                            }
                        });

                    });
        } else {
            swal("Atención", "Favor de llenar campo de observaciones", "warning");
        }
        });

});

function obtenerIdreg() {
    var Folio = $('#fol_gnkl').val();
    var Proyecto = $('#proyectof').val();
    $.ajax({
        type: "GET",
        url: "ConsultaDevolucion?accion=obtenerIdReg&foliosel=" + Folio + "",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data, function (idx, elem) {
                var isValidado;
                isValidado = elem.validado;
                $('#btnEliminar' + elem.IdReg + '').click(function () {
                    $.ajax({
                        type: "GET",
                        url: "ConsultaDevolucion?accion=EliminarReg&idreg=" + elem.IdReg + "&foliosel=+" + Folio + "",
                        dataType: "json",
                        success: function (data) {
                            if (jQuery.isEmptyObject(data)) {
                                swal("Atención", "Registro no eliminado", "warning");
                            } else {
                                $.each(data, function (idx, elem) {
                                    if (elem.Procesado == "Terminado") {
                                        //location.reload();
                                        window.location = "DevolucionesFacturas?Accion=btnEliminar&folio=" + Folio + "&Proyecto=" + Proyecto + "";
                                    } else {
                                        swal("Atención", "Registro no eliminado", "warning");
                                    }
                                });
                            }
                        }
                    });
                });
                $('#btnEliminarClave' + elem.IdReg + '').click(function () {
                    $.ajax({
                        type: "GET",
                        url: "ConsultaDevolucion?accion=EliminarReg&idreg=" + elem.IdReg + "&foliosel=+" + Folio + "",
                        dataType: "json",
                        success: function (data) {
                            if (jQuery.isEmptyObject(data)) {
                                swal("Atención", "Registro no eliminado", "warning");
                            } else {
                                $.each(data, function (idx, elem) {
                                    if (elem.Procesado == "Terminado") {
                                        //location.reload();
                                        window.location = "DevolucionesFacturas?Accion=btnEliminarClave&folio=" + Folio + "&Proyecto=" + Proyecto + "";
                                    } else {
                                        swal("Atención", "Registro no eliminado", "warning");
                                    }
                                });
                            }
                        }
                    });
                });
            });
        }
    });
}

function obtenerProyecto() {
    $('#Nombre option:gt(0)').remove();
    $("#Nombre").append("<option value=0>Seleccione</option>").select2();
    $('#Nombre option:gt(0)').remove();
    $.ajax({
        url: "ExistenciaProyecto",
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