<%-- Carga de requerimiento de unidades a tb_unireq (reemplaza requerimiento.jsp). Servlet /CargaArchivoRequerimiento (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoRequerimiento")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoRequerimiento";
    String caTitulo = "Carga de requerimiento";
    String caDescripcion = "Sube el Excel con el requerimiento de las unidades (unidad, clave, fecha de entrega y cantidad). Los requerimientos pendientes anteriores de cada unidad del archivo se cierran y queda el nuevo.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y el catálogo de unidades con su nivel.";
    String[] caColumnas = {"Renglón", "Unidad", "Nombre de la unidad", "Clave", "Descripción", "Fecha de entrega", "Cantidad"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de requerimiento</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>