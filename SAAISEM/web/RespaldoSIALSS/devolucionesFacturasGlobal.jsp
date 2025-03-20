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
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "",fol_gnkl="";
    
   String username = "";
    if (sesion.getAttribute("nombre") != null ) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        username = (String) sesion.getAttribute("Usuario");
         if(!tipo.equals("1") && !username.equals("Francisco")  && !username.equals("carolina") && !username.equals("LuisJ") && !username.equals("MariaC") && !username.equals("GenaroC")){
            response.sendRedirect("./index.jsp");
        }} else {
        response.sendRedirect("index.jsp");
    }
    
    
    ConectionDB con = new ConectionDB();
    
    System.out.println("Hola"+fol_gnkl);
    
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
            <div>
                <h3>Devoluciones</h3>
                <h4>Folio de Factura: <%=fol_gnkl%></h4>
                <%
                    try {
                        con.conectar();
                        try {
                            ResultSet rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,SUM(F.F_CantReq) as requerido,SUM(F.F_CantSur) as surtido,F.F_Costo,SUM(F.F_Monto) as importe, F.F_Ubicacion FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli WHERE F.F_ClaDoc='" + fol_gnkl + "' GROUP BY F.F_ClaDoc");
                            while (rset.next()) {


                %>
                <h4>Cliente: <%=rset.getString(1)%></h4>
                <h4>Fecha de Entrega: <%=rset.getString(2)%></h4>
                <h4>Factura: <%=rset.getString(3)%></h4>
                <%
                    int req = 0, sur = 0;
                    Double imp = 0.0;
                    ResultSet rset2 = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,(F.F_CantSur) as surtido,(F.F_CantReq) as requerido,F.F_Costo,(F.F_Monto) as importe, F.F_Ubicacion FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli WHERE F.F_ClaDoc='" + fol_gnkl + "' AND F_StsFact='A' GROUP BY U.F_NomCli,F.F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,L.F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto");
                    while (rset2.next()) {
                        req = req + rset2.getInt("requerido");
                        sur = sur + rset2.getInt("surtido");
                        imp = imp + rset2.getDouble("importe");
                        //System.out.println(req);
                    }
                %>

                <div class="row">
                    <h5 class="col-sm-2">Total Solicitado: <%=formatter.format(req)%></h5>
                    <h5 class="col-sm-3">Total Surtido: <%=formatter.format(sur)%></h5>
                    <h5 class="col-sm-3">Total Importe: $ <%=formatterDecimal.format(imp)%></h5>
                    <a href="AdministraRemisiones?Accion=ListaRemision" class="btn btn-default">Regresar</a>
                    <a href="reimpDevolucionFactura.jsp?fol_gnkl=<%=fol_gnkl%>" target="_blank" class="btn btn-success">Impr Devolución</a>
                    <a class="btn btn-success" onclick="window.open('reportes/MarbeteDevo.jsp?fol_gnkl=<%=fol_gnkl%>', '', 'width=1200,height=800,left=50,top=50,toolbar=no')">Marbete Devolución</a></li>
                </div>
                <div>
                   <form action="DevolucionGlobal" method="post">                       
                       <input class="hidden" name="fol_gnkl" value="<%=fol_gnkl%>">
                       <button class="btn btn-block btn-info" name="accion" value="Guardar" onclick="return validaRemision()">Devolución&nbsp;<span class="glyphicon glyphicon-floppy-disk"></span></button>                                        
                       <br />
                       Observaciones:<textarea cols="100" rows="2" required="" name="Obs" id="Obs"></textarea>
                   </form> 
                </div>
                <%
                            }
                        } catch (Exception e) {

                        }
                        con.cierraConexion();
                    } catch (Exception e) {

                    }
                %>
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
                                    <td>Req.</td>
                                    <td>Ubicación</td>
                                    <td>Ent.</td>
                                    <td>Devolución</td>
                                    <td>Costo U</td>
                                    <td>Importe</td>
                                    <td>Modificar</td>
                                    <td>Eliminar</td>
                                    <td></td>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet rset = con.consulta("SELECT F_ClaPro,F_DesPro,F_ClaLot,DATE_FORMAT(F_FecCad,'%d/%m/%Y') AS F_FecCad,F_CantReq, F_Ubicacion,F_CantSur,F_Costo,F_Monto,F_ClaDoc, F_IdFact,F_CantDevo FROM tb_devoglobal WHERE F_ClaDoc='" + fol_gnkl + "';");
                                            while (rset.next()) {
                                            
                                %>
                                <tr>
                                    <td class="Clave"><%=rset.getString(1)%></td>
                                    <td><%=rset.getString(2)%></td>
                                    <td class="Lote"><%=rset.getString(3)%></td>
                                    <td class="Caducidad"><%=rset.getString(4)%></td>
                                    <td><%=rset.getString(5)%></td>
                                    <td><%=rset.getString(6)%></td>
                                    <td class="Cantidad"><%=rset.getString(7)%></td>
                                    <td ><%=rset.getString(12)%></td>
                                    <td><%=rset.getString(8)%></td>
                                    <td><%=rset.getString(9)%></td>
                                    <td>
                                        <a class="btn btn-block btn-warning rowButton" data-toggle="modal" data-target="#Devolucion"><span class="glyphicon glyphicon-pencil"></span></a>
                                    </td>
                                    <td>
                                        <form action="DevolucionGlobal" method="post">
                                        <input class="hidden" name="IdFol" value="<%=rset.getString(11)%>">
                                        <input class="hidden" name="fol_gnkl" value="<%=fol_gnkl%>">
                                        <button class="btn btn-block btn-success" name="accion" value="Eliminar"><span class="glyphicon glyphicon-trash"></span></button>                                        
                                        </form>
                                    </td>
                                    <td class="Identificador"><%=rset.getString(11)%></td>
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
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2019 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>



        <!--
                Modal
        -->
        <div id="Devolucion" class="modal fade" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4>
                            Modificar Devoluci&oacute;n
                        </h4>
                    </div>
                    <form name="Devolucion" action="DevolucionGlobal" method="Post">
                        <div class="modal-body">
                            <div class="row">
                                <h4 class="col-sm-2">Folio</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Folio" id="Folio" type="text" value="<%=fol_gnkl%>" readonly="" required="">
                                </div>
                                <h4 class="col-sm-2">Identificador</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Identi" id="Identi" type="text" value="" readonly="" required="">
                                </div>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-2">Clave</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Clave1" id="Clave1" type="text" value="" readonly="" required="">
                                </div>
                                <h4 class="col-sm-2">Lote</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Lote1" id="Lote1" type="text" value="" readonly="" required="">
                                </div>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-2">Caducidad</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Caducidad1" id="Caducidad1" type="text" value="" readonly="" required="">
                                </div>
                                <h4 class="col-sm-2">Surtida</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Surtida" id="Surtida" type="text" value="" readonly="" required="">
                                </div>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-2">Cantidad a Devolver</h4>
                                <div class="col-sm-4">
                                    <input class="form-control" name="Devolver" id="Devolver" type="text" value="" required="">
                                </div>                                
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-default" name="accion" value="Modificar">Guardar</button>
                            <button type="submit" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        </div>
                    </form>
                </div>
            </div>            
        </div>
        <!--
        /Modal
        -->
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
                                    $(document).ready(function() {
                                        $('#datosCompras').dataTable();
                                    });
</script>
<script>
    $(function() {
        $("#fecha").datepicker();
        $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
    });   
</script>
<script type="text/javascript">
     $(".rowButton").click(function (){
        var $row = $(this).closest("tr");
        var clave = $row.find("td.Clave").text(); 
        var lote = $row.find("td.Lote").text(); 
        var cadu = $row.find("td.Caducidad").text(); 
        var cant = $row.find("td.Cantidad").text(); 
        var idenfi = $row.find("td.Identificador").text(); 
        
        $("#Clave1").val(clave);
        $("#Lote1").val(lote);
        $("#Caducidad1").val(cadu);
        $("#Surtida").val(cant);
        $("#Identi").val(idenfi);
        
    });
    
    function validaRemision() {
            var confirmacion = confirm('Seguro que desea generar la Devolución');
            if (confirmacion === true) {
                //$('#btnGeneraFolio').prop('disabled', true);
                return true;
            } else {
                return false;
            }
        }
        
</script>