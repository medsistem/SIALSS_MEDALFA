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
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        //response.sendRedirect("index.jsp");
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

    String proveedor = "";
    try {
        byte[] a = request.getParameter("Proveedor").getBytes("ISO-8859-1");
        proveedor = (new String(a, "UTF-8")).toUpperCase();
    } catch (Exception e) {

    }

    if (proveedor == null) {
        proveedor = "";
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
        <link href="css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <hr/>

            <div>
                <h3>Reimpresion de folios de Compras</h3>
                <h4>Seleccione el folio a imprimir</h4>
                <div class="row">
                    <form action="reimpresionISEM.jsp" method="post">
                        <h4 class="col-sm-3">Seleccione el Proveedor</h4>

                        <div class="col-sm-5">
                            <select class="form-control" name="Proveedor" id="Proveedor" onchange="SelectProve(this.form);
                                document.getElementById('Fecha').focus()">
                                <option value="">--Proveedor--</option>
                                <%                                try {
                                        con.conectar();
                                        ResultSet rset = con.consulta("select F_ClaProve, F_NomPro from tb_proveedor order by F_NomPro");
                                        while (rset.next()) {
                                %>
                                <option value="<%=rset.getString(2)%>" ><%=rset.getString(2)%></option>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                    }
                                %>

                            </select>
                        </div>

                        <div class="col-sm-1">
                            <button class="btn btn-success btn-block" name="accion" value="Buscar">Buscar</button>
                        </div>
                    </form>
                </div>
                <br />
                <div class="panel panel-success">
                    <div class="panel-body table-responsive">
                        <table class="table table-bordered table-striped" id="datosCompras">
                            <thead>
                                <tr>
                                    <td>No. Folio</td>
                                    <td>Folio Remisión</td>
                                    <td>Orden de Compra</td>
                                    <td>Fecha</td>
                                    <td>Usuario</td>
                                    <td>Proveedor</td>
                                    <td>REP</td>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet rset = con.consulta("SELECT c.F_ClaDoc, c.F_FolRemi, c.F_OrdCom, c.F_FecApl, c.F_User, p.F_NomPro FROM tb_compra c, tb_proveedor p where p.F_NomPro like '%" + proveedor + "' and  c.F_ProVee = p.F_ClaProve GROUP BY F_OrdCom, F_FolRemi; ");
                                            int i = 1;
                                            while (rset.next()) {
                                %>
                                <tr>

                                    <td><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(2)%></td>
                                    <td><%=rset.getString(3)%></td>
                                    <td><%=df3.format(df2.parse(rset.getString(4)))%></td>
                                    <td><%=rset.getString(5)%></td>
                                    <td><%=rset.getString(6)%></td>
                                    <td>
                                        <form action="reimpISEM.jsp" target="_blank">
                                            <button type="submit" class="btn btn-success btn-block" data-toggle="modal" data-target="#Observaciones" name="accion" value="remisionCamion" id="<%=i%>" onclick="ponerFolio(this.id)">Imprimir</button>
                                            <input class="hidden" name="fol_gnkl" value="<%=rset.getString(1)%>">
                                            <input class="hidden" id="F_FR<%=i%>" value="<%=rset.getString("F_FolRemi")%>">
                                            <input class="hidden" id="F_OC<%=i%>" value="<%=rset.getString("F_OrdCom")%>">
                                            <!--button class="btn btn-block btn-success">Imprimir</button-->
                                        </form>
                                    </td>

                                </tr>
                                <%
                                                i++;
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
        <br><br><br>
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2019 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>



        <!--
               Modal
        -->
        <div class="modal fade" id="Observaciones" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <form action="reimpISEM.jsp" method="post" target="_blank">
                        <div class="modal-header">
                        </div>
                        <div class="modal-body">
                            <input name="idCom" id="idCom" class="hidden" />
                            <input name="F_FolRemi" id="F_FolRemi" class="hidden" />
                            <input name="F_OrdCom" id="F_OrdCom" class="hidden" />
                            <h4 class="modal-title" id="myModalLabel">No de Contrato</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="text" name="NoContrato" id="NoContrato" class="form-control" />
                                </div>
                            </div>

                            <h4 class="modal-title" id="myModalLabel">No de Folio</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="text" name="NoFolio" id="NoFolio" class="form-control" />
                                </div>
                            </div>
                            <h4 class="modal-title" id="myModalLabel">Fecha</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="date" name="fecRecepcion" id="fecRecepcionISEM" class="form-control" />
                                </div>
                            </div>
                            <h4 class="modal-title" id="myModalLabel">Observaciones</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <textarea name="Observaciones" id="Observaciones" class="form-control" rows="5" ></textarea>
                                </div>
                            </div>

                            <div style="display: none;" class="text-center" id="Loader">
                                <img src="imagenes/ajax-loader-1.gif" height="150" />
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success" onclick="return validaISEM();" name="accion" value="actualizarCB">Imprimir</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>


        <!--
        /Modal
        -->
    </body>
</html>


<!-- 
================================================== -->
<!-- Se coloca al final del documento para que cargue mas rapido -->
<!-- Se debe de seguir ese orden al momento de llamar los JS -->
<script src="js/jquery-1.9.1.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/jquery-ui-1.10.3.custom.js"></script>
<script src="js/bootstrap-datepicker.js"></script>
<script src="js/jquery.dataTables.js"></script>
<script src="js/dataTables.bootstrap.js"></script>
<script>
                                    $(document).ready(function () {
                                        $('#datosCompras').dataTable();
                                    });
</script>
<script>
    $(function () {
        $("#fecha").datepicker();
        $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
    });

    function ponerFolio(id) {
        document.getElementById('idCom').value = id;
        document.getElementById('F_FolRemi').value = document.getElementById("F_FR" + id).value;
        document.getElementById('F_OrdCom').value = document.getElementById("F_OC" + id).value;
    }


    function ponerRemision(id) {
        var elem = id.split(',');
        document.getElementById('idRem').value = elem[0];
        document.getElementById('remiIncorrecta').value = elem[1];
    }

    function validaISEM() {
        if (document.getElementById('NoContrato').value === "") {
            alert('Capture el número de contrato');
            return false;
        }
        if (document.getElementById('NoFolio').value === "") {
            alert('Capture el número de folio');
            return false;
        }
        if (document.getElementById('fecRecepcionISEM').value === "") {
            alert('Capture la fecha');
            return false;
        }
    }
</script>