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
                    <h3 class="panel-title">Reporteador TXT</h3>
                </div>
                
                <form action="../ReporteadorTxt2" method="post">
                        <div class="panel-footer">
                            <div class="row">                                    
                                <label class="control-label col-sm-1" for="Grupo1">Grupo1</label>                                                            
                                <div class="col-sm-5">
                                    <input type="radio" id="radio1" name="radio1" checked="true" value="1"> Jurisdicción
                                    <input type="radio" id="radio1" name="radio1" value="2"> Insumo
                                    <input type="radio" id="radio1" name="radio1" value="3"> Contrato
                                    <input type="radio" id="radio1" name="radio1" value="4"> Unidades Atención
                                </div>
                                <label class="control-label col-sm-1" for="Grupo2">Grupo2</label>                                                            
                                <div class="col-sm-5">
                                    <input type="radio" id="radio2" name="radio2" checked="true" value="1" > Jurisdicción
                                    <input type="radio" id="radio2" name="radio2" value="2"> Insumo
                                    <input type="radio" id="radio2" name="radio2" value="3"> Contrato
                                    <input type="radio" id="radio2" name="radio2" value="4"> Unidades Atención
                                </div>                                
                            </div>                            
                        </div>
                        <!--div class="panel-footer">
                            <div class="row">                                    
                                <label class="control-label col-sm-1" for="Grupo1">Cifras</label>                                                            
                                <div class="col-sm-10">
                                    <input type="checkbox" id="checkbox1" name="checkbox1" checked="true" value="1"> Cant. Req.
                                    <input type="checkbox" id="checkbox2" name="checkbox2" checked="true" value="2"> Cant. Sur.
                                    <input type="checkbox" id="checkbox3" name="checkbox3" checked="true" value="3"> Costo Unitario
                                    <input type="checkbox" id="checkbox4" name="checkbox4" checked="true" value="4"> Costo Total
                                    <input type="checkbox" id="checkbox5" name="checkbox5" checked="true" value="5"> Costo Servicio
                                    <input type="checkbox" id="checkbox6" name="checkbox6" checked="true" value="6"> Iva
                                    <input type="checkbox" id="checkbox7" name="checkbox7" checked="true" value="7"> Total
                                    <input type="checkbox" id="checkbox8" name="checkbox8" checked="true" value="8"> Piezas No Surtidas
                                    <input type="checkbox" id="checkbox9" name="checkbox9" checked="true" value="9"> Costo No Surtido                                    
                                </div>                                                          
                            </div>                            
                        </div-->
                        <div class="panel-footer">
                            <div class="row">
                                <label class="control-label col-sm-1" for="Surtido">Surtido</label>
                                <select name="surtido" id="surtido" class="col-sm-2">
                                    <option value="0"></option>
                                    <option value="1">Administración</option>
                                    <option value="2">Venta</option>
                                </select>
                                <label class="control-label col-sm-2" for="fecha_ini"><input type="radio" id="radio" name="radio" checked="true" value="1" onchange="habilitar(this.value);">
                                Fecha</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" />
                                </div>
                                <label class="control-label col-sm-1" for="fecha_fin">Al</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_fin" name="fecha_fin" type="date" />
                                </div>  
                            </div>                            
                        </div>
                        <div class="panel-footer">
                            <div class="row">
                                <label class="control-label col-sm-1" for="Coberturas">Cobertura</label>
                                <select name="cobertura" id="cobertura" class="col-sm-2">
                                    <option value="2"></option>
                                    <option value="0">Población Abierta</option>
                                    <option value="1">Seguro Popular</option>
                                </select>
                                <label class="control-label col-sm-2" for="Secuencial"><input type="radio" id="radio" name="radio" value="2" onchange="habilitar(this.value);">
                                Secuencial</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="secuencial1" name="secuencial1" type="text" />
                                </div>
                                <label class="control-label col-sm-1" for="secuelcial">Al</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="secuencial2" name="secuencial2" type="text" />
                                </div>  
                            </div>                            
                        </div>
                        <div class="panel-footer">
                            <div class="row">
                                <label class="control-label col-sm-1" for="Suministro">Suministro</label>
                                <select name="suministro" id="suministro" class="col-sm-2">
                                    <option value="0"></option>
                                    <option value="2504">Medicamento</option>
                                    <option value="2505">Mat. Curación</option>
                                </select>
                                <!--label class="control-label col-sm-2" for="Suministro"><input type="radio" id="radio3" name="radio3" value="3">
                                Folio Agr.</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="secuencial1" name="secuencial1" type="text" />
                                </div>
                                <label class="control-label col-sm-1" for="secuelcial">Al</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="secuencial2" name="secuencial2" type="text" />
                                </div-->  
                            </div>                            
                        </div>
                        <div class="panel-footer">                            
                            <div class="row">
                                <label class="control-label col-sm-1" for="Partida">Partida</label>
                                <div class="col-sm-2">
                                    <input type="radio" id="radio5" name="radio5" checked="true" value="1" > Rurales
                                    <input type="radio" id="radio5" name="radio5" value="2"> Farmacia
                                </div>
                                <label class="control-label col-sm-1" for="Status">Status</label>
                                <div class="col-sm-3">
                                    <input type="radio" id="radio5" name="radio6" checked="true" value="1" > Activas
                                    <input type="radio" id="radio5" name="radio6" value="2"> Canceladas
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
    $(document).ready(function() {
        $('#datosProv').dataTable();
    });
    
    function habilitar(value){
        
        if(value=="1"){
            document.getElementById("fecha_ini").disabled=false;
            document.getElementById("fecha_fin").disabled=false;
            document.getElementById("fecha_ini").value="";
            document.getElementById("fecha_fin").value="";
            document.getElementById("secuencial1").disabled=true;
            document.getElementById("secuencial2").disabled=true;
            

        }else if(value=="2"){
            document.getElementById("secuencial1").disabled=false;
            document.getElementById("secuencial2").disabled=false;
            document.getElementById("secuencial1").value="";
            document.getElementById("secuencial2").value="";
            document.getElementById("fecha_ini").disabled=true;
            document.getElementById("fecha_fin").disabled=true;
             
        }
    }
    
</script>

