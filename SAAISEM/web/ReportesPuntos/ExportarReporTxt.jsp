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
    String usua = "",F_User="",FolCon1="";
    String FolCon="",Reporte="";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    try {
        FolCon = request.getParameter("User");
    } catch (Exception e) {
    }
    ConectionDB con = new ConectionDB();

    
    
    try {
        con.conectar();
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=ReporteadorTxt.xls");
       
%>
<div>    
    <br />
    <div class="panel panel-success">
        <div class="panel-body">
            <table class="table table-bordered table-striped" id="datosCompras" border="1">
                <thead>
                    <tr>
                        <td>Clave</td>
                        <td>Descripción</td>
                        <td>Can/Req</td>
                        <td>Canr/Sur</td>
                        <td>Cos/Tot</td>
                        <td>Cos/Ser</td>
                        <td>Iva</td>
                        <td>Total</td>
                        <td>Pzs No/Sur</td>
                        <td>Cos No/Sur</td>
                    </tr>
                </thead>
                <tbody>
                    <%
                        
                        ResultSet rset = con.consulta("SELECT F_Clave,F_Nombre,FORMAT(F_CanReq,0) AS F_CanReq,FORMAT(F_CanSur,0) AS F_CanSur,"
                        + "FORMAT(F_CostTotal,2) AS F_CostTotal,FORMAT(F_CostServ,2) AS F_CostServ,FORMAT(F_Iva,2) AS F_Iva,FORMAT(SUM(F_Iva+F_CostTotal),2) AS f_total,"
                        + "FORMAT(CASE WHEN (F_PzNoSurt<0) THEN F_PzNoSurt*-1 WHEN (F_PzNoSurt>=0) THEN F_PzNoSurt END,0) AS F_PzNoSurt,"
                                + "FORMAT(CASE WHEN (F_CostNoSurt<0) THEN F_CostNoSurt*-1 WHEN (F_CostNoSurt>=0) THEN F_CostNoSurt END,2) AS F_CostNoSurt FROM tb_txtreporte WHERE F_User='"+usua+"' GROUP BY F_Id; ");
                        while (rset.next()) {
                    %>
                    <tr>                        
                        <td><%=rset.getString(1)%></td>
                        <td><%=rset.getString(2)%></td>
                        <td><%=rset.getString(3)%></td>
                        <td><%=rset.getString(4)%></td>
                        <td><%=rset.getString(5)%></td>
                        <td><%=rset.getString(6)%></td>
                        <td><%=rset.getString(7)%></td>
                        <td><%=rset.getString(8)%></td>
                        <td><%=rset.getString(9)%></td>
                        <td><%=rset.getString(10)%></td>
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
       