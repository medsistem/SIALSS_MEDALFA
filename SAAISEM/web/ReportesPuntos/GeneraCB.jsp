<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>
<%@page import="java.sql.Statement"%>
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

    String Folio="",CB="";
    try {
        Folio = request.getParameter("CB");       
    } catch (Exception e) {
    }    
   
%>
<html>
    <%
        Connection conn;
        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");
        //conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");    
        File reportfile = new File(application.getRealPath("/ReportesPuntos/ClaveCB.jasper"));
        Statement smtfolio = null;
        
        smtfolio = conn.createStatement();
        
        smtfolio.execute("DELETE FROM tb_cbmedica WHERE F_Folio='" + Folio + "'");
        
        ResultSet Cb = smtfolio.executeQuery("SELECT F_Cb FROM tb_lote WHERE F_FOLLOT='"+Folio+"' group by F_FOLLOT");
        if(Cb.next()){
            CB = Cb.getString(1);
        }
        
        
        for(int x = 0; x < 76; x++){
        smtfolio.execute("insert into tb_cbmedica values(0,'"+Folio+"','"+CB+"')");    
        }
        
        Map parameter = new HashMap();

        parameter.put("Folio", Folio);
        

        byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn);

        response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);

        outputStream.flush();
        outputStream.close();

        conn.close();
    %>
</html>
