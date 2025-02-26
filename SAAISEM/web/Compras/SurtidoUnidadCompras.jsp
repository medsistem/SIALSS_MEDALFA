<%-- 
    Document   : requerimiento.jsp
    Created on : 17/02/2014, 03:34:46 PM
    Author     : MEDALFA
--%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%
   HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();

    String fecha_ini = "", fecha_fin = "", clave = "", ClaCli = "", Proyec = "", Origen = "", SelectUnidad = "", DesProyecto ="";
    int Proyecto = 0;
    try {
        fecha_ini = request.getParameter("fecha_ini");
        fecha_fin = request.getParameter("fecha_fin");
        clave = request.getParameter("clave");
        ClaCli = request.getParameter("conceptos");
        Proyec = request.getParameter("Proyecto");
        Origen = request.getParameter("Origen");
        SelectUnidad = request.getParameter("SelectUnidad");
    } catch (Exception e) {
        e.getMessage();
    }
    if (fecha_ini == null) {
        fecha_ini = "";
    }
    if (fecha_fin == null) {
        fecha_fin = "";
    }
    if (clave == null) {
        clave = "";
    }
    if (ClaCli == null) {
        ClaCli = "";
    }
    if (Proyec == null) {
        Proyec = "0";
    }
    if (Origen == null) {
        Origen = "0";
    }
    if (SelectUnidad == null) {
        SelectUnidad = "";
    }
    Proyecto = Integer.parseInt(Proyec);
    try {
        con.conectar();
        ResultSet rset = con.consulta("SELECT F_DesProy FROM tb_proyectos WHERE F_Id='" + Proyecto + "' ;");
        while (rset.next()) {
            DesProyecto = rset.getString(1);
        }
        con.cierraConexion();
    } catch (Exception e) {
        out.println(e.getMessage());
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <link rel="stylesheet" type="text/css" href="../css/select2.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipalCompra.jspf" %>
            <hr/>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Global de Insumo por Unidad</h3>
                </div>
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formUnidadCompra" id="formUnidadCompra" method="post" action="SurtidoUnidadCompras.jsp">

                        <div class="row">
                            <div class="form-group">
                                <label class="control-label col-sm-1" ></label>
                                <!--div class="col-lg-2">
                                    <input class="form-control" id="clave" name="clave" type="text" value=""  />
                                </div-->                    
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_ini" name="fecha_ini" type="date" />
                                </div>
                                <div class="col-lg-2">
                                    <input class="form-control" id="fecha_fin" name="fecha_fin" type="date"/>
                                </div>
                                <div class="col-sm-3">
                                    <select class="form-control" name="Proyecto" id="Proyecto">
                                        <option value="0">-Seleccione Proyecto-</option>
                                        <%
                                            try {
                                                con.conectar();
                                                ResultSet rset = con.consulta("SELECT * FROM tb_proyectos;");
                                                while (rset.next()) {
                                        %>
                                        <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                        <%
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {
                                                out.println(e.getMessage());
                                            }
                                        %>
                                    </select>
                                </div>
                                <div class="col-sm-2">
                                    <select class="form-control" name="Origen" id="Origen">
                                        <option value="0">-Selec Origen-</option>
                                        <%
                                            try {
                                                con.conectar();
                                                ResultSet rset = con.consulta("SELECT * FROM tb_origen;");
                                                while (rset.next()) {
                                        %>
                                        <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                        <%
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {
                                                out.println(e.getMessage());
                                            }
                                        %>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="Clave" class="col-xs-1 control-label">Unidad</label>
                                <div class="col-sm-6">
                                    <select name="SelectUnidad" id="SelectUnidad" class="form-control">
                                        <option value="0">--Seleccione--</option>
                                        <%
                                            try {
                                                con.conectar();
                                                ResultSet rset = null;
                                                rset = con.consulta("SELECT F_ClaCli ,F_NomCli FROM tb_uniatn ;");
                                                while (rset.next()) {
                                        %>
                                        <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                        <%
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        %>
                                    </select>
                                </div>
                                <div class="col-sm-2">
                                    <button name="btnUnidad" id="btnUnidad" class="btn btn-block btn-warning form-control glyphicon glyphicon-search">  Buscar</button>
                                </div>
                                <%if (!(SelectUnidad.equals("0"))) {%>
                                <div class="col-sm-2">
                                    <a href="gnrUnidadGlobal.jsp?Unidad=<%=SelectUnidad%>&Origen=<%=Origen%>&fecha_ini=<%=fecha_ini%>&fecha_fin=<%=fecha_fin%>&Proyecto=<%=Proyecto%>" target="_black" class="btn btn-block btn-success form-control glyphicon glyphicon-file"> Exportar</a>
                                </div>

                                <%}%>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="panel-footer table-responsive">
                    <table class="table table-striped table-bordered" id="datosMedicamento">
                        <thead>
                            <tr>
                                <th>ClaveU</th>
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

                                                    if (Origen.equals("0")) {
                                                        ANDOrigen = "";
                                                    } else {
                                                        ANDOrigen = " AND l.F_Origen='" + Origen + "' ";
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
                                                         //FechaFol2 = "f.F_FecApl between  DATE_SUB('" + fecha_ini + "', INTERVAL 1 DAY) and DATE_SUB( '" + fecha_fin + "', INTERVAL 1 DAy) ";
                                                    }
                                                    if (SelectUnidad != "") {
                                                        ban2 = 1;
                                                        Concep = "f.F_ClaCli = '" + SelectUnidad + "'";
                                                    }
                                                    if (ban == 1 && ban1 == 1 && ban2 == 1) {
                                                        Query = Clave + " AND " + FechaFol + " AND " + Concep;
                                                        //Query2 = Clave + " AND " + FechaFol2 + " AND " + Concep;
                                                    } else if (ban == 1 && ban1 == 1) {
                                                        Query = Clave + " AND " + FechaFol;
                                                       // Query2 = Clave + " AND " + FechaFol2;
                                                    } else if (ban == 1 && ban2 == 1) {
                                                        Query = Clave + " AND " + Concep;
                                                    } else if (ban1 == 1 && ban2 == 1) {
                                                        Query = FechaFol + " AND " + Concep;
                                                       // Query2 = FechaFol2 + " AND " + Concep;
                                                    } else if (ban == 1) {
                                                        Query = Clave;
                                                    } else if (ban1 == 1) {
                                                        Query = FechaFol;
                                                       // Query2 = FechaFol2;
                                                    } else if (ban2 == 1) {
                                                        Query = Concep;
                                                    }

                                    ResultSet rset = null;
                                    rset = con.consulta("(SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro NOT IN ('999242','602310641', '250743','999241','99924','7999248','250739','604390088','604390070','250738','604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A'  " + ANDOrigen + " AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli, f.F_ClaPro) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro IN ('999242','602310641') AND f.F_StsFact = 'A'   " + ANDOrigen + "  AND f.F_CantSur > 0 " + AND + " GROUP BY f.F_ClaCli )  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250743','999241','99924 ','7999248') AND f.F_StsFact = 'A'      " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ( '250739','604390088 ','604390070')  AND f.F_StsFact = 'A'    " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + "  GROUP BY f.F_ClaCli) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri, m.F_PrePro, m.F_NomGen FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250738',' 604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A'   " + ANDOrigen + "   AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)");
                                    System.out.println("(SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro NOT IN ('999242','602310641', '250743','999241','99924','7999248','250739','604390088','604390070','250738','604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A'  " + ANDOrigen + " AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli, f.F_ClaPro) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE  " + Query + "  AND f.F_ClaPro IN ('999242','602310641') AND f.F_StsFact = 'A'   " + ANDOrigen + "  AND f.F_CantSur > 0 " + AND + " GROUP BY f.F_ClaCli )  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250743','999241','99924 ','7999248') AND f.F_StsFact = 'A'      " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)  UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ( '250739','604390088 ','604390070')  AND f.F_StsFact = 'A'    " + ANDOrigen + "  AND f.F_CantSur > 0  " + AND + "  GROUP BY f.F_ClaCli) UNION (SELECT f.F_ClaCli, atn.F_NomCli, f.F_ClaPro, m.F_ClaProSS, m.F_DesPro, Sum(f.F_CantSur), p.F_DesProy, o.F_DesOri FROM tb_factura AS f INNER JOIN tb_uniatn AS atn ON f.F_ClaCli = atn.F_ClaCli INNER JOIN tb_proyectos AS p ON f.F_Proyecto = p.F_Id INNER JOIN tb_medica AS m ON f.F_ClaPro = m.F_ClaPro INNER JOIN tb_lote AS l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica AND f.F_Proyecto = l.F_Proyecto INNER JOIN tb_origen AS o ON l.F_Origen = o.F_ClaOri WHERE " + Query + "  AND f.F_ClaPro IN ('250738',' 604560623','604560649','604560631','604560656','604560672','604560664','999244','999245','999246')  AND f.F_StsFact = 'A'   " + ANDOrigen + "   AND f.F_CantSur > 0  " + AND + " GROUP BY f.F_ClaCli)");
                                  
                                     String claves="", clavess="";
                                    while (rset.next()) {
                                    
                                    claves = rset.getString(3);
                                    clavess = rset.getString(4);
                                    //cant = Integer.parseInt(rset.getString(6));
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
                                <td class="text-center"><small><%=rset.getString(1)%></small></td>
                                <td><small><%=rset.getString(2)%></small></td>
                                <td class="text-center"><small><%=claves%></small></td>
                                <td class="text-center"><small><%=clavess%></small></td>
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
                </div>
            </div>
        </div>
        <%@include file="../jspf/piePagina.jspf" %>
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="../js/jquery-1.9.1.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/jquery.dataTables.js"></script>
        <script src="../js/dataTables.bootstrap.js"></script>
        <script src="../js/select2.js"></script>
        <script>
            $(document).ready(function () {
                $('#datosMedicamento').dataTable();
                $('#SelectUnidad').select2();
            });
        </script>
    </body>
</html>



