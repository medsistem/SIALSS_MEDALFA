<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Sistemas
--%>

<%@page import="java.util.Date"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%

    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    String Secuencial="", FechaSe="", Factura="";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();

    String Fecha11 = "",Fecha22 = "", ClaPro = "", Radio11="",ClaCli = "",  DesPro = "";
    String Fecha1="",Fecha2="",unidad="",Clave="",radio="";
    try {        
        Fecha11 = (String) sesion.getAttribute("Fecha11");
        Fecha22 = (String) sesion.getAttribute("Fecha22");
        Radio11 = (String) sesion.getAttribute("Radio11");
        ClaCli = (String) sesion.getAttribute("Unidad11");
        ClaPro = (String) sesion.getAttribute("Clave11");
        
    } catch (Exception e) {

    }
    if(Fecha11 == null){Fecha11="";}
    if(Fecha22 == null){Fecha22="";}
    if(Radio11 == null){Radio11="";}
    if(ClaCli == null){ClaCli="";}
    if(ClaPro == null){ClaPro="";}
    try{
        Fecha1 = request.getParameter("fecha1");
        Fecha2 = request.getParameter("fecha2");
        unidad = request.getParameter("unidad");
        Clave = request.getParameter("Clave");
        radio = request.getParameter("radio");
    }catch(Exception e) {}
    if(Fecha1 == null){Fecha1=Fecha11;}       
    if(Fecha2 == null){Fecha2=Fecha22;}
    if(unidad == null){unidad=ClaCli;}
    if(Clave == null){Clave=ClaPro;}
    if(unidad.equals("--Seleccione--")){unidad="";}       
    if(Clave.equals("--Seleccione--")){Clave="";}       
    if(radio == null){radio=Radio11;}       
    System.out.println("F1"+Fecha1+" F2"+Fecha2+" Un"+unidad+" Cla"+Clave+" Ra"+radio);
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Bootstrap -->
        <link href="../css/bootstrap.css" rel="stylesheet" media="screen">
        <link href="../css/topPadding.css" rel="stylesheet">
        <link href="../css/datepicker3.css" rel="stylesheet">
        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipal.jspf" %>

            
                <div class="col-sm-12">
                    <h4>Actualizar Txt</h4>
                </div>
            <br />
            <hr/>
            <form action="ActualizaTxt.jsp" method="post">
                <div class="row">
                    <div class="panel-footer">
                        <div class="row">
                            <div class="row">
                            <label class="control-label col-lg-2" for="fecha">Rango Fecha</label>
                            <div class="col-lg-2">
                                    <input class="form-control" id="fecha1" name="fecha1" type="date" />
                                </div>
                            <div class="col-lg-2">
                                    <input class="form-control" id="fecha2" name="fecha2" type="date" />
                                </div>
                            <label class="control-label col-lg-2" for="fecha_ini">Tipo Insumo</label>    
                            <div class="col-lg-4">
                                <input type="radio" id="radio" name="radio" checked="true" value="1" > Ambos
                                <input type="radio" id="radio" name="radio" value="2"> Admón
                                <input type="radio" id="radio" name="radio" value="3"> Venta
                            </div>
                            </div>
                            <br/>
                            <div class="row">
                            <label class="control-label col-sm-2" for="fecha_ini">Unidad</label>    
                            <div class="col-sm-4">
                                <select id="unidad" name="unidad">
                                    <option>--Seleccione--</option>
                                    <%
                                    try{
                                        con.conectar();
                                        ResultSet Unidades = con.consulta("SELECT F_ClaUniIS,CONCAT(F_DesUniIS,' - ',F_ClaUniIS) AS F_DesUniIS FROM tb_unidis ORDER BY F_DesUniIS ASC");
                                        while(Unidades.next()){
                                    %>
                                    <option value="<%=Unidades.getString(1)%>"><%=Unidades.getString(2)%></option>
                                    <%
                                        }
                                    con.cierraConexion();
                                    }catch(Exception e){}    
                                    %>
                                </select>
                            </div>                           
                            </div>
                                <br/>
                            <div class="row">                            
                            <label class="control-label col-sm-2" for="fecha_ini">Clave</label>    
                            <div class="col-sm-1">
                                <select id="Clave" name="Clave">
                                    <option>--Seleccione--</option>
                                    <%
                                    try{
                                        con.conectar();
                                        ResultSet Claves = con.consulta("SELECT F_ClaArtIS,CONCAT(F_ClaArtIS,' - ',F_DesPro) as F_DesPro FROM tb_artiis a INNER JOIN tb_medica m on a.F_ClaInt=m.F_ClaPro");
                                        while(Claves.next()){
                                    %>
                                    <option value="<%=Claves.getString(1)%>"><%=Claves.getString(2)%></option>
                                    <%
                                        }
                                    con.cierraConexion();
                                    }catch(Exception e){}    
                                    %>
                                </select>
                            </div>
                            </div>
                            <div class="panel-body">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <button class="btn btn-block btn-success" id="btn_capturar" name="btn_capturar" onclick="">Generar</button>
                                    </div>
                                </div>
                            </div>   
                        </div> 
                    </div>
                </div>
            </form>
                                <form action="../ActualizaTxt" method="post" id="formCambioUnidad">
                                    <input class="hidden" name="accion" value="CambioUnidad"  />
                                    <input class="hidden" id="Fecha1" name="Fecha1" value="<%=Fecha1%>"  />
                                    <input class="hidden" id="Fecha2" name="Fecha2" value="<%=Fecha2%>"  />
                                    <input class="hidden" id="Radio" name="Radio" value="<%=radio%>"  />                                    
                                    <input class="hidden" id="Unidad1" name="Unidad1" value="<%=unidad%>"  />
                                    <input class="hidden" id="Clave1" name="Clave1" value="<%=Clave%>"  />
                                    <input class="hidden" id="Secuencial" name="Secuencial" value=""  />  
                                    <input class="hidden" id="Unidad" name="Unidad" value=""  />
                                    
                                </form>
                                <form action="../ActualizaTxt" method="post" id="formCambiOriCost">
                                    <input class="hidden" name="accion" value="CambiOrCost"  />
                                    <input class="hidden" id="Fecha1" name="Fecha1" value="<%=Fecha1%>"  />
                                    <input class="hidden" id="Fecha2" name="Fecha2" value="<%=Fecha2%>"  />
                                    <input class="hidden" id="Radio" name="Radio" value="<%=radio%>"  />                                    
                                    <input class="hidden" id="Unidad1" name="Unidad1" value="<%=unidad%>"  />
                                    <input class="hidden" id="Clave1" name="Clave1" value="<%=Clave%>"  />
                                    <input class="hidden" id="Secuencial" name="Secuencial" value=""  />                                    
                                    <input class="hidden" id="Costo" name="Costo" value=""  />
                                    <input class="hidden" id="Origen" name="Origen" value=""  />
                                    <div class="panel panel-success">
                                        <div class="panel-body table-responsive">
                                            <div style="width:100%; height:400px; overflow:auto;">
                                                <table class="table table-bordered table-striped">
                                                    <thead>
                                                        <tr>
                                                            <td>Secuenciales</td>
                                                            <td>Sts</td>
                                                            <td>Surtido</td>
                                                            <td>Clave</td>
                                                            <td>Fecha</td>
                                                            <td>Surtida</td>
                                                            <td>Costo U</td>
                                                            <td>Unidad</td>
                                                            <td>Nombre</td>
                                                            <td>Jurisdicción</td>
                                                            <td>Municipio</td>
                                                            <td>Localidad</td>
                                                            <td></td>                                                            
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <%
                                                        try{
                                                            con.conectar();
                                                            ResultSet Datos=null;
                                                            String QueryF="",QueryU="",QueryC="",Query="",QueryR="";
                                                            if(!(Fecha1 == "") && (!(Fecha2 == ""))){
                                                                QueryF = " F_Fecsur BETWEEN '"+Fecha1+"' AND '"+Fecha2+"' ";
                                                                if(!(unidad == "")){
                                                                    QueryU = " AND F_Cveuni='"+unidad+"' ";                                                                    
                                                                }
                                                                if(!(Clave == "")){
                                                                    QueryC = " AND LTRIM(F_Cveart)=LTRIM('"+Clave+"') ";
                                                                }
                                                                if(radio.equals("2")){
                                                                    QueryR=" AND F_Idsur='1' ";
                                                                }else if(radio.equals("3")){
                                                                    QueryR=" AND F_Idsur='2' ";
                                                                }
                                                                Query = QueryF + QueryU + QueryC + QueryR;
                                                            }
                                                            
                                                            Datos = con.consulta("SELECT F_Secuencial,F_Status,F_Idsur,F_Cveart,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Cansur,F_CosUni,F_Cveuni,u.F_DesUniIS,t.F_CveJur,t.F_Cvemun,t.F_CveLoc FROM tb_txtis t INNER JOIN tb_unidis u on t.F_Cveuni=u.F_ClaUniIS AND t.F_CveJur=u.F_JurUniIS AND t.F_Cvemun=u.F_MunUniIS AND t.F_CveLoc=u.F_LocUniIS WHERE "+Query+";");
                                                            while(Datos.next()){
                                                        %>
                                                        <tr>
                                                            <td><%=Datos.getString(1)%></td>
                                                            <td><%=Datos.getString(2)%></td>
                                                            <td><%=Datos.getString(3)%></td>
                                                            <td><%=Datos.getString(4)%></td>
                                                            <td><%=Datos.getString(5)%></td>
                                                            <td><%=Datos.getString(6)%></td>
                                                            <td><%=Datos.getString(7)%></td>
                                                            <td><%=Datos.getString(8)%></td>
                                                            <td><%=Datos.getString(9)%></td>
                                                            <td><%=Datos.getString(10)%></td>
                                                            <td><%=Datos.getString(11)%></td>
                                                            <td><%=Datos.getString(12)%></td>                                                            
                                                            <%if(!(unidad == "")){%>
                                                            <td><button type="button" class="btn btn-success btn-block" data-toggle="modal" data-target="#modalCambioUnidad<%=Datos.getString(8)%>" id="btnRecalendarizar" >Unidad</button></td>
                                                            <%}else{%>
                                                            <td><button type="button" class="btn btn-warning btn-block" data-toggle="modal" data-target="#modalCambioFecha<%=Datos.getString(1)%>" id="btnRecalendarizar" >Ori & Cost</button></td>
                                                            <%}%>
                                                        </tr>
                                                        <%
                                                            }
                                                           con.cierraConexion();
                                                            }catch(Exception e){}
                                                        %>     
                                                       
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                                <!-- Modal -->
                                <%
                                try{
                                    con.conectar();
                                    ResultSet Datos=null;
                                    String QueryF="",QueryU="",QueryC="",Query="",QueryR="";
                                    if(!(Fecha1 == "") && (!(Fecha2 == ""))){
                                        QueryF = " F_Fecsur BETWEEN '"+Fecha1+"' AND '"+Fecha2+"' ";
                                        
                                        if(!(unidad == "")){
                                            QueryU = " AND F_Cveuni='"+unidad+"' ";                                                                    
                                        }
                                        if(!(Clave == "")){
                                            QueryC = " AND LTRIM(F_Cveart)=LTRIM('"+Clave+"') ";
                                        }
                                        
                                        if(radio.equals("2")){
                                            QueryR=" AND F_Idsur='1' ";
                                        }else if(radio.equals("3")){
                                            QueryR=" AND F_Idsur='2' ";
                                        }
                                        Query = QueryF + QueryU + QueryC + QueryR;
                                    }

                                    Datos = con.consulta("SELECT F_Secuencial,F_Status,F_Idsur,F_Cveart,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Cansur,F_CosUni,F_Cveuni,u.F_DesUniIS,t.F_CveJur,t.F_Cvemun,t.F_CveLoc FROM tb_txtis t INNER JOIN tb_unidis u on t.F_Cveuni=u.F_ClaUniIS AND t.F_CveJur=u.F_JurUniIS AND t.F_Cvemun=u.F_MunUniIS AND t.F_CveLoc=u.F_LocUniIS WHERE "+Query+";");
                                    while(Datos.next()){
                                %>                                
                                <div class="modal fade" id="modalCambioFecha<%=Datos.getString(1)%>" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <form>
                                                <div class="modal-header">
                                                    <div class="row">
                                                        <h4 class="col-sm-12">Actualizar Origen o Costo</h4>
                                                    </div>
                                                </div>
                                                <div class="modal-header">
                                                    <div class="row">
                                                        <label class="control-label col-sm-2" for="fecha">Clave</label>
                                                        <div class=" col-sm-4">
                                                            <input type="text" class="form-control" required name="" id="ModalSecu" readonly="" value="<%=Datos.getString(4)%>" />
                                                        </div>                                                        
                                                    </div>
                                                </div>
                                                <div class="modal-body">
                                                    <h4 class="modal-title" id="myModalLabel">Origen:</h4>
                                                    <div class="row">
                                                        <div class="col-sm-4">
                                                            <input type="text" class="form-control" required name="" id="ModalOrigen" readonly="" value="<%=Datos.getString(3)%>" />
                                                        </div>
                                                        <label class="control-label col-sm-1" for="fecha">Por</label>
                                                        <div class="col-sm-4">
                                                           <input type="text" class="form-control" required name="" id="ModalOrigen2" />
                                                        </div>
                                                    </div>
                                                    <h4 class="modal-title" id="myModalLabel">Costo:</h4>
                                                    <div class="row">
                                                        <div class="col-sm-4">
                                                            <input type="text" class="form-control" required name="" id="ModalCosto" readonly="" value="<%=Datos.getString(7)%>" />
                                                        </div>
                                                        <label class="control-label col-sm-1" for="fecha">Por</label>
                                                        <div class="col-sm-4">
                                                            <input type="text" class="form-control" required name="" id="ModalCosto2" />
                                                        </div>
                                                    </div>
                                                    <div style="display: none;" class="text-center" id="Loader">
                                                        <img src="imagenes/ajax-loader-1.gif" height="150" />
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-success" onclick="return confirmaModal();" name="accion" value="CambiOrCost">Actualizar</button>
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                    con.cierraConexion();
                                }catch(Exception e){}
                                %>
                                <!-- Modal --> 
                                <!-- Modal -->
                                <%
                                try{
                                    con.conectar();
                                    ResultSet Datos=null;
                                    String QueryF="",QueryU="",QueryC="",Query="",QueryR="";
                                    if(!(Fecha1 == "") && (!(Fecha2 == ""))){
                                        QueryF = " F_Fecsur BETWEEN '"+Fecha1+"' AND '"+Fecha2+"' ";
                                        
                                        if(!(unidad == "")){
                                            QueryU = " AND F_Cveuni='"+unidad+"' ";                                                                    
                                        }
                                        if(!(Clave == "")){
                                            QueryC = " AND LTRIM(F_Cveart)=LTRIM('"+Clave+"') ";
                                        }
                                        
                                        if(radio.equals("2")){
                                            QueryR=" AND F_Idsur='1' ";
                                        }else if(radio.equals("3")){
                                            QueryR=" AND F_Idsur='2' ";
                                        }
                                        Query = QueryF + QueryU + QueryC + QueryR;
                                    }

                                    Datos = con.consulta("SELECT F_Secuencial,F_Status,F_Idsur,F_Cveart,DATE_FORMAT(F_Fecsur,'%d/%m/%Y') AS F_Fecsur,F_Cansur,F_CosUni,F_Cveuni,u.F_DesUniIS,t.F_CveJur,t.F_Cvemun,t.F_CveLoc FROM tb_txtis t INNER JOIN tb_unidis u on t.F_Cveuni=u.F_ClaUniIS AND t.F_CveJur=u.F_JurUniIS AND t.F_Cvemun=u.F_MunUniIS AND t.F_CveLoc=u.F_LocUniIS WHERE "+Query+";");
                                    while(Datos.next()){
                                %>                                
                                <div class="modal fade" id="modalCambioUnidad<%=Datos.getString(8)%>" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <form>
                                                <div class="modal-header">
                                                    <div class="row">
                                                        <h4 class="col-sm-12">Actualizar Unidad</h4>
                                                    </div>
                                                </div>
                                                <div class="modal-header">
                                                    <div class="row">
                                                        <label class="control-label col-sm-2" for="fecha">Clave Unidad</label>
                                                        <div class=" col-sm-4">
                                                            <input type="text" class="form-control" required name="" id="ModalSecu" readonly="" value="<%=Datos.getString(8)%>" />
                                                        </div>                                                        
                                                    </div>
                                                </div>
                                                <div class="modal-body">
                                                    <h4 class="modal-title" id="myModalLabel">Cambio Por:</h4>
                                                    <div class="row">
                                                        <div class="col-sm-4">
                                                            <select id="unidad1" name="unidad1">
                                                                    <option>--Seleccione--</option>
                                                                    <%
                                                                    try{
                                                                        con.conectar();
                                                                        ResultSet Unidades1 = con.consulta("SELECT F_ClaUniIS,CONCAT(F_ClaUniIS,' - ',F_DesUniIS) AS F_DesUniIS FROM tb_unidis");
                                                                        while(Unidades1.next()){
                                                                    %>
                                                                    <option value="<%=Unidades1.getString(1)%>"><%=Unidades1.getString(2)%></option>
                                                                    <%
                                                                        }
                                                                    con.cierraConexion();
                                                                    }catch(Exception e){}    
                                                                    %>
                                                                </select>                                                            
                                                        </div>                                                        
                                                    </div>                                                    
                                                    <div style="display: none;" class="text-center" id="Loader">
                                                        <img src="imagenes/ajax-loader-1.gif" height="150" />
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-success" onclick="return confirmaModalU();" name="accion" value="CambioUnidad">Actualizar</button>
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                    con.cierraConexion();
                                }catch(Exception e){}
                                %>
                                <!-- Modal --> 
            <div style="display: none;" class="text-center" id="Loader">
                <img src="../imagenes/ajax-loader-1.gif" height="150" />
            </div>
        <%@include file="../jspf/piePagina.jspf" %>
        <!-- 
    ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="../js/jquery-1.9.1.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/bootstrap-datepicker.js"></script>  
        <script>
            function valida_alta() {
                var fecha_fin = $("#fecha_fin").val();
                var fecha_ini = $("#fecha_ini").val();
                

                if (fecha_ini === "" && fecha_fin==="") {
                    alert("Seleccione Rango de Fechas Fechas");
                    return false;
                }
                    document.getElementById('Loader').style.display = 'block';
                
            }
            
            function confirmaModal() {
                var valida = confirm('Seguro que desea actualizar Txt?');
                var secu = $('#ModalSecu').val();
                var Origen = $('#ModalOrigen2').val();
                var Costo = $('#ModalCosto2').val();
                //alert("Secu: "+secu);
                if ((Origen === "") && (Costo === "")) {
                    alert('Ingrese Datos');
                    return false;
                } else {
                    if (valida) {
                        $('#Secuencial').val($('#ModalSecu').val());
                        $('#Origen').val($('#ModalOrigen2').val());
                        $('#Costo').val($('#ModalCosto2').val());
                        $('#formCambiOriCost').submit();
                    } else {
                        return false;
                    }
                }
            }
            
            function confirmaModalU() {
                var valida = confirm('Seguro que desea actualizar Txt?');
                var Unidad = $('#unidad1').val();
               // alert("Uni: "+Unidad);
                if (Unidad === "--Seleccione--") {
                    alert('Seleccione Unidad');
                    return false;
                } else {
                    if (valida) {
                        $('#Unidad').val($('#unidad1').val());
                        $('#formCambioUnidad').submit();
                    } else {
                        return false;
                    }
                }
            }
      </script>
    </body>

</html>

