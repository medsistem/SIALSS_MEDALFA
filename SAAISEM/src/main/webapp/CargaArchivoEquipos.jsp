<%-- Carga de equipos por Excel: lo mismo que Entrada de Equipos (tb_lote, tb_movinv 49, tb_compra, tb_equipo), muchos a la vez. Servlet /CargaArchivoEquipos (mx.medalfa.sialss.cargas.CargaArchivoServlet); pantalla común jspf/cargaArchivo.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    if (!mx.medalfa.sialss.cargas.CargaArchivoServlet.autorizado(sesion, "/CargaArchivoEquipos")) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/sinPermiso.jsp").forward(request, response);
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
    String caPagina = "CargaArchivoEquipos";
    String caTitulo = "Carga de equipos";
    String caDescripcion = "Sube el Excel con los equipos que entran, igual que en Entrada de Equipos: clave, marca, proyecto, origen, proveedor, ubicación de ingreso, serie o lote, empaque (tarimas, cajas y piezas) y sus datos de compra y garantía. Los renglones con la misma orden de compra y remisión comparten documento. Los equipos que ya están cargados o que se repiten en el archivo se omiten: solo se suben los nuevos.";
    String caPlantilla = "La plantilla trae las columnas, dos ejemplos (una pieza con serie y un lote con empaque) y los catálogos de marca, proyecto, origen, proveedor, ubicación, modelo y condición.";
    String[] caColumnas = {"Renglón", "Clave", "Descripción", "Serie o lote", "Piezas", "Marca", "Ubicación", "Proyecto", "OC / remisión"};
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Carga de equipos</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <%@include file="jspf/cargaArchivo.jspf"%>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>