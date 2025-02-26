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
<%@page import="javax.print.attribute.standard.MediaSize"%>
<%@page import="javax.print.attribute.standard.MediaPrintableArea"%>
<%@page import="javax.print.attribute.PrintRequestAttributeSet"%>
<%@page import="javax.print.attribute.HashPrintRequestAttributeSet"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="javax.print.PrintService"%>
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
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "",F_FolFact="", F_User = "",DesV="", Impresora="";
    String F_Region="",F_DesJur="",F_DesMun="",F_DesLoc="",F_DesUni="",F_Fecha1="",F_Fecha2="",F_Serie1="",F_Serie2="",F_Provee="",F_Surtido="",F_Coberturas="",F_Suministro="";
    int RegistroC=0,Ban=0,F_Punto=0;
    double Hoja=0.0;
    Statement smtfolio = null;
    ResultSet folio = null;
    ResultSet Contare = null;
    Statement smtfolio2 = null;
    Statement ContarReg = null;
    HttpSession sesion = request.getSession();
    
    
    
    try {
//FolCon = request.getParameter("FolCon");
        Impresora = (String) sesion.getAttribute("Impresora");
        F_User = (String) sesion.getAttribute("F_User");
    } catch (Exception e) {
    }
    //String Impresora = request.getParameter("Impresora");
    
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    
    out.println(Impresora);
%>
<html>
    <%
      Connection conn;
        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");   
        smtfolio = conn.createStatement();
        smtfolio2 = conn.createStatement();
                 
        //for(int x=1; x<=8; x++){
        ResultSet FolioFact = smtfolio2.executeQuery("SELECT F_FolFact FROM tb_impreauto WHERE F_User='" + F_User + "' GROUP BY F_FolFact ORDER BY F_FolFact ASC");
        while(FolioFact.next()){
            F_FolFact = FolioFact.getString(1);
        
        folio = smtfolio.executeQuery("SELECT F_FolCon,F_FolFact,F_Punto FROM tb_impreauto WHERE F_User='" + F_User + "' and F_FolFact='"+F_FolFact+"' GROUP BY F_FolCon ORDER BY F_FolCon ASC");
        while (folio.next()) {
            FolCon = folio.getString(1);
            System.out.println("ReporteVerticalImp'"+folio.getString(3)+"'");
            out.println(" <script>window.open('ReporteVerticalImp"+folio.getString(3)+".jsp?User="+sesion.getAttribute("nombre")+"&Impresora="+Impresora+"&FolCon="+FolCon+"', '', 'width=1200,height=800,left=50,top=50,toolbar=no'); </script>");
            out.println(" <script>window.open('ReporteHorizontalImp"+folio.getString(3)+".jsp?User="+sesion.getAttribute("nombre")+"&Impresora="+Impresora+"&FolCon="+FolCon+"', '', 'width=1200,height=800,left=50,top=50,toolbar=no'); </script>");            
            System.out.println("Punto "+FolCon);
             //Thread.sleep(6000);
        }
        System.out.println("Factura "+F_FolFact);
        out.println(" <script>window.open('ReporteConcentradoImp.jsp?User="+sesion.getAttribute("nombre")+"&Impresora="+Impresora+"&FolCon="+F_FolFact+"', '', 'width=1200,height=800,left=50,top=50,toolbar=no'); </script>");
         //Thread.sleep(2000);
        }
        //out.println("<script>window.history.back()</script>");
        /*if(FolCon != ""){
            out.println(" <script>window.open('ReporteVerticalImp.jsp?User="+sesion.getAttribute("nombre")+"&Impresora="+Impresora+"', '', 'width=1200,height=800,left=50,top=50,toolbar=no'); </script>");
        }*/
        FolCon="";
       // }
        
        conn.close();
    %>
    <script type="text/javascript">

        var ventana = window.self;
        ventana.opener = window.self;
        setTimeout("window.history.back()", 500000);

    </script>
</html>
