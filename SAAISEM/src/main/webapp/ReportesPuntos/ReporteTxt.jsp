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
            
            <hr/>

            <div>
                <h3>Reporteador TXT</h3> 
                <h6>Exportar
                    <a class="btn btn-sm btn-success" href="ExportarReporTxt.jsp?User=<%=usua%>"><span class="glyphicon glyphicon-save"></span></a>
                    <a class="btn btn-sm btn-success" href="ReporteTxtImprime.jsp?User=<%=usua%>"><span class="glyphicon glyphicon-print"></span></a>                    
                </h6>               

                <br />
                <div class="panel panel-success">
                    <div class="panel-body table-responsive">
                        <table class="table table-bordered table-striped" id="datosCompras">
                            <thead>
                                <tr>
                                    <td>Clave</td>
                                    <td>Descripción</td>
                                    <td>Can/Req</td>
                                    <td>Can/Sur</td>
                                    <td>Cos/Tot</td>
                                    <td>Cos/Ser</td>
                                    <td>IVA</td>
                                    <td>TOTAL</td>
                                    <td>Pzs No/Sur</td>
                                    <td>Cos No/Sur</td>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet rset = con.consulta("SELECT F_Clave,F_Nombre,FORMAT(F_CanReq,0) AS F_CanReq,FORMAT(F_CanSur,0) AS F_CanSur,"
                                            + "FORMAT(F_CostTotal,2) AS F_CostTotal,FORMAT(F_CostServ,2) AS F_CostServ,FORMAT(F_Iva,2) AS F_Iva,FORMAT(SUM(F_Iva+F_CostTotal),2) AS f_total,"
                                            + "FORMAT(CASE WHEN (F_PzNoSurt<0) THEN F_PzNoSurt*-1 WHEN (F_PzNoSurt>=0) THEN F_PzNoSurt END,0) AS F_PzNoSurt ,"
                                                    + "FORMAT(CASE WHEN (F_CostNoSurt<0) THEN F_CostNoSurt*-1 WHEN (F_CostNoSurt>=0) THEN F_CostNoSurt END,2) AS F_CostNoSurt FROM tb_txtreporte WHERE F_User='"+usua+"' GROUP BY F_Id; ");
                                            while (rset.next()) {
                                %>
                                <tr>

                                    <td><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(2)%></td>
                                    <td><%=rset.getString(3)%></td>
                                    <td><%=rset.getString(4)%></td>
                                    <td><%=rset.getString(5)%></td>
                                    <td><%=rset.getString(6)%></td>
                                    <td><%=rset.getString(7)%></td>
                                    <td><%=rset.getString(8)%></td>
                                    <td><%=rset.getString(9)%></td>
                                    <td><%=rset.getString(10)%></td> 

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
        <%@include file="../jspf/piePagina.jspf" %>

    </body>
</html>


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
                                    $(document).ready(function () {
                                        $('#datosCompras').dataTable();
                                    });
</script>
<script>
    $(function () {
        $("#fecha").datepicker();
        $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
    });

    function ponerFolio(id) {
        document.getElementById('idCom').value = id;
        document.getElementById('F_FolRemi').value = document.getElementById("F_FR" + id).value;
        document.getElementById('F_OrdCom').value = document.getElementById("F_OC" + id).value;
    }

    function validaISEM() {
        if (document.getElementById('NoContrato').value === "") {
            alert('Capture el número de contrato');
            return false;
        }
        if (document.getElementById('NoFolio').value === "") {
            alert('Capture el número de folio');
            return false;
        }
        if (document.getElementById('fecRecepcionISEM').value === "") {
            alert('Capture la fecha');
            return false;
        }
    }

    function ponerRemision(id) {
        var elem = id.split(',');
        document.getElementById('idRem').value = elem[0];
        document.getElementById('remiIncorrecta').value = elem[1];
    }

    function validaRemision() {
        var remiCorrecta = document.getElementById('remiCorrecta').value;
        var fecRemision = document.getElementById('fecRemision').value;

        if (remiCorrecta === "" && fecRemision === "") {
            alert('Ingrese al menos una corrección')
            return false;
        }
    }

    function validaContra(elemento) {
        //alert(elemento);
        var pass = document.getElementById(elemento).value;
        //alert(pass);
        if (pass === "GnKlTolu2014") {
            document.getElementById('actualizaRemi').disabled = false;
        } else {
            document.getElementById('actualizaRemi').disabled = true;
        }
    }
</script>