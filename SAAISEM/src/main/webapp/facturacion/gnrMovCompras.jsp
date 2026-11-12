<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
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
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fecha_ini = "", fecha_fin = "", clave = "", claCli = "", Proyecto = "", origen = "";
    try {
        //if (request.getParameter("accion").equals("buscar")) {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        clave = request.getParameter("clave");
        claCli = request.getParameter("claCli");
        origen = request.getParameter("Origen");
        //}
    } catch (Exception e) {

    }
    if (fecha_ini == null) {
        fecha_ini = "";
    }
    if (fecha_fin == null) {
        fecha_fin = "";
    }
    if (clave == null) {
        clave = "";
    }
    if (claCli == null) {
        claCli = "";
    }

    if (Proyecto == null) {
        Proyecto = "0";
    }

    if (origen == null) {
        origen = "0";
    }
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Movimientos.xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <div class="panel panel-success">
                <div class="panel-body">

                    <table>
                        <%
                            Date dNow = new Date();
                            DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                            String fechaDia = ft.format(dNow);
                        %>
                        <tr>
                            <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"></td>
                            <td colspan="7"><h4><%=fechaDia%></td>
                        </tr><tr></tr>
                        <tr>
                            <th colspan="8"> <h1>Reporte de Consulta de Movimientos</h1></th>
                        </tr><tr></tr>                        
                    </table>
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <%
                            try {
                                con.conectar();
                                try {
                                    System.out.println("clacli" + claCli + "clave" + clave + "fechaini " + fecha_ini + "fecha fin " + fecha_fin + "origen" + origen);
                                    if (claCli.equals("51")) { %>
                        <thead>
                            <tr>
                                <td>Fecha</td>
                                <td>Folio</td>
                                <td>Clave unidad</td>
                                <td>Unidad</td>
                                <td>Clave</td>
                                <td>Clavess</td>
                                <td>Nombre genérico</td>
                                <td>Descripcion</td>
                                <td>Presentacion</td>
                                <td>Lote</td>
                                <td>Caducidad</td>
                                <td>Cantidad</td>
                                <td>Origen</td>
                                <td>Movimiento</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                System.out.println("entre");
                                String fechaFol = "", Query = "";
                                int ban1 = 0;

                                if ((fecha_ini != "") && (fecha_fin != "")) {
                                    ban1 = 1;
                                    System.out.println(ban1);
                                    fechaFol = " v.FecEnt BETWEEN '" + fecha_ini + "' AND '" + fecha_fin + "' ";
                                    System.out.println("");
                                }
                                if ((clave != "") && (ban1 != 1) && origen.equals("0")) {
                                    Query = " v.clave ='" + clave + "' ";
                                    System.out.println("1");
                                } else if ((clave == "") && (ban1 == 1) && origen.equals("0")) {
                                    Query = "" + fechaFol + "";
                                    System.out.println("2");
                                } else if ((clave == "") && (ban1 != 1) && (!origen.equals("0"))) {
                                    Query = " v.origen ='" + origen + "' ";
                                    System.out.println("3");
                                } else if ((clave != "") && (ban1 == 1) && origen.equals("0")) {
                                    Query = " v.clave ='" + clave + "' AND " + fechaFol + "";
                                    System.out.println("4");
                                } else if ((clave != "") && (ban1 == 1) && (!origen.equals("0"))) {
                                    Query = " v.clave ='" + clave + "' AND " + fechaFol + " AND v.origen ='" + origen + "' ";
                                    System.out.println("5");
                                };
                                System.out.println("query" + Query);
                                String query = "SELECT v.FecEnt, v.folio, v.Clave_Unidad, v.Unidad, v.clave, v.F_ClaProSS, v.F_DesPro, v.lote, v.caducidad, v.surtido, v.F_DesOri, v.Nombre_Generico, v.`Presentación`, v.concepto, v.origen FROM v_facturacion AS v WHERE " + Query + "";
                                System.out.println(query);
                                ResultSet rset = con.consulta(query);

                                while (rset.next()) {
                                    System.out.println(query);
                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(2)%></td>
                                <td ><%=rset.getString(3)%></td>
                                <td><%=rset.getString(4)%></td>
                                <td style="mso-number-format:'@'"><%=rset.getString(5)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(6)%></td>
                                <td><%=rset.getString(12)%></td>
                                <td><%=rset.getString(7)%></td>
                                <td><%=rset.getString(13)%></td>
                                <td style="mso-number-format:'@'"><%=rset.getString(8)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td ><%=rset.getString(10)%></td>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString(14)%></td>
                            </tr>
                            <%
                                }
                            } else if (claCli.equals("1"))  {
                            System.out.println("entre a compra");
                            %>

                        <thead>
                            <tr>
                                <td>Fecha</td>
                                <td>Folio</td>
                                <td>Clave</td>
                                <td>Clavess</td>
                                <td>Nombre genérico</td>
                                <td>Descripcion</td>
                                <td>Presentacion</td>
                                <td>Lote</td>
                                 <td>Caducidad</td>
                                 <td>Cantidad</td>
                                <td>Origen</td>
                                <td>Remisión</td>
                                <td>Proveedor</td>                  
                                <td>O.C.</td>                  
                                <td>Contrato</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%

                                String fechaFol = "", Query = "";
                                int ban1 = 0;
                                if (fecha_ini != "" && fecha_fin != "") {
                                    ban1 = 1;
                                    fechaFol = " c.F_FecApl between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                }

                                if ((clave != "") && (ban1 != 1) && origen.equals("0")) {
                                    Query = " c.F_ClaPro ='" + clave + "' ";
                                } else if ((clave == "") && (ban1 == 1) && origen.equals("0")) {
                                    Query = "" + fechaFol + "";
                                } else if ((clave == "") && (ban1 != 1) && (!origen.equals("0"))) {
                                    Query = " l.F_Origen='" + origen + "' ";
                                } else if ((clave != "") && (ban1 == 1) && origen.equals("0")) {
                                    Query = " c.F_ClaPro ='" + clave + "' AND " + fechaFol + "";
                                } else if ((clave != "") && (ban1 == 1) && (!origen.equals("0"))) {
                                    Query = " c.F_ClaPro ='" + clave + "' AND " + fechaFol + " AND l.F_Origen='" + origen + "' ";
                                }
                                System.out.println("query" + Query);
                                String query = "SELECT 'Entrada por compra' AS F_DesCon, c.F_ClaPro, tb_medica.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS 'F_FecCad', SUM( F_CanCom ) 'F_CantMov', O.F_DesOri, c.F_FolRemi, p.F_NomPro, c.F_OrdCom, DATE_FORMAT( F_FecApl, '%d/%m/%Y' ) AS 'Fecha', tb_medica.F_ClaProSS AS clavess, c.F_ClaDoc, tb_medica.F_PrePro, tb_medica.F_NomGen, IFNULL( ped.F_Contratos, '' ) AS F_Contratos	FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar, F_Origen, F_Proyecto FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m ON l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica ON tb_medica.F_ClaPro = c.F_ClaPro INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri LEFT JOIN ( SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedido_sialss AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro WHERE " + Query + " GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                                System.out.println(query);
                                ResultSet rset = con.consulta(query);

                                while (rset.next()) {
                                    System.out.println(query);
                            %>
                            <tr>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString(1)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(2)%></td>
                                <td><%=rset.getString(12)%></td>
                                <td><%=rset.getString(15)%></td>
                                <td ><%=rset.getString(3)%></td>
                                <td><%=rset.getString(14)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(6)%></td>
                                <td><%=rset.getString(7)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(8)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td><%=rset.getString(10)%></td>
                                <td><%=rset.getString(16)%></td>
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
                <br />
                <br />
                <br />
            </div>
        </div>
    </body>
</html>