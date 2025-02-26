    <%@page import="conn.ConectionDB"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.gnk.util.ParameterUtils"%>
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
    String usua = "", tipo = "", nombre = "";
    if (sesion.getAttribute("Usuario") != null) {
        nombre = (String) sesion.getAttribute("nombre");
        usua = (String) sesion.getAttribute("Usuario");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexAuditoria.jsp");
    }
    ConectionDB con = new ConectionDB();
    String fechaInicial = "";
    String fechaFinal = "";
    String Proveedor = "";
    String andString = "c.F_ClaPro != '9999' ";
    String todo = "";
    fechaInicial = ParameterUtils.getParameter("fechaInicial", request);
    fechaFinal = ParameterUtils.getParameter("fechaFinal", request);

    Proveedor = ParameterUtils.getParameter("proveedor", request);
    todo = ParameterUtils.getParameter("todo", request);

    String clave = ParameterUtils.getParameter("clave", request);
    String lote = ParameterUtils.getParameter("lote", request);
    String remision = ParameterUtils.getParameter("remision", request);
    String oc = ParameterUtils.getParameter("oc", request);
    String getString="proveedor="+Proveedor+"&fechaInicial="+fechaInicial+"&fechaFinal="+fechaFinal+"&clave="+clave+"&lote="+lote+"&oc="+oc+"&remision="+remision;
    
    if (!Proveedor.isEmpty()) {
        andString = "p.F_ClaProve='" + Proveedor + "' ";
    }
    if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
        andString = andString.isEmpty() ? "c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "'" : andString + "AND c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "' ";
    }
    if (!clave.isEmpty()) {
        andString = andString.isEmpty() ? "c.F_ClaPro = '" + clave + "' " : andString + "AND c.F_ClaPro = '" + clave + "' ";
    }
    if (!lote.isEmpty()) {
        andString = andString.isEmpty() ? "l.F_ClaLot = '" + lote + "' " : andString + "AND l.F_ClaLot = '" + lote + "' ";
    }
    if (!oc.isEmpty()) {
        andString = andString.isEmpty() ? "c.F_OrdCom = '" + oc + "' " : andString + "AND c.F_OrdCom = '" + oc + "' ";
    }
    if (!remision.isEmpty()) {
        andString = andString.isEmpty() ? "c.F_FolRemi = '" + remision + "' " : andString + "AND c.F_FolRemi = '" + remision + "' ";
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <link href="../css/select2.css" rel="stylesheet">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="../jspf/menuPrincipalAuditoria.jspf" %>
            <div>
                <h3>Reporte por Fecha Proveedor</h3>
                <div class="row">
                    <form action="ReporteFAuditoria.jsp" method="post" class="form-inline col-md-12">
                        <div class="row">
                            <h4 class="col-sm-1">Proveedor</h4>
                            <div class="col-sm-10">
                                <select class="form-control" name="proveedor" id="proveedor">
                                    <option value="">--Proveedor--</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT C.F_ProVee,P.F_NomPro FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee=P.F_ClaProve GROUP BY C.F_ProVee;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    %>

                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="input-group">
                                    <span class="input-group-addon">Fecha Inicial</span>
                                    <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="fechaInicial" name="fechaInicial" />
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="input-group">
                                    <span class="input-group-addon">Fecha Final</span>
                                    <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="fechaFinal" name="fechaFinal" />
                                </div>

                            </div>
                        </div>
                        <br/>
                        <div class="row">
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">Clave</span>
                                    <input type="text" class="form-control" id="clave" name="clave" />
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">Lote</span>
                                    <input type="text" class="form-control" id="lote" name="lote" />
                                </div>

                            </div>
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">Remisión</span>
                                    <input type="text" class="form-control" id="remision" name="remision" />
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">OC</span>
                                    <input id="oc" type="text" class="form-control" name="oc" >
                                </div>
                            </div>
                        </div>
                        <hr>
                        <div class="row">
                            <div class="col-md-12">
                                <button type="submit" class="btn btn-success">Buscar</button>
                               <!-- <button type="submit" class="btn btn-success" name="todo" value="todo">Todo</button> -->
                                <a class="btn btn-success" href="ReporteF_gnrCompra.jsp?<%=getString%>">Descargar&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <br />
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras">
                        <thead>
                            <tr>                                
                                <td class="text-center">Proyecto</td>
                                <td class="text-center">Proveedor</td>
                                <td class="text-center">Clave</td>
                                <td class="text-center">Marca</td>
                                <td class="text-center">Lote</td>
                                <td class="text-center">Caducidad</td>
                                <td class="text-center">Piezas</td>
                                <td class="text-center">Fecha</td>
                                <td class="text-center">OC</td> 
                                <td class="text-center">Folio Medalfa</td> 
                                <td class="text-center">Remisión</td>
                                <td class="text-center">Usuario</td>
                                <td class="text-center">Costo U</td>
                                <td class="text-center">Monto</td>
                                <td class="text-center">O.S.</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    if (todo.equals("")) {

                                        rset = con.consulta("SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, m.F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id INNER JOIN tb_marca m on l.F_ClaMar = m.F_ClaMar where c.F_FecApl >= '2021-01-01' AND " + andString + " GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                    } else {
                                        rset = con.consulta("SELECT p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, m.F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id INNER JOIN tb_marca m on l.F_ClaMar = m.F_ClaMar GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                    }

                                    while (rset.next()) {

                            %>
                            <tr>
                                <td><%=rset.getString("F_DesProy")%></td>
                                <td><%=rset.getString("F_NomPro")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaPro")%></td>
                                <td class="text-center"><%=rset.getString("F_DesMar")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaLot")%></td>
                                <td class="text-center"><%=rset.getString("fecCad")%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt("cantidad"))%></td>
                                <td class="text-center"><%=rset.getString("fecapl")%></td>
                                <td class="text-center"><%=rset.getString("F_OrdCom")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaDoc")%></td>
                                <td class="text-center"><%=rset.getString("F_FolRemi")%></td>
                                <td class="text-center"><%=rset.getString("F_User")%></td>
                                <td class="text-center"><%=formatterD.format(rset.getDouble("F_Costo"))%></td>
                                <td class="text-center"><%=formatterD.format(rset.getDouble("monto"))%></td>
                                <td class="text-center"><%=rset.getString("F_OrdenSuministro")%></td>
                                
                            </tr>
                            <%
                                    }
                                    con.cierraConexion();

                                } catch (Exception e) {
                                    con.cierraConexion();
                                    System.out.println(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <br><br><br>
    </body>
</html>
<%@include file="../jspf/piePagina.jspf" %>

<script src="../js/jquery-2.1.4.min.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/bootstrap-datepicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script src="../js/select2.js"></script>
<script>
    $(document).ready(function () {
        $('#datosCompras').dataTable();
        $("#Fecha").datepicker();
        $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
        $("#Proveedor").select2();
    });
</script>