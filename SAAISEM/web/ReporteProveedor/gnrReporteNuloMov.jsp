<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
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

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Reporte Clave Nulo Bajo Movimiento.xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <table>
                <tr>
                    <%
                        Date dNow = new Date();
                        DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                        String fechaDia = ft.format(dNow);
                    %>
                    <td> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                    <td colspan="5"> <h4><%=fechaDia%></h4></td>
                </tr><tr></tr>
                <tr>
                    <th colspan="6"><h1> Reporte Clave Nulo Movimientos</h1></th>
                </tr><tr></tr>
            </table>
            <div class="panel panel-primary">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <th class="text-center">Proyecto</th>
                                <th class="text-center">Clave</th>
                                <th class="text-center">Nombre_Generico</th>
                                <th class="text-center">Descripción</th>
                                <th class="text-center">Entrada Compra</th>
                                <th class="text-center">Requerido</th>
                                <th class="text-center">Existencia</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();

                                    ResultSet rset = null;
                                    String Ubicaciones = "";
                                    rset = con.consulta("SELECT F_ClaUbi FROM tb_ubicanomostrar;");
                                    if (rset.next()) {
                                        Ubicaciones = rset.getString(1);
                                    }

                                    rset = con.consulta("SELECT P.F_DesProy, L.F_ClaPro, MD.F_DesPro, IFNULL(ME.F_CantMov, 0) AS COMPRA, IFNULL(F.F_CantReq, 0) AS REQ, SUM(L.F_ExiLot) AS F_ExiLot, M.F_ProMov, MD.F_NomGen FROM tb_lote L LEFT JOIN ( SELECT F_ProMov FROM tb_movinv WHERE F_ConMov BETWEEN 51 AND 100 GROUP BY F_ProMov ) AS M ON L.F_ClaPro = M.F_ProMov LEFT JOIN ( SELECT F_ProMov, SUM(F_CantMov) AS F_CantMov FROM tb_movinv WHERE F_ConMov = 1 GROUP BY F_ProMov ) AS ME ON L.F_ClaPro = ME.F_ProMov LEFT JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' GROUP BY F_ClaPro ) AS F ON L.F_ClaPro = F.F_ClaPro INNER JOIN tb_medica MD ON L.F_ClaPro = MD.F_ClaPro INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ClaLot != 'X' AND L.F_Ubica NOT IN (" + Ubicaciones + ") GROUP BY L.F_ClaPro HAVING M.F_ProMov IS NULL ORDER BY L.F_ClaPro + 0;");

                                    while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td><%=rset.getString(8)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(6)%></td>
                            </tr>
                            <%
                                    }

                                    con.cierraConexion();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <br />
        <br />
        <div>
            <h4>Claves Bajo Movimiento</h4>
            <div class="panel panel-primary">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <td>Proyecto</td>
                                <td>Clave</td>
                                <td>Descripción</td>
                                <td>Entrada Compra</td>
                                <td>Requerido</td>
                                <td>Existencia</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();

                                    ResultSet rset = null;

                                    String Ubicaciones = "";
                                    rset = con.consulta("SELECT F_ClaUbi FROM tb_ubicanomostrar;");
                                    if (rset.next()) {
                                        Ubicaciones = rset.getString(1);
                                    }

                                    rset = con.consulta("SELECT P.F_DesProy, F_ProMov, MD.F_DesPro, SUM(F_CantMov) AS F_CantMov, F.F_CantReq, L.ExiLot, ROUND(((F.F_CantReq / SUM(F_CantMov)) * 100 ), 2 ) AS PORC FROM tb_movinv M INNER JOIN ( SELECT F_ClaPro, F_Proyecto, SUM(F_ExiLot) AS ExiLot FROM tb_lote WHERE F_Ubica NOT IN (" + Ubicaciones + ") GROUP BY F_ClaPro HAVING ExiLot > 0 ) AS L ON M.F_ProMov = L.F_ClaPro INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_medica MD ON M.F_ProMov = MD.F_ClaPro INNER JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' GROUP BY F_ClaPro ) AS F ON M.F_ProMov = F.F_ClaPro WHERE M.F_ConMov = 1 GROUP BY F_ProMov HAVING PORC <= 10 ORDER BY PORC ASC;");

                                    while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(6)%></td>
                            </tr>
                            <%
                                    }

                                    con.cierraConexion();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>