<%@page import="java.lang.String"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.logging.Level"%>
<%@page import="conn.ConectionDB"%>
<%@page import="java.util.Date"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
    int IdOrigen = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();
    String vOrden = "", vRemi = "", tipoRV = "", UbicaN = "", MarcaR="", ClaPro="", unidadF = "";

    try {
        
        vOrden = (String) sesion.getAttribute("vOrden");
        vRemi = (String) sesion.getAttribute("vRemi");
        unidadF = (String) sesion.getAttribute("tipoRV");        
    } catch (Exception e) {
    }

    try {
        String Folio = "";
        
        String folio[] = null;
        Folio = request.getParameter("NoCompra");
        sesion.removeAttribute("tipoRV");
        if (!Folio.equals("")) {
            folio = Folio.split(",");
            sesion.setAttribute("vOrden", folio[0]);
            sesion.setAttribute("vRemi", folio[1]);
            if(!folio[2].equals("null")){
            sesion.setAttribute("tipoRV", folio[2]);            
            }
            vOrden = folio[0];
            vRemi = folio[1];
            unidadF = folio[2];
           
            
        }
    } catch (Exception e) {
    }
            System.out.println("vorden : " + vOrden);
            System.out.println("vRemi : " + vRemi);
            System.out.println("tipoRV : " +  sesion.getAttribute("tipoRV") );
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/cupertino/jquery-ui-1.10.3.custom.css" rel="stylesheet">
        <link href="css/sweetalert.css" rel="stylesheet" type="text/css"/>
        <link href="css/dataTables.bootstrap.css" rel="stylesheet" type="text/css" >

        <title>SIALSS</title>
    </head>

    <div class="container">
        <h1>MEDALFA</h1>
        <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
        <%@include file="../jspf/menuPrincipal.jspf" %>
        <form action="verificarCompraAuto.jsp" method="post">
            <br/>
            <div class="row">
                <h3>Validación Recibo</h3>
                <h4 class="col-sm-2">Elegir Remisión: </h4>
                <div class="col-sm-9">
                    <select class="form-control" name="NoCompra" onchange="this.form.submit();">
                        <option value="">-- Proveedor -- Orden de Compra --</option>
                        <%    try {
                                con.conectar();
                                ResultSet rset = null;

                                rset = con.consulta("SELECT c.F_OrdCom, p.F_NomPro, c.F_FolRemi, IFNULL(c.F_unidadFonsabi, '') AS F_unidadFonsabi,c.F_Origen FROM tb_compratemp c, tb_proveedor p WHERE c.F_Provee = p.F_ClaProve and c.F_Estado = '2' GROUP BY c.F_OrdCom, c.F_FolRemi, c.F_unidadFonsabi");

                                while (rset.next()) {
                                    
                        %>
                        <option value="<%=rset.getString(1)%>,<%=rset.getString(3)%>,<%=rset.getString(4)%>"><%=rset.getString(2)%> - <%=rset.getString(1)%> - <%=rset.getString(3)%> - <%=rset.getString(4)%></option>
                        <%
                                }
                            } catch (Exception e) {
                                Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, e);
                            } finally {
                                try {
                                    con.cierraConexion();
                                } catch (SQLException ex) {
                                    Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, ex);
                                }
                            }
                        %>
                    </select>
                </div>
            </div>
            <br/>    
            <div class="row">    
                <h4 class="col-sm-2">Elegir Ubicacion: </h4>
                <div class="col-sm-3">
                    <select class="form-control" name="UbicaN" id="UbicaN" >
                        <option value="0">-- Ubicacion --</option>
                        <%  try {
                                con.conectar();
                                ResultSet rsetU = null; 
                                ResultSet tsrtIdori = null;
                                
                                tsrtIdori = con.consulta ("SELECT C.F_Origen FROM tb_compratemp C WHERE F_OrdCom='" + vOrden + "' and F_FolRemi = '" + vRemi + "' and F_Estado = '2';");
                                
                                while (tsrtIdori.next()){
                                    IdOrigen = tsrtIdori.getInt(1);
                                }
                                System.out.println("IdOrigen: "+IdOrigen);
                               
                                if(unidadF == null || unidadF == "" || unidadF.equals("null")){
                                    System.out.println("Andamos e: "+unidadF);
                                    if(IdOrigen == 21){
                                        
                                        System.out.println("IdOrigen == 21");
                                       rsetU = con.consulta("SELECT u.IdUbi, u.DescUbi FROM tb_ubicanueva AS u WHERE u.status = 'A' AND u.DescUbi RLIKE 'CE23' ORDER BY u.IdUbi ASC");
                                    
                                    } else {
                                        System.out.println("IdOrigen == Normal");
                                       rsetU = con.consulta("SELECT u.IdUbi, u.DescUbi FROM tb_ubicanueva AS u WHERE u.status = 'A' AND u.DescUbi LIKE 'NUEVA%' AND u.DescUbi NOT RLIKE 'CE23'  ORDER BY u.IdUbi ASC");
                                      
                                    }
                                    
                                  //  rsetU = con.consulta("SELECT u.IdUbi, u.DescUbi FROM tb_ubicanueva AS u WHERE u.status = 'A' AND u.DescUbi LIKE 'NUEVA%' ORDER BY u.IdUbi ASC");
                                   
                                    } else { 
                                    System.out.println("Andamos en fonsabi: "+unidadF);
                                    rsetU = con.consulta("SELECT u.IdUbi, u.DescUbi FROM tb_ubicanueva AS u WHERE u.status = 'A' AND u.F_ClaCli = '" + unidadF + "' AND u.DescUbi NOT RLIKE 'CE23'  ORDER BY u.IdUbi ASC");                                    
                                    
                                    }
                                
                                
                                while (rsetU.next()) {                                    
                        %>
                        <option value="<%=rsetU.getString("DescUbi")%>" > <%=rsetU.getString("DescUbi")%> </option>
                        <%
                            UbicaN = rsetU.getString("DescUbi");             }
                                
                            } catch (Exception e) {
                                System.out.println("error--->" + e);
                            } finally {
                                try {
                                    con.cierraConexion();
                                } catch (SQLException ex) {
                                    Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, ex);
                                }
                            }
                        %>
                    </select>
                </div> 
            </div>
            <br/>
        </form>
    </div>


    <div style="width: 90%; margin: auto;">


        <div class="panel panel-default table-responsive">

            <div class="panel-heading">

                <%
                    try {
                        con.conectar();
                        ResultSet rset = con.consulta("SELECT i.F_NoCompra, DATE_FORMAT(i.F_FecSur, '%d/%m/%Y') as F_FecSur, i.F_HorSur, p.F_NomPro, p.F_ClaProve from tb_pedido_sialss i, tb_proveedor p where i.F_Provee = p.F_ClaProve and F_StsPed = '1' and F_NoCompra = '" + vOrden + "'  and F_recibido='0' group by F_NoCompra");
                        while (rset.next()) {
                                

                %>
                <div class="row">
                    <h4 class="col-sm-2">Folio Orden de Compra:</h4>
                    <div class="col-sm-2"><input class="form-control" value="<%=vOrden%>" readonly="" name="folio" id="folio" onkeypress="return tabular(event, this)" /></div>
                    <h4 class="col-sm-1">Remisión:</h4>
                    <div class="col-sm-2"><input class="form-control" value="<%=vRemi%>" readonly="" name="folio" id="folio" onkeypress="return tabular(event, this)" /></div>
                    <div class="col-sm-2"><a href="verificaCompragnr.jsp?oc=<%=vOrden%>&remision=<%=vRemi%>&unidadFonsabi=<%=unidadF%>" class="btn btn-info form-control">Exportar&nbsp;<span class="glyphicon glyphicon-download"></span></a></div>
                </div>
                <div class="row">
                    <h4 class="col-sm-12">Proveedor: <%=rset.getString("p.F_NomPro")%></h4>
                </div>
                <div class="row">
                    <h4 class="col-sm-9">Fecha y Hora de Entrega: <%=rset.getString("F_FecSur")%> <%=rset.getString("i.F_HorSur")%></h4>

                </div>

                <%
                        }
                    } catch (Exception e) {
                        Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, e);
                    } finally {
                        try {
                            con.cierraConexion();
                        } catch (SQLException ex) {
                            Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, ex);
                        }
                    }
                %>

            </div>

            <form action="nuevoAutomaticaLotes" method="post">
                <div class="panel-body table-responsive">

                    <table class="table table-bordered table-striped table-condensed table-responsive">


                        <thead>
                        <th class="text-center">Remisión</th>
                        <th class="text-center">Clave</th>
                        <th class="text-center">Descripción</th>
                        <th class="text-center">Origen</th>
                        <th class="text-center">Lote</th>
                        <th class="text-center">Cantidad</th>
                        <th class="text-center">Costo U</th>
                        <th class="text-center">IVA</th>
                        <th class="text-center">Importe</th>
                        <th class="text-center">Caducidad</th>
                        <th class="text-center">Carta Canje</th>
                        <th class="text-center">Fuente Financiamiento</th>
                        <th class="text-center">Orden de Suministro</th>
                        <th class="text-center">Marca</th>                       
                        <th class="text-center">Nombre Comercial</th>                              
                        <th class="text-center">Editar</th>
                        <th class="text-center">Validar</th>
                        </thead>

                        <%
                            int banBtn = 0,contemarca=0;
                            String query = "";
                            try {
                                con.conectar();
                                if(unidadF == null || unidadF == "" || unidadF.equals("null")){
                                    System.out.println("entre a null");
                                     query = "SELECT C.F_Cb,C.F_ClaPro,M.F_DesPro,C.F_Lote,C.F_FecCad,C.F_Pz,F_IdCom,C.F_Costo,C.F_ImpTo,C.F_ComTot,C.F_FolRemi,C.F_Obser,C.F_Origen,IFNULL(C.F_OrdenSuministro,'') as F_OrdenSuministro,MAR.F_ClaMar,MAR.F_DesMar, o.F_DesOri, IFNULL(C.F_CartaCanje,'') AS cartaCanje, C.F_FuenteFinanza,IFNULL(C.F_MarcaComercial,'') as F_MarcaComercial, C.F_unidadFonsabi FROM tb_compratemp C INNER JOIN tb_medica M  ON C.F_ClaPro=M.F_ClaPro INNER JOIN tb_marca MAR ON C.F_Marca = MAR.F_ClaMar INNER JOIN tb_origen as o on C.F_Origen = o.F_ClaOri WHERE F_OrdCom='" + vOrden + "' and F_FolRemi = '" + vRemi + "' and (F_unidadFonsabi = '' || F_unidadFonsabi IS NULL) and F_Estado = '2';";
                               
                                }else{
                                     query = "SELECT C.F_Cb,C.F_ClaPro,M.F_DesPro,C.F_Lote,C.F_FecCad,C.F_Pz,F_IdCom,C.F_Costo,C.F_ImpTo,C.F_ComTot,C.F_FolRemi,C.F_Obser,C.F_Origen,IFNULL(C.F_OrdenSuministro,'') as F_OrdenSuministro,MAR.F_ClaMar,MAR.F_DesMar, o.F_DesOri, IFNULL(C.F_CartaCanje,'') AS cartaCanje, C.F_FuenteFinanza,IFNULL(C.F_MarcaComercial,'') as F_MarcaComercial, C.F_unidadFonsabi FROM tb_compratemp C INNER JOIN tb_medica M  ON C.F_ClaPro=M.F_ClaPro INNER JOIN tb_marca MAR ON C.F_Marca = MAR.F_ClaMar INNER JOIN tb_origen as o on C.F_Origen = o.F_ClaOri WHERE F_OrdCom='" + vOrden + "' and F_FolRemi = '" + vRemi + "' and c.F_unidadFonsabi = '"+unidadF+"' and F_Estado = '2';";
                                }
                                    System.out.println(query);
                                ResultSet rset = con.consulta(query);
                                while (rset.next()) {
                                    ResultSet rsetmarca = con.consulta("select count(*) from tb_nombrecomercial where F_ClaPro = "+rset.getString("F_ClaPro") );
                                    while (rsetmarca.next()) {
                                        contemarca = rsetmarca.getInt(1);
                                    }
                                    banBtn = 1;
                                    String F_FecCad = "", F_Cb = "", F_OrdenSuministro = "", F_Marca = "", F_CartaCanje  = "", F_FuenteFinanza = "", F_nomComer="";

                                    try {
                                        F_FuenteFinanza = rset.getString("F_FuenteFinanza");
                                        F_CartaCanje = rset.getString("cartaCanje");
                                        F_OrdenSuministro = rset.getString("F_OrdenSuministro");
                                        F_FecCad = rset.getString(5);
                                        F_Cb = rset.getString("F_Cb");
                                        F_nomComer = rset.getString("F_MarcaComercial");                                                
                                        ClaPro = rset.getString("F_ClaPro");
                                          if (F_Cb.equals(" ")) {
                                            F_Cb = "";
                                        }
                                        if (F_Cb.equals("")) {

                                            ResultSet rset2 = con.consulta("SELECT F_Cb, F_ClaMar,  FROM tb_lote WHERE F_ClaPro = '" + rset.getString("F_ClaPro") + "' AND F_ClaLot = '" + rset.getString("F_Lote") + "' group by F_ClaPro");
                                            while (rset2.next()) {
                                                F_Cb = rset2.getString("F_Cb");
                                                F_Marca = rset2.getString("F_ClaMar");
                                            }
                                        }

                                        if (F_Cb.equals("")) {
                                            ResultSet rset2 = con.consulta("SELECT F_Cb, F_ClaMar FROM tb_cb WHERE F_ClaPro = '" + rset.getString("F_ClaPro") + "' and F_ClaLot = '" + rset.getString("F_Lote") + "' group by F_ClaPro");
                                            while (rset2.next()) {
                                                F_Cb = rset2.getString("F_Cb");
                                                F_Marca = rset2.getString("F_ClaMar");
                                            }
                                        }
                                                
                                        if (F_Marca.equals("")) {
                                            ResultSet rset2 = con.consulta("SELECT F_DesMar FROM tb_marca WHERE F_ClaMar = '" + F_Marca + "'");
                                            while (rset2.next()) {
                                                F_Marca = rset2.getString("F_DesMar");
                                            }
                                        }
                                        F_Marca = rset.getString("F_DesMar");
                                    
                                              
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    
                        %>

                        <tbody>
                        <td style="text-align: center; vertical-align: middle;"><%=rset.getString("C.F_FolRemi")%></td>
                        <td style="text-align: center; vertical-align: middle;"><%=rset.getString("F_ClaPro")%></td>
                        <td style="vertical-align: middle;"><%=rset.getString(3)%></td>
                        <td style="text-align: center; vertical-align: middle;"><%=rset.getString("o.F_DesOri")%></td>
                        <td style="text-align: center; vertical-align: middle;"><input class="form-control" value="<%=rset.getString(4)%>" name="F_Lote<%=rset.getString("F_IdCom")%>" readonly  /></td>
                        <td style="text-align: center; vertical-align: middle;"><input class="form-control" value="<%=rset.getString(6)%>" type="number" name="F_Cant<%=rset.getString("F_IdCom")%>" readonly/></td>
                        <td style="text-align: center; vertical-align: middle;"><%=formatterDecimal.format(rset.getDouble("C.F_Costo"))%></td>
                        <td style="text-align: center; vertical-align: middle;"><%=formatterDecimal.format(rset.getDouble("C.F_ImpTo"))%></td>          
                        <td style="text-align: center; vertical-align: middle;"><%=formatterDecimal.format(rset.getDouble("C.F_ComTot"))%></td>
                        <td style="text-align: center; vertical-align: middle;">
                            <%
                                if (F_FecCad.equals("")) {
                            %>
                            <input type="date" class="form-control lg-sm-1" name="F_FecCad<%=rset.getString("F_IdCom")%>" readonly />
                            <%
                            } else {
                            %>
                            <input type="date" class="form-control" name="F_FecCad<%=rset.getString("F_IdCom")%>"  value="<%=F_FecCad%>" readonly />
                            <%
                                }
                            %>
                        </td>
                        <td style="text-align: center; vertical-align: middle;">
                            <input value="<%=rset.getString("cartaCanje")%>" class="form-control" name="F_CartaCanje<%=rset.getString("F_IdCom")%>" readonly/>
                        </td>
                        <td style="text-align: center; vertical-align: middle;"> <input value="<%=rset.getString("F_FuenteFinanza")%>" class="form-control" name="F_FuenteFinanza" readonly> </td>
                        <td style="text-align: center; vertical-align: middle;">
                            <input value="<%=F_OrdenSuministro%>" class="form-control" name="F_OrdenSuministro<%=rset.getString("F_IdCom")%>" readonly/>
                        <td style="text-align: center; vertical-align: middle;">
                            <input value="<%=F_Marca%>" class="form-control" name="F_Marca<%=rset.getString("F_IdCom")%>" id="marca<%=rset.getString("F_IdCom")%>" readonly/>
                        </td>

                        <td style="text-align: center; vertical-align: middle;">
                            <input value="<%=F_nomComer%>" class="form-control" name="F_Marca" id="MarcaR" readonly/>

                        </td>
                        <td style="text-align: center; vertical-align: middle;">
                            <button type="button" class="btn btn-block btn-warning" id="btnEdit"  onclick="editar(<%=rset.getString("F_IdCom")%>,<%=rset.getString("F_ClaPro")%>)" ><span class="glyphicon glyphicon-edit" /></button>
                        </td>
                        <td style="text-align: center; vertical-align: middle;">

                            <button type="button" class="btn btn-info form-control glyphicon glyphicon-ok" id="Validar_<%=rset.getString("F_IdCom")%>"></button>
                        </td>
                        </tbody>
                        <%
                                }
                            } catch (Exception e) {
                                Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, e);
                            } finally {
                                try {
                                    con.cierraConexion();
                                } catch (SQLException ex) {
                                    Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, ex);
                                }
                            }

                        %>

                    </table>

                    <hr/>
                </div>

                <%                        if (banBtn == 1) {
                %>

                <div class="panel-footer">
                    <div class="row">
                        <input name="vOrden" id="vOrden" type="text" style="" class="hidden" value='<%=vOrden%>' />
                        <input name="vRemi" id="vRemi" type="text" style="" class="hidden" value='<%=vRemi%>' />
                        <input name="UbicaN" id="UbicaN" type="text" style="" class="hidden" value='<%=UbicaN%>' />
                        <input name="MarcaR" id="MarcaR" type="text" style="" class="hidden" value='<%=MarcaR%>' />
                        <input name="uFonsabi" id="uFonsabi" type="text" style="" class="hidden" value='<%=unidadF%>' />
                        <div class="col-lg-3 col-lg-offset-3">
                            <button  value="EliminarVerifica" name="accion" class="btn btn-danger btn-block" onclick="return confirm('Seguro que desea eliminar la compra?');">Cancelar Remisión</button>
                        </div>
                        <div class="col-lg-3">
                            <button  value="validarRemision" name="accion" type="button" id="validarRemision" class="btn btn-success  btn-block">Confirmar Remisión</button>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </form>

        </div>
    </div>

    <!--MODAL PARA EDITAR-->        
    <button type="button" class="btn hidden" id="btnModal" data-toggle="modal" data-target="#myModal"></button>
    <div class="modal fade bs-example-modal-lg" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" 
         data-keyboard="false">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h3 class="modal-title text-center text-success" id="myModalLabel1">Editar registro</h3>
                    <input id="idCompraTemporal" type="hidden">
                    <input id="idVolumetria" type="hidden">
                    <input id="UserActual" type="hidden" value="<%=usua%>">
                    <input type="hidden" name="i">
                </div>
                <div class="modal-body">
                    <ul class="nav nav-tabs">
                        <li class="active"><a data-toggle="tab" href="#datos" style="color: black">Datos del Insumo</a></li>
                        <li><a data-toggle="tab" href="#volumetriaTab" style="color: black" id="tabVol1">Volumetría Peso</a></li>
                        <li><a data-toggle="tab" href="#volumetriaVolTab" style="color: black" id="tabVol2">Volumetría Dimensiones</a></li>
                    </ul>
                    <div class="tab-content" style="background-color: #f5f5f5">
                        <div id="datos" class="tab-pane fade in active">
                            <br>
                            <div class="form-group" >
                                <input type="hidden" class="form-control" id="tipoInsumoNuevo">
                                <input type="hidden" class="form-control" id="fuenteFinanza">
                                <label  for="loteNuevo" >Lote</label>
                                <input class="form-control" id="loteNuevo">
                                <label  for="CaducidadNuevo" >Caducidad</label>
                                <input class="form-control" min="<%= LocalDate.now().plusDays(30)%>" id="CaducidadNuevo" type="date" onchange="validaCad()">
                                <br/>
                                <div class="col-sm-12">
                                    <div class="col-sm-4">
                                        <label align="rigth" for="CbNuevo" class="col-sm-10">Codigo de Barras</label>
                                    </div>
                                    <div class="col-sm-4">
                                        <label  for="ordenSuministroNuevo"class="col-sm-10" >Orden de Suministro</label>
                                    </div>
                                    <div class="col-sm-4">
                                        <label  for="cartaCanjeNuevo"class="col-sm-10" >Carta Canje</label>
                                    </div>
                                </div>
                                <div class="col-sm-12">
                                    <div class="col-sm-4">
                                        <input align="rigth" class="col-sm-10 form-control" id="CbNuevo">
                                    </div>
                                    <div class="col-sm-4">
                                        <input class="col-sm-10 form-control" maxlength="40" id="ordenSuministroNuevo" name="ordenSuministroNuevo">
                                    </div>                                      
                                    <div class="col-sm-4">
                                        <input type="text" class="col-sm-10 form-control" maxlength="40" id="cartaCanjeNuevo" name="cartaCanjeNuevo">
                                    </div>
                                    <div>
                                        <input type="hidden"class="col-sm-8" maxlength="40" id="origen" name="origen" disabled="disabled"  onchange="validaOrigen()">
                                    </div>
                                </div>
                                <br/>
                                <label  for="marcaNuevo">Marca </label>
                                <input class="form-control" id="marcaNuevo" onkeyup="descripMarc()">


                                <label  for="MarcaRNuevo">Nombre Comercial</label>
                                <select class="form-control" id="NombreRNuevo" name="NombreRNuevo" onchange="document.getElementById('MarcaRNuevo').value = this.options[this.selectedIndex].value"> 

                                    <!--option value="">Seleccionar Opcion</option-->
                                </select>				                                             
                                <input class="form-control"  id="MarcaRNuevo" name="MarcaRNuevo" type="text" disabled=""/>


                                <label for="unidadFonsabi">Unidad:</label>
                                <select class="form-control" id="UFonnuevo" name="UFonnuevo" onchange="document.getElementById('unidadFonsabi').value = this.options[this.selectedIndex].value"> 
                                    <option value="S/U">Sin conocimieno de unidad destino</option>
                                    <%                                            try {
                                            con.conectar();

                                          ResultSet  rset3 = con.consulta("SELECT * FROM tb_uniatn AS U WHERE U.F_ClaCli LIKE '%%AI' AND F_StsCli = 'A';");
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
                                <input class="form-control" id="unidadFonsabi" name="unidadFonsabi" type="text" disabled=""/> 


                                <label  for="CantidadNuevo" >Cantidad</label>
                                <input class="form-control" id="CantidadNuevo" type="number" min="1" readonly="">
                                <label  for="CantidadNuevo" >Costo U</label>
                                <input class="form-control" id="costo" type="text" readonly="">
                                <br>
                                <div class="col-sm-12">
                                    <label  for="tarimasNuevo" class="col-sm-2" >Tarimas</label>
                                    <input class="col-sm-1" id="tarimasNuevo" type="number" min="1">
                                    <label  for="cajasNuevo" class="col-sm-2" >Cajas x Tarima</label>
                                    <input class="col-sm-1" id="cajasNuevo" type="number" min="1">
                                    <label  for="pzacajasNuevo" class="col-sm-2" >Piezas x Caja</label>
                                    <input class="col-sm-2" id="pzacajasNuevo" type="number" min="1">
                                </div>
                                <div class="col-sm-12"></div>
                                <div class="col-sm-12">
                                    <label  for="cajasiNuevo" class="col-sm-2" >Cajas x Tarima Incompleta</label>
                                    <input class="col-sm-1" id="cajasiNuevo" type="number" min="1">                                    
                                    <label  for="restoNuevo" class="col-sm-2" >Resto</label>
                                    <input class="col-sm-1" id="restoNuevo" type="number" min="1">
                                    <label  for="factorEmpaqueNuevo" class="col-sm-2" >Factor de Empaque</label>
                                    <input class="col-sm-1" id="factorEmpaqueNuevo" type="number" min="1">
                                </div>
                                <div class="col-sm-12">
                                    <label for="cantPedido" class="hidden"> cantidad Pedido</label>
                                    <input class="hidden" id="cantPedido" type="text">
                                    <label for="cantCompra" class="hidden"> cantidad Compra</label>
                                    <input class="hidden" id="cantCompra" type="text">
                                    <label for="cantidadTemp" class="hidden"> cantidad Temp</label>
                                    <input class="hidden" id="cantidadTemp" type="text">

                                </div>
                            </div>
                            <br>
                            <br>
                            <br>
                            <br>
                        </div>
                        <div id="volumetriaTab" class="tab-pane">
                            <h4><strong>Volumetría Peso</strong></h4>
                            <h5><strong>Peso por pieza</strong></h5>
                            <div class="row form-inline" style="text-align: right;">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="pesoPieza">Peso</label>
                                        <input type="text" class="form-control" id="pesoPiezaNuevo" name="pesoPieza"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="unidadPieza">Unidad</label>
                                        <select type="text" class="form-control" id="unidadPesoPiezaNuevo" name="unidadPesoPieza" placeholder="gr" > 
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
                                        <input type="text" class="form-control" id="pesoCajaNuevo" name="pesoCaja"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="unidadCaja">Unidad</label>
                                        <select type="text" class="form-control" id="unidadPesoCajaNuevo" name="unidadPesoCaja" placeholder="Unidad" > 
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
                                        <input type="text" class="form-control" id="pesoConcentradaNuevo" name="pesoConcentrada"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="unidadConcentrada">Unidad</label>
                                        <select type="text" class="form-control" id="unidadPesoConcentradaNuevo" name ="unidadPesoConcentrada" placeholder="Unidad" > 
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
                                        <input type="text" class="form-control" id="pesoTarimaNuevo" name="pesoTarima"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label for="unidadTarima">Unidad</label>
                                        <select type="text" class="form-control" id="unidadPesoTarimaNuevo" name="unidadPesoTarima" placeholder="Unidad" > 
                                            <option value="gr">Gramos (gr)</option>
                                            <option value="kgr">Kilogramos (kgr)</option>
                                            <option value="t">Toneladas (t)</option>
                                        </select>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div id="volumetriaVolTab" class="tab-pane">
                            <h4><strong>Volumetría Volumen</strong></h4>
                            <br>
                            <h5><strong>Volúmen por pieza</strong></h5>
                            <div class="row form-inline" style="text-align: right;">
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="altoPieza">Alto</label>
                                        <input type="text" class="form-control" id="altoPiezaNuevo" name="altoPieza"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="anchoPieza">Ancho</label>
                                        <input type="text" class="form-control" id="anchoPiezaNuevo" name="anchoPieza"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="largoPieza">Largo</label>
                                        <input type="text" class="form-control" id="largoPiezaNuevo" name="largoPieza"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="unidadVolPieza">Unidad</label>
                                        <select type="text" class="form-control" id="unidadVolPiezaNuevo" name="unidadVolPieza" placeholder="Unidad" > 
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
                                        <input type="text" class="form-control" id="altoCajaNuevo" name="altoCaja"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="anchoCaja">Ancho</label>
                                        <input type="text" class="form-control" id="anchoCajaNuevo" name="anchoCaja"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="largoCaja">Largo</label>
                                        <input type="text" class="form-control" id="largoCajaNuevo" name="largoCaja"  placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="unidadVolCaja">Unidad</label>
                                        <select type="text" class="form-control" id="unidadVolCajaNuevo" name="unidadVolCaja" placeholder="Unidad" > 
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
                                        <input type="text" class="form-control" id="altoConcentradaNuevo" name="altoConcentrada" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="anchoConcentrada">Ancho</label>
                                        <input type="text" class="form-control" id="anchoConcentradaNuevo" name="anchoConcentrada" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="largoConcentrada">Largo</label>
                                        <input type="text" class="form-control" id="largoConcentradaNuevo" name="largoConcentrada" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="unidadVolConcentrada">Unidad</label>
                                        <select type="text" class="form-control" id="unidadVolConcentradaNuevo" name="unidadVolConcentrada" placeholder="Unidad" > 
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
                                        <input type="text" class="form-control" id="altoTarimaNuevo" name="altoTarima" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="anchoTarima">Ancho</label>
                                        <input type="text" class="form-control" id="anchoTarimaNuevo" name="anchoTarima" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="largoTarima">Largo</label>
                                        <input type="text" class="form-control" id="largoTarimaNuevo" name="largoTarima" placeholder="0" onKeyPress="return justNumbers(event);
                                                return handleEnter(event);">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="unidadVolTarima">Unidad</label>
                                        <select type="text" class="form-control" id="unidadVolTarimaNuevo" name="unidadVolTarima" placeholder="Unidad" > 
                                            <option value="mm">Milímetros (mm)</option>
                                            <option value="cm">Centímetros (cm)</option>
                                            <option value="m">Metros (m)</option>
                                        </select>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>


                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-success" id="btnSave1">Guardar</button>
                    <button type="button" class="btn btn-success" data-dismiss="modal" id="btnCancel" onclick="location.reload()">Cancelar</button>

                </div>
            </div>
        </div>
    </div>        
    <!--FIN MODAL PARA EDITAR-->               



    <!--MODAL PARA RECHAZAR
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
                                <input name="NoCompraRechazo" id="NoCompraRechazo" value="" class="form-control" readonly="" />
                            </div>
                        </div>
                        <div class="row">

                            <div class="col-sm-12">
                                Proveedor:
                            </div>
                            <div class="col-sm-12">
                                Fecha y Hora 
                            </div>
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
                                <input type="date" class="form-control" id="FechaOrden" name="FechaOrden" />
                            </div>
                            <div class="col-sm-6">
                                <select class="form-control" id="HoraOrden" name="HoraOrden">
                                    <option value=":00">:00</option>
                                    <option value=":30">:30</option>
                                    <option value=":00">:00</option>
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
                                    <h4><input type="checkbox" checked name="todosChk" id="todosChk" onclick="checkea(this)">Seleccionar todas</h4>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-12">
                                <table class="table table-bordered">

                                    <tr>

                                        <td>
                                            <div class="checkbox">
                                                <label>
                                                    <input type="checkbox" checked="" name="chkCancela" value="">
                                                </label>
                                            </div>
                                        </td>

                                    </tr>

                                </table>
                            </div>
                        </div>
                        <div class="text-center" id="imagenCarga" style="display: none;" > 
                            <img src="imagenes/ajax-loader-1.gif" alt="loader">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success" onclick="return validaRechazo();
                                " name="accion" value="Rechazar">Rechazar OC</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>-->
    <!--FIN MODAL PARA RECHAZAR-->


    <%@include file="jspf/piePagina.jspf" %>

    <script src="js/jquery-2.1.4.min.js" type="text/javascript"></script>
    <script src="js/jquery-ui.js" type="text/javascript"></script>
    <script src="js/bootstrap.js"></script>
    <script src="js/dataTables.bootstrap.js"></script>
    <script src="js/recepcion/recepcionEdit.js"></script>
    <script src="js/select2.js" type="text/javascript"></script>        
    <script src="js/sweetalert.min.js" type="text/javascript"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <!--script src="./js/reporteador/datatables.min.js"></script-->

    <script>

                        $('#validarRemision').click(function () {
                            var UbicaN = $("#UbicaN option:selected").val();
                            if ($('#UbicaN').val().trim() === '') {
                                alert('Debe seleccionar una Ubicacion');
                            } else {
                                alert('La Ubicacion es;' + UbicaN);
                            }
                        });

                        function validaCad() {
                            var cad = $("#CaducidadNuevo").val();
                            var tipoInsumo = $("#tipoInsumoNuevo").val();
                            if (tipoInsumo === "CONTROLADO") {
                                var dtFechaActual = new Date();
                                var sumarY = parseInt(1);
                                dtFechaActual.setFullYear(dtFechaActual.getFullYear() + sumarY);
                                if (Date.parse(dtFechaActual) <= Date.parse(cad)) {
                                    document.getElementById('cartaCanjeNuevo').value = "";
                                    $("#cartaCanjeNuevo").prop('disabled', true);

                                } else
                                    $("#cartaCanjeNuevo").prop('disabled', false);
                            } else {
                                $("#cartaCanjeNuevo").prop('disabled', true);
                            }

                        }
                        ;



    </script>

    <script type="text/javascript">
        var IdVal = 'Validar_' + IdVal;
        var UbicaN = $("#UbicaN option:selected").val();

        $('#IdVal').click(function () {

            if ($('#UbicaN').val().trim() === '') {
                alert('Debe seleccionar una Ubicacion');
            } else {
                alert('La Ubicacion es;' + UbicaN);
            }
        }
        );


        function validaOrigen() {
            var origen = $("#origen").val();
            if (origen !== '4' && origen !== '5' && origen !== '10' && origen !== '11' && origen !== '14' && origen !== '15' && origen !== '16' && origen !== '17' && origen !== '18' && origen !== '19') {
                $("#ordenSuministroNuevo").value = "";
                $("#ordenSuministroNuevo").prop('disabled', true);
            } else {
                $("#ordenSuministroNuevo").prop('disabled', false);
            }
            if (origen !== '19') {
                $("#unidadFonsabi").value = "";
                $("#unidadFonsabi").prop('disabled', true);
                $("#UFonnuevo").val("");
                $("#UFonnuevo").prop('disabled', true);
            } else {
                $("#unidadFonsabi").prop('disabled', false);
                $("#UFonnuevo").prop('disabled', false);
            }
        }
        ;

    </script>

    <script type="text/javascript">
        function descripMarc() {
        <%
            try {
                con.conectar();

        %>
            var availableTags = [
        <%               
            ResultSet rset1 = con.consulta("select F_DesMar from tb_marca");
            while (rset1.next()) {
                out.println("'" + rset1.getString(1) + "',");
            }
        %>
            ];
            $("#marcaNuevo").autocomplete({
                source: availableTags
            });
        <%
            } catch (Exception e) {
                Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, e);
            } finally {
                try {
                    con.cierraConexion();
                } catch (SQLException ex) {
                    Logger.getLogger("verifarCompraAuto.jsp").log(Level.SEVERE, null, ex);
                }
            }
        %>
        }
    </script>

    <script>
        var ClaveComer = "", Nomcor = "", unifon = "";
        function editar(valor)
        {

            $.ajax({
                url: "${pageContext.servletContext.contextPath}/recepcionTransaccional",
                data: {accion: "Editar", id: valor},
                type: 'POST',
                dataType: 'JSON',
                async: true,
                success: function (data)
                {
                    //alert(data.cb);
                    $("#loteNuevo").val(data.lote);
                    $("#CantidadNuevo").val(data.cantidad);
                    $("#CbNuevo").val(data.cb);
                    $("#CaducidadNuevo").val(data.caducidad);
                    $("#ordenSuministroNuevo").val(data.ordenSuministro);
                    $("#origen").val(data.origen);
                    $("#marcaNuevo").val(data.marca);
                    $("#tarimasNuevo").val(data.tarimas);
                    $("#cajasNuevo").val(data.cajas);
                    $("#pzacajasNuevo").val(data.pzacajas);
                    $("#cajasiNuevo").val(data.cajasi);
                    $("#restoNuevo").val(data.resto);
                    $("#costo").val(data.costo);
                    $("#factorEmpaqueNuevo").val(data.factorEmpaque);
                    $("#cartaCanjeNuevo").val(data.cartaCanje);
                    $("#tipoInsumoNuevo").val(data.tipoInsumo);
                    $("#cantPedido").val(data.cantPedido);
                    $("#cantCompra").val(data.cantCompra);
                    $("#cantidadTemp").val(data.cantidadTemp);
                    $("#fuenteFinanza").val(data.fuenteFinanza);
                    $("#MarcaRNuevo").val(data.marcaComercial);
                    unifon = $("#unidadFonsabi").val(data.unidadFonsabi);

                    Nomcor = data.marcaComercial;
                    ClaveComer = data.ClaPro;


                    if (data.volumetria) {
                        $("#pesoPiezaNuevo").val(data.volumetria.pesoPieza);
                        $("#unidadPesoPiezaNuevo").val(data.volumetria.unidadPesoPieza);
                        $("#pesoCajaNuevo").val(data.volumetria.pesoCaja);
                        $("#unidadPesoCajaNuevo").val(data.volumetria.unidadPesoCaja);
                        $("#pesoConcentradaNuevo").val(data.volumetria.pesoConcentrada);
                        $("#unidadPesoConcentradaNuevo").val(data.volumetria.unidadPesoConcentrada);
                        $("#pesoTarimaNuevo").val(data.volumetria.pesoTarima);
                        $("#unidadPesoTarimaNuevo").val(data.volumetria.unidadPesoTarima);
                        $("#altoPiezaNuevo").val(data.volumetria.altoPieza);
                        $("#anchoPiezaNuevo").val(data.volumetria.anchoPieza);
                        $("#largoPiezaNuevo").val(data.volumetria.largoPieza);
                        $("#unidadVolPiezaNuevo").val(data.volumetria.unidadVolPieza);
                        $("#altoCajaNuevo").val(data.volumetria.altoCaja);
                        $("#anchoCajaNuevo").val(data.volumetria.anchoCaja);
                        $("#largoCajaNuevo").val(data.volumetria.largoCaja);
                        $("#unidadVolCajaNuevo").val(data.volumetria.unidadVolCaja);
                        $("#altoConcentradaNuevo").val(data.volumetria.altoConcentrada);
                        $("#anchoConcentradaNuevo").val(data.volumetria.anchoConcentrada);
                        $("#largoConcentradaNuevo").val(data.volumetria.largoConcentrada);
                        $("#unidadaVolConcentradaNuevo").val(data.volumetria.unidadaVolConcentradaf);
                        $("#altoTarimaNuevo").val(data.volumetria.altoTarima);
                        $("#anchoTarimaNuevo").val(data.volumetria.anchoTarima);
                        $("#largoTarimaNuevo").val(data.volumetria.largoTarima);
                        $("#unidadVolTarimaNuevo").val(data.volumetria.unidadVolTarima);
                        $("#idVolumetria").val(data.volumetria.id);
                        $("#tabVol1").show();
                        $("#tabVol2").show();
                    } else {
                        $("#tabVol1").hide();
                        $("#tabVol2").hide();
                    }
                    $("#idCompraTemporal").val(valor);
                    $("#btnModal").click();

                    console.log("MArca Comercial: " + Nomcor);
                    //  $("#NombreRNuevo").select2();


                    if (Nomcor !== null && Nomcor !== "") {

                        let temp = data.arrayjson;
                        let arr = temp.sort();
                        var $select = $('#NombreRNuevo');
                        $select.append('<option value=' + Nomcor + '>Seleccion Nombre</option>');
                        for (i = 0; i < arr.length; i++) {
                            $select.append('<option value=' + arr[i] + '>' + arr[i] + '</option>');
                        }
                    } else {
                        $('#NombreRNuevo').prop('disabled', 'disabled');
                    }




                    /*para la unidad*/
                    /* if (unifon !== null && unifon !== ""){
                     
                     let tempfon = data.arrayjsonfon;
                     let arr2 = tempfon.sort();
                     var $select1 = $('#UFonnuevo');
                     $select1.append('<option value='+unifon +'>' +unifon+ '</option>');
                     
                     }else{
                     $('#UFonnuevo').prop('disabled', 'disabled');  
                     }*/



                    /**/


                    validaOrigen();
                    validaCad();

                    const object = data;

                }, error: function (jqXHR, textStatus, errorThrown)

                {
                    alert("Error en sistema");
                }
            });


        }
        ;








    </script>
</html>






