<%-- 
    Document   : InventarioExcel
    Created on : 12/10/2015, 04:45:46 PM
    Author     : Mario
--%>

<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<%
    /**
     * Para cargar el excel del requerimiento
     */
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            
            <%@include file="jspf/menuPrincipal.jspf"%>
        </div>
        <div class="panel container">
            
            <div class="panel-primary">
                <div class="panel-heading">
                    Generar Marbetes
                </div>                
                    <form method="post" class="jumbotron"  action="MarbeteCat" name="form1">
                        <div class="row">
                            <label for="Nombre" class="col-sm-1 control-label">Clave:</label>
                            <div class="col-sm-2">
                                <input class="form-control" type="text" name="clave" id="clave" value="" required/>                                    
                            </div>
                            <label for="Nombre" class="col-sm-1 control-label">Lote:</label>
                            <div class="col-sm-2">
                                <input class="form-control" type="text" name="lote" id="lote" value="" required/>                                    
                            </div>
                            <label for="Nombre" class="col-sm-1 control-label">Caducidad:</label>
                            <div class="col-sm-2">
                                <input class="form-control" type="date" name="cadu" id="cadu" value="" required/>                                    
                            </div>
                            
                        </div>
                        <br>
                        <br>
                        <div class="row">
                            <label for="Nombre" class="col-sm-1 control-label">CB:</label>
                            <div class="col-sm-2">
                                <input class="form-control" type="text" name="cb" id="cb" value="" required/>                                    
                            </div>
                            
                            <label for="Nombre" class="col-sm-1 control-label">Resto:</label>
                            <div class="col-sm-2">
                                <input class="form-control" type="number" name="resto" id="resto" value="" min="1"  maxlength = "4" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);"/>                                    
                            </div>                            
                        </div>
                         
                        <br>
                        <br>
                        <br>
                        <div class="form-group col-sm-6">
                            <button class="btn btn-block btn-info" type="submit" name="accion" value="generarCarta"><span class="glyphicon glyphicon-refresh"></span>Generar Carta</button>
                        </div>
                        <div class="form-group col-sm-6">
                            <button class="btn btn-block btn-warning" type="submit" name="accion" value="generarMedia"><span class="glyphicon glyphicon-refresh"></span>Generar Media Carta</button>
                        </div>
                    </form>
                </div>
            </div>
            <br>
        
        
        <%@include file="jspf/piePagina.jspf"%>
        
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/bootstrap3-typeahead.js" type="text/javascript"></script>

        <script>
            $(document).ready(function () {

                $("#clave").typeahead({
                    source: function (request, response) {

                        $.ajax({
                            url: "AutoCompleteMedicamentos",
                            dataType: "json",
                            data: request,
                            success: function (data, textStatus, jqXHR) {
                                console.log(data);
                                var items = data;
                                response(items);
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                console.log(textStatus);
                            }
                        });
                    }

                });
                $("#descripcion").typeahead({
                    source: function (request, response) {

                        $.ajax({
                            url: "AutoCompleteMedicamentosDesc",
                            dataType: "json",
                            data: request,
                            success: function (data, textStatus, jqXHR) {
                                console.log(data);
                                var items = data;
                                response(items);
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                console.log(textStatus);
                            }
                        });
                    }

                });


            });

            //Obtener la caducidad cuando segun el lote
            $(".rowButton").click(function () {

                var $row = $(this).closest("tr");    // Find the row
                var $clave = $row.find("td.clave").text(); // Find the text             
                var $lote = $row.find("td.lote").text(); // Find the text
                var $cadu = $row.find("td.cadu").text(); // Find the text
                var $cant = $row.find("td.cantidad").text(); // Find the text
             /// var $id = $row.find("td.id").text(); // Find the text
                var $desc = $row.find("td.desc").text(); // Find the text
                //var $costo = $row.find("td.costo").text(); // Find the text
                var $cb = $row.find("td.cb").text(); // Find the text

                $("#claveMod").val($clave);
                $("#descMod").val($desc);
                $("#loteMod").val($lote);
                $("#caduMod").val(formatDate($cadu));
                $("#cantMod").val($cant);
                //$("#costoMod").val($costo);
                $("#cbMod").val($cb);
               // $("#idMod").val($id);

            }); 
            $("#caduMod").val(formatDate($cadu));
                $("#cantMod").val($cant);
                //$("#costoMod").val($costo);
                $("#cbMod").val($cb);
               // $("#idMod").val($id);

            
            $(".rowButtonEli").click(function () {

                var $row = $(this).closest("tr");    // Find the row
                var $id = $row.find("td.id").text(); // Find the text
                $("#idEli").val($id);

            });

            function stopRKey(evt) {
                var evt = (evt) ? evt : ((event) ? event : null);
                var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
                if ((evt.keyCode === 13)) {
                    return false;
                }
            }

            function formatDate(input) {
                var datePart = input.match(/\d+/g),
                        year = datePart[2], // get only two digits
                        month = datePart[1], day = datePart[0];

                //return day + '/' + month + '/' + year;
                return year + '-' + month + '-' + day;
            }

            document.onkeypress = stopRKey;

        </script>
         <script> 
                            
            function filterInteger(evt,input) {
   
                var key = window.Event ? evt.which : evt.keyCode;    
                var chark = String.fromCharCode(key);
                var tempValue = input.value+chark;
                    
                    if((key >= 48 && key <= 57) ) {
                        return filter(tempValue);
                    } else {
                        return key == 8 || key == 13 || key == 0;
                    }
                }
                
            function filter(__val__) {
                var preg = /^[0-9]*$/;
            return preg.test(__val__);
            }
            
            document.getElementById('resto').addEventListener('keypress', function(evt) {
                if (filterInteger(evt, evt.target) === false) {
            evt.preventDefault();
            }
            });         
            
             document.getElementById('resto').addEventListener('keypress', function(evt) {
                if (filterInteger(evt, evt.target) === false) {
            evt.preventDefault();
            }
            }); 
           
        </script> 
    </body>
</html>

