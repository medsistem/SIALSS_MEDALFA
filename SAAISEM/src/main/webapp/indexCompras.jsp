<%-- Inicio de sesión de SIALSS Compras (servlet loginISEM, tabla tb_usuariocompra). El diseño está en jspf/loginSialss.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String loginModulo = "Compras";
    String loginAccion = "loginISEM";
    String loginCampo = "nombre";
%>
<%@include file="jspf/loginSialss.jspf" %>
