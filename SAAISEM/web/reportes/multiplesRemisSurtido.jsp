<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>

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
<% /*Parametros para realizar la conexión*/

    HttpSession sesion = request.getSession();
    ConectionDB con = new ConectionDB();
    String usua = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        //response.sendRedirect("index.jsp");
    }
    int RegistroC = 0, Ban = 0, HojasC = 0, HojasR = 0, Origen = 0;
    String DesV = "", remis = "", ProyectoF = "", ProyectoFactura = "", Proyecto = "", Letra = "";
    double Hoja = 0.0, Hoja2 = 0.0, Costo = 0.0, Iva = 0.0, MTotalMonto = 0.0;
    String User = request.getParameter("User");
    String Impresora = request.getParameter("Impresora");
    int Copy = 0;

    Connection conexion;
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
    ResultSet FoliosC = con.consulta("SELECT F_Folio, F_Copy, U.F_Proyecto, F.F_Proyecto AS F_ProyectoF FROM tb_folioimp F INNER JOIN tb_factura FT ON F.F_Folio = FT.F_ClaDoc AND F.F_Proyecto = FT.F_Proyecto INNER JOIN tb_uniatn U ON FT.F_ClaCli = U.F_ClaCli WHERE F.F_User = '" + User + "' GROUP BY F.F_Folio, F.F_Proyecto ORDER BY F_Folio + 0;");
    while (FoliosC.next()) {
        remis = FoliosC.getString(1);
        Copy = FoliosC.getInt(2);
        ProyectoF = FoliosC.getString(3);
        ProyectoFactura = FoliosC.getString(4);

        con.actualizar("DELETE FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "' AND F_User='" + User + "'");
        int SumaMedReq = 0, SumaMedSur = 0, SumaMedReqT = 0, SumaMedSurT = 0;
        double MontoMed = 0.0, MontoTMed = 0.0;
        String Unidad = "", Fecha = "", Direc = "", F_FecApl = "", F_Obs = "", F_Obs2 = "", Razon = "";
        int SumaMatReq = 0, SumaMatSur = 0, SumaMatReqT = 0, SumaMatSurT = 0;
        double MontoMat = 0.0, MontoTMat = 0.0;

        int TotalReq = 0, TotalSur = 0;
        double TotalMonto = 0.0;

        ResultSet ObsFact = con.consulta("SELECT F_Obser FROM tb_obserfact WHERE F_IdFact='" + remis + "' AND F_Proyecto = '" + ProyectoFactura + "' GROUP BY F_IdFact;");
        while (ObsFact.next()) {
            F_Obs = ObsFact.getString(1);
        }
        int TotMed = 0, TotMat = 0;

        ResultSet FactMed = con.consulta("SELECT SUM(F.F_CantSur) AS F_CantSur FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro WHERE F_ClaDoc = '" + remis + "' AND F_TipMed = '2504' AND F_CantSur > 0 AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "';");
        if (FactMed.next()) {
            TotMed = FactMed.getInt(1);
        }

        ResultSet FactMat = con.consulta("SELECT SUM(F.F_CantSur) AS F_CantSur FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro WHERE F_ClaDoc='" + remis + "' and F_TipMed='2505' and F_CantSur>0 and F_DocAnt !='1' AND F.F_Proyecto = '" + ProyectoFactura + "';");
        if (FactMat.next()) {
            TotMat = FactMat.getInt(1);
        }

        if (TotMed > 0) {
            con.actualizar("DELETE FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "' AND F_User='" + User + "'");
            if ((ProyectoF.equals("1")) || (ProyectoF.equals("4")) || (ProyectoF.equals("5")) || (ProyectoF.equals("6")) || (ProyectoF.equals("7"))) {
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "' AND F_User='" + User + "'");
                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, IFNULL(CPR.F_DesPro, M.F_DesPro) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Origen, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, '') AS Presentacion, F.F_Contrato, F.F_OC FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto WHERE F_ClaDoc = '" + remis + "' AND F_TipMed = '2504' AND F_CantSur > 0 AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen ORDER BY F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    while (DatosFactMed.next()) {
                        SumaMedReq = DatosFactMed.getInt("F_CantReq");
                        SumaMedSur = DatosFactMed.getInt("F_CantSur");
                        Origen = DatosFactMed.getInt("F_Origen");
                        if (Origen == 1) {
                            MontoMed = 0;
                            Costo = 0;
                        } else {
                            MontoMed = DatosFactMed.getDouble("F_Monto");
                            Costo = DatosFactMed.getDouble("F_Costo");
                        }
                        SumaMedReqT = SumaMedReqT + SumaMedReq;
                        SumaMedSurT = SumaMedSurT + SumaMedSur;

                        Unidad = DatosFactMed.getString("F_NomCli");
                        Direc = DatosFactMed.getString("F_Direc");
                        Fecha = DatosFactMed.getString("F_FecEnt");
                        F_FecApl = DatosFactMed.getString("F_FecApl");
                        Razon = DatosFactMed.getString(15);
                        Proyecto = DatosFactMed.getString(18);
                        //F_Obs = DatosFactMed.getString("F_Obser");
                        MontoTMed = MontoTMed + MontoMed;
                        InsertImpreFolio.instance().insert(con,DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) , DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11), String.valueOf(Costo) , String.valueOf(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , User , DatosFactMed.getString(18) ,"","","","","", DatosFactMed.getString(19) , DatosFactMed.getString(20) , DatosFactMed.getString(21) , ProyectoFactura , "0");
                    }
                    TotalReq = SumaMedReqT;
                    TotalSur = SumaMatSurT;
                    TotalMonto = MontoTMed;
                    //Iva = MontoTMat * 0.16;

                    MTotalMonto = TotalMonto + Iva;
                    Numero_a_Letra NumLetra = new Numero_a_Letra();
                    String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                    boolean band = true;
                    Letra = NumLetra.Convertir(numero, band);

                    con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + (String.format("%.2f", TotalMonto)) + "',F_MontoT='" + numero + "',F_Iva='" + (String.format("%.2f", Iva)) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                    //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','TOTAL','','','" + TotalReq + "','" + TotalSur + "','','" + TotalMonto + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','" + Iva + "','Letra',0);");
                    SumaMedReq = 0;
                    SumaMedSur = 0;
                    MontoMed = 0.0;
                    SumaMedReqT = 0;
                    SumaMedSurT = 0;
                    MontoTMed = 0.0;

                    ResultSet Contare = con.consulta("SELECT COUNT(F_ClaDoc),F_Obs FROM tb_imprefolio WHERE F_ClaDoc = '" + remis + "' AND F_User = '" + usua + "' AND F_ProyectoF = '" + ProyectoFactura + "';");
                    if (Contare.next()) {
                        RegistroC = Contare.getInt(1);
                    }

                    Hoja = RegistroC * 1.0 / 13;
                    Hoja2 = RegistroC / 13;
                    HojasC = (int) Hoja2 * 13;

                    HojasR = RegistroC - HojasC;

                    if ((HojasR > 0) && (HojasR <= 8)) {
                        Ban = 1;
                    } else {
                        Ban = 2;
                    }
                    System.out.println("Re: " + RegistroC + " Ban: " + Ban + " Hoja2 " + Hoja2 + " HojaC " + HojasC + " HohasR " + HojasR);
                    Hoja = 0;

                    /*Establecemos la ruta del reporte*/
                    if (Ban == 1) {
                        for (int x = 0; x < Copy; x++) {
                            System.out.println("remis-> " + remis);
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);
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
                    } else {

                        /*Establecemos la ruta del reporte*/
                        //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
                        for (int x = 0; x < Copy; x++) {
                            File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem2.jasper"));
                            Map parameters = new HashMap();
                            parameters.put("Folfact", remis);
                            parameters.put("Usuario", User);
                            parameters.put("F_Obs", F_Obs);

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
            } else {

                ResultSet DatosFactMed = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR(M.F_DesPro, 1, 40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, SUM(F.F_Costo) AS F_Costo, SUM(F.F_Monto) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Origen, L.F_Proyecto, P.F_DesProy FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto=P.F_Id WHERE F_ClaDoc='" + remis + "' and F_TipMed='2504' and F_CantSur>0 and F_DocAnt !='1' AND F.F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen ORDER BY F.F_ClaPro + 0;");
                while (DatosFactMed.next()) {
                    SumaMedReq = DatosFactMed.getInt("F_CantReq");
                    SumaMedSur = DatosFactMed.getInt("F_CantSur");
                    Origen = DatosFactMed.getInt("F_Origen");
                    if (Origen == 1) {
                        MontoMed = 0;
                        Costo = 0;
                    } else {
                        // todo costo a cero por eso se comenta sólo para michoacán
                        //MontoMed = DatosFactMed.getDouble("F_Monto");
                        //Costo = DatosFactMed.getDouble("F_Costo");
                        
                        MontoMed = 0;
                        Costo = 0;
                    }
                    SumaMedReqT = SumaMedReqT + SumaMedReq;
                    SumaMedSurT = SumaMedSurT + SumaMedSur;

                    Unidad = DatosFactMed.getString("F_NomCli");
                    Direc = DatosFactMed.getString("F_Direc");
                    Fecha = DatosFactMed.getString("F_FecEnt");
                    F_FecApl = DatosFactMed.getString("F_FecApl");
                    Razon = DatosFactMed.getString(15);
                    Proyecto = DatosFactMed.getString(18);
                    //F_Obs = DatosFactMed.getString("F_Obser");
                    MontoTMed = MontoTMed + MontoMed;
                    InsertImpreFolio.instance().insert(con, DatosFactMed.getString(1) , DatosFactMed.getString(2) , DatosFactMed.getString(3) ,  DatosFactMed.getString(4) , DatosFactMed.getString(5) , DatosFactMed.getString(6) , DatosFactMed.getString(7) , DatosFactMed.getString(8) , DatosFactMed.getString(9) , DatosFactMed.getString(10) , DatosFactMed.getString(11), String.valueOf(Costo) , String.valueOf(MontoMed) , F_Obs , DatosFactMed.getString(14) , DatosFactMed.getString(15) , User , DatosFactMed.getString(18) ,"","","","","","","","", ProyectoFactura ,"0");
                }
                if (SumaMedSurT > 0) {
                    InsertImpreFolio.instance().insert(con,"",Unidad , Direc , remis , Fecha ,"","SubTotal Medicamento (2504)","","", String.valueOf(SumaMedReqT) , String.valueOf(SumaMedSurT) ,"", String.valueOf(MontoTMed) ,"", F_FecApl , Razon , User , Proyecto ,"","","","","","","","", ProyectoFactura ,"0");
                }

                TotalReq = SumaMedReqT;
                TotalSur = SumaMedSurT;
                TotalMonto = MontoTMed;

                InsertImpreFolio.instance().insert(con,"",Unidad , Direc , remis , Fecha ,"","TOTAL","","", String.valueOf(TotalReq) , String.valueOf(TotalSur) ,"", String.valueOf(TotalMonto) ,"", F_FecApl , Razon , User , Proyecto ,"","","","","","","","", ProyectoFactura ,"0");

                MTotalMonto = TotalMonto + Iva;
                boolean band = true;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                Letra = NumLetra.Convertir(numero, band);
                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + (String.format("%.2f", TotalMonto)) + "',F_MontoT='" + numero + "',F_Iva='" + (String.format("%.2f", Iva)) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc = '" + remis + "' AND F_User = '" + usua + "' AND F_ProyectoF = '" + ProyectoFactura + "';");

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

                ResultSet Contare = con.consulta("SELECT COUNT(F_ClaDoc),F_Obs FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "'  AND F_User='" + User + "' AND F_ProyectoF = '" + ProyectoFactura + "';");
                if (Contare.next()) {
                    RegistroC = Contare.getInt(1);
                }

                Hoja = RegistroC * 1.0 / 35;
                Hoja2 = RegistroC / 35;
                HojasC = (int) Hoja2 * 35;

                HojasR = RegistroC - HojasC;

                if ((HojasR > 0) && (HojasR <= 22)) {
                    Ban = 1;
                } else {
                    Ban = 2;
                }
                System.out.println("Re: " + RegistroC + " Ban: " + Ban + " Hoja2 " + Hoja2 + " HojaC " + HojasC + " HohasR " + HojasR);
                Hoja = 0;

                if (Ban == 1) {
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFolios.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
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
                } else {

                    /*Establecemos la ruta del reporte*/
                    //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
                    for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFolios2.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);

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
        }

        if (TotMat > 0) {
            con.actualizar("DELETE FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "' AND F_User='" + User + "'");
            if ((ProyectoF.equals("1")) || (ProyectoF.equals("4")) || (ProyectoF.equals("5")) || (ProyectoF.equals("6")) || (ProyectoF.equals("7"))) {

                con.actualizar("DELETE FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "' AND F_User='" + User + "'");

                ResultSet DatosFactMat = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, IFNULL(CPR.F_DesPro, M.F_DesPro) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, IFNULL(CPR.F_Costo, SUM(F.F_Costo)) AS F_Costo, IFNULL( CPR.F_Costo * SUM(F.F_CantSur), SUM(F.F_Monto)) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Origen, L.F_Proyecto, P.F_DesProy, IFNULL(CPR.F_Presentacion, '') AS Presentacion, F.F_Contrato, F.F_OC FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto = P.F_Id LEFT JOIN ( SELECT F_ClaPro, F_DesPro, F_Costo, F_Proyecto, F_Presentacion FROM tb_catalogoprecios ) AS CPR ON F.F_ClaPro = CPR.F_ClaPro AND L.F_Proyecto = CPR.F_Proyecto WHERE F_ClaDoc = '" + remis + "' AND F_TipMed = '2505' AND F_CantSur > 0 AND F_DocAnt != '1' AND F.F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen ORDER BY F.F_ClaPro + 0;");
                while (DatosFactMat.next()) {
                    SumaMatReq = DatosFactMat.getInt("F_CantReq");
                    SumaMatSur = DatosFactMat.getInt("F_CantSur");
                    Origen = DatosFactMat.getInt("F_Origen");
                    if (Origen == 1) {
                        MontoMat = 0;
                        Costo = 0;
                    } else {
                        MontoMat = DatosFactMat.getDouble("F_Monto");
                        Costo = DatosFactMat.getDouble("F_Costo");
                    }
                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                    Unidad = DatosFactMat.getString("F_NomCli");
                    Direc = DatosFactMat.getString("F_Direc");
                    Fecha = DatosFactMat.getString("F_FecEnt");
                    F_FecApl = DatosFactMat.getString("F_FecApl");
                    Razon = DatosFactMat.getString(15);
                    Proyecto = DatosFactMat.getString(18);
                    //F_Obs = DatosFactMat.getString("F_Obser");
                    
                    InsertImpreFolio.instance().insert(con, DatosFactMat.getString(1) , DatosFactMat.getString(2) , DatosFactMat.getString(3) ,  DatosFactMat.getString(4) , DatosFactMat.getString(5) , DatosFactMat.getString(6) , DatosFactMat.getString(7) , DatosFactMat.getString(8) , DatosFactMat.getString(9) , DatosFactMat.getString(10) , DatosFactMat.getString(11), String.valueOf(Costo) , String.valueOf(MontoMed) , F_Obs , DatosFactMat.getString(14) , DatosFactMat.getString(15) , usua , DatosFactMat.getString(18) ,"","","","","",DatosFactMat.getString(19),DatosFactMat.getString(20),DatosFactMat.getString(21), ProyectoFactura ,"0");
                }
                if (SumaMatSurT > 0) {
                    //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','SubTotal Mat. Curación (2505)','','','" + SumaMatReqT + "','" + SumaMatSurT + "','','" + MontoTMat + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','','',0);");
                }
                TotalReq = SumaMatReqT;
                TotalSur = SumaMatSurT;
                TotalMonto = MontoTMat;
                Iva = MontoTMat * 0.16;

                MTotalMonto = TotalMonto + Iva;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                boolean band = true;
                Letra = NumLetra.Convertir(numero, band);

                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + (String.format("%.2f", TotalMonto)) + "',F_MontoT='" + numero + "',F_Iva='" + (String.format("%.2f", Iva)) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc='" + remis + "' AND F_User='" + usua + "' AND F_ProyectoF='" + ProyectoFactura + "';");

                //con.actualizar("INSERT INTO tb_imprefolio VALUES('','" + Unidad + "','" + Direc + "','" + remis + "','" + Fecha + "','','TOTAL','','','" + TotalReq + "','" + TotalSur + "','','" + TotalMonto + "','','" + F_FecApl + "','" + Razon + "','" + usua + "','" + Proyecto + "','" + Iva + "','Letra',0);");
                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;

                ResultSet Contare = con.consulta("SELECT COUNT(F_ClaDoc),F_Obs FROM tb_imprefolio WHERE F_ClaDoc = '" + remis + "' AND F_User = '" + usua + "' AND F_ProyectoF = '" + ProyectoFactura + "';");
                if (Contare.next()) {
                    RegistroC = Contare.getInt(1);
                }

                Hoja = RegistroC * 1.0 / 13;
                Hoja2 = RegistroC / 13;
                HojasC = (int) Hoja2 * 13;

                HojasR = RegistroC - HojasC;

                if ((HojasR > 0) && (HojasR <= 8)) {
                    Ban = 1;
                } else {
                    Ban = 2;
                }
                System.out.println("Re: " + RegistroC + " Ban: " + Ban + " Hoja2 " + Hoja2 + " HojaC " + HojasC + " HohasR " + HojasR);
                Hoja = 0;

                /*Establecemos la ruta del reporte*/
                if (Ban == 1) {
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
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
                } else {

                    /*Establecemos la ruta del reporte*/
                    //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
                    for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFoliosIsem2.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);

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
            } else {

                ResultSet DatosFactMat = con.consulta("SELECT F.F_ClaCli, U.F_NomCli, U.F_Direc, F.F_ClaDoc, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, F.F_ClaPro, SUBSTR(M.F_DesPro, 1, 40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantReq) AS F_CantReq, SUM(F.F_CantSur) AS F_CantSur, SUM(F.F_Costo) AS F_Costo, SUM(F.F_Monto) AS F_Monto, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, U.F_Razon, L.F_Origen, L.F_Proyecto, P.F_DesProy FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_proyectos P ON U.F_Proyecto=P.F_Id WHERE F_ClaDoc='" + remis + "' and F_TipMed='2505' and F_CantSur>0 and F_DocAnt !='1' AND F.F_Proyecto = '" + ProyectoFactura + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen ORDER BY F.F_ClaPro + 0;");
                while (DatosFactMat.next()) {
                    SumaMatReq = DatosFactMat.getInt("F_CantReq");
                    SumaMatSur = DatosFactMat.getInt("F_CantSur");
                    Origen = DatosFactMat.getInt("F_Origen");
                    if (Origen == 1) {
                        MontoMed = 0;
                        Costo = 0;
                    } else {
                        MontoMed = DatosFactMat.getDouble("F_Monto");
                        Costo = DatosFactMat.getDouble("F_Costo");
                    }
                    SumaMatReqT = SumaMatReqT + SumaMatReq;
                    SumaMatSurT = SumaMatSurT + SumaMatSur;
                    MontoTMat = MontoTMat + MontoMat;

                    Unidad = DatosFactMat.getString("F_NomCli");
                    Direc = DatosFactMat.getString("F_Direc");
                    Fecha = DatosFactMat.getString("F_FecEnt");
                    F_FecApl = DatosFactMat.getString("F_FecApl");
                    Razon = DatosFactMat.getString(15);
                    Proyecto = DatosFactMat.getString(18);
                    //F_Obs = DatosFactMat.getString("F_Obser");

                    InsertImpreFolio.instance().insert(con, DatosFactMat.getString(1) , DatosFactMat.getString(2) , DatosFactMat.getString(3) ,  DatosFactMat.getString(4) , DatosFactMat.getString(5) , DatosFactMat.getString(6) , DatosFactMat.getString(7) , DatosFactMat.getString(8) , DatosFactMat.getString(9) , DatosFactMat.getString(10) , DatosFactMat.getString(11), String.valueOf(Costo) , String.valueOf(MontoMed) , F_Obs , DatosFactMat.getString(14) , DatosFactMat.getString(15) , User , DatosFactMat.getString(18) ,"","","","","","","","", ProyectoFactura ,"0");
                }
                if (SumaMatSurT > 0) {
                    InsertImpreFolio.instance().insert(con,"",Unidad , Direc , remis , Fecha ,"","SubTotal Mat. Curación (2505)","","", String.valueOf(SumaMatReqT) , String.valueOf(SumaMatSurT) ,"", String.valueOf(MontoTMat) ,"", F_FecApl , Razon , User , Proyecto ,"","","","","","","","", ProyectoFactura ,"0");
                } else {
                    /*for(int x=0; x<4; x++){
             con.actualizar("INSERT INTO tb_imprefolio VALUES('','','','"+remis+"','','','','','','','','','','',0)");   
                }*/
                }
                TotalReq = SumaMatReqT;
                TotalSur = SumaMatSurT;
                TotalMonto = MontoTMat;

                InsertImpreFolio.instance().insert(con,"",Unidad , Direc , remis , Fecha ,"","TOTAL","","", String.valueOf(TotalReq) , String.valueOf(TotalSur) ,"", String.valueOf(TotalMonto) ,"", F_FecApl , Razon , User , Proyecto ,"","","","","","","","", ProyectoFactura ,"0");

                MTotalMonto = TotalMonto + Iva;
                boolean band = true;
                Numero_a_Letra NumLetra = new Numero_a_Letra();
                String numero = String.valueOf(String.format("%.2f", MTotalMonto));
                Letra = NumLetra.Convertir(numero, band);
                con.actualizar("UPDATE tb_imprefolio SET F_Piezas='" + TotalSur + "',F_Subtotal='" + (String.format("%.2f", TotalMonto)) + "',F_MontoT='" + numero + "',F_Iva='" + (String.format("%.2f", Iva)) + "',F_Letra='" + Letra + "' WHERE F_ClaDoc = '" + remis + "' AND F_User = '" + usua + "' AND F_ProyectoF = '" + ProyectoFactura + "';");

                SumaMatReq = 0;
                SumaMatSur = 0;
                MontoMat = 0.0;
                SumaMatReqT = 0;
                SumaMatSurT = 0;
                MontoTMat = 0.0;

                ResultSet Contare = con.consulta("SELECT COUNT(F_ClaDoc),F_Obs FROM tb_imprefolio WHERE F_ClaDoc='" + remis + "'  AND F_User='" + User + "' AND F_ProyectoF = '" + ProyectoFactura + "';");
                if (Contare.next()) {
                    RegistroC = Contare.getInt(1);
                }

                Hoja = RegistroC * 1.0 / 35;
                Hoja2 = RegistroC / 35;
                HojasC = (int) Hoja2 * 35;

                HojasR = RegistroC - HojasC;

                if ((HojasR > 0) && (HojasR <= 22)) {
                    Ban = 1;
                } else {
                    Ban = 2;
                }
                System.out.println("Re: " + RegistroC + " Ban: " + Ban + " Hoja2 " + Hoja2 + " HojaC " + HojasC + " HohasR " + HojasR);
                Hoja = 0;

                if (Ban == 1) {
                    for (int x = 0; x < Copy; x++) {
                        System.out.println("remis-> " + remis);
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFolios.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);
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
                } else {

                    /*Establecemos la ruta del reporte*/
                    //File reportFile = new File(application.getRealPath("reportes/multiplesRemis.jasper"));
                    for (int x = 0; x < Copy; x++) {
                        File reportFile = new File(application.getRealPath("/reportes/ImprimeFolios2.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", User);
                        parameters.put("F_Obs", F_Obs);

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
        }
    }
    conexion.close();

%>
<script type="text/javascript">

    var ventana = window.self;
    ventana.opener = window.self;
    setTimeout("window.close()", 5000);

</script>
