<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="conn.ConectionDB"%>
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
    String fechaSistema = formateador.format(fechaActual);
    String Id = "", Unidad = "", Unidad1 = "", usua = "", tipo = "", AccionS = "", AccionA = "", AccionST = "", AccionAT = "", AccionTAT = "", AccionSAT = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    try {
        Unidad = request.getParameter("Unidad");
        Unidad1 = request.getParameter("Unidad1");
        Id = request.getParameter("Id");
        AccionS = request.getParameter("AccionS");
        AccionA = request.getParameter("AccionA");
        AccionST = request.getParameter("AccionST");
        AccionAT = request.getParameter("AccionAT");
        AccionTAT = request.getParameter("AccionTAT");
        AccionSAT = request.getParameter("AccionSAT");
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

    if (Unidad == null) {
        Unidad = "";
    }
    if (Unidad1 == null) {
        Unidad1 = "";
    }
    if (Id == null) {
        Id = "";
    }
    if (AccionA == null) {
        AccionA = "";
    }
    if (AccionS == null) {
        AccionS = "";
    }
    if (AccionAT == null) {
        AccionAT = "";
    }
    if (AccionSAT == null) {
        AccionSAT = "";
    }
    if (AccionTAT == null) {
        AccionTAT = "";
    }
    if (AccionST == null) {
        AccionST = "";
    }
    if (!(Unidad.equals("0"))) {
        Unidad = Unidad;
    } else if (Unidad1 != "") {
        Unidad = Unidad1;
    }

    ConectionDB con = new ConectionDB();

    //System.out.println("S = " + AccionST + " A = " + AccionAT);
    try {
        con.conectar();
        if (!(AccionS.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N" + Unidad + " = 0 WHERE F_ClaPro = '" + AccionS + "';");
        } else if (!(AccionA.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N" + Unidad + " = 1 WHERE F_ClaPro = '" + AccionA + "';");
        } else if (!(AccionAT.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N" + Unidad + " = 1;");
        } else if (!(AccionST.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N" + Unidad + " = 0;");
        }else if (!(AccionTAT.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N1 = 1, F_N2 = 1, F_N3 = 1, F_N4 = 1, F_N5 = 1, F_N6 = 1, F_N7 = 1, F_N8 = 1, F_N9 = 1, F_N10 = 1, F_N11 = 1, F_N12 = 1, F_N13 = 1, F_N14 = 1, F_N15 = 1, F_N16 = 1;");
        }else if (!(AccionSAT.equals(""))) {
            con.actualizar("UPDATE tb_medica SET F_N1 = 0, F_N2 = 0, F_N3 = 0, F_N4 = 0, F_N5 = 0, F_N6 = 0, F_N7 = 0, F_N8 = 0, F_N9 = 0, F_N10 = 0, F_N11 = 0, F_N12 = 0, F_N13 = 0, F_N14 = 0, F_N15 = 0, F_N16 = 0;");
        }
        con.cierraConexion();
    } catch (Exception e) {
        System.out.println(e.getMessage());
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
        <link rel="stylesheet" href="css/select2.css" />
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="row">
                <h4 class="col-sm-6">Catálogo de insumo por nivel de atención</h4>
            </div>
            <form name="forma1" id="forma1" action="CatalogoUnidad.jsp" method="post">
                <div class="panel-footer">
                    <div class="row">
                        <label class="control-label col-sm-1" for="fecha_ini">Unidad</label>
                        <div class="col-sm-6">
                            <select name="Unidad" id="Unidad" class="form-control">
                                <option value="0">--Seleccione --</option>
                                <option value="1">N1 Unidad Móvil  y Caravanas</option>
                                <option value="2">N2 Centros de Salud Rurales</option>
                                <option value="3">N3 Centros de Salud Urbanos y CEAPS CAD, Geriátricos y UNEMES</option>
                                <option value="4">N4 CEAPS</option>
                                <option value="5">N5 CISAME</option>
                                <option value="6">N6 Maternidades</option>
                                <option value="7">N7 Módulos Odontopediatricos y Centro de Atención Estomatológico</option>
                                <option value="8">N8 Hospitales Municipales</option>
                                <option value="9">N9 Hospitales Generales</option>
                                <option value="10">N10 Hospitales Materno Infantiles</option>
                                <option value="11">N11 Hospital Salud Visual</option>
                                <option value="12">N12 HMI Lic. Mónica Pretelini</option>
                                <option value="13">N13 Centro Medico Lic. Adolfo López Mateos</option>
                                <option value="14">N14 Hospitales Psiquiátricos</option>
                                <option value="15">N15 S.U.E.M.</option>
                                <option value="16">N16 Laboratorio Estatal de Salud Pública</option>
                            </select>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-block btn-info" name="AccionTAT" value="AccionTAT">Activar Todos Niveles</button>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-block btn-success" name="AccionSAT" value="AccionSAT">Suspender Todos Niveles</button>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-sm-2">
                            <button class="btn btn-block btn-success" name="Accion" value="mostrar" >MOSTRAR&nbsp;<label class="glyphicon glyphicon-search"></label></button>
                        </div>
                        <div class="col-sm-2">
                            <a class="btn btn-block btn-primary" href="CatalogoUnidadgnr.jsp?Unidad=<%=Unidad%>" target="_blank">DESCARGAR&nbsp;1x1<label class="glyphicon glyphicon-download"></label></a>
                        </div>
                        <div class="col-sm-2">
                            <a class="btn btn-block btn-warning" href="CatalogoUnidadgnr.jsp?Unidad=" target="_blank">DESCARGAR&nbsp;Todo<label class="glyphicon glyphicon-download"></label></a>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-block btn-info" name="AccionAT" value="AccionAT">Activar Todas</button>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-block btn-danger" name="AccionST" value="AccionST">Suspender Todas</button>
                        </div>
                    </div>
                </div>  
                <div>
                    <br />
                    <%
                        int Total = 0, Medicamento = 0, MaterialC = 0;
                        try {
                            con.conectar();
                            ResultSet rset = con.consulta("SELECT COUNT(*), COUNT( IF (MD.F_TipMed = 2504, 1, NULL)) AS MED, COUNT( IF (MD.F_TipMed = 2505, 1, NULL)) AS MAT FROM tb_medica MD WHERE MD.F_N" + Unidad + " = 1;");
                            while (rset.next()) {
                                Total = rset.getInt(1);
                                Medicamento = rset.getInt(2);
                                MaterialC = rset.getInt(3);
                            }
                            con.cierraConexion();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    %>
                    <label>Total Insumos = <%=Total%>&nbsp;&nbsp;&nbsp;Medicamento = <%=Medicamento%>&nbsp;&nbsp;&nbsp;&nbsp;Material Curación = <%=MaterialC%>&nbsp;&nbsp;&nbsp;Nivel Seleccionado =&nbsp;&nbsp;&nbsp;<%=Unidad%></label>
                    <div class="panel panel-success">
                        <input type="hidden" class="form-control" name="Unidad1" id="Unidad1" value="<%=Unidad%>" />
                        <div class="panel-body table-responsive">
                            <table class="table table-bordered table-striped" id="datosCompras">
                                <thead>
                                    <tr>
                                        <td>Clave</td>
                                        <td>Descripción</td>
                                        <td>Autorizado</td>
                                        <td>Tipo</td>
                                        <td>Modificar</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT F_ClaPro, F_DesPro, CASE WHEN F_N" + Unidad + " = 1 THEN 'SI' ELSE 'NO' END AS AUTORIZADO, F_N" + Unidad + ", T.F_DesMed FROM tb_medica M INNER JOIN tb_tipmed T ON M.F_TipMed = T.F_TipMed;");
                                            while (rset.next()) {
                                    %>
                                    <tr>
                                        <td><%=rset.getString(1)%></td>
                                        <td><%=rset.getString(2)%></td>
                                        <td><%=rset.getString(3)%></td>
                                        <td><%=rset.getString(5)%></td>
                                        <td>
                                            <%if (rset.getInt(4) == 1) {%>
                                            <button class="btn btn-block btn-warning" name="AccionS" value="<%=rset.getString(1)%>">Suspender</button>
                                            <%} else {%>
                                            <button class="btn btn-block btn-info" name="AccionA" value="<%=rset.getString(1)%>">Activar</button>
                                            <%}%>

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
        <script src="js/select2.js"></script>
        <script>
            $(document).ready(function () {
                $('#datosCompras').dataTable();
                $('#Unidad').select2();
            });
        </script>
    </body>
</html>
