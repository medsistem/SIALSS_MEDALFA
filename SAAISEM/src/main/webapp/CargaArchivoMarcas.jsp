<%-- Carga de marcas a tb_marca (skill carga-catalogos-sialss). Servlet /CargaArchivoMarcas (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoMarcas")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoMarcas";
    String caTitulo = "Carga de marcas";
    String caDescripcion = "Sube el Excel con las marcas nuevas (una por renglón; se guardan en mayúsculas). No se duplican: una marca igual o parecida a una ya registrada, o repetida en el archivo, se omite y la revisión dice a cuál se parece.";
    String caPlantilla = "La plantilla trae la columna, un ejemplo y la lista de marcas ya registradas.";
    String[] caColumnas = {"Renglón", "Marca"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de marcas</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>