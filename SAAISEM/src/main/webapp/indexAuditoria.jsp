<%-- Inicio de sesión de SIALSS Auditoría (servlet LoginAuditoria, tabla tb_usuarioauditoria). El diseño está en jspf/loginSialss.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String loginModulo = "Auditoría";
    String loginAccion = "LoginAuditoria";
    String loginCampo = "usuario";
%>
<%@include file="jspf/loginSialss.jspf" %>
