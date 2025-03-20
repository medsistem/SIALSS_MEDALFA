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
    String F_SecFin = "", F_Cvepro = "", F_DesRegion = "", FolCon = "", F_User = "";
    String DesV="",F_Region="",F_DesJur="",F_DesMun="",F_DesLoc="",F_DesUni="",F_Fecha1="",F_Fecha2="",F_Serie1="",F_Serie2="",F_Provee="",F_Surtido="",F_Coberturas="",F_Suministro="";
    int RegistroC=0,Ban=0;
    double Hoja=0.0;
    ResultSet folio = null;
    ResultSet Contare = null;
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
        
        folio = conn.consulta("SELECT F_FolCon FROM tb_imprepconauto WHERE F_Usuario='" + F_User + "' AND F_Date=CURDATE() GROUP BY F_FolCon");
        while (folio.next()) {
            FolCon = folio.getString(1);
            
            Contare = conn.consulta("SELECT COUNT(F_FolCon),F_Region,F_DesJur,F_DesMun,F_DesLoc,F_DesUni,F_Fecha1,F_Fecha2,F_Serie1,F_Serie2,F_Provee,F_Surtido,F_Coberturas,F_Suministro FROM tb_imprepconauto WHERE F_FolCon='"+FolCon+"'");
            if(Contare.next()){
               RegistroC = Contare.getInt(1);
               F_Region= Contare.getString(2);
               F_DesJur= Contare.getString(3);
               F_DesMun= Contare.getString(4);
               F_DesLoc= Contare.getString(5);
               F_DesUni= Contare.getString(6);
               F_Fecha1= Contare.getString(7);
               F_Fecha2= Contare.getString(8);
               F_Serie1= Contare.getString(9);
               F_Serie2= Contare.getString(10);
               F_Provee= Contare.getString(11);
               F_Surtido= Contare.getString(12);
               F_Coberturas= Contare.getString(13);
               F_Suministro= Contare.getString(14);
            }
            if(RegistroC <= 27){
                Ban = 1;
                DesV="1 Hoja";
            }else{
                if(RegistroC >= 35){
                    Hoja = RegistroC * 1.0 / 35;
                    if (Hoja % 1 == 0){
                    DesV="3 Hoja entero";
                    Ban = 2;    
                    }else{
                    DesV="Mas Hoja decimal";
                    Ban = 1;
                    }                    
                }else{
                    DesV = "2 hojas";
                    Ban = 2;
                }
            }
            System.out.println("Re: "+RegistroC+" Ban: "+Ban+" DesV "+DesV+" Hoja "+Hoja);
            Hoja=0;
            
            if(Ban == 1){
            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVerticalAuto.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            parameter.put("F_Imagen", F_Imagen);
            System.out.println("Folio Concentrado-->" + FolCon);
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
            }else{
            File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVerticalAuto2.jasper"));
            Map parameter = new HashMap();
            parameter.put("FolCon", FolCon);
            parameter.put("F_Imagen", F_Imagen);
             parameter.put("F_Region",F_Region);
            parameter.put("F_DesJur",F_DesJur);
            parameter.put("F_DesMun",F_DesMun);
            parameter.put("F_DesLoc",F_DesLoc);
            parameter.put("F_DesUni",F_DesUni);
            parameter.put("F_Fecha1",F_Fecha1);
            parameter.put("F_Fecha2",F_Fecha2);
            parameter.put("F_Serie1",F_Serie1);
            parameter.put("F_Serie2",F_Serie2);
            parameter.put("F_Provee",F_Provee);
            parameter.put("F_Surtido",F_Surtido);
            parameter.put("F_Coberturas",F_Coberturas);
            parameter.put("F_Suministro",F_Suministro);
            System.out.println("Folio Concentrado-->" + FolCon);
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
            }
            //smtfolio2.execute("DELETE FROM tb_imprepconauto WHERE F_FolCon='" + FolCon + "'");
        }
        conn.consulta("DELETE FROM tb_imprepconauto");
        conn.cierraConexion();
    %>
    <script type="text/javascript">

        var ventana = window.self;
        ventana.opener = window.self;
        setTimeout("window.close()", 5000);

    </script>
</html>
