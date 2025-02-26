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
    String usua = "", tipo = "", nombre = "";
    if (sesion.getAttribute("Usuario") != null) {
        nombre = (String) sesion.getAttribute("nombre");
        usua = (String) sesion.getAttribute("Usuario");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("indexAuditoria.jsp");
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
        <link href="../css/TablasScroll/ScrollTables.css" rel="stylesheet" type="text/css"/>
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipalAuditoria.jspf"%>

            <h5>
                Consulta Existencias
            </h5>
            <div class="panel panel-success">
                <div class="panel-body">
                    <div class="row">
                        <h5 class="col-sm-2">Seleccione Proyecto *</h5>
                        <div class="col-sm-2">
                          <select id="Nombre" name="Nombre" class="form-control">
                                <option value ="" selected disabled>Seleccionar</option>
                                <option value ="1">ISEM</option>                                
                            </select>
                          
                        </div>

                        <div class="col-sm-5" >
                            <div class ="form-group">
                                <div class="input-group">
                                    <span class="input-group-addon">Consulta *</span>
                                    <select class="form-control" name="consultaAuditoria" id="consultaAuditoria">
                                        <option value ="" selected disabled>Seleccionar</option>
                                        <option value="2">Disponible</option>
                                        <option value="1">Lote</option>                                        
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-2">
                            <!--<button type="button" class="btn btn-success btn-sm form-control" id="Mostrar">Mostrar Todos</button>-->
                            <button type="button" class="btn btn-info btn-sm form-control" id="Descargar">Descargar</button>
                        </div>
                    </div>
                </div>
                
                <div class="panel panel-success table-responsive" id="folDetalle"  >
                    <br/>
                    <div class="panel-body" id="tableTot" >
                        <div id="dynamic"></div>
                    </div>
                </div>
                                        
                <div style="display:none;" id="Registro">
                    <div class="row">
                        <label class="col-sm-2"><h4>No. Claves : <samp id="totalClave"> </samp></h4></label>
                        <label class="col-sm-4"><h4>Total : <samp id="total"> </samp></h4></label>
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
        <script src="../js/existencia/ExistenciaProyectoAuditoria.js"></script>
        <!--script src="js/jquery-1.9.1.js"></script-->
    </body>

</html>

