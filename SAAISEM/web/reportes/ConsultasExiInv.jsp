<%-- 
    Document   : cambioFechas
    Created on : 14/04/2015, 12:58:35 PM
    Author     : Americo
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>

<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    ConectionDB con = new ConectionDB();


%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="../css/navbar-fixed-top.css" rel="stylesheet">
        <link href="../css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="../css/dataTables.bootstrap.css">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="../jspf/menuPrincipal.jspf"%>

            <div class="panel-heading">
                <h3 class="panel-title">Consulta Inventario por Mes en Cedis</h3>
            </div>
            <form action="../Facturacion" method="post" id="formCambioFechas">
                <div class="row col-sm-6">
                    <a class="btn btn-block btn-info" href="gnrExiCedisInv.jsp">Exportar Global<span class="glyphicon glyphicon-save"></span></a>
                </div>                
                <div>                    
                    <div class="panel panel-success">
                        <div class="panel-body table-responsive">
                            <div style="width:100%; height:500px; overflow:auto;">
                                <table class="table table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th style="text-align: center">Mes</th>
                                            <th style="text-align: center">Inv. Inicio Mes</th>
                                            <th style="text-align: center">Entrada Por Inv. Inicial</th>                                            
                                            <th style="text-align: center">Entrada Por Compra</th>
                                            <th style="text-align: center">Entrada Por Devoluci&oacute;n</th>
                                            <th style="text-align: center">Entrada Por Otros Movimientos</th>
                                            <th style="text-align: center">Salida Por Facturaci&oacute;n</th>
                                            <th style="text-align: center">Salida Por Otros Movimientos</th>
                                            <th style="text-align: center">Inv. Final Mes</th>
                                            <th style="text-align: center">Descargar</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%                                            
                                            try {
                                                con.conectar();
                                                String Fecha = "", MesT = "", FechaI = "", FechaF = "", MesD = "";
                                                int anno = 0, mes = 0, mesd = 0, dia = 0, FechaE =0, annoIni = 2019;
                                                int InvIni2 = 0,InvIni = 0, Compra = 0, Devo = 0, Eotros = 0, SFact = 0, SOtros = 0, InvFinal = 0, EInvMes = 0;
                                                int InvIni1 = 0, Compra1 = 0, Devo1 = 0, Eotros1 = 0, SFact1 = 0, SOtros1 = 0, InvFinal1 = 0, EInvMes1 = 0;
                                               
                                                ResultSet ConsultaMov = null;
                                                 ResultSet ConsultaMov2 = null;
                                                ResultSet Consulta = null;

                                                con.actualizar("DELETE FROM tb_invmescedisglobal;");
                                                ConsultaMov = con.consulta("SELECT  YEAR(CURDATE())-1");
                                                 if (ConsultaMov.next()) {
                                                FechaE = ConsultaMov.getInt(1);
                                                 }
                                                 System.out.println( FechaE);
                                                 
                                                  do { 
                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov * F_SigMov) FROM tb_movinv WHERE  F_FecMov BETWEEN '2019-01-02' AND '"+ FechaE+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                   
                                                    InvIni2 = ConsultaMov.getInt(1);
                                                }
                                                 ConsultaMov2 = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov='11' AND F_FecMov BETWEEN '2019-01-02' AND '"+ FechaE+"-12-31' and F_ProMov <> '9999'NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov2.next()) {
                                                   
                                                    InvIni = ConsultaMov2.getInt(1);
                                                }
                                                 if(annoIni == 2019){
                                                con.insertar("INSERT INTO tb_invmescedisglobal VALUES('AÑO "+annoIni+" a "+annoIni+"','0','" + InvIni + "','0','0','0','0','0','0','12','"+annoIni+"',0)");
                                                 }
                                                 else{
                                                   con.insertar("INSERT INTO tb_invmescedisglobal VALUES('AÑO "+annoIni+" a "+annoIni+"','"+InvFinal+"','" + InvIni + "','0','0','0','0','0','0','12','"+annoIni+"',0)");
                                                  
                                                  }
                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov='1' AND F_FecMov BETWEEN '2019-01-02'  AND '"+annoIni+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                    Compra = ConsultaMov.getInt(1);
                                                }
                                                 System.out.println("Compra:"+ Compra);
                                                 
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_Compra='" + Compra + "' WHERE F_Nmes='12' AND F_Anno = '"+annoIni+"';");

                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov in (3,4,5,6,20) AND F_FecMov BETWEEN '2019-01-02'  AND '"+annoIni+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                    Devo = ConsultaMov.getInt(1);
                                                }
                                                System.out.println("Devo:"+ Devo);
                                                
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_Devo='" + Devo + "' WHERE F_Nmes='12' AND F_Anno= '"+annoIni+"';");

                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov in (2,7,8,9,10,13,16,18,19,21,23,30) AND F_FecMov BETWEEN '2019-01-02'  AND '"+annoIni+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                    Eotros = ConsultaMov.getInt(1);
                                                }
                                                System.out.println("Eotros:"+ Eotros);
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_EoMov='" + Eotros + "' WHERE F_Nmes='12' AND F_Anno= '"+annoIni+"';");

                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov = '51' AND F_FecMov BETWEEN '2019-01-02'  AND '"+annoIni+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                    SFact = ConsultaMov.getInt(1);
                                                }
                                                System.out.println("SFact:"+ SFact);
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_SalFact='" + SFact + "' WHERE F_Nmes='12' AND F_Anno= '"+annoIni+"';");

                                                ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov > '51' AND F_ConMov < '1000' AND F_FecMov BETWEEN '2019-01-02'  AND '"+annoIni+"-12-31' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                if (ConsultaMov.next()) {
                                                    SOtros = ConsultaMov.getInt(1);
                                                }
                                                System.out.println("SOtros :"+ SOtros );
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_SalOtroMov='" + SOtros + "' WHERE F_Nmes='12' AND F_Anno= '"+annoIni+"';");

                                                InvFinal = (InvIni +  Compra + Devo + Eotros) - (SFact + SOtros);
                                                con.actualizar("UPDATE tb_invmescedisglobal SET F_InvFinal='" + InvFinal + "' WHERE F_Nmes='12' AND F_Anno= '"+annoIni+"';");
                                                
                                                InvIni = 0;
                                                Compra = 0;
                                                Devo = 0;
                                                Eotros = 0;
                                                SFact = 0;
                                                SOtros = 0;
                                             

                                                ResultSet ConsulFec = con.consulta("SELECT CURDATE() AS fecha,MONTH(CURDATE()) AS mes,YEAR(CURDATE()) AS anno");
                                                if (ConsulFec.next()) {
                                                    Fecha = ConsulFec.getString(1);
                                                   // mesd = 12;
                                                    mesd = ConsulFec.getInt(2);
                                                    anno = ConsulFec.getInt(3);
                                                   // anno = 2019;
                                                }
                                               annoIni = annoIni +1;
                                               } while (annoIni <= FechaE);//fin del ciclo
                                                 InvFinal = 0;
                                                 
                                                for (mes = 1; mes <= mesd; mes++) {
                                                    if (mes < 10) {
                                                        MesT = "0" + mes;
                                                    } else {
                                                        MesT = "" + mes;
                                                    }
                                                    if (mes == 1) {
                                                        dia = 31;
                                                        MesD = "ENERO";
                                                    } else if (mes == 2) {
                                                        if ((anno == 2016) || (anno == 2020) || (anno == 2024)) {
                                                            dia = 29;
                                                        } else {
                                                            dia = 28;
                                                        }
                                                        MesD = "FEBRERO";
                                                    } else if (mes == 3) {
                                                        dia = 31;
                                                        MesD = "MARZO";
                                                    } else if (mes == 4) {
                                                        dia = 30;
                                                        MesD = "ABRIL";
                                                    } else if (mes == 5) {
                                                        dia = 31;
                                                        MesD = "MAYO";
                                                    } else if (mes == 6) {
                                                        dia = 30;
                                                        MesD = "JUNIO";
                                                    } else if (mes == 7) {
                                                        dia = 31;
                                                        MesD = "JULIO";
                                                    } else if (mes == 8) {
                                                        dia = 31;
                                                        MesD = "AGOSTO";
                                                    } else if (mes == 9) {
                                                        dia = 30;
                                                        MesD = "SEPTIEMBRE";
                                                    } else if (mes == 10) {
                                                        dia = 31;
                                                        MesD = "OCTUBRE";
                                                    } else if (mes == 11) {
                                                        dia = 30;
                                                        MesD = "NOVIEMBRE";
                                                    } else if (mes == 12) {
                                                        dia = 31;
                                                        MesD = "DICIEMBRE";
                                                    }
                                                    FechaI = anno + "-" + MesT + "-01";
                                                    FechaF = anno + "-" + MesT + "-" + dia;
                                                       //  FechaI = "2019" + "-" + MesT + "-01";
                                                   // FechaF = "2019" + "-" + MesT + "-" + dia;
                                                    ConsultaMov = con.consulta("SELECT F_InvFinal FROM tb_invmescedisglobal;");
                                                    while (ConsultaMov.next()) {
                                                        EInvMes = ConsultaMov.getInt(1);
                                                    }
                                                     System.out.println("mes: "+mes);
                                                    con.insertar("INSERT INTO tb_invmescedisglobal VALUES('" + MesD + "-" + anno + "','" + EInvMes + "','0','0','0','0','0','0','0','" + mes + "','" + anno + "',0)");
                                                   
                                                    System.out.println("fechas: "+FechaI+" a "+FechaF);
                                                    
                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov='11' AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        InvIni1 = ConsultaMov.getInt(1);
                                                    }
                                                    System.out.println("InvIni1:"+InvIni1);
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_InvIni='" + InvIni1 + "' WHERE F_Nmes='" + MesT + "' AND F_Anno='" + anno + "';");

                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov='1' AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        Compra1 = ConsultaMov.getInt(1);
                                                    }
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_Compra='" + Compra1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov in (3,4,5,6,20) AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        Devo1 = ConsultaMov.getInt(1);
                                                    }
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_Devo='" + Devo1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov in (2,7,8,9,10,13,16,18,19,21,23,30) AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        Eotros1 = ConsultaMov.getInt(1);
                                                    }
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_EoMov='" + Eotros1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov = '51' AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        SFact1 = ConsultaMov.getInt(1);
                                                    }
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_SalFact='" + SFact1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    ConsultaMov = con.consulta("SELECT SUM(F_CantMov) FROM tb_movinv WHERE F_ConMov > '51' AND F_ConMov < '1000' AND F_FecMov BETWEEN '" + FechaI + "' AND '" + FechaF + "' and F_ProMov NOT IN ('9999', '9998', '9996', '9995');");
                                                    if (ConsultaMov.next()) {
                                                        SOtros1 = ConsultaMov.getInt(1);
                                                    }
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_SalOtroMov='" + SOtros1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    InvFinal1 = (EInvMes + InvIni1 + Compra1 + Devo1 + Eotros1) - (SFact1 + SOtros1);
                                                    con.actualizar("UPDATE tb_invmescedisglobal SET F_InvFinal='" + InvFinal1 + "' WHERE F_Nmes='" + mes + "' AND F_Anno='" + anno + "';");

                                                    //mes = mes - 1;
                                                    EInvMes = 0;
                                                    InvIni = 0;
                                                    Compra = 0;
                                                    Devo = 0;
                                                    Eotros = 0;
                                                    SFact = 0;
                                                    SOtros = 0;
                                                    InvFinal = 0;

                                                }
                                                Consulta = con.consulta("SELECT * FROM tb_invmescedisglobal ORDER BY F_Anno,F_Nmes+0;");
                                                while (Consulta.next()) {
                                        %>
                                        <tr>                                        
                                            <td><%=Consulta.getString(1)%></td>
                                            <td><%=formatter.format(Consulta.getInt(2))%></td>                                            
                                            <td><%=formatter.format(Consulta.getInt(3))%></td>
                                            <td><%=formatter.format(Consulta.getInt(4))%></td>                                         
                                            <td><%=formatter.format(Consulta.getInt(5))%></td>
                                            <td><%=formatter.format(Consulta.getInt(6))%></td>
                                            <td><%=formatter.format(Consulta.getInt(7))%></td>
                                            <td><%=formatter.format(Consulta.getInt(8))%></td>
                                            <td><%=formatter.format(Consulta.getInt(9))%></td>
                                            <% if (Consulta.getInt(11) < anno) { %>
                                                 <td> </td>
                                            <%    }else{
                                            %>
                                            <td><a class="btn btn-block btn-warning" href="gnrExiCedisInvD.jsp?Mess=<%=Consulta.getInt(10)%>&Anno=<%=Consulta.getInt(11)%>"><span class="glyphicon glyphicon-save"></span></a></td>
                                          
                                       
                                             <%    }
                                            %>
                                        </tr>
                                        <%
                                                }
                                                con.cierraConexion();
                                            } catch (Exception e) {

                                            }

                                        %>
                                    </tbody>
                                </table>                                    
                            </div>
                        </div>
                    </div>
                </div>                
            </form>
        </div>




        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="../js/jquery-1.9.1.js"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui-1.10.3.custom.js"></script>
        <script src="../js/bootstrap-datepicker.js"></script>
        <script src="../js/jquery.dataTables.js"></script>
        <script src="../js/dataTables.bootstrap.js"></script>
        <script src="../js/bootstrap3-typeahead.js" type="text/javascript"></script>
        <script>
            $(document).ready(function() {
                $('#datosCompras').dataTable();
                $("#fecha").datepicker();
                $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});

                //$('#btnRecalendarizar').attr('disabled', true);
                //$('#btnImpMult').attr('disabled', true);
            });

        </script>
        <script>
            $(document).ready(function() {
                $('#datosCompras').dataTable();
                $("#fecha").datepicker();
                $("#fecha").datepicker('option', {dateFormat: 'dd/mm/yy'});

                //$('#btnRecalendarizar').attr('disabled', true);
                //$('#btnImpMult').attr('disabled', true);
            });

        </script>
        <script>
            $(document).ready(function() {

                $("#clave").typeahead({
                    source: function(request, response) {

                        $.ajax({
                            url: "../AutoCompleteMedicamentos",
                            dataType: "json",
                            data: request,
                            success: function(data, textStatus, jqXHR) {
                                //console.log(data);
                                var items = data;
                                response(items);
                            },
                            error: function(jqXHR, textStatus, errorThrown) {
                                console.log(textStatus);
                            }
                        });
                    }

                });
                $("#descripcion").typeahead({
                    source: function(request, response) {

                        $.ajax({
                            url: "../AutoCompleteMedicamentosDesc",
                            dataType: "json",
                            data: request,
                            success: function(data, textStatus, jqXHR) {
                                //console.log(data);
                                var items = data;
                                response(items);
                            },
                            error: function(jqXHR, textStatus, errorThrown) {
                                console.log(textStatus);
                            }
                        });
                    }

                });

            });
        </script>

    </body>
</html>

