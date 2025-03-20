<%-- 
    Document   : verFoliosIsem2017.jsp
    Created on : 14-jul-2014, 14:48:02
    Author     : Americo
--%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="conn.ConectionDB"%>
<%@page import="ISEM.CapturaPedidos"%>
<%@page import="javax.servlet.http.HttpSession"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%java.text.DateFormat df1 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "", IdUsu = "", horEnt = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();
    String NoCompra = "", Fecha = "";

    Fecha = request.getParameter("Fecha");
    if (Fecha == null) {
        Fecha = "";
    }
    NoCompra = request.getParameter("NoCompra");

    if (Fecha == null) {
        NoCompra = "";
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS</title>
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/select2.css" rel="stylesheet">
    </head>
    <body onload="focusLocus();">
        <div class="container">
            <div class="row">
                <h1>MEDALFA</h1>
                <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
                <%@include file="jspf/menuPrincipalCompra.jspf" %>
                <h4>Ver OC</h4>
            </div>
            <br/>
            <div class="row">
                <form method="post" action="verFoliosMedalfaCompras.jsp">
                    <div class="row">
                        <label class="col-sm-1">
                            <h4>Proveedor</h4>
                        </label>
                        <div class="col-sm-6">
                            <select class="form-control" name="Proveedor" id="Proveedor" onchange="SelectProve(this.form);
                                    document.getElementById('Fecha').focus()">
                                <option value="">--Proveedor--</option>
                                <%                                    try {
                                        con.conectar();
                                        ResultSet rset = con.consulta("SELECT F_ClaProve, F_NomPro FROM tb_proveedor p, tb_pedido_sialss o WHERE p.F_ClaProve = o.F_Provee GROUP BY o.F_Provee ORDER BY F_NomPro");
                                        while (rset.next()) {
                                %>
                                <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                        }
                                    }
                                %>

                            </select>
                        </div>

                        <label class="col-sm-3">
                            <h4>Fecha de Entrega:</h4>
                        </label>
                        <div class="col-sm-2">
                            <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="Fecha" name="Fecha" value="<%=Fecha%>" onchange="document.getElementById('Hora').focus()" />
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                        <label class="col-sm-1">
                            <h4>Usuario:</h4>
                        </label>
                        <div class="col-sm-6">
                            <select class="form-control" name="Usuario" id="Usuario" onchange="">
                                <option value="">--Usuarios--</option>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rset = con.consulta("SELECT u.F_IdUsu, u.F_Usu FROM tb_usuariocompra u, tb_pedido_sialss p WHERE  u.F_IdUsu ="+IdUsu+"  GROUP BY F_IdUsu;");
                                        while (rset.next()) {
                                %>
                                <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                        }
                                    }
                                %>

                            </select>
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                        <button class="btn btn-success btn-block" name="accion" value="fecha">Buscar</button>
                    </div>
                </form>
            </div>
        </div>
        <br/>



        <div class="row" style="width: 90%; margin: auto;">
            <div class="col-sm-6">
                <div class="panel-default">
                <form method="post">
                    <div class="panel-heading">
                    <input value="<%=Fecha%>" name="Fecha" class="hidden"/>
                    <input value="<%=request.getParameter("Usuario")%>" name="Usuario"  class="hidden"/>
                    <input value="<%=request.getParameter("Proveedor")%>" name="Proveedor"  class="hidden"/>
                    <label class="col-sm-12">
                        <h4>Órdenes de Compra: </h4>
                    </label>
                    </div>
                    <div class="panel-body table-responsive">
                    <table class="table table-bordered table-condensed table-striped" id="datosCompras">
                        <thead>
                            <tr>
                                <td>No. Orden</td>
                                <td>Capturó</td>
                                <td>Proveedor</td>
                                <td>Fecha Entrega</td>
                                <td>Hora entrega</td>
                                <td>Ver</td>
                               
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                String fecha = "", Usuario = "", Proveedor = "";
                                fecha = request.getParameter("Fecha");
                                Usuario = request.getParameter("Usuario");
                                Proveedor = request.getParameter("Proveedor");
                                try {
                                    con.conectar();
                                    ResultSet rset = null;

                                    if (!(Proveedor.equals(""))) {
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedido_sialss o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_Provee = '" + request.getParameter("Proveedor") + "' AND F_StsPed < 3 group by o.F_NoCompra;");

                                    } else if (!(fecha.equals(""))) {
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedido_sialss o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_FecSur =  '" + fecha + "%' AND F_StsPed < 3 group by o.F_NoCompra;");

                                    } else if (!(Usuario.equals(""))) {
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedido_sialss o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_IdUsu = '" + request.getParameter("Usuario") + "' AND F_StsPed < 3 group by o.F_NoCompra");
                                    }

                                    while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td>
                                    <button class="btn btn-success text-center" name="NoCompra" value="<%=rset.getString(1)%>"><span class="glyphicon glyphicon-search"></span></button>
                                </td>
                               
                            </tr>
                            <%
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                    </div>
                </form>
                </div>
            </div>


            <div class="col-sm-6">
                <div class="panel-info">
                    <div class="panel-heading">                     
                        <label> Orden: <%=NoCompra%></label>
                        <a class="btn btn-default" target="_blank" href="imprimeOrdenCompra.jsp?ordenCompra=<%=NoCompra%>"><span class="glyphicon glyphicon-print"></span></a>
                    </div>

                    <div class="panel-body ">
                        <%                
                            try {
                                con.conectar();
                                ResultSet rset = con.consulta("SELECT o.F_NoCompra, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y'), F_HorSur, F_Usu, F_StsPed, rec.F_Recibido, o.F_Zona, o.F_Tipo, DATE_FORMAT(o.F_Fecha, '%d/%m/%Y') FROM tb_pedido_sialss o INNER JOIN tb_proveedor p ON o.F_Provee = p.F_ClaProve INNER JOIN tb_usuariocompra u ON u.F_IdUsu = o.F_IdUsu INNER JOIN ( SELECT F_NoCompra, SUM(F_Recibido) AS F_Recibido FROM tb_pedido_sialss o WHERE F_NoCompra = '" + NoCompra + "' ) rec ON o.F_NoCompra = rec.F_NoCompra WHERE o.F_NoCompra = '" + NoCompra + "' AND F_StsPed < 3 GROUP BY o.F_NoCompra;");
                                while (rset.next()) {
                        %>

                        <form name="FormBusca" action="CapturaPedidos" method="post">
                            <div class="row">
                                <h4 class="col-sm-3">Orden No. </h4>
                                <div class="col-sm-4">
                                    <input class="form-control" value="<%=rset.getString(1)%>" name="NoCompra" id="NoCompra" readonly="" />
                                </div>
                                <h4 class="col-sm-2">Capturó: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString("F_Usu")%>" readonly="" />
                                </div>

                            </div>
                            <%
                                if (rset.getString("F_StsPed").equals("2")) {
                            %>
                            <h4 class="text-success">FOLIO CANCELADO</h4>
                            <%
                                }
                            %>
                            <div class="row">
                                <h4 class="col-sm-3">Proveedor: </h4>
                                <div class="col-sm-9">
                                    <input class="form-control" value="<%=rset.getString(2)%>" readonly="" />
                                </div>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-3">Fecha de Captura: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(10)%>" readonly="" />
                                </div>
                                <h4 class="col-sm-3">Fecha de Entrega: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(3)%>" readonly="" />
                                </div>
                                <!--h4 class="col-sm-3">Hora de Entrega: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(4)%>" readonly="" />
                                </div-->
                            </div>
                            <div class="row">
                                <h4 class="col-sm-3">Zona: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(8)%>" readonly="" />
                                </div>
                                <h4 class="col-sm-3">Tipo OC: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(9)%>" readonly="" />
                                </div>
                            </div>
                            <%
                                //if (usua.equals(rset.getString("F_Usu"))) {
                                if (!tipo.equals("14")){
                                    
                               
                                if ((!rset.getString("F_StsPed").equals("2")) && (rset.getInt(7) == 0)) {
                            %>
                            <br/>
                            <textarea class="form-control" name="Observaciones" id="Observaciones" placeholder="Observaciones para cancelar"></textarea>
                            <br>
                            <div class="row">
                                <div class="col-sm-6">
                                    <button class="btn btn-success btn-block" name="accion" value="cancelaOrden" onclick="return CancelaCompra();">CANCELAR ORDEN DE COMPRA</button>
                                </div>
                                <div class="col-sm-6">
                                    <button type="button" class="btn btn-info btn-block" data-toggle="modal" data-target="#modalCambioFecha" id="btnRecalendarizar" >CAMBIO DE FECHA Y HORA ENTREGA</button>
                                </div>
                            </div>
                            <%
                            } else if (!rset.getString("F_StsPed").equals("2")) {
                            %>
                            <br />
                            <div class="row">
                                <div class="col-sm-12">
                                    <button class="btn btn-info btn-block" name="accion" value="AbrirOrden">ABRIR ORDEN DE COMPRA</button>
                                </div>
                            </div>
                            <%
                                }
                                }
                                if (rset.getString("F_StsPed").equals("2")) {
                                    ResultSet rset2 = con.consulta("select F_Observaciones from tb_obscancela where F_NoCompra = '" + NoCompra + "' ");
                                    while (rset2.next()) {
                            %>
                            <br/>
                            <textarea class="form-control" name="Observa" id="Observa" readonly=""><%=rset2.getString(1)%></textarea>
                            <br>
                            <%
                                    }
                                }
                            %>

                            <br/>
                            <%
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                    }
                                }
                            %>
                        </form>
                    </div>
                    <div class="panel-footer etable-responsive">
                        <table class="table table-bordered table-condensed table-striped" id="datostableoc">
                            <thead>
                                <tr>
                                    <td><strong>Clave</strong></td>
                                    <td><strong>Descripción</strong></td>
                                    <td><strong>Evento</strong></td>
                                    <td><strong>Cantidad</strong></td>
                                    <td><strong>Recibido</strong></td>
                                   
                                </tr>
                            </thead>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = con.consulta("SELECT s.F_Clave, m.F_DesPro, s.F_Cant, IFNULL(com.F_CanCom, 0) AS Ingreso, s.F_Cant - IFNULL(com.F_CanCom, 0) AS Dif, F_Recibido, s.F_StsPed, F_IdIsem, F_Obser FROM tb_pedido_sialss s INNER JOIN tb_medica m ON s.F_Clave = m.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM(F_CanCom) AS F_CanCom FROM tb_compra WHERE F_OrdCom = '" + NoCompra + "' GROUP BY F_ClaPro ) AS com ON s.F_Clave = com.F_ClaPro WHERE F_NoCompra = '" + NoCompra + "' AND s.F_StsPed < 3;");
                                    while (rset.next()) {
                            %>
                            <tbody>
                                <tr>
                                    <td><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(2)%></td>
                                    <td><%=rset.getString(9)%></td>
                                    <td>
                                        <input class="hidden" name="CantSol_<%=rset.getString(8)%>" id="CantSol_<%=rset.getString(8)%>" value="<%=formatter.format(rset.getInt(3))%>" />
                                        <%=formatter.format(rset.getInt(3))%>
                                    </td>
                                    <td>
                                        <input class="hidden" name="Recibido_<%=rset.getString(8)%>" id="Recibido_<%=rset.getString(8)%>" value="<%=formatter.format(rset.getInt(4))%>" />
                                        <%=formatter.format(rset.getInt(4))%>
                                    </td>
                                  
                                </tr>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfaCompras.jsp").log(Level.SEVERE, null, e);
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
    <script src="js/jquery-2.1.4.min.js" type="text/javascript"></script>
    <script src="js/bootstrap.js" type="text/javascript" ></script>
    <script src="js/jquery.alphanum.js" type="text/javascript"></script>
    <script src="js/jquery.dataTables.js" type="text/javascript"></script>
    <script src="js/dataTables.bootstrap.js"></script>
    <script src="js/select2.js"></script>
    <script>
                                    $(document).ready(function () {
                                        $('#datosCompras').dataTable();
                                        $('#datostableoc').dataTable();
                                        $('#Proveedor').select2();
                                        $('#Usuario').select2();
                                    });

                                    
                                    function focusLocus() {
                                        document.getElementById('Proveedor').focus();
                                        if ($('#Fecha').val() !== "") {
                                            document.getElementById('Clave').focus();
                                        }
                                    }

                                    function validaClaDes(boton) {
                                        var btn = boton.value;
                                        var prove = $('#Proveedor').val();
                                        var fecha = $('#Fecha').val();
                                        var hora = $('#Hora').val();
                                        var NoCompra = $('#NoCompra').val();
                                        if (prove === "" || fecha === "" || hora === "0:00" || NoCompra === "") {
                                            alert("Complete los datos");
                                            return false;
                                        }
                                        var valor = "";
                                        var mensaje = "";
                                        if (btn === "Clave") {
                                            valor = $('#Clave').val();
                                            mensaje = "Introduzca la clave";
                                        }
                                        if (btn === "Descripcion") {
                                            valor = $('#Descripcion').val();
                                            mensaje = "Introduzca la descripcion";
                                        }
                                        if (valor === "") {
                                            alert(mensaje);
                                            return false;
                                        }
                                        return true;
                                    }

                                   
    </script>
</html>
