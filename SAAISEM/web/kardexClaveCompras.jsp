<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="javax.naming.NamingException"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.medalfa.saa.querys.kardexQuerys"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="com.medalfa.saa.db.Source"%>
<%@page import="com.medalfa.saa.db.ConnectionManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%request.setCharacterEncoding("UTF-8"); %>
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
    String usua = "", tipo = "", IdUsu = "", Proyecto = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        tipo = (String) sesion.getAttribute("Tipo");
        Proyecto = (String) sesion.getAttribute("Proyecto");
    } else {
        response.sendRedirect("indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();

    String Clave = "", Proyecto2 = "", CB = "", Descripcion = "", accion = "";
    try {
        Clave = request.getParameter("Clave");
        Descripcion = request.getParameter("Descrip");
        Proyecto2 = request.getParameter("Proyecto2");
        CB = request.getParameter("CB");
        accion = request.getParameter("accion");
    } catch (Exception e) {
        Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);

    }
    if (Clave == null) {
        Clave = "";
    }
    if (Proyecto2 == null) {
        Proyecto2 = "";
    }
    if (CB == null) {
        CB = "";
    }
    if (Descripcion == null) {
        Descripcion = "";
    }
    if (accion == null) {
        accion = "";
    }

    String qryLote = "";

    if (!accion.equals("")) {

        try {
           
            con.conectar();
            if (request.getParameter("accion").equals("CB")) {
                con.conectar();
                ResultSet rset = con.consulta("select F_ClaPro from tb_cb where F_Cb = '" + request.getParameter("CB") + "' group by F_ClaPro");
                while (rset.next()) {
                    Clave = rset.getString(1);
                }

                qryLote = ("select F_ClaPro, F_ClaLot, DATE_FORMAT(F_FecCad, '%d/%m/%Y') from tb_lote where F_Cb = '" + request.getParameter("CB") + "' group by F_FecCad, F_ClaLot");
            }
            if (request.getParameter("accion").equals("Clave") ||  request.getParameter("accion").equals("Buscar")) {

                if ((!(Descripcion.equals(""))) || (!(Descripcion.equals(null)))) {
                    ResultSet rsetDesc = con.consulta("SELECT F_ClaPro FROM tb_medica m WHERE F_DesPro =  '" + Descripcion + "' group by F_ClaPro;");
                    while (rsetDesc.next()) {
                        Clave = rsetDesc.getString(1);
                    }
                    qryLote = ("select F_ClaPro, F_ClaLot, DATE_FORMAT(F_FecCad, '%d/%m/%Y') from tb_lote where F_ClaPro = '" + Clave + "' group by F_FecCad, F_ClaLot");
                    CB = "";
                } else {
                    qryLote = ("select F_ClaPro, F_ClaLot, DATE_FORMAT(F_FecCad, '%d/%m/%Y') from tb_lote where F_ClaPro = '" + request.getParameter("Clave") + "' group by F_FecCad, F_ClaLot");
                    CB = "";
                }

            }
        } catch (Exception e) {
            Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/select2.css" rel="stylesheet" type="text/css"/>
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="jspf/menuPrincipalCompra.jspf" %>
        </div>
        <div style="width: 90%; margin: auto;">
            <h3>
                Kardex de Insumo
            </h3>

            <div class="panel panel-success">
                <div class="panel-heading">
                    Criterios de Búsqueda
                </div>
                <div class="panel-body">
                    <form name="FormKardex" action="kardexClaveCompras.jsp" method="Post">
                        <div class="row">
                            <h4 class="col-sm-1">Descripción</h4>
                            <div class="col-sm-2">
                                <input class="form-control" name="Descrip" id="Descrip" type="text" value="<%=Descripcion%>"/>
                            </div>
                            <div class="col-sm-1">
                                <button class="btn btn-success" name="accion" value="Clave"><span class="glyphicon glyphicon-search" ></span></button>
                            </div> 

                            <h4 class="col-sm-1">C.B.</h4>
                            <div class="col-sm-2">
                                <input class="form-control" name="CB" id="CB" type="text" value="<%=CB%>"/>
                            </div>
                            <div class="col-sm-1">
                                <button class="btn btn-success" name="accion" value="CB"><span class="glyphicon glyphicon-search" ></span></button>
                            </div> 
                            <h4 class="col-sm-1">Clave</h4>
                            <div class="col-sm-2">
                                <input class="form-control" name="Clave" id="Clave" type="text" value="<%=Clave%>"/>
                            </div>
                            <div class="col-sm-1">
                                <button class="btn btn-success" name="accion" value="Clave"><span class="glyphicon glyphicon-search" ></span></button>
                            </div>
                        </div>
                        <div class="row">
                            <h4 class="col-sm-1">Lote</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="Lote" id="Lote" onchange="cambiaLoteCadu(this);">
                                    <option value="">-Lote-</option>
                                    <%
                                        if (!accion.equals("")) {
                                            try {
                                                con.conectar();
                                                ResultSet rsetLote = con.consulta(qryLote);
                                                while (rsetLote.next()) {
                                    %>
                                    <option><%=rsetLote.getString(2)%></option>
                                    <%
                                                }

                                            } catch (Exception e) {
                                                Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <h4 class="col-sm-1">Caducidad</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="Cadu" id="Cadu">
                                    <option value="">-Caducidad-</option>
                                    <%
                                        if (!accion.equals("")) {
                                            try {
                                                con.conectar();
                                                ResultSet rsetLote = con.consulta(qryLote);
                                                while (rsetLote.next()) {
                                    %>
                                    <option><%=rsetLote.getString(3)%></option>
                                    <%
                                                }

                                            } catch (Exception e) {
                                                Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <h4 class="col-sm-1">Origen</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="F_Origen" id="F_Origen">
                                    <%
                                        if (!accion.equals("")) {
                                            try {
                                                con.conectar();
                                                ResultSet rset3 = con.consulta("select F_ClaOri, F_DesOri from tb_origen");
                                                while (rset3.next()) {
                                    %>
                                    <option value="<%=rset3.getString("F_ClaOri")%>"><%=rset3.getString("F_DesOri")%></option>
                                    <%
                                                }

                                            } catch (Exception e) {
                                                Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <!--h4 class="col-sm-1">Proyecto</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="Proyecto2" id="Proyecto2">
                                    <option value="0">-- Seleccione --</option>
                            <%
                                /* try {
                                    con.conectar();
                                    ResultSet rset3 = con.consulta("SELECT F_Id, F_DesProy FROM tb_proyectos;");
                                    while (rset3.next()) {*/
                            %>
                            <option value="<%//=rset3.getString(1)%>"><%//=rset3.getString(2)%></option>
                            <%
                                /*}
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }*/
                            %>
                        </select>
                    </div-->
                            <div class="col-sm-3">
                                <button class="btn btn-success form-control" name="accion" value="Buscar">Buscar</button>
                            </div>
                        </div>
                        <div class="row">
                            <!--                            <div class="col-sm-5">
                                                            <button class="btn btn-success form-control" name="accion" value="Buscar">Buscar</button>
                                                        </div>-->
                            <!--                            <div class="col-sm-5">
                                                            <a class="btn btn-success form-control" a href="gnrKardexClave.jsp?Clave=<%=Clave%>&Lote=<%=request.getParameter("Lote")%>&Cadu=<%=request.getParameter("Cadu")%>&Btn=<%=request.getParameter("accion")%>">Reporte de Trazabilidad</a>
                                                        </div>-->
                        </div>
                    </form>
                    <hr/>
                    <h3>Clave: <%=Clave%></h3>
                    <h4>Lote: 
                        <%
                            if (request.getParameter("Lote") != null) {
                                out.println(request.getParameter("Lote"));
                            }
                        %>
                    </h4>
                    <h4>Caducidad: 
                        <%
                            if (request.getParameter("Cadu") != null) {
                                out.println(request.getParameter("Cadu"));
                            }
                        %>
                        <%
                            if (!accion.equals("")) {
                                try {
                                    con.conectar();
                                    ResultSet rset = con.consulta("select F_DesPro from tb_medica where F_ClaPro = '" + Clave + "'");
                                    while (rset.next()) {
                                        out.println("<h4>" + rset.getString(1) + "</h4>");
                                    }

                                } catch (Exception e) {
                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                    }
                                }
                            }
                        %>
                        <br/>
                        <h4>Existencia Actual</h4>
                        <%
                            if (!accion.equals("")) {
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    ResultSet rsetUbi = null;
                                    String ProyectoExit = "", ubicacionesExcluir = "";

                                    rsetUbi = con.consulta("SELECT F_ClaUbi FROM tb_ubicanomostrar;");

                                    if (rsetUbi.next()) 
                                    {
                                       ubicacionesExcluir = rsetUbi.getString("F_ClaUbi");
                                    }

                                    if ((!(Proyecto2.equals("0"))) || (!(Proyecto2.equals("")))) {
                                        ProyectoExit = " AND F_Proyecto='" + Proyecto2 + "' ";
                                    }
                                    if (request.getParameter("accion").equals("Clave")) {
                                        rset = con.consulta("select SUM(F_ExiLot) from tb_lote where F_ClaPro = '" + Clave + "'  and F_ExiLot > 0 AND F_Ubica NOT IN ( "+ubicacionesExcluir+" );");
                                    } else {
                                        rset = con.consulta("select SUM(F_ExiLot) from tb_lote where F_ClaPro = '" + Clave + "' and F_ClaLot ='" + request.getParameter("Lote") + "' and F_FecCad = STR_TO_Date('" + request.getParameter("Cadu") + "', '%d/%m/%Y') and F_Origen = '" + request.getParameter("F_Origen") + "'  and F_ExiLot >0 AND F_Ubica NOT IN ( "+ubicacionesExcluir+" );");
                                    }

                                    while (rset.next()) {
                                        String Total = "0";
                                        Total = rset.getString(1);
                                        if (Total == null) {
                                            Total = "0";
                                        }
                        %>
                        <h4>Total: <%=formatter.format(Integer.parseInt(Total))%></h4>
                        <br/>
                        <%
                                    }

                                } catch (Exception e) {
                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                    }
                                }
                            }
                        %>
                        <div style="width:100%; margin: auto" class="panel panel-success ">
                            <div class="panel-heading">
                                <h3>Ingresos / Egresos</h3>
                            </div>
                            <div class="panel-body table-responsive">

                                <table class="table table-bordered table-condensed table-striped" width="100%" id="kardexTab2">
                                    <thead> 
                                        <tr>
                                            <td>No. Mov</td>
                                            <td>Usuario</td>
                                            <td>Documento</td>
                                            <td>Remisión</td>
                                            <td>Proveedor</td>
                                            <td>Factura</td>
                                            <td>Entrega</td>
                                            <td>Concepto</td>
                                            <td>Clave</td>
                                            <td>Lote</td>
                                            <td>Caducidad</td>
                                            <td>Cant.</td>
                                            <td>Ubicacion</td>
                                            <td>Proyecto</td>
                                            <td>Fecha</td>
                                            <td>Hora</td>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                             if (!accion.equals("")) {
                                            String queryKardex = "";
                                            String parametrosKardex = "";

                                            if (request.getParameter("accion").equals("Clave")) {
                                                queryKardex = kardexQuerys.SEARCH_KARDEX_BY_CLAVE;
                                                parametrosKardex = "clave";
                                            } else {
                                                queryKardex = kardexQuerys.SEARCH_KARDEX_BY_CLAVE_LOTE_CADUCIDAD;
                                                parametrosKardex = "lote_caducidad";
                                            }
                                           

                                                try (Connection connection = ConnectionManager.getManager(Source.SAA_AUDIT).getConnection();
                                                        PreparedStatement ps = connection.prepareStatement(queryKardex)) {
                                                    if (parametrosKardex.equals("lote_caducidad")) {
                                                        ps.setString(1, request.getParameter("Lote"));
                                                        ps.setString(2, request.getParameter("Cadu"));
                                                        ps.setString(3, Clave);
                                                        ps.setString(4, Clave);
                                                        ps.setString(5, Clave);
                                                        ps.setString(6, Clave);

                                                    } else {
                                                        ps.setString(1, Clave);
                                                        ps.setString(2, Clave);
                                                        ps.setString(3, Clave);
                                                        ps.setString(4, Clave);

                                                    }

                                                    try (ResultSet rs = ps.executeQuery()) {
                                                        while (rs.next()) {

                                        %>
                                        <tr>
                                            <td><%=rs.getInt("noMov")%></td>
                                            <td><%=rs.getString("usuario")%></td>
                                            <td><%=rs.getString("ori")%></td>
                                            <td><%=rs.getString("remision")%></td>
                                            <td><%=rs.getString("proveedor")%></td>
                                            <td><%=rs.getString("folioSalida")%></td>
                                            <td><%=rs.getString("puntoEntrega")%></td>                                        
                                            <td><%=rs.getString("concepto")%></td>                                        
                                            <td><%=rs.getString("clave")%>
                                            <td><%=rs.getString("lote")%>
                                            <td><%=rs.getString("caducidad")%>
                                            <td><%=formatter.format(rs.getInt("cantidadMovimiento"))%></td>
                                            <td><%=rs.getString("ubicacion")%></td>
                                            <td><%=rs.getString("proyecto")%></td>
                                            <td><%=rs.getString("fechaMovimiento")%></td>
                                            <td><%=rs.getString("hora")%></td>
                                        </tr>
                                        <%

                                                        }
                                                    }

                                                } catch (SQLException | NamingException ex) {
                                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                                }

                                            }
                                        %>   
                                    </tbody>
                                </table>
                            </div>
                        </div>


                        <div style="width:100%; margin: auto" class="panel panel-body panel-success table-responsive">
                            <h3>Redistribución en Almacén</h3>
                            <table class="table table-bordered table-striped" style="width:100%" id="kardexTab">
                                <thead> 
                                    <tr>
                                        <td>No. Mov</td>
                                        <td>Usuario</td>

                                        <td>Concepto</td>
                                        <td>Clave</td>
                                        <td>Lote</td>
                                        <td>Caducidad</td>
                                        <td>Cantidad</td>
                                        <td>Ubicacion</td>
                                        <td>Proyecto</td>
                                        <td>Fecha</td>
                                        <td>Hora</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (!accion.equals("")) {
                                            try {
                                                con.conectar();
                                                ResultSet rset = null;
                                                String ProyectoMov = "";
                                                if (!(Proyecto2.equals("0"))) {
                                                    ProyectoMov = " AND l.F_Proyecto='" + Proyecto2 + "' ";
                                                }
                                                if (request.getParameter("accion").equals("Clave")) {
                                                    rset = con.consulta("SELECT m.F_User, m.F_ConMov, c.F_DesCon, m.F_ProMov, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y'), (m.F_CantMov * m.F_SigMov), m.F_CostMov, u.F_DesUbi, DATE_FORMAT(m.F_FecMov, '%d/%m/%Y'), m.F_hora, m.F_DocMov, com.F_FolRemi, m.F_IdMov, l.F_Proyecto, p.F_DesProy FROM tb_movinv m INNER JOIN tb_coninv c ON m.F_ConMov = c.F_IdCon INNER JOIN tb_ubica u ON m.F_UbiMov = u.F_ClaUbi INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot AND m.F_ProMov = l.F_ClaPro AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_compra com ON l.F_FolLot = com.F_Lote LEFT JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id WHERE m.F_ProMov = '" + Clave + "' and (m.F_ConMov=1000 or m.F_ConMov = 1) GROUP BY m.F_IdMov ORDER BY m.F_IdMov;");
                                                } else {
                                                    rset = con.consulta("SELECT m.F_User, m.F_ConMov, c.F_DesCon, m.F_ProMov, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y'), (m.F_CantMov * m.F_SigMov), m.F_CostMov, u.F_DesUbi, DATE_FORMAT(m.F_FecMov, '%d/%m/%Y'), m.F_hora, m.F_DocMov, com.F_FolRemi, m.F_IdMov, l.F_Proyecto, p.F_DesProy FROM tb_movinv m INNER JOIN tb_coninv c ON m.F_ConMov = c.F_IdCon INNER JOIN tb_ubica u ON m.F_UbiMov = u.F_ClaUbi INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot AND m.F_ProMov = l.F_ClaPro AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_compra com ON l.F_FolLot = com.F_Lote LEFT JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id WHERE m.F_ProMov = '" + Clave + "' AND l.F_ClaLot = '" + request.getParameter("Lote") + "' AND l.F_FecCad = STR_TO_Date( '" + request.getParameter("Cadu") + "', '%d/%m/%Y' ) AND ( m.F_ConMov = 1000 OR m.F_ConMov = 1 ) AND F_Origen = '" + request.getParameter("F_Origen") + "' GROUP BY m.F_IdMov ORDER BY m.F_IdMov;");
                                                }

                                                while (rset.next()) {
                                                    String Documento = "", Cliente = "", Provoeedor = "", FactRemi = "";
                                                    if (rset.getString(2).equals("1")) {
                                                        ResultSet rset2 = con.consulta("select F_OrdCom, F_Provee from tb_compra where F_ClaDoc = '" + rset.getString(12) + "' ");
                                                        while (rset2.next()) {
                                                            Documento = rset2.getString(1);
                                                            ResultSet rset3 = con.consulta("select F_NomPro from tb_proveedor where F_ClaProve = '" + rset2.getString(2) + "' ");
                                                            while (rset3.next()) {
                                                                Provoeedor = rset3.getString(1);
                                                            }
                                                        }
                                                    }
                                                    if (rset.getString(2).equals("51")) {
                                                        ResultSet rset2 = con.consulta("select F_NomCli, F_ClaDoc from tb_factura f, tb_uniatn u where u.F_ClaCli = f.F_ClaCli and F_ClaDoc = '" + rset.getString(12) + "' ");
                                                        while (rset2.next()) {
                                                            Cliente = rset2.getString(1);
                                                            FactRemi = rset2.getString(2);
                                                        }
                                                    }
                                                    if (rset.getString(2).equals("3")) {
                                                        ResultSet rset2 = con.consulta("select F_NomCli, F_ClaDoc from tb_factura f, tb_uniatn u where u.F_ClaCli = f.F_ClaCli and F_ClaDoc = '" + rset.getString(12) + "' ");
                                                        while (rset2.next()) {
                                                            Cliente = rset2.getString(1);
                                                            FactRemi = rset2.getString(2);
                                                        }
                                                    }
                                    %>
                                    <tr>
                                        <td><%=rset.getString("F_IdMov")%></td>
                                        <td><%=rset.getString(1)%></td>

                                        <td><%=rset.getString(3)%></td>
                                        <td><%=rset.getString(4)%></td>
                                        <td><%=rset.getString(5)%></td>
                                        <td><%=rset.getString(6)%></td>
                                        <td><%=formatter.format(rset.getInt(7))%></td>
                                        <td><%=rset.getString(9)%></td>
                                        <td><%=rset.getString(16)%></td>
                                        <td><%=rset.getString(10)%></td>
                                        <td><%=rset.getString(11)%></td>
                                    </tr>
                                    <%
                                                }

                                            } catch (Exception e) {
                                                Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, e.getMessage(), e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("kardexClaveCompras.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                                                }
                                            }

                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>

                </div>
            </div>
        </div>

        <%@include file="jspf/piePagina.jspf" %>
    </body>
    <!-- 
    ================================================== -->
    <!-- Se coloca al final del documento para que cargue mas rapido -->
    <!-- Se debe de seguir ese orden al momento de llamar los JS -->
    <script src="js/jquery-1.9.1.js"></script>
    <script src="js/bootstrap.js"></script>
    <script src="js/jquery-ui-1.10.3.custom.js"></script>
    <script src="js/jquery.dataTables.js"></script>
    <script src="js/dataTables.bootstrap.js"></script>
    <script src="js/select2.js" type="text/javascript"></script>
    <script src="js/sweetalert.min.js" type="text/javascript"></script>
    <script src="js/kardex/Kardex.js" type="text/javascript"></script>
    <script>
                                    $(document).ready(function () {
                                        $('#kardexTab').dataTable();
                                        $('#kardexTab2').dataTable({
                                            "processing": true,
                                            "button": 'aceptar',
                                            "scrollInfinite": true,
                                            "scrollCollapse": true,
                                            "scrollY": "500px",
                                            "scrollX": true,
                                            "pagination": true,
                                            "paging": true,
                                            "ordering": true,
                                            "info": true
                                        });
                                    });

                                    function cambiaLoteCadu(elemento) {
                                        var indice = elemento.selectedIndex;
                                        document.getElementById('Cadu').selectedIndex = indice;
                                    }
    </script>
</html>

