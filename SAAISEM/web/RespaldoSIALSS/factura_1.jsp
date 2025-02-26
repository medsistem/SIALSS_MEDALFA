<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatter2 = new DecimalFormat("000");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", Clave = "";
    String tipo = "", F_Ruta = "", F_FecEnt = "", Unidad1 = "", Unidad2 = "", Catalogo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        Clave = (String) session.getAttribute("clave");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    if (Clave == null) {
        Clave = "";
    }
    ConectionDB con = new ConectionDB();
    String UsuaJuris = "";
    try {
        con.conectar();
        ResultSet rset = con.consulta("select F_Juris from tb_usuario where F_Usu = '" + usua + "'");
        while (rset.next()) {
            UsuaJuris = rset.getString("F_Juris");
        }
        con.cierraConexion();
    } catch (Exception e) {

    }
    String where = " and (";
    String[] temp;
    temp = UsuaJuris.split(",");
    for (int i = 0; i < temp.length; i++) {
        where += "f.F_Ruta like 'R" + temp[i] + "%'";
        if (i != temp.length - 1) {
            where += " or ";
        }
    }
    where += ")";

    try {
        Unidad1 = request.getParameter("Unidad1");
        Unidad2 = request.getParameter("Unidad2");
        Catalogo = request.getParameter("Catalogo");
        if (Catalogo.equals("Seleccione")) {
            Catalogo = "";
        }

    } catch (Exception e) {
    }
    //System.out.println("Unidad1" + Unidad1 + " Unidad2:" + Unidad2 + " Cata:" + Catalogo);
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/datepicker3.css" rel="stylesheet">
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
        </div>
        <div class="container">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Facturación aútomatica</h3>
                </div>
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formulario1" id="formulario1" method="post" action="factura.jsp">
                        <label class="control-label col-lg-2" for="fecha_ini">Clave Unidad:</label>
                        <div class="col-sm-2">
                            <input name="Unidad1" id="Unidad1" type="text" class="form-control" placeholder="Unidad" value="" />                        
                        </div>
                        <div class="col-sm-2">
                            <input name="Unidad2" id="Unidad2" type="text" class="form-control" value="" placeholder="Unidad" />
                        </div>
                        <h4 class="col-sm-1">Catálogo:</h4>
                        <div class="col-sm-2">
                            <select name="Catalogo" id="Catalogo" class="form-control" required>
                                <option value="">Seleccione</option>
                                <option value="1">N1 Unidad Móvil  y Caravanas</option>
                                <option value="2">N2 Centros de Salud Rurales</option>
                                <option value="3">N3 Centros de Salud Urbanos y CEAPS CAD, Geriátricos y UNEMES</option>
                                <option value="4">N4 CEAPS</option>
                                <option value="5">N5 CISAME</option>
                                <option value="6">N6 Maternidades</option>
                                <option value="7">N7 Módulos Odontopediatricos y Centro de Atención Estomatológico</option>
                                <option value="8">N8 Hospitales Municipales</option>
                                <option value="9">N9 Hospitales Generales</option>
                                <option value="10">N10 Hospitales Materno Infantiles</option>
                                <option value="11">N11 Hospital Salud Visual</option>
                                <option value="12">N12 HMI Lic. Mónica Pretelini</option>
                                <option value="13">N13 Centro Medico Lic. Adolfo López Mateos</option>
                                <option value="14">N14 Hospitales Psiquiátricos</option>
                                <option value="15">N15 S.U.E.M.</option>
                                <option value="16">N16 Laboratorio Estatal de Salud Pública</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <div class="form-group">
                                <!--label for="FecEnt" class="col-sm-2 control-label">Fecha de Entrega</label>
                                <div class="col-sm-2">
                                    <input type="date" class="form-control" id="FecEnt" name="F_FecEnt" />
                                </div-->
                                <div class="col-lg-2">
                                    <button class="btn btn-block btn-success" type="submit" name="accion" value="consultar" onclick="return valida_clave();" > Consultar</button>
                                </div>
                            </div>
                        </div>

                    </form>
                    <%

                    %>
                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                <div class="panel-footer">
                    <form action="Facturacion" name="form1" id="form1" method="post" onsubmit="muestraImagen()">
                        <%                            int banReq1 = 0, banReq = 0;
                            try {
                                con.conectar();
                                String F_NomCli = "", F_Fecha = "", UbicaDesc2 = "";
                                banReq = 0;
                                int F_PiezasReq = 0, TotalSur = 0, UbicaModu2 = 0;
                                if (!(Catalogo == "")) {

                                    ResultSet rset = con.consulta("select  F_ClaCli AS F_ClaUni, F_NomCli from tb_uniatn where F_ClaCli between  '" + Unidad1 + "' and '" + Unidad2 + "'  group by F_ClaCli;");
                                    while (rset.next()) {
                                        F_NomCli = rset.getString("F_NomCli");
                                        ResultSet rset3 = con.consulta("select F_ClaUni, sum(F_Solicitado) as F_PiezasReq,DATE_FORMAT(F_Fecha,'%d/%m/%Y') AS F_Fecha from tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro AND M.F_N" + Catalogo + "='1' where F_Status = '0' and F_ClaUni = '" + rset.getString(1) + "' group by F_ClaUni;");
                                        while (rset3.next()) {
                                            banReq = 1;
                                            banReq1 = 1;
                                            F_PiezasReq = (rset3.getInt("F_PiezasReq"));
                                            F_Fecha = rset3.getString("F_Fecha");
                                            if (F_PiezasReq == 0) {
                                                banReq1 = 0;
                                            }
                                        }
                                        if (F_PiezasReq != 0) {
                        %>
                        <div class="panel panel-default">
                            <div class="panel-heading">

                                <%
                                    if (banReq == 1) {
                                %>
                                <input type="checkbox" name="chkUniFact" id="chkUniFact" value="<%=rset.getString("F_ClaUni")%>" checked="">
                                <!--input type="checkbox" name="chkUniFact[]" id="chkUniFact" value="<%//=rset.getString("F_ClaUni")%>" checked=""-->
                                <%
                                    }
                                %>
                                <a data-toggle="collapse" data-parent="#accordion" href="#111<%=rset.getString(1)%>" style="color:black" aria-expanded="true" aria-controls="collapseOne"><%=rset.getString(1)%> |  <%=F_NomCli%> | Fecha Req. <%=F_Fecha%> </a>


                                <%
                                    if (banReq == 1) {
                                %>
                                <input name="F_ClaUni" value="<%=rset.getString(1)%>" class="hidden" />
                                <!--input name="F_FecEnt" value="<%//=rset.getString("F_Fecha")%>" class="hidden" -->
                                <button class="btn btn-sm btn-warning" name="eliminar" value="<%=rset.getString(1)%>" onclick="eliminarReq(this)" type="button"><span class="glyphicon glyphicon-remove"></span></button>
                                    <%
                                        }
                                    %>
                            </div>
                            <div id="<%=rset.getString(1)%>" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                                <div class="panel-body">
                                    <div class="row">

                                        <!--div class="col-sm-2">
                                        <%
                                            if (banReq == 1) {
                                        %>
                                        <input name="pagina" class="hidden" value="factura.jsp">
                                        <input name="F_ClaUni" value="<%//=rset.getString(1)%>" class="hidden" />
                                        <input name="F_FecEnt" value="<%//=rset.getString("F_Fecha")%>" class="hidden" />
                                        <a class="btn btn-block btn-sm btn-success" href="detRequerimiento.jsp?F_ClaUni=<%//=rset.getString(1)%>&F_Ruta=<%//=F_Ruta%>&F_Mes=<%//=request.getParameter("F_Mes")%>&pagina=factura.jsp" ><span class="glyphicon glyphicon-search"></span></a>
                                        <%
                                            }
                                        %>
                                    </div-->
                                        <div class="col-sm-2">
                                        </div>

                                    </div>
                                    <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered table-condensed" id="datosProv">
                                        <tr>
                                            <td>Clave</td>
                                            <td>Descripción</td>
                                            <td>Piezas Sol</td>
                                            <td>Piezas Sur</td>
                                            <td>Existencia</td>
                                        </tr>
                                        <%
                                            try {
                                                int ExiLot = 0, ExiSol = 0, UbicaModu = 0;
                                                String UbicaDesc = "";
                                                int Cata = Integer.parseInt(Catalogo);
                                                if (Cata == 1) {
                                                    ResultSet UbiMod = con.consulta("SELECT F_Id FROM tb_parametro;");
                                                    if (UbiMod.next()) {
                                                        UbicaModu = UbiMod.getInt(1);
                                                    }
                                                    if (UbicaModu == 1) {
                                                        UbicaDesc = "'MODULA','A0S','APE','DENTAL','REDFRIALERMA'";
                                                    } else if (UbicaModu == 2) {
                                                        UbicaDesc = "'MODULA2','A0S','APE','DENTAL','REDFRIALERMA'";
                                                    } else {
                                                        UbicaDesc = "'A0S','APE','DENTAL','REDFRIALERMA'";
                                                    }

                                                } else if (Cata == 30) {
                                                    ResultSet UbiSolu = con.consulta("SELECT * FROM tb_ubicasoluciones;");
                                                    if (UbiSolu.next()) {
                                                        UbicaDesc = UbiSolu.getString(1);
                                                    }
                                                }else{
                                                    UbicaDesc = "'AF','MATCUR','APE','DENTAL','REDFRIALERMA'";
                                                }
                                                
                                                /*else if ((Cata == 2) || (Cata == 14) || (Cata == 16) || (Cata == 216) || (Cata == 17) || (Cata == 217)) {
                                                    UbicaDesc = "'AF','APE','DENTAL'";
                                                } else if (Cata == 3) {
                                                    UbicaDesc = "'AF','APE','DENTAL'";
                                                }*/

                                                ResultSet rsetR1 = con.consulta("SELECT M.F_ClaPro,M.F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 group by F_IdReq order by M.F_ClaPro+0;");
                                                while (rsetR1.next()) {

                                                    ResultSet rsetR2 = con.consulta("select sum(F_ExiLot) from tb_lote where F_ClaPro='" + rsetR1.getString(1) + "' AND F_Ubica IN (" + UbicaDesc + ");");
                                                    while (rsetR2.next()) {
                                                        ExiLot = rsetR2.getInt(1);
                                                    }
                                        %>
                                        <tr
                                            <%
                                                if (rsetR1.getInt(4) > ExiLot) {
                                                    out.println("class='success'");
                                                    ExiSol = ExiLot;
                                                } else {
                                                    ExiSol = rsetR1.getInt(4);
                                                }
                                                TotalSur = TotalSur + ExiSol;
                                                //System.out.println("'Cantidad"+rset.getString(1)+"_"+rsetR1.getString(1)+"'");
                                                String cadena = rsetR1.getString(1).trim();
                                                cadena = cadena.replace(".", "");
                                            %>
                                            >
                                            <td><%=rsetR1.getString(1)%></td>
                                            <td><%=rsetR1.getString(2)%></td>
                                            <td><%=rsetR1.getInt(4)%></td>
                                            <td ><small><input name="Cantidad<%=rset.getString(1)%><%=cadena%>" id="Cantidad<%=rset.getString(1)%><%=cadena%>" type="number" min="0" class="text-right form-control" value="<%=ExiSol%>" data-behavior="only-num" /></small></td>
                                            <td class="text-right"><%=formatter.format(ExiLot)%></td>
                                        </tr>                 
                                        <%
                                            }
                                        %>
                                        <%
                                            } catch (Exception e) {
                                                out.println(e.getMessage());
                                            }
                                        %>

                                    </table> 
                                    <div class="row">
                                        <h4 class="col-sm-2">
                                            Observaciones:
                                        </h4>
                                        <div class="col-sm-10">
                                            <textarea class="form-control" name="obs<%=rset.getString("F_ClaUni")%>" id="obs<%=rset.getString("F_ClaUni")%>"></textarea>
                                        </div>
                                    </div>
                                    <div class="row">


                                        <h4 class="col-sm-2">
                                            Piezas: <%=formatter.format(F_PiezasReq)%>
                                        </h4>

                                        <h4>Total de Piezas a Facturar: <%=formatter.format(TotalSur)%></h4>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%
                                            TotalSur = 0;
                                        }
                                    }
                                } else {
                                    out.println("<script>alert('Favor de Seleccionar Catálogo')</script>");
                                }
                                con.cierraConexion();
                            } catch (Exception e) {
                                out.println(e.getMessage());
                            }
                        %>
                        <%
                            if (banReq1 == 1) {
                        %>
                        <div class="row">
                            <h4 class="col-sm-3">Fecha de Entrega:</h4>
                            <div class="col-sm-3">
                                <input type="date" id="F_FecEnt" name="F_FecEnt" class="form-control" value="" min="<%=df2.format(new Date())%>"  required />
                            </div>
                            <h4 class="col-sm-1">Catálogo</h4>
                            <div class="col-sm-1">
                                <input type="text" name="Cata" id="Cata" class="form-control" readonly="" required="" value="<%=Catalogo%>" />
                            </div>
                            <h4 class="col-sm-1">Tipo</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="F_Tipo" id="F_Tipo">
                                    <option>Ordinario</option>
                                    <option>Complemento</option>
                                    <option>Apoyo</option>
                                    <option>Programa</option>
                                    <option>Urgente</option>
                                    <option>Extemporaneo</option>
                                </select>
                            </div>
                        </div>
                        <br/>
                        <input name="F_Juris" class="hidden" value="<%=UsuaJuris%>" />
                        <div class="row">
                            <div class="col-sm-6">
                                <button class="btn btn-block btn-warning" type="submit" name="accion" value="cancelar" onclick="return validaRemision()">Cancelar Folio(s)</button> 
                            </div>
                            <div class="col-sm-6">
                                <button class="btn btn-block btn-success" type="submit" name="accion" value="generarRemision" onclick="return validaRemision()">Generar Folio(s)</button> 
                                <!--button class="btn btn-block btn-success" type="button" name="accion" value="generarRemision" id="BtngenerarRemision">Generar Folio(s)</button--> 
                            </div>
                        </div>

                        <%
                            }
                        %>
                    </form>
                </div>
            </div>
        </div>
        <br><br><br>
        <div class="navbar navbar-fixed-bottom navbar-inverse">
            <div class="text-center text-muted">
                MEDALFA || Desarrollo de Aplicaciones 2009 - 2019 <span class="glyphicon glyphicon-registration-mark"></span><br />
                Todos los Derechos Reservados
            </div>
        </div>

        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel"></h4>
                    </div>
                    <div class="modal-body">
                        <div class="text-center" id="imagenCarga">
                            <img src="imagenes/ajax-loader-1.gif" alt="" />
                        </div>
                    </div>
                    <div class="modal-footer">
                    </div>
                </div>
            </div>
        </div>
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
        <!--script src="js/facturajs/Facturacionjsauto_2.js"></script-->
        <script src="js/sweetalert.min.js" type="text/javascript"></script>
        <script>
                                    $(document).ready(function() {
                                        $('#datosProv').dataTable();
                                    });
                                    function validaRemision() {
                                        var confirmacion = confirm('Seguro que desea generar los Folios');
                                        if (confirmacion === true) {
                                            $('#btnGeneraFolio').prop('disabled', true);
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    }

                                    function eliminarReq(e) {
                                        var confirma = confirm('Seguro que desea eliminar este requerimienro?');
                                        if (confirma) {
                                            var F_ClaUni = e.value;
                                            $.ajax({
                                                type: 'POST',
                                                url: 'Facturacion?eliminar=' + F_ClaUni,
                                                /*data: form.serialize(),*/
                                                success: function(data) {
                                                    recargarReqs(data);
                                                }
                                            });
                                            function recargarReqs(data) {
                                                location.reload();
                                            }
                                        }
                                    }
        </script>
        <script type="text/javascript">
            function muestraImagen() {
                $('#myModal').modal();
            }


            $("[data-behavior~=only-alphanum]").alphanum({
                allowSpace: false,
                allowOtherCharSets: false,
                allow: '.'
            });
            $("[data-behavior~=only-alphanum-caps]").alphanum({
                allowSpace: false,
                allowOtherCharSets: false,
                forceUpper: true
            });
            $("[data-behavior~=only-alphanum-caps-15]").alphanum({
                allowSpace: false,
                allowOtherCharSets: false,
                forceUpper: true,
                maxLength: 15
            });
            $("[data-behavior~=only-alphanum-white]").alphanum({
                allow: '.',
                disallow: "'",
                allowSpace: true
            });
            $("[data-behavior~=only-num]").numeric({
                allowMinus: false,
                allowThouSep: false
            });

            $("[data-behavior~=only-alpha]").alphanum({
                allowNumeric: false,
                allowSpace: false,
                allowOtherCharSets: true
            });


        </script> 

    </body>
</html>

