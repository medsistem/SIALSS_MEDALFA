<%-- 
    Document   : gnkKardexClave
    Created on : 22-oct-2014, 8:39:49
    Author     : amerikillo
--%>

<%@page import="java.util.Calendar"%>
<%@page import="java.time.ZonedDateTime"%>
<%@page import="java.time.ZoneId"%>
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
    
    String fecha =  request.getParameter("fecha");
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Trazabilidad_" + request.getParameter("fecha") + ".xls\"");
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
                <th colspan="6"><h2>Trazabilidad del día: <%=request.getParameter("fecha")%></h2></th>
            </tr>
            <tr>
                <td><h4>Ingresos y Egresos</h4></td>
            </tr><tr></tr>
        </table>
        <table border="1">
            <thead> 
                <tr>
                <th>No. Mov</th>
                <th>Usuario</th>
                <th>Documento</th>
                <th>Remisión</th>
                <th>Proveedor</th>
                <th>Folio Salida</th
                <th>Folio Referencia</th>
                <th>Punto de Entrega</th>
                <th>Concepto</th>
                <th>Clave</th>
                <th>Lote</th>
                <th>Caducidad</th>
                <th>Cantidad</th>
                <th>Ubicación</th>
                <th>Origen</th>
                <th>Proyecto</th>
                <th>Fecha Aplicacion</th>
                <th>Fecha Entrega</th>
                <th>Hora</th>
                <th>Comentario</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try (Connection connection = ConnectionManager.getManager(Source.SAA_WAREHOUSE).getConnection();
                            PreparedStatement ps = connection.prepareStatement(kardexQuerys.OBTENER_KARDEX_POR_FECHA_EXPORTAR)) {

                        ps.setString(1, fecha);
                        ps.setString(2, fecha);
                        ps.setString(3, fecha);
                        ps.setString(4, fecha);
                    

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {


                %>
                <tr>
                   <td><%=rs.getInt("noMov")%></td>
                <td><%=rs.getString("usuario")%></td>
                <td><%=rs.getString("ori")%></td>
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
                <td><%=rs.getString("fechaMovimiento")%></td
                <td><%=rs.getString("fechaEnt")%></td>
                <td><%=rs.getString("hora")%></td>
                <td><%=rs.getString("comentarios")%></td>
                </tr>
                <%
                            }
                        }
                    }
                    catch (SQLException | NamingException ex) {
                        Logger.getLogger("gnrKardexFecha.jsp").log(Level.SEVERE, ex.getMessage(), ex);
                    }
                %>
            </tbody>
        </table>
             <h4>Redistribución en Almacén</h4>
        <table border="1">
            <thead> 
                <tr>
                    <th>No. Mov</th>
                    <th>Usuario</th>
                    <th>Documento</th>
                    <th>Remisión</th>
                    <th>Proveedor</th>
                    <th>Folio Salida</th>
                    <th>Folio Referencia</th>
                    <th>Punto de Entrega</th>
                    <th>Concepto</th>
                    <th>Clave</th>
                    <th>Lote</th>
                    <th>Caducidad</th>
                    <th>Cantidad</th>
                    <th>Ubicación</th>
                    <th>Origen</th>
                    <th>Proyecto</th>
                    <th>Fecha Aplicacion</th>
                    <th>Fecha Entrega</th>
                    <th>Hora</th>
                    <th>Comentario</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try (Connection connection = ConnectionManager.getManager(Source.SAA_WAREHOUSE).getConnection();
                            PreparedStatement ps = connection.prepareStatement(kardexQuerys.OBTENER_KARDEX_REDISTRIBUCION_POR_FECHA)) {
                      
                            ps.setString(1, fecha);
                           
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
                    <td><%=rs.getString("F_Comentarios")%></td>
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
