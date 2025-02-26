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
String folLot = "", unidad = "", clave = "";
    
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
        
        
        
        
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="jspf/menuPrincipal.jspf" %>
            <div>
                <h3>Asignación de Unidades FONSABI</h3>
                <br>
                <div class="row">
                    
                    <div class="container">
                        
                            <div class="panel panel-success">
                                <div class="panel-body">
                                    <table  class="table table-striped table-bordered" id="tablaFonsabi">
                                        <thead>
                                            <tr>
                                                <td class="hidden"width:150px"></td>
                                                <td class="text-center"style="width:150px">Clave</td>
                                                <td class="text-center"style="width:150px">Lote</td>
                                                <td class="text-center"style="width:150px">Caducidad</td>
                                                <td class="text-center"style="width:150px">Existencia</td>
                                                <td class="text-center"style="width:150px">Ubicación</td>
                                                <td class="text-center"style="width:150px">Remisión</td>
                                                <td class="text-center"style="width:150px">O.C.</td>
                                                <td class="text-center"style="width:150px">Orden suministro</td>
                                                <td class="text-center">IdLote</td>
                                                <td class="text-center" style="width:200px">Unidad destino</td>
                                                <td class="text-center" style="width:100px"  ><inpunt type="button">Asignar unidad</td>
                                            </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    try {
                                                        con.conectar();
                                                        ResultSet rset = null;                                                       
                                                            rset = con.consulta("SELECT L.F_IdLote, L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_ExiLot, L.F_FolLot, L.F_Ubica, IFNULL(U.F_NomCli, '') AS F_NomCli, U.F_ClaCli, C.F_FolRemi, C.F_OrdCom, C.F_OrdenSuministro FROM tb_lote AS L LEFT JOIN tb_unidadfonsabi AS UF ON UF.F_FolLot = L.F_FolLot LEFT JOIN tb_uniatn AS U ON UF.F_ClaCli = U.F_ClaCli INNER JOIN tb_compra AS C ON C.F_Lote = L.F_FolLot WHERE L.F_Origen = '19' AND L.F_ExiLot > '0';");
                                                        
                                                        while (rset.next()) {
                                                            clave = rset.getString("F_ClaPro");
                                                %>
                                                <tr class="odd gradeX">
                                                    <td class="folLot hidden"><%=rset.getString("F_FolLot")%></td>
                                                    <td class="clave"><%=rset.getString("F_ClaPro")%></td>
                                                    <td class="lote"><%=rset.getString("F_ClaLot")%></td>
                                                    <td class="caducidad"><%=rset.getString("F_FecCad")%></td>
                                                    <td class="existencia"><%=rset.getString("F_ExiLot")%></td>
                                                    <td class="ubicacion"><%=rset.getString("F_Ubica")%></td>
                                                    <td class="remision"><%=rset.getString("F_FolRemi")%></td>
                                                    <td class="ordCom"><%=rset.getString("F_OrdCom")%></td>
                                                    <td class="ordSum"><%=rset.getString("F_OrdenSuministro")%></td>
                                                    <td class="idlote"><%=rset.getString("F_IdLote")%></td>
                                                    <td class="unidadN"><%=rset.getString("F_NomCli")%></td>
                                                    <td>
                                                        <a type="button" class="btn btn-block btn-info rowButton"" data-toggle="modal" data-target="#myModal">Asignar</a>
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
                    
                </div>
                <br><br><br>
                <form name="form1" action="AsignarUnidadFonsabi" method="get" onsubmit="return validarForma(this)">
                    <div class="modal fade" id="myModal" role="dialog">
                        <div class="modal-dialog">

                            <!-- Modal content-->

                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                    <h4 class="modal-title">Agregar unidad destino</h4>
                                </div>
                                <div class="modal-body" >
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="claveMod">Clave:</label>
                                        </div>
                                        <div class="col-sm-5">
                                            <input type="text" class="form-control" readonly id="claveMod" name="claveMod"/>
                                        </div>
                                    </div>
                                
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="loteMod">Lote:</label>
                                        </div>
                                        <div class="col-sm-5">
                                            <input type="text" class="form-control" readonly id="loteMod" name="loteMod"/>
                                        </div>
                                    </div>
                                
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="caducidadMod">Caducidad:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="caducidadMod" name="caducidadMod"/>
                                        </div>
                                    </div>
                                
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="existenciaMod">Existencia:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="existenciaMod" name="existenciaMod"/>
                                        </div>
                                    </div>
                                
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="ubicacionMod">Ubicación:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="ubicacionMod" name="ubicacionMod"/>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="remisionMod">Remisión:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="remisionMod" name="remisionMod"/>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="ordMod">Ord. compra:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="ordComMod" name="ordComMod"/>
                                        </div>
                                    </div>
                                    
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="ordSumMod">Ord. Suministro:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <input type="text" class="form-control" disabled id="ordSumMod" name="ordSumMod"/>
                                        </div>
                                    </div>                       
                                
                                    <div class="col-sm-12 form-group">
                                        <div class="col-sm-2">
                                        <label for="unidadMod">unidad:</label>
                                        </div>
                                        <div class="col-sm-5">
                                        <select class="form-control" name="unidadMod" id="unidadMod">
                                            <option value="">--Seleccionar Clave--</option>
                                            <%
                                                try {
                                                    con.conectar();
                                                    ResultSet rset = con.consulta("SELECT U.F_ClaCli, U.F_NomCli FROM tb_uniatn AS U WHERE U.F_ClaCli LIKE '%AI%' AND U.F_StsCli = 'A';");
                                                    while (rset.next()) {
                                            %>
                                            <option value="<%=rset.getString("F_ClaCli")%>"><%=rset.getString("F_ClaCli")%> - <%=rset.getString("F_NomCli")%></option>
                                            <% }
                                                    con.cierraConexion();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            %>
                                        </select>
                                        </div>
                                    </div>
                                    <br><br>
                                    
                                </div>
                                <br>
                                <br>
                                <br>                                
                                <input class="hidden"  id="unidadNMod" name="unidadNMod" type="text"/>
                                <input class="hidden"  id="folLotMod" name="folLotMod" type="text"/>
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
<script type="text/javascript" src="js/reporteFonsabi.js"></script>
  