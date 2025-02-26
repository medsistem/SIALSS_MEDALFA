<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fol_gnkl = "", fol_remi = "", orden_compra = "", fecha = "";
    try {
        if (request.getParameter("accion").equals("buscar")) {
            fol_gnkl = request.getParameter("fol_gnkl");
            fol_remi = request.getParameter("fol_remi");
            orden_compra = request.getParameter("orden_compra");
            fecha = request.getParameter("fecha");
        }
    } catch (Exception e) {

    }
    if (fol_gnkl == null) {
        fol_gnkl = "";
        fol_remi = "";
        orden_compra = "";
        fecha = "";
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipal.jspf" %>

            <div class="row">
                <h3 class="col-sm-4">Administrar Tranferencias</h3>
                <div class="col-sm-1 col-sm-offset-5">
                    <br/>
                </div>
                <div class="col-sm-2">
                    <br/>
                    <!--a class="btn btn-success" href="../gnrFacturaConcentrado.jsp" target="_blank">Exportar Global</a-->
                </div>
            </div>
            <div>
                <br />
                <div class="panel panel-success">
                    <div class="panel-body table-responsive">
                        <table class="table table-bordered table-striped" id="datosCompras">
                            <thead>
                                <tr>
                                    <!--td></td-->
                                    <td>No. Folio</td>
                                    <td>Punto de Entrega</td>
                                    <td>Fecha de Entrega</td>
                                    <td>Folio</td>
                                    <td>Exportar Ubicación</td>
                                    <td>exportar SAP</td>
                                    <td>Abasto MEDALFA</td>
                                    <td>Abasto GNKLite</td>
                                    <!--td>Ver Factura</td-->
                                    <!--td>Devolución</td-->
                                    <!--td>Reportes</td-->
                                    <%
                                        if (usua.equals("remision")) {
                                            //    out.println("<td>Reintegrar Insumo</td>");
                                        }
                                    %>
                                    <!--td>Excel</td-->
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet rset = con.consulta("SELECT F.F_ClaDoc,F.F_ClaCli,U.F_DesCon,DATE_FORMAT(F.F_FecApl,'%d/%m/%Y') AS F_FecApl,SUM(F.F_Monto) AS F_Costo,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_transferencias F INNER JOIN tb_coninv U ON F.F_ClaCli=U.F_IdCon GROUP BY F.F_ClaDoc ORDER BY F.F_ClaDoc+0;");
                                            while (rset.next()) {

                                %>
                                <tr>
                                    <!--td>
                                        <input type="checkbox" name="">
                                    </td-->
                                    <td><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(3)%></td>
                                    <td><%=rset.getString("F_FecEnt")%></td>
                                    <td>
                                        <form action="../reportes/reimpTransferencia.jsp" target="_blank">
                                            <input class="hidden" name="fol_gnkl" value="<%=rset.getString(1)%>">
                                            <button class="btn btn-block btn-success"><span class="glyphicon glyphicon-print"></span></button>
                                        </form>
                                    </td>
                                    <td><a class="btn btn-warning btn-block" href="../reportes/TransferUbicacion.jsp?F_ClaDoc=<%=rset.getString(1)%>&ConInv=<%=rset.getString(2)%>"><span class="glyphicon glyphicon-download"></span></a></td>
                                    <td><a class="btn btn-success btn-block" href="../reportes/TransferSAP.jsp?F_ClaDoc=<%=rset.getString(1)%>&ConInv=<%=rset.getString(2)%>"><span class="glyphicon glyphicon-download"></span></a></td>
                                    <td>
                                        <a class="btn btn-info btn-block" href="generaAbastoCSV.jsp?F_ClaDoc=<%=rset.getString(1)%>&ConInv=<%=rset.getString(2)%>"><span class="glyphicon glyphicon-download"></span></a>
                                    </td>
                                    <td>
                                        <form action="../AbastoGNKLite" method="post">
                                            <button class="btn btn-warning btn-block glyphicon glyphicon-download" type="submit" name="accion" value="<%=rset.getString(1)%>"></button>                                        
                                        </form>
                                        
                                    </td>
                                    <!--td>
                                        <form action="verFactura.jsp" method="post">
                                            <input class="hidden" name="fol_gnkl" value="<%=rset.getString(1)%>">
                                            <button class="btn btn-block btn-success"><span class="glyphicon glyphicon-search"></span></button>
                                        </form>
                                    </td-->
                                    <!--td>
                                    <%
                                        if (tipo.equals("7")) {
                                    %>
                                    <form action="devolucionesFacturas.jsp" method="post">
                                        <input class="hidden" name="fol_gnkl" value="<%=rset.getString(1)%>">
                                        <button class="btn btn-block btn-success"><span class="glyphicon glyphicon-arrow-left"></span></button>
                                    </form>
                                    <%
                                        }
                                    %>
                                </td-->
                                    <!--td>
                                        <form class="form-horizontal" role="form" name="formulario_receta" id="formulario_receta" method="get" action="ReporteImprime">   
                                            <button class="btn btn-block btn-success" id="btn_capturar" name="btn_capturar" value="<%=rset.getString(1)%>" onclick="return confirm('¿Esta Ud. Seguro de Iniciar proceso de Generación?')">Generar</button>
                                        </form>
                                    </td-->
                                    <%
                                        if (usua.equals("remision")) {
                                    %>
                                    <!--td>
                                    <%
                                        ResultSet rset2 = con.consulta("select * from tb_factdevol where F_ClaDoc = '" + rset.getString(1) + "' group by F_ClaDoc");
                                        while (rset2.next()) {
                                    %>
                                    <form action="reintegrarDevolFact.jsp" method="post">
                                        <input class="hidden" name="fol_gnkl" value="<%=rset2.getString(2)%>">
                                        <button class="btn btn-block btn-info"><span class="glyphicon glyphicon-log-in"></span></button>  
                                    </form>
                                    <%
                                        }
                                    %>
                                </td-->
                                    <%
                                        }
                                    %>
                                    <!--td>
                                        <a class="btn btn-block btn-success" href="gnrFacturaExcel.jsp?fol_gnkl=<%=rset.getString(1)%>"><span class="glyphicon glyphicon-save"></span></a>
                                    </td-->
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
        </div>
        <%@include file="../jspf/piePagina.jspf" %>

        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="../js/jquery-1.9.1.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/bootstrap-datepicker.js"></script>
        <script src="../js/jquery.dataTables.js"></script>
        <script src="../js/dataTables.bootstrap.js"></script>
        <script>
            $(document).ready(function() {
                $('#datosCompras').dataTable();
            });
        </script>
        <script>
            $(function() {
                $("#fecha").datepicker();
                $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
            });
        </script>
    </body>
</html>
