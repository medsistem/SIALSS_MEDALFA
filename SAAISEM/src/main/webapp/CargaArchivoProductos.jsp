<%-- Carga de productos a tb_medica (skill carga-catalogos-sialss). Servlet /CargaArchivoProductos (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoProductos")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoProductos";
    String caTitulo = "Carga de productos";
    String caDescripcion = "Sube el Excel con los productos nuevos (medicamento, material de curación y demás insumos): clave, descripción, tipo de insumo, origen, precio y proyectos (obligatorios), y opcionalmente clave SS, presentación, origen NIM, grupo terapéutico, cantidad máxima, catálogo, niveles de unidad y estatus, como en Alta de Claves. No se duplican: una clave ya registrada o una descripción igual o parecida a la de otro producto (con los mismos números), o repetida en el archivo, se omite.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y los catálogos de tipo de insumo, origen, proyectos y grupo terapéutico.";
    String[] caColumnas = {"Renglón", "Clave", "Descripción", "Tipo", "Origen", "Precio", "Proyectos", "Niveles"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de productos</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>