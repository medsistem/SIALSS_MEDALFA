<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="conn.*" %>
<!DOCTYPE html>
<%
    /**
     * Para verificar las compras que están en proceso de ingreso
     */
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "", pedido = "", IdUsu = "", Usuario = "",Area="";

    //Verifica que este logeado el usuario
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
        IdUsu = (String) sesion.getAttribute("IdUsu");
        Usuario = (String) sesion.getAttribute("Usuario");
        Area = (String) sesion.getAttribute("Area");
    } else {
        response.sendRedirect("index.jsp");
    }

    ConectionDB con = new ConectionDB();
    String F_Nombre="",F_Apellido="",F_ApellidoM="",F_Correo="",F_Id="",F_Usu="";
    try{
        F_Nombre = (String) sesion.getAttribute("F_Nombre");
        F_Apellido = (String) sesion.getAttribute("F_Apellido");
        F_ApellidoM = (String) sesion.getAttribute("F_ApellidoM");
        F_Correo = (String) sesion.getAttribute("F_Correo");
        F_Id = (String) sesion.getAttribute("F_Id");
        F_Usu = (String) sesion.getAttribute("F_Usu");
        
    }catch(Exception e){
        System.out.println(e.getMessage());
    }
    if(F_Nombre == null){ F_Nombre = ""; }
    if(F_Apellido == null){ F_Apellido = ""; }
    if(F_ApellidoM == null){ F_ApellidoM = ""; }
    if(F_Correo == null){ F_Correo = ""; }
    if(F_Id == null){ F_Id = ""; }
    if(F_Usu == null){ F_Usu = ""; }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/datepicker3.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" media="all" href="ValidaPass/style.css" />
        <link href="css/select2.css" rel="stylesheet" type="text/css"/>
        <!---->
        <script src="http://code.jquery.com/jquery-1.7.min.js"></script>
        <script src="ValidaPass/script.js"></script>
        
        <title>SIALSS</title>
    </head>
    <body>
        <div class="container">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>

            <%@include file="jspf/menuPrincipal.jspf"%>
            <div class="row">
                <div class="col-sm-12">
                    <h2>Nuevo Usuario</h2>
                </div>
            </div>
            <form action="ModificacionPass" method="post">
                <br/>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Nombre: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="Nombre" id="Nombre" placeholder="Ingrese Nombre" required="" />
                    </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Apellido Paterno: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="ApellidoP" id="ApellidoP" placeholder="Ingrese Apellido Paterno" required="" />
                    </div>                    
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Apellido Materno: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="ApellidoM" id="ApellidoM" placeholder="Ingrese Apellido Materno" required="" />
                    </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Ingrese Correo: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="Correo" id="Correo" placeholder="Ingrese Correo" required="" />
                    </div>
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Ingrese Usuario: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="text" class="form-control" name="Usuario" id="Usuario" placeholder="Ingrese Usuario" required="" />
                    </div>
                </div>    
                <div class="row">
                    <label for="pswd" class="col-sm-2">
                        <h4>Ingrese Contraseña: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="password" class="form-control" min="6" maxlength="8" name="pswd" id="pswd" placeholder="Ingrese Contraseña Nueva" autofocus="autofocus" required="" />
                    </div>                    
                </div>
                <div class="row">
                    <label class="col-sm-2">
                        <h4>Repita Contraseña: </h4>
                    </label>
                    <div class="col-sm-4">
                        <input type="password" class="form-control" min="6" maxlength="8" name="pswd2" id="pswd2" placeholder="Repita Contraseña Nueva" required="" />
                    </div>
                </div>
                 <div class="row">
                    <label class="col-sm-2">
                        <h4>Tipo Usuario: </h4>
                    </label>
                    <div class="col-sm-4">

                        <select class="form-control" name="TipUsuario" id="TipUsu" required="true" autofocus="autofocus">
                            <option value="Tipo usuario" >Tipo Usuario</option>
                            <%                                try {
                                    con.conectar();
                                    ResultSet rset = null;

                                    rset = con.consulta("SELECT tu.F_IdTipo, tu.F_DesTipo FROM tb_tipusuario tu WHERE tu.F_IdTipo not in  (1) ORDER BY tu.F_DesTipo;");

                                    while (rset.next()) {
                            %>
                            <option value="<%=rset.getString(1)%>" ><%=rset.getString(2)%></option>
                            <%
                                    }
                                } catch (Exception e) {
                                    Logger.getLogger("NuevoUsuario.jsp").log(Level.SEVERE, null, e);
                                } finally {
                                    try {
                                        con.cierraConexion();
                                    } catch (Exception ex) {
                                        Logger.getLogger("NuevoUsuario.jsp").log(Level.SEVERE, null, ex);
                                    }
                                }
                            %>

                        </select>
                    </div>
                </div>

                <div class="row">
                    
                    <label class="col-sm-2"><H4>Proyecto: </H4></label>
                    <div class="col-sm-4">
                        <input type="text" name="ListProyect" class="form-control" id="ListProyect" readonly="true" placeholder="Proyecto" required="true" autofocus="autofocus"/>
                    </div>
                    
                    <select name="SelectProyect" id="SelectProyect" class="col-sm-4">
                        <option>--Seleccione Proyecto--</option>
                        <%
                            try {
                                con.conectar();
                                ResultSet Proyect = con.consulta("SELECT P.F_Id,P.F_DesProy FROM tb_proyectos P GROUP BY P.F_Id;");
                                while (Proyect.next()) {
                        %>
                        <option value="<%=Proyect.getString(1)%>"><%=Proyect.getString(2)%></option>
                        <%
                                }
                                con.cierraConexion();
                            } catch (Exception e) {
                            }
                        %>
                    </select>
                   
                </div>  
                    <div class="row">
                        <input class=" btn btn-block btn-info" type="reset" name="limpiar" id="limpiar" value="Limpiar"  onclick="ListProyect" />
                    </div><br/>
                    <div class="row">
                        <input class=" btn btn-block btn-info" type="submit" name="action" id="action" value="AltaUsuario" onclick="return valida_alta();" />
                    </div>
            </form>
                    <div style="display: none;" class="text-center" id="Loader">
                        <img src="imagenes/ajax-loader-1.gif" height="150" />
                    </div>
            <div id="pswd_info">
                <h4>Requerimiento del password:</h4>
                <ul>
                    <!--li id="letter" class="invalid">Al Menos <strong>Una Letra</strong></li-->
                    <li id="capital" class="invalid"><strong>Una Letra Mayuscula</strong></li>
                    <li id="number" class="invalid"><strong>Un Número</strong></li>
                    <li id="length" class="invalid"><strong> 8 Caracteres</strong></li>
                    <li id="igual" class="invalid"><strong> Contraseña Igual</strong></li>
                </ul>
            </div>
        </div>        
        <!-- 
        ================================================== -->
        <!-- Se coloca al final del documento para que cargue mas rapido -->
        <!-- Se debe de seguir ese orden al momento de llamar los JS -->
        <script src="js/jquery-1.9.1.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/bootstrap-datepicker.js"></script>    
        <script src="js/select2.js" type="text/javascript"></script>
        <script>
            $("#usuario").select2();
            function valida_alta() {                
                                
                 var missinginfo = "";
                  if ($("#Nombre").val() == "") {
                            missinginfo += "\n El campo Nombre no debe de estar vacío";
                        }
                        if ($("#ApellidoP").val() == "") {
                            missinginfo += "\n El campo Apellido Paterno no debe de estar vacío";
                        }
                        if ($("#ApellidoM").val() == "") {
                            missinginfo += "\n El campo Apellido Materno no debe de estar vacío";
                        }
                        if ($("#Correo").val() == "") {
                            missinginfo += "\n El campo Correo no debe de estar vacío";
                        }
                        if ($("#Usuario").val() == "") {
                            missinginfo += "\n El campo Usuario no debe de estar vacío";
                        }
                        if ($("#pswd").val() == "") {
                            missinginfo += "\n El campo Contraseña no debe de estar vacío";
                        }
                        if ($("#pswd2").val() == "") {
                            missinginfo += "\n El campo Repetir Contraseña no debe de estar vacío";
                        }
                        
                            if ($("#TipUsuario").val() == "") {
                                missinginfo += "\n El campo Tipo de Usuario no debe de estar vacío";
                            }
                            
                            if ($("#ListProyect").val() == "") {
                                missinginfo += "\n El campo Proyecto no debe de estar vacío";
                            }
                             
                            if ($("#SelectProyect").val() == "") {
                                missinginfo += "\n seleccione Proyecto no debe de estar vacío";
                            }
                        if (missinginfo != "") {
                            missinginfo = "\n TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ACTUALIZAR INFORMACIÓN:\n" + missinginfo + "\n\n ¡INGRESA LOS DATOS FALTANTES Y TRATA OTRA VEZ!\n";
                            alert(missinginfo);

                            return false;
                        } else {                            
                            document.getElementById('Loader').style.display = 'block';
                            return true;
                        }
                
            }
            
        </script>
           <script>
            $(document).ready(function () {
                $('#SelectProyect').change(function () {
                    $('#proyecto').prop('checked', true);
                    var Lista = $('#ListProyect').val();
                    var valor = $('#SelectProyect').val();
                    if (Lista != "") {
                        $('#ListProyect').val(Lista + "," + valor + "");
                    } else {
                        $('#ListProyect').val("" + valor + "");
                    }
                });
            });
        </script>

        <script>
            function soloLetras(e){
                key = e.keyCode || e.which;
                tecla = String.fromCharCode(key).toLowerCase();
                letras = " áéíóúabcdefghijklmnñopqrstuvwxyz";
                especiales = "8-37-39-46";
                tecla_especial = false
                    for(var i in especiales){
                    if(key == especiales[i])
                    {
                        tecla_especial = true;
                        break;
                    }
                }

                if(letras.indexOf(tecla)== -1 && !tecla_especial){
                    return false;
                }
            }
        </script>

        <script > 
        /**
         * Función para validar una dirección de correo
         * Tiene que recibir el identificador del formulario 
         **/

        function validateMail(idMail)
        {
                //Creamos un objeto 
                object=document.getElementById(idMail);
                valueForm=object.value;
 
                // Patron para el correo
                var patron=/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,4})+$/;
                if(valueForm.search(patron) == 0)
                {
                        //Mail correcto
                        object.style.color="#000";
                        return;
                }
                //Mail incorrecto
                object.style.color="#f00";
        }

        </script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
