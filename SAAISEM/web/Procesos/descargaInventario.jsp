    <%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%-- 
    Document   : descargaInventario
    Created on : 28/11/2014, 08:45:23 AM
    Author     : Americo
--%>

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%
    ConectionDB con = new ConectionDB();
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Existencias_" + df2.format(new Date()) + "_.xls\"");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
    </head>
    <body>
        <table>
            <%
                Date dNow = new Date();
                DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                String fechaDia = ft.format(dNow);
            %>
            <tr>
                <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                <td colspan="6"><h4><%=fechaDia%></h4></td>
            </tr><tr></tr>
            <tr>
                <th colspan="7"><h1>Existencia en Cedis</h1></th>
            </tr><tr></tr>
        </table>
        <table border="1">
            <tr>
                <td>PROYECTO</td>
                <td>Ubicación</td>
                <td>IdLote</td>
                <td>CLAVE</td>
                <td>Descripción</td>
                <td>Presentacion</td>
                <td>Lote</td>
                <td>Caducidad</td>
                <td>Fec Fab</td>
                <td>Marca</td>
                <td>Proveedor</td>
                <td>Existencia</td>
                <td>Bodega</td>
                <td>Origen</td>
                <td>CB</td>
                <td>OC de ingreso</td>
            </tr>
            <%
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("SELECT v.F_IdLote, v.F_ClaPro, v.F_DesPro, v.F_ClaLot, v.F_FecCad, v.F_FecFab, v.F_DesMar, v.F_NomPro, v.F_ExiLot, v.F_DesUbi, v.F_Ubica, v.F_CBUbica, v.F_Cb, v.F_FolLot, v.F_Origen, v.F_DesOri, v.F_DesProy, v.F_PrePro, v.lugar, IFNULL(c.F_OrdCom, '') AS OC FROM v_existencias AS v LEFT JOIN tb_compra AS c ON v.F_FolLot = c.F_Lote WHERE v.F_ExiLot != 0 AND v.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') GROUP BY v.F_IdLote ORDER BY v.F_DesUbi ASC;");
                    while (rset.next()) {
            %>
            <tr>
               <td><%=rset.getString("F_DesProy")%></td>
               <td><%=rset.getString("F_DesUbi")%></td>
               <td><%=rset.getString("F_IdLote")%></td>
               <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_ClaPro")%></td>
                <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_DesPro")%></td>
                <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_PrePro")%></td>
                <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_ClaLot")%></td>
                <td><%=rset.getString("F_FecCad")%></td>
                <td><%=rset.getString("F_FecFab")%></td>
                <td><%=rset.getString("F_DesMar")%></td>
                <td><%=rset.getString("F_NomPro")%></td>
                <td><%=rset.getString("F_ExiLot")%></td>
                <td><%=rset.getString("Lugar")%></td>
                <td><%=rset.getString("F_DesOri")%></td>
                <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("F_Cb")%></td>
                <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset.getString("OC")%></td>
            </tr>
            <%
                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            %>
        </table>
    </body>
</html>