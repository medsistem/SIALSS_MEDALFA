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
    String fechaSistema=formateador.format(fechaActual);
    int dia=0,mes=0,ano=0;
    String Fecha1="",Fecha2="",Fecha11="",Fecha22="",dia1="",mes1="";
    String Folio1="",Folio2="";
    String usua = "";
    String tipo = "";
    String username = "";
    if (sesion.getAttribute("nombre") != null ) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        username = (String) sesion.getAttribute("Usuario");
        }else {
        response.sendRedirect("index.jsp");
    }
    try{
        Fecha1 = (String) sesion.getAttribute("fecha_ini");
        Fecha2 = (String) sesion.getAttribute("fecha_fin");
        Folio1 = (String) sesion.getAttribute("folio1");
        Folio2 = (String) sesion.getAttribute("folio2");            
    }catch(Exception e){
        System.out.println(e.getMessage());
    }
    if(Folio1 == null){Folio1="";}
    if(Folio2 == null){Folio2="";}
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

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="row">
                <h3 class="col-sm-4">Administrar Devoluciones</h3>                
            </div>
            <form name="forma1" id="forma1" action="DevolucionesFacturas" method="post">
            <div class="panel-footer">
                <div class="row">                    
                    <!--label class="control-label col-sm-1" for="fecha_ini">Folios</label>
                    <div class="col-lg-1">
                        <input class="form-control" id="folio1" name="folio1" type="text" value="" onchange="habilitar(this.value);" />
                    </div>
                    <div class="col-lg-1">
                        <input class="form-control" id="folio2" name="folio2" type="text" value="" onchange="habilitar(this.value);"/>
                    </div-->
                                                   
                    <label class="control-label col-sm-1" for="fecha_ini">Fechas</label>
                    <div class="col-sm-2">
                        <input class="form-control" id="fecha_ini" name="fecha_ini"  type="date" onchange="habilitar(this.value);"/>
                    </div>
                    <div class="col-sm-2">
                        <input class="form-control" id="fecha_fin" name="fecha_fin"  type="date" onchange="habilitar(this.value);"/>
                    </div>    
                    <div class="col-sm-3">
                            <button class="btn btn-block btn-success" name="Accion" value="btnMostrarDev" >MOSTRAR&nbsp;<label class="glyphicon glyphicon-search"></label></button>                        
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
                                     <th scope="col">Fecha Dev.</th>
                                     <th scope="col">Doc. Dev.</th>
                                     <th scope="col">Clave Cli.</th>
                                     <th scope="col">Nombre</th>
                                     <th scope="col">Fec. Ent.</th>
                                     <th scope="col">Doc. Ref.</th>
                                     <th scope="col">Devolución</th>
                                     <th scope="col">Marbete</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${ListaDevolucion}" var="detalle">
                                <tr>
                                    <td><c:out value="${detalle.fechamov}" /></td>
                                    <td><c:out value="${detalle.docmovimiento}" /></td>
                                    <td><c:out value="${detalle.claveuni}" /></td>
                                    <td><c:out value="${detalle.unidad}" /></td>
                                    <td><c:out value="${detalle.fechaentrega}" /></td>
                                    <td class="text-center"><c:out value="${detalle.docreferencia}" /></td>
                                    <td class="text-center"><a href="reimpDevolucion.jsp?fol_gnkl=<c:out value="${detalle.docmovimiento}" />&fol_ref=<c:out value="${detalle.docreferencia}" />" target="_blank"  class="btn btn-block btn-info form-control glyphicon glyphicon-print"></a></td>
                                    <td><a href="reportes/MarbeteDevo.jsp?fol_gnkl=<c:out value="${detalle.docmovimiento}" />&Proyecto=1" target="_blank"  class="btn btn-block btn-success form-control glyphicon glyphicon-print"></a></td>
                                </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            </form>
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
                    alert("Ingrese las observaciones de la devolución");
                    return false;
                }
            }
            function validaContra(elemento) {
                //alert(elemento);
                var pass = document.getElementById(elemento).value;
                var id = elemento.split("ContraCancel");
                if (pass === "rosalino") {
                    //alert(pass);
                    document.getElementById(id[1]).disabled = false;
                    //$(id[1]).prop("disabled", false);
                } else {
                    document.getElementById(id[1]).disabled = true;
                    //$(id[1]).prop("disabled", true);
                }
            }
        </script>
    </body>
</html>
