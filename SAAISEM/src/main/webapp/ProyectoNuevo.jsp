<%-- Alta de proyecto: tb_proyectos + columna en tb_medica + índice de facturación + ubicaciones de ingreso y salida
     + tb_ubicanueva + tb_ubicafact + tb_tipocause. Servlet /ProyectoNuevo (mx.medalfa.sialss.proyectos). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.proyectos.ProyectoNuevoServlet.autorizado(sesion)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String tokenProyecto = mx.medalfa.sialss.proyectos.ProyectoNuevoServlet.token(sesion);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Alta de proyecto</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-mr sialss-proy">
            <h1>Alta de proyecto</h1>
            <p class="sialss-secundario">Captura los datos del proyecto. Antes de guardar se muestra todo lo que se va a crear:
                nomenclatura, columna del catálogo de insumos, índice de facturación y ubicaciones de ingreso y salida.</p>

            <p class="sialss-mr__estado" id="pr-estado" role="status" aria-live="polite"></p>

            <div class="sialss-proy__columnas">
                <form id="pr-forma" class="sialss-pin__tarjeta" novalidate>
                    <h2>Datos del proyecto</h2>
                    <label for="pr-nombre">Nombre del proyecto</label>
                    <input type="text" id="pr-nombre" name="nombre" maxlength="30" required autocomplete="off">
                    <p class="sialss-pin__ayuda">Hasta 30 caracteres (así se ve en reportes y listas).</p>

                    <label for="pr-abreviatura">Abreviatura</label>
                    <input type="text" id="pr-abreviatura" name="abreviatura" maxlength="6" required autocomplete="off" class="sialss-proy__mayus">
                    <p class="sialss-pin__ayuda">De 2 a 6 letras o números. Forma la nomenclatura (abreviatura-año-) y los nombres de las ubicaciones.</p>

                    <label for="pr-direccion">Dirección</label>
                    <textarea id="pr-direccion" name="direccion" maxlength="255" rows="2" required></textarea>

                    <label for="pr-rfc">RFC</label>
                    <input type="text" id="pr-rfc" name="rfc" maxlength="13" required autocomplete="off" class="sialss-proy__mayus">

                    <label for="pr-encabezado">Encabezado</label>
                    <textarea id="pr-encabezado" name="encabezado" maxlength="150" rows="2" required></textarea>
                    <p class="sialss-pin__ayuda">Texto que aparece en el encabezado de los documentos del proyecto (hasta 150 caracteres).</p>

                    <label for="pr-contrato">Contrato <span class="sialss-proy__opcional">(opcional)</span></label>
                    <input type="text" id="pr-contrato" name="contrato" maxlength="255" autocomplete="off" placeholder="N/A">

                    <label for="pr-qfb1">Responsable sanitario de medicamento (2504)</label>
                    <input type="text" id="pr-qfb1" name="qfbMedicamento" maxlength="200" required>
                    <label for="pr-qfb2">Responsable sanitario de dispositivos médicos (2505)</label>
                    <input type="text" id="pr-qfb2" name="qfbDispositivos" maxlength="200" required>
                    <p class="sialss-pin__ayuda">Se copian del proyecto 1; cámbielos si este proyecto tiene otros responsables.</p>

                    <button type="submit" class="sialss-boton sialss-boton--primario sialss-boton--ancho" id="pr-guardar" disabled>Dar de alta el proyecto</button>
                </form>

                <section class="sialss-pin__tarjeta sialss-proy__previa" aria-labelledby="titulo-previa">
                    <h2 id="titulo-previa">Lo que se va a crear</h2>
                    <div id="pr-previa"><p class="sialss-pin__ayuda">Escriba el nombre y la abreviatura.</p></div>
                </section>
            </div>

            <h2 class="sialss-etq__subtitulo">Proyectos existentes</h2>
            <table class="sialss-mr__tabla">
                <caption class="sr-only">Proyectos dados de alta</caption>
                <thead><tr><th scope="col">Id</th><th scope="col">Proyecto</th><th scope="col">Nomenclatura</th><th scope="col">Columna en tb_medica</th></tr></thead>
                <tbody id="pr-proyectos"></tbody>
            </table>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-proyecto-nuevo.js?v=2.0.7" defer
                data-ctx="<%=request.getContextPath()%>" data-token="<%=tokenProyecto%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
