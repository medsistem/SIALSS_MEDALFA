<%-- 
    Document   : reabastecerModula
    Created on : 26/03/2015, 12:54:16 PM
    Author     : Americo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB_SQLServer"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMdd"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); %>
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
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <!---->
        <title>SIALSS</title>
    </head>
    <body class="container">
        <h1>MEDALFA</h1>
        <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
        <hr/>
        <h4>Reabastecer Modula&nbsp;&nbsp;&nbsp;<a href="MinMax.jsp"><div class="glyphicon glyphicon-export"></div></a></h4>
        <hr/>
        <h4>
            <div class="col-lg-6">
                <label>Reabastecer&nbsp;&nbsp;&nbsp;</label>
                <input id="radio" name="radio" type="Radio" value="mod1" checked="" />Modula 1
                <input id="radio" name="radio" type="Radio" value="mod2" />Modula 2
                <input id="radio" name="radio" type="Radio" value="af" />AF
            </div> 
        </h4>
        <hr/>
        <table class="table table-bordered table-condensed table-striped" id="tablaMovMod">
            <thead>
                <tr class="text-center">
                    <td>Clave</td>
                    <td>Descripción</td>
                    <td>Ubicación</td>
                    <td>Min</td>
                    <td>Reorden</td>
                    <td>Max</td>
                    <td>Exist Modula</td>
                    <td>Falta Max</td>
                    <td>Reabastecer</td>
                </tr>
            </thead>
            <tdoby>
                <%                    con.conectar();
                    String F_ExiLot = "";
                    int F_ExiLot1 = 0, F_Max = 0, Diferencia = 0, Reabastecer = 0, F_Reo = 0, F_Min = 0;
                    //ResultSet rset = con.consulta("SELECT M.F_Clapro,MD.F_DesPro,FORMAT(M.F_Min,0) AS F_Min,FORMAT(M.F_Reo,0) AS F_Reo,FORMAT(M.F_Max,0) AS F_Max,FORMAT(SUM(F_ExiLot),0) AS F_ExiLot,FORMAT((F_Max - SUM(F_ExiLot)),0) AS F_FMax,CASE WHEN (M.F_Reo>=SUM(F_ExiLot)) THEN FORMAT((M.F_Max-M.F_Min),0) ELSE '0' END AS F_Resurtir FROM tb_lote L INNER JOIN tb_minmax M ON L.F_ClaPro=M.F_Clapro INNER JOIN tb_medica MD ON M.F_Clapro=MD.F_ClaPro WHERE L.F_ClaPro IN (SELECT M.F_Clapro FROM tb_medica MD WHERE MD.F_StsPro='A') AND L.F_Ubica='MODULA'");
                    ResultSet rset = con.consulta("SELECT M.F_Clapro,MD.F_DesPro,M.F_Min,M.F_Reo,M.F_Max,M.F_Ubi FROM tb_minmax M INNER JOIN tb_medica MD ON M.F_Clapro=MD.F_ClaPro WHERE M.F_Ubi<>'D'");
                    while (rset.next()) {
                        F_Max = rset.getInt(5);
                        F_Reo = rset.getInt(4);
                        F_Min = rset.getInt(3);
                        ResultSet ExiMod = con.consulta("SELECT SUM(F_ExiLot) FROM tb_lote WHERE F_ClaPro='" + rset.getString(1) + "' AND F_Ubica='" + rset.getString(6) + "'");
                        if (ExiMod.next()) {
                            F_ExiLot = ExiMod.getString(1);
                        }
                        if (F_ExiLot != null) {
                            F_ExiLot1 = Integer.parseInt(F_ExiLot);
                        } else {
                            F_ExiLot1 = 0;
                        }
                        if (F_ExiLot1 == 0) {
                            Diferencia = F_Max;
                            Reabastecer = F_Max;
                        } else {
                            if (F_ExiLot1 >= F_Reo) {
                                Diferencia = F_Max - F_ExiLot1;
                                if (Diferencia < 0) {
                                    Diferencia = 0;
                                }
                            } else {
                                Diferencia = F_Max - F_ExiLot1;
                                Reabastecer = F_Max - F_Min;
                            }
                            if (F_ExiLot1 <= F_Reo) {
                                Reabastecer = F_Max - F_Min;
                            }
                        }

                %>
                <tr>
                    <td><%=rset.getString(1)%></td>
                    <td><%=rset.getString(2)%></td>
                    <td><%=rset.getString(6)%></td>
                    <td class="text-center"><%=formatter.format(F_Min)%></td>
                    <td class="text-center"><%=formatter.format(F_Reo)%></td>
                    <td class="text-center"><%=formatter.format(F_Max)%></td>
                    <td class="text-center"><%=formatter.format(F_ExiLot1)%></td>
                    <td class="text-center"><%=formatter.format(Diferencia)%></td>
                    <td class="text-center"><%=formatter.format(Reabastecer)%></td>
                </tr>
                <%
                        Diferencia = 0;
                        Reabastecer = 0;
                    }
                    con.cierraConexion();
                %>
            </tdoby>
        </table>
        <!-- ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="../js/jquery-1.9.1.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/jquery.dataTables.js"></script>
        <script src="../js/dataTables.bootstrap.js"></script>
        <script>
            $(document).ready(function() {
                $('#tablaMovMod').dataTable();
            });
        </script>
    </body>
</html>
