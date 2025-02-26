<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

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
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();
    String fechaCap = "";
    String Proveedor = "";
    try {
        fechaCap = df2.format(df3.parse(request.getParameter("Fecha")));
    } catch (Exception e) {

    }
    if (fechaCap == null) {
        fechaCap = "";
    }
    try {
        Proveedor = request.getParameter("Proveedor");
    } catch (Exception e) {

    }
    if (Proveedor == null) {
        Proveedor = "";
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
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>           
            <%@include file="../jspf/menuPrincipal.jspf" %>
            <div>
                <h3>Reporte por Fecha Proveedor</h3>
                <div class="row">
                    <form action="ReporteF.jsp" method="post">
                        <h4 class="col-sm-1">Proveedor</h4>
                        <div class="col-sm-5">
                            <select class="form-control" name="Proveedor" id="Proveedor" onchange="this.form.submit();">
                                <option value="">--Proveedor--</option>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rset = con.consulta("SELECT p.F_ClaProve, p.F_NomPro FROM tb_compra c, tb_proveedor p WHERE c.F_Provee = p.F_ClaProve GROUP BY c.F_Provee order by F_NomPro;");
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
                        <h4 class="col-sm-2">Fecha de Recibo</h4>
                        <div class="col-sm-2">
                            <input type="text" class="form-control" data-date-format="dd/mm/yyyy" id="Fecha" name="Fecha"  onchange="this.form.submit();" />
                        </div>
                        <a class="hidden" href="ReporteF.jsp">Todo</a>
                        <a class="btn btn-success" href="ReporteF_gnr.jsp?provee=<%=Proveedor%>&fecha=<%=fechaCap%>"> Descargar&nbsp; <label class="glyphicon glyphicon-download-alt"></label></a>
                    </form>
                </div>
            </div>
        </div>
        <br/>
        <div class="container">
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" style="width:100%" id="DatosCompras">
                        <thead>
                            <tr>                                
                               <!-- <td class="text-center">Proyecto</td> -->
                                <th class="text-center">Proveedor</th>
                                <th class="text-center">Clave</th>
                                <th class="text-center">Lote</th>
                                <th class="text-center">Caducidad</th>
                                <th class="text-center">Piezas</th>
                                <th class="text-center">OC</th> 
                                <th class="text-center">Folio Medalfa</th> 
                                <th class="text-center">Remisión</th> 
                                <th class="text-center">Usuario</th>
                                <th class="text-center">Fecha-Hora de Captura</th>
                                <th class="text-center">Usuario Ingreso</th>
                                <th class="text-center">Fecha-Hora de Ingreso</th> 
                                <th class="text-center">O.S.</th> 
                                <th class="text-center">Fuente Financiamiento</th> 
                                <!--<td class="text-center">Hora de Validación</td>-->
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    boolean flag = false;
                                    //  if ((Proveedor.equals("")) && (fechaCap.equals(""))) {

                                    //       rset = con.consulta("SELECT c.F_HoraCaptura, c.F_Hora, c.F_UserIngreso, DATE_FORMAT(c.F_FecCaptura, '%d/%m/%Y') AS 'fecCaptura', p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(c.F_OrdenSuministro,'') os FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto; ");
                                    //   } else 
                                    if (!(Proveedor.equals("")) && (fechaCap.equals(""))) {
                                        flag = true;
                                        rset = con.consulta("SELECT IFNULL(c.F_HoraCaptura, '') AS F_HoraCaptura, c.F_Hora, IFNULL(c.F_UserIngreso, '') AS F_UserIngreso, IFNULL(DATE_FORMAT( c.F_FecCaptura, '%d/%m/%Y' ), '') AS 'fecCaptura', p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(c.F_OrdenSuministro,'') os, c.F_FuenteFinanza FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id where c.F_ClaPro NOT IN ('9999', '9998') AND p.F_ClaProve='" + Proveedor + "' GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                    } else {
                                        flag = true;
                                        rset = con.consulta("SELECT IFNULL(c.F_HoraCaptura, '') AS F_HoraCaptura, c.F_Hora, IFNULL(c.F_UserIngreso, '') AS F_UserIngreso, IFNULL(DATE_FORMAT( c.F_FecCaptura, '%d/%m/%Y' ), '') AS 'fecCaptura', p.F_NomPro, c.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS 'fecCad', SUM(F_CanCom) 'cantidad', DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS 'fecapl', c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, pr.F_DesProy, IFNULL(c.F_OrdenSuministro,'') os, c.F_FuenteFinanza FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve INNER JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad FROM tb_lote GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id  where c.F_ClaPro NOT IN ('9999', '9998') AND c.F_FecApl='" + fechaCap + "' GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;");
                                    }
                                    if (flag)
                                        while (rset.next()) {

                            %>
                            <tr>
                              
                                <td><%=rset.getString("F_NomPro")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaPro")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaLot")%></td>
                                <td class="text-center"><%=rset.getString("fecCad")%></td>
                                <td class="text-center"><%=formatter.format(rset.getInt("cantidad"))%></td>
                                <td class="text-center"><%=rset.getString("F_OrdCom")%></td>
                                <td class="text-center"><%=rset.getString("F_ClaDoc")%></td>
                                <td class="text-center"><%=rset.getString("F_FolRemi")%></td>
                                <td class="text-center"><%=rset.getString("F_User")%></td>
                                <td class="text-center"><%=rset.getString("fecCaptura")%>-<%=rset.getString("F_HoraCaptura")%></td>
                                <td class="text-center"><%=rset.getString("F_UserIngreso")%></td>
                                <td class="text-center"><%=rset.getString("fecApl")%>-<%=rset.getString("F_Hora")%></td>
                                <td class="text-center"><%=rset.getString("os")%></td>
                                <td class="text-center"><%=rset.getString("F_FuenteFinanza")%></td>
                                <!--<td class="text-center"><%=rset.getString("F_Hora")%></td>-->
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
        <br><br><br>
    </body>
</html>
<%@include file="../jspf/piePagina.jspf" %>

<script src="../js/jquery-1.9.1.js"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/jquery-ui-1.10.3.custom.js"></script>
<script src="../js/bootstrap-datepicker.js"></script>

<script src="../js/jquery.dataTables.min_1.js"></script>
<script src="../js/dataTables.bootstrap.js"></script>
<script>
                                $(document).ready(function () {
                                    $('#DatosCompras').DataTable({
                                        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.16/i18n/Spanish.json"
        },
                                        scrollX: true
                                    });

                                    $("#Fecha").datepicker();
                                    $("#Fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
                                });
</script>