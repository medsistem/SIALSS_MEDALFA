<%-- 
    Document   : compraAuto3
    Created on : 17/02/2014, 03:34:46 PM
    Author     : MEDALFA
--%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="conn.ConectionDB"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%java.text.DateFormat df1 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
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
    String tipo = "";
    String nombreUnidad = "";
    String claveProducto = "", claveProductoSS = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }

    ConectionDB con = new ConectionDB();
    try {
        if (request.getParameter("accion").equals("Buscar")) {
            sesion.setAttribute("posClave", "0");
            sesion.setAttribute("folioRemi", "");
            sesion.setAttribute("CodBar", "");
        }
    } catch (Exception er) {

    }

    int totalClaves = 0, clavesCapturadas = 0, nomComercial = 0;
    String fecha = "", noCompra = "", Proveedor = "", Fecha = "";
    String TipoMed = "";
    fecha = request.getParameter("Fecha");
    noCompra = request.getParameter("NoCompra");
    Proveedor = request.getParameter("Proveedor");
    if (fecha == null) {
        fecha = "";
    }
    if (noCompra == null) {
        noCompra = (String) sesion.getAttribute("NoCompra");
        if (noCompra == null) {
            noCompra = "";
        }
    }
    if (Proveedor == null) {
        Proveedor = "";
    }

    String posClave = "0";
    try {
        posClave = sesion.getAttribute("posClave").toString();
    } catch (Exception e) {
        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
    }
    if (posClave == null || posClave.equals("")) {
        posClave = "0";
    }

    try {
        if (request.getParameter("accion").equals("buscaCompra")) {
            posClave = "0";
        }
    } catch (Exception e) {
        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
    }

    String folioRemi = "";

    try {
        folioRemi = (String) sesion.getAttribute("folioRemi");
    } catch (Exception e) {
        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
    }

    if (folioRemi == null) {
        folioRemi = "";
    }
    Fecha = request.getParameter("Fecha");
    if (Fecha == null) {
        Fecha = "";
    }

    String CodBar = "", Lote = "", Cadu = "", TipIns = "", proyect = "";
    CodBar = (String) sesion.getAttribute("CodBar");
    Lote = (String) sesion.getAttribute("Lote");
    Cadu = (String) sesion.getAttribute("Cadu");
    TipIns = (String) sesion.getAttribute("TipIns");

    if (CodBar == null) {
        CodBar = "";
    }
    if (Lote == null) {
        Lote = "";
    }
    if (Cadu == null) {
        Cadu = "";
    }

    ResultSet rset = null, rset2 = null, rset3 = null, rset4 = null, rset5 = null;

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link href="../css/select2.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" rel="stylesheet"/>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.js"></script>

        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipal.jspf" %>
            <form action="compraAuto3.jsp" method="post">

                <div class="row">
                    <label class="col-sm-2 text-right">
                        <h4>Proveedor</h4>
                    </label>
                    <div class="col-sm-5">
                        <select class="form-control" name="Proveedor" id="Proveedor" onchange="SelectProve(this.form); document.getElementById('Fecha').focus()">

                            <option value="">--Proveedor--</option>
                            <%
                                try {
                                    con.conectar();

                                    rset = con.consulta("SELECT p.F_ClaProve, p.F_NomPro FROM tb_proveedor p INNER JOIN tb_pedido_sialss pv ON p.F_ClaProve = pv.F_Provee WHERE F_StsPed = '1' AND F_Recibido = '0' GROUP BY pv.F_Provee ORDER BY p.F_NomPro;");

                                    while (rset.next()) {
                            %>
                            <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                            <%
                                    }
                                } catch (Exception e) {
                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {

                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                    }
                                }
                            %>

                        </select>
                    </div>
                    <label class="col-sm-2 text-right">
                        <h4>Fecha</h4>
                    </label>
                    <div class="col-sm-2">
                        <input type="date" class="form-control" data-date-format="dd/mm/yyyy" id="Fecha" name="Fecha" value="<%=Fecha%>" onkeypress="return tabular(event, this)" />
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12">
                        <button class="btn btn-success btn-block" name="accion" value="Buscar">Buscar</button>
                    </div>
                </div>

                <br/>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>&Oacute;rdenes de Compra: </h4>
                    </label>
                    <div class="col-sm-9">
                        <select class="form-control" name="NoCompra" id="NoCompra" onchange="this.form.submit();">
                            <option value="">-- Proveedor -- Orden de Compra --</option>
                            <%
                                try {
                                    fecha = df1.format(df3.parse(Fecha));
                                } catch (Exception e) {
                                    //Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                }
                                try {
                                    con.conectar();

                                    rset = con.consulta("SELECT o.F_NoCompra, p.F_NomPro FROM tb_pedido_sialss o INNER JOIN tb_proveedor p ON o.F_Provee = p.F_ClaProve WHERE o.F_FecSur LIKE '%" + fecha + "%' AND o.F_Provee LIKE '%" + request.getParameter("Proveedor") + "' AND F_StsPed != '2' AND F_Recibido = 0 GROUP BY o.F_NoCompra;");

                                    while (rset.next()) {
                            %>
                            <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%> - <%=rset.getString(1)%></option>
                            <%
                                    }
                                } catch (Exception e) {
                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                    }
                                }
                            %>
                        </select>
                    </div>
                </div>
                <br/>
            </form>


            <form action="../CompraAutomaticaHH" method="get" name="formulario1">
                <br/>
                <%
                    try {
                        int fechaActualVen = 0;
                        int activo = 0;
                        con.conectar();
                        rset = con.consulta("select i.F_NoCompra, i.F_FecSur, i.F_HorSur, p.F_NomPro, p.F_ClaProve, i.F_Proyecto,i.F_FuenteFinanza,i.F_IdOrigen from tb_pedido_sialss i, tb_proveedor p where i.F_Provee = p.F_ClaProve and F_StsPed = '1' and F_NoCompra = '" + noCompra + "' and F_recibido='0' group by F_NoCompra;");
                        while (rset.next()) {
                            Date date = new Date();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String strDate = dateFormat.format(date);
                            Integer fechaActualInt = Integer.parseInt(strDate.replaceAll("-", ""));
                            fechaActualVen = Integer.parseInt(rset.getString(2).replaceAll("-", ""));
                            //System.out.println(fechaActualInt+"********************** " + fechaActualVen);
                            //      System.out.println(fechaActualVen > 0);   
                            //activo = fechaActualVen - fechaActualInt; 
                            System.out.println(activo);
                            if (activo >= 0) {
                %>
                <div class="row">
                    <div class="panel panel-default">
                        <div class="panel-heading" style="background-color: #dff0d8">
                            <div class="row">
                                <h4 class="col-sm-3">Folio Orden de Compra:</h4>
                                <div class="col-sm-2"><input class="form-control" value="<%=rset.getString(1)%>" readonly="" name="folio" id="folio" onkeypress="return tabular(event, this)" /></div>
                                <div class="col-sm-2">
                                </div>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-12">Proveedor: <%=rset.getString(4)%></h4>
                            </div>                             
                            <div class="row">
                                <h4 class="col-sm-12">Fecha y Hora de Entrega: <%=df3.format(df2.parse(rset.getString(2)))%> <%=rset.getString(3)%></h4>
                            </div>
                            <div class="row">
                                <h4 class="col-sm-1">CLAVE:</h4>
                                <div class="col-sm-3">
                                    <select class="form-control" name="selectClave" id="selectClave">
                                        <option>-Seleccione-</option>
                                        <%
                                            /**
                                             * Arroja las claves faltantes por
                                             * ingresar al sistema
                                             */
                                            try {
                                                con.conectar();
                                                rset2 = con.consulta("SELECT F_IdIsem, F_Clave, CONCAT(F_Clave, ' - ', F_ClaveSS) AS F_ClaveSS FROM tb_pedido_sialss WHERE F_NoCompra = '" + noCompra + "' AND F_Recibido = '0';");
                                                while (rset2.next()) {
                                        %>

                                        <option value="<%=rset2.getString(1)%>"><%=rset2.getString(3)%></option>

                                        <%
                                                    totalClaves++;
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {

                                            }
                                        %>
                                    </select>
                                </div>
                                <div class="col-sm-1">
                                    <button class="btn btn-success btn-block" name="accion" value="seleccionaClave">CLAVE</button>
                                </div>
                                <div class="col-sm-5">
                                    <%
                                        try {
                                            con.conectar();
                                            rset5 = con.consulta("SELECT C.F_Cb,C.F_ClaPro,M.F_DesPro,C.F_Lote,C.F_FecCad,C.F_Pz,F_IdCom, C.F_Costo, C.F_ImpTo, C.F_ComTot, C.F_FolRemi  FROM tb_compratemp C INNER JOIN tb_medica M ON C.F_ClaPro=M.F_ClaPro WHERE F_OrdCom='" + noCompra + "'");
                                            while (rset5.next()) {
                                                clavesCapturadas++;
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (Exception ex) {
                                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>

                                    <h4>Insumos Ingresados <%=clavesCapturadas%>/<%=totalClaves%></h4>

                                </div>
                                <div class="col-sm-2">
                                    <a href="#" class="btn btn-success btn-block" data-toggle="modal" data-target="#Rechazar">Rechazar</a>
                                </div>

                            </div>

                            <div class="row">
                                <%
                                    try {
                                        con.conectar();
                                        rset2 = con.consulta("select s.F_Clave, m.F_DesPro, s.F_Lote, DATE_FORMAT(F_Cadu, '%d/%m/%Y'), s.F_Cant, F_IdIsem, F_Obser from tb_pedido_sialss s, tb_medica m where s.F_Clave = m.F_ClaPro and F_NoCompra = '" + rset.getString(1) + "' and F_StsPed = '1'");
                                        while (rset2.next()) {
                                            rset2.last();
                                        }
                                        rset2 = con.consulta("select s.F_Clave,s.F_ClaveSS, m.F_NomGen, s.F_Lote, DATE_FORMAT(F_Cadu, '%d/%m/%Y'), s.F_Cant, F_IdIsem, F_Obser,F_Proyecto ,m.F_PrePro, m.F_Concentracion, m.F_FormaFarm, m.F_DesProEsp, m.F_TipMed , IFNULL(COUNT(nc.F_ClaPro),0) as nombreComercial FROM tb_pedido_sialss AS s INNER JOIN tb_medica AS m ON s.F_Clave = m.F_ClaPro LEFT JOIN tb_nombrecomercial AS nc ON s.F_Clave = nc.F_ClaPro where s.F_Clave = m.F_ClaPro and F_NoCompra = '" + rset.getString(1) + "' AND s.F_Proyecto='" + rset.getString(6) + "' and s.F_IdIsem = '" + sesion.getAttribute("claveSeleccionada") + "' ");
                                        while (rset2.next()) {
                                            claveProducto = rset2.getString(1);
                                            claveProductoSS = rset2.getString(2);
                                            TipoMed = rset2.getString(14);
                                            nomComercial = rset2.getInt(15);
                                            proyect = rset2.getString(9);

                                            ResultSet rsetTipo = con.consulta("SELECT M.F_ClaPro AS clave, CASE WHEN C.F_ClaPro = M.F_ClaPro  THEN 'CONTROLADO' WHEN R.F_ClaPro = M.F_ClaPro THEN 'RED_FRIA' WHEN A.F_ClaPro = M.F_ClaPro THEN 'APE' ELSE 'NORMAL' END 'INSUMO' FROM tb_medica AS M LEFT JOIN tb_ape AS A ON A.F_ClaPro = M.F_ClaPro LEFT JOIN tb_controlados AS C ON C.F_ClaPro = M.F_ClaPro LEFT JOIN tb_redfria AS R ON R.F_ClaPro = M.F_ClaPro WHERE M.F_ClaPro ='" + claveProducto + "';");
                                            while (rsetTipo.next()) {
                                                String tipoInsumo = rsetTipo.getString(2);

                                %>

                                <h4 class="bg-success" style="padding: 5px">CLAVE : <%=rset2.getString(2)%>  | <%=rset2.getString(3)%>  | <%=rset2.getString(13)%> |</h4>
                                <h4 class="bg-success" style="padding: 5px">PRESENTACION : <%=rset2.getString(10)%></h4>
                                <input name="tipoInsumo" id="tipoInsumo" value="<%=tipoInsumo%>" type="hidden"/> 

                                <% if (TipoMed.contentEquals("2504")) {%>
                                <h4 class="bg-success" style="padding: 5px">CONCENTRACION | <%=rset2.getString(11)%></h4>        
                                <h4 class="bg-success" style="padding: 5px">Formula Farmaceutica | <%=rset2.getString(12)%></h4>
                                <% }
                                    }%>



                                <div class="col-sm-6">
                                    <strong>Cantidad a Recibir</strong>
                                    <input type="text" value="<%=formatter.format(rset2.getInt(6))%>" class="form-control" name="cantRecibir" id="cantRecibir" onclick="" readonly=""  onkeypress="return tabular(event, this)"/>
                                </div>


                                <div class="col-sm-6">
                                    <strong>Cantidad Recibida</strong>
                                    <%
                                        int cantRecibida = 0;
                                        try {
                                            con.conectar();
                                            rset3 = con.consulta("select sum(F_CanCom) from tb_compra where F_OrdCom = '" + rset.getString(1) + "' and F_ClaPro = '" + rset2.getString(1) + "';");
                                            while (rset3.next()) {
                                                cantRecibida = cantRecibida + rset3.getInt(1);
                                            }
                                            rset3 = con.consulta("select sum(F_Pz) from tb_compratemp where F_OrdCom = '" + rset.getString(1) + "' and F_ClaPro = '" + rset2.getString(1) + "'; ");
                                            while (rset3.next()) {
                                                cantRecibida = cantRecibida + rset3.getInt(1);
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (Exception ex) {
                                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>
                                    <input type="text" value="<%=formatter.format(cantRecibida)%>" class="form-control" name="cantRecibida" id="cantRecibida" onkeypress="return tabular(event, this)" onclick="" readonly=""/>
                                    <input type="text" value="<%=rset2.getString(1)%>" class="hidden" name="ClaPro" id="ClaPro" onclick="" readonly=""  onkeypress="return tabular(event, this)"/>
                                    <input type="text" value="<%=rset2.getString(2)%>" class="hidden" name="ClaProSS" id="ClaProSS" onclick="" readonly=""  onkeypress="return tabular(event, this)"/>
                                </div>
                            </div>        

                        </div>

                        <br>
                        <!-- 
                        Datos del Insumo 
                        -->            
                        <div class="panel-body">
                           <!-- <div class="row">
                                <div class="col-md-12">
                                    <label><input type="checkbox" id="volumetriaCheck" onclick="checkVolumetria()"> Volumetría</label>
                                </div>
                            </div>
-->
                            <!--Menu-->
                            <ul class="nav nav-tabs">
                                <li class="active"><a data-toggle="tab" href="#datos" style="color: black">Datos del Insumo</a></li>
                                <li><a data-toggle="tab" href="#cantidadesTab" style="color: black">Cantidades</a></li>
                                <li><a data-toggle="tab" href="#volumetriaTab" id="volumetriaTabSel" style="color: black; display: none">Volumetría Peso</a></li>
                                <li><a data-toggle="tab" href="#volumetriaVolTab" id ="volumetriaVolTabSel" style="color: black; display: none">Volumetría Dimensiones</a></li>
                            </ul>
                            <br>
                            <!--Encabezado-->
                            <div class="tab-content" style="background-color: #f5f5f5">
                                <div id="datos" class="tab-pane fade in active">

                                    <!--
                                   Datos del Insumo
                                    -->    
                                    <div class ="row">
                                        <div class="col-md-6">
                                            <div class="row"><div class="col-md-12">
                                                    <strong>Código de Barras:</strong>
                                                    <input type="text" value="<%=CodBar%>" class="form-control" name="codbar" id="codbar" onfocusout="buttonEnable()" onkeypress="return checkKey(event, this);" style="-webkit-text-security: disc;"/>
                                                </div>
                                            </div>
                                            <div class="row"><div class="col-md-6">
                                                    <button class="btn btn-success btn-block" type="submit" name="accion" id="CodigoBarras" value="CodigoBarras" onclick="">CB</button>
                                                </div>
                                                <div class="col-md-6">
                                                    <button class="btn btn-success btn-block" type="submit" name="accion" id="GeneraCodigo" value="GeneraCodigo" onclick=""><span class="glyphicon glyphicon-barcode"></span></button>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="row"><div class="col-md-12">
                                                    <strong>Confirmar Código de Barras:</strong>
                                                    <input type="text" value="<%=CodBar%>" class="form-control" id="confirm-codbar" onclick="" onkeypress="return checkKey(event, this);" onpaste="return false" onfocusout="confirmaCampo('codbar')"/>
                                                </div>
                                            </div>

                                        </div>

                                    </div>
                                    <br>

                                    <%
                                        int contadorLotes = 0;
                                        String idMarca = "";
                                        if (!CodBar.equals("")) {
                                            try {
                                                con.conectar();
                                                rset3 = con.consulta("select F_Cb, F_ClaPro, F_ClaLot, F_FecCad, F_FecFab, F_ClaMar from tb_lote where F_Cb='" + CodBar + "' AND F_ClaPro='" + claveProducto + "' group by F_ClaPro,F_ClaLot, F_FecCad");
                                                while (rset3.next()) {
                                                    contadorLotes++;
                                                }
                                            } catch (Exception e) {
                                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                }
                                            }
                                        }
                                        if (contadorLotes > 1) {
                                    %>
                                    Lote
                                    <input type="text" value="<%=Lote%>" onfocusout="buttonEnable()" class="form-control" name="lot" id="lot" onkeypress="return tabular(event, this)"/>
                                    <select class="form-control" name="list_lote" id="list_lote"  onchange="cambiaLoteCadu(this);" onkeypress="return tabular(event, this)">
                                        <option>--Lote--</option>
                                        <%
                                            if (!CodBar.equals("")) {
                                                try {
                                                    con.conectar();
                                                    rset3 = con.consulta("select F_Cb, F_ClaPro, F_ClaLot, F_FecCad, F_FecFab, F_ClaMar from tb_lote where F_Cb='" + CodBar + "' AND F_ClaPro='" + claveProducto + "' group by F_ClaLot, F_FecCad");
                                                    while (rset3.next()) {
                                                        idMarca = rset3.getString(6);
                                        %>
                                        <option><%=rset3.getString(3)%></option>
                                        <%
                                                    }
                                                } catch (Exception e) {
                                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                } finally {
                                                    try {
                                                        con.cierraConexion();
                                                    } catch (Exception ex) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                    }
                                                }
                                            }
                                        %>
                                    </select>

                                    Caducidad:
                                    <input type="text" value="<%=Cadu%>" onfocusout="buttonEnable()" data-date-format="dd/mm/yyyy" class="form-control" name="cad" id="cad" onclick="" onKeyPress="
                                            return LP_data(event, this);
                                            anade(this, event);
                                            return tabular(event, this);
                                           " maxlength="10" onblur="validaCadu();"/>
                                    <select class="form-control" name="list_cadu" id="list_cadu">
                                        <option>--Caducidad--</option>
                                        <%
                                            if (!CodBar.equals("")) {
                                                try {
                                                    con.conectar();
                                                    rset3 = con.consulta("select F_Cb, F_ClaPro, F_ClaLot, DATE_FORMAT(F_FecCad,'%d/%m/%Y'), F_FecFab, F_ClaMar from tb_lote where F_Cb='" + CodBar + "' AND F_ClaPro='" + claveProducto + "'  group by F_ClaLot, F_FecCad");
                                                    while (rset3.next()) {
                                        %>
                                        <option><%=rset3.getString(4)%></option>
                                        <%
                                                    }
                                                } catch (Exception e) {
                                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                } finally {
                                                    try {
                                                        con.cierraConexion();
                                                    } catch (Exception ex) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                    }
                                                }
                                            }
                                        %>
                                    </select>

                                    <%
                                    } else {
                                    %>
                                    <%
                                        if (!CodBar.equals("")) {
                                            try {
                                                con.conectar();
                                                rset3 = con.consulta("select F_Cb, F_ClaPro, F_ClaLot, F_FecCad, F_FecFab, F_ClaMar from tb_lote where F_Cb='" + CodBar + "' AND F_ClaPro='" + claveProducto + "' group by F_ClaLot, F_FecCad");
                                                while (rset3.next()) {
                                                    idMarca = rset3.getString(6);
                                                    Lote = rset3.getString(3);
                                                }
                                            } catch (Exception e) {
                                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                            } finally {
                                                try {
                                                    con.cierraConexion();
                                                } catch (Exception ex) {
                                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                }
                                            }
                                        }
                                    %>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <strong>Lote</strong>
                                            <input type="text" value="<%=Lote%>" onfocusout="buttonEnable()" class="form-control" name="lot" id="lot" onkeypress="return tabular(event, this)" style="-webkit-text-security: disc;" autocomplete="off"/>
                                            <%
                                                if (!CodBar.equals("")) {
                                                    try {
                                                        con.conectar();
                                                        rset3 = con.consulta("select F_Cb, F_ClaPro, F_ClaLot, DATE_FORMAT(F_FecCad,'%d/%m/%Y'), F_FecFab, F_ClaMar from tb_lote where F_Cb='" + CodBar + "' AND F_ClaPro='" + claveProducto + "' group by F_ClaLot, F_FecCad");
                                                        while (rset3.next()) {
                                                            Cadu = rset3.getString(4);
                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                }
                                            %>
                                        </div>
                                        <div class="col-md-6">
                                            <strong>Confirmar Lote</strong>
                                            <input type="text" class="form-control" name="lot" id="confirm-lot" onkeypress="return tabular(event, this)" onpaste="return false" onfocusout="confirmaCampo('lot')" autocomplete="off"/>
                                        </div>
                                        <div class="col-md-6">
                                            <strong>Caducidad</strong>
                                            <input type="date" value="<%=Cadu%>" onfocusout="buttonEnable()" min="<%=LocalDate.now().plusDays(30)%>" class="form-control" name="cad" id="cad" onpaste="return false" onkeypress="return false;" maxlength="10" style="-webkit-text-security: disc;" autocomplete="off" onchange="validaCad(this.value)"/>

                                        </div>
                                        <div class="col-md-6">
                                            <strong>Confirmar Caducidad</strong>
                                            <input type="date" min="<%= LocalDate.now().plusDays(30)%>" class="form-control" id="confirm-cad" onpaste="return false"  maxlength="10" onfocusout="confirmaCampo('cad')" autocomplete="off"/>

                                            <%
                                                }
                                            %>
                                        </div>
                                    </div>
                                    <div class="row" id="marca">
                                        <div class="col-md-4">
                                            <strong>Marca</strong>
                                            <select class="form-control" name="list_marca" onfocusout="buttonEnable()" onKeyPress="return tabular(event, this)" id="list_marca">
                                                <option value="">Marca</option>
                                                <%
                                                    try {
                                                        con.conectar();
                                                        //ResultSet rset3 = con.consulta("SELECT F_ClaMar,F_DesMar FROM tb_marca order by F_DesMar");
                                                        rset3 = con.consulta("SELECT F_ClaMar,F_DesMar FROM tb_marca where F_DesMar <> '' order by F_DesMar");
                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString("F_ClaMar")%>"
                                                        <%
                                                            if (rset3.getString("F_ClaMar").equals(idMarca)) {
                                                                out.println("selected");
                                                            }
                                                        %>
                                                        ><%=rset3.getString("F_DesMar")%></option>
                                                <%

                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>

                                        </div>
                                        <div class="col-md-1">
                                            <strong>Refresh</strong>
                                            <button class="btn btn-block btn-success glyphicon glyphicon-refresh" type = "submit" value = "refresh" name = "accion" ></button>
                                        </div>
                                        <div class="col-md-1">
                                            <strong>Alta</strong>
                                            <a href="../marcas.jsp" class="btn btn-block btn-success glyphicon glyphicon-upload" target="_blank"></a>
                                            <input value="<%=rset.getString("p.F_ClaProve")%>" name="claPro" id="claPro" class="hidden" onkeypress="return tabular(event, this)" />
                                        </div>


                                        <div class="col-md-6">
                                            <strong>Confirmar Marca</strong>
                                            <select class="form-control"  onKeyPress="return tabular(event, this)" id="confirm-list_marca" onfocusout="confirmaCampo('list_marca')">
                                                <option value="">Marca</option>
                                                <%
                                                    try {
                                                        con.conectar();
                                                        //ResultSet rset3 = con.consulta("SELECT F_ClaMar,F_DesMar FROM tb_marca order by F_DesMar");
                                                        rset3 = con.consulta("SELECT F_ClaMar,F_DesMar FROM tb_marca where F_DesMar <> '' order by F_DesMar");
                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString("F_ClaMar")%>"
                                                        <%
                                                            if (rset3.getString("F_ClaMar").equals(idMarca)) {
                                                                out.println("selected");
                                                            }
                                                        %>
                                                        ><%=rset3.getString("F_DesMar")%></option>
                                                <%

                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>



                                    <div class="row">

                                        <input value="<%=rset.getString("p.F_ClaProve")%>" name="claPro" id="claPro" class="hidden" onkeypress="return tabular(event, this)" />
                                    </div>

                                    <br>
                                    <div class="row">
                                        <div class="col-sm-3">
                                            <strong>Origen:</strong>
                                            <select class="form-control" name="F_Origen" id="F_Origen"  onchange="validaOS(this.value)">
                                                <%
                                                    try {
                                                        con.conectar();
                                                        int IdOrigen = rset.getInt(8);

                                                        if (IdOrigen == 21) {
                                                            rset3 = con.consulta("SELECT F_ClaOri,F_DesOri FROM tb_origen where F_ClaOri = 21;");
                                                        } else {
                                                            rset3 = con.consulta("SELECT F_ClaOri,F_DesOri FROM tb_origen where F_ClaOri not in (21);");
                                                        }

                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString(1)%>"><%=rset3.getString(2)%></option>
                                                <%
                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>
                                        <div class="col-sm-2">
                                            <strong>Costo:</strong>
                                            <input name="F_Costo" id="F_Costo" type="text" class="form-control" />
                                          
                                        </div>

                                        <div class="col-sm-3">
                                            <strong>Proyecto:</strong>
                                            <select class="form-control" name="F_Proyectos" id="F_Proyectos">
                                                <%                                            try {
                                                        con.conectar();

                                                        rset3 = con.consulta("SELECT * FROM tb_proyectos where F_Id='" + proyect + "';");
                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString(1)%>"><%=rset3.getString(2)%></option>
                                                <%
                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>



                                        <div class="col-sm-3">
                                            <strong>Nombre Comercial:</strong>
                                            <%if (nomComercial > 0) {%>
                                            <select class="form-control" name="lMarcaR" onfocusout="buttonEnable()" onKeyPress="return tabular(event, this)" id="lMarcaR" >   
                                                <option value="">Nombre Comercial</option>
                                                <%
                                                    try {
                                                        con.conectar();

                                                        rset3 = con.consulta("select F_Id,F_NombreComercial from tb_nombrecomercial where F_ClaPro = " + claveProducto);
                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString("F_NombreComercial")%>"><%=rset3.getString("F_NombreComercial")%></option>
                                                <%

                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>  
                                            <%      } else {%>
                                            <select class="form-control" name="MarcaR" id="lMarcaR" disabled="" required="false"> 
                                                <option value="">Nombre Comercial</option>
                                            </select>
                                            <%  }  %>                                        

                                        </div>    
                                    </div>
                                    <br>
                                    <div class="row">
                                        <div class="col-sm-4">
                                            <strong>Orden de Suministro:</strong>
                                            <input name="ordenSuministro" maxlength="40" id="ordenSuministro" type="text" onfocusout="buttonEnable()" class="form-control" onkeypress="return tabular(event, this)" onpaste="return false" style="-webkit-text-security: disc;" autocomplete="off" />
                                        </div>
                                        <div class="col-sm-4">
                                            <strong>Confirmar Orden de Suministro:</strong>
                                            <input name="confirm-ordenSuministro" maxlength="40" id="confirm-ordenSuministro" type="text" class="form-control" onkeypress="return tabular(event, this)" onpaste="return false" onfocusout="confirmaCampo('ordenSuministro')" autocomplete="off"/>
                                        </div>
                                    </div  >
                                    <div class="row">
                                        <div class="col-sm-4">
                                            <strong>Carta Canje:</strong>                                            
                                            <input name="cartaCanje" id="cartaCanje" type="text" maxlength="50" class="form-control" onkeypress="return tabular(event, this)"/>
                                        </div>
                                        <div class="col-sm-4">
                                            <strong> Unidad:</strong>
                                            <select class="form-control" name="unidadFonsabi" id="unidadFonsabi"  onChange="validaUnidad(this.value)" onfocusout="buttonEnable()" onKeyPress="return tabular(event, this)" >
                                                <option value="S/U">Sin conocimiento de unidad destino</option>
                                                <%                                            try {
                                                        con.conectar();

                                                        rset3 = con.consulta("SELECT * FROM tb_uniatn AS U WHERE U.F_ClaCli LIKE '%%AI' AND F_StsCli = 'A';");
                                                        while (rset3.next()) {
                                                %>
                                                <option value="<%=rset3.getString(1)%>"><%=rset3.getString(2)%></option>
                                                <%
                                                        }
                                                    } catch (Exception e) {
                                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                                    } finally {
                                                        try {
                                                            con.cierraConexion();
                                                        } catch (Exception ex) {
                                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>

                                    </div>
                                </div>


                                <!--
                                CANTIDADES
                                -->    
                                <div id="cantidadesTab" class="tab-pane fade">

                                    <h5><strong>Tarimas Completas</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="tarimas">Tarimas</label>
                                                <input type="text" class="form-control" onfocusout="buttonEnable()" onpaste="return false" id="TarimasC" name="TarimasC" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="" style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="tarimas">Cajas x Tarima</label>
                                                <input type="text" class="form-control" onfocusout="buttonEnable()" id="CajasxTC" onpaste="return false" name="CajasxTC" placeholder="0" onKeyPress="return justNumbers(event);" onkeyup="totalPiezas()" onclick="" 
                                                       style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="PzsxCC">Piezas x Caja</label>
                                                <input type="text" class="form-control" onfocusout="buttonEnable()" id="PzsxCC" name="PzsxCC" onpaste="return false" placeholder="0" onKeyPress="return justNumbers(event);" onkeyup="totalPiezas()" onclick=""
                                                       style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                    </div>
                                    <hr>
                                    <h5><strong>Tarimas Incompletas</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="tarimasI">Tarimas</label>
                                                <input type="text" class="form-control" id="TarimasI" name="TarimasI" placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onfocusout="buttonEnable()" style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="CajasxTI">Cajas x Tarima</label>
                                                <input type="text" class="form-control" id="CajasxTI" name="CajasxTI" placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onfocusout="buttonEnable()" style="-webkit-text-security: disc;"> 
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="Resto">Resto</label>
                                                <input type="text" class="form-control" id="Resto" name="Resto" placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onfocusout="buttonEnable()" style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="factor">Factor de Empaque</label>
                                                <input type="text" class="form-control" id="factorEmpaque" name="factorEmpaque" value="1" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onpaste="return false" onfocusout="buttonEnable()" style="-webkit-text-security: disc;">
                                            </div>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5><strong>Totales</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="Tarimas">Tarimas</label>
                                                <input type="text" class="form-control" id="Tarimas" name="Tarimas" placeholder="0" onpaste="return false" readonly="" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="Cajas">Cajas Completas</label>
                                                <input type="text" class="form-control" id="Cajas" name="Cajas" placeholder="0" onpaste="return false" readonly="" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="CajasIn">Cajas Incompletas</label>
                                                <input type="text" class="form-control" id="CajasIn" name="CajasIn" placeholder="0" onpaste="return false" readonly="" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="">
                                            </div>
                                        </div>
                                        <br>
                                    </div>

                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="TCajas">Total Cajas</label>
                                                <input type="text" class="form-control" id="TCajas" name="TCajas" placeholder="0" onpaste="return false" readonly="" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="Piezas">Piezas</label>
                                                <input type="text" class="form-control" id="Piezas" name="Piezas" placeholder="0" onpaste="return false" readonly="" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onkeyup="totalPiezas()" onclick="">
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h4><strong>Confirmar cantidades</strong></h4>
                                    <h5><strong>Tarimas Completas</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-tarimas">Tarimas</label>
                                                <input type="text" class="form-control" id="confirm-TarimasC"  onpaste="return false" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onfocusout="confirmaCampo('TarimasC')" onclick="">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-">Cajas x Tarima</label>
                                                <input type="text" class="form-control" id="confirm-CajasxTC" placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);" onclick="" 
                                                       onfocusout="confirmaCampo('CajasxTC')">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="tarimas">Piezas x Caja</label>
                                                <input type="text" class="form-control" id="confirm-PzsxCC"  placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);" onclick=""
                                                       onfocusout="confirmaCampo('PzsxCC')">
                                            </div>
                                        </div>
                                    </div>
                                    <hr>
                                    <h5><strong>Tarimas Incompletas</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-tarimasI">Tarimas</label>
                                                <input type="text" class="form-control" id="confirm-TarimasI"  placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onfocusout="confirmaCampo('TarimasI')">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-CajasxTI">Cajas x Tarima</label>
                                                <input type="text" class="form-control" id="confirm-CajasxTI" placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onclick="" onfocusout="confirmaCampo('CajasxTI')"> 
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-Resto">Resto</label>
                                                <input type="text" class="form-control" id="confirm-Resto"  placeholder="0" onpaste="return false" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onclick="" onfocusout="confirmaCampo('Resto')">
                                            </div>
                                        </div>
                                        <br>
                                    </div>
                                    <br>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="confirm-factor">Factor de Empaque</label>
                                                <input type="text" class="form-control" id="confirm-factorEmpaque" onpaste="return false" value="1" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);" onclick="" onfocusout="confirmaCampo('factorEmpaque')">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div id="volumetriaTab" class="tab-pane fade">
                                    <h4><strong>Volumetría Peso</strong></h4>
                                    <h5><strong>Peso por pieza</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoPieza">Peso</label>
                                                <input type="text" class="form-control" id="pesoPieza" name="pesoPieza" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadPieza">Unidad</label>
                                                <select type="text" class="form-control" id="unidadPesoPieza" onfocusout="buttonEnable()" name="unidadPesoPieza" placeholder="gr" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <h5><strong>Confirmaciór peso por pieza</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoPieza">Peso</label>
                                                <input type="text" class="form-control" id="confirm-pesoPieza" onfocusout="confirmaCampo('pesoPieza')"  placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadPieza">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadPesoPieza" placeholder="gr" onfocusout="confirmaCampo('unidadPesoPieza')" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Peso por Caja</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoCaja">Peso</label>
                                                <input type="text" class="form-control" id="pesoCaja" name="pesoCaja" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadCaja">Unidad</label>
                                                <select type="text" class="form-control" id="unidadPesoCaja" name="unidadPesoCaja" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <h5><strong>Confirmar peso por Caja</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoCaja">Peso</label>
                                                <input type="text" class="form-control" id="confirm-pesoCaja" onfocusout="confirmaCampo('pesoCaja')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadCaja">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadPesoCaja" onfocusout="confirmaCampo('unidadPesoCaja')" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Peso por Caja Concentrada</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoConcentrada">Peso</label>
                                                <input type="text" class="form-control" id="pesoConcentrada" name="pesoConcentrada" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadConcentrada">Unidad</label>
                                                <select type="text" class="form-control" id="unidadPesoConcentrada" name ="unidadPesoConcentrada" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <h5><strong>Confirmar peso por Caja Concentrada</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoConcentrada">Peso</label>
                                                <input type="text" class="form-control" id="confirm-pesoConcentrada" onfocusout="confirmaCampo('pesoConcentrada')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadConcentrada">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadPesoConcentrada" onfocusout="confirmaCampo('unidadPesoConcentrada')" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Peso por Tarima</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoTarima">Peso</label>
                                                <input type="text" class="form-control" id="pesoTarima" name="pesoTarima" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadTarima">Unidad</label>
                                                <select type="text" class="form-control" id="unidadPesoTarima" name="unidadPesoTarima" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <h5><strong>Confirmar peso por Tarima</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="pesoTarima">Peso</label>
                                                <input type="text" class="form-control" id="confirm-pesoTarima" onfocusout="confirmaCampo('pesoTarima')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="unidadTarima">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadPesoTarima" onfocusout="confirmaCampo('unidadPesoTarima')" placeholder="Unidad" > 
                                                    <option value="gr">Gramos (gr)</option>
                                                    <option value="kgr">Kilogramos (kgr)</option>
                                                    <option value="t">Toneladas (t)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                </div>


                                <!--
                                Volumetria peso
                                -->

                                <div id="volumetriaVolTab" class="tab-pane fade">
                                    <h4><strong>Volumetría Volumen</strong></h4>
                                    <br>
                                    <h5><strong>Volúmen por pieza</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoPieza">Alto</label>
                                                <input type="text" class="form-control" id="altoPieza" name="altoPieza" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoPieza">Ancho</label>
                                                <input type="text" class="form-control" id="anchoPieza" name="anchoPieza" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoPieza">Largo</label>
                                                <input type="text" class="form-control" id="largoPieza" name="largoPieza" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolPieza">Unidad</label>
                                                <select type="text" class="form-control" id="unidadVolPieza" name="unidadVolPieza" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <br>

                                    <!--
                                    Volumetria Dimensiones 
                                    -->
                                    <h5><strong>Confirmar Volúmen por pieza</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoPieza">Alto</label>
                                                <input type="text" class="form-control" id="confirm-altoPieza" onfocusout="confirmaCampo('altoPieza')"  placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoPieza">Ancho</label>
                                                <input type="text" class="form-control" id="confirm-anchoPieza" onfocusout="confirmaCampo('anchoPieza')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoPieza">Largo</label>
                                                <input type="text" class="form-control" id="confirm-largoPieza" onfocusout="confirmaCampo('largoPieza')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolPieza">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadVolPieza" onfocusout="confirmaCampo('unidadVolPieza')" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Volúmen por Caja</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoCaja">Alto</label>
                                                <input type="text" class="form-control" id="altoCaja" name="altoCaja" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoCaja">Ancho</label>
                                                <input type="text" class="form-control" id="anchoCaja" name="anchoCaja" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoCaja">Largo</label>
                                                <input type="text" class="form-control" id="largoCaja" name="largoCaja" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolCaja">Unidad</label>
                                                <select type="text" class="form-control" id="unidadVolCaja" name="unidadVolCaja" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <br>
                                    <h5><strong>Confirmar Volúmen por Caja</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoCaja">Alto</label>
                                                <input type="text" class="form-control" id="confirm-altoCaja" onfocusout="confirmaCampo('altoCaja')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoCaja">Ancho</label>
                                                <input type="text" class="form-control" id="confirm-anchoCaja" onfocusout="confirmaCampo('anchoCaja')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoCaja">Largo</label>
                                                <input type="text" class="form-control" id="confirm-largoCaja" onfocusout="confirmaCampo('largoCaja')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolCaja">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadVolCaja" onfocusout="confirmaCampo('unidadVolCaja')" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Volúmen por Caja Concentrada</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoConcentrada">Alto</label>
                                                <input type="text" class="form-control" id="altoConcentrada" name="altoConcentrada" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoConcentrada">Ancho</label>
                                                <input type="text" class="form-control" id="anchoConcentrada" name="anchoConcentrada" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoConcentrada">Largo</label>
                                                <input type="text" class="form-control" id="largoConcentrada" name="largoConcentrada" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolConcentrada">Unidad</label>
                                                <select type="text" class="form-control" id="unidadVolConcentrada" name="unidadVolConcentrada" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <br>
                                    <h5><strong>Confirmar Volúmen por Caja Concentrada</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoConcentrada">Alto</label>
                                                <input type="text" class="form-control" id="confirm-altoConcentrada" onfocusout="confirmaCampo('altoConcentrada')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoConcentrada">Ancho</label>
                                                <input type="text" class="form-control" id="confirm-anchoConcentrada" onfocusout="confirmaCampo('anchoConcentrada')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoConcentrada">Largo</label>
                                                <input type="text" class="form-control" id="confirm-largoConcentrada" onfocusout="confirmaCampo('largoConcentrada')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolConcentrada">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadVolConcentrada" onfocusout="confirmaCampo('unidadVolConcentrada')" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <hr>
                                    <h5><strong>Volúmen por Tarima</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoTarima">Alto</label>
                                                <input type="text" class="form-control" id="altoTarima" name="altoTarima" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoTarima">Ancho</label>
                                                <input type="text" class="form-control" id="anchoTarima" name="anchoTarima" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoTarima">Largo</label>
                                                <input type="text" class="form-control" id="largoTarima" name="largoTarima" onfocusout="buttonEnable()" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolTarima">Unidad</label>
                                                <select type="text" class="form-control" id="unidadVolTarima" name="unidadVolTarima" onfocusout="buttonEnable()" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                    <br>
                                    <h5><strong>Confirmar Volúmen por Tarima</strong></h5>
                                    <div class="row form-inline" style="text-align: right;">
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="altoTarima">Alto</label>
                                                <input type="text" class="form-control" id="confirm-altoTarima" onfocusout="confirmaCampo('altoTarima')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="anchoTarima">Ancho</label>
                                                <input type="text" class="form-control" id="confirm-anchoTarima" onfocusout="confirmaCampo('anchoTarima')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="largoTarima">Largo</label>
                                                <input type="text" class="form-control" id="confirm-largoTarima" onfocusout="confirmaCampo('largoTarima')" placeholder="0" onKeyPress="return justNumbers(event);
                                                        return handleEnter(event);">
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="unidadVolTarima">Unidad</label>
                                                <select type="text" class="form-control" id="confirm-unidadVolTarima" onfocusout="confirmaCampo('unidadVolTarima')" placeholder="Unidad" > 
                                                    <option value="mm">Milímetros (mm)</option>
                                                    <option value="cm">Centímetros (cm)</option>
                                                    <option value="m">Metros (m)</option>
                                                </select>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </div>


                            <h5><strong>Observaciones</strong></h5>
                            <textarea class="form-control" id="Obser" name="Obser"></textarea>

                            <hr/>
                            <div class="row">
                                <%
                                    //if (tipo.equals("2") || tipo.equals("3") || tipo.equals("1")) {
%>

                                <h4 class="col-sm-2 text-right">Folio de Remisión:</h4>
                                <div class="col-sm-4"><input class="form-control" value="<%=folioRemi%>" name="folioRemi" id="folioRemi" onkeypress="return tabular(event, this)" /></div>

                                <div class="col-sm-6">          
                                    <button class="btn btn-block btn-success" name="accion" id="accion" value="guardarLote" onclick="return validaCompra();" >Guardar Lote</button>
                                </div>
                                <%
                                    // }
                                %>
                            </div>



                            <%
                                    }
                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                    }
                                }
                            %>
                        </div>
                    </div>
                </div>
            </form>
            <% } else {  %>      

            <div class="row">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <h4 class="col-sm-3">Fecha vencida:</h4>
                            <div class="col-sm-2" style="width: 61%;"><h4>La orden de compra que desea ver ya ha llegado a su fecha de vencimiento si desea continuar porfavor de comunicarse con soporte a compras.</h4></div>
                        </div>
                    </div>
                    <hr/>
                </div>
            </div>    

            <%
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                } finally {
                    try {
                        con.cierraConexion();
                    } catch (Exception ex) {
                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                    }
                }
            %>
            <div class="row">
                <div class="table-responsive">
                    <table class="table table-bordered table-striped" style="width: 100%">
                        <thead>
                        <th>Remisión</th>
                        <th><a name="ancla"></a>Código de Barras</th>
                        <th>CLAVE</th>
                        <th>Descripción</th>  
                        <th>Presentación</th>
                        <th>Lote</th>
                        <th>Caducidad</th>
                        <th>Carta canje</th>
                        <th>Orden de suministro</th>
                        <th>Nombre comercial</th>
                        <th>Cantidad</th>                      
                        <th>Costo U.</th>                     
                        <th>IVA</th>                       
                        <th>Importe</th>
                        <th></th>
                        </thead>
                        <%
                            int banCompra = 0;
                            try {
                                con.conectar();
                                rset = con.consulta("SELECT C.F_Cb,C.F_ClaPro,M.F_DesPro,C.F_Lote,C.F_FecCad,C.F_Pz,F_IdCom, C.F_Costo, C.F_ImpTo, C.F_ComTot, C.F_FolRemi, M.F_PrePro, C.F_OrdenSuministro, C.F_CartaCanje, C.F_MarcaComercial FROM tb_compratemp C INNER JOIN tb_medica M ON C.F_ClaPro=M.F_ClaPro WHERE F_OrdCom='" + noCompra + "' and F_Estado = '1'");
                                while (rset.next()) {
                                    banCompra = 1;
                        %>
                        <tbody>
                        <td><%=rset.getString("C.F_FolRemi")%></td>
                        <td><%=rset.getString(1)%></td>
                        <td><%=rset.getString(2)%></td>              
                        <td><%=rset.getString(3)%></td>
                        <td><%=rset.getString(12)%></td>
                        <td><%=rset.getString(4)%></td>
                        <td><%=df3.format(df2.parse(rset.getString(5)))%></td>
                        <td><%=rset.getString("C.F_CartaCanje")%></td>
                        <td><%=rset.getString(13)%></td>
                        <td><%=rset.getString("C.F_MarcaComercial")%></td>
                        <td><%=formatter.format(rset.getDouble(6))%></td>
                        <td><%=formatterDecimal.format(rset.getDouble("C.F_Costo"))%></td>
                        <td><%=formatterDecimal.format(rset.getDouble("C.F_ImpTo"))%></td>          
                        <td><%=formatterDecimal.format(rset.getDouble("C.F_ComTot"))%></td>              
                        <td>

                            <form method="get" action="../ModificacionesHH">
                                <input name="id" type="text" style="" class="hidden" value="<%=rset.getString(7)%>" />
                                <button class="btn btn-warning" name="accion" value="modificarCompraAuto"><span class="glyphicon glyphicon-pencil" ></span></button>
                                <button class="btn btn-success" onclick="return confirm('¿Seguro de que desea eliminar?');" name="accion" value="eliminarCompraAuto"><span class="glyphicon glyphicon-remove"></span>
                                </button>
                            </form>
                        </td>
                        </body>
                        <%
                                }
                            } catch (Exception e) {
                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                            } finally {
                                try {
                                    con.cierraConexion();
                                } catch (Exception ex) {
                                    Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                }
                            }

                        %>
                    </table>
                </div>
                <%                    if (banCompra == 1) {
                %>
            </div>
            <hr/>
            <div class="row">
                <div class="col-lg-6">
                    <form action="../nuevoAutomaticaLotes" method="post">
                        <input name="fol_gnkl" type="text" style="" class="hidden" value="<%=noCompra%>" />
                        <button  value="Eliminar" name="accion" class="btn btn-success btn-block" onclick="return confirm('¿Seguro que desea eliminar la compra?');">Cancelar Compra</button>
                    </form>
                </div>
                <div class="col-lg-6">
                    <form action="../ModificacionesHH" method="post">
                        <input name="fol_gnkl" type="text" style="" class="hidden" value="<%=noCompra%>" />
                        <button  value="verificarCompraAuto" name="accion" class="btn btn-success btn-block" onclick="return confirm('¿Seguro que desea verificar la compra?');">Ingresar OC</button>
                    </form>
                </div>
            </div> 
            <%
                }
            %>

        </div>



        <!--
        Modal
        -->
        <div class="modal fade" id="Rechazar" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form action="Rechazos" method="get">
                        <div class="modal-header">
                            <div class="row">
                                <div class="col-sm-5">
                                    <h4 class="modal-title" id="myModalLabel">Rechazar Orden de Compra</h4>
                                </div>
                                <div class="col-sm-2">
                                    <input name="NoCompraRechazo" id="NoCompraRechazo" value="<%=noCompra%>" class="form-control" readonly="" />
                                </div>
                            </div>
                            <div class="row">
                                <%
                                    try {
                                        con.conectar();
                                        rset = con.consulta("select i.F_NoCompra, i.F_FecSur, i.F_HorSur, p.F_NomPro, p.F_ClaProve from tb_pedido_sialss i, tb_proveedor p where i.F_Provee = p.F_ClaProve and F_StsPed = '1' and F_NoCompra = '" + noCompra + "' and F_recibido='0' group by F_NoCompra");
                                        while (rset.next()) {
                                %>
                                <div class="col-sm-12">
                                    Proveedor:<%=rset.getString("p.F_NomPro")%>
                                </div>
                                <div class="col-sm-12">
                                    Fecha y Hora <%=rset.getString("i.F_FecSur")%> - <%=rset.getString("i.F_HorSur")%>
                                </div>
                                <%
                                        }
                                    } catch (Exception e) {
                                        Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                    } finally {
                                        try {
                                            con.cierraConexion();
                                        } catch (Exception ex) {
                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                        }
                                    }
                                %>
                            </div>
                        </div>
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-sm-12">
                                    <h4>Observaciones de Rechazo</h4>
                                </div>
                                <div class="col-sm-12">
                                    <textarea class="form-control" placeholder="Observaciones" name="rechazoObser" id="rechazoObser" rows="5"></textarea>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-12">
                                    <h4>Fecha de nueva recepción</h4>
                                </div>
                                <div class="col-sm-6">
                                    <input type="date" min="<%=df2.format(new Date())%>" class="form-control" id="FechaOrden" name="FechaOrden" />
                                </div>
                                <div class="col-sm-6">
                                    <select class="form-control" id="HoraOrden" name="HoraOrden">
                                        <%
                                            for (int i = 0; i < 24; i++) {
                                                if (i != 24) {
                                        %>
                                        <option value="<%=i%>:00"><%=i%>:00</option>
                                        <option value="<%=i%>:30"><%=i%>:30</option>
                                        <%
                                        } else {
                                        %>
                                        <option value="<%=i%>:00"><%=i%>:00</option>
                                        <%
                                                }
                                            }
                                        %>
                                    </select>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-sm-12">
                                    <h4>Correo del proveedor</h4>
                                    <input type="email" class="form-control" id="correoProvee" name="correoProvee" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-6">
                                    <h4>Claves a Cancelar</h4>
                                    <h6>*Deseleccione las claves que no va a cancelar</h6>
                                </div>
                                <div class="col-sm-6">
                                    <div class="checkbox">
                                        <label>
                                            <h4><input type="checkbox" checked name="todosChk" id="todosChk" onclick="checkea(this)">Seleccionar todas</h4>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-12">
                                    <%
                                        try {
                                            con.conectar();
                                            rset = con.consulta("select F_Clave from tb_pedido_sialss where F_NoCompra = '" + noCompra + "' ");
                                            int columna = 1;
                                            while (rset.next()) {
                                                if (columna == 1) {
                                                }
                                    %>

                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" checked="" name="chkCancela" value="<%=rset.getString(1)%>"><%=rset.getString(1)%>
                                        </label>
                                    </div>
                                    <%
                                                if (columna % 5 == 0) {
                                                    columna = 0;
                                                }
                                                columna++;
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (Exception ex) {
                                                Logger.getLogger("compraAuto3.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>

                                </div>
                            </div>
                            <div class="text-center" id="imagenCarga" style="display: none;" > 
                                <img src="../imagenes/ajax-loader-1.gif">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-success" onclick="return validaRechazo();" name="accion" value="Rechazar">Rechazar OC</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>

<script src="../js/jquery-2.1.4.min.js" type="text/javascript"></script>
<script src="../js/bootstrap.js"></script>
<script src="../js/funcIngresos.js"></script>
<script src="../js/select2.js"></script>

<script type="text/javascript">

                                $("#selectClave").select2();
                                $("#list_marca").select2();
                                $("#unidadFonsabi").select2();

                                $("#Proveedor").select2();
                                $("#NoCompra").select2();

                                function checkea(obj) {
                                    var cbs = document.getElementsByName('chkCancela');
                                    if (obj.checked) {
                                        for (var i = 0; i < cbs.length; i++)
                                        {
                                            cbs[i].checked = true;
                                        }
                                    } else {
                                        for (var i = 0; i < cbs.length; i++)
                                        {
                                            cbs[i].checked = false;
                                        }
                                    }
                                }
                                function validaRechazo() {
                                    var obser = document.getElementById('rechazoObser').value;
                                    var fechaN = document.getElementById('FechaOrden').value;
                                    var horaN = document.getElementById('HoraOrden').value;
                                    var correoProvee = document.getElementById('correoProvee').value;
                                    if (obser === "") {
                                        alert('Ingrese las observaciones del rechazo.');
                                        return false;
                                    }
                                    if (fechaN === "") {
                                        alert('Ingrese nueva fecha de recepción.');
                                        return false;
                                    }
                                    if (horaN === "0:00") {
                                        alert('Ingrese nueva hora de recepción.');
                                        return false;
                                    }
                                    if (correoProvee === "0:00") {
                                        alert('Ingrese correo de proveedor.');
                                        return false;
                                    }
                                    var con = confirm('¿Seguro que desea rechazar la OC?');
                                    if (con === false) {
                                        return false;
                                    }
                                    document.getElementById('imagenCarga').style.display = 'block';
                                }

                                function justNumbers(e)
                                {
                                    var keynum = window.event ? window.event.keyCode : e.which;
                                    if ((keynum === 8) || (keynum === 46))
                                        return true;
                                    return /\d/.test(String.fromCharCode(keynum));
                                }

                                otro = 0;
                                function LP_data(e, esto) {
                                    var key = (document.all) ? e.keyCode : e.which; //codigo de tecla. 
                                    if (key < 48 || key > 57)//si no es numero 
                                        return false; //anula la entrada de texto.
                                    else
                                        anade(esto);
                                }
                                function anade(esto) {

                                    if (esto.value.length > otro) {
                                        if (esto.value.length === 2) {
                                            esto.value += "/";
                                        }
                                    }
                                    if (esto.value.length > otro) {
                                        if (esto.value.length === 5) {
                                            esto.value += "/";
                                        }
                                    }
                                    if (esto.value.length < otro) {
                                        if (esto.value.length === 2 || esto.value.length === 5) {
                                            esto.value = esto.value.substring(0, esto.value.length - 1);
                                        }
                                    }
                                    otro = esto.value.length;
                                }

                                var formatNumber = {
                                    separador: ",", sepDecimal: '.',
                                    formatear: function (num) {
                                        num += '';
                                        var splitStr = num.split('.');
                                        var splitLeft = splitStr[0];
                                        var splitRight = splitStr.length > 1 ? this.sepDecimal + splitStr[1] : '';
                                        var regx = /(\d+)(\d{3})/;
                                        while (regx.test(splitLeft)) {
                                            splitLeft = splitLeft.replace(regx, '$1' + this.separador + '$2');
                                        }
                                        return this.simbol + splitLeft + splitRight;
                                    },
                                    new : function (num, simbol) {
                                        this.simbol = simbol || '';
                                        return this.formatear(num);
                                    }
                                };
                                function totalPiezas() {
                                    var totalTarimas = 0;
                                    var TarimasC = document.getElementById('TarimasC').value;
                                    var CajasxTC = document.getElementById('CajasxTC').value;
                                    var PzsxCC = document.getElementById('PzsxCC').value;
                                    var TarimasI = document.getElementById('TarimasI').value;
                                    var CajasxTI = document.getElementById('CajasxTI').value;
                                    var Resto = document.getElementById('Resto').value;
                                    //alert("Resto = "+Resto);
                                    if (TarimasC === "") {
                                        TarimasC = 0;
                                    }
                                    if (CajasxTC === "") {
                                        CajasxTC = 0;
                                    }
                                    if (PzsxCC === "") {
                                        PzsxCC = 0;
                                    }
                                    if (TarimasI === "") {
                                        TarimasI = 0;
                                    }
                                    if (CajasxTI === "") {
                                        CajasxTI = 0;
                                    }

                                    if (Resto === "") {
                                        Resto = 0;
                                    }

                                    var totalCajas = parseInt(CajasxTC) * parseInt(TarimasC) + parseInt(CajasxTI);
                                    document.getElementById('TCajas').value = formatNumber.new(totalCajas);
                                    if (Resto === 0) {

                                        document.getElementById('CajasIn').value = formatNumber.new(0);
                                    } else {

                                        document.getElementById('TCajas').value = formatNumber.new(totalCajas + 1);
                                        document.getElementById('CajasIn').value = formatNumber.new(1);

                                    }


                                    if (parseInt(CajasxTI) !== parseInt(0)) {
                                        TarimasI = parseInt(1);
                                    }

                                    if ((parseInt(CajasxTI) === parseInt(0)) && (parseInt(TarimasC) !== parseInt(0))) {
                                        totalTarimas = parseInt(TarimasC);
                                    } else {
                                        totalTarimas = parseInt(TarimasC) + parseInt(TarimasI);
                                    }


                                    if (totalTarimas === 0 && Resto !== 0) {
                                        totalTarimas = totalTarimas + 1;
                                    }

                                    document.getElementById('Tarimas').value = formatNumber.new(totalTarimas);
                                    var totalCajas = parseInt(CajasxTC) * parseInt(TarimasC) + parseInt(CajasxTI);
                                    document.getElementById('Cajas').value = formatNumber.new(totalCajas);
                                    var totalPiezas = parseInt(PzsxCC) * parseInt(totalCajas);
                                    document.getElementById('Piezas').value = formatNumber.new(totalPiezas + parseInt(Resto));
                                }

                                function validaCadu() {
                                    var cad = document.getElementById('cad').value;
                                    var cartaCanje = document.getElementById('cartaCanje').value;
                                    if (cad === "") {
                                        alert("Falta Caducidad");
                                        document.getElementById('cad').focus();
                                        cartaCanje === "";
                                        return false;
                                    } else {
                                        if (cad.length < 10) {
                                            alert("Caducidad Incorrecta");
                                            document.getElementById('cad').focus();
                                            cartaCanje === "";
                                            return false;
                                        } else {
                                            var dtFechaActual = new Date();
                                            var sumarDias = parseInt(1);
                                            dtFechaActual.setDate(dtFechaActual.getDate() + sumarDias);
                                            var fechaSpl = cad.split("/");
                                            var Caducidad = fechaSpl[2] + "-" + fechaSpl[1] + "-" + fechaSpl[0];
                                            cartaCanje === "";
                                            if (Date.parse(dtFechaActual) >= Date.parse(Caducidad)) {
                                                alert("Verifique la caducidad");
                                                document.getElementById('cad').focus();
                                                return false;
                                            }
                                        }
                                    }
                                }

                                function validaOS(value) {
                                    if (value !== '4' && value !== '5' && value !== '10' && value !== '11' && value !== '14' && value !== '15' && value !== '16' && value !== '17' && value !== '18' && value !== '19' && value !== '20' && value !== '22' ) {
                                        document.getElementById('ordenSuministro').value = "";
                                        document.getElementById('ordenSuministro').disabled = true;
                                        document.getElementById('confirm-ordenSuministro').value = "";
                                        document.getElementById('confirm-ordenSuministro').disabled = true;
                                    } else {
                                        document.getElementById('ordenSuministro').disabled = false;
                                        document.getElementById('confirm-ordenSuministro').disabled = false;
                                    }
                                    if (value !== '19') {
                                        document.getElementById('unidadFonsabi').value = "";
                                        document.getElementById('unidadFonsabi').disabled = true;
                                    } else {
                                        document.getElementById('unidadFonsabi').disabled = false;
                                    }
                                }

                                validaOS(0);

                                function validaUnidad(value) {

                                    var unidadFonsabi = document.getElementById('unidadFonsabi').options[document.getElementById('unidadFonsabi').selectedIndex].text;
                                    if (unidadFonsabi !== "") {
                                        alert("\t La unidad seleccionada es: \n \t" + unidadFonsabi);
                                    }
                                }
                                ;

                                function validaCompra() {

                                    var origen = document.getElementById('F_Origen').value;
                                    if (origen === '4' || origen === '5' || origen === '10' || origen === '11' || origen === '14' || origen === '15' || origen === '16' || origen === '17' || origen === '18' || origen === '19' || origen === '22' || origen === '20') {
                                        var ordenSuministro = document.getElementById('ordenSuministro').value;

                                        var confirmordenSuministro = document.getElementById('confirm-ordenSuministro').value;
                                        var coincide = (ordenSuministro === confirmordenSuministro);
                                        if (ordenSuministro === "") {
                                            alert("Falta Orden de Suministro");
                                            document.getElementById('ordenSuministro').focus();
                                            return false;
                                        }
                                        if (!coincide) {
                                            alert("No coinciden los datos de la orden de suministro");
                                            return false;
                                        }
                                    }

                                    if (origen === '19') {
                                        var unidad = document.getElementById('unidadFonsabi').value;
                                        console.log("unidad: " + unidad);
                                        if (unidad === "" || unidad === null) {
                                            alert("Falta seleccionar unidad");
                                            return false;
                                        } else {
                                            var unidadFonsabi = document.getElementById('unidadFonsabi').options[document.getElementById('unidadFonsabi').selectedIndex].text;

                                            var conf = confirm("\tConfirmar si la unidad seleccionada es: \n\n\t" + unidadFonsabi);
                                            if (conf === false) {
                                                return false;
                                            }
                                        }

                                    }

                                    var folioRemi = document.getElementById('folioRemi').value;
                                    if (folioRemi === "") {
                                        alert("Falta Folio de Remisión");
                                        document.getElementById('folioRemi').focus();
                                        return false;
                                    }

                                    var codBar = document.getElementById('codbar').value;
                                    if (codBar === "") {
                                        alert("Falta Código de Barras");
                                        document.getElementById('codbar').focus();
                                        return false;
                                    }

                                    var lot = document.getElementById('lot').value;
                                    if (lot === "" || lot === "-") {
                                        alert("Falta Lote");
                                        document.getElementById('lot').focus();
                                        return false;
                                    }

                                    var marca = document.getElementById('list_marca').value;
                                    if (marca === "" || marca === "-") {
                                        alert("Falta Marca");
                                        document.getElementById('list_marca').focus();
                                        return false;
                                    }

                                    var F_Costo = document.getElementById('F_Costo').value;
                                    if (F_Costo === "" || F_Costo === "-") {
                                        alert("Falta Costo");
                                        document.getElementById('F_Costo').focus();
                                        return false;
                                    }
                                    var F_Proyectos = document.getElementById('F_Proyectos').value;
                                    if (F_Proyectos === "" || F_Proyectos === "-") {
                                        alert("Seleccione Proyecto");
                                        document.getElementById('F_Proyectos').focus();
                                        return false;
                                    }

                                    var cadu = document.getElementById('cad').value;
                                    var tipoInsumo = document.getElementById('tipoInsumo').value;
                                    if (tipoInsumo === "CONTROLADO") {
                                        cartaCanje === "";
                                        var fechaActual = new Date();
                                        var sumarMes = parseInt(6);
                                        fechaActual.setMonth(fechaActual.getMonth() + sumarMes);

                                        var fechaSpl = cadu.split("/");
                                        var Caducidad2 = fechaSpl[2] + "-" + fechaSpl[1] + "-" + fechaSpl[0];

                                        if (Date.parse(fechaActual) >= Date.parse(Caducidad2)) {

                                            var mensaje = confirm("Deseas ingresar insumo CONTROLADO con caducidad menor a 6 meses");

                                            if (mensaje) {
                                                var cartaCanje = document.getElementById('cartaCanje').value;
                                                if (cartaCanje === "") {
                                                    alert("Ingresar Carta Canje");
                                                    document.getElementById('cartaCanje').focus();
                                                    return false;
                                                }
                                            } else {
                                                return false;
                                                cartaCanje === "";
                                            }
                                        } else {
                                            var dtFechaActual = new Date();
                                            var sumYear = parseInt(1);
                                            dtFechaActual.setFullYear(dtFechaActual.getFullYear() + sumYear);
                                            var fechaSpl = cadu.split("/");
                                            var Caducidad = fechaSpl[2] + "-" + fechaSpl[1] + "-" + fechaSpl[0];
                                            cartaCanje === "";

                                            if (Date.parse(dtFechaActual) >= Date.parse(Caducidad)) {
                                                var cartaCanje = document.getElementById('cartaCanje').value;
                                                if (cartaCanje === "") {
                                                    alert("Ingresar Carta Canje");
                                                    document.getElementById('cartaCanje').focus();
                                                    return false;
                                                }
                                            }
                                        }
                                    }


                                    var Piezas = document.getElementById('Piezas').value;
                                    if (Piezas === "" || Piezas === "0") {
                                        document.getElementById('Piezas').focus();
                                        alert("Favor de llenar todos los datos");
                                        return false;
                                    }

                                    var cantRecibida = document.getElementById('cantRecibida').value;
                                    var cantTotal = document.getElementById('cantRecibir').value;
                                    cantRecibida = cantRecibida.replace(/,/gi, "");
                                    cantTotal = cantTotal.replace(/,/gi, "");
                                    Piezas = Piezas.replace(/,/gi, "");
                                    var nCantidad = parseInt(Piezas) + parseInt(cantRecibida);
                                    if (nCantidad > parseInt(cantTotal)) {
                                        alert("Excede la cantidad a recibir, favor de verificar");
                                        return false;

                                    }

    <%if (nomComercial > 0) {%>

                                    var nomcomer = document.getElementById('lMarcaR').value;
                                    if (nomcomer === "" && codBar !== "") {
                                        console.log("nomcomer: " + nomcomer + " cb: " + codBar);
                                        document.getElementById('lMarcaR').focus();
                                        alert("Seleccionar nombre comercial");
                                        return false;
                                    }
    <% }%>
                                }


                                function tabular(e, obj)
                                {
                                    tecla = (document.all) ? e.keyCode : e.which;
                                    if (tecla !== 13)
                                        return;
                                    frm = obj.form;
                                    for (i = 0; i < frm.elements.length; i++)
                                        if (frm.elements[i] === obj)
                                        {
                                            if (i === frm.elements.length - 1)
                                                i = -1;
                                            break
                                        }
                                    if (frm.elements[i + 1].disabled === true)
                                        tabular(e, frm.elements[i + 1]);
                                    else
                                        frm.elements[i + 1].focus();
                                    return false;
                                }

                                function cambiaLoteCadu(elemento) {
                                    var indice = elemento.selectedIndex;
                                    document.getElementById('list_cadu').selectedIndex = indice;
                                    document.getElementById('lot').value = document.getElementById('list_lote').value;
                                    document.getElementById('cad').value = document.getElementById('list_cadu').value;
                                }


                                function checkKey(e, obj) {
                                    tecla = (document.all) ? e.keyCode : e.which;
                                    if (tecla !== 13)
                                        return;
                                    frm = obj.form;
                                    for (i = 0; i < frm.elements.length; i++)
                                        if (frm.elements[i] === obj)
                                        {
                                            if (i === frm.elements.length - 1)
                                                i = -1;
                                            break
                                        }
                                    if (frm.elements[i + 1].disabled === true)
                                        tabular(e, frm.elements[i + 1]);
                                    else
                                        frm.elements[i + 1].focus();
                                    document.getElementById('CodigoBarras').click();
                                    return false;
                                }
                                function confirmaCampo(idCampo) {
                                    var valor = $("#" + idCampo).val();
                                    var confirm = $("#confirm-" + idCampo).val();
                                    if (valor !== confirm) {
                                        $("#accion").prop("disabled", true);
                                        document.getElementById("confirm-" + idCampo).style.border = "3px solid #a94442";
                                    } else {
                                        $("#confirm-" + idCampo).css("border-color", "#3c763d");
                                        document.getElementById("confirm-" + idCampo).style.border = "1px solid #3c763d";
                                        buttonEnable(true);
                                    }
                                }

                                function buttonEnable(confirm) {
                                    $("#accion").prop("disabled", false);
                                    var fields = ['lot', 'cad', 'list_marca', 'TarimasC', 'CajasxTC', 'PzsxCC', 'TarimasI', 'CajasxTI', 'Resto'];
                                    var volumetriaFields = ['pesoPieza',
                                        'unidadPesoPieza', 'pesoCaja', 'unidadPesoCaja', 'pesoConcentrada', 'unidadPesoConcentrada', 'pesoTarima', 'unidadPesoTarima',
                                        'altoPieza', 'largoPieza', 'anchoPieza', 'unidadVolPieza', 'altoCaja', 'largoCaja', 'anchoCaja', 'unidadVolCaja', 'altoConcentrada',
                                        'largoConcentrada', 'anchoConcentrada', 'unidadVolConcentrada', 'altoTarima', 'largoTarima', 'anchoTarima', 'unidadVolTarima'];
                                    var checkVolumetria = document.getElementById('volumetriaCheck').checked;
                                    if (checkVolumetria === true) {
                                        fields = fields.concat(volumetriaFields);
                                    }
                                    for (var i = 0; i < fields.length; i++) {
                                        var idCampo = fields[i];
                                        var enable = ($("#" + idCampo).val() === $("#confirm-" + idCampo).val()) && $("#" + idCampo).val();
                                        if (!enable) {
                                            if (!confirm) {
                                                document.getElementById("confirm-" + fields[i]).style.border = "3px solid #a94442";
                                            }
                                            console.log("CAMPO NO COINCIDE: " + fields[i]);
                                            $("#accion").prop("disabled", true);
                                            break;
                                        }
                                    }
//            var enable= $("#").val() === $("#confirm-"+idCampo).val();

                                }



                                function validaCad() {
                                    var cad = document.getElementById('cad').value;
                                    var tipoInsumo = document.getElementById('tipoInsumo').value;
                                    if (tipoInsumo === "CONTROLADO") {
                                        var dtFechaActual = new Date();
                                        var sumarDias = parseInt(1);
                                        dtFechaActual.setFullYear(dtFechaActual.getFullYear() + sumarDias);
                                        var fechaSpl = cad.split("/");
                                        var Caducidad = fechaSpl[2] + "-" + fechaSpl[1] + "-" + fechaSpl[0];

                                        if (Date.parse(dtFechaActual) <= Date.parse(Caducidad)) {

                                            document.getElementById('cartaCanje').disabled = true;
                                            document.getElementById('cartaCanje').value = "";

                                        } else
                                            document.getElementById('cartaCanje').disabled = false;
                                    } else
                                        document.getElementById('cartaCanje').disabled = true;

                                }
                                $("#cartaCanje").prop("disabled", true);




                                function checkVolumetria()
                                {
                                    var checkbox = document.getElementById('volumetriaCheck');
                                    if (checkbox.checked !== true)
                                    {
                                        document.getElementById("volumetriaTabSel").style.display = "none";
                                        document.getElementById("volumetriaVolTabSel").style.display = "none";
                                    } else {
                                        document.getElementById("volumetriaTabSel").style.display = "block";
                                        document.getElementById("volumetriaVolTabSel").style.display = "block";
                                    }
                                }
                                $("#accion").prop("disabled", true);



</script>

</html>
