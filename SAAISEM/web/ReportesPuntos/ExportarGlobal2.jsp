<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.*" %>
<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.lang.*" import="java.util.*" import= "javax.swing.*" import="java.io.*" import="java.text.DateFormat" import="java.text.ParseException" import="java.text.SimpleDateFormat" import="java.util.Calendar" import="java.util.Date"  import="java.text.NumberFormat" import="java.util.Locale" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns:v="urn:schemas-microsoft-com:vml"
xmlns:o="urn:schemas-microsoft-com:office:office"
xmlns:x="urn:schemas-microsoft-com:office:excel"
xmlns="http://www.w3.org/TR/REC-html40">
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
        FolCon = request.getParameter("Folio");
    } catch (Exception e) {
    }
    ConectionDB con = new ConectionDB();

    
    
    try {
        con.conectar();
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename="+FolCon+".xls");
       
%>
<div>    
    <br />
    <div class="panel panel-success">
        <div class="panel-body">
            <table class="table table-bordered table-striped" id="datosCompras" border="1">
                <thead>
                    <tr>
                        <td>Fecha Pedido</td>
                        <td>Fecha Entrega</td>
                        <td>Clave SAP</td>
                        <td>Cantidad</td>
                        <td>Lote</td>
                        <td>Periodo</td>
                        <td>Secuencial</td>
                        <td>Reporte MEDALFA</td>
                        <td>Tipo de Venta</td>
                        <td>Pob Abi/Seg Pop</td>
                        <td>Tipo Medicamento</td>
                        <td>Clave Unidad</td>
                        <td>Clave MEDALFA</td>
                    </tr>
                </thead>
                <tbody>
                    <%
                        
                                    ResultSet Reportes = con.consulta("SELECT * FROM tb_imprepconglobalote WHERE F_Reporte='"+FolCon+"'");
                                    while(Reportes.next()){
                    %>
                    <tr>                        
                        <td><%=Reportes.getString(2)%></td>
                        <td><%=Reportes.getString(3)%></td>
                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=Reportes.getString(4)%></td>
                        <td><%=Reportes.getString(5)%></td>
                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=Reportes.getString(6)%></td>
                        <td><%=Reportes.getString(7)%></td>
                        <td><%=Reportes.getString(8)%></td>
                        <td><%=Reportes.getString(9)%></td>
                        <td><%=Reportes.getString(10)%></td>
                        <td><%=Reportes.getString(11)%></td>
                        <td><%=Reportes.getString(12)%></td>
                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=Reportes.getString(13)%></td>
                        <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=Reportes.getString(14)%></td>
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
       