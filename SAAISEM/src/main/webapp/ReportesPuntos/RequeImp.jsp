<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>
<%@page import="conn.ConectionDBTrans"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="javax.print.attribute.standard.MediaSizeName"%>
<%@page import="javax.print.attribute.standard.MediaSize"%>
<%@page import="javax.print.attribute.standard.Copies"%>
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
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", F_ClaUni = "", F_User = "";
    
    ResultSet folio = null;

    try {
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
    } catch (Exception e) {
    }
    String Impresora = request.getParameter("Impresora");
    
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    out.println(F_Imagen);
%>
<html>
    <%
    ConectionDBTrans conn = new ConectionDBTrans();
        
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
        
        folio =  conn.consulta("SELECT F_ClaUni FROM tb_auxrai GROUP BY F_ClaUni ORDER BY F_ClaUni ASC");
        while (folio.next()) {
            F_ClaUni = folio.getString(1);

            File reportfile = new File(application.getRealPath("/ReportesPuntos/Requerimientos.jasper"));
            Map parameter = new HashMap();
            parameter.put("F_ClaUni", F_ClaUni);
            System.out.println("Req-->" + F_ClaUni);
            /*
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn);
            JasperPrintManager.printReport(jasperPrint, false);
            */
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn.getConn());
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

            //exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, imprePredet.getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
            
            try {
                    exporter.exportReport();
                } catch (Exception ex) {
                    //out.print("<script type='text/javascript'>alert('Folio sin Datos ');</script>");
                    System.out.println("Error-> " + ex);
                }
            
            //smtfolio2.execute("DELETE FROM tb_imprepconauto WHERE F_FolCon='" + FolCon + "'");
        }

        conn.cierraConexion();
    %>
    <script type="text/javascript">

        var ventana = window.self;
        ventana.opener = window.self;
        setTimeout("window.close()", 5000);

    </script>
</html>
