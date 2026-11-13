<%-- Etiquetas de ubicación: poste 5×4 cm (varias por hoja carta o A4), barra 15×10 cm y QR 15×10 cm, en PDF.
     Servlet /EtiquetasUbicacion (mx.medalfa.sialss.etiquetas). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    int[] porHojaCarta = mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.cuadricula(mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Papel.CARTA);
    int[] porHojaA4 = mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.cuadricula(mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Papel.A4);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Etiquetas de ubicación</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-mr sialss-etq">
            <h1>Etiquetas de ubicación</h1>
            <p class="sialss-secundario">Elija el tipo de etiqueta y las ubicaciones. Se genera un PDF listo para imprimir
                al 100 % (sin "ajustar a la página"). El código lleva la clave de la ubicación, la misma que leen las
                terminales de mano.</p>

            <form id="forma-etq" action="<%=request.getContextPath()%>/EtiquetasUbicacion" method="post" target="_blank">
                <input type="hidden" name="accion" value="pdf">
                <input type="hidden" name="claves" id="etq-claves">

                <fieldset class="sialss-etq__tipos">
                    <legend>Tipo de etiqueta</legend>
                    <label class="sialss-etq__tipo">
                        <input type="radio" name="tipo" value="POSTE" checked>
                        <span class="sialss-etq__muestra sialss-etq__muestra--poste" aria-hidden="true"><i></i><b>2PS01-L1-F1</b></span>
                        <strong>Poste</strong>
                        <span>5 × 4 cm · código de barras y ubicación · varias por hoja</span>
                    </label>
                    <label class="sialss-etq__tipo">
                        <input type="radio" name="tipo" value="BARRA">
                        <span class="sialss-etq__muestra sialss-etq__muestra--barra" aria-hidden="true"><i></i><b>2PS01-L1-F1</b></span>
                        <strong>Barra</strong>
                        <span>15 × 10 cm · una por hoja del tamaño de la etiqueta</span>
                    </label>
                    <label class="sialss-etq__tipo">
                        <input type="radio" name="tipo" value="QR">
                        <span class="sialss-etq__muestra sialss-etq__muestra--qr" aria-hidden="true"><i></i><b>2PS01<br>L1<br>F1</b></span>
                        <strong>QR</strong>
                        <span>15 × 10 cm · una por hoja del tamaño de la etiqueta</span>
                    </label>
                </fieldset>

                <div class="sialss-mr__barra" id="etq-papel-campo">
                    <div class="sialss-mr__campo">
                        <label for="etq-papel">Hoja para etiquetas de poste</label>
                        <select id="etq-papel" name="papel">
                            <option value="CARTA">Carta · <%=porHojaCarta[0] * porHojaCarta[1]%> por hoja (<%=porHojaCarta[0]%> × <%=porHojaCarta[1]%>)</option>
                            <option value="A4">A4 · <%=porHojaA4[0] * porHojaA4[1]%> por hoja (<%=porHojaA4[0]%> × <%=porHojaA4[1]%>)</option>
                        </select>
                    </div>
                </div>
            </form>

            <h2 class="sialss-etq__subtitulo">Ubicaciones</h2>
            <div class="sialss-mr__barra">
                <div class="sialss-mr__campo">
                    <label for="etq-seccion">Sección</label>
                    <select id="etq-seccion"><option value="">Todas</option></select>
                </div>
                <div class="sialss-mr__campo">
                    <label for="etq-texto">Buscar clave o ubicación</label>
                    <input type="search" id="etq-texto" placeholder="Ejemplo: 2PS01-L1" autocomplete="off">
                </div>
                <button type="button" class="sialss-boton" id="etq-buscar">Buscar</button>
            </div>

            <p class="sialss-mr__estado" id="etq-estado" role="status" aria-live="polite"></p>

            <div class="sialss-mr__acciones">
                <button type="button" class="sialss-boton" id="etq-todas">Seleccionar todas</button>
                <button type="button" class="sialss-boton" id="etq-ninguna">Quitar selección</button>
                <button type="button" class="sialss-boton sialss-boton--primario" id="etq-generar" disabled>Generar PDF</button>
                <span class="sialss-mr__resumen" id="etq-resumen">0 seleccionadas</span>
            </div>

            <table class="sialss-mr__tabla">
                <caption class="sr-only">Ubicaciones encontradas</caption>
                <thead><tr><th scope="col" class="sialss-etq__col-sel"><span class="sr-only">Seleccionar</span></th><th scope="col">Ubicación</th><th scope="col">Clave (código)</th><th scope="col">Sección</th></tr></thead>
                <tbody id="etq-lista"><tr><td colspan="4" class="sialss-mr__vacio">Busque por sección o por texto.</td></tr></tbody>
            </table>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-etiquetas-ubicacion.js?v=2.0.6" defer
                data-ctx="<%=request.getContextPath()%>" data-maximo="<%=mx.medalfa.sialss.etiquetas.EtiquetasUbicacionServlet.MAXIMO%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
