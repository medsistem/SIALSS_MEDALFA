<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="conn.ConectionDB"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterD = new DecimalFormat("#,###,###.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();
    String fechaCap = "";
    String Proveedor = "";
    try {
        fechaCap = df2.format(df3.parse(request.getParameter("Fecha")));
    } catch (Exception e) {

    }
    if (fechaCap == null) {
        fechaCap = "";
    }
    try {
        Proveedor = request.getParameter("Proveedor");
    } catch (Exception e) {

    }
    if (Proveedor == null) {
        Proveedor = "";
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <link href="../css/select2.css" rel="stylesheet">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <% if (tipo.equals("13")) {
                    %>
                    <%@include file="../jspf/menuPrincipalCompra.jspf" %>
            <%    }else{ %>
            <%@include file="../jspf/menuPrincipal.jspf" %>
                
                 <%   } %>
            <div class="row">
                <form action="ReporteNuloMov.jsp" method="post">
                    <a class="btn btn-success" href="gnrReporteNuloMov.jsp">Descargar&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                </form>
            </div>
            <div>
                <h3>Reporte Clave Nulo Movimiento</h3>
            </div>
        </div>
        <br />
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras">
                        <thead>
                            <tr>                                
                                <th class="text-center">Proyecto</th>
                                <th class="text-center">Clave</th>
                                <th class="text-center">Nombre_Generico</th>
                                <th class="text-center">Descripción</th>
                                <th class="text-center">Presentación</th>
                                <th class="text-center">Entrada Compra</th>
                                <th class="text-center">Requerido</th>
                                <th class="text-center">Existencia</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                int Existencia = 0;
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    String Ubicaciones = "";
                                    rset = con.consulta("SELECT F_ClaUbi FROM tb_ubicanomostrar;");
                                    if (rset.next()) {
                                        Ubicaciones = rset.getString(1);
                                    }
                                    rset = con.consulta("SELECT P.F_DesProy, L.F_ClaPro, MD.F_DesPro, IFNULL(ME.F_CantMov, 0) AS COMPRA, IFNULL(F.F_CantReq, 0) AS REQ, SUM(L.F_ExiLot) AS F_ExiLot, M.F_ProMov, MD.F_NomGen, MD.F_PrePro FROM tb_lote L LEFT JOIN ( SELECT F_ProMov FROM tb_movinv WHERE F_ConMov BETWEEN 51 AND 100 GROUP BY F_ProMov ) AS M ON L.F_ClaPro = M.F_ProMov LEFT JOIN ( SELECT F_ProMov, SUM(F_CantMov) AS F_CantMov FROM tb_movinv WHERE F_ConMov = 1 GROUP BY F_ProMov ) AS ME ON L.F_ClaPro = ME.F_ProMov LEFT JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' GROUP BY F_ClaPro ) AS F ON L.F_ClaPro = F.F_ClaPro INNER JOIN tb_medica MD ON L.F_ClaPro = MD.F_ClaPro INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ClaLot != 'X' AND L.F_Ubica NOT IN (SELECT ue.ubicacion FROM ubicaciones_excluidas AS ue) GROUP BY L.F_ClaPro HAVING M.F_ProMov IS NULL ORDER BY L.F_ClaPro + 0;");

                                    while (rset.next()) {
                                        Existencia = Existencia + rset.getInt(6);
                            %>
                            <tr>
                                <td class="text-center"><%=rset.getString(1)%></td>
                                <td class="text-center"><%=rset.getString(2)%></td>
                                <td class="text-center"><%=rset.getString(8)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(4))%></td>                                
                                <td class="text-center"><%=formatter.format(rset.getInt(5))%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(6))%></td>
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
            <div>
                <label><h3>Total Existencia: <%=formatter.format(Existencia)%></h3></label>
            </div>
        </div>
        <br>
        <div class="container">
            <div>
                <h3>Reporte Clave Bajo Movimiento</h3>
            </div>
        </div>
        <br />        
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosComprasBM">
                        <thead>
                            <tr>                                
                                <td class="text-center">Proyecto</td>
                                <td class="text-center">Clave</td>
                                <td class="text-center">Descripción</td>
                                <td class="text-center">Presentación</td>
                                <td class="text-center">Entrada Compra</td>
                                <td class="text-center">Requerido</td>
                                <td class="text-center">Existencia</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                Existencia = 0;
                                try {
                                    con.conectar();

                                    ResultSet rset = null;
                                    String Ubicaciones = "";
                                    rset = con.consulta("SELECT F_ClaUbi FROM tb_ubicanomostrar;");
                                    if (rset.next()) {
                                        Ubicaciones = rset.getString(1);
                                    }
                                    rset = con.consulta("SELECT P.F_DesProy, F_ProMov, MD.F_DesPro, SUM(F_CantMov) AS F_CantMov, F.F_CantReq, L.ExiLot, ROUND(((F.F_CantReq / SUM(F_CantMov)) * 100 ), 2 ) AS PORC, MD.F_PrePro FROM tb_movinv M INNER JOIN ( SELECT F_ClaPro, F_Proyecto, SUM(F_ExiLot) AS ExiLot FROM tb_lote WHERE F_Ubica NOT IN ( SELECT ue.ubicacion FROM ubicaciones_excluidas AS ue ) GROUP BY F_ClaPro HAVING ExiLot > 0 ) AS L ON M.F_ProMov = L.F_ClaPro INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_medica MD ON M.F_ProMov = MD.F_ClaPro INNER JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' GROUP BY F_ClaPro ) AS F ON M.F_ProMov = F.F_ClaPro WHERE M.F_ConMov = 1 GROUP BY F_ProMov HAVING PORC <= 10 ORDER BY PORC ASC;");

                                    while (rset.next()) {
                                        Existencia = Existencia + rset.getInt(6);
                            %>
                            <tr>
                                <td class="text-center"><%=rset.getString(1)%></td>
                                <td class="text-center"><%=rset.getString(2)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(8)%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(4))%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(5))%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(6))%></td>
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
            <div>
                <label><h3>Total Existencia: <%=formatter.format(Existencia)%></h3></label>
            </div>
        </div>
    </body>
</html>
<%@include file="../jspf/piePagina.jspf" %>

<script src="../js/jquery-2.1.4.min.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/bootstrap-datepicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script src="../js/select2.js"></script>
<script>
    $(document).ready(function () {
        $('#datosCompras').dataTable();
        $('#datosComprasBM').dataTable();
        $("#Fecha").datepicker();
        $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
        $("#Proveedor").select2();
    });
</script>