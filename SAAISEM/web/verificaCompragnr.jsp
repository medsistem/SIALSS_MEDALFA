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
        //response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fol_remi = "", orden_compra = "", unidadF = "";
    try {
        fol_remi = request.getParameter("remision");
        orden_compra = request.getParameter("oc");
        unidadF = request.getParameter("unidadFonsabi");
    } catch (Exception e) {

    }

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Exportar_OC_" + orden_compra + ".xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <table>
                <%
                    Date dNow = new Date();
                    DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                    String fechaDia = ft.format(dNow);
                %>
                <tr>
                    <td> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                    <td colspan="10"><h4><%=fechaDia%></h4></td>
                </tr><tr></tr>
                <tr>
                <th colspan="11"><h1>Validar OC</h1></th>
                </tr>
                <tr>
                    <td colspan="5"><h4>No. OC = <%=orden_compra%></h4></td>
                    <td colspan="5"><h4>Remisión = <%=fol_remi%></h4></td>
                </tr><tr></tr>
                
            </table>
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <th class="text-center">Remisión</th>
                                <th class="text-center">Clave</th>
                                <th class="text-center">Descripción</th>
                                <th class="text-center">Origen</th>
                                <th class="text-center">Lote</th>
                                <th class="text-center">Cantidad</th>
                                <th class="text-center">Costo U</th>
                                <th class="text-center">IVA</th>
                                <th class="text-center">Importe</th>
                                <th class="text-center">Caducidad</th>
                                <th class="text-center">Carta canje</th>
                                <th class="text-center">Fuente financiamiento</th>
                                <th class="text-center">Orden de suministro</th>
                                <th class="text-center">Marca</th>
                                <th class="text-center">Nombre comercial</th>  
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    try {
                                        int banBtn = 0;
                                        
                                        String query = "";
                                        
                                        if(unidadF == null || unidadF == "" || unidadF.equals("null")){
                                            query = "SELECT C.F_Cb, C.F_ClaPro, M.F_DesPro, C.F_Lote, C.F_FecCad, C.F_Pz, C.F_IdCom, C.F_Costo, C.F_ImpTo, C.F_ComTot, C.F_FolRemi, C.F_Obser, C.F_Origen, MAR.F_ClaMar, MAR.F_DesMar, O.F_DesOri, C.F_CartaCanje, C.F_FuenteFinanza, C.F_OrdenSuministro, C.F_MarcaComercial FROM tb_compratemp AS C INNER JOIN tb_medica AS M ON C.F_ClaPro = M.F_ClaPro INNER JOIN tb_marca AS MAR ON C.F_Marca = MAR.F_ClaMar INNER JOIN tb_origen AS O ON C.F_Origen = O.F_ClaOri WHERE C.F_OrdCom = '" + orden_compra + "' AND C.F_FolRemi = '" + fol_remi + "' and (F_unidadFonsabi = '' || F_unidadFonsabi IS NULL) AND C.F_Estado = '2';";
                                        } else{
                                           query = "SELECT C.F_Cb, C.F_ClaPro, M.F_DesPro, C.F_Lote, C.F_FecCad, C.F_Pz, C.F_IdCom, C.F_Costo, C.F_ImpTo, C.F_ComTot, C.F_FolRemi, C.F_Obser, C.F_Origen, MAR.F_ClaMar, MAR.F_DesMar, O.F_DesOri, C.F_CartaCanje, C.F_FuenteFinanza, C.F_OrdenSuministro, C.F_MarcaComercial FROM tb_compratemp AS C INNER JOIN tb_medica AS M ON C.F_ClaPro = M.F_ClaPro INNER JOIN tb_marca AS MAR ON C.F_Marca = MAR.F_ClaMar INNER JOIN tb_origen AS O ON C.F_Origen = O.F_ClaOri WHERE C.F_OrdCom = '" + orden_compra + "' AND C.F_FolRemi = '" + fol_remi + "' AND c.F_unidadFonsabi = '" + unidadF + "' AND C.F_Estado = '2';"; 
                                        }
                                        System.out.println(query);
                                        System.out.println("unidad: " + unidadF);
                                        ResultSet rset = con.consulta(query);
                                        while (rset.next()) {
                                            banBtn = 1;
                                            String F_FecCad = "", F_Cb = "", F_Marca = "";
                                            try {
                                                F_FecCad = rset.getString(5);
                                            } catch (Exception e) {
                                            }

                                            F_Cb = rset.getString("F_Cb");
                                            if (F_Cb.equals("")) {

                                                ResultSet rset2 = con.consulta("SELECT F_Cb, F_ClaMar FROM tb_lote WHERE F_ClaPro = '" + rset.getString("F_ClaPro") + "' AND F_ClaLot = '" + rset.getString("F_Lote") + "' group by F_ClaPro");
                                                while (rset2.next()) {
                                                    F_Cb = rset2.getString("F_Cb");
                                                    F_Marca = rset2.getString("F_ClaMar");
                                                }
                                            }

                                            if (F_Cb.equals("")) {
                                                ResultSet rset2 = con.consulta("SELECT F_Cb, F_ClaMar FROM tb_cb WHERE F_ClaPro = '" + rset.getString("F_ClaPro") + "' and F_ClaLot = '" + rset.getString("F_Lote") + "' group by F_ClaPro");
                                                while (rset2.next()) {
                                                    F_Cb = rset2.getString("F_Cb");
                                                    F_Marca = rset2.getString("F_ClaMar");
                                                }
                                            }
                                            F_Marca = rset.getString("F_DesMar");
                                            if (F_Marca.equals("")) {
                                                ResultSet rset2 = con.consulta("SELECT F_DesMar FROM tb_marca WHERE F_ClaMar = '" + F_Marca + "'");
                                                while (rset2.next()) {
                                                    F_Marca = rset2.getString("F_DesMar");
                                                }
                                            }

                                            if (F_Cb.equals(" ")) {
                                                F_Cb = "";
                                            }

                            %>
                            <tr>
                                <td style="text-align: center; vertical-align: middle;"><%=rset.getString("C.F_FolRemi")%></td>
                                <td style="text-align: center; vertical-align: middle; mso-number-format:'@';"><%=rset.getString("F_ClaPro")%></td>
                                <td style="vertical-align: middle;"><%=rset.getString("F_DesPro")%></td>
                                <td style="text-align: center; vertical-align: middle;"><%=rset.getString("F_Origen")%></td>
                                <td style="mso-number-format:'@'; text-align: center; vertical-align: middle;"><%=rset.getString("F_Lote")%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=rset.getString("F_pz")%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=formatterDecimal.format(rset.getDouble("C.F_Costo"))%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=formatterDecimal.format(rset.getDouble("C.F_ImpTo"))%></td>          
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=formatterDecimal.format(rset.getDouble("C.F_ComTot"))%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=F_FecCad%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=rset.getString("F_CartaCanje")%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=rset.getString("F_FuenteFinanza")%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=rset.getString("F_OrdenSuministro")%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=F_Marca%></td>
                                <td style="text-align: center; vertical-align: middle;" class="text-center"><%=rset.getString("F_MarcaComercial")%></td>
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
    </body>
</html>