<%-- 
    Document   : requerimiento.jsp
    Created on : 17/02/2014, 03:34:46 PM
    Author     : MEDALFA
--%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
     ConectionDB con = new ConectionDB();
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
            
            <% if(tipo.equals("13")){ %>
            
            <%@include file="jspf/menuPrincipalCompra.jspf" %>
            <% }else{ %>
            <%@include file="jspf/menuPrincipal.jspf" %>
            <% }%>
        </div>
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Cargar OC Por Contrato</h3>
                </div>
                <div class="panel-body ">
                    
                    <form method="post" class="jumbotron"  action="FileUploadServletReciboContrato" enctype="multipart/form-data" name="form1">
                        
                        <div class="form-group">
                            <div class="form-group">
                                <div class="row">
                                    <div class="col-lg-3 text-success">
                                        <h4>Seleccione el Excel a Cargar</h4>
                                    </div>
                                    <div class="col-sm-5">
                                        <input class="form-control" type="file" name="file1" id="file1" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/>                                    
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-lg-3 text-success">
                                        <h4>Ingrese No de Contrato</h4>
                                    </div>
                                         
                                    <div class="col-lg-3">
                                        
                                          <input  type="text" name="IdContrato" id="IdContrato"  maxlength="20" max="20" minlength="2" min="2" required=""/>
                                    </div>

                                </div>
                            </div>

                        </div>
                        
                        <button class="btn btn-block btn-success" type="submit"  onclick="return valida_alta();"> Cargar Archivo</button>
                    </form>
                    <div style="display: none;" class="text-center" id="Loader">
                        <img src="imagenes/ajax-loader-1.gif" height="150" alt="gif" />
                    </div>
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                <div class="table-responsive">
                     <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv">
                        <thead>
                            <tr>
                                <th>NoOC</th>
                                <th>Proveedor</th>
                                <th>Clave</th>
                                <th>Cantidad</th>
                                <th>OcError</th>
                                <th>ProveError</th>
                                <th>ClaveError</th>
                                <th>CantidadError</th>
                                <th>ProyectoError</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    int var1 = 0;
                                    String Oc = "";
                                    con.conectar();
                                    ResultSet rset = con.consulta("SELECT F_NoOc, F_Proveedor,F_Clave,F_Cant,CASE WHEN F_ProblemaReq  = 0 THEN 'Error en OC'  ELSE 'Correcto'  END as OCError,CASE WHEN F_ProblemaProvee  = 0 THEN 'Error en Proveedor'  ELSE 'Correcto'  END as ProveError, CASE WHEN F_ProblemaClave  = 0 THEN 'Error en Clave'  ELSE 'Correcto'  END  as ClaveError, CASE WHEN F_ProblemaCantidad  = 0 THEN 'Error en Cantidad'  ELSE 'Correcto'  END as CantidadError,CASE WHEN F_ProblemaProyecto  = 0 THEN 'Error en Proyecto'  ELSE 'Correcto'  END as ProyectoError FROM tb_cargaoc O WHERE F_Usu = '" + usua + "' AND (O.F_ProblemaClave = 0 OR  O.F_ProblemaProvee = 0 OR O.F_ProblemaProyecto = 0 OR O.F_ProblemaReq = 0 OR O.F_ProblemaCantidad = 0);");
                                    while (rset.next()) {
                                        if (!rset.getString(1).equals("Error") && !rset.getString(2).equals("Error") && !rset.getString(3).equals("")) {
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
                                <td><small><%=rset.getString(9)%></small></td> 
                            </tr>
                            <%
                                        }
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    out.print(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
                
                    <!--    <div class="table table-responsive">
                            <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv">
                                <thead>
                                <th>NoOc</th>
                                <th>Proveedor</th>
                                <th>Clave</th>
                                <th>Cantidad</th>
                                
                                </thead>
                                <tbody>
                                       <%
                        /*        try {
                                  
                                    con.conectar();
                                    ResultSet rsetDD = con.consulta("SELECT F_NoOc, F_Proveedor,F_Clave,F_Cant FROM tb_cargaoc O WHERE F_Usu = '" + usua + "' AND O.F_ProblemaClave = 1 AND  O.F_ProblemaProvee = 1 AND O.F_ProblemaProyecto = 1 AND O.F_ProblemaReq = 1 AND O.F_ProblemaCantidad = 1 ");
                                    while (rsetDD.next()) {
                           */
                            %>
                                <td>< %=rsetDD.getString(1)%></td>
                                <td>< %=rsetDD.getString(2)%></td>
                                <td>< %=rsetDD.getString(3)%></td>
                                <td>< %=rsetDD.getString(4)%></td>
                                   <% /* } 
                                        con.cierraConexion();
                                } catch (Exception e) {
                                    out.print(e.getMessage());
                                }*/
                            %>
                                </tbody>
                            </table>
                            
                            
                        </div>        
                     -->
            </div>
        </div>
        <br><br><br>
       
        <%@include file="jspf/piePagina.jspf" %>
        <script src="js/jquery-2.1.4.min.js" type="text/javascript"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        
        <script>
                            function valida_alta() {
                                var Nombre = document.getElementById('file1').value;
                                 var Contrato = document.getElementById('IdContrato').value;
                                if (Nombre === "") {
                                    alert("Seleccione un archivo por favor");
                                    return false;
                                }
                                 if (Contrato === "" && Contrato === '') {
                                    alert("Ingresar contrato");
                                    return false;
                                }
                         
                                document.getElementById('Loader').style.display = 'block';
                            }
                            
                            function validaContrato() {
                                var valorContrato = document.getElementById('IdContratomodal').value;
                                
                                if (valorContrato === "") {
                                        alert('Ingrese un numero de contrato');
                                        return false;
                                   
                                    document.getElementById('Loader').style.display = 'block';
                                }
                            }
                            
                            

        </script>
    </body>
</html>
