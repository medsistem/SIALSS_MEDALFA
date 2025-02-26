<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>
<%@page import="java.sql.DriverManager"%>
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
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_Imagen = "";
    try {

        F_Title = request.getParameter("F_Title");
        F_Surti = request.getParameter("F_Surti");
        F_Cober = request.getParameter("F_Cober");
        F_Sumi = request.getParameter("F_Sumi");
        F_FecIni = request.getParameter("F_FecIni");
        F_FecFin = request.getParameter("F_FecFin");
        F_SecIni = request.getParameter("F_SecIni");
        F_SecFin = request.getParameter("F_SecFin");
        F_Cvepro = request.getParameter("F_Cvepro");
        F_DesRegion = request.getParameter("F_DesRegion");
        FolCon = request.getParameter("FolCon");
        String path = getServletContext().getRealPath("/");
        F_Imagen = path + "imagenes\\savi1.jpg";

    } catch (Exception e) {
    }
    out.println("Titulo= " + F_Title + " fi=" + F_FecIni + " fin=" + F_FecFin + " seci=" + F_SecIni
            + " secfin=" + F_SecFin + " ");

%>
<html>
    <%        Connection conn;
        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");

        File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVertical.jasper"));

        Map parameter = new HashMap();
        parameter.put("Title", F_Title);
        parameter.put("F_Surti", F_Surti);
        parameter.put("F_Cober", F_Cober);
        parameter.put("F_Sumi", F_Sumi);
        parameter.put("F_FecIni", F_FecIni);
        parameter.put("F_FecFin", F_FecFin);
        parameter.put("F_SecIni", F_SecIni);
        parameter.put("F_SecFin", F_SecFin);
        parameter.put("F_Cvepro", F_Cvepro);
        parameter.put("F_DesRegion", F_DesRegion);
        parameter.put("FolCon", FolCon);
        parameter.put("F_Imagen", F_Imagen);

        System.out.println("Folio-->" + F_Title);
    //System.out.println("Ruta-->"+this.getClass().getResourceAsStream(F_Imagen));

        byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn);

    //JasperPrint jasperPrint= JasperFillManager.fillReport(reportfile.getPath(),parameter,conn);
        //JasperPrintManager.printReport(jasperPrint,false);
        response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);

        outputStream.flush();
        outputStream.close();

        conn.close();
    %>
</html>
