<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "",Clave="",DesTipo = "",F_DesPro="",F_StsPro="",F_Catipo="",F_Costo="";
    int tipo = 0,F_Origen=0,F_IncMen=0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    try{
        Clave = request.getParameter("Clave");
    }catch(Exception e){
        
    }
    
    ConectionDB con = new ConectionDB();
    
    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <hr/>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Catalogo de Insumo para la Salud</h3>
                </div>
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formulario1" id="formulario1" method="post" action="Medicamentos">
                        <div class="row">
                            <div class="form-group">                                
                                <label for="Clave" class="col-xs-1 control-label">CLAVE</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Clave" name="Clave" maxlength="60" placeholder="CLAVE" value="" />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Descripción</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control" id="Descripcion" maxlength="40" name="Descripcion" placeholder="Descripcion"   />
                                </div>
                                <label for="Sts" class="col-xs-1 control-label">Status</label>
                                <div class="col-xs-2">                                                                        
                                    <input type="radio" name="radiosts" id="radiosts" checked="" value="A">Activo&nbsp;<input type="radio" name="radiosts" id="radiosts" value="S">Suspendido                                    
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <label for="list_medica" class="col-xs-1 control-label">ORIGEN</label>
                            <div class="col-xs-2">                                                                    
                                <input type="radio" name="radiorigen" id="radiorigen" checked="" value="1">ADMÓN&nbsp;<input type="radio" name="radiorigen" id="radiorigen" value="2">VENTA                                
                             </div>
                             <label for="list_medica" class="col-xs-1 control-label">CATÁLOGO</label>
                            <div class="col-xs-3">                                                                    
                                <input type="radio" name="radiocat" id="radiocat" checked="" value="1">(1)R-C-H&nbsp;<input type="radio" name="radiocat" id="radiocat" value="2">(2)C-H&nbsp;<input type="radio" name="radiocat" id="radiocat" value="3">(3)H&nbsp;<input type="radio" name="radiocat" id="radiocat" value="4">Ninguno                                
                             </div>
                            <label for="list_medica" class="col-xs-1 control-label">PARTIDA</label>
                            <div class="col-xs-3">                                                                    
                                <input type="radio" name="radiotipo" id="radiotipo" checked="" value="2504">Medicamento&nbsp;<input type="radio" name="radiotipo" id="radiotipo" value="2505">Mat. Curación                                
                            </div>                            
                        </div>
                             <br/>
                             <div class="row">
                                <label for="Costo" class="col-xs-1 control-label">Precio</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Costo" name="Costo" placeholder="Costo" value="<%=F_Costo%>"  />
                                </div>
                                <label for="Costo" class="col-xs-2 control-label">%Inc. Mensual</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Incmen" name="Incmen" placeholder="Inc Mensual" value="<%=F_IncMen%>" onKeyPress="return isNumberKey(event, this)" />
                                </div>
                             </div>
                             
                       
                        
                        <br/>
                        <div class="row">
                            <div class="col-sm-6">
                              <button class="btn btn-block btn-success" type="submit" name="accion" value="Agregar">Agregar</button>   
                            </div>
                            <div class="col-sm-6">
                                <a href="medicamento.jsp" class="btn btn-block btn-warning" type="submit" name="accion" value="Modificar">Regresar</a>   
                            </div>
                        </div>

                    </form>
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                
            </div>
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
        <script>
                            $(document).ready(function() {
                                $('#datosProv').dataTable();
                            });
        </script>
        <script>


            function isNumberKey(evt, obj)
            {
                var charCode = (evt.which) ? evt.which : event.keyCode;
                if (charCode === 13 || charCode > 31 && (charCode < 48 || charCode > 57)) {
                    if (charCode === 13) {
                        frm = obj.form;
                        for (i = 0; i < frm.elements.length; i++)
                            if (frm.elements[i] === obj)
                            {
                                if (i === frm.elements.length - 1)
                                    i = -1;
                                break
                            }
                        /*ACA ESTA EL CAMBIO*/
                        if (frm.elements[i + 1].disabled === true)
                            tabular(e, frm.elements[i + 1]);
                        else
                            frm.elements[i + 1].focus();
                        return false;
                    }
                    return false;
                }
                return true;

            }


            function valida_alta() {
                var missinginfo = "";
                var radiotipo = ($("#radiotipo1")).val();
                alert(radiotipo);
                if ($("#Clave").val() == "") {
                    missinginfo += "\n El campo Clave no debe de estar vacío";
                }
                if ($("#Descripcion").val() == "") {
                    missinginfo += "\n El campo Descripcion no debe de estar vacío";
                }
                if ($("#radiosts").val() == "") {
                    missinginfo += "\n El campo Descripcion no debe de estar vacío";
                }
                if ($("#Costo").val() == "") {
                    missinginfo += "\n El campo Costo no debe de estar vacío";
                }
                if (missinginfo != "") {
                    missinginfo = "\n TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ENVIAR PETICIÓN DE SOPORTE:\n" + missinginfo + "\n\n ¡INGRESA LOS DATOS FALTANTES Y TRATA OTRA VEZ!\n";
                    alert(missinginfo);

                    return false;
                } else {

                    return true;
                }
                
            }
        </script>
        <script language="javascript">
            function justNumbers(e)
            {
                var keynum = window.event ? window.event.keyCode : e.which;
                if ((keynum == 8) || (keynum == 46))
                    return true;

                return /\d/.test(String.fromCharCode(keynum));
            }
            otro = 0;
            function LP_data() {
                var key = window.event.keyCode;//codigo de tecla. 
                if (key < 48 || key > 57) {//si no es numero 
                    window.event.keyCode = 0;//anula la entrada de texto. 
                }
            }
            function anade(esto) {
                if (esto.value.length === 0) {
                    if (esto.value.length == 0) {
                        esto.value += "(";
                    }
                }
                if (esto.value.length > otro) {
                    if (esto.value.length == 4) {
                        esto.value += ") ";
                    }
                }
                if (esto.value.length > otro) {
                    if (esto.value.length == 9) {
                        esto.value += "-";
                    }
                }
                if (esto.value.length < otro) {
                    if (esto.value.length == 4 || esto.value.length == 9) {
                        esto.value = esto.value.substring(0, esto.value.length - 1);
                    }
                }
                otro = esto.value.length
            }


            function tabular(e, obj)
            {
                tecla = (document.all) ? e.keyCode : e.which;
                if (tecla != 13)
                    return;
                frm = obj.form;
                for (i = 0; i < frm.elements.length; i++)
                    if (frm.elements[i] == obj)
                    {
                        if (i == frm.elements.length - 1)
                            i = -1;
                        break
                    }
                /*ACA ESTA EL CAMBIO*/
                if (frm.elements[i + 1].disabled == true)
                    tabular(e, frm.elements[i + 1]);
                else
                    frm.elements[i + 1].focus();
                return false;
            }

        </script> 
    </body>
</html>



