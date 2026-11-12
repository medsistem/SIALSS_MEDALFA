<%-- 
    Document   : insumoNuevoRedist
    Created on : 6/10/2014, 10:49:37 AM
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
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String ClaPro = "", UbiAnt = "", UbiCb = "";
    try {
        UbiCb = request.getParameter("UbiCb");
        //UbiAnt = request.getParameter("UbiAnt");

    } catch (Exception e) {
    }

    if (ClaPro == null) {
        ClaPro = "";
    }

    try {
        con.conectar();
        ResultSet rset = con.consulta("select F_Cb from tb_ubica where F_Cb='" + UbiCb + "'");
        while (rset.next()) {
            UbiCb = rset.getString("F_Cb");
        }
        if (!UbiCb.equals("")) {
            UbiAnt = UbiCb;
        }
        con.cierraConexion();
    } catch (Exception e) {

    }
%>
<html>
    <head>
        <!-- Estilos CSS -->
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <!---->
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipal.jspf" %>

            <H4>Redistribuccion HH</H4>
            <form action="leerInsRedist.jsp" method="post">
                <a class="btn btn-default" href="insumoNuevoRedist.jsp">Regresar</a>
                <button class="btn btn-success" type="submit" name="UbiAnt" value="PorUbicar">Por Ubicar</button>
            </form>

            <form action="leerInsRedist.jsp" method="post">
                <div class="row">
                    <h5 class="col-lg-12">CB del Insumo a Mover</h5>
                    <div class="col-lg-12">
                        <input class="form-control" name="UbiCb" id="UbiCb" placeholder="010101" />
                        <!-- input class="form-control" name="ClaPro" value="< %=ClaPro%>" autofocus="" /-->
                    </div>
                </div>
                <br/>
                <div class="row">
                    <div class="col-lg-12">
                        <button class="btn btn-block btn-success btn-lg" type="submit" name="UbiAnt" value="Leer">Leer Insumo</button>
                    </div>
                </div>
                <!--div class="col-lg-12">
                    <input class="hidden" name="UbiAnt" value="< %=UbiAnt%>" />                        
                </div-->
            </form>
            <hr/>
            <h4>Insumos Médicos</h4>
            <div class="panel panel-success">
                <div class="panel-body table-responsive">
                    <table class="cell-border table table-striped table-bordered" cellspacing="0"  id="example">
                        <thead>
                            <tr>
                                <td>Proyecto</td>
                                <td>Clave</td>
                                <td>Lote</td>
                                <td>Caducidad</td>
                                <td>Ubicación</td>
                                <td>Cantidad</td>
                                <td>Origen</td>
                                <td>Acción</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    if (!UbiAnt.equals("PorUbicar")) {
                                        ResultSet rset = null;
                                        con.conectar();
                                        String UbiCrossd = "";
                                        ResultSet rsetUbica = con.consulta("SELECT * FROM tb_ubicacrosdock;");
                                        if (rsetUbica.next()) {
                                            UbiCrossd = rsetUbica.getString(1);
                                        }

                                        if (!UbiCb.equals("")) {
                                            System.out.println("entre a ubicar por cb");
                                            rset = con.consulta("SELECT u.F_DesUbi, l.F_ClaPro, l.F_ExiLot, m.F_DesPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS F_FecCad, l.F_IdLote, l.F_Origen, p.F_DesProy, o.F_DesOri FROM tb_lote l INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_ubica u ON l.F_Ubica = u.F_ClaUbi INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen o ON l.F_Origen = o.F_ClaOri WHERE l.F_ExiLot != 0 AND u.F_Cb = '" + UbiCb + "' AND l.F_Ubica NOT IN (" + UbiCrossd + ");");
                                        }
                                        // else if (!UbiAnt.equals("1")) {
                                        //    System.out.println("2");
                                        //   rset = con.consulta("SELECT u.F_DesUbi, l.F_ClaPro, l.F_ExiLot, m.F_DesPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS F_FecCad, l.F_IdLote, l.F_Origen, p.F_DesProy, o.F_DesOri FROM tb_lote l INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_ubica u ON l.F_Ubica = u.F_ClaUbi INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen o ON l.F_Origen = o.F_ClaOri WHERE l.F_ExiLot != 0 AND u.F_Cb = '" + UbiAnt + "' AND l.F_Ubica NOT IN (" + UbiCrossd + ");");                                  
                                        //  }
                                        System.out.println(rset);
                                        while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString("F_DesProy")%></td>        
                                <td><%=rset.getString("F_ClaPro")%></td>
                                <td><%=rset.getString("F_ClaLot")%></td>
                                <td><%=rset.getString("F_FecCad")%></td>
                                <td><%=rset.getString("F_DesUbi")%></td>
                                <td><%=formatter.format(rset.getInt("F_ExiLot"))%></td>
                                <td><%=rset.getString("F_DesOri")%></td>
                                <td>
                                    <form action="ingCantRedist.jsp" method="post">
                                        <input class="hidden" name="UbiAnt" value="<%=UbiAnt%>" />
                                        <input class="hidden" name="ClaPro" value="<%=ClaPro%>" />
                                        <input value="<%=rset.getString("F_IdLote")%>" class="hidden" name="idLote" />
                                        <% if (!rset.getString("F_DesUbi").contains("MERMA-REDFRIA")) {
                                                if (rset.getString("F_DesUbi").contains("ACOPIO") || rset.getString("l.F_Origen").equals("14")) {
                                                    if (usua.equals("Aureliano") || usua.equals("AGomezFa") || usua.equals("DcNJaen") || usua.equals("JRobles") || usua.equals("MPadua") || usua.equals("DanielH") || usua.equals("JEscutiaMu") || usua.equals("SHernandezMi") || usua.equals("GRamirezDi") || usua.equals("LFuentesEs")) { %>
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <% } else { %>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% }
                                        } else if ((rset.getString("F_DesUbi").contains("ST-AT"))) {
                                        %>   
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>

                                        <%
                                        } else if (rset.getString("F_DesUbi").equals("CONCILIACION-INV") || (rset.getString("F_DesUbi").contains("AT")) || (rset.getString("F_DesUbi").contains("AECONCILIACIONINV")) || (rset.getString("F_DesUbi").contains("STCONCILIACIONINV"))) {
                                            if (usua.equals("SHernandezMi") || usua.equals("GRamirezDi") || usua.equals("LFuentesEs")) {%>
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <%} else {%>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% }
                                        } else { %>
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <%}
                                        } else { %>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% } %>
                                    </form>
                                </td>
                            </tr>
                            <%
                                }
                            } else {

                                System.out.println("3");
                                con.conectar();
                                ResultSet rset = con.consulta("SELECT u.F_DesUbi, l.F_ClaPro, l.F_ExiLot, m.F_DesPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS F_FecCad, l.F_IdLote, l.F_Origen, p.F_DesProy, o.F_DesOri FROM tb_lote l INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_ubica u ON l.F_Ubica = u.F_ClaUbi INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen o ON l.F_Origen = o.F_ClaOri WHERE l.F_ExiLot != 0 AND u.F_Cb = '3103';");
                                while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString("F_DesProy")%></td>        
                                <td><%=rset.getString("F_ClaPro")%></td>
                                <td><%=rset.getString("F_ClaLot")%></td>
                                <td><%=rset.getString("F_FecCad")%></td>
                                <td><%=rset.getString("F_DesUbi")%></td>
                                <td><%=formatter.format(rset.getInt("F_ExiLot"))%></td>
                                <td><%=rset.getString("F_DesOri")%></td>
                                <td>
                                    <form action="ingCantRedist.jsp" method="post">
                                        <input class="hidden" name="UbiAnt" value="<%=UbiAnt%>" />
                                        <input class="hidden" name="ClaPro" value="<%=ClaPro%>" />
                                        <input value="<%=rset.getString("F_IdLote")%>" class="hidden" name="idLote" />
                                        <% if (!rset.getString("F_DesUbi").contains("MERMA-REDFRIA")) {
                                            if (rset.getString("F_DesUbi").contains("ACOPIO") || rset.getString("l.F_Origen").equals("14")) {
                                                if (usua.equals("Aureliano") || usua.equals("AGomezFa") || usua.equals("DcNJaen") || usua.equals("JRobles") || usua.equals("MPadua") || usua.equals("DanielH") || usua.equals("JEscutiaMu") || usua.equals("SHernandezMi") || usua.equals("GRamirezDi") || usua.equals("LFuentesEs")) { %>                                      
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <% } else { %>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% }
                                        } else if ((rset.getString("F_DesUbi").contains("ST-AT"))) {
                                        %>   
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>

                                        <%
                                        } else if (rset.getString("F_DesUbi").equals("CONCILIACION-INV") || (rset.getString("F_DesUbi").contains("AT")) || (rset.getString("F_DesUbi").contains("AECONCILIACIONINV")) || (rset.getString("F_DesUbi").contains("STCONCILIACIONINV"))) {
                                            if (usua.equals("SHernandezMi") || usua.equals("GRamirezDi") || usua.equals("LFuentesEs")) {%>
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <%} else {%>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% }
                                        } else { %>
                                        <button class="btn btn-block btn-success" type="submit">Seleccionar</button>
                                        <%}   } else { %>
                                        <button class="btn btn-block btn-success" type="submit" disabled>Seleccionar</button>
                                        <% } %>
                                    </form>
                                </td>
                            </tr>
                            <%

                                        }
                                        con.cierraConexion();
                                    }
                                } catch (Exception e) {

                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%@include file="../jspf/piePagina.jspf" %>
    </body>
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
    <script type="text/javascript">
        $(document).ready(function () {
            $('#example').dataTable();
        });
    </script>
</html>
