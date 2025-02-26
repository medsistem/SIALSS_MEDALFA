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
            
            
            <br />
        
            <div class="panel panel-success">
                <div class="panel-heading">
                        
                    <h3>Reporte Clave FUERA DE CATALOGO</h3>
               
                    <form action="ReporteClavesFuera.jsp" method="post">
                        <a class="btn btn-success" href="gnrReporteClavesFuera.jsp">Descargar&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                    </form>
                
                </div> 
                
                <hr />
                
                <div class="panel-body">
                
               
                    <table class="table table-bordered table-striped" id="datosCompras">
                        <thead>
                            <tr>                                
                                <th class="text-center">Clave</th>
                                <th class="text-center">Nombre_Generico</th>
                                <th class="text-center">Descripción</th>
                                <th class="text-center">Presentación</th>
                                <th class="text-center">Existencia</th>
                                <th class="text-center">Solicitado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                int Existencia = 0, Solicitado = 0;
                                try {
                                    con.conectar();

                                    ResultSet rset = null;
                                   // rset = con.consulta("SELECT M.F_ClaPro, M.F_DesPro, IFNULL(L.F_ExiLot, 0) AS EXILOTE, IFNULL(F.F_CantReq, 0) AS SOLICITADO FROM tb_medica M LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N1 = 1 ) AS N1 ON M.F_ClaPro = N1.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N2 = 1 ) AS N2 ON M.F_ClaPro = N2.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N3 = 1 ) AS N3 ON M.F_ClaPro = N3.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N4 = 1 ) AS N4 ON M.F_ClaPro = N4.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N5 = 1 ) AS N5 ON M.F_ClaPro = N5.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N6 = 1 ) AS N6 ON M.F_ClaPro = N6.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N7 = 1 ) AS N7 ON M.F_ClaPro = N7.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N8 = 1 ) AS N8 ON M.F_ClaPro = N8.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N9 = 1 ) AS N9 ON M.F_ClaPro = N9.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N10 = 1 ) AS N10 ON M.F_ClaPro = N10.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N11 = 1 ) AS N11 ON M.F_ClaPro = N11.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N12 = 1 ) AS N12 ON M.F_ClaPro = N12.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N13 = 1 ) AS N13 ON M.F_ClaPro = N13.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N14 = 1 ) AS N14 ON M.F_ClaPro = N14.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N15 = 1 ) AS N15 ON M.F_ClaPro = N15.F_ClaPro LEFT JOIN ( SELECT F_ClaPro FROM tb_medica WHERE F_N16 = 1 ) AS N16 ON M.F_ClaPro = N16.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, F_Proyecto, SUM(F_ExiLot) AS F_ExiLot FROM tb_lote  WHERE F_Ubica NOT IN ('AT' ,'A0T','AT2',  'AT3', 'AT4', 'ATI','NUEVA', 'NUEVAT','duplicado','CADUCADOS','MERMA', 'INGRESOS_V', 'CUARENTENA') GROUP BY F_ClaPro ) AS L ON M.F_ClaPro = L.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM(F_CantReq) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A' AND F_CantSur = 0 GROUP BY F_ClaPro ) AS F ON M.F_ClaPro = F.F_ClaPro WHERE N1.F_ClaPro IS NULL AND N2.F_ClaPro IS NULL AND N3.F_ClaPro IS NULL AND N4.F_ClaPro IS NULL AND N5.F_ClaPro IS NULL AND N6.F_ClaPro IS NULL AND N7.F_ClaPro IS NULL AND N8.F_ClaPro IS NULL AND N9.F_ClaPro IS NULL AND N10.F_ClaPro IS NULL AND N11.F_ClaPro IS NULL AND N12.F_ClaPro IS NULL AND N13.F_ClaPro IS NULL AND N14.F_ClaPro IS NULL AND N15.F_ClaPro IS NULL AND N16.F_ClaPro IS NULL AND M.F_ClaPro != '9999';");
                                   rset = con.consulta("SELECT M.F_ClaPro, M.F_DesPro, IFNULL( L.F_ExiLot, 0 ) AS EXILOTE, IFNULL( F.F_CantReq, 0 ) AS SOLICITADO, M.F_NomGen, M.F_PrePro FROM tb_medica AS M LEFT JOIN ( SELECT F_ClaPro, F_Proyecto, SUM( F_ExiLot ) AS F_ExiLot FROM tb_lote WHERE F_Ubica NOT IN ( SELECT ue.ubicacion FROM ubicaciones_excluidas AS ue )  GROUP BY	F_ClaPro ) AS L ON M.F_ClaPro = L.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM( F_CantReq ) AS F_CantReq FROM tb_factura WHERE F_StsFact = 'A'  AND F_CantSur = 0  GROUP BY F_ClaPro ) AS F ON M.F_ClaPro = F.F_ClaPro  WHERE M.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND M.F_N30 = 1 GROUP BY M.F_ClaPro;");

                                    while (rset.next()) {
                                        Existencia = Existencia + rset.getInt(3);
                                        Solicitado = Solicitado + rset.getInt(4);
                            %>
                            <tr>
                                <td class="text-center"><%=rset.getString(1)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td><%=rset.getString(6)%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(3))%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(4))%></td>
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
                <label><h3>Total Existencia: <%=formatter.format(Existencia)%>&nbsp;&nbsp;Total Solicitado: <%=formatter.format(Solicitado)%></h3></label>
            </div>
        </div>
        <br><br><br>
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
        $("#Fecha").datepicker();
        $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
        $("#Proveedor").select2();
    });
</script>