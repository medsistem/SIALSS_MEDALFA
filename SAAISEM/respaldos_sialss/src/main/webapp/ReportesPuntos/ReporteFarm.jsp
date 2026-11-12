<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    String fecha_ini="", fecha_fin="",radio="",radio2="",F_FolCon="",NombreUnidad="";
    int F_Idsur=0,F_IdePro=0,F_Cvesum=0,F_Punto=0,F_Con=0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    
    try {
        fecha_ini = request.getParameter("fecha_ini");        
        fecha_fin = request.getParameter("fecha_fin");    
        NombreUnidad = request.getParameter("NombreUnidad");
    } catch (Exception e) {

    }
    if(fecha_ini==null){
        fecha_ini="";
    }
    if(fecha_fin==null){
        fecha_fin="";
    }
    if(NombreUnidad==null){
        NombreUnidad="";
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
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipal.jspf" %>
        </div>
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Reportes de Farmacias</h3>
                </div>
                
                <form action="ReporteFarm.jsp" method="post">
                        <div class="panel-footer">
                            <div class="row">
                                <label class="control-label col-sm-1" for="fecha_ini">Unidad</label>
                                <div class="col-lg-4">
                                    <input type="text" class="form-control"  name="NombreUnidad" id="NombreUnidad" value="" onkeyup="descripUni()" required="true" />                                    
                                </div>
                                <label class="control-label col-sm-1" for="fecha_ini">Fecha Inicio</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" required="true" />
                                </div>
                                <label class="control-label col-sm-1" for="fecha_fin">Fecha Fin</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_fin" name="fecha_fin" type="date" required="true" />
                                </div>                                
                            </div>                            
                        </div>     
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-sm-10 text-center">
                                    <button class="btn btn-block btn-success" id="btn_capturar">MOSTRAR&nbsp;<label class="glyphicon glyphicon-search"></label></button>
                                </div>
                               
                            </div>
                        </div>                        
                    </form>
                
                <br />
                
                <div class="panel-footer">
                    <div style="width:100%; height:400px; overflow:auto;">
                            <!--table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv"-->
                            <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <td>Factura AG</td>
                                        <td>Cliente</td> 
                                        <td>Uni.Atn.</td>                                
                                        <td>Fec/Surt</td>
                                        <td>Importe</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                try {
                                    ResultSet rset = null;
                                    
                                    con.conectar();
                                    
                                        rset = con.consulta("SELECT F_FacGNKLAgr,U.F_ClaInt1,U.F_DesUniIS,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,FORMAT(SUM(F_Cansur*F_CosUni),2) AS F_MONTO "
                                                            + "FROM tb_txtis T INNER JOIN tb_unidis U ON T.F_Cveuni=U.F_ClaUniIS "
                                                            + "WHERE F_FacGNKLAgr LIKE '%AG-F%' AND F_DesUniIS = '"+NombreUnidad+"' AND F_Fecsur BETWEEN '"+fecha_ini+"' AND '"+fecha_fin+"' "
                                                            + "GROUP BY F_Fecsur");                                   
                                    
                                   
                                    while (rset.next()) {                                        
                            %>
 
                         


                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>                                
                                <td><%=rset.getString(3)%></td>                                  
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                                                 
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
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script type="text/javascript">
          $(function() {
               var availableTags = [
          <%
            try {
                con.conectar();
                try {
                    ResultSet rset = con.consulta("SELECT F_DesUniIS FROM tb_unidis WHERE F_ClaInt1 IN('7094A','2200A','4100A','4101A','4102A','8200A','8201A','4103A') ORDER BY F_DesUniIS ASC");
                    while (rset.next()) {
                        out.println("'" + rset.getString(1) + "',");
                    }
                } catch (Exception e) {

                }
                con.cierraConexion();
            } catch (Exception e) {

            }
        %>
               ];
               $("#NombreUnidad").autocomplete({
                   source: availableTags
               });
          });
        </script>
    <script>
    $(document).ready(function() {
        $('#datosProv').dataTable();
    });
     function valida_alta() {
                var Nombre = document.getElementById('file1').value;

                if (Nombre === "") {
                    alert("Tiene campos vacíos, verifique.");
                    return false;
                }         
                document.getElementById('Loader').style.display = 'block';
            }
</script>

