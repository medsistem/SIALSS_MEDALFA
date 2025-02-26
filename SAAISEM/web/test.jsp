<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="net.sf.jasperreports.engine.*" %> 
<%@ page import="java.util.*" %> 
<%@ page import="java.io.*" %> 
<%@ page import="java.sql.*" %> 
<%
    try{
        File reportFile = new File(application.getRealPath("/reportes/test_jasper.jasper"));
        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), new HashMap(), new JREmptyDataSource());
        response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length);
        ouputStream.flush();
        ouputStream.close();
    }catch(Exception e){
        %> 
<p>Error:
    <%= e.getMessage() %>
</p>
<%
    }
%> 