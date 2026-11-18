<%-- Carga de unidades a tb_uniatn (skill carga-catalogos-sialss). Servlet /CargaArchivoUnidades (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoUnidades")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoUnidades";
    String caTitulo = "Carga de unidades";
    String caDescripcion = "Sube el Excel con las unidades de atención nuevas: clave, nombre, tipo de unidad, municipio y proyecto (obligatorios), dirección, CLUES, nivel y ruta. La jurisdicción sale del municipio y el nivel, del tipo de unidad. No se duplican: una unidad con la misma clave, la misma CLUES o el nombre igual o parecido a una registrada, o repetida en el archivo, se omite.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y los catálogos de tipo de unidad, municipio, proyecto y unidades ya registradas.";
    String[] caColumnas = {"Renglón", "Clave", "Nombre", "Tipo", "Municipio", "Proyecto", "CLUES", "Nivel"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de unidades</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>