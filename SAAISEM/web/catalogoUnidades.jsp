<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="conn.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/select2.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/bootstrap-switch.css" rel="stylesheet" type="text/css"/>

        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4 class="col-xs-10">SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
           
        </div>

        <div class="container">
            <div class="panel panel-success">

                <div class="panel-heading">
                    <h3 class="panel-title">Catalogo de Unidades</h3>
                </div>

                <% if (tipo.equals("1") || tipo.equals("7")) { %>
                <div class="panel-body ">
                    <form class="form-horizontal" name="form1" method="get" action="AltaUnidad">
                        <div class="form-group">
                            <label for="Clave" class="col-xs-1 control-label">Clave*</label>
                            <div class="col-xs-4">
                                <input type="text" class="form-control" id="Clave" name="Clave" placeholder="Clave" onKeyPress="return tabular(event, this)" required="" autofocus >
                            </div>
                            <label for="juris" class="col-xs-2 control-label">Juris - Muni*</label>
                            <div class="col-xs-4">
                                <select name="juris" id="juris" class="form-control" required="">
                                    <option value="">--Seleccione--</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rsetJM = con.consulta("SELECT F_ClaMunIS,CONCAT(j.F_DesJurIS,' - ',m.F_DesMunIS) AS F_DesMunIS FROM tb_muniis m INNER JOIN tb_juriis j ON m.F_JurMunIS=j.F_ClaJurIS;");
                                            while (rsetJM.next()) {
                                    %>

                                    <option value="<%=rsetJM.getString(1)%>"><%=rsetJM.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="Nombre" class="col-xs-1 control-label">Nombre*</label>
                            <div class="col-xs-4">
                                <input type="text" class="form-control" id="Nombre" name="Nombre" maxlength="60" placeholder="Nombre" required="" onKeyPress="return tabular(event, this)" />
                            </div>
                            <label for="TipUnidad" class="col-xs-2 control-label">Tipo Unidad*</label>
                            <div class="col-xs-4">
                                <select name="tipo" id="tipo" class="form-control" required="">
                                    <option value="">--Seleccione--</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rsetProy = con.consulta("SELECT F_idTipUni,F_NomUni,F_NomeUnidad FROM tb_tipunidad order by F_NomUni;");
                                            while (rsetProy.next()) {
                                    %>

                                    <option value="<%=rsetProy.getString(3)%>"><%=rsetProy.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                        }
                                    %>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="Direccion" class="col-xs-1 control-label">Dirección*</label>
                            <div class="col-xs-4">
                                <input type="text" class="form-control" id="Direccion" maxlength="100" name="Direccion" placeholder="Dirección" required="" onKeyPress="return tabular(event, this)" />
                            </div>
                            <!--
                            <label for="Regsa" class="col-xs-2 control-label">Registro Sanitario</label>
                            <div class="col-xs-4">
                                <input class="form-control" type="text" name="Regsa" id="Regsa" maxlength="40" max="40" />
                            </div>
                            -->
                        </div>


                        <div class="form-group">
                            <label for="IdReporte" class="col-xs-1 control-label">IdReporte</label>
                            <div class="col-xs-4">
                                <input class="form-control" type="number" name="IdReporte" id="IdReporte" />
                            </div>                            
<!--
                            <label for="Respsa" class="col-xs-2 control-label">Responsable sanitario</label>
                            <div class="col-xs-4">
                                <select class="form-control" name="Respsa" id="Respsa">
                                    <option value="0">No</option>
                                    <option value="1">SI</option>                                                                       
                                </select>                                
                            </div>
-->
                        </div>    
                                <div class="panel-group col-xs-11">
                                    <div class="col-xs-3 navbar-right" >
                                        <button class="btn btn-block btn-success" type="submit" name="accion" value="guardar" >Guardar <span class="glyphicon glyphicon-floppy-save"></span> </button>
                                    </div>
                                    <div class="col-xs-3 navbar-right" >
                                        <a class="btn btn-block btn-info" onclick="window.close();">Salir  <span class="glyphicon glyphicon-log-out"></span></a>
                                    </div> 
                                </div>
                    </form>
                    <div class="panel-footer col-lg-12">
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
        <div class="container">
            <div class="container-fluid">    
                <div class="panel-footer">
                    <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv">
                        <thead>
                            <tr>
                                <th>Clave</th>
                                <th>Nombre</th>
                                <th>Sts</th>
                                <th>Clues   </th>
                                <th>Dirección</th>
                                <th>Tipo Unidad</th>
                                <th>Dispensador</th>

                                <!--th>Registro Sanitario</th>
                                <th>Responsable Sanitario</th-->
                                    <% if (tipo.equals("1")) { %>
                                <th>Modificar</th>
                                    <% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    //ResultSet rset = con.consulta("SELECT F_ClaCli, F_NomCli, F_StsCli, F_ClaJur, F_ClaJurNum, F_Tipo, F_ClaMun, F_Direc, F_Ruta, F_Dispen, F_Razon, F_Clues, F_Proyecto, F_IdReporte, IFNULL(F_RegSan, ''), CASE WHEN F_RespSan = '1' THEN 'SI' ELSE 'NO' END 'F_RespSan' FROM tb_uniatn ORDER BY F_ClaCli ASC");
                                    ResultSet rset = con.consulta("SELECT F_ClaCli, F_NomCli, F_StsCli, F_ClaJur, F_ClaJurNum, F_Tipo, F_ClaMun, F_Direc, F_Ruta, F_Dispen, F_Razon, F_Clues, F_Proyecto, F_IdReporte FROM tb_uniatn ORDER BY F_ClaCli ASC");
                                    System.out.println(rset);
                                    while (rset.next()) {
                            %>
                            <tr class="odd gradeX">
                                <td class="Clave"><small><%=rset.getString(1)%></small></td>
                                <td class="Nombre"><small><%=rset.getString(2)%></small></td>
                                <td class="Sts"><small><%=rset.getString(3)%></small></td>
                                <td class="clues"><small><%=rset.getString(12)%></small></td>
                                <td class="Direc"><small><%=rset.getString(8)%></small></td>
                                <td class="Tipo"><small><%=rset.getString(6)%></small></td>
                                <td class="Dispen"><small><%=rset.getString(10)%></small></td>

                                <!--td class="RegSa"><small>< %=rset.getString(15)%></small></td>
                                <td class="ResSa"><small>< %=rset.getString(16)%></small></td-->
                                        <% if (tipo.equals("1") || tipo.equals("7")) { %>
                                <td>
                                    <a class="btn btn-block btn-warning rowButton" data-toggle="modal" data-target="#Devolucion"><span class="glyphicon glyphicon-pencil"></span></a>
                                </td>
                                <% } %>
                            </tr>
                            <%
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
        <!-- Modal de modificar el tipo de unidad  -->
        <div id="Devolucion" class="modal fade" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header" style="background: #7aba7b">
                        <h4 style="align-content: center">
                            Modificar Unidad
                        </h4>
                    </div>
                    <form name="AltaUni" action="AltaUnidad" method="Post">
                        <div class="modal-body">
                            <div class="form-group col-lg-12">
                                <label class="col-sm-3">Clave unidad:</label>
                                <div class="col-sm-4">
                                    <input class="form-control" name="claveMod" id="claveMod" type="text" value="" readonly="" required="">
                                </div>                                
                            </div>

                            <div class="form-group col-lg-12">
                                <label class="col-sm-3">Nombre unidad:</label>
                                <div class="col-sm-9">
                                    <input class="form-control" name="nombreMod" id="nombreMod" type="text" value="" required="">
                                </div>                                
                            </div>
                            <div class="form-group col-lg-12 ">
                                <label class="col-sm-3">Dirección:</label>
                                <div class="col-sm-9">
                                    <input class="form-control" name="direccionMod" id="direccionMod" type="text" required="">
                                </div>                                
                            </div>
                            <div class="form-group col-lg-12">                                
                                <label for="estatus" class="control-label col-lg-3">Estatus:</label>                                
                                <div class="col-sm-6">
                                    <select name="estatusMod" id="estatusMod" class="form-control col-sm-4">
                                        <option value="A">Activo</option>
                                        <option value="C">Suspendido</option>
                                    </select>
                                </div>                                
                            </div>  
                            <div class="form-group col-lg-12">
                                <label for="clues" class="control-label col-lg-3">Clues</label>
                                <div class="col-lg-6">
                                    <input class="form-control" name="cluesMod" id="cluesMod" type="text">
                                </div>
                            </div>
                            <div class="form-group col-lg-12">
                                <label class="col-lg-3">Tipo Unidad:</label>                                
                                <div class="col-lg-6">
                                    <select name="tipoMod" id="tipoMod" class="form-control">
                                        <option value="">--Seleccione--</option>
                                        <%
                                            try {
                                                con.conectar();
                                                ResultSet rsetProy = con.consulta("SELECT F_idTipUni,F_NomUni,F_NomeUnidad FROM tb_tipunidad order by F_NomUni;");
                                                while (rsetProy.next()) {
                                        %>

                                        <option value="<%=rsetProy.getString(3)%>"><%=rsetProy.getString(2)%></option>
                                        <%
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {
                                            }
                                        %>
                                    </select>
                                </div>
                            </div>
                            <!--<div class="form-group col-lg-12">
                                <label class="col-lg-3">Registro Sanitario:</label>
                                <div class="col-lg-6">
                                    <input class="form-control" id="regSaMod" name="regSaMod" type="text">                                            
                                </div>
                            </div>
                            <div class="form-group col-lg-12">
                                <label class="col-lg-3">Responsable Sanitario:</label>
                                <div class="col-lg-6">
                                    <select class="form-control" id="resSaMod" name="resSaMod" type="text">                                            
                                        <option value="1">SI</option>
                                        <option value="0">NO</option>
                                    </select>
                                </div>
                            </div>
-->
                        </div>
                        <div class="modal-footer">
                            <div class="navbar-right col-lg-4">          
                                <button type="submit" class="btn btn-block btn-success" name="accion" value="Modificar">Guardar <span class="glyphicon glyphicon-floppy-saved"></span></button>

                            </div>
                            <div class="col-lg-4">
                                <button type="submit" class="btn btn-block btn-primary" data-dismiss="modal">Cancelar <span class="glyphicon glyphicon-floppy-remove"></span></button>
                            </div>
                        </div>
                    </form>
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
        <script src="js/select2.js"></script>
        <script src="js/bootstrap-switch.js" type="text/javascript"></script>
        <script>
                                    $(document).ready(function () {
                                        $('#datosProv').dataTable();

                                    });
                                    $(document).ready(function () {
                                        $('#estadoSwitch').bootstrapSwitch();
                                    });
        </script>

        <script type="text/javascript">
            $(".rowButton").click(function () {
                var $row = $(this).closest("tr");
                var clave = $row.find("td.Clave").text();
                var nombre = $row.find("td.Nombre").text();
                var sts = $row.find("td.Sts").text();
                var clues = $row.find("td.clues").text();
                var direc = $row.find("td.Direc").text();
                var tipo = $row.find("td.Tipo").text();
                var regSa = $row.find("td.RegSa").text();
                var resSa = $row.find("td.ResSa").text();



                $("#claveMod").val(clave);
                $("#nombreMod").val(nombre);
                $("#estatusMod").val(sts);
                $("#direccionMod").val(direc);
                $("#cluesMod").val(clues);
                $("#tipoMod").val(tipo);
                $("#regSaMod").val(regSa);
                $("#resSaMod").val(resSa);


            });
            $('#tipou').change(function () {
                var tipou = $('#tipou').val();
                if (tipou !== '') {
                    $('#TipoU').val(tipou);
                }
            });

            $('#juris').change(function () {
                var juris = $('#juris').val();

                if (juris !== '') {
                    $('#jurisU').val(juris);
                }
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
                var Clave = document.getElementById('Clave').value;
                var Nombre = document.getElementById("Nombre").value;                
                if (Nombre === "" || Clave === "") {
                    alert("Tiene campos vacíos, verifique.");
                    return false;
                } }
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
                    var key = window.event.keyCode; //codigo de tecla. 
                    if (key < 48 || key > 57) {//si no es numero 
                        window.event.keyCode = 0; //anula la entrada de texto. 
                    }
                }
                function anade(esto) {
                    if (esto.value === "(55") {
                        if (esto.value.length === 0) {
                            if (esto.value.length === 0) {
                                esto.value += "(";
                            }
                        }
                        if (esto.value.length > otro) {
                            if (esto.value.length === 3) {
                                esto.value += ") ";
                            }
                        }
                        if (esto.value.length > otro) {
                            if (esto.value.length === 9) {
                                esto.value += "-";
                            }
                        }
                        if (esto.value.length < otro) {
                            if (esto.value.length === 4 || esto.value.length === 9) {
                                esto.value = esto.value.substring(0, esto.value.length - 1);
                            }
                        }
                    } else {
                        if (esto.value.length === 0) {
                            if (esto.value.length === 0) {
                                esto.value += "(";
                            }
                        }
                        if (esto.value.length > otro) {
                            if (esto.value.length === 4) {
                                esto.value += ") ";
                            }
                        }
                        if (esto.value.length > otro) {
                            if (esto.value.length === 9) {
                                esto.value += "-";
                            }
                        }
                        if (esto.value.length < otro) {
                            if (esto.value.length === 4 || esto.value.length === 9) {
                                esto.value = esto.value.substring(0, esto.value.length - 1);
                            }
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

    
</html>

