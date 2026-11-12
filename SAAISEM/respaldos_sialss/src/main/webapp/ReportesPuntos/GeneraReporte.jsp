<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
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
    String tipo = "";
    String fecha_ini="", fecha_fin="",radio="",F_FolCon="",folio1="",folio2="";
    int F_Idsur=0,F_IdePro=0,F_Cvesum=0,F_Punto=0,F_Con=0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    
    try {
        fecha_ini = request.getParameter("fecha_ini");        
        fecha_fin = request.getParameter("fecha_fin");
        folio1 = request.getParameter("folio1");        
        folio2 = request.getParameter("folio2");
        radio = request.getParameter("radio");        
    } catch (Exception e) {

    }
    if(fecha_ini==null){
        fecha_ini="";
    }
    if(fecha_fin==null){
        fecha_fin="";
    }
    if(folio1 == null){
        folio1="";
    }
    if(folio2 == null){
        folio2 = "";
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
                    <h3 class="panel-title">Generar Reportes Automáticos</h3>
                </div>
                
                <form action="GeneraReporte.jsp" method="post">
                        <div class="panel-footer">
                            <div class="row">                                         
                                <label class="control-label col-lg-2" for="fecha_ini"><input type="radio" id="radio" name="radio" value="si" onchange="habilitar(this.value);" checked >Por Folio</label>
                                <div class="col-lg-2">
                                    <input class="form-control" id="folio1" name="folio1" type="text" value="<%=folio1%>" />
                                </div>
                                <div class="col-lg-2">
                                    <input class="form-control" id="folio2" name="folio2" type="text" value="<%=folio2%>" />
                                </div>
                            </div>
                            <br>
                            <div class="row">                                    
                                <label class="control-label col-lg-2" for="fecha_ini"><input type="radio" id="radio" name="radio" value="no" onchange="habilitar(this.value);" >Por Fecha</label>
                                
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" />
                                </div>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_fin" name="fecha_fin" type="date" />
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
                    <%
                        int Contar=0;
                        try {
                            con.conectar();
                            try {
                                String FechaFol="";
                                    if(radio.equals("si")){
                                        FechaFol = " F.F_ClaDoc between '"+folio1+"' and '"+folio2+"' ";
                                    }else{
                                        FechaFol = " F.F_FecEnt between '"+fecha_ini+"' and '"+fecha_fin+"' ";
                                    }
                                    
                                   ResultSet rset = con.consulta("SELECT F.F_ClaDoc,F.F_ClaCli,U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura F INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli where "+FechaFol+" GROUP BY F.F_ClaDoc ORDER BY F.F_ClaDoc+0");                                                                                                                       
                                    
                                    while (rset.next()) {
                                    Contar = rset.getInt(1);
                                    }
                                } catch (Exception e) {

                                }
                                con.cierraConexion();
                            } catch (Exception e) {

                        }

                        %>            
                    <div class="row">
                        <%
                        if(Contar > 0){
                        %>
                        <form class="form-horizontal" role="form" name="formulario_receta" id="formulario_receta" method="get" action="../ReporteImprimeAuto">   
                            <input class="form-control" id="radio1" name="radio1" type="hidden" value="<%=radio%>" />
                            <input class="form-control" id="foio11" name="folio11" type="hidden" value="<%=folio1%>" />
                            <input class="form-control" id="folio21" name="folio21" type="hidden" value="<%=folio2%>" />
                            <input class="form-control" id="fecha_ini1" name="fecha_ini1" type="hidden" value="<%=fecha_ini%>" />
                            <input class="form-control" id="fecha_fin1" name="fecha_fin1" type="hidden" value="<%=fecha_fin%>" />
                            
                            <label class="control-label col-sm-2" for="imprera">Seleccione Impresora</label>
                            <div class="col-sm-2 col-sm-2">                       
                                <select id="impresora" name="impresora">
                                    <option value="">--Seleccione Impresora--</option>
                                    <%
                                    String Nom = "";
                                    PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
                                    for (PrintService printService : impresoras) {
                                        Nom = printService.getName();
                                        //System.out.println("impresora" + Nom);                            
                                    %>
                                    <option value="<%=Nom%>"><%=Nom%></option>                            
                                    <%}%>
                                </select>                        
                            </div>
                            <div class="col-sm-4 col-sm-offset-2">                       
                            <button class="btn btn-block btn-success" id="btn_capturar" name="btn_capturar" value="<%//=rset.getString(1)%>" onclick="return confirm('¿Esta Ud. Seguro de Iniciar proceso de Generación?')">Generar</button>
                            </div>
                        </form>
                     <%}%>
                    </div>
                <div class="panel-footer">
                    <div style="width:100%; height:400px; overflow:auto;">
                            <!--table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosProv"-->
                            <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <td>No. Folio</td>
                                        <td>Clave Cliente</td>
                                        <td>Nombre Cliente</td>
                                        <td>Fecha de Entrega</td>                                    
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                try {
                                    ResultSet rset = null;                                    
                                    con.conectar();
                                    String FechaFol="";
                                    if(radio.equals("si")){
                                        FechaFol = " F.F_ClaDoc between '"+folio1+"' and '"+folio2+"' ";
                                    }else{
                                        FechaFol = " F.F_FecEnt between '"+fecha_ini+"' and '"+fecha_fin+"' ";
                                    }
                                    
                                        rset = con.consulta("SELECT F.F_ClaDoc,F.F_ClaCli,U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura F INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli where "+FechaFol+" GROUP BY F.F_ClaDoc ORDER BY F.F_ClaDoc+0");                                                                                                                       
                                    
                                    while (rset.next()) {
                                        
                            %>
 
                         


                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>                                
                                <td><%=rset.getString(3)%></td>                        
                                <td><%=rset.getString(4)%></td>
                                                                 
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
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2019 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>
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
<script>
    function habilitar(value){
        
        if(value=="si"){
            document.getElementById("fecha_ini").disabled=true;
            document.getElementById("fecha_fin").disabled=true;
            document.getElementById("folio1").disabled=false;
            document.getElementById("folio2").disabled=false;

        }else if(value=="no"){
            document.getElementById("folio1").disabled=true;
            document.getElementById("folio2").disabled=true;
            document.getElementById("folio1").value="";
            document.getElementById("folio2").value="";
            document.getElementById("fecha_ini").disabled=false;
            document.getElementById("fecha_fin").disabled=false;
             
        }
    }
    </script>
    <script>
    $(document).ready(function() {
        $('#datosProv').dataTable();
    });
</script>

