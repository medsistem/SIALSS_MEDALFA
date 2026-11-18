<%-- Inicio de sesión de SIALSS Auditoría (servlet LoginAuditoria, tabla tb_usuario, perfiles 28 AUDITORIA, 1 y 100). El diseño está en jspf/loginSialss.jspf. --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String loginModulo = "Auditoría";
    String loginAccion = "LoginAuditoria";
    String loginCampo = "usuario";
%>
<%@include file="jspf/loginSialss.jspf" %>
