<%-- Carga de proveedores a tb_proveedor (skill carga-catalogos-sialss). Servlet /CargaArchivoProveedores (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoProveedores")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoProveedores";
    String caTitulo = "Carga de proveedores";
    String caDescripcion = "Sube el Excel con los proveedores nuevos: nombre o razón social (obligatorio), RFC, dirección, colonia, código postal, teléfono, fax, correo, población y clave SAP. No se duplican: un proveedor con nombre igual o parecido (sin contar S.A. de C.V. y similares) o con el mismo RFC que uno registrado, o repetido en el archivo, se omite.";
    String caPlantilla = "La plantilla trae las columnas, un ejemplo y la lista de proveedores ya registrados con su RFC.";
    String[] caColumnas = {"Renglón", "Nombre", "RFC", "Dirección", "Teléfono", "Correo"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de proveedores</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>