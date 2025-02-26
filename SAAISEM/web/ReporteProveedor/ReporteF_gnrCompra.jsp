<%@page import="com.gnk.util.ParameterUtils"%>
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
        DecimalFormat formatterD = new DecimalFormat("#,###,###.00");

        ResultSet rset;

        String fechaInicial = "";
        String fechaFinal = "";
        String Proveedor = "";
        String todo = "";
        String andString = "c.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) ";
        fechaInicial = ParameterUtils.getParameter("fechaInicial", request);
        fechaFinal = ParameterUtils.getParameter("fechaFinal", request);

        Proveedor = ParameterUtils.getParameter("proveedor", request);
        todo = ParameterUtils.getParameter("todo", request);

        String clave = ParameterUtils.getParameter("clave", request);
        String lote = ParameterUtils.getParameter("lote", request);
        String remision = ParameterUtils.getParameter("remision", request);
        String oc = ParameterUtils.getParameter("oc", request);

        String but = "r";

        if (!Proveedor.isEmpty()) {
            andString = "p.F_ClaProve='" + Proveedor + "' ";
        }
        if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
            andString = andString.isEmpty() ? "c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "'" : andString + "AND c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "' ";
        }
        if (!clave.isEmpty()) {
            andString = andString.isEmpty() ? "c.F_ClaPro = '" + clave + "' " : andString + "AND c.F_ClaPro = '" + clave + "' ";
        }
        if (!lote.isEmpty()) {
            andString = andString.isEmpty() ? "l.F_ClaLot = '" + lote + "' " : andString + "AND l.F_ClaLot = '" + lote + "' ";
        }
        if (!oc.isEmpty()) {
            andString = andString.isEmpty() ? "c.F_OrdCom = '" + oc + "' " : andString + "AND c.F_OrdCom = '" + oc + "' ";
        }
        if (!remision.isEmpty()) {
            andString = andString.isEmpty() ? "c.F_FolRemi = '" + remision + "' " : andString + "AND c.F_FolRemi = '" + remision + "' ";
        }

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
                                {
                                    mso-displayed-decimal-separator:"\.";
                                    mso-displayed-thousand-separator:"\,";
                                }
                                @page
                                {
                                    margin:.75in .7in .75in .7in;
                                    mso-header-margin:.3in;
                                    mso-footer-margin:.3in;
                                    mso-page-orientation:landscape;
                                }
                                .style1 {
                                    color: #FF0000
                                }
                                -->
                            </style>
                            <![if !supportTabStrip]><script language="JavaScript">
                                <!--
                            function fnUpdateTabs()
                                {
                                    if (parent.window.g_iIEVer >= 4) {
                                        if (parent.document.readyState == "complete"
                                                && parent.frames['frTabs'].document.readyState == "complete")
                                            parent.fnSetActiveSheet(0);
                                        else
                                            window.setTimeout("fnUpdateTabs();", 150);
                                    }
                                }

                                if (window.name != "frSheet")
                                    window.location.replace("../Libro1.htm");
                                else
                                    fnUpdateTabs();
                                //-->
                            </script>
                            <![endif]>
                            </head>

                            <body link=blue vlink=purple>
                                <table>
                                <%                                        Date dNow = new Date();
                                    DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                                    String fechaDia = ft.format(dNow);
                                %>
                                <tr>
                                    <td> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                                            <td colspan="9"><h4><%=fechaDia%></h4></td>
                                        </tr><tr></tr>
                                    <tr>
                                        <th colspan="10"><h1> Reporte por Fecha Proveedor</h1></th>
                                    </tr><tr></tr>
                                    </table>

                                    <table border=1 cellpadding=0 cellspacing=0 width=439 style='border-collapse: collapse;table-layout:fixed;width:330pt'>                   
                                        <thead>
                                            <tr height=20 style='height:15.0pt'>  
                                                <!--th class=xl66>Proyecto</th-->
                                                  <th class=xl66>Claves</th>
                                                <th class=xl66>Nombre genérico</th>
                                              
                                                <th class=xl66>Descripción específica</th>
                                                <th class=xl66>Presentación</th>
                                                
                                                <th class=xl66>Proveedor</th>
                                                <th class=xl66>Marca</th>
                                                <th class=xl66>Lote</th>
                                                <th class=xl66>Caducidad</th>
                                                <th class=xl66>Piezas</th>
                                                <th class="text-center">Costo U</th>
                                                <th class="text-center">Monto</th>
                                                <th class=xl66>Fecha</th>
                                                <th class=xl66>O.C.</th>
                                                
                                                <th class=xl66>Fuente Financiamiento</th>
                                                <th class=xl66>No. Ingreso</th>
                                                <th class=xl66>Remisión</th>
                                                <th class=xl66>Usuario</th>
                                                <th class=xl66>O.S.</th>
                                                <th class=xl66>Forma farmacéutica</th>
                                                <th class=xl66>Concentración</th>
                                                <th class=xl66>Contrato</th>
                                                
                                            </tr>
                                            <tr height=20 style='height:15.0pt'>
                                        </thead>
                                        <%

                                            if (todo.equals("")) {

                                                String query = "SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro,tb_medica.F_NomGen,tb_medica.F_FormaFarm,tb_medica.F_Concentracion,tb_medica.F_DesProEsp,tb_medica.F_ClaProSS, c.F_FuenteFinanza,tb_medica.F_PrePro, IFNULL(ped.F_Contratos, '') AS F_Contratos FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m on l.F_ClaMar= m.F_ClaMar INNER JOIN tb_medica ON tb_medica.F_ClaPro = c.F_ClaPro LEFT JOIN (SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro where c.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                                                System.out.println(query);
                                                rset = con.consulta(query);
                                                while (rset.next()) {
                                        %>
                                        <!--td height=20 class=xl75 style='height:15.0pt'>< %=rset.getString("F_DesProy")%></td--> 
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_ClaProSS")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_NomGen")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_DesProEsp")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_PrePro")%></td>
                                        
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(1)%></td>
                                        
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_DesMar")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(3)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(4)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=nf1.format(rset.getInt(5))%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=formatterD.format(rset.getDouble(12))%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=formatterD.format(rset.getDouble(13))%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(6)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(7)%></td>
                                        
                                       <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_FuenteFinanza")%></td>
                                        
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(8)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString(9)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(10)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_OrdenSuministro")%></td>
                                        
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_FormaFarm")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_Concentracion")%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString("F_Contratos")%></td>
                                        
                                        </tr>
                                        <% }
                                            }
                                        %>





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