<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

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

    String fol_gnkl = "", fol_remi = "", orden_compra = "", fecha = "";
    int Ban = 0;
    try {
        if (request.getParameter("accion").equals("buscar")) {
            fol_gnkl = request.getParameter("fol_gnkl");
            fol_remi = request.getParameter("fol_remi");
            orden_compra = request.getParameter("orden_compra");
            String IdProyecto = request.getParameter("idProyecto");
            fecha = request.getParameter("fecha");

        }

    } catch (Exception e) {

    }
    if (fol_gnkl == null) {
        fol_gnkl = "";
        fol_remi = "";
        orden_compra = "";
        fecha = "";

    }

    Ban = Integer.parseInt(request.getParameter("Ban"));
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Factura_" + request.getParameter("fol_gnkl") + ".xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <h4>Folio de Factura: <%=request.getParameter("fol_gnkl")%></h4>
            <%
                try {
                    con.conectar();
                    try {
                        ResultSet rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,SUM(F.F_CantReq) as requerido,SUM(F.F_CantSur) as surtido,F.F_Costo,SUM(F.F_Monto) as importe, F.F_Ubicacion, DATE_FORMAT(F.F_FecApl,'%d/%m/%Y') as elaboracion, tb_obserfact.F_Obser FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli LEFT JOIN tb_obserfact ON F.F_ClaDoc = tb_obserfact.F_IdFact AND F.F_Proyecto = tb_obserfact.F_Proyecto WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' GROUP BY F.F_ClaDoc");
                        while (rset.next()) {
                            String obser = rset.getString(14);
                            if (obser.equals(null)) {
                                obser = "";
                            }

            %>

            <h4>Cliente: <%=rset.getString(1)%></h4>
            <h4>Fecha de Entrega: <%=rset.getString(2)%></h4>
            <h4>Fecha de Elaboración: <%=rset.getString(13)%></h4>
            <h4>Observación: <%=obser%></h4>
            <h4>Factura: <%=rset.getString(3)%></h4>
            <%
                int req = 0, sur = 0;
                Double imp = 0.0;
                ResultSet rset2 = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,(F.F_CantSur) as surtido,(F.F_CantReq) as requerido,F.F_Costo,(F.F_Monto) as importe, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' GROUP BY F.F_IdFact");
                while (rset2.next()) {
                    req = req + rset2.getInt("requerido");
                    sur = sur + rset2.getInt("surtido");
                    imp = imp + rset2.getDouble("importe");
                    System.out.println(req);
                }
            %>

            <div class="row">
                <h5 class="col-sm-3">Total Solicitado: <%=formatter.format(req)%></h5>
                <h5 class="col-sm-3">Total Surtido: <%=formatter.format(sur)%></h5>
                <h5 class="col-sm-3">Total Importe: $ <%=formatterDecimal.format(imp)%></h5>
            </div>
            <%
                        }
                    } catch (Exception e) {

                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            %>
            <br />
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <td>Clave</td>
                                <td>Descripción</td>
                                <td>Origen</td>
                                <td>Lote</td>
                                <td>Caducidad</td>
                                <td>Req.</td>
                                <td>Ubicación</td>
                                <td>Ent.</td>
                                <td>Costo U</td>
                                <td>Importe</td>
                                <td>Fec Fab</td>
                                <td>Marca</td>
                                <td>Status</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                String ubi = request.getParameter("ubi");
                                System.out.println("Ubica: "+ubi);
                                System.out.println("Ban: "+Ban);
                                try {
                                    con.conectar();

                                    try {
                                        ResultSet rset = null;
                                        if (Ban == 0) {
                                          System.out.println("entre a ban");
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro  INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur = 0 GROUP BY F.F_IdFact;");
                                        } else if (ubi.contains("APE") || ubi.contains("ES") || ubi.contains("ESPAL") || ubi.contains("APECADUCO")) {
                                          System.out.println("entre ape");   
                                        rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar INNER JOIN tb_ape ape ON F.F_ClaPro = ape.F_ClaPro WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND (F_Ubicacion RLIKE '(MODULA|APE)|((APE|ES)(1|2|3|PAL|URGENTE|CDist|SANT))' OR F_Ubicacion RLIKE 'FACFO')   GROUP BY F.F_IdFact;");
                                        } else if (ubi.equals("REDFRIA") || ubi.contains("RFFO") || ubi.contains("RFCADUCO")) {
                                          System.out.println("entre a rf");
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion RLIKE 'REDFRIA|RFFO|RFCADUCO' GROUP BY F.F_IdFact;");
                                        } else if (ubi.equals("CONTROLADO") || ubi.contains("CTRFO") || ubi.contains("CTRLCADUCO")) {
                                          System.out.println("entre a controlado");    
                                        rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion RLIKE 'CONTROLADO|CTRFO|CTRLCADUCO' GROUP BY F.F_IdFact;");
                                        } else if (ubi.equals("ONCOAPE")) {
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion LIKE 'ONCOAPE%' GROUP BY F.F_IdFact;");
                                        } else if (ubi.equals("ONCORF")) {
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion LIKE 'ONCORF%' GROUP BY F.F_IdFact;");
                                         } else if (ubi.contains("FACFO")) {
                                         System.out.println("entre a fonsabi");
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar LEFT JOIN tb_ape ape ON F.F_ClaPro = ape.F_ClaPro WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion LIKE 'FACFO%' AND ape.F_ClaPro is NULL GROUP BY F.F_IdFact;");                                      
                                        } else {
                                            rset = con.consulta("SELECT U.F_NomCli,DATE_FORMAT(F.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F.F_ClaDoc,F.F_ClaPro,M.F_DesPro,L.F_ClaLot,DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,F.F_CantReq,F.F_CantSur,F.F_Costo,F.F_Monto, F.F_Ubicacion, DATE_FORMAT(L.F_FecFab, '%d/%m/%Y') AS F_FecFab, Mar.F_DesMar, F_StsFact, L.F_Origen FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli  INNER JOIN tb_marca Mar ON Mar.F_ClaMar = L.F_ClaMar WHERE F.F_ClaDoc='" + request.getParameter("fol_gnkl") + "' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' AND F.F_CantSur > 0 AND F_Ubicacion NOT IN ('CONTROLADO', 'REDFRIA','APE','ONCOAPE','ONCORF','ONCORFCADUCO','ONCOAPECADUCO','STONCORFCADUCO','STONCOAPECADUCO') AND F_Ubicacion NOT RLIKE '(MODULA|APE)|((APE|ES)(1|2|3|PAL|URGENTE|CDist|SANT))' AND F_Ubicacion NOT RLIKE 'REDFRIA|RFFO|RFCADUCO' AND F_Ubicacion NOT RLIKE 'FACFO|RFFO|CTRFO'  GROUP BY F.F_IdFact;");
                                        }

                                        while (rset.next()) {
                            %>
                            <tr>
                                <td style="mso-number-format:'@';"><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString("F_Origen")%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(6)%></td>
                                <td><%=rset.getString(7)%></td>
                                <td><%=rset.getString(8)%></td>
                                <td><%=rset.getString(12)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td><%=rset.getString(10)%></td>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString("F_FecFab")%></td>
                                <td><%=rset.getString("F_DesMar")%></td>
                                <td><%=rset.getString("F_StsFact")%></td>
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