<%--
    Marbete Manual — versión corregida.

    Cambios respecto al original (el detalle está en VALIDACION.md):
      1. marbeteManual.js se carga al final del <body>, después de jQuery.
         Estaba en el <head> y por eso daba "$ is not defined".
      2. contentType declara charset=UTF-8.
      3. El sendRedirect lleva return: antes el resto de la página se seguía
         ejecutando aunque no hubiera sesión.
      4. Las conexiones se cierran en finally.
      5. Las excepciones se registran en vez de descartarse.
      6. Los <option> de encabezado llevan value="" para que .val() devuelva
         cadena vacía y la validación del JS pueda detectarlos.
      7. Se quitó el bloque de jQuery UI autocomplete: apuntaba a #Clave, un
         input que está comentado, y embebía las 903 claves del catálogo en el
         HTML sin que nadie las usara.
--%>
<%@page import="com.medalfa.saa.utils.StaticText"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") == null) {
        // El return es indispensable. Sin él, sendRedirect solo marca la
        // respuesta y el JSP sigue corriendo: abre conexiones y arma el HTML
        // para alguien que no tiene sesión.
        response.sendRedirect("../index.jsp");
        return;
    }
    usua = (String) sesion.getAttribute("nombre");
    tipo = (String) sesion.getAttribute("Tipo");

    ConectionDB con = new ConectionDB();
%>
<!DOCTYPE html>
<html lang="es-MX">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS</title>
        <link href="../css/bootstrap.css" rel="stylesheet"/>
        <link href="../css/select2.css" rel="stylesheet" type="text/css"/>
        <link href="../css/sweetalert.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipal.jspf" %>

            <div class="panel panel-success">
                <div class="panel-heading">
                    Criterios de Búsqueda
                </div>

                <div class="panel-body">
                    <div class="row">

                        <label class="col-lg-1 col-md-1 col-sm-1" style="margin-top: 5px"
                               for="ProyecMar">Proyecto:</label>
                        <div class="col-lg-2 col-md-2 col-sm-2">
                            <select class="form-control" id="ProyecMar" name="ProyecMar">
                                <%-- value="" para que .val() devuelva "" y no null --%>
                                <option selected disabled>--Proyecto--</option>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rsetProy = con.consulta(
                                            "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_DesProy");
                                        while (rsetProy.next()) {
                                %>
                                <option value="<%= rsetProy.getInt("F_Id") %>"><%= rsetProy.getString("F_DesProy") %></option>
                                <%
                                        }
                                    } catch (Exception e) {
                                        // Antes era e.getMessage() a secas: se llamaba al método y se
                                        // tiraba el resultado, así que el error desaparecía sin rastro.
                                        log("Error al cargar tb_proyectos en Marbete_Manual.jsp", e);
                                    } finally {
                                        // En finally: si la consulta truena a medio camino, la conexión
                                        // igual se devuelve. Estando dentro del try se quedaba abierta.
                                        try { con.cierraConexion(); } catch (Exception ex) { }
                                    }
                                %>
                            </select>
                        </div>

                        <label class="col-lg-1 col-md-1 col-sm-1" style="margin-top: 5px"
                               for="ClaveMar">Producto:</label>
                        <div class="col-lg-2 col-md-2 col-sm-2">
                            <select class="form-control" id="ClaveMar" name="ClaveMar">
                                <option selected disabled>--Clave--</option>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rsetProd = con.consulta(
                                            "SELECT F_ClaPro FROM tb_lote GROUP BY F_ClaPro ORDER BY F_ClaPro");
                                        while (rsetProd.next()) {
                                            String claPro = rsetProd.getString("F_ClaPro");
                                %>
                                <option value="<%= claPro %>"><%= claPro %></option>
                                <%
                                        }
                                    } catch (Exception e) {
                                        log("Error al cargar productos en Marbete_Manual.jsp", e);
                                    } finally {
                                        try { con.cierraConexion(); } catch (Exception ex) { }
                                    }
                                %>
                            </select>
                        </div>

                        <label class="col-lg-1 col-md-1 col-sm-1" style="margin-top: 5px"
                               for="loteMar">Lote:</label>
                        <div class="col-lg-2 col-md-2 col-sm-2">
                            <select class="form-control" id="loteMar" name="loteMar">
                                <option selected disabled>--Lote--</option>
                                <%
                                    /*
                                     * Esta consulta trae TODOS los lotes de la tabla, sin filtrar por
                                     * el producto elegido, y agrupa por F_ClaLot: dos productos que
                                     * compartan clave de lote se funden en un solo renglón.
                                     *
                                     * Con los datos actuales son más de dos mil opciones y el usuario
                                     * puede elegir un lote que no corresponde al producto. Queda así
                                     * para no cambiar el comportamiento de tu pantalla en esta
                                     * corrección; el proyecto marbetes-global resuelve exactamente
                                     * esto cargando los lotes del producto seleccionado.
                                     */
                                    try {
                                        con.conectar();
                                        ResultSet rsetLot = con.consulta(
                                            "SELECT F_ClaLot FROM tb_lote GROUP BY F_ClaLot ORDER BY F_ClaLot");
                                        while (rsetLot.next()) {
                                            String claLot = rsetLot.getString("F_ClaLot");
                                %>
                                <option value="<%= claLot %>"><%= claLot %></option>
                                <%
                                        }
                                    } catch (Exception e) {
                                        log("Error al cargar lotes en Marbete_Manual.jsp", e);
                                    } finally {
                                        try { con.cierraConexion(); } catch (Exception ex) { }
                                    }
                                %>
                            </select>
                        </div>

                    </div>
                </div>

                <div class="panel-footer">
                    <button class="btn btn-block btn-success" type="button" id="generarMarbete">
                        Generar Marbetes
                    </button>
                </div>

            </div>

            <%@include file="../jspf/piePagina.jspf"%>
        </div>

        <%-- El orden importa: jQuery primero, los plugins después, y hasta el
             final el script de la pantalla, que usa a los tres. --%>
        <script src="../js/jquery-1.12.4.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/select2.js" type="text/javascript"></script>
        <script src="../js/sweetalert.min.js" type="text/javascript"></script>
        <script src="../js/marbetes/marbeteManual.js" type="text/javascript"></script>
    </body>
</html>
