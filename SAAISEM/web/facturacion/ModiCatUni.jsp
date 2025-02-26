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
    String usua = "",Id="",DesTipo = "",F_Juris="",F_StsPro="",F_Catipo="",F_Costo="";
    String tipo = "";
    int F_Origen=0,F_IncMen=0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    try{
        Id = request.getParameter("Id");
    }catch(Exception e){
        
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
            <hr/>
            <%@include file="../jspf/menuPrincipal.jspf" %>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Catálogo de Unidades (Modificar)</h3>
                </div>
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formulario1" id="formulario1" method="post" action="../Medicamentos">
                        <%
                        try {
        con.conectar();
        
        
        ResultSet rset = con.consulta("SELECT F_ClaUniIS,F_JurUniIS,F_DesUniIS,F_ClaSap,F_CooUniIS,F_ClaInt1,F_ClaInt2,F_ClaInt3,F_ClaInt4,F_ClaInt5,F_ClaInt6,F_ClaInt7,F_ClaInt8,F_ClaInt9,F_ClaInt10,"
                + "F_MedUniIS,me.F_DesMedIS,F_LocUniIS,l.F_DesLocIS,F_MunUniIS,m.F_DesMunIS,j.F_DesJurIS,F_RegUniIS,F_NivUniIS,C.F_DesCooIS FROM tb_unidis u INNER JOIN tb_juriis j ON u.F_JurUniIS=j.F_ClaJurIS "
                + "INNER JOIN tb_muniis m on u.F_MunUniIS=m.F_ClaMunIS AND u.F_JurUniIS=j.F_ClaJurIS INNER JOIN tb_locais l on u.F_LocUniIS=l.F_ClaLocIS AND u.F_MunUniIS=l.F_MunLocIS INNER JOIN tb_mediis me on u.F_MedUniIS=me.F_ClaMedIs "
                + "INNER JOIN tb_cooris c on u.F_CooUniIS=C.F_ClaCooIS WHERE F_Id='"+Id+"'");    
        while (rset.next()) {
                        %>
                        <input type="hidden" class="form-control" id="Id" name="Id" maxlength="60" placeholder="CLAVE" value="<%=Id%>" />
                        <div class="row">                                                          
                                <label for="Clave" class="col-xs-1 control-label">Clave</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Clave" name="Clave" maxlength="60" placeholder="CLAVE" readonly="" value="<%=rset.getString(1)%>" />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Jurisdicción</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="juris" maxlength="40" name="juris" placeholder="Jurisdicción" readonly="" value="<%=rset.getString(2)%>" />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Descripción</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control" id="descip" maxlength="40" name="descrip" placeholder="Descripción" value="<%=rset.getString(3)%>" />
                                </div>
                        </div>
                                <br />
                        <div class="row">
                                                     
                                <label for="Clave" class="col-xs-1 control-label">Destinatario</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Destina" name="Destina" onkeypress="return isNumberKey(event, this);" maxlength="60" placeholder="Clave Destinatario" value="<%=rset.getString(4)%>" />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Coordinación</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="coordi" maxlength="40" name="coordi" onkeypress="return isNumberKey(event, this);" placeholder="Coor. Municipal" value="<%=rset.getString(5)%>" /><%=rset.getString(25)%>
                                </div>                                
                            
                        </div>
                                <br />
                        <div class="row">
                            <div class="form-group">                                
                                <label for="Clave" class="col-xs-1 control-label">Módulos</label>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m1" name="m1" maxlength="5" placeholder="M1" value="<%=rset.getString(6)%>"/><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>                                
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m2" name="m2" maxlength="5" placeholder="M2" value="<%=rset.getString(7)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m3" name="m3" maxlength="5" placeholder="M3" value="<%=rset.getString(8)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m4" name="m4" maxlength="5" placeholder="M4" value="<%=rset.getString(9)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m5" name="m5" maxlength="5" placeholder="M5" value="<%=rset.getString(10)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m6" name="m6" maxlength="5" placeholder="M6" value="<%=rset.getString(11)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m7" name="m7" maxlength="5" placeholder="M7" value="<%=rset.getString(12)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m8" name="m8" maxlength="5" placeholder="M8" value="<%=rset.getString(13)%>" /><!--a href="" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m9" name="m9" maxlength="5" placeholder="M9" value="<%=rset.getString(14)%>" /><!--a href="Modulos.jsp?M" class="btn btn-block btn-warning">?</a-->
                                </div>
                                <div class="col-xs-1">
                                    <input type="text" class="form-control" id="m10" name="m10" maxlength="5" placeholder="M10" value="<%=rset.getString(15)%>" /><!--a href="Modulos.jsp?M" class="btn btn-block btn-warning">?</a-->
                                </div>
                            </div>
                        </div>        
                                
                             <br/>
                             <div class="row">
                                <label for="Costo" class="col-xs-1 control-label">Doctor</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="doctor" name="doctor" placeholder="Doctor" value="<%=rset.getString(16)%>"  /><%=rset.getString(17)%>
                                </div>
                                <label for="Costo" class="col-xs-2 control-label">Localidad</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="localidad" name="localidad" placeholder="localidad" onkeypress="return isNumberKey(event, this);" value="<%=rset.getString(18)%>" /><%=rset.getString(19)%>
                                </div>
                             </div>
                             <br />
                             <div class="row">
                                <label for="Costo" class="col-xs-1 control-label">Municipio</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="municipio" name="municipio" placeholder="Municipio" onkeypress="return isNumberKey(event, this);" value="<%=rset.getString(20)%>" /><%=rset.getString(21)%>
                                </div>
                                <label for="Costo" class="col-xs-1 control-label">Jurisdicción</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control" id="juris" name="juris" placeholder="Jurisdiccion" readonly="" value="<%=rset.getString(22)%>"/>
                                </div>
                             </div>
                             <br />
                             <div class="row">
                                <label for="Costo" class="col-xs-1 control-label">Región</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="region" name="region" placeholder="Region" onkeypress="return isNumberKey(event, this);" value="<%=rset.getString(23)%>"  />
                                </div>
                                <label for="Costo" class="col-xs-1 control-label">Nivel</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="nivel" name="nivel" onkeypress="return isNumberKey(event, this);" placeholder="Nivel" value="<%=rset.getString(24)%>" />
                                </div>
                             </div>
                                <%
                                }
                                    con.cierraConexion();
                                }catch(Exception e){

                                }
                                %>
                        <br/>
                        <div class="row">
                            <div class="col-sm-6">
                              <button class="btn btn-block btn-success" type="submit" name="accion" value="ActualizarUnidis">Modificar</button>   
                            </div>
                            <div class="col-sm-6">
                                <a href="catalogoUniDis.jsp" class="btn btn-block btn-warning" type="submit" name="accion" value="Regresar">Regresar</a>   
                            </div>
                        </div>
                    </form>
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                
            </div>
        </div>
        <%@include file="../jspf/piePagina.jspf" %>
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



