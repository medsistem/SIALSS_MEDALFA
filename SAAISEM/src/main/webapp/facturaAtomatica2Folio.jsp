<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="com.medalfa.saa.vo.RequeridoVOList"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.medalfa.saa.vo.RequeridoVO"%>
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
    String usua = "", Clave = "", Desproyecto = "";
    String tipo = "", F_Ruta = "", F_FecEnt = "", Unidad1 = "", Unidad2 = "", Catalogo = "";
    int Proyecto = 0, UbicaModu = 0;

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

        ResultSet UbiMod = con.consulta("SELECT PU.F_Id,P.F_Id, IFNULL(P.F_DesProy, '') AS Proyecto FROM tb_parametrousuario PU LEFT JOIN ( SELECT F_Id, F_DesProy FROM tb_proyectos ) P ON PU.F_Proyecto = P.F_Id WHERE F_Usuario = '" + usua + "';");
        if (UbiMod.next()) {
            UbicaModu = UbiMod.getInt(1);
            Proyecto = UbiMod.getInt(2);
            Desproyecto = UbiMod.getString(3);
        }

        con.cierraConexion();
    } catch (Exception e) {

    }
    String where = " and (";
    String[] temp;
    String solicita = "";
    temp = UsuaJuris.split(",");
    for (int i = 0; i < temp.length; i++) {
        where += "f.F_Ruta like 'R" + temp[i] + "%'";
        if (i != temp.length - 1) {
            where += " or ";
        }
    }
    where += ")";
    String tipoOri = "AND F_TipOri = '";
    String tipoRemision = "Ordinario";
    try {
        Unidad1 = request.getParameter("Unidad1");
        Unidad2 = request.getParameter("Unidad2");
        Catalogo = request.getParameter("Catalogo");
        solicita = request.getParameter("solicita");
        tipoRemision = request.getParameter("F_Tipo");
        if (Catalogo.equals("Seleccione")) {
            Catalogo = "";
        }

    } catch (Exception e) {
    }
    System.out.println("Unidad1" + Unidad1 + " Unidad2:" + Unidad2 + " Cata:" + Catalogo);
    String tipoUnidad = "";
    Integer controladoDiasCad = 41;
    Boolean proxACaducar = false;
    List<String> proxACaducarClaves = new ArrayList<String>();
    
    LocalDate todaysDate = LocalDate.now();
    System.out.println(todaysDate);
    String FechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
