<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>

<%@page import="conn.ConectionDB"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="javax.print.attribute.standard.Copies"%>
<%@page import="javax.print.attribute.standard.MediaSizeName"%>
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
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_User = "";
    Statement smtfolio = null;
    ResultSet folio = null;
    Statement smtfolio2 = null;
    try {
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
    } catch (Exception e) {
    }
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    //out.println(F_Imagen);
%>
<html>
    <%
   ConectionDB con = new ConectionDB();
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
                if (Nom.contains("PDFCreator")) {
                    Epson = count;
                } else {
                    Impre = count;
                }
                count++;
            }
        
        folio = con.consulta("SELECT F_FolCon FROM tb_imprepconglobal WHERE F_Usuario='" + F_User + "' AND F_Date=CURDATE() GROUP BY F_FolCon");
        while (folio.next()) {
            FolCon = folio.getString(1);

            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVerticalglobal.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, con.getConn());

                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, imprePredet.getAttributes());
                exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                //exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
            try {
                    exporter.exportReport();
                } catch (Exception ex) {
                    //out.print("<script type='text/javascript'>alert('Folio sin Datos ');</script>");
                    System.out.println("Error-> " + ex);
                }
            
            //parameter.put("F_Imagen", F_Imagen);
            
            /*
            byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn);

            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);

            outputStream.flush();
            outputStream.close();
            */
            /*
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn);
            JasperPrintManager.printReport(jasperPrint, false);
            */
            
            smtfolio2.execute("DELETE FROM tb_imprepconglobal WHERE F_FolCon='" + FolCon + "'");
            System.out.println("Folio Vertical2-->" + FolCon);
        }

        con.cierraConexion();
    %>
 <head>
        <script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 1000);

        </script>
    </head>    
</html>
