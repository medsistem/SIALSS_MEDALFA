<%-- 
    Document   : gnkKardexClave
    Created on : 22-oct-2014, 8:39:49
    Author     : amerikillo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

    String fecha_ini = "", fecha_fin = "", clave = "", Concepto = "",  Proyec = "", Origeno = "", DesProyecto = "", Unidad="";
   int Proyecto = 0;
    try {
        //if (request.getParameter("accion").equals("buscar")) {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        clave = request.getParameter("clave");
        Concepto = request.getParameter("Concepto");
        Proyec = request.getParameter("Proyecto");
        Origeno = request.getParameter("Origen");
        Unidad = request.getParameter("Unidad");
        //}
    } catch (Exception e) {
        e.getMessage();
    }
    System.out.println(Origeno);
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

    if ( Proyec == null) {
         Proyec = "0";
    }

    if (Origeno == null) {
        Origeno = "0";
    }
    
    if( Proyec.equals("0")){
        DesProyecto = "TODOS";
    }else {
        DesProyecto = DesProyecto;
    }
   
    Proyecto = Integer.parseInt(Proyec);
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=\"Medicamento_" + Unidad + ".xls\"");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>-</title>
    </head>
    <body>
        <table border="1">
            <thead> 
                <tr>
                    <th>Clave Uni</th>
                    <th>Unidad</th>
                    <th>Clave</th>
                    <th>Clave SS</th>
                    <th>Nombre genérico</th>
                    <th>Descripción</th>
                    <th>Presentación</th>
                    <th>Cantidad</th>
                    <th>Proyecto</th>
                    <th>Origen</th>
                </tr>
            </thead>
            <tbody>
                <%
                    try {
                        con.conectar();
                         String FechaFol = "", Clave = "", Concep = "", Query = "", AND = "", ANDOrigen = "";
                                                    int ban = 0, ban1 = 0, ban2 = 0;

                                                    if (Origeno.equals("0")) {
                                                        ANDOrigen = "";
                                                    } else {
                                                        ANDOrigen = " AND l.F_Origen='" + Origeno + "' ";
                                                    }

                                                    if (Proyecto == 0) {
                                                        AND = "";
                                                    } else {
                                                        AND = " AND f.F_Proyecto='" + Proyecto + "' ";
                                                    }

                                                    if (clave != "") {
                                                        ban = 1;
                                                        Clave = "f.F_ClaPro='" + clave + "' ";
                                                    }
                                                    if (fecha_ini != "" && fecha_fin != "") {
                                                        ban1 = 1;
                                                        FechaFol = " f.F_FecApl between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                                                         }
                                                    if (Unidad != "") {
                                                        ban2 = 1;
                                                        Concep = "f.F_ClaCli = '" + Unidad+ "'";
                                                    }
                                                    if (ban == 1 && ban1 == 1 && ban2 == 1) {
                                                        Query = Clave + " AND " + FechaFol + " AND " + Concep;
                                                     } else if (ban == 1 && ban1 == 1) {
                                                        Query = Clave + " AND " + FechaFol;
                                                       } else if (ban == 1 && ban2 == 1) {
                                                        Query = Clave + " AND " + Concep;
                                                    } else if (ban1 == 1 && ban2 == 1) {
                                                        Query = FechaFol + " AND " + Concep;
                                                     } else if (ban == 1) {
                                                        Query = Clave;
                                                    } else if (ban1 == 1) {
                                                        Query = FechaFol;
                                                     } else if (ban2 == 1) {
                                                        Query = Concep;
                                                    }

                                    ResultSet rset = null;
                                    rset = con.consulta("(SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "   AND f.F_StsFact = 'A'  " + ANDOrigen + " AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli, f.F_ClaPro) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro IN ('999242','602310641') AND f.F_StsFact = 'A'   " + ANDOrigen + "  AND f.F_CantSur > 0 " + AND + " GROUP BY f.F_ClaCli )  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250743','999241','99924 ','7999248') AND f.F_StsFact = 'A' " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ( '250739','604390088 ','604390070')  AND f.F_StsFact = 'A' " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + "  GROUP BY f.F_ClaCli) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250738',' 604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A' " + ANDOrigen + "   AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)");
                                    System.out.println("(SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "   AND f.F_StsFact = 'A'  " + ANDOrigen + " AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli, f.F_ClaPro) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro IN ('999242','602310641') AND f.F_StsFact = 'A'   " + ANDOrigen + "  AND f.F_CantSur > 0 " + AND + " GROUP BY f.F_ClaCli )  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250743','999241','99924 ','7999248') AND f.F_StsFact = 'A' " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ( '250739','604390088 ','604390070')  AND f.F_StsFact = 'A' " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + "  GROUP BY f.F_ClaCli) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250738',' 604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A' " + ANDOrigen + "   AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)"); 
                                    System.out.println(ANDOrigen); 
                                    String claves="", clavess="";
                                    while (rset.next()) {
                                    
                                    claves = rset.getString(3);
                                    clavess = rset.getString(4);
                                    
                                    if(claves.equals("250738") || claves.equals("604560623") || claves.equals("604560649") || claves.equals("604560631") || claves.equals("604560656") || claves.equals("604560672") || claves.equals("604560664") || claves.equals("999244") || claves.equals("999245") || claves.equals("999246")){      
                                      claves = "Guantes";
                                      clavess = "Guantes";
                                    }else if(claves.equals("250743") || claves.equals("999241") || claves.equals("99924") || claves.equals("7999248")){      
                                      claves = "Cubrebocas";
                                      clavess = "Cubrebocas";
                                    }else if(claves.equals("250739") || claves.equals("604390088") || claves.equals("604390070")){      
                                      claves = "Gorro";
                                      clavess = "Gorro";
                                    }else if(claves.equals("999242") || claves.equals("602310641")){      
                                      claves = "Bata";
                                      clavess = "Bata";
                                    }
                %>
                <tr>
                                <td><small><%=rset.getString(1)%></small></td>
                                <td><small><%=rset.getString(2)%></small></td>
                                <td style="mso-number-format:'@';"><small><%=claves%></small></td>
                                <td style="mso-number-format:'@';"><small><%=clavess%></small></td>
                                <td class="text-center"><small><%=rset.getString(9)%></small></td>
                                <td><small><%=rset.getString(5)%></small></td>
                                <td class="text-center"><small><%=rset.getString(10)%></small></td>
                                <td class="text-center"><small><%=rset.getString(6)%></small></td>
                                <td class="text-center"><small><%=rset.getString(7)%></small></td>
                                <td class="text-center"><small><%=rset.getString(8)%></small></td>
                </tr>
                <%
                        }
                        con.cierraConexion();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                %>
            </tbody>
        </table>        
    </body>
</html>
