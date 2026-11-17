<%-- Carga de inventario desde Excel directo a tb_lote y tb_movinv (11 ALTA DE INVENTARIO) con el usuario de la
     sesión; se confirma con su contraseña. Servlet /CargaInventarioExcel (mx.medalfa.sialss.inventario). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.inventario.CargaInventarioServlet.autorizado(sesion)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String usuarioCarga = String.valueOf(sesion.getAttribute(mx.medalfa.sialss.seguridad.PoliticaSesion.USUARIO))
            .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de inventario</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-mr sialss-captura">
            <h1>Carga de inventario</h1>
            <p class="sialss-secundario">Sube un Excel con los lotes y sus existencias. Primero se revisa todo el archivo sin
                guardar nada; si no hay errores, confirmas con tu contraseña y se registra en el inventario como
                "ALTA DE INVENTARIO" a nombre de <strong><%=usuarioCarga%></strong>.</p>

            <fieldset>
                <legend>1. Archivo</legend>
                <div class="sialss-filtros">
                    <div class="sialss-filtros__campo">
                        <label for="ci-archivo">Excel (.xlsx o .xls)</label>
                        <input type="file" class="form-control" id="ci-archivo" accept=".xlsx,.xls,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
                    </div>
                    <div class="sialss-filtros__campo">
                        <span class="sialss-filtros__ayuda">La plantilla trae las columnas, un ejemplo y los catálogos de proyecto, origen, proveedor y marca.</span>
                        <a class="sialss-boton" href="<%=request.getContextPath()%>/CargaInventarioExcel?accion=plantilla">Descargar plantilla</a>
                    </div>
                </div>
            </fieldset>

            <div class="sialss-captura__acciones">
                <button type="button" class="sialss-boton sialss-boton--primario" id="ci-revisar">Revisar archivo</button>
                <button type="button" class="sialss-boton" id="ci-cancelar" hidden>Cancelar carga</button>
            </div>
            <p class="sialss-mr__estado" id="ci-estado" role="status" aria-live="polite"></p>
            <ul class="sialss-captura__errores" id="ci-errores" hidden></ul>

            <form class="sialss-captura__confirmar" id="ci-confirmar" hidden>
                <div class="sialss-filtros__campo">
                    <label for="ci-contrasena">2. Para confirmar, escribe tu contraseña</label>
                    <input type="password" class="form-control" id="ci-contrasena" autocomplete="current-password">
                </div>
                <button type="submit" class="sialss-boton sialss-boton--primario" id="ci-aplicar">Confirmar carga</button>
                <span id="ci-resumen"></span>
            </form>

            <h2 class="sialss-etq__subtitulo">Vista previa</h2>
            <table class="sialss-mr__tabla">
                <caption class="sr-only">Primeros 200 renglones del archivo</caption>
                <thead><tr><th scope="col">Renglón</th><th scope="col">Clave</th><th scope="col">Descripción</th><th scope="col">Lote</th>
                        <th scope="col">Caducidad</th><th scope="col">Cantidad</th><th scope="col">Ubicación</th><th scope="col">Proyecto</th>
                        <th scope="col">Tarima</th></tr></thead>
                <tbody id="ci-muestra"><tr><td colspan="9" class="sialss-mr__vacio">Sube un archivo y pulsa "Revisar archivo".</td></tr></tbody>
            </table>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-carga-inventario.js?v=2" defer data-ctx="<%=request.getContextPath()%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
