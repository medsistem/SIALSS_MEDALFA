<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>

<%@page import="Impresiones.InsertImpreFolio"%>
<%@page import="java.text.DecimalFormat"%>
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
    DecimalFormat df = new DecimalFormat("#,###.00");
    int Copy = 0;
    String TipoInsumo = request.getParameter("Tipo");

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
        int SumaMedReq = 0, SumaMedSur = 0, SumaMedReqT = 0, SumaMedSurT = 0, Origen = 0;
        double MontoMed = 0.0, MontoTMed = 0.0, Costo = 0.0;
        String Unidad = "", Fecha = "", Direc = "", F_FecApl = "", F_Obs = "", F_Obs2 = "", Razon = "", Proyecto = "", Letra = "", Jurisdiccion = "", Municipio = "", ImagenControlado = "", CargoResponsable = "", NombreResponsable = "";
        int SumaMatReq = 0, SumaMatSur = 0, SumaMatReqT = 0, SumaMatSurT = 0, ContarControlado = 0, Contarape = 0;
        double MontoMat = 0.0, MontoTMat = 0.0;

        int TotalReq = 0, TotalSur = 0;
        double TotalMonto = 0.0;

        ResultSet ObsFact = con.consulta("SELECT CONCAT(F_Obser, ' - ', F_Tipo) AS F_Obser FROM tb_obserfact WHERE F_IdFact='" + remis + "' AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F_IdFact;");
        while (ObsFact.next()) {
            F_Obs = ObsFact.getString(1);
        }

        ResultSet RsetControlado = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTAR, IFNULL(FC.CONTARCONTROLADO, 0) AS CONTARCONTROLADO, COUNT(*) - IFNULL(FC.CONTARCONTROLADO, 0) AS DIF FROM tb_factura F LEFT JOIN ( SELECT fa.F_ClaDoc, COUNT(*) AS CONTARCONTROLADO FROM tb_factura fa INNER JOIN tb_controlados ctr on ctr.F_ClaPro = fa.F_ClaPro WHERE fa.F_ClaDoc = '" + remis + "'  AND fa.F_Proyecto = '" + ProyectoFactura + "' AND fa.F_Ubicacion RLIKE 'CONTROLADO|CTRFO' ) FC ON F.F_ClaDoc = FC.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "'AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaDoc;");
        if (RsetControlado.next()) {
            ContarControlado = RsetControlado.getInt(3);
        }
        if (ContarControlado > 0) {
            ImagenControlado = "image/Controlado.jpg";
            NoImgApe = "image/no_imgape.jpg";
            Imgape = NoImgApe;
            RedFria = "image/Nored_fria.jpg";

            ResultSet RsetUsuCargo = con.consulta("SELECT uc.Usuario_Nombre, uc.Cargo, uc.Nomeclatura_Usu FROM tb_usuariocargo AS uc WHERE uc.`Status` = 1 AND uc.IdTipoUsu = 16;");
            if (RsetUsuCargo.next()) {

                CargoResponsable = RsetUsuCargo.getString(2);
                NombreResponsable = RsetUsuCargo.getString(3) + ' ' + RsetUsuCargo.getString(1);
            }

        } else {
            ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";

            
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
            /*Cuando son cero*/
            if (Ban2 == 0) {
            
                if (TipoInsumo.equals("0")) {
                System.out.println("cero");
                              CargoResponsable = " ";
            NombreResponsable = " ";
                    con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("Copy -->" + Copy);
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsemCero.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                }

            }
            if (Ban2 == 1) {
               // ResultSet DatosFactMed = null;
                if (TipoInsumo.equals("5")) {
                    //RECETAS
                    System.out.println("tecetas tipo 5");

                      con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                  
                        for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsemReceta.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                     
                        System.out.println(remis);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                        try {
                            exporter.exportReport();
                        } catch (Exception ex) {
                            //out.print("<script type='text/javascript'>alert('Folio sin Datos ');</script>");
                            System.out.println("Error recetas-> " + ex.getMessage());

                        }
                    }
             
                }
                  
                if (TipoInsumo.equals("1")) {
                System.out.println("seco");
            ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                            try {
                                exporter.exportReport();
                            } catch (Exception ex) {

                                System.out.println("Error seco-> " + ex);

                            }
                        }
                }//fin de normal
                
                  }  
                 if (Ban2 == 2) { 
                
                if (TipoInsumo.equals("2")) {
                 System.out.println("redfria");
                  Imgape = "image/no_imgape.jpg";
                      ImagenControlado = "image/NoControlado.jpg";
                      CargoResponsable = " ";
                      NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                            try {
                                exporter.exportReport();
                            } catch (Exception ex) {

                                System.out.println("Error redfri-> " + ex);

                            }
                        }
                }//Fin de red fria
                
                 }
                   if (Ban2 == 3) {
                if (TipoInsumo.equals("3")) {
                System.out.println("ape");
                 RedFria = "image/Nored_fria.jpg";
                   ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                            try {
                                exporter.exportReport();
                            } catch (Exception ex) {

                                System.out.println("Error ape-> " + ex);

                            }
                        }
                }//Fin de ape
               
                   }
                  if (Ban2 == 4) {   
                if (TipoInsumo.equals("4")) {
                System.out.println("controlado");
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                            try {
                                exporter.exportReport();
                            } catch (Exception ex) {

                                System.out.println("Error controlado-> " + ex);

                            }
                        }
                }//Fin de controlado
                
                  }
                 if (Ban2 == 6) {
                     if (TipoInsumo.equals("6")) {
                     System.out.println("fonsabi seco");
                       ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                             parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }//fin de fonsabi
                        if (TipoInsumo.equals("2")) {
                        System.out.println("fonsabi RF");
                       Imgape = "image/no_imgape.jpg";
                         ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                             parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }//fin de fonsabi red fria
                   if (TipoInsumo.equals("3")) {
                   System.out.println("fonsabi APE");
                     RedFria = "image/Nored_fria.jpg";
                       ImagenControlado = "image/NoControlado.jpg";
            CargoResponsable = " ";
            NombreResponsable = " ";
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }//fin de fonsabi ape        
                
                   if (TipoInsumo.equals("4")) {
                   System.out.println("fonsabi 4");
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                             parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }//fin de fonsabi controlado   
                   
                 } 
                 if (Ban2 == 7) {
                   
                   if (TipoInsumo.equals("7")) {
                    System.out.println("onco ape");
                        con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                            parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }//fin de onco ape        
                
                   
                 }if (Ban2 == 8) {
               
                             if (TipoInsumo.equals("8")) {
                               System.out.println("onco red fria");
                                con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                                for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
                            parameters.put("Imgape", Imgape);
                            parameters.put("RedFria", RedFria);
                            parameters.put("TipoInsumo", TipoInsumo);
                             parameters.put("BanTip", Ban2);
                            parameters.put("ImagenControlado", ImagenControlado);
                            parameters.put("CargoResponsable", CargoResponsable);
                            parameters.put("NombreResponsable", NombreResponsable);
                            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                        }
                }
                     }//fin de onco red fria
 

            }//FIN DEL PROYECTO 1
        }//FIN DEL CICLO FOLIOS C
        conexion.close();
                      /*ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon,  L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS, CONCAT('image/',CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur = 0 GROUP BY F.F_ClaPro ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                  
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) , Nomenclatura + "" + DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7), DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) ,ProyectoFactura , DatosFactMed.getString(21) , DatosFactMed.getString(23) , DatosFactMed.getString(24), NoImgApe, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }

         
                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;*/
                
              //  Ban = 1;
               

             
                    
            //*********normal **************
          
              
      /*        if ( TipoInsumo.equals("1") ) {
                    
                       System.out.println("entro con normal ");
                   // DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion NOT LIKE '%REDFRIA%' AND F.F_Ubicacion NOT LIKE 'APE%' AND F.F_Ubicacion NOT LIKE '%CONTROLADO%' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                 // ResultSet ContarDatos = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTARFACT, IFNULL(C.CONTARC, 0) AS CONTARREG, ( COUNT(*) - IFNULL(C.CONTARC, 0)) AS DIF FROM tb_factura F LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) AS CONTARC FROM tb_factura WHERE F_ClaDoc = '" + remis + "' AND F_ClaPro IN ('9999', '9998', '9996', '9995')) AS C ON F.F_ClaDoc = C.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "';");
               
                /*  if (ContarDatos.next()) {
                    Diferencia = ContarDatos.getInt(4);
                }*/
                
                /*while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                    //Origen = DatosFactMed.getInt("F_Origen");
                  
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    //F_Obs = DatosFactMed.getString("F_Obser");
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) ,DatosFactMed.getString(3), Nomenclatura + "" + DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24) , NoImgApe, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }
              
                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','TOTAL','','','" + TotalReq + "','" + TotalSur + "','','" + TotalMonto + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','" + Iva + "','Letra',0);");
                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;*/

                /*if (Diferencia == 0) {
                        Ban = 3;
                } else {
                      
                }
               */
                /*Establecemos la ruta del reporte*/
          /*        Ban = 1;
                if (Ban == 1) {
                    con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", TipoInsumo);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                } else if (Ban == 3) {/*Establecemos la ruta del reporte*/
                    //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
        /*           con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                    for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsemReceta.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", TipoInsumo);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        System.out.println(remis);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
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
                } 
                }
            
              
            ///////////CONTROLADO//////////
            //////////////////////////////
            
            if (TipoInsumo.equals("4")) {
                    System.out.println("entro con normal ");
               /* DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro LEFT JOIN tb_controlados ctr ON F.F_claPro = ctr.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion NOT LIKE '%REDFRIA%' AND F.F_Ubicacion NOT LIKE '%APE%' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                    //Origen = DatosFactMed.getInt("F_Origen");
                  
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    //F_Obs = DatosFactMed.getString("F_Obser");
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) ,DatosFactMed.getString(3), Nomenclatura + "" + DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24) , NoImgApe, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }
              
                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
               Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','TOTAL','','','" + TotalReq + "','" + TotalSur + "','','" + TotalMonto + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','" + Iva + "','Letra',0);");
                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;*/

               /*         Ban = 1;
               
                /*Establecemos la ruta del reporte*/
         /*       if (Ban == 1) {
                    con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis CONTROLADO-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", TipoInsumo);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                } 
            }
            }
            //*********Redfria
            if (Ban2 == 2) {
                 if (TipoInsumo.equals("2")) { 
          
                RedFria = "image/red_fria.jpg";
                Imgape = "image/no_imgape.jpg";
                
                /*con.actualizar("DELETE FROM tb_imprefolio WHERE F_User='" + User + "';");
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                //con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                
                
                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion LIKE '%REDFRIA%' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                  
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    //F_Obs = DatosFactMed.getString("F_Obser");
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con,DatosFactMed.getString(1), DatosFactMed.getString(2), DatosFactMed.getString(3) , Nomenclatura + "" + DatosFactMed.getString(4) + "-RF", DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24), NoImgApe, Encabezado , DatosFactMed.getString(26) , remis ,"0");
                }
                if (SumaMedSurT > 0) {
                    //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','SubTotal Medicamento (2504)','','','" + SumaMedReqT + "','" + SumaMedSurT + "','','" + MontoTMed + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','','',0);");
                }
                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "-RF' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','TOTAL','','','" + TotalReq + "','" + TotalSur + "','','" + TotalMonto + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','" + Iva + "','Letra',0);");
                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;*/
