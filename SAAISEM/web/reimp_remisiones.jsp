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
    String usua = "";
    String tipo = "";
    String attGlobal = "?proyecto=";
    String proyecto = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    try {
        proyecto = (String) sesion.getAttribute("proyecto");
        attGlobal += proyecto;
        Fecha1 = (String) sesion.getAttribute("fecha_ini");
        Fecha2 = (String) sesion.getAttribute("fecha_fin");
        Folio1 = (String) sesion.getAttribute("folio1");
        Folio2 = (String) sesion.getAttribute("folio2");
        if (Folio1 == null) {
            Folio1 = "";
        }
        if (Folio2 == null) {
            Folio2 = "";
        }
        if (Folio1.compareTo("") != 0 && Folio2.compareTo("") != 0) {
            attGlobal += "&folio1=" + Folio1 + "&folio2=" + Folio2;
        }
        if (Fecha1.compareTo("") != 0 && Fecha2.compareTo("") != 0) {
            attGlobal += "&f1=" + Fecha1 + "&f2=" + Fecha2;
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    if (Folio1 == null) {
        Folio1 = "";
    }
    if (Folio2 == null) {
        Folio2 = "";
    }
    System.out.println("Fecha1= "+Fecha1+ " Fecha2= "+ Fecha2 + " Folio1= "+Folio1+ " Folio2= "+ Folio2);
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <!--link href="css/navbar-fixed-top.css" rel="stylesheet"-->
        <link href="css/datepicker3.css" rel="stylesheet">
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="row">
                <h3 class="col-sm-3">Administrar Remisiones</h3>
                <div class="col-sm-1 col-sm-offset-4">
                    <br/>
                    <!-- class="btn btn-info" href="gnrFacturaConcentrado.jsp" target="_blank">Imprimir Multiples</a-->
                </div>

                <div class="col-sm-4">
                    <br/>
                    <button class="btn btn-info" id="descargarReporteRecetas" name="descargarReporteRecetas">Reporte de Recetas &nbsp;<label class="glyphicon glyphicon-cloud-download"></label></button>
                    <a class="btn btn-success" href="gnrFacturaConcentrado.jsp?Fecha1=<%=Fecha1%>&Fecha2=<%=Fecha2%>&Folio1=<%=Folio1%>&Folio2=<%=Folio2%>" target="_blank">Exportar Global</a>
                </div>
            </div>
            <form name="forma1" id="forma1" action="AdministraRemisiones" method="post">
                <div class="panel-footer">
                    <div class="row">                    
                        <label class="control-label col-sm-1" for="fecha_ini">Folios</label>
                        <div class="col-sm-2">
                            <input class="form-control" id="folio1" name="folio1" type="text" value="" onchange="habilitar(this.value);" />
                        </div>
                        <div class="col-lg-2">
                            <input class="form-control" id="folio2" name="folio2" type="text" value="" onchange="habilitar(this.value);"/>
                        </div>

                        <label class="control-label col-sm-1" for="fecha_ini">Fechas</label>
                        <div class="col-sm-2">
                            <input class="form-control" id="fecha_ini" name="fecha_ini" value="<%=Fecha1%>" type="date" onchange="habilitar(this.value);"/>
                            <br/>
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
                        <%if (!(tipo.equals("8"))) {%>
                        <div class="col-sm-3">
                            <button class="btn btn-block btn-info" name="Accion" value="exportar" onclick="return confirm('Seguro que desea exportar los Abastos?');">Exportar Abasto&nbsp;<label class="glyphicon glyphicon-floppy-saved"></label></button>                        
                        </div>
                        <%}%>
                        <div class="col-sm-3">
                             <c:forEach items="${listaRemision}" var="detalle">
                           <!-- <button class="btn btn-block btn-warning" name="Accion" value="exportarPDF" onclick="return confirm('Seguro que desea exportar las remisiones pdf?');">Exportar Remisiones<label class="glyphicon glyphicon-export"></label></button>                        
                               <a href="ImprimeFolio?fol_gnkl=<c:out value="${detalle.folio}" />&Proyecto=<c:out value="${detalle.idproyecto}" />&BanDato=<c:out value="${detalle.ban}" />&TipBanDato=<c:out value="${detalle.bantip}" />&idProyecto=<c:out value="${detalle.idproyecto}" />" target="_blank" class="btn btn-block btn-warning">Exportar Remisiones <span class="glyphicon glyphicon-export"></span></a>
                               -->                
                             </c:forEach>
                        </div>
                    </div>
                </div>  
                <div>
                    <br />
                    <div class="panel panel-success">
                        <div class="panel-body table-responsive">
                            <table class="table table-bordered table-striped" id="datosCompras">
                                <thead>
                                    <tr>
                                        <th>No. Folio</th>
                                        <th>Punto de Entrega</th>
                                        <th>Proyecto Unidad</th>
                                        <th>Estatus</th>
                                        <th>Factura</th>
                                        <th>Fec Ent</th>
                                        <th>Tipo</th>
                                        <th>Folio</th>
                                        <th>Excel</th>
                                            <%if (tipo.equals("7")) {%>
                                        <th>Cancelar</th>
                                        <th>Modula</th>
                                            <%}%>
                                          
                                            
                                        <th>Cancelados</th>
                                    
                                            <%if (tipo.equals("7") || tipo.equals("21")) {%>
                                        <th>Abasto</th>
                                            <%}%>

                                        <!--%/*}*/%-->
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${listaRemision}" var="detalle">
                                        <tr>
                                            <td><c:out value="${detalle.folio}" /></td>
                                            <td><c:out value="${detalle.unidad}" /></td>
                                            <td><c:out value="${detalle.proyecto}" /></td>
                                            <td><c:out value="${detalle.sts}" /></td>
                                            <td><c:out value="${detalle.proyectfactura}" /></td>
                                            <td><c:out value="${detalle.fechae}" /></td>
                                            <td><c:out value="${detalle.tipofact}" /></td>
                                            
                                            <td>
                                                <c:if test="${detalle.ver == 'SI'}">
                                                    <a href="ImprimeFolio?fol_gnkl=<c:out value="${detalle.folio}" />&Proyecto=<c:out value="${detalle.idproyecto}" />&BanDato=<c:out value="${detalle.ban}" />&TipBanDato=<c:out value="${detalle.bantip}" />&idProyecto=<c:out value="${detalle.idproyecto}" />&idsts=A"  target="_blank" class="btn btn-block btn-primary"><span class="glyphicon glyphicon-print"></span></a>
                                                    </c:if>
                                            </td> 
                                            
                                            <td>
                                                <c:if test="${detalle.ver == 'SI'}">
                                                    <a class="btn btn-block btn-success" href="gnrFacturaExcel.jsp?fol_gnkl=<c:out value="${detalle.folio}" />&idProyecto=<c:out value="${detalle.idproyecto}" />&Ban=<c:out value="${detalle.ban}" />&ubi=<c:out value="${detalle.ubi}"/>"><span class="glyphicon glyphicon-save"></span></a>
                                                    </c:if>
                                            </td> 

                                            <%if (tipo.equals("7")) {%>
                                            <td>
                                        
                                                <c:if test="${detalle.cancela == 'SI'}">
                                                    <a class="btn btn-block btn-warning" data-toggle="modal" data-target="#Cancelacion<c:out value="${detalle.folio}" /><c:out value="${detalle.idproyecto}" />"><span class="glyphicon glyphicon-trash"></span></a>                                   
                                                    </c:if>
                                                    
                                            </td>
                                            <td>
                                                <c:if test="${detalle.ban == '1'}">
                                                    <c:if test="${detalle.ver == 'SI'}">
                                                        <input class="hidden" name="fol_gnkl" value="<c:out value="${detalle.folio}" />">
                                                        <input class="hidden" name="idproyecto" value="<c:out value="${detalle.idproyecto}" />">
                                                        <button class="btn btn-block btn-info" name="Accion" value="ReenviarFactura" onclick="return confirm('Seguro de Reenviar este concentrado?')"><span class="glyphicon glyphicon-upload"></span></button>                                        
                                                        </c:if>
                                                    </c:if>
                                            </td>
                                            <%}%>
                                            
                                            <td>
                                                <c:if test="${detalle.sts == 'C'}">
                                                    <div class="row">
                                                    <a style="width: 60px;margin: 0 auto;" class="btn btn-danger btn-block" target="_blank" onclick="javascript:window.open('canceladosFacturacion.jsp?F_ClaDoc=<c:out value="${detalle.folio}"/>&ConInv=<c:out value="${detalle.claveuni}"/>&idProyecto=<c:out value="${detalle.idproyecto}" />&fentrega=<c:out value="${detalle.fechae}"/>&ubi=<c:out value="${detalle.ubi}"/>', '', 'width=1000,height=600,left=80,top=100,toolbar=yes');" > <span  class="glyphicon glyphicon-remove-sign"></span></a>
                                                    </div>
                                                    <br/>
                                                    <div class="row">
                                                    <a style="width: 60px;margin: 0 auto;" target="_blank"  class="btn btn-block btn-success" href="ImprimeFolio?fol_gnkl=<c:out value="${detalle.folio}" />&Proyecto=<c:out value="${detalle.idproyecto}" />&BanDato=<c:out value="${detalle.ban}" />&TipBanDato=<c:out value="${detalle.bantip}" />&idProyecto=<c:out value="${detalle.idproyecto}" />&idsts=<c:out value="${detalle.sts}" />" ><span class="glyphicon glyphicon-print "></span></a>
                                                    </div>
                                                </c:if>
                                            </td>
                                            
                                            <%if (tipo.equals("7") || tipo.equals("21")) { %>
                                            <td>
                                                <a class="btn btn-warning btn-block" target="_black" href="facturacion/generaAbastoCSV.jsp?F_ClaDoc=<c:out value="${detalle.folio}"/>&ConInv=<c:out value="${detalle.claveuni}"/>&idProyecto=<c:out value="${detalle.idproyecto}" />"><span class="glyphicon glyphicon-download"></span></a>
                                                <br/>
                                                <a class="btn btn-info btn-block" target="_black" href="facturacion/generaAbastoCSVDownloads.jsp?F_ClaDoc=<c:out value="${detalle.folio}"/>&ConInv=<c:out value="${detalle.claveuni}"/>&idProyecto=<c:out value="${detalle.idproyecto}" />"><span class="glyphicon glyphicon-hand-up"></span></a>

                                            </td>
                                            <% }%>

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
        <script src="js/sweetalert.min.js" type="text/javascript"></script>
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
            function validaContra(elemento) {
                //alert(elemento);
                var pass = document.getElementById(elemento).value;
                var id = elemento.split("ContraCancel");
                if (pass === "MedC5n2020") {
                    //alert(pass);
                    document.getElementById(id[1]).disabled = false;
                    //$(id[1]).prop("disabled", false);
                } else {
                    document.getElementById(id[1]).disabled = true;
                    //$(id[1]).prop("disabled", true);
                }
            }
            $('#descargarReporteRecetas').click(function () {
                var fechaIni = $('#fecha_ini').val();
                var fechaFin = $('#fecha_fin').val();
                // Convertir las cadenas de fecha a objetos de fecha
                var fechaIniStr = new Date(fechaIni);
                var fechaFinStr = new Date(fechaFin);
                console.log("fecha de inicio " + fechaIni);
                console.log("fecha de fin" + fechaFin);
                if (fechaIni === "" && fechaFin !== "") {
                    swal("Ingresar fecha de inicio");
                    return false;
                } else if (fechaIni !== "" && fechaFin === "") {
                    swal("Ingresar fecha final");
                    return false;
                } else if (fechaIni === "" || fechaFin === "") {
                    swal("Ingresar rango de fecha");
                    return false;
                } else if (fechaIniStr > fechaFinStr){
                    swal("La fecha final debe ser mayor a la fecha de inicio");
                    return false;
                }
                window.open("ExcelReporteRecetas?fechaIni="+fechaIni+"&fechaFin="+fechaFin);
            });
        </script>
    </body>
</html>
