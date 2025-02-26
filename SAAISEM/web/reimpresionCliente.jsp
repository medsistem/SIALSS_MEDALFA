

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    java.util.Date fecha = new Date();
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
          tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String FechaIni = "", FechaFin = "";
    try {
        FechaIni = request.getParameter("FechaIni");
        FechaFin = request.getParameter("FechaFin");
    } catch (Exception e) {

    }
    if (FechaIni == null) {
        FechaIni = df2.format(fecha);
    }
    if (FechaFin == null) {
        FechaFin = df2.format(fecha);
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
            <%@include file="jspf/menuPrincipalCompra.jspf" %>
            <hr/>

            <div>
                <h3>Reimpresion de folios de Compras</h3>
                <h4>Seleccione el folio a imprimir</h4>
                <form name="FormOC" action="reimpresionCliente.jsp" method="Post">
                    <div class="row">
                        <h4 class="col-sm-2">Rango de Fecha</h4>
                        <div class="col-sm-2">
                            <input class="form-control" name="FechaIni" id="FechaIni" type="date" value="<%=FechaIni%>"/>
                        </div>
                        <div class="col-sm-2">
                            <input class="form-control" name="FechaFin" id="FechaFin" type="date" value="<%=FechaFin%>"/>
                        </div>
                        <div class="col-sm-2">
                            <button class="btn btn-success" name="accion" value="Clave">Por Fecha</button>
                        </div>
                        <!--div class="col-sm-2">
                            <a href="gnrSAE.jsp?FechaIni=<%=FechaIni%>&FechaFin=<%=FechaFin%>" target="_black" class="btn btn-info glyphicon glyphicon-export">Exportar SAE</a>
                        </div-->
                    </div>
                    <br />
                     </form>
                    <div class="panel panel-success">
                        <div class="panel-body table-responsive">
                            <table class="table table-bordered table-striped" id="datosCompras">
                                <thead>
                                    <tr>
                                        <th>No. Folio</th>
                                        <th>Folio Remisión</th>
                                        <th>Orden de Compra</th>
                                        <th>Fecha Ingreso</th>
                                        <th>Usuario</th>
                                        <th>Proveedor</th>
                                        <th>Compra</th>
                                        <th>Ver Compra</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        try {
                                            con.conectar();
                                            try {
                                                if ((FechaIni != "") && (FechaFin != "")) {
                                                    ResultSet rset = con.consulta("SELECT c.F_ClaDoc, c.F_FolRemi, c.F_OrdCom, c.F_FecApl, c.F_User, p.F_NomPro FROM tb_compra c, tb_proveedor p where c.F_ProVee = p.F_ClaProve AND c.F_FecApl BETWEEN '" + FechaIni + "' AND '" + FechaFin + "' GROUP BY F_OrdCom, F_FolRemi,c.F_FecApl,F_ClaDoc ORDER BY F_FolRemi ASC; ");
                                                    while (rset.next()) {
                                    %>
                                    <tr>

                                        <td><%=rset.getString(1)%></td>
                                        <td> <%=rset.getString(2)%>    </td>
                                        <td><%=rset.getString(3)%></td>
                                        <td><%=df3.format(df2.parse(rset.getString(4)))%></td>
                                        <td><%=rset.getString(5)%></td>
                                        <td><%=rset.getString(6)%></td>
                                        <td>
                                            <a href="reimpReporte.jsp?F_FolRemi=<%=rset.getString("F_FolRemi")%>&F_OrdCom=<%=rset.getString("F_OrdCom")%>&fol_gnkl=<%=rset.getString(1)%>&fecha=<%=rset.getString(4)%>" target="_blank" class="btn btn-block btn-success">ACUSE MDF</a>
                                        </td>
                                        
                                        <td>
                                            <form action="verCompra.jsp" method="post">
                                                <input class="hidden" name="F_FolRemi" value="<%=rset.getString("F_FolRemi")%>">
                                                <input class="hidden" name="F_OrdCom" value="<%=rset.getString("F_OrdCom")%>">
                                                <input class="hidden" name="fol_gnkl" value="<%=rset.getString(1)%>">
                                                 <input class="hidden" name="F_FecApl" value="<%=rset.getString(4)%>">
                                                <button class="btn btn-block btn-default">Ver Compra</button>
                                            </form>
                                        </td>

                                    </tr>
                                    <%
                                                    }
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
        <%@include file="jspf/piePagina.jspf" %>


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