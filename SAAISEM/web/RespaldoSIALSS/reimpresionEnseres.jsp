<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    java.util.Date fecha = new Date();
    HttpSession sesion = request.getSession();
    String usua = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String FechaIni = "", FechaFin = "";
    try {
        FechaIni = request.getParameter("FechaIni");
        FechaFin = request.getParameter("FechaFin");
    } catch (Exception e) {

    }
    if (FechaIni == null) {
        FechaIni = df2.format(fecha);
    }
    if (FechaFin == null) {
        FechaFin = df2.format(fecha);
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
                <form name="FormOC" action="reimpresionEnseres.jsp" method="Post">
                    <div class="row">
                        <h4 class="col-sm-2">Rango de Fecha</h4>
                        <div class="col-sm-2">
                            <input class="form-control" name="FechaIni" id="FechaIni" type="date" value="<%=FechaIni%>"/>
                        </div>
                        <div class="col-sm-2">
                            <input class="form-control" name="FechaFin" id="FechaFin" type="date" value="<%=FechaFin%>"/>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-success" name="accion" value="Clave">Por Fecha</button>
                        </div>
                    </div>
                    <br />
                    <div class="panel panel-success">
                        <div class="panel-body table-responsive">
                            <table class="table table-bordered table-striped" id="datosCompras">
                                <thead>
                                    <tr>
                                        <td>Fecha</td>
                                        <td>Folio</td>
                                        <td>Orden de Compra</td>
                                        <td>Cantidad</td>
                                        <td>Compra</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        try {
                                            con.conectar();
                                            try {
                                                if ((FechaIni != "") && (FechaFin != "")) {
                                                    ResultSet rset = con.consulta("SELECT DATE_FORMAT( DATE(F_FechaCaptura), '%d/%m/%Y' ) AS F_FechaCaptura, F_ClaDoc, F_Oc, SUM(F_Cantidad) AS F_Cantidad, DATE(F_FechaCaptura) FROM tb_enserescompra WHERE DATE(F_FechaCaptura) BETWEEN '" + FechaIni + "' AND '" + FechaFin + "' GROUP BY F_ClaDoc, F_Oc; ");
                                                    while (rset.next()) {
                                    %>
                                    <tr>

                                        <td><%=rset.getString(1)%></td>
                                        <td><%=rset.getString(2)%></td>
                                        <td><%=rset.getString(3)%></td>
                                        <td><%=rset.getString(4)%></td>
                                        <td>
                                            <a href="reimpReporteEnseres.jsp?F_FolRemi=<%=rset.getString(2)%>&F_OrdCom=<%=rset.getString(3)%>&fol_gnkl=<%=rset.getString(2)%>&fecha=<%=rset.getString(5)%>" target="_blank" class="btn btn-block btn-success">Imprimir</a>
                                        </td>
                                    </tr>
                                    <%
                                                    }
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
                </form>
            </div>
        </div>
        <%@include file="jspf/piePagina.jspf" %>



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

        <div class="modal fade" id="EditaRemision" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <form action="Modificaciones">
                        <div class="modal-header">
                            <h3>Edición de remisiones</h3>
                        </div>
                        <div class="modal-body">
                            <input name="idRem" id="idRem" class="hidden" />
                            <h4 class="modal-title" id="myModalLabel">Remisión Incorrecta</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="text" name="remiIncorrecta" id="remiIncorrecta" class="form-control" readonly="" />
                                </div>
                            </div>
                            <h4 class="modal-title" id="myModalLabel">Remisión</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="text" name="remiCorrecta" id="remiCorrecta" class="form-control" />
                                </div>
                            </div>
                            <h4 class="modal-title" id="myModalLabel">Fecha de Recepción</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="date" name="fecRemision" id="fecRemision" class="form-control" />
                                </div>
                            </div>
                            <h4 class="modal-title" id="myModalLabel">Contraseña</h4>
                            <div class="row">
                                <div class="col-sm-12">
                                    <input type="password" name="contrasena" id="remiContraseña" class="form-control"  onkeyup="validaContra(this.id);" />
                                </div>
                            </div>
                            <div style="display: none;" class="text-center" id="LoaderRemi">
                                <img src="imagenes/ajax-loader-1.gif" height="150" />
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-success" onclick="return validaRemision();" name="accion" value="actualizarRemi" id="actualizaRemi" disabled>Actualizar</button>
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

    function ponerRemision(id) {
        var elem = id.split(',');
        document.getElementById('idRem').value = elem[0];
        document.getElementById('remiIncorrecta').value = elem[1];
    }

    function validaRemision() {
        var remiCorrecta = document.getElementById('remiCorrecta').value;
        var fecRemision = document.getElementById('fecRemision').value;

        if (remiCorrecta === "" && fecRemision === "") {
            alert('Ingrese al menos una corrección')
            return false;
        }
    }

    function validaContra(elemento) {
        //alert(elemento);
        var pass = document.getElementById(elemento).value;
        //alert(pass);
        if (pass === "GnKlTolu2014") {
            document.getElementById('actualizaRemi').disabled = false;
        } else {
            document.getElementById('actualizaRemi').disabled = true;
        }
    }
</script>