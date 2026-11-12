<%-- Inicio de sesión de SIALSS Almacén (servlet /login, tabla tb_usuario). El diseño está en jspf/loginSialss.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String loginModulo = "Almacén";
    String loginAccion = "login";
    String loginCampo = "nombre";
%>
<%@include file="jspf/loginSialss.jspf" %>
