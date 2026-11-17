<%-- Perfiles de usuario (tb_tipusuario): alta y suspensión. Solo SUPERUSUARIO (perfil 100).
     Datos: servlet /PerfilesUsuario (mx.medalfa.sialss.usuarios.PerfilesServlet). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    if (sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.usuarios.PerfilesServlet.autorizado(sesion)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String usua = (String) sesion.getAttribute("nombre");
    String seccionUsuarios = "perfiles";
    String tokenPerfiles = mx.medalfa.sialss.usuarios.PerfilesServlet.token(sesion);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Perfiles de usuario</title>
    </head>
    <body>
        <%@include file="jspf/menuPrincipal.jspf"%>
        <div class="container sialss-mr sialss-captura">
            <%@include file="jspf/usuariosSecciones.jspf"%>
            <h1>Perfiles de usuario</h1>
            <p class="sialss-secundario">Da de alta perfiles nuevos o suspende los que ya no se usan. Nadie con un perfil
                suspendido puede iniciar sesión y ese perfil deja de ofrecerse al crear usuarios. Las opciones de menú de
                cada perfil se asignan en "Menús por perfil y usuario".</p>

            <div class="sialss-mr__aviso" id="pf-sin-estatus" role="note" hidden>
                <strong>Todavía no se puede suspender.</strong> Falta aplicar en la base el script
                <code>06_perfiles_superusuario.sql</code> (estatus de los perfiles y perfil SUPERUSUARIO).
            </div>

            <form id="pf-alta">
                <fieldset>
                    <legend>Nuevo perfil</legend>
                    <div class="sialss-filtros">
                        <div class="sialss-filtros__campo">
                            <label for="pf-nombre">Nombre del perfil</label>
                            <input type="text" class="form-control" id="pf-nombre" maxlength="30" required autocomplete="off"
                                   placeholder="Por ejemplo: SUPERVISOR DE ALMACÉN">
                        </div>
                    </div>
                </fieldset>
                <div class="sialss-captura__acciones">
                    <button type="submit" class="sialss-boton sialss-boton--primario" id="pf-guardar">Dar de alta</button>
                </div>
            </form>
            <p class="sialss-mr__estado" id="pf-estado" role="status" aria-live="polite"></p>

            <h2 class="sialss-etq__subtitulo">Perfiles</h2>
            <table class="sialss-mr__tabla">
                <caption class="sr-only">Perfiles de usuario con su estatus</caption>
                <thead><tr><th scope="col">Número</th><th scope="col">Perfil</th><th scope="col">Usuarios activos</th>
                        <th scope="col">Estatus</th><th scope="col">Acción</th></tr></thead>
                <tbody id="pf-lista"><tr><td colspan="5" class="sialss-mr__vacio">Cargando…</td></tr></tbody>
            </table>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-perfiles.js?v=1" defer
                data-ctx="<%=request.getContextPath()%>" data-token="<%=tokenPerfiles%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
