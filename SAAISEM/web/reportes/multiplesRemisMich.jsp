<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>



<%@page import="java.text.DecimalFormat"%>
<%@page import="Impresiones.InsertImpreFolio"%>
<%@page import="NumeroLetra.Numero_a_Letra"%>
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
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="net.sf.jasperreports.engine.*" %> 
<%@ page import="java.util.*" %> 
<%@ page import="java.io.*" %> 
<%@ page import="java.sql.*" %> 
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link href="../css/loader.css" rel="stylesheet" type="text/css"/>

    </head>
    <body>
        <div class="imp" >


            <% /*Parametros para realizar la conexión*/

                    HttpSession sesion = request.getSession();
                    ConectionDB con = new ConectionDB();
                    String usua = "";
                    if (sesion.getAttribute("nombre") != null) {
                        usua = (String) sesion.getAttribute("nombre");
                    }

                    int RegistroC = 0, Ban = 0, HojasC = 0, HojasR = 0, ContarRedF = 0, Ban2 = 0, Diferencia = 0;
                    String DesV = "", remis = "", ProyectoF = "", ProyectoFactura = "", Nomenclatura = "", Encabezado = "", RedFria = "", Imgape = "", NoImgApe = "";
                    double Hoja = 0.0, Hoja2 = 0.0, MTotalMonto = 0.0, Iva = 0.0;
                    String User = request.getParameter("User");
                    String Impresora = request.getParameter("Impresora");
                    String TipoInsumo = request.getParameter("Tipo");
                    DecimalFormat df = new DecimalFormat("#,###.00");
                    int Copy = 0;

                    Connection conexion;
                    //Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    conexion = con.getConn();
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

                    ResultSet FoliosC = con.consulta("SELECT F_Folio, F_Copy, U.F_Proyecto, F.F_Proyecto AS F_ProyectoF, F_Ban FROM tb_folioimp F INNER JOIN tb_factura FT ON F.F_Folio = FT.F_ClaDoc AND F.F_Proyecto = FT.F_Proyecto INNER JOIN tb_uniatn U ON FT.F_ClaCli = U.F_ClaCli WHERE F.F_User = '" + User + "' GROUP BY F.F_Folio, F.F_Proyecto, F_Ban ORDER BY F_Folio + 0;");
                    while (FoliosC.next()) {
                        remis = FoliosC.getString(1);
                        Copy = FoliosC.getInt(2);
                        ProyectoF = FoliosC.getString(3);
                        ProyectoFactura = FoliosC.getString(4);
                        Ban2 = FoliosC.getInt(5);

                        ResultSet RsetNomenc = con.consulta("SELECT F_Nomenclatura, F_Encabezado FROM tb_proyectos WHERE F_Id='" + ProyectoFactura + "';");
                        while (RsetNomenc.next()) {
                            Nomenclatura = RsetNomenc.getString(1);
                            Encabezado = RsetNomenc.getString(2);
                        }

                        con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "' and F_ProyectoF = '" + ProyectoFactura + "' ;");
                        int SumaMedReq = 0, SumaMedSur = 0, SumaMedReqT = 0, SumaMedSurT = 0;
                        double MontoMed = 0.0, MontoTMed = 0.0, Costo = 0.0;
                        String Unidad = "", Fecha = "", Direc = "", F_FecApl = "", F_Obs = "", F_Obs2 = "", Razon = "", Proyecto = "", Letra = "", ImagenControlado = "";
                        int SumaMatReq = 0, SumaMatSur = 0, SumaMatReqT = 0, SumaMatSurT = 0, ContarControlado = 0, Contarape = 0;
                        double MontoMat = 0.0, MontoTMat = 0.0;

                        int TotalReq = 0, TotalSur = 0;
                        double TotalMonto = 0.0;

                        ResultSet ObsFact = con.consulta("SELECT CONCAT(F_Obser, ' - ', F_Tipo) AS F_Obser FROM tb_obserfact WHERE F_IdFact='" + remis + "' AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F_IdFact;");
                        while (ObsFact.next()) {
                            F_Obs = ObsFact.getString(1);
                        }

                       // ResultSet RsetControlado = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTAR, IFNULL(FC.CONTARCONTROLADO, 0) AS CONTARCONTROLADO, COUNT(*) - IFNULL(FC.CONTARCONTROLADO, 0) AS DIF FROM tb_factura F LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) AS CONTARCONTROLADO FROM tb_factura WHERE F_ClaDoc = '" + remis + "' AND F_Proyecto = '" + ProyectoFactura + "' AND F_Ubicacion  IN ('CONTROLADO') ) FC ON F.F_ClaDoc = FC.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "'AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaDoc;");
                       ResultSet RsetControlado = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTAR, IFNULL(FC.CONTARCONTROLADO, 0) AS CONTARCONTROLADO, COUNT(*) - IFNULL(FC.CONTARCONTROLADO, 0) AS DIF FROM tb_factura F LEFT JOIN ( SELECT fa.F_ClaDoc, COUNT(*) AS CONTARCONTROLADO FROM tb_factura fa INNER JOIN tb_controlados ctr on ctr.F_ClaPro = fa.F_ClaPro WHERE fa.F_ClaDoc = '" + remis + "'  AND fa.F_Proyecto = '" + ProyectoFactura + "' ) FC ON F.F_ClaDoc = FC.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "'AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaDoc;");
                    
                       if (RsetControlado.next()) {
                            ContarControlado = RsetControlado.getInt(3);
                        }
                        if (ContarControlado > 0) {

                            ImagenControlado = "image/Controlado.jpg";
                            NoImgApe = "image/no_imgape.jpg";
                            Imgape = NoImgApe;
                            RedFria = "image/Nored_fria.jpg";
                        } else {

                            ImagenControlado = "image/NoControlado.jpg";
                            }
                            ResultSet DatosRedF = con.consulta("SELECT COUNT(*) FROM tb_redfria r INNER JOIN tb_factura f ON r.F_ClaPro = f.F_ClaPro WHERE F_StsFact = 'A' AND F_ClaDoc = '" + remis + "' AND F_CantSur > 0 AND F_Proyecto = '" + ProyectoFactura + "';");
                            if (DatosRedF.next()) {
                                ContarRedF = DatosRedF.getInt(1);
                            }
                            if (ContarRedF > 0) {
                                RedFria = "image/red_fria.jpg";
                            } else {
                                RedFria = "image/Nored_fria.jpg";
                            }

                            ResultSet DatosAPE = con.consulta("SELECT COUNT(*) FROM tb_ape ap INNER JOIN tb_factura f ON ap.F_ClaPro = f.F_ClaPro WHERE F_StsFact = 'A' AND F_ClaDoc = '" + remis + "' AND F_CantSur > 0 AND F_Proyecto = '" + ProyectoFactura + "';");
                            if (DatosAPE.next()) {
                                Contarape = DatosAPE.getInt(1);
                            }
                            NoImgApe = "image/no_imgape.jpg";

                            if (Contarape > 0) {
                                Imgape = "image/imgape.png";
                            } else {
                                Imgape = NoImgApe;
                            }
                            

                
                         System.out.println("BANDERA: " + Ban2);
                        System.out.println("Tipo de insumo: " + TipoInsumo);
                        /////////////////////////////////////
                        /////////proyecto isem///////
                        /////////////////////////////////
                        if (ProyectoFactura.equals("1")) {

                            if (Ban2 == 1) {
                                if (TipoInsumo.equals("5")) {
                                     for (int x = 0; x < Copy; x++) {
                                            
                                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                            Map parameters = new HashMap();
                                            parameters.put("Folfact", remis);
                                            parameters.put("Usuario", User);
                                            parameters.put("F_Obs", F_Obs);
                                            parameters.put("TipoInsumo", TipoInsumo);
                                            parameters.put("Imgape", Imgape);
                                            parameters.put("RedFria", RedFria);
                                            parameters.put("ImagenControlado", ImagenControlado);
                                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                            try {
                                                exporter.exportReport();
                                            } catch (Exception ex) {

                                                System.out.println("Error- RECETAS> " + ex.getMessage());

                                            }
                                        }
                                }
                                if (TipoInsumo.equals("1") ) {
                                ImagenControlado = "image/NoControlado.jpg";
                                     for (int x = 0; x < Copy; x++) {
                                            
                                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                            Map parameters = new HashMap();
                                            parameters.put("Folfact", remis);
                                            parameters.put("Usuario", User);
                                            parameters.put("F_Obs", F_Obs);
                                            parameters.put("TipoInsumo", TipoInsumo);
                                            parameters.put("BanTip", Ban2);
                                            parameters.put("Imgape", Imgape);
                                            parameters.put("RedFria", RedFria);
                                            parameters.put("ImagenControlado", ImagenControlado);
                                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                            try {
                                                exporter.exportReport();
                                            } catch (Exception ex) {

                                                System.out.println("Error- SECO> " + ex.getMessage());

                                            }
                                        }
                                }
                                
                            }//BAN 1
                            //REDFRIA
                             if (Ban2 == 2) {
                                
                           
                                if (TipoInsumo.equals("2")) {
                                  Imgape = "image/no_imgape.jpg";
                                      ImagenControlado = "image/NoControlado.jpg";
                                     
                                       for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact", remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error REDFRIA-> " + ex.getMessage());

                                        }
                                    }
                                }
                               
                            }//BAN REDFRIS
                             //APE
                             if (Ban2 == 3) {
                                 
                                if (TipoInsumo.equals("3") ) {
                                RedFria = "image/Nored_fria.jpg";
                                    ImagenControlado = "image/NoControlado.jpg";
                                        for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- APE> " + ex.getCause());

                                        }
                                    }
                                }
                            }//BAN APE
                            //CONTROLADO 
                            if (Ban2 == 4) {
                                if (TipoInsumo.equals("4") ) {
                                       for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- CONTROLADO> " + ex.getMessage());

                                        }
                                    }
                                }
                            }//BAN CONTROLADO
                            
                             if (Ban2 == 6) {//FONSABI
                                  if (TipoInsumo.equals("6") ) {
                                        for (int x = 0; x < Copy; x++) {
                                            ImagenControlado = "image/NoControlado.jpg";
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- FONSABI> " + ex.getCause());

                                        }
                                    }
                                }
                                        
                            if (TipoInsumo.equals("2") ) {
                                Imgape = "image/no_imgape.jpg";
                                    ImagenControlado = "image/NoControlado.jpg";
                                        for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- REDFRIA FONSABI> " + ex.getCause());

                                        }
                                    }
                                }
                                  if (TipoInsumo.equals("3") ) {
                                  RedFria = "image/Nored_fria.jpg";
                                      ImagenControlado = "image/NoControlado.jpg";
                                        for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- APE FONSABI> " + ex.getCause());

                                        }
                                    }
                                }
                                 
                                if (TipoInsumo.equals("4") ) {
                                       for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- CONTROLADO FONSABI> " + ex.getMessage());

                                        }
                                    }
                                }
                            }
                             if (Ban2 == 7) {
                                if (TipoInsumo.equals("7") ) {
                                       for (int x = 0; x < Copy; x++) {
                                        System.out.println("remis-> " + remis);
                                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                        Map parameters = new HashMap();
                                        parameters.put("Folfact",remis);
                                        parameters.put("Usuario", User);
                                        parameters.put("F_Obs", F_Obs);
                                        parameters.put("Imgape", Imgape);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- redfria onco> " + ex.getMessage());

                                        }
                                    }
                                }//redfria onco
                }   
                if (Ban2 == 8) {
                                if (TipoInsumo.equals("8") ) {
                                for (int x = 0; x < Copy; x++) {
                                File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosMicSurt.jasper"));
                                Map parameters = new HashMap();
                                parameters.put("Folfact",remis);
                                parameters.put("Usuario", User);
                                parameters.put("F_Obs", F_Obs);
                                parameters.put("Imgape", Imgape);
                                        parameters.put("TipoInsumo", TipoInsumo);
                                        parameters.put("BanTip", Ban2);
                                        parameters.put("RedFria", RedFria);
                                        parameters.put("ImagenControlado", ImagenControlado);
                                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                                        try {
                                            exporter.exportReport();
                                        } catch (Exception ex) {

                                            System.out.println("Error- APE onco> " + ex.getMessage());

                                        }
                                    }
                                }//APE onco
                            }//FIN DE BAN ONCO 
                          }//FIN DEL PROYECTO 1
        }//FIN DEL CICLO FOLIOS C
                conexion.close (); 
                       
            %>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
        <script type="text/javascript">
            $(window).load(function () {
                $(".imp").fadeOut("slow");
                setTimeout("window.close()", 6000);
            });

        </script>
    </body>
</html>