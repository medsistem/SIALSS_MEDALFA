<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>
<%@page import="conn.ConectionDBTrans"%>
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
<%@page import="org.omg.PortableInterceptor.SYSTEM_EXCEPTION"%>
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

    String factura = "", Reportes = "", Punto = "", fecha1 = "", fecha2 = "", FoliosFact = "", Mes = "", Dia = "", AA = "", Fechas = "", FecEntrega = "";
    String Impresora = "";
    String path = getServletContext().getRealPath("/");
    String C1 = "N", C2 = "N", C3 = "N", C4 = "N", C5 = "N", C6 = "N", C7 = "N", C8 = "N";
    String C1_1 = "A", C2_1 = "A", C3_1 = "A", C4_1 = "A", C5_1 = "A", C6_1 = "A", C7_1 = "A", C8_1 = "A";
    int ban = 0, puntos = 0, Fecha = 0;
    Statement smtfolio = null;
    Statement smtfacturas = null;
    ResultSet folio = null;
    ResultSet facturas = null;

    try {
        factura = request.getParameter("factura");
        ban = Integer.parseInt(request.getParameter("ban"));
        fecha1 = request.getParameter("Fecha1");
        fecha2 = request.getParameter("Fecha2");
        Impresora = request.getParameter("Impresora");
    } catch (Exception e) {
    }
    System.out.println(factura + "//" + ban);
    String F_Imagen = path + "imagenes\\check2.png";
