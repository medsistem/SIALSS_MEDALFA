<%-- 
    Document   : gnkKardexClave
    Created on : 22-oct-2014, 8:39:49
    Author     : amerikillo
--%>

<%@page import="java.time.ZonedDateTime"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="com.medalfa.saa.db.ConnectionManager"%>
<%@page import="com.medalfa.saa.db.Source"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.medalfa.saa.querys.kardexQuerys"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "";
    ConectionDB con = new ConectionDB();
    String Btn = request.getParameter("Btn");
    String ProyectoCL = request.getParameter("ProyectoCL");
    String origen = request.getParameter("origen");
    String Campo = "", ANDP = "";

    String fechaInicial = request.getParameter("fechaInicial");
    String fechaFinal = request.getParameter("fechaFinal");

    if (!(ProyectoCL.equals(""))) {
        ANDP = " AND l.F_Proyecto IN (" + ProyectoCL + ") ";
        try {
            con.conectar();
            ResultSet rset = con.consulta("SELECT GROUP_CONCAT(CONCAT(F_Campo,'= 1')) AS F_Campo FROM tb_proyectos WHERE F_Id IN (" + ProyectoCL + ");");
            if (rset.next()) {
                Campo = rset.getString(1);
            }

            Campo = Campo.replace(",", " OR ");
            Campo = " AND ( " + Campo + " ) ";
        } catch (Exception ex) {
            Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    String query_kardex = "";
    int banQuery = 0;
    String query_reubicacion = "";

    if (Btn.equals("Clave")) {

        if (!fechaInicial.equals("") && !fechaFinal.equals("")) 
        {
            query_kardex = String.format(kardexQuerys.OBTENER_KARDEX_POR_CLAVE, "AND m.F_FecMov BETWEEN ? AND ?");
            query_reubicacion = String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_CLAVE, "AND m.F_FecMov BETWEEN ? AND ?");
        } else {
            query_kardex = String.format(kardexQuerys.OBTENER_KARDEX_POR_CLAVE, "");
            query_reubicacion =  String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_CLAVE, "");
        }

        
    } else {
        banQuery = 1;
        
        if (!fechaInicial.equals("") && !fechaFinal.equals("")) 
        {
            query_kardex = String.format(kardexQuerys.OBTENER_KARDEX_POR_LOTE_CADUCIDAD, "AND m.F_FecMov BETWEEN ? AND ?");
            query_reubicacion = String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_LOTE_Y_CADUCIDAD, "AND m.F_FecMov BETWEEN ? AND ?");
        }
        else
        {        
        query_kardex = String.format(kardexQuerys.OBTENER_KARDEX_POR_LOTE_CADUCIDAD, "");
        query_reubicacion = String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_LOTE_Y_CADUCIDAD, "");
        }
    }

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Trazabilidad_" + request.getParameter("Clave") + ".xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <table>
            <%
               Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
                
                Date dNow = cal.getTime();
                
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR)-1);
                
                DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                
                dNow = cal.getTime();
                
                String fechaDia = ft.format(dNow);
            %>
            <tr>
               <td colspan="3"><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"/></td>
                <td colspan="6"> <h4><%=fechaDia%></h4></td>
            </tr>
            <tr></tr>
            <tr>
                <th colspan="6"><h1>Kardex por Clave</h1></th>
            </tr><tr></tr>
            <tr>
                <td colspan="2"><h2>Clave: <%=request.getParameter("Clave")%></h2></td>
                <td colspan="4"> 
            <%
            try {
                con.conectar();              
              ResultSet rset = con.consulta("select F_DesPro, F_NomGen from tb_medica where F_ClaPro = '" + request.getParameter("Clave") + "' " + Campo + ";");
                while (rset.next()) {
                    out.println("<h4>" + rset.getString(2) + " / " + rset.getString(1) + " </h4>");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    con.cierraConexion();
                } catch (Exception ex) {
                ex.printStackTrace();
                }
            }
            %></td>
            </tr>
            <tr>
                <td colspan="3"><h3>Lote: <%=request.getParameter("Lote")%></h3></td>
                <td colspan="3"><h3>Caducidad: <%=request.getParameter("Cadu")%></h3></td>
            </tr>
            <%
            try {
                con.conectar();
                ResultSet rset = null;
                if (Btn.equals("Clave") && request.getParameter("Lote").equals("-Seleccione-")) 
                {
                    System.out.println("sin lote");
                   // rset = con.consulta("select SUM(F_ExiLot) from tb_lote where F_ClaPro = '" + request.getParameter("Clave") + "' and F_ExiLot !=0 " + ANDP + ";");
                   // rset = con.consulta("SELECT m.F_ProMov, SUM(m.F_CantMov * m.F_SigMov) FROM tb_movinv AS m INNER JOIN (SELECT l.F_ClaPro FROM tb_lote AS l WHERE l.F_ClaPro = '" + request.getParameter("Clave") + "' AND l.F_ExiLot > 0 GROUP BY l.F_ClaPro) as lot on lot.F_ClaPro = m.F_ProMov WHERE m.F_ProMov = '" + request.getParameter("Clave") + "' GROUP BY m.F_ProMov;");
                rset = con.consulta("SELECT  m.F_ProMov,(SUM(m.F_CantMov*m.F_SigMov)) AS existencia FROM tb_movinv AS m  WHERE m.F_ProMov = '" + request.getParameter("Clave") + "';");
            
                } 
                else 
                {
                    System.out.println("con lote");
                 //   rset = con.consulta("select SUM(F_ExiLot) from tb_lote as l where F_ClaPro = '" + request.getParameter("Clave") + "' and F_ClaLot ='" + request.getParameter("Lote") + "' and F_FecCad = '" + request.getParameter("Cadu") + "'and l.F_Origen = '"+request.getParameter("origen")+"' and F_ExiLot !=0 " + ANDP + ";");
               //  rset = con.consulta("SELECT m.F_ProMov, SUM(m.F_CantMov * m.F_SigMov) FROM tb_movinv AS m INNER JOIN (select SUM(F_ExiLot), l.F_FolLot, l.F_ClaPro from tb_lote as l where F_ClaPro = '" + request.getParameter("Clave") + "' and F_ClaLot ='" + request.getParameter("Lote") + "' and F_FecCad = '" + request.getParameter("Cadu") + "'and l.F_Origen = '"+request.getParameter("origen")+"' and F_ExiLot !=0 " + ANDP + ") as lot on lot.F_ClaPro = m.F_ProMov and lot.F_FolLot = m.F_LotMov WHERE m.F_ProMov = '" + request.getParameter("Clave") + "' GROUP BY m.F_ProMov;");
              rset = con.consulta("SELECT  m.F_ProMov,(SUM(m.F_CantMov*m.F_SigMov)) AS existencia FROM tb_movinv AS m INNER JOIN (select l.F_ClaPro,l.F_ClaLot,l.F_Ubica, l.F_FolLot, m.F_DesPro FROM tb_lote l INNER JOIN tb_ubica AS u ON  u.F_ClaUbi = l.F_Ubica INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro where l.F_ClaPro = '" + request.getParameter("Clave") + "' and F_ClaLot ='" + request.getParameter("Lote") + "' and F_FecCad = '" + request.getParameter("Cadu") + "'and l.F_Origen = '"+request.getParameter("origen")+"' and F_ExiLot !=0 " + ANDP + " GROUP BY l.F_IdLote ) as lot on lot.F_ClaPro = m.F_ProMov and lot.F_FolLot = m.F_LotMov AND lot.F_Ubica = m.F_UbiMov;");
                  
                }

                while (rset.next()) {
                    String Total = "0";
                    Total = rset.getString(2);
                    if (Total == null) {
                        Total = "0";
                    }
        %>
            <tr>
                <td colspan="3"><h4>Existencia Actual: <%=formatter.format(Integer.parseInt(Total))%> </h4></td>
            </tr>
              <tr>
                <td></td>
            </tr>
        </table>
        <%
                }

            } catch (Exception ex) {
                Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
            } finally {
                try {
                    con.cierraConexion();
                } catch (Exception ex) {
                    Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        %>
        <table border="1">
            <tr>
                <td>Clave</td>
                <td>Lote</td>
                <td>Caducidad</td>
                <td>Ubicacion</td>
                <td>Cantidad</td>
                <td>Ubicación temporal</td>
            </tr>
            <%
                try {
                    con.conectar();
                        ResultSet rset = null;
                        if (Btn.equals("Clave") && request.getParameter("Lote").equals("-Seleccione-")) {
                           
                           // rset = con.consulta("select l.F_ClaPro, l.F_ClaLot, l.F_FecCad, l.F_Ubica, l.F_ExiLot, IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal FROM tb_lote l INNER JOIN tb_ubica u ON l.F_ClaPro = '" + request.getParameter("Clave") + "' AND l.F_ExiLot != 0 AND u.F_ClaUbi = l.F_Ubica " + ANDP + ";");
                         rset = con.consulta("SELECT m.F_ProMov AS clave, l.F_ClaLot, l.F_FecCad, m.F_UbiMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia,IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_IdLote FROM tb_movinv AS m INNER JOIN tb_ubica AS u ON  u.F_ClaUbi = m.F_UbiMov INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot and m.F_UbiMov = l.F_Ubica and m.F_ProMov = l.F_ClaPro WHERE m.F_ProMov = '" + request.getParameter("Clave") + "'  GROUP BY m.F_LotMov, m.F_UbiMov HAVING existencia <> 0;");
                    
                        // rset = con.consulta("SELECT m.F_ProMov,lot.F_ClaLot, lot.F_FecCad, lot.F_Ubica, SUM(m.F_CantMov*m.F_SigMov), lot.esTemporal FROM tb_movinv AS m INNER JOIN (select l.F_ClaPro, l.F_ClaLot, l.F_FecCad, l.F_Ubica, l.F_ExiLot, IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_FolLot FROM tb_lote l INNER JOIN tb_ubica u ON l.F_ClaPro = '" + request.getParameter("Clave") + "' AND l.F_ExiLot != 0 AND u.F_ClaUbi = l.F_Ubica " + ANDP + ") as lot on lot.F_ClaPro = m.F_ProMov and lot.F_FolLot = m.F_LotMov AND lot.F_Ubica = m.F_UbiMov WHERE m.F_ProMov = '" + request.getParameter("Clave") + "' GROUP BY lot.F_ClaLot,lot.F_FecCad,lot.F_Ubica,m.F_LotMov ORDER BY lot.F_ClaLot,lot.F_FecCad,lot.F_Ubica;");
                        } else {
                         //  rset = con.consulta("SELECT l.F_ClaPro, l.F_ClaLot, l.F_FecCad, l.F_Ubica, l.F_ExiLot, IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal FROM tb_lote l INNER JOIN tb_ubica u ON l.F_ClaPro = '" + request.getParameter("Clave") + "' and l.F_ClaLot ='" + request.getParameter("Lote") + "' AND l.F_FecCad = '" + request.getParameter("Cadu") + "' and l.F_Origen = '"+request.getParameter("origen")+"' AND l.F_ExiLot !=0 AND u.F_ClaUbi = l.F_Ubica " + ANDP + ";");
                     //  rset = con.consulta("SELECT m.F_ProMov,lot.F_ClaLot, lot.F_FecCad, lot.F_Ubica, SUM(m.F_CantMov*m.F_SigMov), lot.esTemporal FROM tb_movinv AS m INNER JOIN (SELECT l.F_ClaPro, l.F_ClaLot, l.F_FecCad, l.F_Ubica, l.F_ExiLot, IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_FolLot FROM tb_lote l INNER JOIN tb_ubica u ON l.F_ClaPro = '" + request.getParameter("Clave") + "' and l.F_ClaLot ='" + request.getParameter("Lote") + "' AND l.F_FecCad = '" + request.getParameter("Cadu") + "' and l.F_Origen = '"+request.getParameter("origen")+"' AND l.F_ExiLot !=0 AND u.F_ClaUbi = l.F_Ubica " + ANDP +") as lot on lot.F_ClaPro = m.F_ProMov and lot.F_FolLot = m.F_LotMov AND lot.F_Ubica = m.F_UbiMov WHERE m.F_ProMov = '" + request.getParameter("Clave") + "' GROUP BY lot.F_ClaLot,lot.F_FecCad,lot.F_Ubica,m.F_LotMov ORDER BY lot.F_ClaLot,lot.F_FecCad,lot.F_Ubica");
                      rset = con.consulta("SELECT m.F_ProMov AS clave, l.F_ClaLot, l.F_FecCad, m.F_UbiMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia,IF( u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_IdLote FROM tb_movinv AS m INNER JOIN tb_ubica AS u ON  u.F_ClaUbi = m.F_UbiMov INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot and m.F_UbiMov = l.F_Ubica and m.F_ProMov = l.F_ClaPro and l.F_ClaPro = '" + request.getParameter("Clave") + "' and l.F_ClaLot ='" + request.getParameter("Lote") + "' AND l.F_FecCad = '" + request.getParameter("Cadu") + "' and l.F_Origen = '"+request.getParameter("origen")+"' AND l.F_ExiLot <> 0  " + ANDP +"  WHERE m.F_ProMov = '" + request.getParameter("Clave") + "'  GROUP BY m.F_LotMov, m.F_UbiMov, l.F_IdLote HAVING existencia <> 0;");
             
                        }
                    while (rset.next()) {
            %>
            <tr>
                <td style="mso-number-format:'@';"><%=rset.getString(1)%></td>
                <td style="mso-number-format:'@';"><%=rset.getString(2)%></td>
                <td><%=rset.getString(3)%></td>
                <td><%=rset.getString(4)%></td>
                <td><%=rset.getString(5)%></td>
                <td><%=rset.getString(6)%></td>
            </tr>
            <%
                    }

                } catch (Exception ex) {
                    Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                } finally {
                    try {
                        con.cierraConexion();
                    } catch (Exception ex) {
                        Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            %>
        </table>
        <br/>

        <h4>Ingresos y Egresos</h4>
        <table border="1">
            <thead> 
                <tr>
                    <th>No. Mov</th>
                    <th>Usuario</th>
                    <th>Documento</th>
                    <th>Remisión</th>
                    <th>Proveedor</th>
                    <th>Folio</th>
                    <th>Folio Referencia</th>
                    <th>Punto de Entrega</th>
                    <th>Concepto</th>
                    <th>Clave</th>
                    <th>Lote</th>
                    <th>Caducidad</th>
                    <th>Cantidad</th>
                    <th>Ubicacion</th>
                    <th>Origen</th>
                    <th>Proyecto</th>
                    <th>Fecha Aplicacion</th>
                    <th>Fecha Entrega</th>
                    <th>Hora</th>   
                    <th>Observaciones</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try (Connection connection = ConnectionManager.getManager(Source.SAA_WAREHOUSE).getConnection();
                            PreparedStatement ps = connection.prepareStatement(query_kardex)) {
                        if (banQuery == 0) {
                            ps.setString(1, request.getParameter("Clave"));
                            ps.setString(2, request.getParameter("Clave"));
                            ps.setString(3, request.getParameter("Clave"));
                            ps.setString(4, request.getParameter("Clave"));
                            if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
                                ps.setString(5, fechaInicial);
                                ps.setString(6, fechaFinal);
                            }
                        } else {
                            ps.setString(1, request.getParameter("Lote"));
                            ps.setString(2, request.getParameter("Cadu"));
                            ps.setInt(3, Integer.parseInt(origen));
                            ps.setString(4, request.getParameter("Clave"));
                            ps.setString(5, request.getParameter("Clave"));
                            ps.setString(6, request.getParameter("Clave"));
                            ps.setString(7, request.getParameter("Clave"));
                            if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
                                ps.setString(8, fechaInicial);
                                ps.setString(9, fechaFinal);
                            }
                        }

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {


                %>
                <tr>
                   <td><%=rs.getInt("noMov")%></td>
                    <td><%=rs.getString("usuario")%></td>
                    <td><%=rs.getString(4)%></td>
                    <td><%=rs.getString("remision")%></td>
                    <td><%=rs.getString("proveedor")%></td>
                    <td><%=rs.getString("folioSalida")%></td>
                    <td><%=rs.getString("folioReferencia")%></td>
                    <td><%=rs.getString("puntoEntrega")%></td>
                    <td><%=rs.getString("concepto")%></td>
                    <td style="mso-number-format:'@';"><%=rs.getString("clave")%></td>
                    <td style="mso-number-format:'@';"><%=rs.getString("lote")%></td>
                    <td><%=rs.getString("caducidad")%></td>
                    <td><%=formatter.format(rs.getInt("cantidadMovimiento"))%></td>
                    <td><%=rs.getString("ubicacion")%></td>
                    <td><%=rs.getString("origen")%></td>
                    <td><%=rs.getString("proyecto")%></td>
                    <td><%=rs.getString("fechaMovimiento")%></td>
                    <td><%=rs.getString("fechaEnt")%></td>
                    <td><%=rs.getString("hora")%></td>
                    <td><%=rs.getString("comentarios")%></td>
                </tr>
                <%
                            }
                        }
                    } catch (SQLException | NamingException ex) {
                        Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                    }
                %>
            </tbody>
        </table>

        <br/>

        <h4>Redistribución en Almacén</h4>
        <table border="1">
            <thead> 
                <tr>
                     <th>No. Mov</th>
                    <th>Usuario</th>
                    <th>Documento</th>
                    <th>Remisión</th>
                    <th>Proveedor</th>
                    <th>Folio </th>
                    <th>Folio Referencia</th>
                    <th>Punto de Entrega</th>
                    <th>Concepto</th>
                    <th>Clave</th>
                    <th>Lote</th>
                    <th>Caducidad</th>
                    <th>Cantidad</th>
                    <th>Ubicacion</th>
                    <th>Origen</th>
                    <th>Proyecto</th>
                    <th>Fecha Aplicacion</th>
                    <th>Fecha Entrega</th>
                    <th>Hora</th>
                    <th>Observaciones</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try (Connection connection = ConnectionManager.getManager(Source.SAA_WAREHOUSE).getConnection();
                            PreparedStatement ps = connection.prepareStatement(query_reubicacion)) {
                        if (banQuery == 0) {
                            ps.setString(1, request.getParameter("Clave"));
                            if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
                                ps.setString(2, fechaInicial);
                                ps.setString(3, fechaFinal);
                            }

                        } else {
                            ps.setString(1, request.getParameter("Clave"));
                            ps.setString(2, request.getParameter("Lote"));
                            ps.setString(3, request.getParameter("Cadu"));
                            ps.setInt(4, Integer.parseInt(origen));
                            if (!fechaInicial.equals("") && !fechaFinal.equals("")) {
                                ps.setString(5, fechaInicial);
                                ps.setString(6, fechaFinal);
                            }

                        }

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                %>
                <tr>
                    <td><%=rs.getInt("idMovimiento")%></td>
                    <td><%=rs.getString("usuario")%></td>
                    <td><%=rs.getString("documento")%></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><%=rs.getString("concepto")%></td>
                    <td style="mso-number-format:'@';"><%=rs.getString("clave")%></td>
                    <td style="mso-number-format:'@';"><%=rs.getString("lote")%></td>
                    <td><%=rs.getString("caducidad")%></td>
                    <td><%=formatter.format(rs.getInt("cantidad"))%></td>
                    <td><%=rs.getString("ubicacion")%></td>
                    <td><%=rs.getString("origen")%></td>
                    <td><%=rs.getString("descProyecto")%></td>
                    <td><%=rs.getString("fechaMovimiento")%></td>
                    <td></td>
                    <td><%=rs.getString("hora")%></td>
                    <td><%=rs.getString("F_comentarios")%></td>
                </tr>
                <%
                            }
                        }
                    } catch (SQLException | NamingException ex) {
                        Logger.getLogger("gnrKardexClaveReload.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                    }
                %>
            </tbody>
        </table>
    </body>
</html>
