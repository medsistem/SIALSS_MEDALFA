<%-- Mi PIN (menú USUARIO): crear, cambiar o quitar el PIN para iniciar sesión. Servlet /MiPin. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String tokenPin = mx.medalfa.sialss.seguridad.MiPinServlet.token(sesion);
    String fechaPin = mx.medalfa.sialss.seguridad.MiPinServlet.fechaPin(sesion);
    String mensajePin = (String) sesion.getAttribute(mx.medalfa.sialss.seguridad.MiPinServlet.MENSAJE);
    boolean mensajeOk = Boolean.TRUE.equals(sesion.getAttribute(mx.medalfa.sialss.seguridad.MiPinServlet.MENSAJE_OK));
    sesion.removeAttribute(mx.medalfa.sialss.seguridad.MiPinServlet.MENSAJE);
    sesion.removeAttribute(mx.medalfa.sialss.seguridad.MiPinServlet.MENSAJE_OK);
    if (mensajePin != null) {
        mensajePin = mensajePin.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
    int digitosPin = mx.medalfa.sialss.seguridad.Pin.digitos();
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Mi PIN</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-mr sialss-pin">
            <h1>Mi PIN de acceso</h1>
            <p class="sialss-secundario">Con un PIN de <%=digitosPin%> números puede iniciar sesión o desbloquear la pantalla
                sin escribir su contraseña. Su contraseña sigue funcionando. Después de
                <%=mx.medalfa.sialss.seguridad.PoliticaSesion.intentosMaximos()%> intentos fallidos, con PIN o con contraseña,
                su usuario se bloquea.</p>

            <% if (mensajePin != null) { %>
            <p class="sialss-mensaje <%= mensajeOk ? "sialss-mensaje--ok" : "sialss-mensaje--error" %>" role="<%= mensajeOk ? "status" : "alert" %>"><%=mensajePin%></p>
            <% } %>

            <section class="sialss-pin__tarjeta" aria-labelledby="titulo-crear">
                <h2 id="titulo-crear"><%= fechaPin == null ? "Crear PIN" : "Cambiar PIN" %></h2>
                <p class="sialss-pin__ayuda"><%= fechaPin == null ? "Todavía no tiene PIN." : "Tiene un PIN desde el " + fechaPin + "." %></p>
                <form action="<%=request.getContextPath()%>/MiPin" method="post" id="forma-pin" novalidate>
                    <input type="hidden" name="token" value="<%=tokenPin%>">
                    <input type="hidden" name="accion" value="guardar">
                    <label for="pin">PIN nuevo</label>
                    <input type="password" id="pin" name="pin" inputmode="numeric" maxlength="<%=digitosPin%>" autocomplete="off" required>
                    <p class="sialss-pin__ayuda">Exactamente <%=digitosPin%> números. No se permiten números repetidos (111111) ni seguidos (123456).</p>
                    <label for="pin2">Repita el PIN</label>
                    <input type="password" id="pin2" name="pin2" inputmode="numeric" maxlength="<%=digitosPin%>" autocomplete="off" required>
                    <label for="contrasena">Su contraseña actual</label>
                    <input type="password" id="contrasena" name="contrasena" autocomplete="current-password" required>
                    <p id="pin-error" class="sialss-mensaje sialss-mensaje--error" role="alert" hidden></p>
                    <button type="submit" class="sialss-boton sialss-boton--primario sialss-boton--ancho">Guardar PIN</button>
                </form>
            </section>

            <% if (fechaPin != null) { %>
            <section class="sialss-pin__tarjeta" aria-labelledby="titulo-quitar">
                <h2 id="titulo-quitar">Quitar PIN</h2>
                <p class="sialss-pin__ayuda">Después solo podrá entrar con su contraseña.</p>
                <form action="<%=request.getContextPath()%>/MiPin" method="post">
                    <input type="hidden" name="token" value="<%=tokenPin%>">
                    <input type="hidden" name="accion" value="quitar">
                    <label for="contrasena-quitar">Su contraseña actual</label>
                    <input type="password" id="contrasena-quitar" name="contrasena" autocomplete="current-password" required>
                    <button type="submit" class="sialss-boton sialss-boton--ancho">Quitar PIN</button>
                </form>
            </section>
            <% } %>
        </div>
        <script>
            (function () {
                var n = <%=digitosPin%>;
                document.getElementById('forma-pin').addEventListener('submit', function (ev) {
                    var pin = document.getElementById('pin'), pin2 = document.getElementById('pin2');
                    var pass = document.getElementById('contrasena'), error = document.getElementById('pin-error');
                    var msg = null, foco = null;
                    if (!new RegExp('^[0-9]{' + n + '}$').test(pin.value)) { msg = 'El PIN debe tener exactamente ' + n + ' números.'; foco = pin; }
                    else if (pin.value !== pin2.value) { msg = 'Los dos PIN no son iguales.'; foco = pin2; }
                    else if (!pass.value) { msg = 'Escriba su contraseña actual.'; foco = pass; }
                    if (msg) { ev.preventDefault(); error.textContent = msg; error.hidden = false; foco.focus(); }
                });
            })();
        </script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
