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
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fecha_ini = "", fecha_fin = "", NOOC = "", Proveedor = "";
    try {
        //if (request.getParameter("accion").equals("buscar")) {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        NOOC = request.getParameter("NOOC");
        Proveedor = request.getParameter("Proveedor");
        //}
    } catch (Exception e) {

    }
    if (fecha_ini == null) {
        fecha_ini = "";
    }
    if (fecha_fin == null) {
        fecha_fin = "";
    }
    if (NOOC == null) {
        NOOC = "";
    }
    if (Proveedor == null) {
        Proveedor = "";
    }
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"ReporteOC.xls\"");
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
                            <td colspan="3"> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                            <td colspan="6"> <h4><%=fechaDia%></h4></td>
                        </tr><tr></tr>
                        <tr>
                            <th colspan="9"> <h1>Reporte de Orden de Compra</h1></th>
                        </tr><tr></tr>
                    </table>
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <th>No OC</th>
                                <th>Proveedor</th>
                                <th>Clave</th>
                                <th>Nombre genérico</th>
                                <th>Descripción</th>
                                <th>Presentación</th>
                                <th>Grupo terapéutico</th>
                                <th>Solicitado</th>
                                <th>Recibido</th>                                
                                <th>Monto</th>
                                <th>Diferencia</th>
                                <th>Fuente de Financiamiento</th>
                                <th>Contrato</th>
                                <th>Fecha Programada</th>
                                <th>Fecha Ingreso</th>
                                <th>Fecha Creacion</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    try {
                                        String FechaFol = "", NOOCS = "", Query = "", ProveedorS = "";
                                        int ban = 0, ban1 = 0, ban2 = 0;
                                        if (NOOC != "") {
                                            ban = 1;
                                            NOOCS = " AND P.F_NoCompra='" + NOOC + "' ";
                                        }
                                        if (fecha_ini != "" && fecha_fin != "") {
                                            ban1 = 1;
                                            FechaFol = " AND DATE(P.F_Fecha) BETWEEN '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                        }

                                        if (Proveedor != "") {
                                            ban2 = 1;
                                            ProveedorS = " AND P.F_Provee = '" + Proveedor + "' ";
                                        }

                                        if (ban == 1 && ban1 == 1 && ban2 == 1) {
                                            Query = NOOCS + FechaFol + ProveedorS;
                                        } else if (ban == 1 && ban1 == 1) {
                                            Query = NOOCS + FechaFol;
                                        } else if (ban == 1 && ban2 == 1) {
                                            Query = NOOCS + ProveedorS;
                                        } else if (ban1 == 1 && ban2 == 1) {
                                            Query = FechaFol + ProveedorS;
                                        } else if (ban == 1) {
                                            Query = NOOCS;
                                        } else if (ban1 == 1) {
                                            Query = FechaFol;
                                        } else if (ban2 == 1) {
                                            Query = ProveedorS;
                                        }
                                        else if (ban != 1 && ban1 != 1 && ban2 != 1) {
                                                        Query = "AND p.F_Fecha >= '2022-01-01'";
                                                    }
                                        ResultSet rset = con.consulta("SELECT F_NoCompra, F_Clave, M.F_DesPro, FORMAT(SUM(F_Cant), 0) AS F_Cant, FORMAT(IFNULL(C.F_CanCom, 0), 0) AS F_CanCom, FORMAT(IFNULL(C.F_Costo, 0), 2) AS F_Costo, FORMAT(IFNULL(C.F_ComTot, 0), 2) AS F_ComTot, SUM(F_Cant) - IFNULL(C.F_CanCom, 0) AS PENDIENTE, PR.F_NomPro, DATE_FORMAT(P.F_FecSur, '%d/%m/%Y') AS FECHAPROX, IFNULL(C.F_FecApl, '') AS FECHAING, DATE_FORMAT(P.F_Fecha, '%d/%m/%Y') AS FECHCREA, P.F_FuenteFinanza, M.F_NomGen, M.F_PrePro, M.F_Grupo, P.F_Contratos  FROM tb_pedido_sialss P LEFT JOIN ( SELECT F_OrdCom, F_ClaPro, SUM(F_CanCom) AS F_CanCom, SUM(F_ComTot) AS F_ComTot, F_Costo, GROUP_CONCAT( DISTINCT ( DATE_FORMAT(F_FecApl, '%d/%m/%Y'))) AS F_FecApl FROM tb_compra GROUP BY F_OrdCom, F_ClaPro ) AS C ON P.F_NoCompra = C.F_OrdCom AND P.F_Clave = C.F_ClaPro LEFT JOIN tb_medica M ON P.F_Clave = M.F_ClaPro LEFT JOIN tb_proveedor PR ON P.F_Provee = PR.F_ClaProve WHERE P.F_StsPed = 1 " + Query + " GROUP BY P.F_NoCompra, F_Clave, P.F_Provee ORDER BY F_NoCompra ASC, F_Clave ASC;");
                                        while (rset.next()) {
                            %>
                            <tr>
                                <td><%=rset.getString(1)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(2)%></td>
                                <td><%=rset.getString(14)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(15)%></td>
                                <td><%=rset.getString(16)%></td>
                                <td><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>                                
                                <td><%=rset.getString(7)%></td>                                                      
                                <td><%=rset.getString(8)%></td>
                                <td><%=rset.getString(13)%></td>
                                <td><%=rset.getString(17)%></td>
                                <td><%=rset.getString(10)%></td>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString(12)%></td>
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
                <br />
                <br />
                <br />
                <!--div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosfirmas" border="0">
                        <tr>
                            <td colspan="3"><img src="http://187.176.10.50:8081/SIALSS_MDF/imagenes/firmas/juris1/1001A.jpg" width="80" height="100"></td>
                            <td colspan="3"><img src="http://187.176.10.50:8081/SIALSS_MDF/imagenes/firmas/juris1/1001A.jpg" width="80" height="100"></td>
                        </tr>
                        <tr>
                            <td colspan="2"><h5>RESPONSABLE MEDICO</h5></td>
                            <td colspan="3"><h5>COORDINADOR O ADMINISTRADOR MUNICIPAL</h5></td>
                        </tr>
                    </table>
                </div>
                </div-->


            </div>
        </div>
    </body>
</html>