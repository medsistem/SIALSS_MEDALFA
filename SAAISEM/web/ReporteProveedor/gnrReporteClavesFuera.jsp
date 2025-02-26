
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns:v="urn:schemas-microsoft-com:vml"
      xmlns:o="urn:schemas-microsoft-com:office:office"
      xmlns:x="urn:schemas-microsoft-com:office:excel"
      xmlns="http://www.w3.org/TR/REC-html40">
    <%

        ConectionDB con = new ConectionDB();
       
        DecimalFormat formatterD = new DecimalFormat("#,###,###.00");

        ResultSet rset = null;

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=Reporte Clave fuera Catalogo.xls");

        //ingresar un try y realizar una sola conexión
        try {
            con.conectar();


    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            
                    
                            <!--link rel=Stylesheet href=stylesheet.css-->

                            </head>
                            <table>
                                <%            Date dNow = new Date();
                                    DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                                    String fechaDia = ft.format(dNow);
                                %>
                                <tr>
                                    <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"></td>
                                    <td colspan="3"><h4><%=fechaDia%></td>
                                </tr><tr></tr>
                                <tr>
                                    <th colspan="4"> <h2>Reporte claves fuera de catálogo</h2></th>
                                </tr><tr></tr> 
                            </table>


                            <table border="1">
                                <thead>
                                    <tr height=20 style='height:15.0pt'>  
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Clave</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Nombre_Generico</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Descripción</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Presentación</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Existencia</td>
                                        <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Solicitado</td>
                                    </tr>
                                </thead>
                                <tr height=20 style='height:15.0pt'>

                                    <%
                                        // rset = con.consulta("SELECT M.F_ClaPro, M.F_DesPro, IFNULL(L.F_ExiLot, 0) AS EXILOTE, IFNULL(F.F_CantReq, 0) AS SOLICITADO FROM tb_medica M LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N1 = 1 ) AS N1 ON M.F_ClaPro = N1.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N2 = 1 ) AS N2 ON M.F_ClaPro = N2.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N3 = 1 ) AS N3 ON M.F_ClaPro = N3.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N4 = 1 ) AS N4 ON M.F_ClaPro = N4.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N5 = 1 ) AS N5 ON M.F_ClaPro = N5.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N6 = 1 ) AS N6 ON M.F_ClaPro = N6.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N7 = 1 ) AS N7 ON M.F_ClaPro = N7.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N8 = 1 ) AS N8 ON M.F_ClaPro = N8.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N9 = 1 ) AS N9 ON M.F_ClaPro = N9.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N10 = 1 ) AS N10 ON M.F_ClaPro = N10.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N11 = 1 ) AS N11 ON M.F_ClaPro = N11.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N12 = 1 ) AS N12 ON M.F_ClaPro = N12.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N13 = 1 ) AS N13 ON M.F_ClaPro = N13.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N14 = 1 ) AS N14 ON M.F_ClaPro = N14.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N15 = 1 ) AS N15 ON M.F_ClaPro = N15.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N16 = 1 ) AS N16 ON M.F_ClaPro = N16.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, F_Proyecto, SUM(F_ExiLot) AS F_ExiLot FROM tb_lote WHERE F_Ubica NOT IN ( 'AT' ,'A0T','AT2',  'AT3', 'AT4', 'ATI','NUEVA', 'NUEVAT','duplicado') GROUP BY F_ClaPro ) AS L ON M.F_ClaPro = L.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' AND F_CantSur = 0 GROUP BY F_ClaPro ) AS F ON M.F_ClaPro = F.F_ClaPro WHERE N1.F_ClaPro IS NULL AND N2.F_ClaPro IS NULL AND N3.F_ClaPro IS NULL AND N4.F_ClaPro IS NULL AND N5.F_ClaPro IS NULL AND N6.F_ClaPro IS NULL AND N7.F_ClaPro IS NULL AND N8.F_ClaPro IS NULL AND N9.F_ClaPro IS NULL AND N10.F_ClaPro IS NULL AND N11.F_ClaPro IS NULL AND N12.F_ClaPro IS NULL AND N13.F_ClaPro IS NULL AND N14.F_ClaPro IS NULL AND N15.F_ClaPro IS NULL AND N16.F_ClaPro IS NULL AND M.F_ClaPro != '9999';");
                                        rset = con.consulta("SELECT M.F_ClaPro, M.F_DesPro, IFNULL( L.F_ExiLot, 0 ) AS EXILOTE, IFNULL( F.F_CantReq, 0 ) AS SOLICITADO, M.F_NomGen, M.F_PrePro FROM tb_medica AS M LEFT JOIN ( SELECT	F_ClaPro,	F_Proyecto,	SUM( F_ExiLot ) AS F_ExiLot  FROM	tb_lote  WHERE	F_Ubica NOT IN ( SELECT ue.ubicacion FROM ubicaciones_excluidas AS ue )  GROUP BY	F_ClaPro ) AS L ON M.F_ClaPro = L.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM( F_CantReq ) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A'  AND F_CantSur = 0  GROUP BY F_ClaPro ) AS F ON M.F_ClaPro = F.F_ClaPro  WHERE M.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND M.F_N30 = 1 GROUP BY M.F_ClaPro;");

                                        while (rset.next()) {
                                    %>
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(1)%></td> 
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(5)%></td>
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(2)%></td>
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getString(6)%></td>
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getInt(3)%></td>
                                    <td height=20 class=xl75 style='height:15.0pt'><%=rset.getInt(4)%></td>
                                </tr>
                                <% } %>
                            </table>

                            </body>

                            </html>
                            <%
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            %>