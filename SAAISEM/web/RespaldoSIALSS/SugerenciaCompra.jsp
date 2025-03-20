<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    Date fechaActual = new Date();
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    String fechaSistema = formateador.format(fechaActual);

    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    int Cantidad = 0;

    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("indexMedalfa.jsp");
    }

    ConectionDB con = new ConectionDB();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="jspf/menuPrincipalCompra.jspf" %>


            <h3>Crear Sugerencia</h3>

            <div class="row">
                <div class="col-lg-12">
                </div>
            </div>
            <hr/>
            <div>
                <form action="Capturar" method="post" name="formulario" id="formulario">
                    <div class="row">
                        <div class="container">
                            <div class="row">
                                <h4 class="col-sm-2" >Sugerencia:</h4>
                            </div>
                            <div class="row">
                                <div>
                                    <textarea name="ObsGral" id="ObsGral" class="form-control" cols="40" required=""></textarea>
                                </div>
                            </div>
                            <br />
                            <div class="row">
                                <label class="control-label col-sm-2">Usuario Solicitante</label>
                                <div class="col-sm-6">
                                    <input type="text" id="Solicitante" name="Solicitante" class="form-control" placeholder="Introduzca su Nombre" value="<%=usua%>" readonly="" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <br/>
                    <div>
                        <button class="btn btn-success btn-block" type="button" name="BtnSugerenciaCompra" id="BtnSugerenciaCompra">Registrar Sugerencia</button>
                    </div>
                </form>
            </div>
        </div>
        <br><br><br>

        <%@include file="jspf/piePagina.jspf" %>
        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/jquery.dataTables.js"></script>
        <script src="js/dataTables.bootstrap.js"></script>
        <script src="js/funcCapReq.js"></script>
        <script src="js/FuncionesBasicas.js"></script>
        <script src="js/sugerencia/Sugerencia.js"></script>
        <link rel="stylesheet" type="text/css" media="all" href="style.css" />
        <script src="http://code.jquery.com/jquery-1.7.min.js"></script>
        <script src="js/sweetalert.min.js" type="text/javascript"></script>
        <script src="js/jquery-ui.js"></script>
        <script>

            $(document).ready(function () {
                $('#datosProv').dataTable();

            });
        </script>
        <script>
            function Confirmar() {
                /*var valida = confirm('Seguro que desea registrar su Requerimiento?');
                 if (valida) {
                 $('#formCambio').submit();
                 //this.disabled = true
                 }else {
                 
                 return false;
                 }*/

                var missinginfo = "";
                if ($("#periodo").val() == "") {
                    missinginfo += "\n El campo Período no debe de estar vacío";
                }
                if ($("#nombrem").val() == "") {
                    missinginfo += "\n El campo Nombre no debe de estar vacío";
                }

                if (missinginfo != "") {
                    missinginfo = "\n TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ENVIAR PETICIÓN DE REQUERIMIENTO\n" + missinginfo + "\n\n ¡INGRESA LOS DATOS FALTANTES Y TRATA OTRA VEZ!\n";
                    alert(missinginfo);

                    return false;
                } else {

                    return true;
                }
                // var x = confirm("Seguro que desea Confirmar este pedido?");
                //if (x)
                //document.getElementById('Loader').style.display = 'block';
                //else
                //  return false;

            }
            /*function valida_alta() {               
             document.getElementById('Loader').style.display = 'block';
             }*/
        </script>

    </body>
</html>

