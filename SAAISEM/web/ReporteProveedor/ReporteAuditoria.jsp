<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

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
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";

    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();
    String fechaCap = "";
    String Proveedor = "";
    try {
        fechaCap = df2.format(df3.parse(request.getParameter("Fecha")));
    } catch (Exception e) {

    }
    if (fechaCap == null) {
        fechaCap = "";
    }
    try {
        Proveedor = request.getParameter("Proveedor");
    } catch (Exception e) {

    }
    if (Proveedor == null) {
        Proveedor = "";
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
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="../jspf/menuPrincipal.jspf" %>
            <div>
                <h3>Reporte Auditoría</h3>
                <div class="row">
                    <form action="ReporteAuditoria.jsp" method="post">
                        <h4 class="col-sm-1">Auditor</h4>
                        <div class="col-sm-5">
                            <select class="form-control" name="Proveedor" id="Proveedor" onchange="this.form.submit();">
                                <option value="">--Auditor--</option>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rset = con.consulta("SELECT V.F_User, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu GROUP BY V.F_User;");
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
                        <h4 class="col-sm-2">Fecha Validación:</h4>
                        <div class="col-sm-2">
                            <input type="text" class="form-control" data-date-format="dd/mm/yyyy" id="Fecha" name="Fecha"  onchange="this.form.submit();" />
                        </div>
                        <a class="btn btn-primary" href="ReporteAuditoria.jsp">Todo</a>
                        <a class="btn btn-primary" href="ReporteAuditoria_gnr.jsp?provee=<%=Proveedor%>&fecha=<%=fechaCap%>">Descargar&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                    </form>
                </div>
            </div>
        </div>
        <br />
        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras">
                        <thead>
                            <tr>                                
                                <td class="text-center">Fecha</td>
                                <td class="text-center">Usuario</td>
                                <td class="text-center">Proveedor</td>
                                <td class="text-center">OC</td>
                                <td class="text-center">Remisión</td>
                                <td class="text-center">Folio Int</td>
                                <td class="text-center">Cantidad</td>
                                <td class="text-center">Claves</td>
                                <td class="text-center">Lotes</td>
                                <td class="text-center">Proyecto</td>
                                <td class="text-center">Ver</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    if (!(Proveedor.equals("")) && (!(fechaCap.equals("")))) {

                                        rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro, '' ), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro, ''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, ''), V.F_Id FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE V.F_User = '" + Proveedor + "' AND DATE(V.F_Date) = '" + fechaCap + "';");
                                    } else if (!(Proveedor.equals("")) && (fechaCap.equals(""))) {

                                        rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro, '' ), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro, ''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, ''), V.F_Id FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE V.F_User = '" + Proveedor + "';");
                                    } else if (Proveedor.equals("") && (!(fechaCap.equals("")))) {

                                        rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro, '' ), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro, ''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, ''), V.F_Id FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc WHERE DATE(V.F_Date) = '" + fechaCap + "';");
                                    } else {
                                        rset = con.consulta("SELECT DATE_FORMAT(V.F_Date, '%d/%m/%Y') AS F_Date, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre, IFNULL (CO.F_NomPro, '' ), V.F_Oc, V.F_FolRemi, V.F_Folio, CO.F_CanCom, IFNULL (CO.F_ClaPro, ''), IFNULL (CO.F_Lote, ''), IFNULL (CO.F_DesProy, ''), V.F_Id FROM tb_validaauditor V INNER JOIN tb_usuario U ON V.F_User = U.F_Usu LEFT JOIN ( SELECT C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee, P.F_NomPro, C.F_Proyecto, PR.F_DesProy, DATE_FORMAT(C.F_FecApl, '%d/%m/%Y') AS F_FecApl, SUM(F_CanCom) AS F_CanCom, COUNT(DISTINCT(F_ClaPro)) AS F_ClaPro, COUNT(DISTINCT(F_Lote)) AS F_Lote FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_proyectos PR ON C.F_Proyecto = PR.F_Id GROUP BY C.F_OrdCom, C.F_FolRemi, C.F_ClaDoc, C.F_ProVee ) AS CO ON V.F_Oc = CO.F_OrdCom AND V.F_FolRemi = CO.F_FolRemi AND V.F_Folio = CO.F_ClaDoc;");
                                    }
                                    while (rset.next()) {

                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td class="text-center"><%=rset.getString(3)%></td>
                                <td class="text-center"><%=rset.getString(4)%></td>
                                <td class="text-center"><%=rset.getString(5)%></td>
                                <td class="text-center"><%=rset.getString(6)%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt(7))%></td>
                                <td class="text-center"><%=rset.getString(8)%></td>
                                <td class="text-center"><%=rset.getString(9)%></td>
                                <td class="text-center"><%=rset.getString(10)%></td>
                                <td class="text-center">
                                    <a href="verAuditoria.jsp?Id=<%=rset.getString(11)%>" class="btn btn-block form-control glyphicon glyphicon-eye-open btn-success"></a>
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
<script>
                                $(document).ready(function () {
                                    $('#datosCompras').dataTable();
                                    $("#Fecha").datepicker();
                                    $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
                                });
</script>