%>
<html>
    <%
        if (ban == 1) {
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

            folio = conn.consulta("SELECT F_FacGNKLAgr,F_Folios,F_DesUniIS,F_DesJurIS,F_DesCooIS,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Puntos FROM tb_caratula WHERE F_FacGNKLAgr='" + factura + "'");
            while (folio.next()) {
                puntos = folio.getInt(7);
                Fechas = folio.getString(6);
                Reportes = Reportes + puntos;
            }
            Dia = Fechas.substring(0, 2);
            AA = Fechas.substring(6, 10);
            Fecha = Integer.parseInt(Fechas.substring(3, 5));
            System.out.println("fecha" + Fecha);
            if (Fecha == 1) {
                Mes = "ENERO";
            } else if (Fecha == 2) {
                Mes = "FEBRERO";
            } else if (Fecha == 3) {
                Mes = "MARZO";
            } else if (Fecha == 4) {
                Mes = "ABRIL";
            } else if (Fecha == 5) {
                Mes = "MAYO";
            } else if (Fecha == 6) {
                Mes = "JUNIO";
            } else if (Fecha == 7) {
                Mes = "JULIO";
            } else if (Fecha == 8) {
                Mes = "AGOSTO";
            } else if (Fecha == 9) {
                Mes = "SEPTIEMBRE";
            } else if (Fecha == 10) {
                Mes = "OCTUBRE";
            } else if (Fecha == 11) {
                Mes = "NOVIEMBRE";
            } else {
                Mes = "DICIEMBRE";
            }

            FecEntrega = Dia + "/" + Mes + "/" + AA;

            int y = 1;
            for (int x = 0; x < Reportes.length(); x++) {
                Punto = Reportes.substring(x, y);
                y = y + 1;
                System.out.println("punto:" + Punto);

                if (Punto.equals("1")) {
                    C1 = "X";
                    C1_1 = "";
                } else if (Punto.equals("2")) {
                    C2 = "X";
                    C2_1 = "";
                } else if (Punto.equals("3")) {
                    C3 = "X";
                    C3_1 = "";
                } else if (Punto.equals("4")) {
                    C4 = "X";
                    C4_1 = "";
                } else if (Punto.equals("5")) {
                    C5 = "X";
                    C5_1 = "";
                } else if (Punto.equals("6")) {
                    C6 = "X";
                    C6_1 = "";
                } else if (Punto.equals("7")) {
                    C7 = "X";
                    C7_1 = "";
                } else {
                    C8 = "X";
                    C8_1 = "";
                }

            }
            File reportfile = new File(application.getRealPath("/ReportesPuntos/MarbeteSobre.jasper"));
            Map parameter = new HashMap();

            parameter.put("C1", C1);
            parameter.put("C2", C2);
            parameter.put("C3", C3);
            parameter.put("C4", C4);
            parameter.put("C5", C5);
            parameter.put("C6", C6);
            parameter.put("C7", C7);
            parameter.put("C8", C8);
            parameter.put("C1_1", C1_1);
            parameter.put("C2_1", C2_1);
            parameter.put("C3_1", C3_1);
            parameter.put("C4_1", C4_1);
            parameter.put("C5_1", C5_1);
            parameter.put("C6_1", C6_1);
            parameter.put("C7_1", C7_1);
            parameter.put("C8_1", C8_1);
            parameter.put("Factura", factura);
            parameter.put("fecha", FecEntrega);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn.getConn());
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

            //exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, imprePredet.getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

            /*
             JasperPrint jasperPrint= JasperFillManager.fillReport(reportfile.getPath(),parameter,conn);
             JasperPrintManager.printReport(jasperPrint,false);   
             */
            try {
                exporter.exportReport();
            } catch (Exception ex) {
                //out.print("<script type='text/javascript'>alert('Folio sin Datos ');</script>");
                System.out.println("Error-> " + ex);
            }
            Reportes = "";
            conn.cierraConexion();
        } else {
            Connection conn;
            Class.forName("org.mariadb.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");
            //conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "saa_medalfaIsem", "S4a_M3d@l7@2020");    
            smtfacturas = conn.createStatement();
            smtfolio = conn.createStatement();

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

            /*facturas = smtfacturas.executeQuery("SELECT F_FacGNKLAgr FROM tb_caratula WHERE F_Fecsur between '"+fecha1+"' and '"+fecha2+"' group by F_FacGNKLAgr");
             while(facturas.next()){    
             FoliosFact = facturas.getString(1);
             System.out.println("facturas:"+FoliosFact);*/
            //folio = smtfolio.executeQuery("SELECT F_FacGNKLAgr,F_Folios,F_DesUniIS,F_DesJurIS,F_DesCooIS,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Puntos FROM tb_caratula WHERE F_FacGNKLAgr='"+FoliosFact+"'");
            folio = smtfolio.executeQuery("SELECT F_FacGNKLAgr,F_Folios,F_DesUniIS,F_DesJurIS,F_DesCooIS,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Puntos FROM tb_caratula WHERE F_FacGNKLAgr='" + factura + "'");
            while (folio.next()) {
                puntos = folio.getInt(7);
                Fechas = folio.getString(6);
                Reportes = Reportes + puntos;
            }

            Dia = Fechas.substring(0, 2);
            AA = Fechas.substring(6, 10);
            Fecha = Integer.parseInt(Fechas.substring(3, 5));
            System.out.println("fecha" + Fecha);
            if (Fecha == 1) {
                Mes = "ENERO";
            } else if (Fecha == 2) {
                Mes = "FEBRERO";
            } else if (Fecha == 3) {
                Mes = "MARZO";
            } else if (Fecha == 4) {
                Mes = "ABRIL";
            } else if (Fecha == 5) {
                Mes = "MAYO";
            } else if (Fecha == 6) {
                Mes = "JUNIO";
            } else if (Fecha == 7) {
                Mes = "JULIO";
            } else if (Fecha == 8) {
                Mes = "AGOSTO";
            } else if (Fecha == 9) {
                Mes = "SEPTIEMBRE";
            } else if (Fecha == 10) {
                Mes = "OCTUBRE";
            } else if (Fecha == 11) {
                Mes = "NOVIEMBRE";
            } else {
                Mes = "DICIEMBRE";
            }

            FecEntrega = Dia + "/" + Mes + "/" + AA;
            int y = 1;
            for (int x = 0; x < Reportes.length(); x++) {
                Punto = Reportes.substring(x, y);
                y = y + 1;
                System.out.println("Factura"+factura+" punto:" + Punto);

                if (Punto.equals("1")) {
                    C1 = "X";
                    C1_1 = "";
                } else if (Punto.equals("2")) {
                    C2 = "X";
                    C2_1 = "";
                } else if (Punto.equals("3")) {
                    C3 = "X";
                    C3_1 = "";
                } else if (Punto.equals("4")) {
                    C4 = "X";
                    C4_1 = "";
                } else if (Punto.equals("5")) {
                    C5 = "X";
                    C5_1 = "";
                } else if (Punto.equals("6")) {
                    C6 = "X";
                    C6_1 = "";
                } else if (Punto.equals("7")) {
                    C7 = "X";
                    C7_1 = "";
                } else {
                    C8 = "X";
                    C8_1 = "";
                }

            }
            File reportfile = new File(application.getRealPath("/ReportesPuntos/MarbeteSobre.jasper"));
            Map parameter = new HashMap();

            /*parameter.put("C1",C1);
             parameter.put("C2",C2);
             parameter.put("C3",C3);
             parameter.put("C4",C4);
             parameter.put("C5",C5);
             parameter.put("C6",C6);
             parameter.put("C7",C7);
             parameter.put("C8",C8);            
             parameter.put("Factura",FoliosFact);
             parameter.put("imagen",F_Imagen);
             parameter.put("fecha",FecEntrega);*/
            parameter.put("C1", C1);
            parameter.put("C2", C2);
            parameter.put("C3", C3);
            parameter.put("C4", C4);
            parameter.put("C5", C5);
            parameter.put("C6", C6);
            parameter.put("C7", C7);
            parameter.put("C8", C8);
            parameter.put("C1_1", C1_1);
            parameter.put("C2_1", C2_1);
            parameter.put("C3_1", C3_1);
            parameter.put("C4_1", C4_1);
            parameter.put("C5_1", C5_1);
            parameter.put("C6_1", C6_1);
            parameter.put("C7_1", C7_1);
            parameter.put("C8_1", C8_1);
            //parameter.put("Factura", FoliosFact);
            parameter.put("Factura", factura);
            parameter.put("fecha", FecEntrega);

            /*
             JasperPrint jasperPrint= JasperFillManager.fillReport(reportfile.getPath(),parameter,conn);
             JasperPrintManager.printReport(jasperPrint,false);    
             */
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportfile.getPath(), parameter, conn);
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

            Reportes = "";
            //}
            conn.close();
        }

    %>
    <script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 100000);

        </script>
</html>