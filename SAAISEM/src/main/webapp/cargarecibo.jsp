<%-- Pantalla anterior de carga: se reemplazó por CargaArchivoOC.jsp (menú CARGA DE ARCHIVOS, mismo modelo que la carga de inventario:
     revisar todo el archivo y confirmar con contraseña). La versión anterior está en el historial de git. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% response.sendRedirect(request.getContextPath() + "/CargaArchivoOC.jsp"); %>
