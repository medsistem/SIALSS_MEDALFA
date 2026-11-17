<%-- Carga de caducos devueltos por las unidades a CADUCADOSFARMACIA (reemplaza cargacaducos.jsp). Servlet /CargaArchivoCaducos (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoCaducos")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoCaducos";
    String caTitulo = "Carga de caducos";
    String caDescripcion = "Sube el Excel con los productos caducados que devuelven las unidades (unidad, clave, lote, caducidad y cantidad). Entran a la ubicación CADUCADOSFARMACIA con un documento por unidad.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y el catálogo de unidades.";
    String[] caColumnas = {"Renglón", "Unidad", "Nombre de la unidad", "Clave", "Descripción", "Lote", "Caducidad", "Cantidad"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de caducos</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>