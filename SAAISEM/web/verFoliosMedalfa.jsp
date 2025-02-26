<%-- 
    Document   : verFoliosIsem2017.jsp
    Created on : 14-jul-2014, 14:48:02
    Author     : Americo
--%>

<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
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
    int fechaActualVen = 0;
    int activo = 0;
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
                <%if (tipo.equals("13")){%>
           <%@include file="jspf/menuPrincipalCompra.jspf" %>
            <% } %>
                <h4>Ver OC</h4>
            </div>
            <br/>
            <div class="row">
                <form method="post" action="verFoliosMedalfa.jsp">
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
                                        ResultSet rset = con.consulta("SELECT F_ClaProve, F_NomPro FROM tb_proveedor p, tb_pedidoisem2017 o WHERE p.F_ClaProve = o.F_Provee GROUP BY o.F_Provee ORDER BY F_NomPro");
                                        while (rset.next()) {
                                %>
                                <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
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
                                        ResultSet rset = con.consulta("SELECT u.F_IdUsu, u.F_Usu FROM tb_usuariocompra u, tb_pedidoisem2017 p WHERE  u.F_IdUsu = p.F_IdUsu GROUP BY F_IdUsu;");
                                        while (rset.next()) {
                                %>
                                <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
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
            <div >
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
                                <!--<td>Agregar</td>-->
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
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedidoisem2017 o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_Provee = '" + request.getParameter("Proveedor") + "' AND F_StsPed < 3 group by o.F_NoCompra;");

                                    } else if (!(fecha.equals(""))) {
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedidoisem2017 o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_FecSur =  '" + fecha + "%' AND F_StsPed < 3 group by o.F_NoCompra;");

                                    } else if (!(Usuario.equals(""))) {
                                        rset = con.consulta("SELECT o.F_NoCompra, u.F_Usu, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y') F_FecSur, F_HorSur, F_Fecha FROM tb_pedidoisem2017 o, tb_proveedor p, tb_usuariocompra u WHERE u.F_IdUsu = o.F_IdUsu AND o.F_Provee = p.F_ClaProve and o.F_IdUsu = '" + request.getParameter("Usuario") + "' AND F_StsPed < 3 group by o.F_NoCompra");
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
                               
                               
                              <td>
                                    <a href="AgregarMedalfa.jsp?Proyecto=1&DesProyecto=ISEM&Campo=F_ProIsem&OC=< %=rset.getString(1)%>" class="btn btn-success text-center"><span class="glyphicon glyphicon-plus"></span></a>
                                </td>
                        
                            </tr>
                            <%
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                    </div>
                </form>
                </div>
            </div>


            <div >
                <div class="panel-info">
                    <div class="panel-heading">                     
                        <label> Orden: <%=NoCompra%></label>
                        <a class="btn btn-default" target="_blank" href="imprimeOrdenCompra.jsp?ordenCompra=<%=NoCompra%>"><span class="glyphicon glyphicon-print"></span></a>
                    </div>

                    <div class="panel-body ">
                        <%                
                            try {
                                con.conectar();
                                ResultSet rset = con.consulta("SELECT o.F_NoCompra, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y'), F_HorSur, F_Usu, F_StsPed, rec.F_Recibido, o.F_Zona, o.F_Tipo, DATE_FORMAT(o.F_Fecha, '%d/%m/%Y'), o.F_FuenteFinanza,o.F_IdOrigen,IFNULL(o.F_Contratos,'SIN CONTRATO') AS F_Contratos, ori.F_DesOri,o.F_FecSur  FROM tb_pedidoisem2017 o INNER JOIN tb_proveedor p ON o.F_Provee = p.F_ClaProve INNER JOIN tb_usuariocompra u ON u.F_IdUsu = o.F_IdUsu LEFT JOIN tb_origen ori ON o.F_IdOrigen = ori.F_ClaOri INNER JOIN ( SELECT F_NoCompra, SUM(F_Recibido) AS F_Recibido FROM tb_pedidoisem2017 o WHERE F_NoCompra = '" + NoCompra + "' ) rec ON o.F_NoCompra = rec.F_NoCompra WHERE o.F_NoCompra = '" + NoCompra + "' AND F_StsPed < 3 GROUP BY o.F_NoCompra;");
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
                                    <input class="form-control" value="< %=rset.getString(4)%>" readonly="" />
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
                             <div class="row">
                                <h4 class="col-sm-3">Fuente de Financiamiento: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(11)%>" readonly="" />
                                </div>
                               
                            </div>  
                              <%
                                
                                if (rset.getInt(12) == 21) {
                            %>
                            <div class="row">
                                <h4 class="col-sm-3">No de Contrato: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(13)%>" readonly="" />
                                </div>
                              
                               
                            </div> 
                            <div class="row">     
                                <h4 class="col-sm-3">Origen: </h4>
                                <div class="col-sm-3">
                                    <input class="form-control" value="<%=rset.getString(14)%>" readonly="" />
                                </div>
                               
                            </div>
                                
                            <%
                                }
                               
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
                                //}
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
                                    
                                Date date = new Date();
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String strDate = dateFormat.format(date);
                                    Integer fechaActualInt = Integer.parseInt(strDate.replaceAll("-", ""));
                                    fechaActualVen = Integer.parseInt(rset.getString(15).replaceAll("-", ""));
                                     
                                  //  activo = fechaActualVen - fechaActualInt;
                                    System.out.println(fechaActualInt+"********************** " + fechaActualVen);
                                    System.out.println("activo: "+activo);
                                }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                    }
                                }
                            %>
                        </form>
                    </div>
                        
                        
                    <%
                    if (activo >= 0) {
                    %>    
                    <div class="panel-footer etable-responsive">
                        <table class="table table-bordered table-condensed table-striped" id="datostableoc">
                            <thead>
                                <tr>
                                    <td><strong>Clave</strong></td>
                                    <td><strong>Nombre genérico</strong></td>
                                    <td><strong>Descripción</strong></td>
                                    <td><strong>Presentación</strong></td>
                                    <td><strong>Evento</strong></td>
                                    <td><strong>Cantidad</strong></td>
                                    <td><strong>Recibido</strong></td>
                                    <td><strong>Pendiente</strong></td>
                                    <td><strong>Contrato</strong></td>
                                     <%if (tipo.equals("13")){%>
                                    <td><strong>Cant. Modificar</strong></td>
                                    <td><strong>Modificar</strong></td>
                                    <td><strong>Quitar</strong></td>
                                    <% } %>
                                </tr>
                            </thead>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = con.consulta("SELECT s.F_ClaveSS, m.F_DesPro, s.F_Cant, IFNULL(com.F_CanCom, 0) AS Ingreso, s.F_Cant - IFNULL(com.F_CanCom, 0) AS Dif, F_Recibido, s.F_StsPed, F_IdIsem, F_Obser, m.F_PrePro, m.F_NomGen, IFNULL(s.F_Contratos,'') AS F_Contratos  FROM tb_pedidoisem2017 s INNER JOIN tb_medica m ON s.F_Clave = m.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM(F_CanCom) AS F_CanCom FROM tb_compra WHERE F_OrdCom = '" + NoCompra + "' GROUP BY F_ClaPro ) AS com ON s.F_Clave = com.F_ClaPro WHERE F_NoCompra = '" + NoCompra + "' AND s.F_StsPed < 3;");
                                    while (rset.next()) {
                            %>
                            <tbody>
                                <tr>
                                    <td><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(11)%></td>
                                    <td><%=rset.getString(2)%></td>
                                    <td><%=rset.getString(10)%></td>
                                    <td><%=rset.getString(9)%></td>
                                    <td>
                                        <input class="hidden" name="CantSol_<%=rset.getString(8)%>" id="CantSol_<%=rset.getString(8)%>" value="<%=formatter.format(rset.getInt(3))%>" />
                                        <%=formatter.format(rset.getInt(3))%>
                                    </td>
                                    <td>
                                        <input class="hidden" name="Recibido_<%=rset.getString(8)%>" id="Recibido_<%=rset.getString(8)%>" value="<%=formatter.format(rset.getInt(4))%>" />
                                        <%=formatter.format(rset.getInt(4))%>
                                    </td>
                                    <td><%=formatter.format(rset.getInt(5))%></td>
                                    <td><%=rset.getString(12)%></td>
                                    <%if (tipo.equals("13")){
                                        if ((rset.getInt(6) == 0) && (rset.getInt(7) == 1)) {
                                    %>
                                    <td>
                                        <input  id="cantidadM_<%=rset.getString(8)%>" name="cantidadM_<%=rset.getString(8)%>" type="number" min="0" maxlength="4" pattern="^[0-9]+" onkeypress='return validaNumericos(event)' />
                                    </td>
                                    <td>
                                        <button class="btn btn-warning glyphicon glyphicon-edit" name="detalleModificar" value="<%=rset.getString(8)%>" onclick="aplicarModificacion(this)"></button>
                                    </td>
                                    <td>
                                        <button class="btn btn-danger glyphicon glyphicon-remove" name="detalleEliminar" value="<%=rset.getString(8)%>" onclick="aplicarEliminar(this)"></button>
                                    </td>
                                    <%} else {%>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <%}}%>
                                </tr>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            Logger.getLogger("verFoliosMedalfa.jsp").log(Level.SEVERE, null, e);
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                            
                    </div>
                        
                            
                                 <%} else {  %>      

            <div class="row">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <h4 class="col-sm-3">Fecha vencida:</h4>
                            <div class="col-sm-2" style="width: 61%;"><h4>La orden de compra que desea ver ya ha llegado a su fecha de vencimiento si desea continuar porfavor de comunicarse con soporte a compras.</h4></div>
                        </div>
                    </div>
                    <hr/>
                </div>
            </div> 
                            
                    <% } %>        
                            
                </div>
                            
                            
                            
                            
            </div>
        </div>
            <!-- Modal -->
            <div class="modal fade" id="modalCambioFecha" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <form name="FormBusca" action="CapturaPedidos" method="post">
                            <div class="modal-header">
                                <div class="row">
                                    <h4 class="col-sm-12">Cambiar Fecha Oc <%=NoCompra%></h4>
                                    <input type="hidden" class="form-control" name="NoCompra" id="NoCompra" value="<%=NoCompra%>" />

                                </div>
                            </div>
                            <div class="modal-body">
                                <h4 class="modal-title" id="myModalLabel">Seleccionar fecha:</h4>
                                <div class="row">
                                    <div class="col-sm-12">
                                        <input type="date" class="form-control" required name="F_FecEnt" id="F_FecEnt" />
                                    </div>
                                </div>
                                <h4 class="modal-title" id="myModalLabel">Seleccionar Hora:</h4>
                                <div class="col-sm-12">
                                    <select class="form-control" id="HoraN" name="HoraN" onchange="document.getElementById('Clave').focus()">
                                        <%
                                            for (int i = 0; i < 24; i++) {
                                                if (i != 24) {
                                        %>
                                        <option value="<%=i%>:00"
                                                <%
                                                    if (horEnt.equals(i + ":00")) {
                                                        out.println("selected");
                                                    }
                                                %>
                                                ><%=i%>:00</option>
                                        <option value="<%=i%>:30"
                                                <%
                                                    if (horEnt.equals(i + ":30")) {
                                                        out.println("selected");
                                                    }
                                                %>
                                                ><%=i%>:30</option>
                                        <%
                                        } else {
                                        %>
                                        <option value="<%=i%>:00"
                                                <%
                                                    if (horEnt.equals(i + ":00")) {
                                                        out.println("selected");
                                                    }
                                                %>
                                                ><%=i%>:00</option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>
                                <div style="display: none;" class="text-center" id="Loader">
                                    <img src="imagenes/ajax-loader-1.gif" height="150" />
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                                    <button class="btn btn-success" name="accion" value="recalendarizar" onclick="return confirm('¿Seguro que desea cambiar la fecha y hora?')">Confirmar</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <!-- Modal -->
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

                                    function CancelaCompra() {
                                        var confirma = confirm("¿Seguro que desea cancelar la orden? ");
                                        if (confirma === true) {
                                            var obser = $('#Observaciones').val();
                                            if (obser === "") {
                                                alert('Favor de llenar el campo de observaciones');
                                                document.getElementById('Observaciones').focus();
                                                return false;
                                            } else {
                                                return true;
                                            }
                                        } else {
                                            return false;
                                        }
                                    }
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

                                    function validaCaptura() {

                                        var ClaPro = $('#ClaPro').val();
                                        var DesPro = $('#DesPro').val();
                                        var CanPro = $('#CanPro').val();
                                        if (ClaPro === "" || DesPro === "" || CanPro === "") {
                                            alert("Complete los datos");
                                            return false;
                                        }
                                        return true;
                                    }

                                    function confirmaModal() {
                                        var valida = confirm('Seguro que desea cambiar la fecha de entrega?');
                                        if ($('#ModalFecha').val() === "") {
                                            alert('Falta la fecha');
                                            return false;
                                        } else {
                                            if (valida) {
                                                $('#F_FecEnt').val($('#ModalFecha').val());
                                                alert($('#F_FecEnt').val($('#ModalFecha').val()));
                                                $('#formCambioFechas').submit();
                                            } else {
                                                return false;
                                            }
                                        }
                                    }
                                    
                               function validaNumericos(event) {
                                    if(event.charCode >= 48 && event.charCode <= 57){
                                    return true;
                                }
                                    return false;        
                            }

                                    function aplicarModificacion(valor) {

                                        var detalleModificar = $(valor).attr('value');
                                        var CantidadM = $('#cantidadM_' + detalleModificar).val();
                                        var Recibido = $('#Recibido_' + detalleModificar).val();
                                        var CantSol = $('#CantSol_' + detalleModificar).val();
                                        if (CantidadM != "") {
                                            if (CantidadM > 0 ) {
                                                if (parseInt(CantidadM) > parseInt(Recibido)) {
                                                    if (detalleModificar !== "") {

                                                        var r = confirm("¿Desea realizar la modificación?");
                                                        if (r) {
                                                            var $form = $(this);
                                                            $.ajax({
                                                                type: "POST",
                                                                url: "CapturaPedidos?accion=Modificar&CantidadM=" + CantidadM + "&CantSol=" + CantSol + "&detalleModificar=" + detalleModificar,
                                                                dataType: "json",
                                                                success: function (data) {
                                                                    console.log(data.msg);
                                                                    if (data.msg === "ok") {
                                                                        alert('Cantidad Modificada');
                                                                        location.reload();
                                                                    } else {
                                                                        alert('Ocurrio un irreor intente de nuevo');
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                } else {
                                                    alert("Favor de agregar cantidad Mayor a Recibido");
                                                }
                                            } else {
                                                alert("Favor de agregar cantidad Mayor a Cero o que no sea decimal");
                                            }
                                        } else {
                                            alert("Favor de agregar cantidad");
                                        }
                                    }

                                    function aplicarEliminar(valor) {

                                        var detalleEliminar = $(valor).attr('value');


                                        if (detalleEliminar !== "") {

                                            var r = confirm("¿Desea realizar la Eliminación?");
                                            if (r) {
                                                var $form = $(this);
                                                $.ajax({
                                                    type: "POST",
                                                    url: "CapturaPedidos?accion=Eliminar&detalleModificar=" + detalleEliminar,
                                                    dataType: "json",
                                                    success: function (data) {
                                                        console.log(data.msg);
                                                        if (data.msg === "ok") {
                                                            alert('Registro Eliminado');
                                                            location.reload();
                                                        } else {
                                                            alert('Ocurrio un irreor intente de nuevo');
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    
                                    
                                    
                                    
    </script>
</html>
