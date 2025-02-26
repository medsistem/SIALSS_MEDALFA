<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="com.gnk.util.Calendario"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%java.text.DateFormat df = new java.text.SimpleDateFormat("yyyyMMddhhmmss"); %>
<%java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd"); %>
<%java.text.DateFormat df3 = new java.text.SimpleDateFormat("dd/MM/yyyy"); %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", Clave = "", Descripcion = "",tipou = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipou = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }

    Clave = request.getParameter("Clave");
    Descripcion = request.getParameter("Descripcion");

    if (Clave == null) {
        Clave = "";
    }
    if (Descripcion == null) {
        Descripcion = "";
    }

    ConectionDB con = new ConectionDB();
    
    boolean editable = true;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link href="css/navbar-fixed-top.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">       
        
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <hr/>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Catalogo de Insumo para la Salud</h3>
                </div>
              
                <div class="panel-body ">
                    <form class="form-horizontal" role="form" name="formulario1" id="formulario1" method="post" action="medicamento.jsp">
                        <div class="row">
                            <div class="form-group">                                
                                <label for="Clave" class="col-xs-1 control-label">CLAVE</label>
                                <div class="col-xs-2">
                                    <input type="text" class="form-control" id="Clave" name="Clave" maxlength="10" placeholder="CLAVE" onKeyPress="return tabular(event, this)"  />
                                </div>
                                <label for="Descripcion" class="col-xs-1 control-label">Descripción</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control" id="Descrip" maxlength="80" name="Descripcion" placeholder="Descripcion" />
                                </div>
                            </div>
                        </div>
                        <br/>
                        <div class="row">
                            <div class="col-sm-6">
                                <button class="btn btn-block btn-info glyphicon glyphicon-search" type="submit"  > Buscar</button> 
                            </div>
                            <% if(tipou.equals("1")){ %>
                            <div class="col-sm-6">
                                <% if (editable){%>
                                <a href="AltaClave.jsp" class="btn btn-block btn-success glyphicon glyphicon-saved" > Nuevo Insumo</a> 
                                <% }%>
                            </div>
                            <% } %>
                        </div>
                    </form>                    
                </div>
                <div class="panel-footer">
                    <form method="post" action="Medicamentos">
                        <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="datosmed">
                            <thead>
                                <tr>
                                    <th>CLAVE</th>
                                    <th>CLAVE SS</th>
                                    <th>Descripción</th>
                                    <th>Presentación</th>
                                    <th>Sts</th>
                                    <th>Tipo Medicamento</th>
                                   
                                    <th>Origen</th>
                                    <% if(tipou.equals("1") || tipou.equals("12")){ %>
                                     <th>Costo</th>
                                    <th>Modificar</th>
                                    <% } %>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    try {
                                        con.conectar();
                                        ResultSet rset = null;
                                        int tipo = 0, ori = 0;
                                        String DesTipo = "";
                                        if (Descripcion != "") {
                                            rset = con.consulta("SELECT F_ClaPro, F_ClaProSS, F_DesPro, F_StsPro, F_TipMed, F_Costo, F_DesOri, F_PrePro FROM tb_medica m INNER JOIN tb_origen as o ON F_Origen = F_ClaOri   where F_DesPro like '%" + Descripcion + "%' ORDER BY F_ClaPro+0 ASC");
                                        } else if (Clave != "") {
                                            rset = con.consulta("SELECT F_ClaPro, F_ClaProSS, F_DesPro, F_StsPro, F_TipMed, F_Costo, F_DesOri, F_PrePro FROM tb_medica m INNER JOIN tb_origen as o ON   F_Origen = F_ClaOri   where F_ClaPro = '" + Clave + "' ORDER BY F_ClaPro+0 ASC");
                                        } else {
                                            rset = con.consulta("SELECT F_ClaPro, F_ClaProSS, F_DesPro, F_StsPro, F_TipMed, F_Costo, F_DesOri, F_PrePro FROM tb_medica m INNER JOIN tb_origen as o ON   F_Origen = F_ClaOri   ORDER BY F_ClaPro+0 ASC");
                                        }

                                        while (rset.next()) {
                                            tipo = Integer.parseInt(rset.getString(5));
                                            
                                            if (tipo == 2504) {
                                                DesTipo = "MEDICAMENTO";
                                            } else {
                                                DesTipo = "MAT. CURACIÓN";
                                            }
                                %>
                                <tr class="odd gradeX">
                                    <td class="text-center"><small><%=rset.getString(1)%></small></td>
                                    <td class="text-center"><small><%=rset.getString(2)%></small></td>
                                    <td><small><%=rset.getString(3)%></small></td>
                                    <td><small><%=rset.getString(8)%></small></td>                                    
                                    <td class="text-center"><small><%=rset.getString(4)%></small></td>
                                    
                                    <td class="text-center"><small><%=DesTipo%></small></td>
                                                                
                                    <td class="text-center"><small><%=rset.getString(7)%></small></td>
                                    <% if(tipou.equals("1") || tipou.equals("12")){ %>
                                    <td class="text-right"><small>$ <%=formatterDecimal.format(rset.getDouble(6))%></small></td>          
                                    <td><small><a href="ModiClave.jsp?Clave=<%=rset.getString(1)%>" class="btn btn-block btn-warning"><span class="glyphicon glyphicon-pencil"></span></a></small></td>
                                    <% } %>
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
                    </form>
                </div>
            </div>
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
        <script>
                                    $(document).ready(function () {
                                        $('#datosmed').dataTable();
                                    });
        </script>
        <script>


            function isNumberKey(evt, obj)
            {
                var charCode = (evt.which) ? evt.which : event.keyCode;
                if (charCode === 13 || charCode > 31 && (charCode < 48 || charCode > 57)) {
                    if (charCode === 13) {
                        frm = obj.form;
                        for (i = 0; i < frm.elements.length; i++)
                            if (frm.elements[i] === obj)
                            {
                                if (i === frm.elements.length - 1)
                                    i = -1;
                                break
                            }
                        /*ACA ESTA EL CAMBIO*/
                        if (frm.elements[i + 1].disabled === true)
                            tabular(e, frm.elements[i + 1]);
                        else
                            frm.elements[i + 1].focus();
                        return false;
                    }
                    return false;
                }
                return true;

            }



        </script>
        <script language="javascript">
            function justNumbers(e)
            {
                var keynum = window.event ? window.event.keyCode : e.which;
                if ((keynum == 8) || (keynum == 46))
                    return true;

                return /\d/.test(String.fromCharCode(keynum));
            }
            otro = 0;
            function LP_data() {
                var key = window.event.keyCode;//codigo de tecla. 
                if (key < 48 || key > 57) {//si no es numero 
                    window.event.keyCode = 0;//anula la entrada de texto. 
                }
            }
            function anade(esto) {
                if (esto.value.length === 0) {
                    if (esto.value.length == 0) {
                        esto.value += "(";
                    }
                }
                if (esto.value.length > otro) {
                    if (esto.value.length == 4) {
                        esto.value += ") ";
                    }
                }
                if (esto.value.length > otro) {
                    if (esto.value.length == 9) {
                        esto.value += "-";
                    }
                }
                if (esto.value.length < otro) {
                    if (esto.value.length == 4 || esto.value.length == 9) {
                        esto.value = esto.value.substring(0, esto.value.length - 1);
                    }
                }
                otro = esto.value.length
            }


            function tabular(e, obj)
            {
                tecla = (document.all) ? e.keyCode : e.which;
                if (tecla != 13)
                    return;
                frm = obj.form;
                for (i = 0; i < frm.elements.length; i++)
                    if (frm.elements[i] == obj)
                    {
                        if (i == frm.elements.length - 1)
                            i = -1;
                        break
                    }
                /*ACA ESTA EL CAMBIO*/
                if (frm.elements[i + 1].disabled == true)
                    tabular(e, frm.elements[i + 1]);
                else
                    frm.elements[i + 1].focus();
                return false;
            }

        </script>
        <script type="text/javascript">
            $(function () {
                var desc = [
            <%
               try {
                  con.conectar();
                      ResultSet rset2 = con.consulta("SELECT SUBSTR( F_DesPro, 1, 200 ) AS F_DesPro FROM tb_medica ORDER BY F_DesPro;");
                      while (rset2.next()) {
                          out.println(" '" + rset2.getString(1) + "',");
                      }
                        con.cierraConexion();
                  } catch (Exception e) {
                    e.getMessage();
                  }
                
              
            %>
                ];
                $("#Descrip").autocomplete({
                    source: desc
                });
            });
        </script>
        
        <script type="text/javascript">
            $(function () {
                var availableTags = [
            <%
              try {
                  con.conectar();
                
                      ResultSet rset = con.consulta("SELECT F_ClaPro FROM tb_medica ORDER BY F_ClaPro+0");
                      while (rset.next()) {
                          out.println("'" + rset.getString(1) + "',");
                      }
                  
                  con.cierraConexion();
              } catch (Exception e) {
                    e.getMessage();
              }
            %>
                ];
                $("#Clave").autocomplete({
                    source: availableTags
                });
            });
        </script>
    </body>
</html>



