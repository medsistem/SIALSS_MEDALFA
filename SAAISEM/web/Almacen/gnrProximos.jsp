<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
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
    
    HttpSession sesion = request.getSession();
    String usua = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String Proyecto = "", DesProyecto = "", Fecha ="";
    try {
        Proyecto = request.getParameter("Proyecto");
        DesProyecto = request.getParameter("DesProyecto");
        
    } catch (Exception e) {
        e.getMessage();
    }
    if (Proyecto == null) {
        Proyecto = "0";
    }
    if(Proyecto.equals("0")){
        DesProyecto = "TODOS";
    }else {
        DesProyecto = DesProyecto;
    }
    //Fecha="=HOY()";
    java.util.Date fecha = new Date();
    
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Proximos_a_caducar_"+DesProyecto+".xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <div>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <table>
                        <thead>
                            
                                <%            Date dNow = new Date();
                                    DateFormat ft = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
                                    String fechaDia = ft.format(dNow);
                                %>
                                <tr>
                                    <td><img src="https://4.bp.blogspot.com/-QPFcJij97lE/XkwwIpM6omI/AAAAAAAABcA/GwpuompAg60ucAtDnYPBGkf-A6SwPHAYwCLcBGAsYHQ/s1600/logoMdf.png" id="logoMdf"></td>
                                    <td colspan="7"><h4><%=fechaDia%></td>
                                </tr><tr></tr>
                                <tr>
                                    <th colspan="8"> <h1>Reporte proximos a caducar</h1></th>
                                </tr><tr></tr>                           
                            
                        </thead>
                    </table>
                </div>
            </div>
            <br />
           
            <div class="panel panel-success">
                 <div class="panel-footer">
                   
                        <div class="table table-responsive" style=" overflow:auto;">
                            <table class="table table-bordered table-striped" id="gnrtbProx" border="1">
                                <thead class="table-success">
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">CLAVE</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">DESCRIPCION</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">LOTE</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">CADUCIDAD</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">PRESENTACION</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">EXISTENCIA</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">UBICACION</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">ORIGEN</th>
                                <th style="color:white; width:auto; background-color:#00d1a7; font-family:Arial Black;">RE-UBICAR</th>
                              
                                </thead>
                                <tbody>
                                    <% try {
                                            con.conectar();
                                            
                                            try {
                                                ResultSet rs = null;
                                            rs = con.consulta("SELECT l.F_ClaPro, m.F_DesPro, l.F_ClaLot, l.F_FecCad, m.F_PrePro, l.F_ExiLot, u.F_DesUbi, o.F_DesOri, CASE 	WHEN u.F_DesUbi NOT LIKE '%-%'  THEN 'PROXACADUCAR' ELSE CONCAT(u.F_DesUbi,'-','PROXACADUCAR') END  FROM tb_lote AS l INNER JOIN tb_medica AS m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri INNER JOIN tb_proyectos AS pry ON l.F_Proyecto = pry.F_Id INNER JOIN tb_ubica AS u ON l.F_Ubica = u.F_ClaUbi WHERE l.F_ExiLot > 0 AND l.F_Ubica NOT IN ('CADUCADOS', 'MERMA', 'CROSSDOCKMORELIA', 'INGRESOS_V', 'CUARENTENA', 'AT', 'A0T', 'AT2', 'AT3', 'AT4', 'ATI', 'duplicado', 'CADUCADOSFARMACIA') AND l.F_Ubica NOT LIKE '%PROXACADUCAR%' AND l.F_FecCad BETWEEN CURDATE() AND (SELECT DATE_ADD(CURDATE() , INTERVAL 3 MONTH)) AND l.F_Proyecto = '"+ Proyecto +"' ORDER BY l.F_Origen ASC,l.F_ClaPro ASC,l.F_ClaLot ASC,l.F_FecCad ASC,l.F_Ubica ASC");     
                                              // rs = con.consulta("SELECT l.F_ClaPro, m.F_DesPro, l.F_ClaLot, l.F_FecCad, m.F_PrePro, l.F_ExiLot, l.F_Ubica, o.F_DesOri, 'PROXACADUCAR' FROM tb_lote AS l INNER JOIN tb_medica AS m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE l.F_ExiLot > 0 AND l.F_Ubica NOT IN ('CADUCADOS', 'MERMA', 'CROSSDOCKMORELIA', 'INGRESOS_V', 'CUARENTENA', 'AT', 'A0T', 'AT2', 'AT3', 'AT4', 'ATI', 'duplicado', 'CADUCADOSFARMACIA', 'PROXACADUCAR') AND l.F_FecCad BETWEEN CURDATE() AND (SELECT DATE_ADD(CURDATE() , INTERVAL 3 MONTH)) AND l.F_Proyecto = '"+ Proyecto +"' ORDER BY l.F_Origen ASC, l.F_ClaPro ASC, l.F_ClaLot ASC, l.F_FecCad ASC, l.F_Ubica ASC");
                                   
                                        while(rs.next()){    
                                    %>
                                    <tr>    
                                        <td style="mso-number-format:'@';"><%out.println(rs.getString(1));%></td>
                                        <td><%out.println(rs.getString(2));%></td>
                                        <td style="mso-number-format:'@';"><%out.println(rs.getString(3));%></td>
                                        <td><%out.println(rs.getString(4));%></td>
                                        <td><%out.println(rs.getString(5));%></td>
                                        <td><%out.println(rs.getString(6));%></td>
                                        <td><%out.println(rs.getString(7));%></td>
                                        <td><%out.println(rs.getString(8));%></td>
                                        <td><%out.println(rs.getString(9));%></td>
                                        
                                    </tr>
                                    <% 
                                            }//fin del while
                                            } catch (Exception e) {
                                                e.getMessage();
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                  
                </div>
        </div>
        <script>
            $(document).ready(function () {
                $('#gnrtbProx').dataTable();
                $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});
            }
        </script>
    </body>
</html>