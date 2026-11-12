<%--
    Inicio de sesión de SIALSS Almacén. Diseño de SALPE con los colores de Medalfa.
    Al mostrarse cierra la sesión anterior (sirve también como "Cerrar sesión").
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String mensajeLogin = (String) session.getAttribute("mensaje");
    session.invalidate();
    if (mensajeLogin != null) {
        mensajeLogin = mensajeLogin.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    String ctxLogin = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS · Iniciar sesión</title>
        <link href="<%=ctxLogin%>/css/sialss-app.css" rel="stylesheet">
    </head>
    <body class="sialss-login-pagina">
        <main class="sialss-login" aria-labelledby="titulo-login">
            <div class="sialss-login__titulo">
                <img src="<%=ctxLogin%>/imagenes/LogoMedalfa.png" alt="Medalfa" width="175" height="34">
                <div>
                    <p class="sialss-login__sistema">SIALSS</p>
                    <p class="sialss-login__lema">Administración y logística para servicios de salud · Almacén</p>
                </div>
            </div>
            <h1 id="titulo-login">Iniciar sesión</h1>
            <p class="sialss-secundario">Use el usuario que le asignó el departamento de sistemas.</p>

            <form id="forma-login" action="<%=ctxLogin%>/login" method="post" novalidate>
                <label for="nombre">Usuario</label>
                <input type="text" id="nombre" name="nombre" autocomplete="username" autocapitalize="none" spellcheck="false" required autofocus>

                <label for="pass">Contraseña</label>
                <div class="sialss-campo-contrasena">
                    <input type="password" id="pass" name="pass" autocomplete="current-password" required>
                    <button type="button" class="sialss-boton-texto" id="ver-contrasena" aria-pressed="false" aria-controls="pass">Mostrar</button>
                </div>

                <% if (mensajeLogin != null) { %>
                <p class="sialss-mensaje sialss-mensaje--error" role="alert"><%=mensajeLogin%></p>
                <% } %>
                <p id="login-error" class="sialss-mensaje sialss-mensaje--error" role="alert" hidden></p>

                <button type="submit" class="sialss-boton sialss-boton--primario sialss-boton--ancho">Entrar</button>
            </form>

            <p class="sialss-aviso-legal">Uso exclusivo del personal autorizado. Cada acceso queda registrado.
                Tras cuatro intentos fallidos el usuario se suspende.</p>

            <div class="sialss-login__pie">
                <span>Versión 2.0.0 · Java 17 · Tomcat 11</span>
                <a href="<%=ctxLogin%>/index.jsp">Volver al inicio</a>
            </div>
        </main>

        <script>
            (function () {
                var boton = document.getElementById('ver-contrasena');
                var campo = document.getElementById('pass');
                boton.addEventListener('click', function () {
                    var mostrar = campo.type === 'password';
                    campo.type = mostrar ? 'text' : 'password';
                    boton.textContent = mostrar ? 'Ocultar' : 'Mostrar';
                    boton.setAttribute('aria-pressed', mostrar ? 'true' : 'false');
                    campo.focus();
                });
                document.getElementById('forma-login').addEventListener('submit', function (ev) {
                    var error = document.getElementById('login-error');
                    var u = document.getElementById('nombre');
                    if (!u.value.trim() || !campo.value) {
                        ev.preventDefault();
                        error.textContent = !u.value.trim() ? 'Escriba su usuario.' : 'Escriba su contraseña.';
                        error.hidden = false;
                        (!u.value.trim() ? u : campo).focus();
                    }
                });
            })();
        </script>
    </body>
</html>
