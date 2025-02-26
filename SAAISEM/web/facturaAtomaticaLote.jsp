<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<%@page import="Facturacion.FacturacionManual" %>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%
    FacturacionManual fact = new FacturacionManual();
    HttpSession sesion = request.getSession();
    String F_IndGlobal = (String) sesion.getAttribute("F_IndGlobal");
    String usua = "", tipo = "", Desproyecto = "";
    int Proyecto = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String ClaCli = "", FechaEnt = "", ClaPro = "", DesPro = "";

    try {
        ClaCli = request.getParameter("ClaCli");
    } catch (Exception e) {

    }

    if (ClaCli == null) {
        ClaCli = "";
    }
    try {

        con.conectar();
        ResultSet rsetProy = con.consulta("SELECT P.F_Id, IFNULL(P.F_DesProy, '') AS Proyecto FROM tb_parametrousuario PU LEFT JOIN ( SELECT F_Id, F_DesProy FROM tb_proyectos ) P ON PU.F_Proyecto = P.F_Id WHERE PU.F_Usuario = '" + usua + "';");
        if (rsetProy.next()) {
            Proyecto = rsetProy.getInt(1);
            Desproyecto = rsetProy.getString(2);
        }

        ResultSet rset = con.consulta("select F_IdFact, F_StsFact, F_ClaCli, F_FecEnt, F_User from tb_facttemplote where F_User = '" + usua + "' and F_ClaCli = '" + ClaCli + "';");
        rset.last();
        //while (rset.next()) {
        if (rset.getString("F_StsFact").equals("3") && rset.getString("F_User").equals(usua)) {
            sesion.setAttribute("F_IndGlobal", rset.getString(1));
            F_IndGlobal = (String) sesion.getAttribute("F_IndGlobal");
            ClaCli = rset.getString("F_ClaCli");
            sesion.setAttribute("ClaCliFM", ClaCli);
            FechaEnt = rset.getString("F_FecEnt");
        }
        con.cierraConexion();
    } catch (Exception e) {
    }

    if (F_IndGlobal == null) {
        F_IndGlobal = "0";
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="jspf/menuPrincipal.jspf" %>

            <div class="row">
                <div class="col-sm-12">
                    <h2>Facturación Lote</h2>
                </div>
            </div>
            <hr/>
            <form action="facturaAtomaticaLote.jsp" method="post">
                <h4 class="text-muted">Folio: <%=F_IndGlobal%></h4>
                <div class="row">
                    <div class="col-sm-1">
                        <h4>Unidad:</h4>
                    </div>
                    <input type="hidden" class="form-control" name="ClaCliSelect" id="ClaCliSelect" value="<%=ClaCli%>" />
                    <div class="col-sm-4">
                        <select class="form-control" name="ClaCli" id="ClaCli"  >
                            <option>--Seleccione--</option>
                            <%
                                try {
                                    con.conectar();
                                    ResultSet rset = con.consulta("SELECT F.F_ClaCli, F_NomCli FROM tb_facttemplote F INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli WHERE F.F_StsFact = 3 GROUP BY F.F_ClaCli;");
                                    while (rset.next()) {
                            %>
                            <option value="<%=rset.getString(1)%>"
                                    <%
                                        if (rset.getString(1).equals(ClaCli)) {
                                            out.println("selected");
                                        }
                                    %>
                                    ><%=rset.getString(2)%></option>
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
                        <button class="form-control btn btn-block btn-primary" type="submit">Buscar</button>
                    </div>
                    <div class="col-sm-1">
                        <h4>Proyecto</h4>
                    </div>
                    <div class="col-sm-2">
                        <input type="text" readonly="" class="form-control" name="DesProyecto" id="DesProyecto" value="<%=Desproyecto%>"/>
                        <input type="hidden" class="form-control" name="Proyecto" id="Proyecto" value="<%=Proyecto%>"/>
                    </div>
                </div>
                <table class="table table-condensed table-striped table-bordered table-responsive">
                    <tr>
                        <td>CLAVE:</td>
                        <td>Lote</td>
                        <td>Caducidad</td>
                        <td>Ubicación</td>
                        <td>Marca</td>
                        <td>Cantidad</td>
                        <td>Proyecto</td>
                    </tr>
                    <%
                        int banBtn = 0, Total = 0;
                        try {
                            con.conectar();
                            ResultSet rset = con.consulta("SELECT l.F_ClaPro, l.F_ClaLot, DATE_FORMAT(l.F_FecCad, '%d/%m/%Y'), f.F_Cant, l.F_Ubica, f.F_IdFact, mar.F_DesMar, f.F_Id, p.F_DesProy FROM tb_facttemplote f INNER JOIN tb_lote l ON f.F_IdLot = l.F_IdLote INNER JOIN tb_medica m ON m.F_ClaPro = l.F_ClaPro INNER JOIN tb_marca mar ON l.F_ClaMar = mar.F_ClaMar INNER JOIN tb_proyectos p ON l.F_Proyecto = p.F_Id WHERE F_ClaCli = '" + ClaCli + "' and F_StsFact=3;");
                            while (rset.next()) {
                                banBtn = 1;
                                Total = Total + rset.getInt(4);
                    %>
                    <tr>
                        <td><%=rset.getString(1)%></td>
                        <td><%=rset.getString(2)%></td>
                        <td><%=rset.getString(3)%></td>
                        <td><%=rset.getString(5)%></td>
                        <td><%=rset.getString("F_DesMar")%></td>
                        <td><%=rset.getString(4)%></td>
                        <td><%=rset.getString(9)%></td>
                    </tr>
                    <%
                            }
                            con.cierraConexion();
                        } catch (Exception e) {
                            out.println(e.getMessage());
                        }
                    %>
                </table>
                <%
                    if (banBtn == 1) {
                %>

                <div class="row">
                    <h4 class="col-sm-2">Total Solicitado</h4>
                    <div class="col-sm-2">
                        <input class="form-control" name="Total" id="Total" value="<%=Total%>" readonly="" />
                    </div>
                    <h4 class="col-sm-2">Observaciones</h4>
                    <div class="col-sm-5">
                        <textarea class="form-control" name="obs" id="obs"></textarea>
                    </div>
                </div>
                <br />
                <div class="row">
                    <div class="col-sm-2">
                        <h4>Fecha de Entrega</h4>
                    </div>
                    <div class="col-sm-2">
                        <input type="date" class="form-control" name="FechaEnt" id="FechaEnt" value="<%=FechaEnt%>" min=""/>
                    </div>
                    <h4 class="col-sm-1">OC</h4>
                    <div class="col-sm-2">
                        <input class="form-control" name="OC" id="OC" type="text" />
                    </div>
                    <h4 class="col-sm-1">Tipo</h4>
                    <div class="col-sm-2">
                        <select class="form-control" name="F_Tipo" id="Tipo">
                            <option>Ordinario</option>
                            <option>Complemento</option>
                            <option>Apoyo</option>
                            <option>Programa</option>
                            <option>Urgente</option>
                            <option>Extemporaneo</option>
                            <option>Tuberculosis</option>
                            <option>Diabetes</option>
                            <option>Cancer De Máma</option>
                            <option>Lepra</option>
                            <option>Dengue Y Paludismo</option>
                            <option>Colera</option>
                        </select>
                    </div>
                </div>
                <br/>
                <div class="row">
                    <div class="col-sm-12">
                        <button class="btn btn-block btn-success" name="accion" type="button" id="BtnConfirmar" value="ConfirmarFactura">Confirmar Factura</button>
                        <!--button class="btn btn-block btn-success" name="accion" id="BtnConfirmar" value="ConfirmarFactura" onclick="return confirm('Seguro de confirmar la Factura?')">Confirmar Factura</button-->
                    </div>
                    <!--div class="col-sm-6">
                        <button class="btn btn-block btn-success" name="accion" value="CancelarFactura" onclick="return confirm('Seguro de CANCELAR la Factura?')">Cancelar Factura</button>
                    </div-->
                </div>

                <%
                    }
                %>

            </form>
        </div>
        <%@include file="jspf/piePagina.jspf" %>
        <!-- 
    ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/jquery-ui-1.10.3.custom.js"></script>
        <script src="js/jquery.dataTables.js"></script>
        <script src="js/dataTables.bootstrap.js"></script>
        <script src="js/bootstrap-datepicker.js"></script>
        <script src="js/jquery.alphanum.js" type="text/javascript"></script>
        <script src="js/funcIngresos.js"></script>
        <script src="js/facturajs/Facturacionjslote.js"></script>
        <script src="js/sweetalert.min.js" type="text/javascript"></script>    
        <script>

            $(document).ready(function () {
                var Clave = $("#ClaveSel").val();
                if (Clave != "") {
                    $("#Cantidad").focus();
                } else {
                    $("#ClaPro").focus();
                }
            });
            $(function () {
                $("#ClaCli").keyup(function () {
                    var nombre = $("#ClaCli").val();

                    $("#ClaCli").autocomplete({
                        source: "JQInvenCiclico?accion=BuscarUnidad&nom_uni=" + nombre,
                        select: function (event, ui) {
                            $("#ClaCli").val(ui.item.nom_com);
                            return false;
                        }
                    }).data("ui-autocomplete")._renderItem = function (ul, item) {
                        return $("<li>")
                                .data("ui-autocomplete-item", item)
                                .append("<a>" + item.nom_com + "</a>")
                                .appendTo(ul);
                    };
                });
            });

            var today = new Date().toISOString().split('T')[0];
            document.getElementsByName("FechaEnt")[0].setAttribute('min', today);

            function justNumbers(e)
            {
                var keynum = window.event ? window.event.keyCode : e.which;
                if ((keynum === 8) || (keynum === 46))
                    return true;
                return /\d/.test(String.fromCharCode(keynum));
            }

            function cambiaLoteCadu(elemento) {
                var indice = elemento.selectedIndex;
                document.getElementById('SelectCadu').selectedIndex = indice;
            }

            function validaBuscar() {
            }


            function validaSeleccionar() {
                var DesSel = document.getElementById('DesSel').value;
                if (DesSel === "") {
                    alert('Favor de Capturar Toda la información');
                    return false;
                }
                var cantidad = document.getElementById('Cantidad').value;
                if (cantidad === "") {
                    alert('Escriba una cantidad');
                    return false;
                }

            }



            function validaUni() {

                var Unidad = document.getElementById('ClaCli').value;
                if (Unidad === "") {
                    alert('Seleccione Unidad');
                    return false;
                }

                var FechaEnt = document.getElementById('FechaEnt').value;
                if (FechaEnt === "") {
                    alert('Seleccione Fecha de Entrega');
                    return false;
                }
                var clave = document.getElementById('ClaPro').value;
                if (clave === "") {
                    alert('Escriba una Clave');
                    return false;
                }

                var causes = document.getElementById('ClaCli').value;

                if (causes === "") {
                    alert('Capture Diagnóstico válido');
                    e.focus();
                    return false;
                }
                var causesArray = causes.split(" - ");
                causes = causesArray[0];
                var causesTodos = "";
            <%
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("select F_ClaCli, F_NomCli from tb_uniatn ");
                    while (rset.next()) {
            %>
                causesTodos = causesTodos + "<%=rset.getString("F_ClaCli")%>-";
            <%
                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            %>
                var causesTodosArray = causesTodos.split('-');
                var ban1 = 0;
                for (i = 0; i <= causesTodosArray.length; i++) {
                    if (causes === causesTodosArray[i]) {
                        return true;
                        ban1 = 1;
                    }
                }
                if (ban1 === 0) {
                    alert('Capture Unidad válida');
                    return false;
                }
            }
        </script>
    </body>

</html>