System.out.println(FechaHoy);
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
                    <h3 class="panel-title">Facturación aútomatica 2 Folios Proyecto : <%=Desproyecto%></h3>
                </div>
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formulario1" id="formulario1" method="post" action="facturaAtomatica2Folio.jsp">

                        <div class="row">
                            <label class="control-label col-sm-2">Clave Unidad:</label>
                            <div class="col-sm-2">
                                <input name="Unidad1" id="Unidad1" type="text" class="form-control" placeholder="Unidad" value=""  required=""/>                        
                            </div>
                            <div class="col-sm-2">
                                <input name="Unidad2" id="Unidad2" type="text" class="form-control" value="" placeholder="Unidad" required=""/>
                            </div>
                            <h4 class="col-sm-1">Solicita: </h4>
                            <div class="col-sm-2">
                                <select name="solicita" class="form-control" required>
                                    <option value="">Seleccione</option>
                                    <!--option value="1">N1 Unidad Móvil  y Caravanas</option-->
                                    <option value="jurisdiccion">Jurisdicción</option>
                                    <option value="unidad">Unidad</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <h4 class="control-label col-sm-2">Catálogo:</h4>
                            <div class="col-sm-2">
                                <select name="Catalogo" id="Catalogo" class="form-control" required>
                                    <option value="">Seleccione</option>

                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = null;
                                            rset = con.consulta("SELECT tu.F_idNivel,CONCAT(tu.F_NivelUni,' - ',tu.F_NomUni) as NombreUnidad FROM tb_tipnivelfact AS tu ORDER BY tu.F_idNivel ASC;");

                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                                    <%
                                            }
                                        } catch (Exception e) {
                                            Logger.getLogger("facturaAutomatica2Folio.jsp").log(Level.SEVERE, null, e);
                                        } finally {
                                            try {
                                                con.cierraConexion();
                                            } catch (Exception ex) {
                                                Logger.getLogger("facturaAutomatica2Folio.jsp").log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <h4 class="col-sm-1">Tipo</h4>
                            <div class="col-sm-2">
                                <select class="form-control" name="F_Tipo" id="F_Tipo" value="<%=tipoRemision%>">
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

                            <div class="col-lg-2">
                                <button class="btn btn-block btn-success" type="submit" name="accion" value="consultar" onclick="return valida_clave();" > Consultar</button>
                            </div>

                        </div>
                    </form>

                    <div>
                        <h6>Los campos marcados con * son obligatorios</h6>
                    </div>
                </div>
                <div class="panel-footer">
                    <form action="Facturacion" method="post" onsubmit="muestraImagen()">
                        <%
                            int banReq1 = 0, banReq = 0;
                            try {
                                con.conectar();
                                ResultSet rset = null;
                                String F_NomCli = "", F_Fecha = "", UbicaDesc2 = "", FechaExc = "";
                                int F_PiezasReq = 0, TotalSur = 0, UbicaModu2 = 0, uniex = 0;

                                ResultSet rsetuniex = con.consulta("SELECT COUNT(*), uae.FechaSurtido FROM tb_uniatnexclu uae WHERE uae.Clacli BETWEEN '" + Unidad1 + "' AND '" + Unidad2 + "' AND uae.StsClaCli = 1;  ");
                                while (rsetuniex.next()) {
                                    FechaExc = rsetuniex.getString(2);
                                    uniex = rsetuniex.getInt(1);
                                }
                                
                                System.out.println("siaqui:" +FechaExc+ " "+ uniex + " "+FechaHoy);

                                if (!(Catalogo == "")) {
                                 
                                    if (uniex == 0) {
                                        System.out.println("cuando es normal");
                                          rset = con.consulta("SELECT F_ClaCli AS F_ClaUni, F_NomCli, U.F_Tipo FROM tb_uniatn U INNER JOIN tb_unireq R ON U.F_ClaCli = R.F_ClaUni WHERE F_ClaCli between  '" + Unidad1 + "' and '" + Unidad2 + "' AND R.F_Status = 0 AND U.F_StsCli = 'A' GROUP BY F_ClaCli;");
                                     
                                        } else {
                                         
                                        if (FechaExc.equals(FechaHoy)) {
                                           
                                          rset = con.consulta("SELECT F_ClaCli AS F_ClaUni, F_NomCli, U.F_Tipo FROM tb_uniatn U INNER JOIN tb_unireq R ON U.F_ClaCli = R.F_ClaUni WHERE F_ClaCli between  '" + Unidad1 + "' and '" + Unidad2 + "' AND R.F_Status = 0 AND U.F_StsCli = 'A' GROUP BY F_ClaCli;");
                                           
                                            } else {
                                            System.out.println("entro a mensaje");
                                            %>
                         <div class="row">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="row">
                            <h4 class="col-sm-3">Fecha Surtido:</h4>
                            <div class="col-sm-2" style="width: 61%;"><h4>
                                    La unidad: <%=Unidad1 %>  Solo se remisiona en la fecha  <%=FechaExc  %> 
                                    </h4></div>
                        </div>
                    </div>
                    <hr/>
                </div>
            </div> 
                        
                        
                        <%
                                            }
                                       
                                        }
                                   // rset = con.consulta("SELECT F_ClaCli AS F_ClaUni, F_NomCli, U.F_Tipo FROM tb_uniatn U INNER JOIN tb_unireq R ON U.F_ClaCli = R.F_ClaUni WHERE F_ClaCli between  '" + Unidad1 + "' and '" + Unidad2 + "' AND R.F_Status = 0 AND U.F_StsCli = 'A' GROUP BY F_ClaCli;");
                                     System.out.println("consulta asas: "+rset);

if (rset != null) {
System.out.println("rset no es null");
while (rset.next()) {

System.out.println("inicio recorrido");
                                        F_NomCli = rset.getString("F_NomCli");
                                        tipoUnidad = rset.getString("F_Tipo");
                                        ResultSet rset3 = con.consulta("select F_ClaUni, sum(F_Solicitado) as F_PiezasReq,DATE_FORMAT(F_Fecha,'%d/%m/%Y') AS F_Fecha from tb_unireq REQ INNER JOIN tb_uniatn ua ON REQ.F_ClaUni = ua.F_ClaCli INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro AND M.F_N" + Catalogo + "='1' where F_Status = '0' and REQ.F_ClaUni = '" + rset.getString(1) + "' AND ua.F_StsCli = 'A' group by F_ClaUni;");
  if (!rset3.wasNull() ) {                                     
while (rset3.next()) {
                                            banReq = 1;
                                            banReq1 = 1;
                                            F_PiezasReq = (rset3.getInt("F_PiezasReq"));
                                            F_Fecha = rset3.getString("F_Fecha");
                                            if (F_PiezasReq == 0) {
                                                banReq1 = 0;
                                            }
                                        }}
                                        if (F_PiezasReq != 0) {
                        %>
                        <div class="panel panel-default">
                            <div class="panel-heading">

                                <%
                                    if (banReq == 1) {
                                %>
                                <input type="checkbox" name="chkUniFact" id="chkUniFact" value="<%=rset.getString("F_ClaUni")%>" checked="">
                                <%
                                    }
                                %>
                                <a data-toggle="collapse" data-parent="#accordion" href="#111<%=rset.getString(1)%>" style="color:black" aria-expanded="true" aria-controls="collapseOne"><%=rset.getString(1)%> |  <%=F_NomCli%> | Fecha Req. <%=F_Fecha%> </a>


                                <%
                                    if (banReq == 1) {
                                %>
                                <input name="F_ClaUni" value="<%=rset.getString(1)%>" class="hidden" />
                                
                                <button class="btn btn-sm btn-warning" name="eliminar" value="<%=rset.getString(1)%>" onclick="eliminarReq(this)" type="button"><span class="glyphicon glyphicon-remove"></span></button>
                                    <%
                                        }
                                    %>
                            </div>
                            <div id="<%=rset.getString(1)%>" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                                <div class="panel-body">
                                    <div class="row">

                                        <div class="col-sm-12">
                                            <h4 id="proxCaducarLegend"></h4>
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
                                                int ExiLot = 0, ExiSol = 0;
                                                String UbicaDesc = "", id = "";
                                                int Cata = Integer.parseInt(Catalogo);
                                                String Query2 = "SELECT PU.F_Id, P.F_Id AS PROYECTO, UF.F_UbicaSQL, UF.F_idUbicaFac  FROM tb_parametrousuario AS PU INNER JOIN tb_proyectos AS P ON PU.F_Proyecto = P.F_Id INNER JOIN tb_ubicafact AS UF ON PU.F_Id = UF.F_idUbicaFac WHERE PU.F_Usuario = '" + usua + "';";
                                                ResultSet rsetR2 = con.consulta(Query2);
                                                while (rsetR2.next()) {
                                                    id = rsetR2.getString(1);
                                                    UbicaDesc = rsetR2.getString(3);
                                                    Integer idParametro = rsetR2.getInt(4);
                                                    if (idParametro == 13 && tipoRemision.equals("Urgente")) {
                                                        UbicaDesc = "WHERE F_Ubica IN ('APEURGENTE','CONTROLADOURGENTE','REDFRIAURGENTE','ORDINARIOURGENTE')";
                                                        controladoDiasCad = 0;
                                                    }
                                                }

                                                String caducidad = " F_FecCad >= curdate()";

                                                if (tipoUnidad.equals("RURAL")) {
                                                    caducidad = " TIMESTAMPDIFF(MONTH, DATE_SUB(curdate(),INTERVAL DAYOFMONTH(curdate())- 1 DAY), F_FecCad) > 1 ";
                                                }

                                                String query = "", queryctr = "";

                                                switch (id) {
                                                    case "40":
                                                    case "41":
                                                    case "14": {
                                                        query = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot FROM tb_lote " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad < curdate() GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro not IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";
                                                     queryctr = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE, LOTE.proxCaducar as proxCaducar FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot, SUM(IF(F_FecCad <= DATE_ADD(curdate(),INTERVAL 180 DAY),1,0)) as proxCaducar FROM tb_lote l " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad  < curdate() AND F_ExiLot > 0 GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";

                                                        break;
                                                    }
                                                    case "19": {
                                                        query = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot FROM tb_lote l INNER JOIN tb_unidadfonsabi uf ON uf.F_FolLot = l.F_FolLot AND uf.F_ClaCli = '" + rset.getString("F_ClaUni") + "' " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad > curdate() GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica INNER JOIN tb_ubicaatn atn ON L.F_Ubica = atn.Ubicacion AND atn.No_Unidad = '" + rset.getString("F_ClaUni") + "' " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov ) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' group by F_IdReq order by M.F_ClaPro+0;";
                                                     queryctr = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE, LOTE.proxCaducar as proxCaducar FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot, SUM(IF(F_FecCad <= DATE_ADD(curdate(),INTERVAL 180 DAY),1,0)) as proxCaducar FROM tb_lote l " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad >= DATE_ADD(curdate(),INTERVAL " + controladoDiasCad + " DAY) AND F_ExiLot > 0 GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";

                                                        break;
                                                    }
                                                    default: {
                                                         System.out.println("query normal " + query);
                                                    queryctr = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE, LOTE.proxCaducar as proxCaducar FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot, SUM(IF(F_FecCad <= DATE_ADD(curdate(),INTERVAL 180 DAY),1,0)) as proxCaducar FROM tb_lote l " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad >= curdate() AND F_ExiLot > 0 GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro not IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";
                                                       query = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot FROM tb_lote l " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND " + caducidad + " GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";
                                                    }
                                                }

                                               
                                                ResultSet rsetR1 = con.consulta(query);
                                              System.out.println("normal: " +  query);
                                            //    queryctr = "SELECT M.F_ClaPro,SUBSTR(M.F_DesPro,1,80) AS F_DesPro,REQ.F_CajasReq, REQ.F_Solicitado,REPLACE(M.F_ClaPro,'.',''),IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXITLOTE, LOTE.proxCaducar as proxCaducar FROM tb_unireq REQ INNER JOIN tb_medica M ON REQ.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot, SUM(IF(F_FecCad <= DATE_ADD(curdate(),INTERVAL 180 DAY),1,0)) as proxCaducar FROM tb_lote l " + UbicaDesc + " AND F_Proyecto='" + Proyecto + "' AND F_FecCad >= DATE_ADD(curdate(),INTERVAL " + controladoDiasCad + " DAY) AND F_ExiLot > 0 GROUP BY F_ClaPro) AS LOTE ON REQ.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica " + UbicaDesc + " AND L.F_Proyecto='" + Proyecto + "' GROUP BY F_ProMov) AS MOVI ON REQ.F_ClaPro=MOVI.F_ProMov WHERE F_ClaUni='" + rset.getString("F_ClaUni") + "' and F_Status =0 and F_Solicitado != 0 AND F_N" + Catalogo + "='1' AND M.F_StsPro='A' AND REQ.F_ClaPro IN (SELECT F_ClaPro from tb_controlados) group by F_IdReq order by M.F_ClaPro+0;";

                                                ResultSet rsetControlado = con.consulta( queryctr);
                                                System.out.println("Controlado: " +  queryctr);

                                                RequeridoVOList requeridos = new RequeridoVOList();

                                                while (rsetR1.next()) {
                                                    RequeridoVO requerido = new RequeridoVO();
                                                    requerido.setClave(rsetR1.getString(1));
                                                    requerido.setDescripcion(rsetR1.getString(2));
                                                    requerido.setRequerido(rsetR1.getInt(3));
                                                    requerido.setSolicitado(rsetR1.getInt(4));
                                                    requerido.setClaveTrimed(rsetR1.getString(5));
                                                    requerido.setExistencia(rsetR1.getInt(8));
                                                    requerido.setProxCaducar(0);
                                                    requeridos.add(requerido);

                                                }

                                                while (rsetControlado.next() && !id.equals("19")) {
                                                    RequeridoVO requerido = new RequeridoVO();
                                                    requerido.setClave(rsetControlado.getString(1));
                                                    requerido.setDescripcion(rsetControlado.getString(2));
                                                    requerido.setRequerido(rsetControlado.getInt(3));
                                                    requerido.setSolicitado(rsetControlado.getInt(4));
                                                    requerido.setClaveTrimed(rsetControlado.getString(5));
                                                    requerido.setExistencia(rsetControlado.getInt(8));
                                                    Integer proxCaducar = (rsetControlado.getInt("proxCaducar"));
                                                    requerido.setProxCaducar(proxCaducar);
                                                    requeridos.add(requerido);
                                                }

//                                                requeridos.sort();
                                                for (RequeridoVO requerido : requeridos.list) {
                                                    ExiLot = requerido.getExistencia();
                                        %>
                                        <tr
                                            <%
                                                if (requerido.getSolicitado() > ExiLot) {
                                                    out.println("class='success'");
                                                    ExiSol = ExiLot;
                                                } else {
                                                    if (requerido.getProxCaducar()) {
                                                        out.println("class='warning'");
                                                        proxACaducar = true;
                                                        proxACaducarClaves.add(requerido.getClave());
                                                    }
                                                    ExiSol = requerido.getSolicitado();
                                                }
                                                if (ExiLot < 0) {
                                                    ExiLot = 0;
                                                    ExiSol = 0;
                                                }
                                                TotalSur = TotalSur + ExiSol;


                                            %>
                                            >
                                            <td><%=requerido.getClave()%></td>
                                            <td><%=requerido.getDescripcion()%></td>
                                            <td><%=requerido.getSolicitado()%></td>
                                            <td ><small><input name="Cantidad_<%=rset.getString("F_ClaUni")%>_<%=requerido.getClaveTrimed().trim()%>" id="Cantidad_<%=rset.getString("F_ClaUni")%>_<%=requerido.getClaveTrimed().trim()%>" type="number" min="0" class="text-right form-control" value="<%=ExiSol%>" data-behavior="only-num" /></small></td>
                                            <td class="text-right"><%=formatter.format(ExiLot)%></td>
                                        </tr>                 
                                        <%
                                            }
                                        %>
                                        <%
                                            } catch (Exception e) {
                                                e.printStackTrace();
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
                        
                        <%
                                            TotalSur = 0;
                                        }

                                    }
}else{
System.out.println("consulta en null");
  out.println("<script>alert('Favor de Seleccionar Unidad Activa')</script>");
}      
                                } else {
                                    out.println("<script>alert('Favor de Seleccionar Catálogo')</script>");
                                }
System.out.println("ya me sali");
                                con.cierraConexion();
                            } catch (Exception e) {
                                out.println(e.getMessage());
                            }
                       %>
                        </div>
                       <%
                            if (banReq1 == 1) {
                        %>
                     
                        
                        <div class="row">
                            <h4 class="col-sm-2">Fecha de Entrega:</h4>
                            <div class="col-sm-2">
                                <input type="date" name="F_FecEnt" id="FecEnt" class="form-control" value="" min="<%=df2.format(new Date())%>" onkeydown="return false"  required />
                            </div>
                            <h4 class="col-sm-1">OC</h4>
                            <div class="col-sm-2">
                                <input class="form-control" name="OC" id="OC" type="text" />
                            </div>
                            <h4 class="col-sm-1">Catálogo</h4>
                            <div class="col-sm-1">
                                <input type="text" name="Cata" id="Cata" class="form-control" readonly="" required="" value="<%=Catalogo%>" />
                            </div>

                        </div>
                        <br/>
                        <input type="hidden" readonly="" class="form-control" name="Proyecto" id="Proyecto" value="<%=Proyecto%>"/>
                        <input type="hidden" readonly="" class="form-control" name="F_Tipo" id="F_Tipo" value="<%=tipoRemision%>"/>
                        <input type="hidden" readonly="" class="form-control" id="solicita" value="<%=solicita%>"/>
                        <input name="F_Juris" class="hidden" value="<%=UsuaJuris%>" />
                        <div class="row">
                            <div class="col-sm-6">
                                <button class="btn btn-block btn-warning" type="submit" name="accion" value="cancelar" onclick="return validaRemision()">Cancelar Folio(s)</button> 
                            </div>
                            <div class="col-sm-6">
                                <button class="btn btn-block btn-success" type="button" name="accion" value="generarRemision" id="BtngenerarRemision">Generar Folio(s)</button>
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
        <%@include file="jspf/piePagina.jspf" %>

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
        <script src="js/facturajs/AutomaticaFacturacion2Folio.js"></script>
        <script src="js/jquery.alphanum.js" type="text/javascript"></script>
        <script src="js/sweetalert.min.js" type="text/javascript"></script>
        <script>
                                    $(document).ready(function () {
//                                        $('#datosProv').dataTable();
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
                                                success: function (data) {
                                                    recargarReqs(data);
                                                }
                                            });
                                            function recargarReqs(data) {
                                                location.reload();
                                            }
                                        }
                                    }
                                    function valida_clave() {
                                        var missinginfo = "";
                                        if (($("#Unidad1").val() == "") && ($("#Unidad2").val() == "")) {
                                            missinginfo += "\n El campo Clave de la Unidad no debe de estar vacío";
                                        }
                                        if (missinginfo != "") {
                                            missinginfo = "\n TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ENVIAR PETICIÓN DE SOPORTE:\n" + missinginfo + "\n\n ¡INGRESA LOS DATOS FALTANTES Y TRATA OTRA VEZ!\n";
                                            alert(missinginfo);
                                            return false;
                                        } else {
                                            return true;
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
            $("#F_Tipo").val("<%=tipoRemision%>");
            if (<%=proxACaducar%> === true) {
                var legend = "Las claves marcadas en color amarillo son controlados con insumo PRÓXIMO A CADUCAR";
                document.getElementById("proxCaducarLegend").innerHTML = legend;
            }
        </script> 

    </body>
</html>