/*
                 Ban = 1;
               con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                if (Ban == 1) {
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("Imgape", Imgape);
                        parameters.put("TipoInsumo", TipoInsumo);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                }
            }
            }
            //*********APE
            if (Ban2 == 3) {
                 if (TipoInsumo.equals("3")) { 
          
                RedFria = "image/Nored_fria.jpg";
                Imgape = "image/imgape.png";
                
                /*con.actualizar("DELETE FROM tb_imprefolio WHERE F_User='" + User + "';");
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                //con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                
                
                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion LIKE '%APE%' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                   
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) , Nomenclatura + "" + DatosFactMed.getString(4) + "-APE", DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24) , Imgape, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }
                if (SumaMedSurT > 0) {
                }
                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "-APE' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;*/

               
  /*                      Ban = 1;
                 
                if (Ban == 1) {
                    con.actualizar("INSERT INTO tb_registrofoliosimpresos VALUES (0, '" + remis + "', '" + ProyectoF + "', NOW(), '" + sesion.getAttribute("nombre") + "', '" + TipoInsumo + "', '" + Impresora + "');");
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("Imgape", Imgape);
                        parameters.put("TipoInsumo", TipoInsumo);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                } 
            }
        }       
     //}           /************************INSABI Y MAS*********************************/
 /*  else {
             
         System.out.println("Iinea por insabi");
            if (Ban2 == 1) {
*/        /*         if (TipoInsumo.equals("0")  || TipoInsumo.equals("4") ) {/*
//                RedFria = "Nored_fria.jpg";
                System.out.println("Aqui ando");
                ResultSet ContarDatos = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTARFACT, IFNULL(C.CONTARC, 0) AS CONTARREG, ( COUNT(*) - IFNULL(C.CONTARC, 0)) AS DIF FROM tb_factura F LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) AS CONTARC FROM tb_factura WHERE F_ClaDoc = '" + remis + "' AND F_ClaPro = 9999 ) AS C ON F.F_ClaDoc = C.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "';");
                if (ContarDatos.next()) {
                    Diferencia = ContarDatos.getInt(4);
                }

                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion != 'REDFRIA' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                   
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) , Nomenclatura + "" + DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24) , NoImgApe, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }

                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                 SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;


                if (Diferencia == 0) {
                        Ban = 3;
                } else {
                        Ban = 1;
                     }
              

            
                if (Ban == 1) {   */
           /*         for (int x = 0; x < Copy; x++) {
                        System.out.println("remis contrfon-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", Nomenclatura + remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
                        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

                        try {
                            exporter.exportReport();
                        } catch (Exception ex) {

                            System.out.println("Error con fon-> " + ex);

                        }
                    }
             } /*else if (Ban == 3) {
                    //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
                    for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsemReceta.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", Nomenclatura + remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        System.out.println(remis);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                } 
            }
                 
            }
            if (Ban2 == 2) {
                
                 if (TipoInsumo.equals("0")  || TipoInsumo.equals("2") ) {
                RedFria = "image/red_fria.jpg";
                
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_User='" + User + "';");
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                
                
                
                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR( IFNULL( REPLACE (CPR.F_DesPro, '\n', ' '), M.F_DesPro ), 1, 333 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, M.F_PrePro) AS Presentacion, F.F_Contrato, F.F_OC, PS.F_DesProy AS ProyectoFact, J.F_DesJurIS, MU.F_DesMunIS,CONCAT('image/', CASE WHEN RF.F_ClaPro IS NOT NULL THEN 'red_fria.jpg' ELSE 'Nored_fria.jpg' END) AS REDFRI, F.F_Cause, U.F_Clues FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto INNER JOIN tb_proyectos PS ON F.F_Proyecto = PS.F_Id LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis MU ON U.F_ClaMun = MU.F_ClaMunIS AND U.F_ClaJur = MU.F_JurMunIS LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro WHERE F_ClaDoc = '" + remis + "'  AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' AND F_CantSur > 0 AND F.F_Ubicacion = 'REDFRIA' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad ORDER BY REDFRI ASC, F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;
                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(21);
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) , Nomenclatura + "" + DatosFactMed.getString(4) + "-RF", DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11) , String.valueOf(Costo) , df.format(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , usua , DatosFactMed.getString(21) ,"","","","","", DatosFactMed.getString(18) , DatosFactMed.getString(19) , DatosFactMed.getString(20) , ProyectoFactura , DatosFactMed.getString(22) , DatosFactMed.getString(23) , DatosFactMed.getString(24) , NoImgApe, Encabezado , DatosFactMed.getString(26) , remis , "0");
                }
                 TotalReq = SumaMatReqT + SumaMedReqT;
                TotalSur = SumaMedSurT + SumaMatSurT;
                TotalMonto = MontoTMat + MontoTMed;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + df.format(TotalMonto) + "',F_MontoT='" + df.format(MTotalMonto) + "',F_Iva='" + df.format(Iva) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + Nomenclatura + "" + remis + "-RF' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                SumaMedReq = 0;
                SumaMedSur = 0;
                MontoMed = 0.0;
                SumaMedReqT = 0;
                SumaMedSurT = 0;
                MontoTMed = 0.0;

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;

                        Ban = 1;
                   

         
                if (Ban == 1) {
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", Nomenclatura + remis+"-RF");
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
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
                    }
                } 
               }
            }
        }*/
    //}
   // }
   // conexion.close();

%>
<script type="text/javascript">


    var ventana = window.self;
    ventana.opener = window.self;
    setTimeout("window.close()", 5000);

</script>
