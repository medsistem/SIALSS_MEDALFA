<%-- 
    Document   : genMarbete
    Created on : 11/05/2016, 12:47:24 PM
    Author     : juan
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("../index.jsp");
    }
    ConectionDB con = new ConectionDB();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SIALSS</title>
        <link href="../css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="../css/cupertino/jquery-ui-1.10.3.custom.css"/>
        <script src="../js/jquery-1.9.1.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js"></script>
        <script src="../js/jquery-ui.js"></script>
        <script src="../js/marbetesGeneracion.js"></script>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <%@include file="../jspf/menuPrincipal.jspf" %>  
            <div class="row">
                <h3 class="col-sm-3">Generar Marbetes</h3>
                <div class="col-sm-2 col-sm-offset-2">
                    <br/>
                    <input id="tipoUsuario" value="<%=tipo%>"  type="hidden">

                </div>
                <br/>
                <br/>
                <hr/>
            </div> 
            <div class=" panel panel-success" >
                <div class="panel-heading">
                    <h4>Ingresar datos</h4>
                </div> 
                <div class="panel-body">
                    <div class="row" >
                        <h4 class="col-lg-1 col-md-1 col-sm-1 text-success" style="font-weight: bold;" >Folio:</h4>
                        <div class="col-lg-2 col-md-2 col-sm-2" >
                            <input class="form-control" type="number" id="folNumber" min="1" maxlength = "10" pattern="^[0-9]+" onpaste="return false;" onDrop="return false;" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
                        </div>
                        <h5 class="col-sm-1 text-success" style="font-weight: bold;">Proyecto</h5>
                        <div class="col-sm-2">
                            <select id="Nombre" name="Nombre" class="form-control">
                                <option value="0">--Seleccione--</option>
                                <%
                                    try {
                                        con.conectar();
                                        try {
                                            ResultSet RsetProy = con.consulta("SELECT * FROM tb_proyectos;");
                                            while (RsetProy.next()) {
                                %>
                                <option value="<%=RsetProy.getString(1)%>"><%=RsetProy.getString(2)%></option>
                                <%
                                            }
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                        con.cierraConexion();
                                    } catch (Exception e) {
                                    }
                                %>
                            </select>
                        </div>
                       
                        <h5 class="col-sm-1 text-success" style="font-weight: bold;">Ruta</h5>      
                        <div class="col-sm-2">
                            <select id="RutaN" name="RutaN" class="form-control">
                                <option value="0">--Seleccione--</option>
                                <option value="1">RUTA ORDINARIA</option>
                               
                                <option value="3">URGENTE</option>
                            </select>
                        </div>
                         <div class="col-lg-1 col-md-1 col-sm-1" >
                            <button class="btn btn-success" type="button" id="searchButton" ><span class="glyphicon glyphicon-search"></span></button>
                        </div>
                    </div>  
                    <hr/>
                    <div class="row">
                        <h4 class="col-lg-3 col-md-2 col-sm-2  text-success" style="font-weight: bold;" >Nombre de Unidad:</h4>
                        <div class="col-lg-9 col-md-9 col-sm-9" >
                            <input class="form-control" type="text" readonly id="uniName">
                            <input class="form-control" type="hidden" id="RF">
                            <input class="form-control" type="hidden" id="Ct">
                            <input class="form-control" type="hidden" id="Proyecto">
                        </div>
                    </div>
                    <hr/>
                    <div class="row" >
                        <h4 class="col-lg-3 col-md-3 col-sm-3  text-success" style="font-weight: bold;" >Número de marbetes:</h4>
                        <div class="col-lg-2 col-md-2 col-sm-2" >
                            <input class="form-control" type="number" id="marbetNumber" min="1" step="1" maxlength = "4" oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
                        </div>
                        <h4 class="col-lg-3 col-md-3 col-sm-3  text-success" style="font-weight: bold; text-align: right" >Ruta:</h4>
                        <div class="col-lg-2 col-md-2 col-sm-2" >
                            <input class="form-control" type="number" id="ruta" maxlength="3" min="1" step="1" maxlength = "4" required oninput="javascript: if (this.value.length > this.maxLength) this.value = this.value.slice(0, this.maxLength);">
                        </div>
                    </div>   
                    <hr/>
                    <div class="row" >
                        <div class="col-lg-4 col-md-4 col-sm-4" >

                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-4" >
                            <button class="btn btn-block btn-success" type="button" id="generarMarbete" >Generar Marbetes</button>
                        </div>
                        <div class="col-lg-4 col-md-4 col-sm-4" >
                        </div>
                    </div>
                </div>
            </div>
        </div> 
      
         <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/bootstrap3-typeahead.js" type="text/javascript"></script>                    
                            
        <script> 
                            
            function filterInteger(evt,input) {
   
                var key = window.Event ? evt.which : evt.keyCode;    
                var chark = String.fromCharCode(key);
                var tempValue = input.value+chark;
                    
                    if((key >= 48 && key <= 57) ) {
                        return filter(tempValue);
                    } else {
                        return key == 8 || key == 13 || key == 0;
                    }
                }
                
            function filter(__val__) {
                var preg = /^[0-9]*$/;
            return preg.test(__val__);
            }
            
            document.getElementById('marbetNumber').addEventListener('keypress', function(evt) {
                if (filterInteger(evt, evt.target) === false) {
            evt.preventDefault();
            }
            });
            
            document.getElementById('folNumber').addEventListener('keypress', function(evt) {
                if (filterInteger(evt, evt.target) === false) {
            evt.preventDefault();
            }
            });
            
            document.getElementById('ruta').addEventListener('keypress', function(evt) {
                if (filterInteger(evt, evt.target) === false) {
            evt.preventDefault();
            }
            });
            
           
            
            const campoNumerico = document.getElementById('marbetNumber');

if (campoNumerico === 0) {
    
    alert('valor no valido')
}
            
        </script>                           
    </body>
</html>
