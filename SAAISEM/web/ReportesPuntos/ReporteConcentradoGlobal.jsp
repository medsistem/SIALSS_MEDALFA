<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>

<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="javax.print.attribute.standard.Copies"%>
<%@page import="javax.print.attribute.standard.MediaSizeName"%>
<%@page import="javax.print.attribute.standard.MediaSize"%>
<%@page import="javax.print.attribute.standard.MediaPrintableArea"%>
<%@page import="javax.print.attribute.PrintRequestAttributeSet"%>
<%@page import="javax.print.attribute.HashPrintRequestAttributeSet"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.*"%>
<%@page import="net.sf.jasperreports.engine.data.JRXlsDataSource"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.util.*" %>
<%@page import="java.io.*"%> 
<%@page import="java.util.HashMap"%> 
<%@page import="java.util.Map"%> 
<%@page import="net.sf.jasperreports.engine.*"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%> 
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%

    String F_Title = "", F_Surti = "", F_Cober = "", F_Sumi = "", F_FecIni = "", F_FecFin = "", F_SecIni = "";
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_User = "";
    Statement smtfolio = null;
    Statement smtfolio2 = null;
    ResultSet folio = null;
    try {
        FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
    } catch (Exception e) {
    };
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    out.println(F_Imagen);
%>
<html>
    <%
        Connection conn;
        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");
        //conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");
       
            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepConcentradoglobal.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            //parameter.put("F_Imagen", F_Imagen);
            out.println("Concentrado-->"+FolCon);
            
            byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn);
            
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);

            outputStream.flush();
            outputStream.close();
            
        conn.close();
    %>
    <head>
        <script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 1000);

        </script>
    </head> 
</html>
