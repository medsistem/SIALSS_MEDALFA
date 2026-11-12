<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>

<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="net.sf.jasperreports.engine.*" %> 
<%@ page import="java.util.*" %> 
<%@ page import="java.io.*" %> 
<%@ page import="java.sql.*" %> 
<% /*Parametros para realizar la conexión*/

    HttpSession sesion = request.getSession();
    ConectionDB con = new ConectionDB();
    String usua = "";
    
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    
    String fol_Mov = request.getParameter("fol_Mov");
    String fecha = request.getParameter("fecha");
    String concepto = request.getParameter("concepto");
    
    System.out.println("Parametros: "+fol_Mov+" / "+fecha+" / "+concepto);
    System.out.println("Fecha: "+fecha);
    
    File reportFile = new File(application.getRealPath("/reportes/ImprimeMovimiento.jasper"));
    
    Map parameters = new HashMap();
    parameters.put("Folfact", fol_Mov);
    parameters.put("fecha", fecha);
    parameters.put("concepto", concepto);
    
    byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, con.getConn());
    
    response.setContentType("application/pdf");
    response.setContentLength(bytes.length);
    ServletOutputStream ouputStream = response.getOutputStream();
    ouputStream.write(bytes, 0, bytes.length); 
    ouputStream.flush();
    ouputStream.close();
     con.cierraConexion();
%>
