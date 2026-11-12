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
<%
    double importeTotal = 0;
    float importeTotalTotal = 0;

    /**
     * Para generar las remisiones a partir de los marbetes
     */
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link href="../css/select2.css" rel="stylesheet" type="text/css"/>
        <link href="../css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link rel="stylesheet" type="text/css" href="../css/jquery.dataTables.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipal.jspf"%>
            
            <br>
            <h4> Reporte Concentrado Facturado   </h4>
            <div class="panel panel-success">
                <div class="panel-body">
                    <div class="row">
                        <h5 class="col-sm-1">Fecha</h5>
                        <div class="col-sm-2">
                            <input type="date" id="fechafactura" name="fechafactura" class="form-control" />
                        </div>
                        <h5 class="col-sm-2">Tipo Unidad</h5>
                        <div class="col-sm-3">
                            <select id="TipoUnidad" name="TipoUnidad" class="form-control">
                            </select>
                        </div>
                        <div class="col-sm-2">
                            <button type="button" class="btn btn-success btn-sm form-control" id="Mostrar">Mostrar Todos</button>
                        </div>
                        <div class="col-sm-2">
                            <button type="button" class="btn btn-info btn-sm form-control" id="Descargar">Descargar</button>
                        </div>
                    </div>
                </div>
                <!--
                Insumo a remisionar
                -->
                <div class="panel panel-success table-responsive" id="folDetalle"  >
                    <br/>
                    <div class="panel-body" id="tableTot" >
                        <div id="dynamic"></div>
                    </div>
                </div>
                <div style="display:none;" id="Registro">
                    <div class="row">
                        <label class="col-sm-2"><h4>Total : <samp id="total" </samp></h4></label>
                    </div>
                </div>
            </div>
        </div>
        <br><br><br>
        <%@include file="../jspf/piePagina.jspf" %>
        <!-- 
    ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <!--script src="js/jquery-1.9.1.js"></script>
        <script src="js/jquery-2.1.4.min.js"></script>
        
        
        <script src="js/jquery.dataTables.js"></script-->
        <script src="../js/jquery-1.12.4.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui.js"></script>
        <script src="../js/jquery.dataTables.min_1.js"></script>
        <script src="../js/select2.js" type="text/javascript"></script>
        <script src="../js/sweetalert.min.js" type="text/javascript"></script>
        <script src="../js/existencia/Facturadodia.js"></script>
        <!--script src="js/jquery-1.9.1.js"></script-->
    </body>

</html>

