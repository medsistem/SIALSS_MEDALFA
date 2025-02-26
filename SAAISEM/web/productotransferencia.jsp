<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<%@page import="Facturacion.FacturacionManual" %>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%
    FacturacionManual fact = new FacturacionManual();
    HttpSession sesion = request.getSession();
    String F_IndGlobal = (String) sesion.getAttribute("F_IndGlobal");
    String usua = "", tipo = "", Desproyecto = "";
    int Proyecto = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        if(!tipo.equals("1")){
            response.sendRedirect("../index.jsp");
        }
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String ClaCli = "", FechaEnt = "", ClaPro = "", DesPro = "";

    try {
        ClaCli = (String) sesion.getAttribute("ClaCliFM");
        FechaEnt = (String) sesion.getAttribute("FechaEntFM");
        ClaPro = (String) sesion.getAttribute("ClaProFM");
        DesPro = (String) sesion.getAttribute("DesProFM");
    } catch (Exception e) {

    }

    System.out.println(FechaEnt);
    if (ClaCli == null) {
        ClaCli = "";
    }
    if (FechaEnt == null) {
        FechaEnt = "";
    }
    if (ClaPro == null) {
        ClaPro = "";
    }
    if (DesPro == null) {
        DesPro = "";
    }
    try {

        con.conectar();
        ResultSet rsetProy = con.consulta("SELECT P.F_Id, IFNULL(P.F_DesProy, '') AS Proyecto FROM tb_parametrousuario PU LEFT JOIN ( SELECT F_Id, F_DesProy FROM tb_proyectos ) P ON PU.F_Proyecto = P.F_Id WHERE PU.F_Usuario = '" + usua + "';");
        if (rsetProy.next()) {
            Proyecto = rsetProy.getInt(1);
            Desproyecto = rsetProy.getString(2);
        }
        
        ResultSet rset = con.consulta("select F_IdFact, F_StsFact, F_ClaCli, F_FecEnt, F_User from tb_facttemp where F_User = '" + usua + "'");
        rset.last();
        //while (rset.next()) {
        if (rset.getString("F_StsFact").equals("3") && rset.getString("F_User").equals(usua)) {
            sesion.setAttribute("F_IndGlobal", rset.getString(1));
            F_IndGlobal = (String) sesion.getAttribute("F_IndGlobal");
            ClaCli = rset.getString("F_ClaCli");
            sesion.setAttribute("ClaCliFM", ClaCli);
            FechaEnt = rset.getString("F_FecEnt");
        }
        //}
        
        con.cierraConexion();

    } catch (Exception e) {

    }

    try {
        if (request.getParameter("accion").equals("nuevoFolio")) {
            sesion.setAttribute("F_IndGlobal", fact.dameIndGlobalTranferencia() + "");
            F_IndGlobal = (String) sesion.getAttribute("F_IndGlobal");
        }
    } catch (Exception e) {

    }
    ClaCli = Integer.toString(Proyecto);
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="row">
                <div class="col-sm-12">
                    <h2>Transferencias Productos</h2>
                </div>
            </div>
            <hr/>
            <%                if (F_IndGlobal == null) {
            %>
            <form action="productotransferencia.jsp" method="post">
                <button class="btn btn-block btn-success" name="accion" value="nuevoFolio">Nueva Transferencia</button>
            </form>
            <%
            } else {
            %>
            <form action="FacturacionManual" method="post">
                <h4 class="text-muted">Folio: <%=F_IndGlobal%></h4>
                <div class="row">
                    <div class="col-sm-1">
                        <h4>Proyecto:</h4>
                    </div>
                    <div class="col-sm-2">
                        <input value="<%=ClaCli%>"  class="form-control" name="ClaCli" id="ClaCli" readonly=""  />
                    </div>
                    <div class="col-sm-2">
                        <input type="text" readonly="" class="form-control" name="DesProyecto" id="DesProyecto" value="<%=Desproyecto%>"/>
                        <input type="hidden" class="form-control" name="Proyecto" id="Proyecto" value="<%=Proyecto%>"/>
                        <input type="hidden" class="form-control" name="FolioS" id="FolioS" value="<%=F_IndGlobal%>"/>
                    </div>
                    <div class="col-sm-1">
                        <h4>Fecha</h4>
                    </div>
                    <div class="col-sm-2">
                        <input type="date" class="form-control" name="FechaEnt" id="FechaEnt" value="<%=FechaEnt%>"/>
                    </div>
                </div> 
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-sm-2">
                                <h4>Ingrese CLAVE:</h4>
                            </div>
                            <div class="col-sm-2">
                                <input class="form-control" name="ClaPro" id="ClaPro"/>
                            </div>
                            <div class="col-sm-2">
                                <button class="btn btn-success btn-block" name="accion" value="btnClaveTProyecto" id="btnClaveTProyecto" onclick="return validaUni();">Buscar</button>
                            </div>
                        </div>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-sm-1">
                                <h4>CLAVE:</h4>
                            </div>
                            <div class="col-sm-2">
                                <input class="form-control" readonly="" value="<%=ClaPro%>" id="ClaveSel"/>
                            </div>
                            <div class="col-sm-2">
                                <h4>Descripción:</h4>
                            </div>
                            <div class="col-sm-7">
                                <textarea class="form-control" readonly="" id="DesSel"><%=DesPro%></textarea>
                            </div>
                        </div>
                        <br/>

                    </div>
                    <div class="panel-footer">
                        <div class="row">
                            <div class="col-sm-2">
                                <h4>Cantidad a Facturar:</h4>
                            </div>
                            <div class="col-sm-2">
                                <input class="form-control" name="Cantidad" id="Cantidad" onKeyPress="return justNumbers(event);"/>
                            </div>
                            <div class="col-sm-2">
                                <button class="btn btn-block btn-success" name="accion" value="SeleccionaLoteTProyecto" onclick="return validaSeleccionar();">Seleccionar</button>
                            </div>
                        </div>

                    </div>
                </div>
                <table class="table table-condensed table-striped table-bordered table-responsive">
                    <tr>
                        <td>CLAVE:</td>
                        <td>Lote</td>
                        <td>Caducidad</td>
                        <td>Ubicación</td>
                        <td>Marca</td>
                        <td>Cantidad</td>
                        <td>Proyecto</td>
                        <td>Remover</td>
                    </tr>
                    <%
                        int banBtn = 0;
                        try {
                            con.conectar();
                            ResultSet rset = con.consulta("SELECT l.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y'), f.F_Cant, l.F_Ubica, f.F_IdFact, mar.F_DesMar, f.F_Id, p.F_DesProy FROM tb_facttemp f INNER JOIN tb_lote l ON f.F_IdLot = l.F_IdLote INNER JOIN tb_medica m ON m.F_ClaPro = l.F_ClaPro INNER JOIN tb_marca mar ON l.F_ClaMar = mar.F_ClaMar INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id WHERE F_ClaCli = '" + ClaCli + "' and F_StsFact=3;");
                            while (rset.next()) {
                                banBtn = 1;
                    %>
                    <tr>
                        <td><%=rset.getString(1)%></td>
                        <td><%=rset.getString(2)%></td>
                        <td><%=rset.getString(3)%></td>
                        <td><%=rset.getString(5)%></td>
                        <td><%=rset.getString("F_DesMar")%></td>
                        <td><%=rset.getString(4)%></td>
                        <td><%=rset.getString(9)%></td>
                        <td>
                            <button class="btn btn-block btn-success" name="accionEliminarProducproyecto" value="<%=rset.getString("F_Id")%>" onclick="return confirm('Seguro que desea eliminar esta clave?')"><span class="glyphicon glyphicon-remove"></span></button>
                        </td>
                    </tr>
                    <%
                            }
                            con.cierraConexion();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    %>
                </table>
                <%
                    if (banBtn == 1) {
                %>

                <div class="row">
                    <h4 class="col-sm-1">Proyecto</h4>
                    <div class="col-sm-3">
                        <select class="form-control" name="ProyectoFinal" id="ProyectoFinal">
                            <option value="0">--Seleccione--</option>
                            <%
                                try {
                            con.conectar();
                            ResultSet rsetProyecto = con.consulta("SELECT F_Id, F_DesProy FROM tb_proyectos WHERE F_Id NOT IN('"+Proyecto+"');");
                            while (rsetProyecto.next()) {
                            %>
                            <option value="<%=rsetProyecto.getString(1)%>"><%=rsetProyecto.getString(2)%></option>
                            <%
                            }
                            con.cierraConexion();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    %>
                        </select>
                    </div>
                    <h4 class="col-sm-2">Observaciones</h4>
                    <div class="col-sm-4">
                        <textarea class="form-control" name="obs" id="obs"></textarea>
                    </div>
                </div>
                <br/>
                <div class="row">
                    <div class="col-sm-6">
                        <button class="btn btn-block btn-success" name="accion" type="button" id="BtnConfirmar" value="ConfirmarFactura">Confirmar Tranferencia</button>
                        <!--button class="btn btn-block btn-success" name="accion" id="BtnConfirmar" value="ConfirmarFactura" onclick="return confirm('Seguro de confirmar la Factura?')">Confirmar Factura</button-->
                    </div>
                    <div class="col-sm-6">
                        <button class="btn btn-block btn-success" name="accion" value="CancelarTranferProductoProyecto" onclick="return confirm('Seguro de CANCELAR la Tranferencia?')">Cancelar Tranferencia</button>
                    </div>
                </div>

                <%
                    }
                %>

            </form>
            <%
                }
            %>
        </div>
        <%@include file="jspf/piePagina.jspf" %>
        <!-- 
    ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/jquery.dataTables.js"></script>
        <script src="js/dataTables.bootstrap.js"></script>
        <script src="js/bootstrap-datepicker.js"></script>
        <script src="js/jquery.alphanum.js" type="text/javascript"></script>
        <script src="js/funcIngresos.js"></script>
        <script src="js/facturajs/TranferProductoProyecto.js"></script>
        <script src="js/sweetalert.min.js" type="text/javascript"></script>    
        <script>

                            $(document).ready(function () {
                                var Clave = $("#ClaveSel").val();
                                if (Clave != "") {
                                    $("#Cantidad").focus();
                                } else {
                                    $("#ClaPro").focus();
                                }
                            });

                            function justNumbers(e)
                            {
                                var keynum = window.event ? window.event.keyCode : e.which;
                                if ((keynum === 8) || (keynum === 46))
                                    return true;
                                return /\d/.test(String.fromCharCode(keynum));
                            }

                            function cambiaLoteCadu(elemento) {
                                var indice = elemento.selectedIndex;
                                document.getElementById('SelectCadu').selectedIndex = indice;
                            }

                            function validaBuscar() {
                            }


                            function validaSeleccionar() {
                                var DesSel = document.getElementById('DesSel').value;
                                if (DesSel === "") {
                                    alert('Favor de Capturar Toda la información');
                                    return false;
                                }
                                var cantidad = document.getElementById('Cantidad').value;
                                if (cantidad === "") {
                                    alert('Escriba una cantidad');
                                    return false;
                                }

                            }



                            function validaUni() {

                                var Unidad = document.getElementById('ClaCli').value;
                                if (Unidad === "") {
                                    alert('Seleccione Unidad');
                                    return false;
                                }

                                var FechaEnt = document.getElementById('FechaEnt').value;
                                if (FechaEnt === "") {
                                    alert('Seleccione Fecha');
                                    return false;
                                }
                                var clave = document.getElementById('ClaPro').value;
                                if (clave === "") {
                                    alert('Escriba una Clave');
                                    return false;
                                }

                                var causes = document.getElementById('ClaCli').value;

                                if (causes === "") {
                                    alert('Capture Diagnóstico válido');
                                    e.focus();
                                    return false;
                                }
                                var causesArray = causes.split(" - ");
                                causes = causesArray[0];
                                var causesTodos = "";
            <%
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("SELECT F_Id, F_DesProy FROM tb_proyectos;");
                    while (rset.next()) {
            %>
                                causesTodos = causesTodos + "<%=rset.getString(1)%>-";
            <%
                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            %>
                                var causesTodosArray = causesTodos.split('-');
                                var ban1 = 0;
                                for (i = 0; i <= causesTodosArray.length; i++) {
                                    if (causes === causesTodosArray[i]) {
                                        return true;
                                        ban1 = 1;
                                    }
                                }
                                if (ban1 === 0) {
                                    alert('Capture Unidad válida');
                                    return false;
                                }
                            }
        </script>
    </body>

</html>

