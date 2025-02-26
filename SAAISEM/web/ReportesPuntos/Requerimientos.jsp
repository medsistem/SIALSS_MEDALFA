<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    String fecha_ini="", fecha_fin="",radio="",F_FolCon="";
    int F_Idsur=0,F_IdePro=0,F_Cvesum=0,F_Punto=0,F_Con=0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    
    try {
        fecha_ini = request.getParameter("fecha_ini");        
        fecha_fin = request.getParameter("fecha_fin");    
        radio = request.getParameter("radio");        
    } catch (Exception e) {

    }
    if(fecha_ini==null){
        fecha_ini="";
    }
    if(fecha_fin==null){
        fecha_fin="";
    }
    
    ConectionDB con = new ConectionDB();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipal.jspf" %>
        </div>
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Generar Requerimientos Automáticos</h3>
                </div>
                
                <form action="../Requerimiento" method="post">
                        <div class="panel-footer">
                            <div class="row">
                                <label class="control-label col-sm-1" for="fecha_ini">Unidad</label>
                                <div class="col-lg-6">
                                    <input type="text" class="form-control"  name="NombreUnidad" id="NombreUnidad" value="" onkeyup="descripUni()" required="true" />                                    
                                </div>
                                <label class="control-label col-sm-1" for="fecha_ini">Fecha Entrega</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="Fecha" name="Fecha" type="date" required="true"/>
                                </div>                                                          
                            </div>
                            <div class="row">
                                <label class="control-label col-sm-2" for="fecha_ini">Clasificación: </label>
                                <div class="col-sm-2">
                                    <input type="radio" id="radio1" name="radio1" checked="true" value="1" > Rurales
                                    <input type="radio" id="radio1" name="radio1" value="2"> Todos
                                </div>
                                </div>
                            <br />                            
                            <div class="row">                                
                                <div class="col-sm-2">
                                    <input type="radio" id="radio2" name="radio2" checked="true" value="1" > Cantidad Requerida                                    
                                </div>
                                </div>
                            <br />
                                <div class="row">
                                <label class="control-label col-sm-2" for="imprera">Impresora</label>
                                <div class="col-sm-2 col-sm-2">                       
                                    <select id="impresora" name="impresora">
                                        <option value="">--Seleccione Impresora--</option>
                                        <%
                                        String Nom = "";
                                        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
                                        for (PrintService printService : impresoras) {
                                            Nom = printService.getName();
                                            //System.out.println("impresora" + Nom);                            
                                        %>
                                        <option value="<%=Nom%>"><%=Nom%></option>                            
                                        <%}%>
                                    </select>                        
                                </div>
                            </div>
                        </div>     
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-sm-6">
                                    <button type="submit" class="btn btn-info btn-block" id="btnImp" name="accion" value="imprimir" >Imprimir&nbsp;<span class="glyphicon glyphicon-print"></span></button>
                                </div>
                                <div class="col-sm-6">
                                    <button type="submit" class="btn btn-success btn-block" id="btnExp" name="accion" value="exportar" >Exportar&nbsp;<span class="glyphicon glyphicon-export"></span></button>
                                </div>
                            </div>
                        </div>                        
                    </form>
            </div>
        </div>
        <br><br><br>
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2019 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>
    </body>
</html>


<!-- 
================================================== -->
<!-- Se coloca al final del documento para que cargue mas rapido -->
<!-- Se debe de seguir ese orden al momento de llamar los JS -->
<script src="../js/jquery-1.9.1.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script type="text/javascript">
          $(function() {
               var availableTags = [
          <%
            try {
                con.conectar();
                try {
                    ResultSet rset = con.consulta("SELECT F_DesUniIS FROM tb_unidis ORDER BY F_DesUniIS ASC");
                    while (rset.next()) {
                        out.println("'" + rset.getString(1) + "',");
                    }
                } catch (Exception e) {

                }
                con.cierraConexion();
            } catch (Exception e) {

            }
        %>
               ];
               $("#NombreUnidad").autocomplete({
                   source: availableTags
               });
          });
        </script>
    <script>
    $(document).ready(function() {
        $('#datosProv').dataTable();
    });
</script>

