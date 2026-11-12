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

        ResultSet rset = null;
        String Proveedor = "", fechaCap = "";

        try {
            fechaCap = request.getParameter("fecha");
            Proveedor = request.getParameter("provee");
        } catch (Exception e) {
        }

        String but = "r";

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=Reporte auditoria.xls");

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
                                    <%
                                        Date dNow = new Date();
                                        DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                                        String fechaDia = ft.format(dNow);
                                    %>
                                    <tr>
                                        <td> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                                        <td colspan="9"> <h4><%=fechaDia%></h4> </td>
                                    </tr><tr></tr>
                                    <tr>
                                        <th colspan="10"><h1>Reporte de Auditoria</h1></th>
                                    </tr><tr></tr>
                                </table>

                                <table border=1 cellpadding=0 cellspacing=0 width=439 style='border-collapse: collapse;table-layout:fixed;width:330pt'>
                                    <tr height=20 style='height:15.0pt'>  
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Fecha</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Usuario</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Proveedor</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">OC</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Remisión</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Folio Int</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Cantidad</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Claves</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Lotes</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Proyecto</td>
                                    </tr>
                                    <tr height=20 style='height:15.0pt'>

                                        <%                                                        if (!(Proveedor.equals("")) && (!(fechaCap.equals("")))) {

                                                rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro,''), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro,''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy,'') FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE V.F_User = '" + Proveedor + "' AND DATE(V.F_Date) = '" + fechaCap + "';");
                                            } else if (!(Proveedor.equals("")) && (fechaCap.equals(""))) {

                                                rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro,''), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro,''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, '') FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE V.F_User = '" + Proveedor + "';");
                                            } else if (Proveedor.equals("") && (!(fechaCap.equals("")))) {

                                                rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro,''), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro,''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, '') FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE DATE(V.F_Date) = '" + fechaCap + "';");
                                            } else {
                                                rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro,''), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro,''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, '') FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc;");
                                            }
                                            while (rset.next()) {
                                        %>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(1)%></td> 
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(2)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(3)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(4)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(5)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(6)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=nf1.format(rset.getInt(7))%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(8)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(9)%></td>
                                        <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(10)%></td>
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