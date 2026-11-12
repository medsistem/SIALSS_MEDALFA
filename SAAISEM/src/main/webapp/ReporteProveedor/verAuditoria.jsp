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
    String usua = "", tipo = "";

    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String Id = "", F_Oc = "", FolRemi = "", Folio = "", NomPro = "", Nombre = "";
    try {

        Id = request.getParameter("Id");

    } catch (Exception e) {

    }
    if (Id == null) {
        Id = "";
    }

    try {
        con.conectar();

        ResultSet rset = con.consulta("SELECT V.F_Oc, V.F_FolRemi, V.F_Folio, P.F_NomPro, CONCAT( U.F_Nombre, ' ', U.F_Apellido, ' ', U.F_ApellidoM ) AS F_Nombre FROM tb_validaauditor V INNER JOIN tb_compra C ON V.F_Oc = C.F_OrdCom AND V.F_FolRemi = C.F_FolRemi AND V.F_Folio = C.F_ClaDoc INNER JOIN tb_proveedor P ON C.F_ProVee = P.F_ClaProve INNER JOIN tb_usuario U ON V.F_User = U.F_Usu WHERE V.F_Id = '" + Id + "' GROUP BY V.F_Oc, V.F_FolRemi, V.F_Folio;");
        if (rset.next()) {
            F_Oc = rset.getString(1);
            FolRemi = rset.getString(2);
            Folio = rset.getString(3);
            NomPro = rset.getString(4);
            Nombre = rset.getString(5);
        }

        con.cierraConexion();
    } catch (Exception e) {

    }


%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
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
            <hr/>

            <div>
                <h3>Registro Auditor&nbsp;&nbsp;&nbsp;&nbsp;<%=Nombre%></h3>
                <h4>OC: <%=F_Oc%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Remisión: <%=FolRemi%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Folio: <%=Folio%></h4>
                <h4>Proveedor: <%=NomPro%></h4>
                <div class="text-right">
                    <a href="ReporteAuditoria.jsp" class="btn btn-default">Regresar</a>
                </div>

                <br />
                <div class="panel panel-success">
                    <div class="panel-body">
                        <table class="table table-bordered table-striped" id="datosCompras">
                            <thead>
                                <tr>
                                    <td>Clave</td>
                                    <td>Descripción</td>
                                    <td>Lote</td>
                                    <td>Caducidad</td>
                                    <td>Cantidad</td>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet rset = con.consulta("SELECT V.F_Oc, V.F_FolRemi, V.F_Folio, C.F_ClaPro, M.F_DesPro, L.F_ClaLot, L.F_FecCad, C.F_CanCom FROM tb_validaauditor V INNER JOIN tb_compra C ON V.F_Oc = C.F_OrdCom AND V.F_FolRemi = C.F_FolRemi AND V.F_Folio = C.F_ClaDoc LEFT JOIN tb_medica M ON C.F_ClaPro = M.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_Proyecto FROM tb_lote WHERE F_ClaLot != 'X' GROUP BY F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_Proyecto ) L ON C.F_ClaPro = L.F_ClaPro AND C.F_Lote = L.F_FolLot AND C.F_Proyecto = L.F_Proyecto WHERE V.F_Id = '" + Id + "';");
                                            while (rset.next()) {
                                %>
                                <tr>
                                    <td><%=rset.getString(4)%></td>
                                    <td><%=rset.getString(5)%></td>
                                    <td><%=rset.getString(6)%></td>
                                    <td><%=rset.getString(7)%></td>
                                    <td><%=rset.getString(8)%></td>

                                </tr>
                                <%
                                            }
                                        } catch (Exception e) {

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
        </div>
        <br><br><br>
        <%@include file="../jspf/piePagina.jspf" %>
    </body>
</html>


<!-- 
================================================== -->
<!-- Se coloca al final del documento para que cargue mas rapido -->
<!-- Se debe de seguir ese orden al momento de llamar los JS -->
<script src="../js/jquery-1.9.1.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/bootstrap-datepicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script>
    $(document).ready(function () {
        $('#datosCompras').dataTable();
    });
</script>
<script>
    $(function () {
        $("#fecha").datepicker();
        $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
    });
</script>