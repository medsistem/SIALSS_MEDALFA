<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>

<%@page import="conn.ConectionDB"%>
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
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
    } catch (Exception e) {
    }
    
    String Impresora = request.getParameter("Impresora");
    FolCon = request.getParameter("FolCon");
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    out.println(F_Imagen);
%>
<html>
    <%
        ConectionDB conn = new ConectionDB(); 
        
        
        int count = 0, Epson = 0, Impre = 0;
            String Nom = "";
            PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService imprePredet = PrintServiceLookup.lookupDefaultPrintService();
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            MediaSizeName mediaSizeName = MediaSize.findMedia(4, 4, MediaPrintableArea.INCH);
            printRequestAttributeSet.add(mediaSizeName);
            printRequestAttributeSet.add(new Copies(1));
            
            for (PrintService printService : impresoras) {
                Nom = printService.getName();
                System.out.println("impresora" + Nom);
                if (Nom.contains(Impresora)) {
                    Epson = count;
                } else {
                    Impre = count;
                }
                count++;
            }
        
     
            System.out.println("Folio Validacion-->" + FolCon);
            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepConcentradoauto.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            parameter.put("F_Imagen", F_Imagen);
            
         
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn.getConn());
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
            
            try {
                    exporter.exportReport();
                } catch (Exception ex) {
                  
                    System.out.println("Error-> " + ex);
                }
      
        conn.cierraConexion();
    %>
    <head>
        <script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 500);

        </script>
    </head> 
</html>
