<%-- 
    Document   : capturaISEM.jsp
    Created on : 14-jul-2014, 14:48:02
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.sql.SQLException"%>
<%@page import="conn.ConectionDB_Linux"%>
<%@page import="conn.ConectionDB"%>
<%@page import="ISEM.CapturaPedidos"%>
<%@page import="javax.servlet.http.HttpSession"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formNoCom = new DecimalFormat("000");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "", IdUsu = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("indexMedalfa.jsp");
    }
    ConectionDB con = new ConectionDB();
    CapturaPedidos indice = new CapturaPedidos();
    String proveedor = "", fecEnt = "", horEnt = "", claPro = "", desPro = "", NoCompra = "", origen = "", cedis = "", Proyecto = "", Desproyecto = "", Campo = "", TipoOC = "", zona = "";
    try {
        NoCompra = (String) sesion.getAttribute("NoCompra");
        proveedor = (String) sesion.getAttribute("Proveedor");
        fecEnt = (String) sesion.getAttribute("fec_entrega");
    } catch (Exception e) {

    }

    if (claPro == null) {
        claPro = "";
        desPro = "";
        origen = "";
    }

    if (NoCompra == null) {
        NoCompra = "";
    }

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SIALSS</title>
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="shortcut icon"
              href="imagenes/system-settings-icon_31831.png" />
        <link href="css/select2.css" rel="stylesheet">

    </head>
    <body onload="focusLocus();
            SelectProve(FormBusca);">
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="jspf/menuPrincipalCompra.jspf" %>
            <h4>Captura OC Enseres</h4>
            <hr/>
            <form name="FormCaptura" action="CapturaPedidos" method="post">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <div class="row">
                            <label class="col-sm-3 col-sm-offset-7 text-right">
                                <h4>Número de Orden de Compra</h4>
                            </label>
                            <input class="hidden" value="<%=cedis%>" name="nomCedis" >
                            <div class="col-sm-2">
                                <input type="text" class="form-control" id="NoCompra" name="NoCompra" value="<%=NoCompra%>" />
                            </div>
                        </div>
                        <br/>
                        <div class="row">
                            <label class="col-sm-1">
                                <h4>Proveedor:</h4>
                            </label>
                            <div class="col-sm-6">
                                <select class="form-control" name="Proveedor" id="Proveedor" onchange="SelectProve(this.form);
                                document.getElementById('Fecha').focus()">
                                    <option value="">--Proveedor--</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = null;

                                            rset = con.consulta("SELECT F_Id, F_Proveedor FROM tb_enseresproveedor;");

                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"
                                            <%
                                                if (proveedor.equals(rset.getString(1))) {
                                                out.println("selected");
                                                }

                                            %>
                                            ><%=rset.getString(2)%></option>
                                    <%
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (SQLException ex) {
                                                Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>
                                </select>  
                            </div>
                        </div>
                        <div class="row">
                            <label class="col-sm-2">
                                <h4>Fecha de Entrega:</h4>
                            </label>
                            <div class="col-sm-2">
                                <input type="date" class="form-control" id="Fecha" name="Fecha" value="<%=fecEnt%>"/>
                            </div>
                        </div>
                        <br/>
                        <div class="row">

                            <label class="col-sm-1 text-right">
                                <h4>Clave:</h4>
                            </label>
                            <div class="col-sm-8">
                                <select name="ClaPro" id="ClaPro" class="form-control">
                                    <option>-- Seleccione --</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = null;
                                            rset = con.consulta("SELECT F_Id, CONCAT(F_Insumos, ' - ', F_UM) FROM tb_enseres WHERE F_Sts = 'A';");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"
                                            <%
                                                out.println("selected");
                                            %>
                                            ><%=rset.getString(2)%></option>
                                    <%
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (SQLException ex) {
                                                Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                        <div class="row">                            
                            <label class="col-sm-2 text-right">
                                <h4>Pzs a Entregar:</h4>
                            </label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="CanPro" id="CanPro" data-behavior="only-num" />
                            </div>
                        </div>
                        <br/>
                        <button class="btn btn-block btn-success" name="accion" value="capturarEnseres" onclick="return validaCaptura();">Capturar</button>
                    </div>
                </div>
            </form>
            <br/>
            <table class="table table-bordered table-condensed table-striped">
                <tr>
                    <td><strong>Descripción</strong></td>
                    <td><strong>Cantidad</strong></td>
                    <td></td>
                </tr>
                <%
                    int banConfirma = 0;
                    try {
                        con.conectar();
                        ResultSet rset = con.consulta("SELECT E.F_Insumos, O.F_Cant, O.F_Id FROM tb_enseresoc O INNER JOIN tb_enseres E ON O.F_IdEnseres = E.F_Id WHERE O.F_Sts = 0 AND F_Usuario = '" + (String) sesion.getAttribute("IdUsu") + "' AND F_Oc = '" + NoCompra + "';");
                        while (rset.next()) {
                            banConfirma = 1;
                %>
                <tr>
                    <td><%=rset.getString(1)%></td>
                    <td><%=formatter.format(rset.getInt(2))%></td>
                    <td>
                        <form action="CapturaPedidos" method="post">
                            <input name="id" value="<%=rset.getString(3)%>" class="hidden" />
                            <button class="btn btn-success" name="accion" value="eliminaEnseres"><span class="glyphicon glyphicon-remove"></span></button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, e);
                    } finally {
                        try {
                            con.cierraConexion();
                        } catch (SQLException ex) {
                            Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, ex);
                        }
                    }
                %>

            </table>
            <div class="row">
                <%
                    if (banConfirma == 1) {
                %>
                <form name="FormCaptura" action="CapturaPedidos" method="post">
                    <div class="col-sm-6">
                        <input class="hidden" name="NoCompra" value="<%=NoCompra%>"/>
                        <button class="btn btn-success btn-block" name="accion" value="confirmarEnseres" onclick="return confirm('¿Seguro que desea CONFIRMAR el pedido?')">Confirmar Orden de Compra</button>
                    </div>
                    <div class="col-sm-6">
                        <button class="btn btn-success btn-block" name="accion" value="cancelarEnseres" onclick="return confirm('¿Seguro que desea CANCELAR el pedido?')">Limpiar Pantalla</button>
                    </div>
                </form>
                <%
                    }
                %>
            </div>
        </div>
        <%@include file="jspf/piePagina.jspf" %>
        <script src="js/jquery-2.1.4.min.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/bootstrap.js"></script>
        <script src="js/jquery.alphanum.js" type="text/javascript"></script>
        <script src="js/select2.js" type="text/javascript"></script>


        <script type="text/javascript">

                            $("#Proveedor").select2();
                            $("#ClaPro").select2();
                            var cedis = "<%=cedis%>";

                            $("#cedis option[value=" + cedis + "] ").prop('selected', 'selected');
                            $("#cedis").val(cedis);
                            $("[data-behavior~=only-alphanum-white]").alphanum({
                                allow: '.',
                                disallow: "'",
                                allowSpace: true
                            });
                            $("[data-behavior~=only-num]").numeric({
                                allowMinus: false,
                                allowThouSep: false
                            });




                            function focusLocus() {
                                document.getElementById('Proveedor').focus();
                                if (document.getElementById('Fecha').value !== "") {
                                    document.getElementById('ClaPro').focus();
                                }
                                if (document.getElementById('ClaPro').value !== "") {
                                    document.getElementById('Prioridad').focus();
                                }
                            }

                            function validaClaDes(boton) {
                                var btn = boton.value;
                                var prove = document.getElementById('Proveedor').value;
                                var fecha = document.getElementById('Fecha').value;
                                var hora = document.getElementById('Hora').value;
                                var cedis = document.getElementById('cedis').value;
                                var NoCompra = document.getElementById('NoCompra').value;
                                if (prove === "" || fecha === "" || hora === "0:00" || NoCompra === "" || cedis === "") {
                                    alert("Complete los datos");
                                    return false;
                                }
                                var valor = "";
                                var mensaje = "";
                                if (btn === "ClaPro") {
                                    valor = document.getElementById('ClaPro').value;
                                    mensaje = "Introduzca la clave";
                                }
                                if (btn === "Descripcion") {
                                    valor = document.getElementById('Descripcion').value;
                                    mensaje = "Introduzca la descripcion";
                                }
                                if (valor === "") {
                                    alert(mensaje);
                                    return false;
                                }
                                return true;
                            }

                            function validaCaptura() {
                                var ClaPro = document.getElementById('ClaPro').value;
                                var DesPro = document.getElementById('DesPro').value;
                                var CanPro = document.getElementById('CanPro').value;
                                var proveedor = document.getElementById('Proveedor').value;
                                if (ClaPro === "" || DesPro === "" || CanPro === "") {
                                    alert("Complete los datos");
                                    return false;
                                }
                                var CanRes = document.getElementById('CantRest').value;
                                CanRes = CanRes.replace(",", "");
                                CanRes = CanRes.replace(",", "");
                                CanRes = CanRes.replace(",", "");
                                CanRes = CanRes.replace(",", "");
                                CanRes = CanRes.replace(",", "");
                                if (parseInt(CanRes) < parseInt(CanPro) && proveedor !== '63' && proveedor !== '19') {
                                    alert("La Cantidad Solicitada no puede ser mayor a la Cantidad Restante");
                                    return false;
                                }
                                return true;
                            }
                            function SelectProve(form) {
            <%
                /*try {
                    con.conectar();
                    ResultSet rset3 = con.consulta("SELECT DISTINCT F_ClaProve FROM tb_prodprov2017");
                    while (rset3.next()) {
                        out.println("if (form.Proveedor.value == '" + rset3.getString(1) + "') {");
                        out.println("var select = document.getElementById('ClaPro');");
                        out.println("select.options.length = 0;");
                        int i = 1;
                        ResultSet rset4 = con.consulta("SELECT F_ClaPro FROM tb_prodprov2017  WHERE  F_ClaProve='" + rset3.getString(1) + "';");

                        out.println("select.options[select.options.length] = new Option('-Seleccione-', '');");
                        while (rset4.next()) {
                            out.println("select.options[select.options.length] = new Option('" + rset4.getString(1) + "', '" + rset4.getString(1) + "');");
                            i++;
                        }
                        out.println("}");
                    }
                } catch (Exception e) {
                    Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, e);
                } finally {
                    try {
                        con.cierraConexion();
                    } catch (Exception ex) {
                        Logger.getLogger("capturaMedalfa.jsp").log(Level.SEVERE, null, ex);
                    }
                }*/
            %>
                            }
        </script>
    </body>

</html>
