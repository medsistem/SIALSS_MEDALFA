<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<!DOCTYPE html>
<%
    ConectionDB con = new ConectionDB();
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    String nombre = "";
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
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="jspf/menuPrincipalAuditoria.jspf" %>
        </div>
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h2 class="panel-title col-sm-10">Cargar información</h2>                
                        <button class="btn btn-primary"  id="plantilla"> Plantilla excel </button>
                    </div>
                <div class="panel-body ">
                    <form method="post" class="jumbotron"  action="FileUploadServletAuditoria" enctype="multipart/form-data" name="form1">
                        <div class="form-group">
                            <div class="form-group">
                                <div class="col-lg-4 text-success">
                                    <h4>Seleccione el excel a cargar</h4>
                                </div>
                                <label for="file1" class="col-xs-2 control-label">Nombre archivo*</label>
                                <div class="col-sm-5">
                                    <input class="form-control" type="file" name="file1" id="file1" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/>                                    
                                </div>
                            </div>
                        </div>
                        <button class="btn btn-block btn-success" type="submit" name="accion" value="guardar" onclick="return valida_alta();"> Cargar archivo</button>
                    </form>
                    <div style="display: none;" class="text-center" id="Loader">
                        <img src="imagenes/ajax-loader-1.gif" height="150" alt="gif" />
                    </div>
                    
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                <div class="panel-footer">
                    <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv">
                        
                        <!-- Muestra el reporte de incidencias en el excel-->
                        <thead>
                        <h5> Los campos marcados con "0" son los que contienen incidencias </h5>

                        <tr>
                            <th>Clave </th>
                            <th>Lote</th>
                            <th>Error clave</th>
                            <th>Error lote</th>
                            <th>Error cantidad</th>
                            <th>Error origen </th>
                            <th>Error estatus</th>
                            <th>Error caducidad</th>
                        </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = con.consulta("SELECT clave, lote, validaclave, validalote, validacantidad, validaorigen, validaestatus, validacaducidad FROM tb_reporteauditoriatemp WHERE F_Usu = '" + usua + "' AND (validaclave = 0 OR validaorigen = 0 OR validaestatus = 0 OR validalote = 0 OR validacantidad = 0 OR validacaducidad = 0);");
                                    while (rset.next()) {
                            %>
                            <tr class="odd gradeX">
                                <td><small><%=rset.getString(1)%></small></td>
                                <td><small><%=rset.getString(2)%></small></td>
                                <td><small><%=rset.getString(3)%></small></td>
                                <td><small><%=rset.getString(4)%></small></td>
                                <td><small><%=rset.getString(5)%></small></td> 
                                <td><small><%=rset.getString(6)%></small></td> 
                                <td><small><%=rset.getString(7)%></small></td>                            
                                <td><small><%=rset.getString(8)%></small></td>                            

                            </tr>
                            <%
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    out.print(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <br><br><br>
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2022 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>
        <script src="js/jquery-2.1.4.min.js" type="text/javascript"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script>
                            function valida_alta() {
                                var Nombre = document.getElementById('file1').value;
                                if (Nombre === "") {
                                    alert("Seleccione un archivo por favor");
                                    return false;
                                }
                                document.getElementById('Loader').style.display = 'block';
                            }

                            
                                    $('#plantilla').click(function () {
                            
                                    window.open("ExcelPlantillaAuditoria?Tipo=1");
                            });
        </script>
    </body>
</html>
