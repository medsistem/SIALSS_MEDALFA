
$(function () {

    $("#searchFile").click(function ()
    {
        var fileName = $("#fileName").val();

        if (fileName.length < 4)
        {
            swal("Atención", "Favor de ingresar un nombre válido (longitud mínima de 4)", "warning");

        } else
        {
            searchFiles(fileName);
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

function download(fileName) {
    $.ajax({
        url: context + "/fileSearch",
        data: JSON.stringify({fileName: fileName, action: "downloadFile", type:localStorage.getItem("tipo")}),
        type: 'POST',
        xhrFields: {
            responseType: 'blob' // to avoid binary data being mangled on charset conversion
        },
        beforeSend: function ()
        {
            $("#myModal").modal({show: true});
        },
        success: function (blob, status, xhr)
        {
            var filename = "";
            var disposition = xhr.getResponseHeader('Content-Disposition');
            if (disposition) {
                var subs = disposition.split("filename=");
                if (subs != null && subs[1]){
                    filename = subs[1].replace(/['"]/g, '');
                }
            }

            if (typeof window.navigator.msSaveBlob !== 'undefined') {
                // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                window.navigator.msSaveBlob(blob, filename);
            } else {
                var URL = window.URL || window.webkitURL;
                var downloadUrl = URL.createObjectURL(blob);

                if (filename) {
                    // use HTML5 a[download] attribute to specify filename
                    var a = document.createElement("a");
                    // safari doesn't support this yet
                    if (typeof a.download === 'undefined') {
                        window.location.href = downloadUrl;
                    } else {
                        a.href = downloadUrl;
                        a.download = filename;
                        document.body.appendChild(a);
                        a.click();
                    }
                } else {
                    window.location.href = downloadUrl;
                }
                $("#myModal").modal('hide');
                setTimeout(function () {
                    URL.revokeObjectURL(downloadUrl);
                }, 100); // cleanup
            }
        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Error Contactar al departamento de sistemas");

        }
    });
}


function searchFiles(fileName)
{

    $.ajax({
        url: context + "/fileSearch",
        data: JSON.stringify({fileName: fileName, action: "searchFile", type:localStorage.getItem("tipo")}),
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
                mostrarDocumentos(data);
//                MostrarKardexRedistribucion(data.reabastecimiento);

                $("#myModal").modal('hide');
            }

        }, error: function (jqXHR, textStatus, errorThrown) {

            alert("Error Contactar al departamento de sistemas");

        }
    });
}

function mostrarDocumentos(data) {


    $("#listaDocumentos").remove();

    if (!data) {
        return;
    }
    var json = data;
    var aDataSet = [];
    for (var i = 0; i < json.length; i++)
    {
        var fileName = json[i];
        var btnDownload = "<button class=btn btn-block btn-success' onclick=\"download('"+fileName+"')\"><span class='glyphicon glyphicon-save'></span></button>";

        aDataSet.push([fileName, btnDownload]);
    }
    $(document).ready(function () {
        $('#dynamic').html('<table class="table table-borded table-condensed table-striped " width="100%" id="listaDocumentos"></table>');
        $('#listaDocumentos').dataTable({
            "aaData": aDataSet,
            "bAutoWidth": true,
            "aoColumns": [
                {"sTitle": "Nombre", "sClass": "center"},
                {"sTitle": "Descargar", "sClass": "center"}
            ]
        });


    });


}
