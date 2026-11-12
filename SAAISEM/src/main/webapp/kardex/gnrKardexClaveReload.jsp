<%--
    Kardex por clave o por lote (se descarga como Excel). Lo abre kardex/kardex.jsp.
    Las consultas están en mx.medalfa.sialss.dao.kardex.KardexDao; esta página solo las pinta.
--%>
<%@page import="java.util.List"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="mx.medalfa.sialss.dao.Consultas"%>
<%@page import="mx.medalfa.sialss.dao.Fila"%>
<%@page import="mx.medalfa.sialss.dao.kardex.KardexDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%!
    private static String h(String s) {
        return s == null ? "null" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    private static String p(jakarta.servlet.http.HttpServletRequest r, String n) {
        String v = r.getParameter(n);
        return v == null ? "" : v;
    }
%>
<%
    Logger log = Logger.getLogger("gnrKardexClaveReload.jsp");
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);

    KardexDao.Filtro filtro = new KardexDao.Filtro(p(request, "Btn"), p(request, "Clave"), p(request, "Lote"), p(request, "Cadu"),
            p(request, "origen"), Consultas.enteros(p(request, "ProyectoCL")), p(request, "fechaInicial"), p(request, "fechaFinal"));
    KardexDao dao = new KardexDao();

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Trazabilidad_" + filtro.clave().replaceAll("[^\\w.-]", "_") + ".xls\"");

    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 1);
    String fechaDia = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss").format(cal.getTime());
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <table>
            <tr>
               <td colspan="3"><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"/></td>
                <td colspan="6"> <h4><%=fechaDia%></h4></td>
            </tr>
            <tr></tr>
            <tr>
                <th colspan="6"><h1>Kardex por Clave</h1></th>
            </tr><tr></tr>
            <tr>
                <td colspan="2"><h2>Clave: <%=h(filtro.clave())%></h2></td>
                <td colspan="4">
            <%
                try {
                    for (Fila f : dao.producto(filtro)) {
                        out.println("<h4>" + h(f.get(2)) + " / " + h(f.get(1)) + " </h4>");
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
            %></td>
            </tr>
            <tr>
                <td colspan="3"><h3>Lote: <%=h(filtro.lote())%></h3></td>
                <td colspan="3"><h3>Caducidad: <%=h(filtro.caducidad())%></h3></td>
            </tr>
            <%
                try {
                    for (Fila f : dao.existencia(filtro)) {
                        String total = f.get(2) == null ? "0" : f.get(2);
            %>
            <tr>
                <td colspan="3"><h4>Existencia Actual: <%=formatter.format(new java.math.BigDecimal(total))%> </h4></td>
            </tr>
              <tr>
                <td></td>
            </tr>
        </table>
        <%
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
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
                    for (Fila f : dao.lotes(filtro)) {
            %>
            <tr>
                <td style="mso-number-format:'@';"><%=h(f.get(1))%></td>
                <td style="mso-number-format:'@';"><%=h(f.get(2))%></td>
                <td><%=h(f.get(3))%></td>
                <td><%=h(f.get(4))%></td>
                <td><%=h(f.get(5))%></td>
                <td><%=h(f.get(6))%></td>
            </tr>
            <%
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
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
                    try {
                        for (Fila f : dao.movimientos(filtro)) {
                %>
                <tr>
                    <td><%=f.entero("noMov")%></td>
                    <td><%=h(f.get("usuario"))%></td>
                    <td><%=h(f.get(4))%></td>
                    <td><%=h(f.get("remision"))%></td>
                    <td><%=h(f.get("proveedor"))%></td>
                    <td><%=h(f.get("folioSalida"))%></td>
                    <td><%=h(f.get("folioReferencia"))%></td>
                    <td><%=h(f.get("puntoEntrega"))%></td>
                    <td><%=h(f.get("concepto"))%></td>
                    <td style="mso-number-format:'@';"><%=h(f.get("clave"))%></td>
                    <td style="mso-number-format:'@';"><%=h(f.get("lote"))%></td>
                    <td><%=h(f.get("caducidad"))%></td>
                    <td><%=formatter.format(f.entero("cantidadMovimiento"))%></td>
                    <td><%=h(f.get("ubicacion"))%></td>
                    <td><%=h(f.get("origen"))%></td>
                    <td><%=h(f.get("proyecto"))%></td>
                    <td><%=h(f.get("fechaMovimiento"))%></td>
                    <td><%=h(f.get("fechaEnt"))%></td>
                    <td><%=h(f.get("hora"))%></td>
                    <td><%=h(f.get("comentarios"))%></td>
                </tr>
                <%
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, ex.getMessage(), ex);
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
                    try {
                        for (Fila f : dao.reubicaciones(filtro)) {
                %>
                <tr>
                    <td><%=f.entero("idMovimiento")%></td>
                    <td><%=h(f.get("usuario"))%></td>
                    <td><%=h(f.get("documento"))%></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><%=h(f.get("concepto"))%></td>
                    <td style="mso-number-format:'@';"><%=h(f.get("clave"))%></td>
                    <td style="mso-number-format:'@';"><%=h(f.get("lote"))%></td>
                    <td><%=h(f.get("caducidad"))%></td>
                    <td><%=formatter.format(f.entero("cantidad"))%></td>
                    <td><%=h(f.get("ubicacion"))%></td>
                    <td><%=h(f.get("origen"))%></td>
                    <td><%=h(f.get("descProyecto"))%></td>
                    <td><%=h(f.get("fechaMovimiento"))%></td>
                    <td></td>
                    <td><%=h(f.get("hora"))%></td>
                    <td><%=h(f.get("F_comentarios"))%></td>
                </tr>
                <%
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                %>
            </tbody>
        </table>
    </body>
</html>
