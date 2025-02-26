<%-- 
    Document   : Reporte1
    Created on : 19/12/2013, 10:11:35 AM
    Author     : CEDIS TOLUCA3
--%>
<%@page import="java.io.*"%> 
<%@page import="java.sql.Connection"%> 
<%@page import="java.sql.DriverManager"%> 
<%@page import="java.util.HashMap"%> 
<%@page import="java.util.Map"%> 
<%@page import="net.sf.jasperreports.engine.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>

<%
HttpSession Session = request.getSession();
String clave="",lote="",cadu="",piezas="";
clave = (String)Session.getAttribute("clave");
lote = (String)Session.getAttribute("lote");
cadu = (String)Session.getAttribute("cadu");
piezas = (String)Session.getAttribute("piezas");
%>

<!DOCTYPE html>
<html>
    <%

    Connection conn; 
    Class.forName("org.mariadb.jdbc.Driver"); 
    conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gnklmex_consolidada2","root","eve9397");
    
    /*Establecemos la ruta del reporte*/ 
    File reportfile = new File(application.getRealPath("/Ubicaciones/consult_resto.jasper")); 
    /* No enviamos parámetros porque nuestro reporte no los necesita */ 
    Map parameter= new HashMap(); 
    //parameter.put("unidad", "1");
    parameter.put("clave", clave);
    parameter.put("lote", lote);
    parameter.put("cadu", cadu);
    parameter.put("piezas", piezas);
    //System.out.println("Uni:"+Unidad+" Fol:"+Folio+" Caj:"+Cajas);
    /*Enviamos la ruta del reporte, los parámetros y la conexión*/ 
    byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter,conn);
    
    /*Indicamos que la respuesta va a ser en formato PDF*/ 
    response.setContentType("application/pdf"); response.setContentLength(bytes.length); ServletOutputStream outputStream= response.getOutputStream(); outputStream.write(bytes,0,bytes.length); 
    /*Limpiamos y cerramos flujos de salida*/ 
    outputStream.flush(); outputStream.close();
    conn.close();
%>
</html>
