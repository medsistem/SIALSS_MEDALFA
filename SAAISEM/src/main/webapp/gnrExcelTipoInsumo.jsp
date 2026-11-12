<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%
    HttpSession sesion = request.getSession();
    String usua = "";
    int Total = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String tipoInsumo = request.getParameter("tipoIns");

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Reporte de insumo" + tipoInsumo + ".xls");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <div>
        <div class="panel panel-primary">
            <div class="panel-body">
                <%
                    Date dNow = new Date();
                    DateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaDia = ft.format(dNow);
                %>
                <table>
                    <tr>
                        <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="LogoMdf"</td>
                        <td colspan="2" align="rigth" style="font-family:Arial Black;" ><%=fechaDia%></td>
                    </tr><tr></tr><tr></tr>
                    <tr>
                        <th colspan="3"><h1>Reporte de insumo <%=tipoInsumo%></h1></th>
                    </tr>                
                    <tr></tr>            
                </table>
                <table class="table table-bordered table-striped" id="tableInsumo" border="1">
                    <thead>
                        <tr>
                            <td style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">Clave</td>
                            <td style="color:white; background-color:#00d1a7; font-family:Arial Black;">Descripción</td>                        
                            <td style="color:white; background-color:#00d1a7; font-family:Arial Black;">Presentación</td>
                            <% if (tipoInsumo.equals("Oncologico")) { %>
                            <td style="color:white; background-color:#00d1a7; font-family:Arial Black;">Clasificación</td>
                            <% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            try {
                                con.conectar();
                                try {
                                    con.conectar();
                                    ResultSet rset = null;
                                    if (tipoInsumo.equals("Controlado")) {
                                        rset = con.consulta("SELECT c.F_ClaPro, SUBSTRING(m.F_DesPro, 1, 150), m.F_PrePro FROM tb_controlados AS c INNER JOIN tb_medica AS m ON c.F_ClaPro = m.F_ClaPro; ");
                                    } else if ((tipoInsumo.equals("Red_Fria"))) {
                                        rset = con.consulta("SELECT rf.F_ClaPro, SUBSTRING(m.F_DesPro, 1, 150), m.F_PrePro FROM tb_redfria AS rf INNER JOIN tb_medica AS m ON rf.F_ClaPro = m.F_ClaPro; ");
                                    } else if ((tipoInsumo.equals("APE"))) {
                                        rset = con.consulta("SELECT APE.F_ClaPro, SUBSTRING(m.F_DesPro, 1, 150), m.F_PrePro FROM tb_ape AS APE INNER JOIN tb_medica AS m ON APE.F_ClaPro = m.F_ClaPro; ");
                                    } else if ((tipoInsumo.equals("Oncologico"))) {
                                        rset = con.consulta("SELECT o.F_ClaPro, m.F_DesPro, m.F_PrePro, o.F_TipIns FROM tb_onco AS o INNER JOIN tb_medica AS m ON o.F_ClaPro = m.F_ClaPro; ");
                                    }
                                    while (rset.next()) {
                        %>
                        <tr>                                        
                            <td style='mso-number-format:"@"'><%=rset.getString(1)%></td>
                            <td><%=rset.getString(2)%></td>
                            <td><%=rset.getString(3)%></td>
                            <% if(tipoInsumo.equals("Oncologico")) { %>
                            <td><%=rset.getString(4)%></td>
                            <% } %>
                        </tr>
                        <%
                                    }
                                } catch (Exception e) {
                                    con.cierraConexion();
                                }
                            } catch (Exception e) {
                                con.cierraConexion();
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</html>