<%-- Página que muestra SesionFilter cuando la opción del menú está bloqueada para el usuario. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    mx.medalfa.sialss.seguridad.Acceso accesoSinPermiso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(session);
    String menuSinPermiso = accesoSinPermiso != null ? accesoSinPermiso.menu : "index.jsp";
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS · Sin permiso</title>
        <link href="<%=request.getContextPath()%>/css/sialss-app.css?v=2.0.21" rel="stylesheet">
    </head>
    <body class="sialss-login-pagina">
        <main class="sialss-login" aria-labelledby="titulo-sin-permiso">
            <div class="sialss-login__titulo">
                <img src="<%=request.getContextPath()%>/imagenes/LogoMedalfa.png" alt="Medalfa" width="175" height="34">
                <div><p class="sialss-login__sistema">SIALSS</p></div>
            </div>
            <h1 id="titulo-sin-permiso">Opción bloqueada</h1>
            <p class="sialss-mensaje sialss-mensaje--error" role="alert">Esta opción del menú está bloqueada para su usuario.
                Si la necesita, comuníquese con el administrador del sistema.</p>
            <a class="sialss-boton sialss-boton--primario sialss-boton--ancho" href="<%=request.getContextPath()%>/<%=menuSinPermiso%>">Volver al menú</a>
        </main>
    </body>
</html>
