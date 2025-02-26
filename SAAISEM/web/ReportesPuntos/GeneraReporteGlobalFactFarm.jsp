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
                    <h3 class="panel-title">Generar Reportes Globales de Facturación FARMACIA</h3>
                </div>
                
                <form action="../ReporteGlobalFarm" method="post">
                        <div class="panel-footer">
                            <div class="row">                                    
                                <label class="control-label col-sm-1" for="fecha_ini">Fecha Inicio</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" />
                                </div>
                                <label class="control-label col-sm-1" for="fecha_fin">Fecha Fin</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_fin" name="fecha_fin" type="date" />
                                </div>                                
                                <div class="col-sm-3">
                                    <select name="Unidad" id="Unidad">
                                        <option value="">--- Seleccione Unidad ---</option>
                                        <%
                                    try {
                                        ResultSet rset = null;
                                        con.conectar();
                                        rset = con.consulta("SELECT F_ClaUni,F_DesUni FROM tb_unidadceaps ORDER BY F_DesUni ASC");
                                        while (rset.next()) {
                                        %>
                                        <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                        <%
                                        }    
                                        } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                        }
                                    %>
                                    </select>
                                </div>
                            </div>                            
                        </div>     
                        <div class="panel-body">
                            <div class="row">
                                <button class="btn btn-block btn-warning" id="generar">Generar&nbsp;<label class="glyphicon glyphicon-open"></label></button>
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
<script>
    //$("#fecha_ini").datepicker();
    //$("#fecha_fin").datepicker();
    
    </script>
    <script>
    $(document).ready(function() {
        $('#datosProv').dataTable();
    });
</script>

