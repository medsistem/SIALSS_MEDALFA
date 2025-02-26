<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        //response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String Unidad = "";
    try {
        Unidad = request.getParameter("Unidad");
    } catch (Exception e) {

    }

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Catalogo_Nivel_Atencion.xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <h4>Catálogo Nivel de atención = <%=Unidad%></h4>

            <br />
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <td>Clave</td>
                                <td>Descripción</td>
                                <td>Tipo</td>
                                <%if (Unidad.equals("")) {%>
                                <td>N1</td>
                                <td>N2</td>
                                <td>N3</td>
                                <td>N4</td>
                                <td>N5</td>
                                <td>N6</td>
                                <td>N7</td>
                                <td>N8</td>
                                <td>N9</td>
                                <td>N10</td>
                                <td>N11</td>
                                <td>N12</td>
                                <td>N13</td>
                                <td>N14</td>
                                <td>N15</td>
                                <td>N16</td>
                                <%}else{%>
                                <td>Autorizado</td>
                                <%}%>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    try {
                                        ResultSet rset = null;
                                        String Condicion = "";

                                        if (Unidad.equals("")) {
                                            rset = con.consulta("SELECT F_ClaPro, F_DesPro, T.F_DesMed, CASE WHEN F_N1 = 1 THEN 'SI' ELSE 'NO' END AS N1, CASE WHEN F_N2 = 1 THEN 'SI' ELSE 'NO' END AS N2, CASE WHEN F_N3 = 1 THEN 'SI' ELSE 'NO' END AS N3, CASE WHEN F_N4 = 1 THEN 'SI' ELSE 'NO' END AS N4, CASE WHEN F_N5 = 1 THEN 'SI' ELSE 'NO' END AS N5, CASE WHEN F_N6 = 1 THEN 'SI' ELSE 'NO' END AS N6, CASE WHEN F_N7 = 1 THEN 'SI' ELSE 'NO' END AS N7, CASE WHEN F_N8 = 1 THEN 'SI' ELSE 'NO' END AS N8, CASE WHEN F_N9 = 1 THEN 'SI' ELSE 'NO' END AS N9, CASE WHEN F_N10 = 1 THEN 'SI' ELSE 'NO' END AS N10, CASE WHEN F_N11 = 1 THEN 'SI' ELSE 'NO' END AS N11, CASE WHEN F_N12 = 1 THEN 'SI' ELSE 'NO' END AS N12, CASE WHEN F_N13 = 1 THEN 'SI' ELSE 'NO' END AS N13, CASE WHEN F_N14 = 1 THEN 'SI' ELSE 'NO' END AS N14, CASE WHEN F_N15 = 1 THEN 'SI' ELSE 'NO' END AS N15, CASE WHEN F_N16 = 1 THEN 'SI' ELSE 'NO' END AS N16 FROM tb_medica M INNER JOIN tb_tipmed T ON M.F_TipMed = T.F_TipMed;");
                                        } else {
                                            rset = con.consulta("SELECT F_ClaPro, F_DesPro, T.F_DesMed , CASE WHEN F_N" + Unidad + " = 1 THEN 'SI' ELSE 'NO' END AS AUTORIZADO, F_N" + Unidad + " FROM tb_medica M INNER JOIN tb_tipmed T ON M.F_TipMed = T.F_TipMed;");
                                        }
                                        while (rset.next()) {
                            %>
                            <tr>
                                <td style="mso-number-format:'@';"><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td><%=rset.getString(3)%></td>
                                <%if (Unidad.equals("")) {%>
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(6)%></td>
                                <td><%=rset.getString(7)%></td>
                                <td><%=rset.getString(8)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td><%=rset.getString(10)%></td>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString(12)%></td>
                                <td><%=rset.getString(13)%></td>
                                <td><%=rset.getString(14)%></td>
                                <td><%=rset.getString(15)%></td>
                                <td><%=rset.getString(16)%></td>
                                <td><%=rset.getString(17)%></td>
                                <td><%=rset.getString(18)%></td>
                                <td><%=rset.getString(19)%></td>
                                <%}else{%>
                                <td><%=rset.getString(4)%></td>
                                <%}%>
                            </tr>
                            <%
                                        }
                                    } catch (Exception e) {

                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {

                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>