<%-- 
    Document   : ReporteVertical
    Created on : 6/01/2015, 03:09:40 PM
    Author     : Sistemas
--%>

<%@page import="conn.ConectionDBTrans"%>
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
    String DesV="",F_Region="",F_DesJur="",F_DesMun="",F_DesLoc="",F_DesUni="",F_Fecha1="",F_Fecha2="",F_Serie1="",F_Serie2="",F_Provee="",F_Surtido="",F_Coberturas="",F_Suministro="";
    int RegistroC=0,RegistroC2=0,Ban=0;
    double Hoja=0.0;
    
    ResultSet folio = null;
    ResultSet Contare = null;
    try {
//FolCon = request.getParameter("FolCon");
        F_User = request.getParameter("User");
        FolCon = request.getParameter("FolCon");
    } catch (Exception e) {
    }
    String path = getServletContext().getRealPath("/");
    String F_Imagen = path + "imagenes\\savi1.jpg";
    //out.println(F_Imagen);
%>
<html>
    <%
         ConectionDBTrans conn = new ConectionDBTrans();
        Contare =  conn.consulta("SELECT COUNT(F_FolCon),F_Region,F_DesJur,F_DesMun,F_DesLoc,F_DesUni,F_Fecha1,F_Fecha2,F_Serie1,F_Serie2,F_Provee,F_Surtido,F_Coberturas,F_Suministro FROM tb_imprepconfarm WHERE F_FolCon='"+FolCon+"'");
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
            if(RegistroC <= 23){
                Ban = 1;
                DesV="1 Hoja";
            }else{
                if(RegistroC >= 27){
                    Hoja = RegistroC / 27;
                    System.out.println("Hojas: "+Hoja);
                    int Hoja2 =(int) Hoja * 27;
                    RegistroC2 = RegistroC - Hoja2;
                    if (RegistroC2 >0 && RegistroC2 <= 23){
                        DesV="Mas Hoja decimal";
                        Ban = 1;
                    }else{
                        DesV="Mas Hoja decimal Mayor13";
                        Ban = 2;
                    }               
                }else{
                    DesV = "2 hojas";
                    Ban = 2;
                }
            }
             System.out.println("Re: "+RegistroC+" Ban: "+Ban+" DesV "+DesV+" Hoja "+Hoja);
            Hoja=0;
            
            if(Ban == 1){
                File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVerticalFarm.jasper"));
                Map parameter = new HashMap();
                parameter.put("FolCon", FolCon);

                byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn.getConn());

                response.setContentType("application/pdf");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);

                outputStream.flush();
                outputStream.close();

                System.out.println("Folio Vertical2-->" + FolCon);
            }else{
                File reportfile = new File(application.getRealPath("/ReportesPuntos/RepVerticalFarm2.jasper"));
                Map parameter = new HashMap();
                parameter.put("FolCon", FolCon);
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
                byte[] bytes = JasperRunManager.runReportToPdf(reportfile.getPath(), parameter, conn.getConn());

                response.setContentType("application/pdf");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);

                outputStream.flush();
                outputStream.close();

                System.out.println("Folio Vertical2-->" + FolCon);
            }
            
        

        conn.cierraConexion();
    %>
 <head>
        <!--script type="text/javascript">

            var ventana = window.self;
            ventana.opener = window.self;
            setTimeout("window.close()", 1000);

        </script-->
    </head>    
</html>
