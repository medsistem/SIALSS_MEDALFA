<%--
    Pantalla de bloqueo por inactividad para cuando se navega a otra página con la sesión bloqueada
    (la manda SesionFilter). Al desbloquear regresa a la página que se pidió.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="mx.medalfa.sialss.seguridad.Acceso"%>
<%@page import="mx.medalfa.sialss.seguridad.PoliticaSesion"%>
<%
    String ctxBloqueo = request.getContextPath();
    Acceso accesoBloqueo = PoliticaSesion.acceso(session);
    if (accesoBloqueo == null) {
        response.sendRedirect(ctxBloqueo + "/indexalmacen.jsp?motivo=inactividad");
        return;
    }
    // Solo rutas internas: evita redirigir a otro sitio.
    String volverBloqueo = request.getParameter("volver");
    if (volverBloqueo == null || !volverBloqueo.startsWith("/") || volverBloqueo.startsWith("//") || volverBloqueo.contains("\\")) {
        volverBloqueo = "/" + accesoBloqueo.menu;
    }
    String nombreBloqueo = String.valueOf(session.getAttribute("nombre") == null ? "" : session.getAttribute("nombre"));
    java.util.function.UnaryOperator<String> attr = s -> s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS · Sesión bloqueada</title>
        <link href="<%=ctxBloqueo%>/css/sialss-app.css?v=2.0.19" rel="stylesheet">
    </head>
    <body class="sialss-login-pagina">
        <noscript><p class="sialss-login">Active JavaScript para desbloquear la sesión.</p></noscript>
        <script src="<%=ctxBloqueo%>/js/sialss-sesion.js?v=2.0.5" defer
                data-ctx="<%=ctxBloqueo%>"
                data-bloqueo="<%=PoliticaSesion.bloqueoMs() / 1000%>"
                data-cierre="<%=PoliticaSesion.cierreMs() / 1000%>"
                data-usuario="<%=attr.apply(nombreBloqueo)%>"
                data-login="<%=accesoBloqueo.paginaLogin%>"
                data-modo="pagina"
                data-volver="<%=attr.apply(volverBloqueo)%>"></script>
    </body>
</html>
