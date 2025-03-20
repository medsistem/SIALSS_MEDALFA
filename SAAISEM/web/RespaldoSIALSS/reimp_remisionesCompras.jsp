<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();

    Date fechaActual = new Date();
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    String fechaSistema = formateador.format(fechaActual);
    int dia = 0, mes = 0, ano = 0;
    String Fecha1 = "", Fecha2 = "", Fecha11 = "", Fecha22 = "", dia1 = "", mes1 = "";
    String Folio1 = "", Folio2 = "";
    String usua = "", tipo = "", IdUsu = "", Proyecto = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        tipo = (String) sesion.getAttribute("Tipo");
        Proyecto = (String) sesion.getAttribute("Proyecto");
    } else {
        response.sendRedirect("indexMedalfa.jsp");
    }
    try {
        Fecha1 = (String) sesion.getAttribute("fecha_ini");
        Fecha2 = (String) sesion.getAttribute("fecha_fin");
        Folio1 = (String) sesion.getAttribute("folio1");
        Folio2 = (String) sesion.getAttribute("folio2");
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    if (Folio1 == null) {
        Folio1 = "";
    }
    if (Folio2 == null) {
        Folio2 = "";
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

            <%@include file="jspf/menuPrincipalCompra.jspf" %>

            <div class="row">
                <h3 class="col-sm-3">Administrar Remisiones</h3>
                <div class="col-sm-2 col-sm-offset-5">
                    <br/>
                    <!-- class="btn btn-info" href="gnrFacturaConcentrado.jsp" target="_blank">Imprimir Multiples</a-->
                </div>
                <!--div class="col-sm-2">
                    <br/>
                    <a class="btn btn-success" href="gnrFacturaConcentrado.jsp?f1=<%=Fecha1%>&f2=<%=Fecha2%>" target="_blank">Exportar Global</a>
                </div-->
            </div>
            <form name="forma1" id="forma1" action="AdministraRemisionesCompras" method="post">
                <div class="panel-footer">
                    <div class="row">                    
                        <label class="control-label col-sm-1" for="fecha_ini">Folios</label>
                        <div class="col-lg-1">
                            <input class="form-control" id="folio1" name="folio1" type="text" value="" onchange="habilitar(this.value);" />
                        </div>
                        <div class="col-lg-1">
                            <input class="form-control" id="folio2" name="folio2" type="text" value="" onchange="habilitar(this.value);"/>
                        </div>

                        <label class="control-label col-sm-1" for="fecha_ini">Fechas</label>
                        <div class="col-sm-2">
                            <input class="form-control" id="fecha_ini" name="fecha_ini" value="<%=Fecha1%>" type="date" onchange="habilitar(this.value);"/>
                        </div>
                        <div class="col-sm-2">
                            <input class="form-control" id="fecha_fin" name="fecha_fin" value="<%=Fecha2%>" type="date" onchange="habilitar(this.value);"/>
                        </div>
                        <label class="control-label col-sm-1" for="fecha_ini">Proyecto</label>
                        <div class="col-sm-2">
                            <select name="Proyecto" id="Proyecto" class="form-control">
                                <option value="0">--Seleccione --</option>
                                <c:forEach items="${listaProyecto}" var="detalleP">
                                    <option value="<c:out value="${detalleP.idproyecto}"/>"><c:out value="${detalleP.desproyecto}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="panel-body">
                    <div class="row">
                        <div class="col-sm-6">
                            <button class="btn btn-block btn-success" name="Accion" value="mostrar" >MOSTRAR&nbsp;<label class="glyphicon glyphicon-search"></label></button>
                        </div>
                        <!--div class="col-sm-3">
                            <button class="btn btn-block btn-warning" name="Accion" value="exportarDispen" onclick="return confirm('Seguro que desea exportar los Folios por Dispensador?');">Exportar por Dispensador&nbsp;<label class="glyphicon glyphicon-floppy-saved"></label></button>                        
                        </div-->
                    </div>
                </div>  
                <div>
                    <br />
                    <div class="panel panel-success">
                        <div class="panel-body table-responsive">
                            <table class="table table-bordered table-striped" id="datosCompras">
                                <thead>
                                    <tr>
                                        <td>No. Folio</td>
                                        <td>Punto de Entrega</td>
                                        <td>Proyecto Unidad</td>
                                        <td>Estatus</td>
                                        <td>Factura</td>
                                        <td>Fec Ent</td>
                                        <td>Tipo</td>
                                        <td>Folio</td>
                                        <td>Excel</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${listaRemision}" var="detalle">
                                        <tr>
                                            <td><c:out value="${detalle.folio}" /></td>
                                            <td><c:out value="${detalle.claveuni}" />-<c:out value="${detalle.unidad}" /></td>
                                            <td><c:out value="${detalle.proyecto}" /></td>
                                            <td><c:out value="${detalle.sts}" /></td>
                                            <td><c:out value="${detalle.proyectfactura}" /></td>
                                            <td><c:out value="${detalle.fechae}" /></td>
                                            <td><c:out value="${detalle.tipofact}" /></td>
                                            <td>
                                                <c:if test="${detalle.ver == 'SI'}">
                                                   <!-- <a href="ImprimeFolio?fol_gnkl=< c:out value="$ {detalle.folio}" />&Proyecto=< c:out value="$ {detalle.idproyecto}" />&BanDato=< c:out value="$ {detalle.ban}" />&TipBanDato=< c:out value="$ {detalle.bantip}" />&idProyecto=< c:out value="$ {detalle.idproyecto}" />" target="_blank" class="btn btn-block btn-primary"><span class="glyphicon glyphicon-print"></span></a>-->
                                                 <a href="ImprimeFolio?fol_gnkl=<c:out value="${detalle.folio}" />&Proyecto=<c:out value="${detalle.idproyecto}" />&BanDato=<c:out value="${detalle.ban}" />&TipBanDato=<c:out value="${detalle.bantip}" />&idProyecto=<c:out value="${detalle.idproyecto}" />" target="_blank" class="btn btn-block btn-primary"><span class="glyphicon glyphicon-print"></span></a>   
                                                </c:if>
                                            </td>
                                            <td>
                                               <c:if test="${detalle.ver == 'SI'}">
                                               <!-- <a class="btn btn-block btn-success" href="gnrFacturaExcel.jsp?fol_gnkl=< c:out value="$ {detalle.folio}" />&idProyecto=< c:out value=" $ {detalle.idproyecto}" />&Ban=< c:out value="$ {detalle.ban}" />"><span class="glyphicon glyphicon-save"></span></a>-->
                                               <a class="btn btn-block btn-success" href="gnrFacturaExcel.jsp?fol_gnkl=<c:out value="${detalle.folio}" />&idProyecto=<c:out value="${detalle.idproyecto}" />&Ban=<c:out value="${detalle.ban}" />&ubi=<c:out value="${detalle.ubi}"/>"><span class="glyphicon glyphicon-save"></span></a>     
                                               </c:if>
                                               
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </form>
            <!--
        Modal
            -->
            <c:forEach items="${listaRemision}" var="detalle">
                <div class="modal fade" id="Cancelacion<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                    <div class="modal-dialog">
                        <form action="FacturacionManual">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <div class="row">
                                        <div class="col-sm-5">
                                            Cancelar Folio:
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-body">
                                    <input id="IdFact" name="IdFact" value="<c:out value="${detalle.folio}" />" class="hidden">
                                    <input id="IdProyecto" name="IdProyecto" value="<c:out value="${detalle.idproyecto}" />" class="hidden">
                                    <div class="row">
                                        <div class="col-sm-12">
                                            <div class="col-sm-3">
                                                Folio Factura: <c:out value="${detalle.folio}" />
                                            </div>
                                            <div class="col-sm-6">
                                                Clave Cliente: <c:out value="${detalle.claveuni}" />
                                            </div>
                                            <div class="col-sm-3">
                                                Proyecto: <c:out value="${detalle.idproyecto}" />
                                            </div>
                                        </div>
                                    </div>
                                    <h4 class="modal-title" id="myModalLabel">Observaciones</h4>
                                    <div class="row">
                                        <div class="col-sm-12">
                                            <textarea name="Obser" id="Obser<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />" class="form-control"></textarea>
                                        </div>
                                    </div>
                                    <h4 class="modal-title" id="myModalLabel">Contraseña</h4>
                                    <div class="row">
                                        <div class="col-sm-12">
                                            <input name="ContraCancel<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />" id="ContraCancel<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />" class="form-control" type="password" onkeyup="validaContra(this.id);" />
                                        </div>
                                    </div>
                                    <div style="display: none;" class="text-center" id="Loader">
                                        <img src="imagenes/ajax-loader-1.gif" height="150" />
                                    </div>
                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-success" id="<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />" disabled onclick="return validaCancel(this.id);" name="accion" value="CancelarFolio">Cancelar</button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </c:forEach>
            <!--
            /Modal
            -->  
        </div>                                        
        <%@include file="jspf/piePagina.jspf" %>

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
            function validaCancel(e) {
                var id = e;
                if (document.getElementById('Obser' + id).value === "") {
                    alert("Ingrese las observaciones de la devolución")
                    return false;
                }
            }
           
        </script>
    </body>
</html>
