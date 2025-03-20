<%@page import="conn.ConectionDB"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterD = new DecimalFormat("#,###,###.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();

    String insumoEspecial = "";
    try {
        insumoEspecial = request.getParameter("insumoEspecial");
    } catch (Exception e) {
    }
    if (insumoEspecial == null) {
        insumoEspecial = "";
    }
%>
<html>
    <style>
        #insOnco {
            display: none;
        }
    </style>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <script type="text/javascript" src="js/tipoInsumo.js"></script>

        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="jspf/menuPrincipal.jspf" %>
            <div>
                <h3>Reporte de Insumo Especial</h3>
                <br>
                <div class="row">
                    <form action="reporteInsumoEspecial.jsp" method="get">
                        <h4 class="col-sm-1" valign="top">Insumo </h4>
                        <div class="col-sm-5">
                            <select class="form-control" name="insumoEspecial" id="insumoEspecial" onchange="this.form.submit();">
                                <option value="">--Seleccionar el tipo de insumo--</option>
                                
                                
                                <option value="APE">Ape</option>
                                <option value="Controlado">Controlado</option>
                              
                                <option value="Red_Fria">Red Fría</option>
                            
                            </select>

                        </div>
                        &nbsp;&nbsp;&nbsp;
                        <%if (insumoEspecial.equals("Controlado") || insumoEspecial.equals("Red_Fria") || insumoEspecial.equals("APE") || insumoEspecial.equals("Oncologico")) {%>
                        <a href="gnrExcelTipoInsumo.jsp?tipoIns=<%=request.getParameter("insumoEspecial")%>" class="btn btn-success" onsubmit="return descargarExcel(this)" name="accion" value="enviar" method="get"><span class="glyphicon glyphicon-download-alt"></span> Descargar</a>
                        <% } %>
                        &nbsp;&nbsp;&nbsp;
                        <button type="button" class="btn btn-info" data-toggle="modal" data-target="#myModal"><span class="glyphicon glyphicon-plus col-sm-2"></span> Clave</button>
                        <br><br><br>
                        <h3><% String insumoSelecionado = request.getParameter("insumoEspecial");
                            if (insumoSelecionado == null) {
                                out.println("");
                            } else {
                                out.println("Catálogo de  " + insumoSelecionado);
                            }
                            %></h3>                
                        <div class="container">
                            <div class="panel panel-success">
                                <div class="panel-body">
                                    <table class="table table-bordered table-striped" id="datosInsumoEspecial">
                                        
                                        <thead>
                                            <tr>
                                                <td class="text-center"style="width:150px">Clave</td>
                                                <td class="text-center">Descripción</td>
                                                <td class="text-center" style="width:200px">Presentación</td>
                                                <% if (insumoEspecial.equals("Oncologico")){ %>
                                                <td class="text-center" style="width:200px">Clasificación</td>
                                                   <% } %>
                                                <td class="text-center" style="width:100px"  ><inpunt type="button">Eliminar</td>
                                            </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    try {
                                                        con.conectar();
                                                        ResultSet rset = null;
                                                        if (insumoEspecial.equals("Controlado")) {
                                                            rset = con.consulta("SELECT c.F_ClaPro, m.F_DesPro, m.F_PrePro FROM tb_controlados AS c INNER JOIN tb_medica AS m ON c.F_ClaPro = m.F_ClaPro; ");
                                                        } else if ((insumoEspecial.equals("Red_Fria"))) {
                                                            rset = con.consulta("SELECT rf.F_ClaPro, m.F_DesPro, m.F_PrePro FROM tb_redfria AS rf INNER JOIN tb_medica AS m ON rf.F_ClaPro = m.F_ClaPro; ");
                                                        } else if ((insumoEspecial.equals("APE"))) {
                                                            rset = con.consulta("SELECT APE.F_ClaPro, m.F_DesPro, m.F_PrePro FROM tb_ape AS APE INNER JOIN tb_medica AS m ON APE.F_ClaPro = m.F_ClaPro; ");
                                                        } else if ((insumoEspecial.equals("Seco"))) {
                                                            rset = con.consulta("SELECT m.F_ClaPro, m.F_DesPro, m.F_PrePro FROM tb_medica AS m ");
                                                        }
                                                        while (rset.next()) {
                                                %>
                                                <tr>
                                                    <td class="text-center"><%=rset.getString(1)%></td>
                                                    <td class="text-left"><%=rset.getString(2)%></td>
                                                    <td class="text-center"><%=rset.getString(3)%></td>
                                                    <% if (insumoEspecial.equals("Oncologico")) { %>
                                                    <td class="text-center"><%=rset.getString(4)%></td>
                                                    <% } %>
                                                    <td>
                                                        <a href="TipoInsumo?F_Clapro=<%=rset.getString(1)%>&tipoIns=<%=(insumoSelecionado)%>&accion=eliminar" class="btn btn-danger"  >Eliminar</a>
                                                    </td>
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
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <br><br><br>
                <form name="form1" action="TipoInsumo" method="get" onsubmit="return validarForma(this)">
                    <div class="modal fade" id="myModal" role="dialog">
                        <div class="modal-dialog">

                            <!-- Modal content-->

                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                    <h4 class="modal-title">Agregar Clave</h4>
                                </div>
                                <div class="modal-body"> 

                                    <div class="col-sm-6">
                                        <select class="form-control" name="ingresarClave" id="ingresarClave" onchange="filtrarClave()">
                                            <option value="">--Seleccionar Clave--</option>
                                            <% try {
                                                    con.conectar();
                                                    ResultSet rset = con.consulta("SELECT F_ClaPro, F_DesPro FROM tb_medica WHERE F_ProIsem = 1;");
                                                    while (rset.next()) {
                                            %>
                                            <option value="<%=rset.getString(1)%>"><%=rset.getString(1)%></option>
                                            <% }
                                                    con.cierraConexion();
                                                } catch (Exception e) {
                                                    System.out.println(e.getMessage());
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <br><br>

                                    <div class="col-sm-6">
                                        <select  class="form-control" name="tipoIns" id="tipoIns" >
                                            <option value="">--Seleccionar el tipo de insumo--</option>
                                            <option value="Controlado">Controlado</option>
                                            <option value="Red_Fria">Red Fría</option>
                                            <option value="APE">APE</option>
                                            <option value="Oncologico">Oncologico</option>
                                        </select>
                                    </div>
                                    <br><br>

                                    <div class="col-sm-6">
                                        <select  class="form-control" name="insOnco" id="insOnco">
                                            <option value="">--tipo de insumo oncologico--</option>                                            
                                            <option value="RF">Red Fría</option>
                                            <option value="APE">APE</option>
                                        </select>
                                    </div>
                                </div>
                                <br>
                                <br>
                                <br>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-success" name="accion" value="agregar" >Agregar</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>

            </div>
        </div>
    </body>
</html>
<%@include file="/jspf/piePagina.jspf" %>

<script src="js/jquery-1.9.1.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/jquery-ui-1.10.3.custom.js"></script>
<script src="js/bootstrap-datepicker.js"></script>
<script src="js/jquery.dataTables.js"></script>
<script src="js/dataTables.bootstrap.js"></script>
<script type="text/javascript" src="js/tipoInsumo.js"></script>


