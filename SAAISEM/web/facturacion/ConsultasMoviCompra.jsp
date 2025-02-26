<%-- 
    Document   : cambioFechas
    Created on : 14/04/2015, 12:58:35 PM
    Author     : Americo
--%>

<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fecha_ini = "", fecha_fin = "", clave = "", ClaCli = "", Proyec = "", Origen = "", DesProyecto = "";
    int Proyecto = 0;
    try {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        clave = request.getParameter("clave");
        ClaCli = request.getParameter("ClaCli");
        Proyec = request.getParameter("Proyecto");
        Origen = request.getParameter("Origen");
    } catch (Exception e) {

    }
    if (fecha_ini == null) {
        fecha_ini = "";
    }
    if (fecha_fin == null) {
        fecha_fin = "";
    }
    if (clave == null) {
        clave = "";
    }
    if (ClaCli == null) {
        ClaCli = "";
    }
    if (Proyec == null) {
        Proyec = "0";
    }
    if (Origen == null) {
        Origen = "0";
    }
    Proyecto = Integer.parseInt(Proyec);
    try {
        con.conectar();
        ResultSet rset = con.consulta("SELECT F_DesProy FROM tb_proyectos WHERE F_Id='" + Proyecto + "' ;");
        while (rset.next()) {
            DesProyecto = rset.getString(1);
        }
        con.cierraConexion();
    } catch (Exception e) {
        out.println(e.getMessage());
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
        <script src="http://code.jquery.com/jquery-1.7.min.js"></script>
        <link href="../css/select2.css" rel="stylesheet" type="text/css"/>
        <link href="../css/sweetalert.css" rel="stylesheet" type="text/css"/>

        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">

        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <div class="panel-heading">

                <h1>MEDALFA</h1>
                <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

                <%@include file="../jspf/menuPrincipalCompra.jspf"%>
                <div class="row">
                    <h3 class="col-sm-9">Consultas Movimientos</h3>
                    <div class="col-sm-3 float-rigth">
                        <!--<button class="btn btn-block btn-info" id="descargar" name="descargar">Descargar&nbsp;<span class="glyphicon glyphicon-download-alt"></span></button>-->
                        <a class="btn btn-block btn-info" id="descargar">Exportar<span class="glyphicon glyphicon-save"></span></a>
                    </div>
                </div>
            </div>

            <form action="ConsultasMoviCompra.jsp" method="post">
                <div class="panel-title panel-success">
                    <div class="row">
                        <div class="col-sm-3">
                            <div>
                                <label class="control-label col-sm-3 float-left" for="clave" >Clave:</label>
                            </div>
                            <div class="col-sm-9">
                                <input class="form-control" id="clave" name="clave" type="text" value="<%=clave%>"/>                        
                            </div>
                        </div>
                        <div class="col-sm-5">
                            <div>
                                <label class="control-label col-sm-2 float-left" for="fecha">Fecha: </label>
                            </div>
                            <div class="col-sm-5">
                                <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" onkeydown="return false" value="<%=fecha_ini%>" />
                            </div>
                            <div class="col-sm-5">
                                <input class="form-control" id="fecha_fin" name="fecha_fin" type="date" onkeydown="return false" value="<%=fecha_fin%>" />
                            </div>
                        </div>
                        <div class="col-sm-3">
                            <div>
                                <label class=" control-label col-sm-3 float-left" for="origen">Origen: </label>
                            </div>
                            <div class="col-sm-9">
                                <select class="form-control" name="Origen" id="Origen">
                                    <option value="<%=Origen%>">-Selec Origen-</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT * FROM tb_origen;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            out.println(e.getMessage());
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
                    <br/>

                    <div class="row">                        

                        <div class="col-sm-4">
                            <div>
                                <label class="control-label col-sm-3 float-left" for="fecha_ini">Conceptos:</label>
                            </div>
                            <div class="col-sm-9">
                                <select class="form-control" name="ClaCli" id="ClaCli" required >
                                    <option value="">-Seleccione Concepto Mov.-</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT F_IdCon, CONCAT('[', F_IdCon, ']  ', F_DesCon) AS F_DesCon FROM tb_coninv WHERE F_IdCon IN (1, 51);");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            out.println(e.getMessage());
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                        <div class="col-sm-7">
                            <div class="col-sm-4">
                                <a href="ConsultasMoviCompra.jsp" class="btn btn-block btn-warning">Limpiar&nbsp;<label class="glyphicon glyphicon-trash"></label></a>
                            </div>
                            <div class="col-sm-4">
                                <button class="btn btn-block btn-success" id="btn_capturar">Mostrar&nbsp;<label class="glyphicon glyphicon-search"></label></button>                        
                            </div>
                            </form>
                        </div>
                    </div> 

                </div>   
                <br/>


                <form action="" method="post" id="formCambioFechas">
                    <div class="panel-body">
                        <% if (ClaCli.equals("1")) { %>
                        <div class="panel panel-success">
                            <div style="width:100%; height:600px; overflow:auto;">
                                <div class="panel-body table-responsive">
                                    <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosCompras">
                                        <thead>
                                            <tr>
                                                <td class="style1">Fecha</td>
                                                <td>Des/Mov</td>
                                                <td>Clave</td>
                                                <td>ClaveSS</td>                                            
                                                <td>Nombre genérico</td>
                                                <td>Descripción</td>
                                                <td>Presentación</td>
                                                <td>Lote</td>
                                                <td>Caducidad</td>
                                                <td>Cantidad</td>
                                                <td>Origen</td>
                                                <td>Remision</td>
                                                <td>Proveedor</td>
                                                <td>OC</td>
                                                <td>Contrato</td>

                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                try {
                                                    con.conectar();
                                                    try {

                                                        String FechaFol = "", FechaFol2 = "", Clave = "", Concep = "", Query = "", Query2 = "", AND = "", ANDOrigen = "";
                                                        int ban = 0, ban1 = 0, ban2 = 0;

                                                        if (fecha_ini != "" && fecha_fin != "") {
                                                            ban1 = 1;
                                                            FechaFol = " c.F_FecApl between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                                        }

                                                        if ((clave != "") && (ban1 != 1) && Origen.equals("0")) {
                                                            Query = " c.F_ClaPro ='" + clave + "' ";
                                                        } else if ((clave == "") && (ban1 == 1) && Origen.equals("0")) {
                                                            Query = "" + FechaFol + "";
                                                        } else if ((clave == "") && (ban1 != 1) && (!Origen.equals("0"))) {
                                                            Query = " l.F_Origen='" + Origen + "' ";
                                                        } else if ((clave != "") && (ban1 == 1) && Origen.equals("0")) {
                                                            Query = " c.F_ClaPro ='" + clave + "' AND " + FechaFol + "";
                                                        } else if ((clave != "") && (ban1 == 1) && (!Origen.equals("0"))) {
                                                            Query = " c.F_ClaPro ='" + clave + "' AND " + FechaFol + " AND l.F_Origen='" + Origen + "' ";
                                                        }
                                                        String query = "SELECT 'Entrada por compra' AS F_DesCon, c.F_ClaPro, tb_medica.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS 'F_FecCad', SUM( F_CanCom ) 'F_CantMov', O.F_DesOri, c.F_FolRemi, p.F_NomPro, c.F_OrdCom, DATE_FORMAT( F_FecApl, '%d/%m/%Y' ) AS 'Fecha', tb_medica.F_ClaProSS AS clavess, c.F_ClaDoc, tb_medica.F_PrePro, tb_medica.F_NomGen, IFNULL( ped.F_Contratos, '' ) AS F_Contratos	FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar, 	F_Origen, F_Proyecto FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m ON l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica ON tb_medica.F_ClaPro = c.F_ClaPro INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri LEFT JOIN ( SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro WHERE " + Query + " GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                                                        ResultSet rset = con.consulta(query);
                                                        while (rset.next()) {
                                            %>
                                            <tr>
                                                <td><%=rset.getString(11)%></td>
                                                <td><%=rset.getString(1)%></td>
                                                <td><%=rset.getString(2)%></td>
                                                <td><%=rset.getString(12)%></td>
                                                <td><%=rset.getString(15)%></td>
                                                <td><%=rset.getString(3)%></td>
                                                <td><%=rset.getString(14)%></td>                                            
                                                <td><%=rset.getString(4)%></td>                                            
                                                <td><%=rset.getString(5)%></td>
                                                <td><%=rset.getString(6)%></td>
                                                <td><%=rset.getString(7)%></td>
                                                <td><%=rset.getString(8)%></td>
                                                <td><%=rset.getString(9)%></td>
                                                <td><%=rset.getString(10)%></td>
                                                <td><%=rset.getString(16)%></td>
                                            </tr>
                                            <%                                                        }
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
                        <!-- </div>-->
                        <%} else if (ClaCli.equals("51")) {%>
                        <div class="panel panel-success">
                            <div class="panel-body table-responsive">
                                <div style="width:100%; height:600px; overflow:auto;">

                                    <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosCompras">
                                        <thead>
                                            <tr>
                                                <td>Fecha</td>
                                                <td>Folio</td>
                                                <td>ClaveUni</td>
                                                <td>Unidad</td>
                                                <td>Clave</td>
                                                <td>ClaveSS</td>
                                                <td>Nombre genérico</td>
                                                <td>Descripción</td>
                                                <td>Presentación</td>
                                                <td>Lote</td>
                                                <td>Caducidad</td>
                                                <td>Cantidad</td>
                                                <td>Origen</td>
                                                <td>Movimiento</td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                try {
                                                    con.conectar();
                                                    try {

                                                        String FechaFol = "", FechaFol2 = "", Clave = "", Concep = "", Query = "", Query2 = "", AND = "", ANDOrigen = "";
                                                        int ban = 0, ban1 = 0, ban2 = 0;

                                                        if (fecha_ini != "" && fecha_fin != "") {
                                                            ban1 = 1;
                                                            FechaFol = " v.FecEnt BETWEEN '" + fecha_ini + "' AND '" + fecha_fin + "' ";
                                                        }

                                                        if ((clave != "") && (ban1 != 1) && Origen.equals("0")) {
                                                            Query = " v.clave ='" + clave + "' ";
                                                        } else if ((clave == "") && (ban1 == 1) && Origen.equals("0")) {
                                                            Query = "" + FechaFol + "";
                                                        } else if ((clave == "") && (ban1 != 1) && (!Origen.equals("0"))) {
                                                            Query = " v.origen ='" + Origen + "' ";
                                                        } else if ((clave != "") && (ban1 == 1) && Origen.equals("0")) {
                                                            Query = " v.clave = '" + clave + "' AND " + FechaFol + "";
                                                        } else if ((clave != "") && (ban1 == 1) && (!Origen.equals("0"))) {
                                                            Query = " v.clave = ='" + clave + "' AND " + FechaFol + " AND v.origen = '" + Origen + "' ";
                                                        }
                                                        System.out.println(Query + "cpondicion");
                                                        // String query = "SELECT f.F_FecEnt AS FecEnt, f.F_ClaDoc AS folio, f.F_ClaCli AS Clave_Unidad, u.F_NomCli AS Unidad, f.F_ClaPro AS clave, md.F_ClaProSS, md.F_DesPro, l.F_ClaLot AS lote, l.F_FecCad AS caducidad, Sum( f.F_CantSur ) AS surtido, o.F_DesOri, md.F_NomGen AS Nombre_Generico, md.F_PrePro AS Presentación, 'Salida por facturación' AS concepto FROM tb_factura AS f INNER JOIN tb_uniatn AS u ON f.F_ClaCli = u.F_ClaCli INNER JOIN tb_medica AS md ON f.F_ClaPro = md.F_ClaPro LEFT JOIN tb_obserfact ob ON f.F_ClaDoc = ob.F_IdFact AND f.F_Proyecto = ob.F_Proyecto INNER JOIN tb_lote l ON f.F_Lote = l.F_FolLot AND f.F_ClaPro = l.F_ClaPro AND f.F_Ubicacion = l.F_Ubica INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id WHERE " + Query + " AND f.F_StsFact = 'A' AND f.F_CantSur > 0 AND f.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY f.F_ClaCli, f.F_ClaPro, f.F_FecEnt, f.F_ClaDoc, l.F_Proyecto, f.F_Lote, l.F_Origen ORDER BY f.F_FecEnt ASC, f.F_ClaDoc ASC, f.F_ClaPro";
                                                        String query = "SELECT v.FecEnt, v.folio, v.Clave_Unidad, v.Unidad, v.clave, v.F_ClaProSS, v.F_DesPro, v.lote, v.caducidad, v.surtido, v.F_DesOri, v.Nombre_Generico, v.`Presentación`, v.concepto, v.origen FROM v_facturacion AS v WHERE " + Query + "";
                                                        System.out.println(query);
                                                        ResultSet rset = con.consulta(query);

                                                        while (rset.next()) {

                                            %>
                                            <tr>
                                                <td><%=rset.getString(1)%></td>
                                                <td><%=rset.getString(2)%></td>
                                                <td><%=rset.getString(3)%></td>
                                                <td><%=rset.getString(4)%></td>
                                                <td><%=rset.getString(5)%></td>
                                                <td><%=rset.getString(6)%></td>
                                                <td><%=rset.getString(12)%></td>
                                                <td><%=rset.getString(7)%></td>
                                                <td><%=rset.getString(13)%></td>
                                                <td><%=rset.getString(8)%></td>
                                                <td><%=rset.getString(9)%></td>
                                                <td><%=rset.getString(10)%></td>
                                                <td><%=rset.getString(11)%></td>
                                                <td><%=rset.getString(14)%></td>
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
                        <!-- </div>-->
                    </div> 
                    <%}%>
                </form>
        </div>
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
        <script src="../js/sweetalert.min.js" type="text/javascript"></script>
        <script src="../js/consultaMovCompras.js" type="text/javascript"></script>

    </body>
</html>

