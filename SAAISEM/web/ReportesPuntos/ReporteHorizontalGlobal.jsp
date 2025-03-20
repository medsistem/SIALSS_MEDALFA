<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>

<%@page import="conn.ConectionDB"%>
<%@page import="javax.print.attribute.standard.Copies"%>
<%@page import="javax.print.attribute.standard.MediaSizeName"%>
<%@page import="javax.print.attribute.standard.MediaSize"%>
<%@page import="javax.print.attribute.standard.MediaPrintableArea"%>
<%@page import="javax.print.attribute.PrintRequestAttributeSet"%>
<%@page import="javax.print.attribute.HashPrintRequestAttributeSet"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="net.sf.jasperreports.view.JasperViewer"%>
<%@page import="net.sf.jasperreports.engine.util.JRLoader"%>
<%@page import="java.net.URL"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.io.*"%> 
<%@page import="java.util.HashMap"%> 
<%@page import="java.util.Map"%> 
<%@page import="net.sf.jasperreports.engine.*"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%> 
<%@page import="java.sql.Statement"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%

    String F_Title = "", F_Surti = "", F_Cober = "", F_Sumi = "", F_FecIni = "", F_FecFin = "", F_SecIni = "";
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_User = "";
    Statement smtfolio = null;
    ResultSet folio = null;
    Statement smtfolio2 = null;
    try {
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("Use");
        FolCon = request.getParameter("FolCon");
    } catch (Exception e) {
    }

    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
%>
<html>
    <%
        ConectionDB conn = new ConectionDB();
        
           File reportfile = new File(application.getRealPath("/ReportesPuntos/RepHorizontalglobal.jasper"));
            Map parameter = new HashMap();

            parameter.put("FolCon", FolCon);
            //parameter.put("F_Imagen", F_Imagen);
            System.out.println("Folio Horizontal2-->" + FolCon);
            
            byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn.getConn());
            
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);

            outputStream.flush();
            outputStream.close();
            
        conn.cierraConexion();
    %>
    <head>
        
    </head> 
</html>
