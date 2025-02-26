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

    String Unidad = "", Folio1 = "", Folio2 = "", Fec1 = "", Fec2 = "", Proyecto = "";
    try {
        Unidad = request.getParameter("Unidad");
        Folio1 = request.getParameter("Folio1");
        Folio2 = request.getParameter("Folio2");
        Fec1 = request.getParameter("Fec1");
        Fec2 = request.getParameter("Fec2");
        Proyecto = request.getParameter("Proyecto");
    } catch (Exception e) {

    }

    if (Unidad == null) {
        Unidad = "";
    }
    if (Folio1 == null) {
        Folio1 = "";
    }
    if (Folio2 == null) {
        Folio2 = "";
    }
    if (Fec1 == null) {
        Fec1 = "Lote";
    }
    if (Fec2 == null) {
        Fec2 = "Lote";
    }
    if (Proyecto == null) {
        Proyecto = "0";
    }

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Imprimir_folios.xls\"");
%>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>

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
                    <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"</td>
                    <td colspan="3"><h4><%=fechaDia%></h4></td>
                </tr><tr></tr>
                <tr>
                    <th colspan="4"><h1>Multiples Remisiones</h1></th>
                </tr><tr></tr>
            </table>
            <table class="table table-bordered table-striped" id="datosCompras" border="1">
                <thead>
                    <tr>
                        <th>PROYECTO</th>
                        <th>No. FOLIO</th>
                        <th>NOMBRE UNIDAD</th>
                        <th>FECHA ENTREGA</th>
                        <th>CLAVES</th>
                        <th>PIEZAS</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        try {
                            con.conectar();
                            try {
                                String QUni = "", QFolio = "", QFecha = "", Query = "", AND = "";
                                int ban = 0, ban2 = 0, ban3 = 0;
                                if (Proyecto.equals("0")) {
                                    AND = "";
                                } else {
                                    AND = " AND u.F_Proyecto='" + Proyecto + "' ";
                                }
                                if (Unidad != "") {
                                    ban = 1;
                                }
                                if (Folio1 != "" && Folio2 != "") {
                                    ban2 = 1;
                                }
                                if (Fec1 != "" && Fec2 != "") {
                                    ban3 = 1;
                                }
                                if (ban == 1) {
                                    QUni = " F.F_ClaCli = '" + Unidad + "' ";
                                }
                                if (ban2 == 1) {
                                    if (ban == 0) {
                                        QFolio = " F_ClaDoc between '" + Folio1 + "' and '" + Folio2 + "' ";
                                    } else {
                                        QFolio = " AND F_ClaDoc between '" + Folio1 + "' and '" + Folio2 + "' ";
                                    }
                                }
                                if (ban3 == 1) {
                                    if (ban == 0 && ban2 == 0) {
                                        QFecha = " F_FecEnt between '" + Fec1 + "' and '" + Fec2 + "' ";
                                    } else {
                                        QFecha = " AND F_FecEnt between '" + Fec1 + "' and '" + Fec2 + "' ";
                                    }
                                }

                                Query = QUni + QFolio + QFecha;
                                ResultSet rset = con.consulta("SELECT f.F_ClaDoc,f.F_ClaCli,CONCAT(f.F_ClaCli,' - ',F_NomCli) AS F_NomCli,DATE_FORMAT(F_FecApl,'%d/%m/%Y') AS F_FecApl,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt,F_StsFact, p.F_DesProy, Count(f.F_ClaDoc),Sum(f.F_CantSur) FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id WHERE  " + Query + " AND F_StsFact='A' AND f.F_CantSur > 0 " + AND + " GROUP BY f.F_ClaCli,F_StsFact,p.F_DesProy,f.F_ClaDoc ORDER BY F_ClaDoc+0 ASC;");
                                System.out.println(rset);
                                while (rset.next()) {


                    %>
                <td><%=rset.getString(7)%></td>
                <td><%=rset.getString(1)%></td>
                <td><%=rset.getString(3)%></td>
                <td><%=rset.getString("F_FecEnt")%></td>
                 <td><%=rset.getString(8)%></td>
                  <td><%=rset.getString(9)%></td>
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