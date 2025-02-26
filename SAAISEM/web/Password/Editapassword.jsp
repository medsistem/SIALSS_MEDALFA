<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%
    /**
     * Para verificar las compras que están en proceso de ingreso
     */
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "", pedido = "", IdUsu = "", Usuario = "",Area="";

    //Verifica que este logeado el usuario
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        Usuario = (String) sesion.getAttribute("Usuario");
        Area = (String) sesion.getAttribute("Area");
    } else {
        response.sendRedirect("index.jsp");
    }

    

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/datepicker3.css" rel="stylesheet">
        <!---->
        <link rel="stylesheet" type="text/css" media="all" href="ValidaPass/style.css" />	
        <script src="http://code.jquery.com/jquery-1.7.min.js"></script>
        <script src="ValidaPass/script.js"></script>
        <link href="css/select2.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap-switch.css" rel="stylesheet" type="text/css"/>
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4><h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4></h4>

            <%@include file="../jspf/menuPrincipal.jspf"%>
            <div class="row">
                <div class="col-sm-12">
                    <h2>Editar Usuario</h2>
                </div>
            </div>
            <hr/>
            <form action="EditaPassword" method="post">
                <div class="row">
                    <label class="col-sm-2">
                        Estatus
                    </label>
                    <div class="col-sm-4">                        
                        <div class="bootstrap-switch bootstrap-switch-wrapper bootstrap-switch-id-switch-offText bootstrap-switch-animate bootstrap-switch-on">
                            <div class="bootstrap-switch-container">
                                <input id="estadoSwitch" name="stadoSwitch" type="checkbox" data-on-text="Activo" data-off-text="Suspendido" <c:if test="${usuario.sts=='A'}">checked="true"</c:if>>
                                </div>
                            </div>
                    </div>
                        <div class="col-sm-2">
                            <input type="hidden" class="form-control" name="Id" id="Id" value="${usuario.id}" />
                            <input type="hidden" class="form-control" name="Usuarios" id="Usuarios" value="${usuario.usuario}" />
                            <input type="hidden" class="form-control" name="StsActual" id="StsActual" value="${usuario.sts}" />
                            <button class="form-control btn btn-warning glyphicon glyphicon-random" name="action" value="ModificaSts">&nbsp;Modificar Sts</button>
                        </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Nombre: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="Nombre" id="Nombre" placeholder="Ingrese Nombre" required="" readonly="" value="${usuario.nombre}" />
                    </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Apellido Paterno: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="ApellidoP" id="ApellidoP" placeholder="Ingrese Apellido Paterno" required="" readonly="" value="${usuario.apaterno}" />
                    </div>                    
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Apellido Materno: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="ApellidoM" id="ApellidoM" placeholder="Ingrese Apellido Materno" required="" readonly="" value="${usuario.amaterno}" />
                    </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Ingrese Correo: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="Correo" id="Correo" placeholder="Ingrese Correo" value="${usuario.correo}" />
                    </div>
                </div>
                <div class="row">
                    <label for="pswd" class="col-sm-2">
                        <h4>Ingrese Contraseña: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="password" class="form-control" min="6" maxlength="8" name="pswd" id="pswd" placeholder="Ingrese Contraseña Nueva" autofocus="autofocus" />
                    </div>                    
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Repita Contraseña: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="password" class="form-control" min="6" maxlength="8" name="pswd2" id="pswd2" placeholder="Repita Contraseña Nueva" />
                    </div>
                </div>                
                <div class="row">
                    
                    <input class="form-control btn btn-block btn-info" type="submit" name="action" id="action" value="Actualizar" onclick="return valida_alta();" />
                    
                </div>
                <br/>
            </form>
            <div style="display: none;" class="text-center" id="Loader">
                <img src="imagenes/ajax-loader-1.gif" height="150" />
            </div>
            <div id="pswd_info">
                <h4>Requerimiento del password:</h4>
                <ul>
                    <!--li id="letter" class="invalid">Al Menos <strong>Una Letra</strong></li-->
                    <li id="capital" class="invalid"><strong>Una Letra Mayuscula</strong></li>
                    <li id="number" class="invalid"><strong>Un Número</strong></li>
                    <li id="length" class="invalid"><strong> 8 Caracteres</strong></li>
                    <li id="igual" class="invalid"><strong> Contraseña Igual</strong></li>
                </ul>
            </div>
        </div>        
        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/bootstrap-datepicker.js"></script>    
        <script src="js/select2.js" type="text/javascript"></script>
        <script src="js/bootstrap-switch.js" type="text/javascript"></script>
        <script>
            $(document).ready(function () {
                $('#estadoSwitch').bootstrapSwitch();
            });
                        $("#usuario").select2();
                        function valida_alta() {

                            var missinginfo = "";
                            if ($("#Nombre").val() == "") {
                                missinginfo += "\n El campo Nombre no debe de estar vacío";
                            }
                            if ($("#ApellidoP").val() == "") {
                                missinginfo += "\n El campo Apellido Paterno no debe de estar vacío";
                            }
                            if ($("#ApellidoM").val() == "") {
                                missinginfo += "\n El campo Apellido Materno no debe de estar vacío";
                            }
                            if ($("#Correo").val() == "") {
                                missinginfo += "\n El campo Correo no debe de estar vacío";
                            }
                            if ($("#pswd").val() == "") {
                                missinginfo += "\n El campo Contraseña no debe de estar vacío";
                            }
                            if ($("#pswd2").val() == "") {
                                missinginfo += "\n El campo Repetir Contraseña no debe de estar vacío";
                            }

                            if (missinginfo != "") {
                                missinginfo = "\n TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ACTUALIZAR INFORMACIÓN:\n" + missinginfo + "\n\n ¡INGRESA LOS DATOS FALTANTES Y TRATA OTRA VEZ!\n";
                                alert(missinginfo);

                                return false;
                            } else {
                                document.getElementById('Loader').style.display = 'block';
                                return true;
                            }

                        }

        </script>
        <%@include file="../jspf/piePagina.jspf" %>
    </body>
</html>
