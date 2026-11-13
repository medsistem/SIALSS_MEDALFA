<%-- Menús por perfil y usuario: qué opciones del menú ve cada perfil y excepciones por usuario (permitir o bloquear).
     Datos: servlet /MenuRoles (mx.medalfa.sialss.menu.MenuRolesServlet); tablas tb_menu_*. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    if (sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.menu.MenuRolesServlet.autorizado(sesion)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String usua = (String) sesion.getAttribute("nombre");
    String seccionUsuarios = "menus";
    String tokenMenu = mx.medalfa.sialss.menu.MenuRolesServlet.token(sesion);
    mx.medalfa.sialss.seguridad.Acceso accesoActual = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Menús por perfil y usuario</title>
    </head>
    <body>
        <%@include file="jspf/menuPrincipal.jspf"%>
        <div class="container sialss-mr">
            <%@include file="jspf/usuariosSecciones.jspf"%>
            <h1>Menús por perfil y usuario</h1>
            <p class="sialss-secundario">Elija qué opciones del menú ve cada perfil. Para una persona en particular puede
                permitirle una opción que su perfil no tiene o bloquearle una que sí tiene; una opción bloqueada
                tampoco se puede abrir escribiendo su dirección.</p>

            <div class="sialss-mr__aviso" id="mr-sin-tablas" role="note" hidden>
                <strong>Todavía no se pueden guardar cambios.</strong> El menú se está tomando del archivo del sistema porque
                faltan las tablas de menú en la base. Pida al administrador de la base de datos que ejecute
                <code>despliegue/menu_roles_tablas.sql</code>; después de eso los cambios se guardan aquí.
            </div>

            <div class="sialss-mr__barra">
                <div class="sialss-mr__campo">
                    <label for="mr-acceso">Acceso</label>
                    <select id="mr-acceso">
                        <option value="ALMACEN"<%= accesoActual == mx.medalfa.sialss.seguridad.Acceso.ALMACEN ? " selected" : "" %>>Almacén</option>
                        <option value="COMPRAS"<%= accesoActual == mx.medalfa.sialss.seguridad.Acceso.COMPRAS ? " selected" : "" %>>Compras y Medalfa</option>
                        <option value="AUDITORIA"<%= accesoActual == mx.medalfa.sialss.seguridad.Acceso.AUDITORIA ? " selected" : "" %>>Auditoría</option>
                    </select>
                </div>
            </div>

            <div class="sialss-pestanas" role="tablist" aria-label="Asignar menú">
                <button type="button" role="tab" id="tab-perfil" aria-controls="panel-perfil" aria-selected="true">Por perfil</button>
                <button type="button" role="tab" id="tab-usuario" aria-controls="panel-usuario" aria-selected="false" tabindex="-1">Por usuario</button>
                <button type="button" role="tab" id="tab-cambios" aria-controls="panel-cambios" aria-selected="false" tabindex="-1">Historial de cambios</button>
            </div>

            <p class="sialss-mr__estado" id="mr-estado" role="status" aria-live="polite"></p>

            <section id="panel-perfil" role="tabpanel" aria-labelledby="tab-perfil">
                <div class="sialss-mr__barra">
                    <div class="sialss-mr__campo">
                        <label for="mr-perfil">Perfil</label>
                        <select id="mr-perfil"></select>
                    </div>
                    <span class="sialss-mr__resumen" id="mr-perfil-resumen"></span>
                </div>
                <div class="sialss-mr__acciones">
                    <button type="button" class="sialss-boton" id="mr-perfil-todo">Marcar todas</button>
                    <button type="button" class="sialss-boton" id="mr-perfil-nada">Quitar todas</button>
                    <button type="button" class="sialss-boton" id="mr-perfil-deshacer">Deshacer cambios</button>
                    <button type="button" class="sialss-boton sialss-boton--primario" id="mr-perfil-guardar">Guardar menú del perfil</button>
                </div>
                <div class="sialss-mr__modulos" id="mr-perfil-modulos"></div>
            </section>

            <section id="panel-usuario" role="tabpanel" aria-labelledby="tab-usuario" hidden>
                <div class="sialss-mr__barra">
                    <div class="sialss-mr__campo">
                        <label for="mr-buscar">Buscar usuario</label>
                        <input type="search" id="mr-buscar" placeholder="Usuario o nombre" autocomplete="off">
                    </div>
                    <div class="sialss-mr__campo">
                        <label for="mr-usuario">Usuario</label>
                        <select id="mr-usuario"></select>
                    </div>
                </div>
                <div id="mr-usuario-detalle" hidden>
                    <p class="sialss-mr__usuario" id="mr-usuario-info"></p>
                    <div class="sialss-mr__acciones">
                        <button type="button" class="sialss-boton" id="mr-usuario-limpiar">Todo según su perfil</button>
                        <button type="button" class="sialss-boton" id="mr-usuario-deshacer">Deshacer cambios</button>
                        <button type="button" class="sialss-boton sialss-boton--primario" id="mr-usuario-guardar">Guardar permisos del usuario</button>
                        <span class="sialss-mr__resumen" id="mr-usuario-resumen"></span>
                    </div>
                    <table class="sialss-mr__tabla">
                        <caption class="sr-only">Opciones del menú y su ajuste para el usuario</caption>
                        <thead>
                            <tr><th scope="col">Opción</th><th scope="col">Su perfil</th><th scope="col">Ajuste para este usuario</th><th scope="col">Resultado</th></tr>
                        </thead>
                        <tbody id="mr-usuario-opciones"></tbody>
                    </table>
                </div>
                <p class="sialss-mr__vacio" id="mr-usuario-vacio">Elija un usuario para ver y ajustar sus opciones.</p>
            </section>

            <section id="panel-cambios" role="tabpanel" aria-labelledby="tab-cambios" hidden>
                <table class="sialss-mr__tabla">
                    <caption class="sr-only">Últimos 30 cambios del menú</caption>
                    <thead><tr><th scope="col">Fecha</th><th scope="col">Quién</th><th scope="col">Acceso</th><th scope="col">Cambio a</th><th scope="col">Detalle</th></tr></thead>
                    <tbody id="mr-cambios"></tbody>
                </table>
            </section>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-menu-roles.js?v=2.0.5" defer
                data-ctx="<%=request.getContextPath()%>" data-token="<%=tokenMenu%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
