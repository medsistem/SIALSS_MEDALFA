<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>
<%@page import="conn.ConectionDB"%>
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

    String F_Title = "", F_Surti = "", F_Cober = "", F_Sumi = "", F_FecIni = "", F_FecFin = "", F_SecIni = "";
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_User = "";
    Statement smtfolio = null;
    Statement smtfolio2 = null;
    ResultSet folio = null;
    try {
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
    } catch (Exception e) {
    };
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    out.println(F_Imagen);
%>
<html>
    <%
        ConectionDB conn = new ConectionDB();

        folio = conn.consulta("SELECT F_FactAgr FROM tb_imprepvalauto WHERE F_Usuario='" + F_User + "' AND F_Date=CURDATE() GROUP BY F_FactAgr");
        while (folio.next()) {
            FolCon = folio.getString(1);
            System.out.println("Folio Validacion-->" + FolCon);
            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepConcentradoauto.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            parameter.put("F_Imagen", F_Imagen);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn.getConn());
            JasperPrintManager.printReport(jasperPrint, false);

            smtfolio2.execute("DELETE FROM tb_imprepvalauto WHERE F_FactAgr='" + FolCon + "'");

        }
        conn.cierraConexion();
    %>
    <head>
        <script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 5000);

        </script>
    </head> 
</html>
