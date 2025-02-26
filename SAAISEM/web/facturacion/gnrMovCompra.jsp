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

    String fecha_ini = "", fecha_fin = "", clave = "",contar = "", Concepto = "", Proyecto = "", Origen = "", DesOrigen = "", DesProyecto = "", DesConcepto = "", Desclave="";
    try {
        //if (request.getParameter("accion").equals("buscar")) {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        clave = request.getParameter("clave");
        contar = request.getParameter("Contar");
        Concepto = request.getParameter("Concepto");
        Proyecto = request.getParameter("Proyecto");
        Origen = request.getParameter("Origen");
        DesProyecto = request.getParameter("DesProyecto");
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
    if (Concepto == null) {
        Concepto = "";
    }

    if (Proyecto == null) {
        Proyecto = "0";
    }

    if (Origen == null) {
        Origen = "0";
    }
    
    if(Origen.equals("0")){
        DesOrigen = "TODOS";
    }else if(Origen.equals("1")){
        DesOrigen = "ADMINISTRACIÓN";
    }else if(Origen.equals("2")){
        DesOrigen = "VENTA";
    }else if(Origen.equals("3")){
        DesOrigen = "COVID-EMERGENTE";
    }else if(Origen.equals("4")){
        DesOrigen = "INSABI";
    }
    
    if(Proyecto.equals("0")){
        DesProyecto = "TODOS";
    }else {
        DesProyecto = DesProyecto;
    }
    
    if(Concepto.equals("")){
        DesConcepto = "TODOS";
    }else if(Concepto.equals("1")){
        DesConcepto = "ENTRADA POR COMPRA";
    }else if(Concepto.equals("51")){
        DesConcepto = "SALIDA POR FACTURACIÓN";
    }
    
    if(clave.equals("")){
        Desclave = "TODAS";
    }else {
        Desclave = clave;
    }
    
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Movimientos_Entradas_Salidas.xls\"");
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
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <td>Fecha Consulta:</td>
                                <td colspan="2"><%=fecha_ini%> AL <%=fecha_fin%></td>
                            </tr>
                            <tr>
                                <td>Clave:</td>
                                <td><%=Desclave%></td>
                               
                            </tr>
                            <tr>
                                <td>Proyecto</td>
                                <td><%=DesProyecto%></td>
                            </tr>
                            
                            <tr>
                                <td>Concepto</td>
                                <td><%=DesConcepto%></td>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <br />
            <%if(Concepto.equals("1") || Concepto.equals("1,51") ){%>
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                <td>Fecha</td>
                                 <td>Des/Mov</td>
                                 <td>Clave</td>
                                 <td>ClaveSS</td>
                                 <td>Nombre genérico</td>
                                 <td>Descripción</td>
                                 <td>Presentación</td>
                                 <td>Lote</td>
                                 <td>Caducidad</td>
                                 <td>Cantidad</td>
                                 <td>Origen</td>
                                 <td>Remision</td>
                                 <td>Proveedor</td>
                                 <td>OC</td>
                                 <td>Contrato</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    try {
                                        String FechaFol = "",FechaFol2 = "", Clave = "", Concep = "", Query = "", Query2 = "", AND = "", ANDOrigen = "";
                                        int ban = 0, ban1 = 0, ban2 = 0;

                                        if (Origen.equals("0")) {
                                            ANDOrigen = "";
                                        } else {
                                            ANDOrigen = " AND l.F_Origen='" + Origen + "' ";
                                        }

                                        if (Proyecto.equals("0")) {
                                            AND = "";
                                        } else {
                                            AND = " AND l.F_Proyecto='" + Proyecto + "' ";
                                        }
                                        if (clave != "") {
                                            ban = 1;
                                            Clave = " m.F_ProMov='" + clave + "' ";
                                        }
                                        if (fecha_ini != "" && fecha_fin != "") {
                                            ban1 = 1;
                                            FechaFol = " m.F_FecMov between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                             FechaFol2 = " m.F_FecMov between  DATE_SUB('" + fecha_ini + "', INTERVAL 1 DAy) and DATE_SUB( '" + fecha_fin + "', INTERVAL 1 DAy) ";
                                                  
                                        }
                                        if (Concepto != "") {
                                            ban2 = 1;
                                            Concep = " m.F_ConMov IN (" + Concepto + ") ";
                                        }
                                        if (ban == 1 && ban1 == 1 && ban2 == 1) {
                                            Query = Clave + " AND " + FechaFol + " AND " + Concep;
                                            Query2 = Clave + " AND " + FechaFol2 + " AND " + Concep;
                                        } else if (ban == 1 && ban1 == 1) {
                                            Query = Clave + " AND " + FechaFol;
                                            Query2 = Clave + " AND " + FechaFol2;
                                        } else if (ban == 1 && ban2 == 1) {
                                            Query = Clave + " AND " + Concep;
                                        } else if (ban1 == 1 && ban2 == 1) {
                                            Query = FechaFol + " AND " + Concep;
                                            Query2 = FechaFol2 + " AND " + Concep;
                                        } else if (ban == 1) {
                                            Query = Clave;
                                        } else if (ban1 == 1) {
                                            Query = FechaFol;
                                            Query2 = FechaFol2 + " AND " ;
                                        } else if (ban2 == 1) {
                                            Query = Concep;
                                        }

                                        //ResultSet rset = con.consulta("SELECT C.F_DesCon, F_ProMov, MD.F_DesPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F_CantMov) AS F_CantMov, O.F_DesOri, IFNULL(mc.F_DesMar, '') AS F_DesMar, IFNULL(pr.F_NomPro, '') AS F_NomPro, IFNULL(cp.F_OrdCom,'') AS F_OrdCom FROM tb_movinv m INNER JOIN tb_lote l ON m.F_ProMov = l.F_ClaPro AND m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_medica MD ON m.F_ProMov = MD.F_ClaPro INNER JOIN tb_coninv C ON m.F_ConMov = C.F_IdCon INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen O ON l.F_Origen = O.F_ClaOri LEFT JOIN tb_proveedor pr ON l.F_ClaPrv = pr.F_ClaProve LEFT JOIN tb_marca mc ON l.F_ClaMar = mc.F_ClaMar LEFT JOIN(SELECT F_ClaPro, F_Lote, GROUP_CONCAT(F_OrdCom) AS F_OrdCom FROM tb_compra GROUP BY F_ClaPro, F_Lote) AS cp ON l.F_ClaPro=cp.F_ClaPro AND l.F_FolLot=cp.F_Lote WHERE " + Query + " AND m.F_ConMov IN (1, 51) " + AND + " " + ANDOrigen + " GROUP BY F_ConMov,F_ProMov,l.F_ClaLot,F_FecCad,l.F_Origen;");
                                        String query = "(SELECT C.F_DesCon, m.F_ProMov, MD.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS F_FecCad, Sum(m.F_CantMov) AS F_CantMov, O.F_DesOri, IFNULL( cp.F_FolRemi, '' ) AS F_FolRemi, IFNULL( pr.F_NomPro, '' ) AS F_NomPro, IFNULL( cp.F_OrdCom, '' ) AS F_OrdCom, m.F_FecMov AS Fecha, MD.F_ClaProSS AS clavess, m.F_hora, MD.F_PrePro, MD.F_NomGen, IFNULL(cp.F_Contratos, '') AS F_Contratos FROM tb_movinv AS m INNER JOIN tb_lote AS l ON m.F_ProMov = l.F_ClaPro AND m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_medica AS MD ON m.F_ProMov = MD.F_ClaPro INNER JOIN tb_coninv AS C ON m.F_ConMov = C.F_IdCon INNER JOIN tb_proyectos AS p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri LEFT JOIN tb_proveedor AS pr ON l.F_ClaPrv = pr.F_ClaProve LEFT JOIN ( SELECT tb_compra.F_ClaPro, tb_compra.F_Lote, GROUP_CONCAT( F_OrdCom ) AS F_OrdCom, tb_compra.F_FolRemi, pe.F_Contratos FROM tb_compra INNER JOIN tb_pedidoisem2017 AS pe ON tb_compra.F_OrdCom = pe.F_NoCompra AND tb_compra.F_ClaPro = pe.F_Clave GROUP BY tb_compra.F_ClaPro, tb_compra.F_Lote ) AS cp ON l.F_ClaPro = cp.F_ClaPro AND l.F_FolLot = cp.F_Lote WHERE  " + Query2 + "  AND m.F_ConMov IN (1) AND m.F_ProMov not in ('9999', '9998', '9996', '9995') " + AND + " " + ANDOrigen + "  and m.F_hora > '17:00:00' GROUP BY m.F_ConMov, m.F_ProMov, l.F_ClaLot, l.F_Origen, l.F_FecCad, l.F_Cb order by m.F_FecMov asc, m.F_hora asc, l.F_Origen asc) UNION (SELECT C.F_DesCon, m.F_ProMov, MD.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS F_FecCad, Sum(m.F_CantMov) AS F_CantMov, O.F_DesOri, IFNULL( cp.F_FolRemi, '' ) AS F_FolRemi, IFNULL( pr.F_NomPro, '' ) AS F_NomPro, IFNULL( cp.F_OrdCom, '' ) AS F_OrdCom, m.F_FecMov AS Fecha, MD.F_ClaProSS AS clavess, m.F_hora, MD.F_PrePro, MD.F_NomGen, IFNULL(cp.F_Contratos, '') AS F_Contratos FROM tb_movinv AS m INNER JOIN tb_lote AS l ON m.F_ProMov = l.F_ClaPro AND m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_medica AS MD ON m.F_ProMov = MD.F_ClaPro INNER JOIN tb_coninv AS C ON m.F_ConMov = C.F_IdCon INNER JOIN tb_proyectos AS p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri LEFT JOIN tb_proveedor AS pr ON l.F_ClaPrv = pr.F_ClaProve LEFT JOIN ( SELECT tb_compra.F_ClaPro, tb_compra.F_Lote, GROUP_CONCAT( F_OrdCom ) AS F_OrdCom, tb_compra.F_FolRemi, pe.F_Contratos FROM tb_compra INNER JOIN tb_pedidoisem2017 AS pe ON tb_compra.F_OrdCom = pe.F_NoCompra AND tb_compra.F_ClaPro = pe.F_Clave GROUP BY tb_compra.F_ClaPro, tb_compra.F_Lote ) AS cp ON l.F_ClaPro = cp.F_ClaPro AND l.F_FolLot = cp.F_Lote WHERE  " + Query + " AND m.F_ConMov IN (1) AND m.F_ProMov not in ('9999', '9998', '9996', '9995') " + AND + " " + ANDOrigen + " AND m.F_hora <= '17:00:00' GROUP BY m.F_ConMov, m.F_ProMov, l.F_ClaLot, l.F_Origen, l.F_FecCad, l.F_Cb order by m.F_FecMov asc, m.F_hora asc, l.F_Origen asc);";
                                        ResultSet rset = con.consulta(query);
                                      while (rset.next()) {
                                      System.out.println("query wxcwl" +query);
                            %>
                            <tr>
                                <td><%=rset.getString(11)%></td>
                                <td><%=rset.getString(1)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(2)%></td>
                                <td><%=rset.getString(12)%></td>
                                <td><%=rset.getString(15)%></td>
                                <td><%=rset.getString(3)%></td>
                                <td><%=rset.getString(14)%></td>
                                <td style="mso-number-format:'@';"><%=rset.getString(4)%></td>
                                <td><%=rset.getString(5)%></td>
                                <td><%=rset.getString(6)%></td>
                                <td><%=rset.getString(7)%></td>
                                <td><%=rset.getString(8)%></td>
                                <td><%=rset.getString(9)%></td>
                                <td><%=rset.getString(10)%></td>
                                <td><%=rset.getString(16)%></td>
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
        <%}%>
      <%if(Concepto.equals("51")){%>
            <div class="panel panel-success">
                <div class="panel-body">
                    <table class="table table-bordered table-striped" id="datosCompras" border="1">
                        <thead>
                            <tr>
                                 <td>Fecha</td>
                                            <td>Folio</td>
                                            <td>ClaveUni</td>
                                            <td>Unidad</td>
                                            <td>Clave</td>
                                            <td>ClaveSS</td>
                                            <td>Nombre genérico</td>
                                            <td>Descripción</td>
                                            <td>Presentación</td>
                                            <td>Lote</td>
                                            <td>Caducidad</td>
                                            <td>Cantidad</td>
                                            <td>Origen</td>
                                            <td>Movimiento</td>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    con.conectar();
                                    try {
                                        String FechaFol = "",FechaFol2 = "", Clave = "", Concep = "", Query = "",Query2 = "", AND = "", ANDOrigen = "";
                                        int ban = 0, ban1 = 0, ban2 = 0;

                                        if (Origen.equals("0")) {
                                            ANDOrigen = "";
                                        } else {
                                            ANDOrigen = " AND l.F_Origen='" + Origen + "' ";
                                        }

                                        if (Proyecto.equals("0")) {
                                            AND = "";
                                        } else {
                                            AND = " AND l.F_Proyecto='" + Proyecto + "' ";
                                        }
                                        if (clave != "") {
                                            ban = 1;
                                            Clave = " f.F_ClaPro='" + clave + "' ";
                                        }
                                        if (fecha_ini != "" && fecha_fin != "") {
                                            ban1 = 1;
                                            FechaFol = " f.F_FecApl between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                             FechaFol2 = " f.F_FecApl between  DATE_SUB('" + fecha_ini + "', INTERVAL 1 DAy) and DATE_SUB( '" + fecha_fin + "', INTERVAL 1 DAy) ";
                                                  
                                        }
                                        if (Concepto != "") {
                                            ban2 = 1;
                                            Concep = "Salida Por Facturacion";
                                        }
                                        if (ban == 1 && ban1 == 1 && ban2 == 1) {
                                            Query = Clave + " AND " + FechaFol + " AND " ;
                                            Query2 = Clave + " AND " + FechaFol2 + " AND " ;
                                        } else if (ban == 1 && ban1 == 1) {
                                            Query = Clave + " AND " + FechaFol + " AND ";
                                            Query = Clave + " AND " + FechaFol2 + " AND ";
                                        } else if (ban == 1 && ban2 == 1) {
                                            Query = Clave + " AND ";
                                        } else if (ban1 == 1 && ban2 == 1) {
                                            Query = FechaFol + " AND " ;
                                            Query2 = FechaFol2 + " AND " ;
                                        } else if (ban == 1) {
                                            Query = Clave + " AND ";
                                        } else if (ban1 == 1) {
                                            Query = FechaFol + " AND ";
                                            Query2 = FechaFol2 + " AND ";
                                        } 
                                        /*else if (ban2 == 1) {
                                            Query = Concep;
                                        }*/

                                        //ResultSet rset = con.consulta("SELECT C.F_DesCon, F_ProMov, MD.F_DesPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F_CantMov) AS F_CantMov, O.F_DesOri, IFNULL(mc.F_DesMar, '') AS F_DesMar, IFNULL(pr.F_NomPro, '') AS F_NomPro, IFNULL(cp.F_OrdCom,'') AS F_OrdCom FROM tb_movinv m INNER JOIN tb_lote l ON m.F_ProMov = l.F_ClaPro AND m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_medica MD ON m.F_ProMov = MD.F_ClaPro INNER JOIN tb_coninv C ON m.F_ConMov = C.F_IdCon INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen O ON l.F_Origen = O.F_ClaOri LEFT JOIN tb_proveedor pr ON l.F_ClaPrv = pr.F_ClaProve LEFT JOIN tb_marca mc ON l.F_ClaMar = mc.F_ClaMar LEFT JOIN(SELECT F_ClaPro, F_Lote, GROUP_CONCAT(F_OrdCom) AS F_OrdCom FROM tb_compra GROUP BY F_ClaPro, F_Lote) AS cp ON l.F_ClaPro=cp.F_ClaPro AND l.F_FolLot=cp.F_Lote WHERE " + Query + " AND m.F_ConMov IN (1, 51) " + AND + " " + ANDOrigen + " GROUP BY F_ConMov,F_ProMov,l.F_ClaLot,F_FecCad,l.F_Origen;");
                                      //ResultSet rset = con.consulta("SELECT m.F_FecMov, m.F_DocMov, f.F_ClaCli, atn.F_NomCli,m.F_ProMov, MD.F_ClaProSS, MD.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS F_FecCad, m.F_CantMov AS F_CantMov, O.F_DesOri, C.F_DesCon FROM tb_movinv AS m INNER JOIN tb_lote AS l ON m.F_ProMov = l.F_ClaPro AND m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica INNER JOIN tb_medica AS MD ON m.F_ProMov = MD.F_ClaPro INNER JOIN tb_coninv AS C ON m.F_ConMov = C.F_IdCon INNER JOIN tb_proyectos AS p ON l.F_Proyecto = p.F_Id INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri INNER JOIN tb_factura AS f ON m.F_DocMov = f.F_ClaDoc INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli WHERE	" + Query + "  AND m.F_ConMov IN ( 51 ) " + AND + " " + ANDOrigen + " GROUP BY m.F_ConMov, m.F_ProMov, l.F_ClaLot, l.F_FecCad, l.F_Origen ORDER BY m.F_DocMov ASC;");
                                     // ResultSet rset = con.consulta("SELECT f.F_FecApl, f.F_ClaDoc, atn.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, l.F_ClaLot, l.F_FecCad, f.F_CantSur, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_lote AS l ON f.F_Lote = l.F_FolLot AND f.F_ClaPro = l.F_ClaPro INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + " f.F_CantSur > 0 AND f.F_StsFact = 'A'   " + ANDOrigen + "  " + AND + " GROUP BY f.F_IdFact ORDER BY  f.F_FecApl ASC, f.F_ClaDoc ASC,  f.F_ClaPro;");
                                      ResultSet rset = con.consulta("(SELECT f.F_FecApl, f.F_ClaDoc, atn.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, l.F_ClaLot, l.F_FecCad, f.F_CantSur, o.F_DesOri, m.F_NomGen, m.F_PrePro FROM tb_factura AS f INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_lote AS l ON f.F_Lote = l.F_FolLot AND f.F_ClaPro = l.F_ClaPro INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query2 + "  f.F_CantSur > 0 AND f.F_StsFact = 'A' AND f.F_ClaPro not in ('9999', '9998', '9996', '9995')  " + ANDOrigen + "  " + AND + "  AND F.F_Hora > '17:00:00' group BY f.F_IdFact ORDER BY  f.F_FecApl ASC, f.F_ClaDoc ASC,  f.F_ClaPro) UNION (SELECT f.F_FecApl, f.F_ClaDoc, atn.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, l.F_ClaLot, l.F_FecCad, f.F_CantSur, o.F_DesOri, m.F_NomGen, m.F_PrePro FROM tb_factura AS f INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_lote AS l ON f.F_Lote = l.F_FolLot AND f.F_ClaPro = l.F_ClaPro INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  f.F_CantSur > 0 AND f.F_StsFact = 'A' AND f.F_ClaPro not in ('9999', '9998', '9996', '9995')  " + ANDOrigen + "  " + AND + " AND F.F_Hora <= '17:00:00'  GROUP BY f.F_IdFact ORDER BY  f.F_FecApl ASC, f.F_ClaDoc ASC,  f.F_ClaPro);");
                                                                                
                                      while (rset.next()) {

                                        %>
                                        <tr>
                                            <td><%=rset.getString(1)%></td>
                                            <td><%=rset.getString(2)%></td>
                                            <td><%=rset.getString(3)%></td>
                                            <td><%=rset.getString(4)%></td>
                                            <td style="mso-number-format:'@';"><%=rset.getString(5)%></td>
                                            <td><%=rset.getString(6)%></td>
                                            <td><%=rset.getString(12)%></td>
                                            <td><%=rset.getString(7)%></td>
                                            <td><%=rset.getString(13)%></td>
                                            <td style="mso-number-format:'@';"><%=rset.getString(8)%></td>
                                            <td><%=rset.getString(9)%></td>
                                            <td><%=rset.getString(10)%></td>
                                            <td><%=rset.getString(11)%></td>
                                            <td><%=Concep%></td>
                                            
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
               
            </div>
        </div>
           <%}%>
    </body>
</html>