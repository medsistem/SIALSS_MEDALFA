<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>

<%@page import="conn.*" %>
<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.lang.*" import="java.util.*" import= "javax.swing.*" import="java.io.*" import="java.text.DateFormat" import="java.text.ParseException" import="java.text.SimpleDateFormat" import="java.util.Calendar" import="java.util.Date"  import="java.text.NumberFormat" import="java.util.Locale" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns:v="urn:schemas-microsoft-com:vml"
      xmlns:o="urn:schemas-microsoft-com:office:office"
      xmlns:x="urn:schemas-microsoft-com:office:excel"
      xmlns="http://www.w3.org/TR/REC-html40">
    <%

        ConectionDB con = new ConectionDB();
        NumberFormat nf1 = NumberFormat.getInstance(Locale.US);

        ResultSet rset, rset2, rset3, rset4, rset5;
        String Clave = "", Claves = "", clave = "", provee = "", nomprovee = "", fecha = "", orden = "", Exist = "";
        int exist1 = 0, exist2 = 0, existr = 0, existt = 0, cont = 0, inventario = 0, inventariof = 0;

        String totPzs = "";
        String totMon = "";
        String totPzsMed = "";
        String totMonMed = "";
        String totPzsMat = "";
        String totMonMat = "";
        String fecha_1 = "";
        String fecha_2 = "";
        int total_pzs = 0, total_medi = 0, total_cura = 0;
        double total_mon_medi = 0.0, total_mon_cura = 0.0, total_montos = 0.0, total_tot = 0.0, montos_res = 0.0;
        String ixt_cad = "", ixt_cad2 = "";
        int cant_pzs = 0, ixt_uni = 0;
        double cant_totMon = 0.0, ixt_costo = 0.0;
        String only = "";

        try {
            fecha = request.getParameter("fecha");
            provee = request.getParameter("provee");
        } catch (Exception e) {
        }

        String but = "r";

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=Reporte por Fecha Proveedor.xls");

    //ingresar un try y realizar una sola conexión
        try {
            con.conectar();


    %>
    <head>
        <meta http-equiv=Content-Type content="text/html; charset=windows-1252">
            <meta name=ProgId content=Excel.Sheet>
                <meta name=Generator content="Microsoft Excel 12">
                    <link id=Main-File rel=Main-File href="../Libro1.htm">
                        <link rel=File-List href=filelist.xml>
                            <!--link rel=Stylesheet href=stylesheet.css-->
                            <style> 
                                <!--table
                                {mso-displayed-decimal-separator:"\.";
                                 mso-displayed-thousand-separator:"\,";}
                                @page
                                {margin:.75in .7in .75in .7in;
                                 mso-header-margin:.3in;
                                 mso-footer-margin:.3in;
                                 mso-page-orientation:landscape;}
                                .style1 {color: #FF0000}
                                -->
                            </style>
                            <![if !supportTabStrip]><script language="JavaScript">
                            
                                    if (parent.window.g_iIEVer >= 4) {
                                        if (parent.document.readyState == "complete"
                                                && parent.frames['frTabs'].document.readyState == "complete")
                                            parent.fnSetActiveSheet(0);
                                        else
                                            window.setTimeout("fnUpdateTabs();", 150);
                                    }
                      

                                if (window.name != "frSheet")
                                    window.location.replace("../Libro1.htm");
                                else
                                    fnUpdateTabs();
                       
                            </script>
                            <![endif]>
                            </head>

                            <body link=blue vlink=purple>
                                <table>
                                    <%
                                        Date dNow = new Date();
                                        DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                                        String fechaDia = ft.format(dNow);
                                    %>
                                    <tr>
                                        <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"></td>
                                        <th colspan="8"><h4><%=fechaDia%></h4></th>
                                  
                                    <tr>
                                        <th colspan="9"> <h1> Reporte por Fecha Proveedor</h1></th>
                                    </tr>
                                
                                </table>

                                <table border=1 cellpadding=0 cellspacing=0 width=439 style='border-collapse: collapse;table-layout:fixed;width:330pt'>
                                    <tr height=20 style='height:15.0pt'>  
                                       
                                        <th class=xl66>Proveedor</th>
                                        <th class=xl66>Claves</th>
                                        <th class=xl66>Lote</th>
                                        <th class=xl66>Caducidad</th>
                                        <th class=xl66>Piezas</th>
                                        <th class=xl66>O.C.</th>
                                        <th class=xl66>No. Ingreso</th>
                                        <th class=xl66>Remisión</th>
                                        <th class=xl66>Usuario Captura</th>
                                        <th class=xl66>Fecha de Captura</th>
                                        <th class=xl66>Hora de Captura</th> 
                                        <th class=xl66>Usuario Ingreso</th>
                                        <th class=xl66>Fecha de Ingreso</th> 
                                        <th class=xl66>Hora de Ingreso</th> 
                                        <th class=xl66>O.S.</th> >
                                        <th class=xl66>Fuente Finanzamiento</th> >
                                    </tr>
                                    <tr height=20 style='height:15.0pt'>

                                        <%     if ((provee.equals("")) && (fecha.equals(""))) {
                                                rset = con.consulta("SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(DATE_FORMAT( F_FecCaptura, '%d/%m/%Y' ), '') AS 'fecCaptura', IFNULL(c.F_UserIngreso, '') AS userVal, c.F_Hora, IFNULL(c.F_HoraCaptura, '') AS horaCaptura, IFNULL(c.F_OrdenSuministro,'') AS os, c.F_FuenteFinanza FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id WHERE c.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto; ");
                                            } else if (!(provee.equals("")) && (fecha.equals(""))) {
                                                rset = con.consulta("SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(DATE_FORMAT( F_FecCaptura, '%d/%m/%Y' ), '') AS 'fecCaptura', IFNULL(c.F_UserIngreso, '') AS userVal, c.F_Hora, IFNULL(c.F_HoraCaptura, '') AS horaCaptura, IFNULL(c.F_OrdenSuministro,'') AS os, c.F_FuenteFinanza FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id where c.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND p.F_ClaProve='" + provee + "' GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                            } else {
                                                rset = con.consulta("SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(DATE_FORMAT( F_FecCaptura, '%d/%m/%Y' ), '') AS 'fecCaptura', IFNULL(c.F_UserIngreso, '') AS userVal, c.F_Hora, IFNULL(c.F_HoraCaptura, '') AS horaCaptura, IFNULL(c.F_OrdenSuministro,'') AS os, c.F_FuenteFinanza FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id where c.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND c.F_FecApl='" + fecha + "' GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                            }
                                            while (rset.next()) {
                                        %>
                                     
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(1)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(2)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(3)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(4)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=nf1.format(rset.getInt(5))%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(7)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(8)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(9)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(10)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("fecCaptura")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("horaCaptura")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("userVal")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(6)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_Hora")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("os")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_FuenteFinanza")%></td>
                                    </tr>
                                    <% } %>





                                    <![if supportMisalignedColumns]>
                                    <tr height=0 style='display:none'>
                                        <td width=120 style='width:90pt'></td>
                                        <td width=117 style='width:88pt'></td>
                                        <td width=101 style='width:76pt'></td>
                                        <td width=101 style='width:76pt'></td>
                                        <td width=101 style='width:76pt'></td>
                                    </tr>
                                    <![endif]>
                                </table>

                            </body>

                            </html>
                            <%
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            %>