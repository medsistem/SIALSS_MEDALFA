<%-- 
    Document   : index
    Created on : 01-oct-2013, 12:05:12
    Author     : wence
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    String info = null;

%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>SIALSS</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Bootstrap -->
        <link href="css/bootstrap.css" rel="stylesheet" media="screen">
        <link href="css/login.css" rel="stylesheet" media="screen">
        
    </head>
    <body>
       <div class="wrapper fadeInDown">
            <div id="formContent">              
                <!-- Icon -->
                <div class="fadeIn first">
                    <img src="imagenes/LogoMedalfa-.png"/>
                    <h2>SIALSS ALMACEN</h2>
                   
                </div>
                <!-- Login Form -->
                <form name ="form" id="forma-login" class="marco" action="login" method="post" >
                 
                    <input type="text" name="nombre" id="nombre" class="fadeIn second glyphicon glyphicon-user" autofocus="" placeholder="Usuario"/>  
                    <input type="password" name="pass" id="pass" class="fadeIn third"  placeholder="Contrase&ntilde;a V&aacute;lida"/>

                    <input type="submit" class="fadeIn fourth" value="Entrar" name="envio">       

                    <div id="formFooter">
                       
                        <% info = (String) session.getAttribute("mensaje");
                            //out.print(info);
                            if (!(info == null || info.equals(null))) {
                        %>
                        <div><%=info%>
                        <%
                            }
                            session.invalidate();
                        %>
                        <div ALIGN="right">Versión 1.0.2.1   <a href="/SIALSS_MDF/"><h2>Inicio</h2></a></div>
                    </div>
                    
                    </div>
                </form>     
            </div>
        </div> 
        
        
       
    </body>
</html>