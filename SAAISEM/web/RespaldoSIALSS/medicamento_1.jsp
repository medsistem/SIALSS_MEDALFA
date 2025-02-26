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
    String usua = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
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
                                <div class="col-xs-3">
                                    <input type="text" class="form-control" id="Clave" name="Clave" maxlength="60" placeholder="CLAVE" onKeyPress="return tabular(event, this)"  />
                                </div>                      
                                <label for="Clave" class="col-xs-1 control-label">SAP</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="SAP" name="SAP" maxlength="60" placeholder="SAP" onKeyPress="return tabular(event, this)"  />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Descripción</label>
                                <div class="col-xs-3">
                                    <input type="text" class="form-control" id="Descripcion" maxlength="40" name="Descripcion" placeholder="Descripcion" onKeyPress="return tabular(event, this)"  />
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <label for="list_medica" class="col-xs-1 control-label">Insumo</label>
                            <div class="col-xs-2">
                                <select class="form-control" name="list_medica" onKeyPress="return tabular(event, this)" id="list_medica" onchange="">
                                    <option value="">Tip. Insumo</option>
                                    <option value="2504">Medicamento</option>
                                    <option value="2505">Mat. Curación</option>
                                </select>
                            </div>
                            <label for="Costo" class="col-xs-2 control-label">Costo</label>
                            <div class="col-xs-2">
                                <input type="text" class="form-control" id="Costo" name="Costo" placeholder="Costo"  />
                            </div>
                            <label for="Costo" class="col-xs-2 control-label">Presentación</label>
                            <div class="col-xs-2">
                                <input type="text" class="form-control" id="PresPro" name="PresPro" placeholder="Presentación" maxlength="10"  />
                            </div>
                        </div>
                        <br/>
                        <div>
                            <label for="F_Origen" class="col-xs-1 control-label">Origen</label>
                            <div class="col-xs-2">
                                <select name="F_Origen" id="F_Origen" class="form-control">
                                    <option value="1">Admon</option>
                                    <option value="2">Venta</option>
                                </select>
                            </div>
                        </div>
                        <br/>
                        <!--div class="row">
                            <label for="Costo" class="col-xs-1 control-label">Máximo</label>
                            <div class="col-xs-2">
                                <input type="number" class="form-control" id="Max" name="Max" placeholder="Máximo"  />
                            </div>
                            <label for="Costo" class="col-xs-1 control-label">Mínimo</label>
                            <div class="col-xs-2">
                                <input type="number" class="form-control" id="Min" name="Min" placeholder="Mínimo"   />
                            </div>
                        </div-->
                        <br/>
                        <button class="btn btn-block btn-success" type="submit" name="accion" value="guardar" onclick="return valida_alta();"> Guardar</button> 

                    </form>
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                <div class="panel-footer">
                    <form method="post" action="Medicamentos">
                        <table class="table table-striped table-bordered" id="datosProv111">
                            <thead>
                                <tr>
                                    <td>CLAVE</td>
                                    <td>SAP</td>
                                    <td>Descripción</td>
                                    <td>Tipo Medicamento</td>
                                    <td>Costo</td>
                                    <td>Max</td>
                                    <td>Min</td>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        int tipo = 0;
                                        String DesTipo = "";
                                        ResultSet rset = con.consulta("SELECT m.* FROM tb_medica m, tb_artiis a where m.F_ClaPro = a.F_ClaInt ORDER BY F_ClaPro+0 ASC");
                                        while (rset.next()) {
                                            int max = 0, min = 0;
                                            ResultSet rset2 = con.consulta("select F_Max, F_Min from tb_maxmodula where F_ClaPro = '" + rset.getString("F_ClaPro") + "'");
                                            while (rset2.next()) {
                                                max = rset2.getInt("F_Max");
                                                min = rset2.getInt("F_Min");
                                            }
                                            tipo = Integer.parseInt(rset.getString(4));
                                            if (tipo == 2504) {
                                                DesTipo = "MEDICAMENTO";
                                            } else {
                                                DesTipo = "MAT. CURACIÓN";
                                            }
                                %>
                                <tr>
                                    <td><small><%=rset.getString(1)%></small></td>
                                    <td><small><%=rset.getString("F_ClaSap")%></small></td>
                                    <td><small><%=rset.getString(2)%></small></td>
                                    <td><small><%=DesTipo%></small></td>
                                    <td class="text-right"><small>$ <%=formatterDecimal.format(rset.getDouble(5))%></small></td>  
                                    <td><input type="number" name="Max<%=rset.getString(1)%>" class="form-control input-sm text-right" value="<%=(max)%>" /></td>  
                                    <td><input type="number" name="Min<%=rset.getString(1)%>" class="form-control input-sm text-right" value="<%=(min)%>" /></td>
                                </tr>
                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                %>
                            </tbody>
                        </table>
                        <button class="btn btn-success btn-block" name="accion" value="Actualizar">Actualizar</button>
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
                if ($("#Clave").val() == "") {
                    missinginfo += "\n El campo Clave no debe de estar vacío";
                }
                if ($("#Descripcion").val() == "") {
                    missinginfo += "\n El campo Descripcion no debe de estar vacío";
                }
                if ($("#list_medica").val() == "") {
                    missinginfo += "\n Favor de seleccionar un tipo de Medicamento";
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



