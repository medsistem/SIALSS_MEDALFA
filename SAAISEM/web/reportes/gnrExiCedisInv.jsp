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
    int Total = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Existencias_Cedis_Mes_Global.xls\"");
    try {
                            con.conectar();
                            ResultSet Consulta = null;
%>
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
                    <td> <img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                    <td colspan="5"><h4><%=fechaDia%></h4></td>
                </tr><tr></tr>
                <tr>
                    <th colspan="6"><h1>Existencia en Cedis por Mes</h1></th>
                </tr><tr></tr>
            </table>
            <table class="table table-bordered table-striped" id="datosCompras" border="1">
                <thead>
                       <tr>
                        <th class="text-center">Mes</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><th style='mso-number-format:"@"'><%=Consulta.getString(1)%></th><%}%>                       
                    </tr>
                    <tr>
                        <th>(+)Inv. Inicio Mes</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(2))%></td><%}%> 
                    </tr>
                    <tr>
                        <th>(+)Entrada Por Inventario Inicial</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(3))%></td><%}%>     
                    </tr>
                    <tr>
                        <th>(+)Entrada Por Compra</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(4))%></td><%}%> 
                    </tr>
                    <tr>
                        <th>(+)Entrada Por Devoluci&oacute;n</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(5))%></td><%}%> 
                    </tr>
                    <tr>                        
                        <th>(+)Entrada Por Otros Movimientos</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(6))%></td><%}%> 
                    </tr>
                    <tr>                        
                        <th>SubTotal Entradas</th><%Consulta = con.consulta("SELECT SUM(F_InvMes+F_InvIni+F_Compra+F_Devo+F_EoMov) FROM tb_invmescedisglobal GROUP BY F_Mes ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(1))%></td><%}%>  
                    </tr>
                    <tr></tr>
                    <tr>                        
                        <th>(-)Salida Por Facturaci&oacute;n</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(7))%></td><%}%> 
                    </tr>
                    <tr>
                        <th>(-)Salida Por Otros Movimientos</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(8))%></td><%}%> 
                    </tr>
                    <tr>                        
                        <th>SubTotal Salidas</th><%Consulta = con.consulta("SELECT SUM(F_SalFact+F_SalOtroMov) FROM tb_invmescedisglobal GROUP BY F_Mes ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(1))%></td><%}%> 
                    </tr>
                    <tr></tr>
                    <tr>
                        <th>Inv. Final Mes</th><%Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                            while (Consulta.next()) {%><td><%=formatter.format(Consulta.getInt(9))%></td><%}%>
                    </tr>                      
                </thead>
                <tbody>
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
                    <%
                            
                            con.cierraConexion();
                        } catch (Exception e) {

                        }


                    %>