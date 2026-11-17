<%-- Carga de órdenes de compra a tb_pedido_sialss (reemplaza cargarecibo.jsp). Servlet /CargaArchivoOC (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoOC")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoOC";
    String caTitulo = "Carga de órdenes de compra";
    String caDescripcion = "Sube el Excel con las órdenes de compra (orden, proveedor, clave, cantidad, proyecto y fuente de financiamiento). Si una orden ya estaba cargada para ese proveedor y proyecto, se reemplaza.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y los catálogos de proveedores y proyectos.";
    String[] caColumnas = {"Renglón", "Orden de compra", "Proveedor", "Clave", "Descripción", "Cantidad", "Proyecto", "Fuente de financiamiento"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de órdenes de compra</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>