<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="in.co.sneh.model.Proveedor"%>
<%@page import="conn.ConectionDB"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="com.gnk.util.ParameterUtils"%>
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
    String fechaInicial = "";
    String fechaFinal = "";
    String Proveedor = "";
    
    String andString = "c.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) ";
    String todo = "";
    fechaInicial = ParameterUtils.getParameter("fechaInicial", request);
    fechaFinal = ParameterUtils.getParameter("fechaFinal", request);

    Proveedor = ParameterUtils.getParameter("proveedor", request);
    todo = ParameterUtils.getParameter("todo", request);

    String clave = ParameterUtils.getParameter("clave", request);
    String lote = ParameterUtils.getParameter("lote", request);
    String remision = ParameterUtils.getParameter("remision", request);
    String oc = ParameterUtils.getParameter("oc", request);
    String getString="proveedor="+Proveedor+"&fechaInicial="+fechaInicial+"&fechaFinal="+fechaFinal+"&clave="+clave+"&lote="+lote+"&oc="+oc+"&remision="+remision;
    
    if (!Proveedor.isEmpty()) {
        andString = andString.isEmpty() ? "p.F_ClaProve='" + Proveedor + "' " : andString + "AND p.F_ClaProve = '" + Proveedor + "' ";
        
       
    }
    if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
        andString = andString.isEmpty() ? "c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "'" : andString + "AND c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "' ";
    }
    
    if (!oc.isEmpty()) {
        andString = andString.isEmpty() ? "c.F_OrdCom = '" + oc + "' " : andString + "AND c.F_OrdCom = '" + oc + "' ";
    }
    if (!remision.isEmpty()) {
        andString = andString.isEmpty() ? "c.F_FolRemi = '" + remision + "' " : andString + "AND c.F_FolRemi = '" + remision + "' ";
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <link href="../css/select2.css" rel="stylesheet">
        <link href="../css/TablasScroll/ScrollTables.css" rel="stylesheet" type="text/css"/>
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="../jspf/menuPrincipalCompra.jspf" %>
            <div>
                <h3>Reporte por Fecha Proveedor</h3>
                <div class="row">
                    <form action="ReporteFCompra.jsp" method="post" class="form-inline col-md-12">
                        <div class="row">
                            <h4 class="col-sm-1">Proveedor</h4>
                            <div class="col-sm-10">
                                <select class="form-control" name="proveedor" id="proveedor">
                                    <option value="">--Proveedor--</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT C.F_ProVee,P.F_NomPro FROM tb_compra C INNER JOIN tb_proveedor P ON C.F_ProVee=P.F_ClaProve GROUP BY C.F_ProVee;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    %>

                                </select>
                            </div>
                        </div>
                                    <br/>
                        <div class="row">
                            <div class="col-md-5">
                                <div class="input-group">
                                    <span class="input-group-addon">Fecha Inicial</span>
                                    <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="fechaInicial" name="fechaInicial" />
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="input-group">
                                    <span class="input-group-addon">Fecha Final</span>
                                    <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="fechaFinal" name="fechaFinal" />
                                </div>

                            </div>
                        </div>
                        <br/>
                        <!--div class="row">
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">Clave</span>
                                    <input type="text" class="form-control" id="clave" name="clave" />
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="input-group">
                                    <span class="input-group-addon">Lote</span>
                                    <input type="text" class="form-control" id="lote" name="lote" />
                                </div>

                            </div-->
                            <div class="col-md-5">
                                <div class="input-group">
                                    <span class="input-group-addon">Remisión</span>
                                    <input type="text" class="form-control" id="remision" name="remision" />
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="input-group">
                                    <span class="input-group-addon">OC</span>
                                    <input id="oc" type="text" class="form-control" name="oc" >
                                </div>
                            </div>
                        </div>
                        <br/>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-md-10" >
                                    <div class="col-sm-2">
                                        <button type="submit" class="btn btn-block btn-success">Buscar &nbsp;<label class="glyphicon glyphicon-search"/></button>
                                    </div>
                                <!--button type="submit" class="btn btn-success" name="todo" value="todo">Todo</button-->
                                <div class="col-sm-2">
                                    <a class="btn btn-block btn-success" href="ReporteF_gnrCompraFiltro.jsp?proveedor=<%=Proveedor%>&fechaInicial=<%=fechaInicial%>&fechaFinal=<%=fechaFinal%>&oc=<%=oc%>&remision=<%=remision%>">Descargar&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                                </div>
                                </div>
                                <div class="col-md-2">
                                <a class="btn btn-success" href="ReporteF_gnrCompra.jsp?<%=andString%>">Descargar Todo&nbsp;<label class="glyphicon glyphicon-download-alt"></label></a>
                                </div>                                
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <br />
        <div class="container" style="overflow-x: scroll; overflow-y: scroll" >
            <!--div class="panel panel-success"-->
            <div class="table-responsive-md">
                    <table class="table table-striped table-bordered table-md" cellspacing="0" width="100%" id="datosCompras">
                        <thead>
                            <tr>                                
                               <!-- <th class="text-center">Proyecto</th>-->
                                <th class="text-center">Clave</th>
                                <th class="text-center">Nombre Generico</th><!-- comment -->
                                <th class="text-center">Descripcion Esp.</th>
                                <th class="text-center">Presentación</th>
                                 
                                <th class="text-center">Proveedor</th>
                                <th class="text-center">Marca</th>
                                <th class="text-center">Lote</th>
                                <th class="text-center">Caducidad</th>
                                <th class="text-center">Piezas</th>
                                <th class="text-center">Costo U</th>
                                <th class="text-center">Monto</th>
                                <th class="text-center">Fecha</th>
                                <th class="text-center">OC</th> 
                                
                                <th class="text-center">Fuente de Financiamiento</th> 
                                
                                <th class="text-center">Folio Medalfa</th> 
                                <th class="text-center">Remisión</th>
                                <th class="text-center">Usuario</th>
                                <th class="text-center">Ord. Sum.</th>
                                
                                <th class="text-center">Forma Farmaceutica</th>
                                <th class="text-center">Concentracion</th>
                                <th class="text-center">Contrato</th>
                                
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                        con.conectar();
                                        ResultSet rset = null;
                                        boolean flag = false;
                                        System.out.println(andString + "query");
                                        if (!(Proveedor.equals(""))) {
                                            if (!(fechaInicial.equals("")) && !(fechaFinal.equals(""))) { 
                                            System.out.println("query 1");
                                                flag = true;
                                                String query = "SELECT p.F_NomPro, md.F_ClaProSS, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, m.F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro,IFNULL(md.F_NomGen,'') AS F_NomGen,md.F_FormaFarm,md.F_Concentracion,IFNULL(md.F_DesProEsp,md.F_DesPro) AS F_DesProEsp, c.F_FuenteFinanza, md.F_PrePro, IFNULL(ped.F_Contratos, ' - ') AS F_Contratos  FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m on l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica md ON c.F_ClaPro = md.F_ClaPro LEFT JOIN (SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro where c.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) AND p.F_ClaProve='" + Proveedor + "' AND c.F_FecApl BETWEEN '" + fechaInicial + "' AND '" + fechaFinal + "'  GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                                                rset = con.consulta(query);
                                                System.out.println(query);

                                            } else {                  
                                            System.out.println("query 2");
                                                flag = true;
                                                String query = "SELECT p.F_NomPro, md.F_ClaProSS, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, m.F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro,IFNULL(md.F_NomGen,'') AS F_NomGen,md.F_FormaFarm,md.F_Concentracion,IFNULL(md.F_DesProEsp,md.F_DesPro) AS F_DesProEsp, c.F_FuenteFinanza, md.F_PrePro, IFNULL(ped.F_Contratos, ' - ') AS F_Contratos  FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m on l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica md ON c.F_ClaPro = md.F_ClaPro LEFT JOIN (SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro where " + andString + "  GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;"; 
                                                rset = con.consulta(query);
                                                System.out.println(query);
                                            }
                                        } else {
                                            if (!(andString.equals("c.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_claves_excluidas) "))) {                                           
                                            flag = true;
                                            System.out.println("query 3");
                                            String query = "SELECT p.F_NomPro, md.F_ClaProSS, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, c.F_Costo, (SUM(F_CanCom) * c.F_Costo) AS monto, m.F_DesMar, IFNULL(c.F_OrdenSuministro,'') as F_OrdenSuministro,IFNULL(md.F_NomGen,'') AS F_NomGen,md.F_FormaFarm,md.F_Concentracion,IFNULL(md.F_DesProEsp,md.F_DesPro) AS F_DesProEsp, c.F_FuenteFinanza, md.F_PrePro, IFNULL(ped.F_Contratos, ' - ') AS F_Contratos  FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m on l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica md ON c.F_ClaPro = md.F_ClaPro LEFT JOIN (SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro  where " + andString + "  GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                                            rset = con.consulta(query);
                                            System.out.println(query);
                                            }
                                            }
                                        
                                        
                                        if (flag)
                                            while (rset.next()) {
                                            

                            %>
                            <tr>
                                <!--td>< %=rset.getString("F_DesProy")%></td-->
                               
                                <td class="text-center"><%=rset.getString("F_ClaProSS")%></td>
                                <td class="text-center"><%=rset.getString("F_NomGen")%></td><!-- comment -->
                                <td ><%=rset.getString("F_DesProEsp")%></td>
                                <td class="text-center"><%=rset.getString("F_PrePro")%></td>
                                <td><%=rset.getString("F_NomPro")%></td>
                                <td class="text-center"><%=rset.getString("F_DesMar")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaLot")%></td>
                                <td class="text-center"><%=rset.getString("fecCad")%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt("cantidad"))%></td>
                                <td class="text-center"><%=formatterD.format(rset.getDouble("F_Costo"))%></td>
                                <td class="text-center"><%=formatterD.format(rset.getDouble("monto"))%></td>
                                <td class="text-center"><%=rset.getString("fecapl")%></td>
                                <td class="text-center"><%=rset.getString("F_OrdCom")%></td>
                                
                                 <td class="text-center"><%=rset.getString("F_FuenteFinanza")%></td>
                                
                                <td class="text-center"><%=rset.getString("F_ClaDoc")%></td>
                                <td class="text-center"><%=rset.getString("F_FolRemi")%></td>
                                <td class="text-center"><%=rset.getString("F_User")%></td>
                               
                                <td class="text-center"><%=rset.getString("F_OrdenSuministro")%></td>
                               
                                <td class="text-center"><%=rset.getString("F_FormaFarm")%></td>
                                <td class="text-center"><%=rset.getString("F_Concentracion")%></td>
                                <td class="text-center"><%=rset.getString("F_Contratos")%></td>
                            
                            </tr>
                            <%
                                    }
                                    con.cierraConexion();

                                } catch (Exception e) {
                                    con.cierraConexion();
                                    System.out.println(e.getMessage());
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        <!--/div-->
        <br><br><br>
    </body>
</html>
<%@include file="../jspf/piePagina.jspf" %>

<script src="../js/jquery-2.1.4.min.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/bootstrap-datepicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script src="../js/select2.js"></script>
<script>
    $(document).ready(function () {
        $('#datosCompras').dataTable({
    "scrollX": true,
    "scrollY": true,
    "scrollCollapse": true
  });
  $('.dataTables_length').addClass('bs-select');
});
        $("#Fecha").datepicker();
        $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
        $("#Proveedor").select2();
   
</script>