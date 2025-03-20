<%-- 
    Document   : cambioFechas
    Created on : 14/04/2015, 12:58:35 PM
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%DecimalFormat format = new DecimalFormat("####,###");%>
<%

    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fecha_ini = "", fecha_fin = "";
    try {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
    } catch (Exception e) {

    }
    if (fecha_ini == null) {
        fecha_ini = "";
    }
    if (fecha_fin == null) {
        fecha_fin = "";
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

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="panel-heading">
                <h3 class="panel-title">Parametro facturacón CSRD</h3>
            </div>
            <form action="ParametroCsrd" method="post">


                <div class="row">
                    <label class="col-sm-offset-2">UBICACIONES FACTURACIÓN CSRD</label>
                </div>
                <div class="container">
                    <div class="row col-sm-8">
                        <table class="table col-sm-3">
                            <tr>
                                <%
                                    try {
                                        con.conectar();
                                        int Parametro = 0;
                                        ResultSet Consulta = con.consulta("SELECT F_Id FROM tb_parametro;");
                                        if (Consulta.next()) {
                                            Parametro = Consulta.getInt(1);
                                        }
                                        System.out.println(Parametro);
                                        if (Parametro == 1) {
                                %>                                
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="1" checked="true" />MODULA 1, AS, APE y DENTAL</td>                                
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="2" />MODULA 2, AS, APE y DENTAL</td>
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="3" /> AS, APE y DENTAL</td>
                                    <%} else if (Parametro == 2) {%>
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="1" />MODULA 1, AS, APE y DENTAL</td>                                
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="2" checked="true" />MODULA 2, AS, APE y DENTAL</td>
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="3" /> AS, APE y DENTAL</td>
                                    <%} else {%>
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="1" />MODULA 1, AS, APE y DENTAL</td>                                
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="2" />MODULA 2, AS, APE y DENTAL</td>
                                <td><input type="radio" class="btn btn-sm col-lg-offset-2" name="radio" id="radio" value="3" checked="true" /> AS, APE y DENTAL</td>

                                <%
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                        }%>
                            </tr>
                        </table>
                    </div>
                </div>
                <br />
                <div class="panel-body">
                    <div class="row">
                        <div class="col-sm-6">
                            <button class="btn btn-block btn-success" id="accion" name="accion" value="ActualizarP" onclick="return confirma();">ACTUALIZAR&nbsp;<label class="glyphicon glyphicon-search"></label></button>                        
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
                                $(document).ready(function() {
                                    $('#datosCompras').dataTable();
                                    $("#fecha").datepicker();
                                    $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});

                                    //$('#btnRecalendarizar').attr('disabled', true);
                                    //$('#btnImpMult').attr('disabled', true);
                                });
        </script>
        <script>
            $(document).ready(function() {
                $('#SelectJuris').change(function() {
                    var Lista = $('#ListJuris').val();
                    var valor = $('#SelectJuris').val();
                    if (Lista != "") {
                        $('#ListJuris').val(Lista + ",'" + valor + "'");
                    } else {
                        $('#ListJuris').val("'" + valor + "'");
                    }
                });
                $('#SelectMuni').change(function() {
                    var Lista = $('#ListMuni').val();
                    var valor = $('#SelectMuni').val();
                    if (Lista != "") {
                        $('#ListMuni').val(Lista + ",'" + valor + "'");
                    } else {
                        $('#ListMuni').val("'" + valor + "'");
                    }
                });
                $('#SelectUnidad').change(function() {
                    var Lista = $('#ListUni').val();
                    var valor = $('#SelectUnidad').val();
                    if (Lista != "") {
                        $('#ListUni').val(Lista + ",'" + valor + "'");
                    } else {
                        $('#ListUni').val("'" + valor + "'");
                    }
                });
                $('#SelectClave').change(function() {
                    var Lista = $('#ListClave').val();
                    var valor = $('#SelectClave').val();
                    if (Lista != "") {
                        $('#ListClave').val(Lista + ",'" + valor + "'");
                    } else {
                        $('#ListClave').val("'" + valor + "'");
                    }
                });
            });
        </script>
    </body>
</html>

