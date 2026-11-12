<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "", username="";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        username = (String) sesion.getAttribute("Usuario");
    } else {
        response.sendRedirect("index.jsp");
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
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
      <!--  <link href="css/navbar-fixed-top.css" rel="stylesheet">
        -->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="jspf/menuPrincipal.jspf" %>
            <div class="text-center">
                <br /><br /><br />
                <img src="imagenes/LogoMedalfa.png" class="img-fluid" width="200" height="100" alt="Logo GNK claro2" />
                <br/>

            </div>
             
        </div>
           
        <%@include file="jspf/piePagina.jspf" %>
        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <%
        String message = (String) sesion.getAttribute("mensaje");
        if (message != null) {
        %>
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/sweetalert.min.js" type="text/javascript"></script>
        <script type="text/javascript">
            swal({
                title: "<%=message%>",
                text: "",
                type: "warning"
            }, function () {
                //location.reload();
//                window.location = "facturaAtomaticaMich.jsp";
            });
        </script>
        <%
            sesion.removeAttribute("mensaje");
        }
        %>
    </body>

</html>

