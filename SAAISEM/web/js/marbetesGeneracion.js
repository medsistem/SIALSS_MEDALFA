$(function ()
{
    $("#folNumber").focus();
    $("#searchButton").click(function () {
        var folio = $("#folNumber").val();
        var Nombre = $("#Nombre").val();
        var RutaN = $("#RutaN").val();
        if ((folio === "") || (Nombre === "0") || (RutaN === "0")) {
            alert("Ingresar valor a todos los campos Folio-Proyecto-Ruta");
        } else {
            $.ajax({
                url: "../marbetesGenerar",
                data: {ban: 0, folio: folio, Proyecto: Nombre},
                type: 'POST',
                async: false,
                success: function (data)
                {
                    if (data.unidad === "") {
                        alert("Folio inexistente");
                        $("#uniName").val("");
                        $("#RF").val("");
                        $("#Cont").val("");
                    } else {
                        $("#uniName").val(data.unidad);
                        $("#RF").val(data.RF);
                        $("#Proyecto").val(Nombre);
                        $("#Ct").val(data.Ct);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert("Error Contactar al departamento de sistemas");
                }
            });
        }
    });

    $("#generarMarbete").click(function ()
    {
        var folio = $("#folNumber").val();
        var unidad = $("#uniName").val();
        var marbetes = $("#marbetNumber").val();
        var Proyecto = $("#Proyecto").val();
        var ruta = $("#ruta").val();
        var RF = $("#RF").val();
        var Ct = $("#Ct").val();
       // var Ruta = $("#ruta").val();
        var RutaN = $("#RutaN").val();
         
          
        if (folio === "") {
            alert("Ingresar número de folio.");
            return false;
        } else if (unidad === "") {
            alert("Ingresar Unidad de atención.");
            return false;
        } else if ((RutaN === "0") || (RutaN === "")) {
            alert("Ingresar el numero de Ruta");
        } else if ((marbetes === "0") || (marbetes === "")) {
            alert("Ingresar cantidad de marbetes o cantidad no valida");
            return false;
            //return false;
        } else {
            $.ajax({
                url: "../marbetesGenerar",
                data: {ban: 1, folio: folio, unidad: unidad, marbetes: marbetes, Proyecto: Proyecto, ruta: ruta, RutaN: RutaN},
                type: 'POST',
                async: false,
                success: function (data)
                {
                    if (data.msj === "realizado") {
                        $("#folNumber").val("");
                        $("#uniName").val("");
                        $("#marbetNumber").val("");
                        window.open("../Marbetes/MarbeteN.jsp?folio=" + folio + "&RF=" + RF + "&Proyecto=" + Proyecto + "&Ct=" + Ct + "&ruta=" + RutaN);
                    } else {
                        alert("Error contactar al desarrollador de sistemas.");
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert("Error Contactar al departamento de sistemas");
                }
            });
        }
    });
});


