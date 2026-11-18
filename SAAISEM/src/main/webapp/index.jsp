<%-- Inicio de SIALSS: elige el acceso (Almacén, Compras o Auditoría). Mismo diseño que el login (jspf/loginSialss.jspf):
     fondo animado, colores de Medalfa y css/sialss-app.css. Antes era un carrusel de Bootstrap 4 desde CDN. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
    String[][] accesos = {
        {"Almacén", "indexalmacen.jsp", "imagenes/alm6.jpg",
            "Entradas, inventario, remisiones, cargas de archivos, reportes y catálogos del almacén."},
        {"Compras", "indexCompras.jsp", "imagenes/alm7.jpg",
            "Órdenes de compra, proveedores, puntos de entrega y seguimiento de lo recibido."},
        {"Auditoría", "indexAuditoria.jsp", "",
            "Consulta de órdenes de compra, existencias y reportes para revisión."}};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS · Inicio</title>
        <link href="<%=ctx%>/css/sialss-app.css?v=2.0.21" rel="stylesheet">
    </head>
    <body class="sialss-login-pagina sialss-inicio-pagina">
        <div class="sialss-login-fondo" aria-hidden="true">
            <img src="<%=ctx%>/imagenes/login-fondo-cadena.gif" alt="" width="1144" height="748">
        </div>
        <main class="sialss-inicio" aria-labelledby="titulo-inicio">
            <header class="sialss-inicio__encabezado">
                <img src="<%=ctx%>/imagenes/LogoMedalfa.png" alt="Medalfa" width="175" height="34">
                <div>
                    <p class="sialss-login__sistema">SIALSS</p>
                    <p class="sialss-login__lema">Sistema Integral de Administración y Logística para Servicios de Salud</p>
                </div>
            </header>
            <h1 id="titulo-inicio">¿A qué módulo quieres entrar?</h1>

            <ul class="sialss-inicio__accesos">
                <% for (String[] a : accesos) { %>
                <li>
                    <a class="sialss-inicio__acceso" href="<%=ctx%>/<%=a[1]%>">
                        <% if (!a[2].isEmpty()) { %>
                        <img class="sialss-inicio__foto" src="<%=ctx%>/<%=a[2]%>" alt="" width="400" height="225" loading="lazy">
                        <% } else { %>
                        <span class="sialss-inicio__foto sialss-inicio__foto--icono" aria-hidden="true">
                            <svg viewBox="0 0 48 48" width="56" height="56" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="10" y="7" width="24" height="32" rx="3"/><path d="M16 16h12M16 22h12M16 28h6"/><circle cx="33" cy="33" r="7"/><path d="m38 38 5 5"/>
                            </svg>
                        </span>
                        <% } %>
                        <span class="sialss-inicio__texto">
                            <span class="sialss-inicio__nombre">SIALSS <%=a[0]%></span>
                            <span class="sialss-inicio__desc"><%=a[3]%></span>
                            <span class="sialss-inicio__entrar">Entrar <span aria-hidden="true">→</span></span>
                        </span>
                    </a>
                </li>
                <% } %>
            </ul>

            <p class="sialss-aviso-legal">Uso exclusivo del personal autorizado. Cada acceso queda registrado.</p>
            <div class="sialss-login__pie">
                <span>Versión 2.0.0 · Java 17 · Tomcat 11</span>
                <span>Medalfa · Desarrollo de Aplicaciones</span>
            </div>
        </main>
    </body>
</html>
