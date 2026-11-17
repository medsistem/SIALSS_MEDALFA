<%-- 
    Document   : index
    Created on : 17/02/2014, 03:34:46 PM
    Author     : Americo
--%>

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
    String usua = "", Clave = "", DesTipo = "", F_DesPro = "", F_StsPro = "", F_Catipo = "", F_Costo = "";
    int tipo = 0, F_Origen = 0, F_IncMen = 0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    try {
        Clave = request.getParameter("Clave");
    } catch (Exception e) {

    }

    ConectionDB con = new ConectionDB();


%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Estilos CSS -->
        <link href="css/bootstrap.css" rel="stylesheet">
        <link rel="stylesheet" href="css/cupertino/jquery-ui-1.10.3.custom.css" />
        <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap.css">
        <link href="css/sialss-app.css?v=2.0.16" rel="stylesheet">
        <!---->
        <title>SIALSS</title>
    </head>
    <body>
        <%
            int anioCatalogo = java.time.LocalDate.now().getYear();
            String[][] nivelesUnidad = {
                {"1", "UM y C"}, {"2", "CSR"}, {"3", "CSU, CAT, UNEMES y geriátricos"}, {"4", "CEAPS"},
                {"5", "CISAME"}, {"6", "MAT"}, {"7", "MOyCAE"}, {"8", "HM"},
                {"9", "HG"}, {"10", "HMI"}, {"11", "HSV"}, {"12", "HMI Pretelini"},
                {"13", "CM"}, {"14", "HP"}, {"15", "S.U.E.M."}, {"16", "LESP"}};
        %>
        <div class="container sialss-insumo">
            <h1>MEDALFA</h1>
            <h4>SISTEMA INTEGRAL DE ADMINISTRACIÓN Y LOGÍSTICA PARA SERVICIOS DE SALUD (SIALSS)</h4>
            <hr/>
            <div class="panel panel-success">
                <div class="panel-heading">
                    <h3 class="panel-title">Catálogo de Insumo para la Salud</h3>
                </div>
                <div class="panel-body">
                    <form role="form" name="formulario1" id="formulario1" method="post" action="Medicamentos">
                        <div class="sialss-insumo__rejilla">
                            <div class="sialss-insumo__campo">
                                <label for="Clave">Clave *</label>
                                <input type="text" class="form-control" id="Clave" name="Clave" maxlength="60" required>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="ClaveSS">Clave SS *</label>
                                <input type="text" class="form-control" id="ClaveSS" name="ClaveSS" maxlength="250" required>
                            </div>
                            <fieldset class="sialss-insumo__campo">
                                <legend>Estatus</legend>
                                <div class="sialss-insumo__opciones">
                                    <label><input type="radio" name="radiosts" value="A" checked> Activo</label>
                                    <label><input type="radio" name="radiosts" value="S"> Suspendido</label>
                                </div>
                            </fieldset>
                            <div class="sialss-insumo__campo sialss-insumo__campo--ancho">
                                <label for="Descripcion">Descripción *</label>
                                <input type="text" class="form-control" id="Descripcion" maxlength="250" name="Descripcion" required>
                            </div>
                            <div class="sialss-insumo__campo sialss-insumo__campo--ancho">
                                <label for="Presentacion">Presentación *</label>
                                <input type="text" class="form-control" id="Presentacion" maxlength="60" name="Presentacion" required>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="radiorigen">Origen *</label>
                                <select class="form-control" name="radiorigen" id="radiorigen" required>
                                    <option value="">Seleccione el origen</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT * FROM tb_origen;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            out.println(e.getMessage());
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="radiotipo">Tipo de insumo *</label>
                                <select class="form-control" name="radiotipo" id="radiotipo" required>
                                    <option value="">Seleccione el tipo</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT * FROM tb_tipmed;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(1)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            out.println(e.getMessage());
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="CatalogoNim">Catálogo (año) *</label>
                                <%-- Desde 2018 hasta el año en curso: cada año nuevo aparece solo y viene elegido. --%>
                                <select name="CatalogoNim" id="CatalogoNim" class="form-control" required>
                                    <% for (int anio = anioCatalogo; anio >= 2018; anio--) { %>
                                    <option value="<%=anio%>"<%= anio == anioCatalogo ? " selected" : "" %>><%=anio%></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>

                        <fieldset class="sialss-insumo__grupo">
                            <legend>Nivel de unidad</legend>
                            <div class="sialss-insumo__acciones">
                                <button type="button" class="btn btn-default btn-sm" data-niveles="1">Marcar todos</button>
                                <button type="button" class="btn btn-default btn-sm" data-niveles="0">Quitar todos</button>
                            </div>
                            <div class="sialss-insumo__casillas">
                                <% for (String[] nivel : nivelesUnidad) { %>
                                <label for="cat<%=nivel[0]%>"><input type="checkbox" name="cat<%=nivel[0]%>" id="cat<%=nivel[0]%>" value="<%=nivel[0]%>" checked> (<%=nivel[0]%>) <%=nivel[1]%></label>
                                <% } %>
                            </div>
                        </fieldset>

                        <div class="sialss-insumo__rejilla">
                            <div class="sialss-insumo__campo">
                                <label for="Costo">Precio *</label>
                                <input type="text" class="form-control" id="Costo" name="Costo" inputmode="decimal" value="<%=F_Costo%>" required>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="origennim">Origen NIM *</label>
                                <select name="origennim" id="origennim" class="form-control" required>
                                    <option value="">Seleccione</option>
                                    <option value="LP">LP</option>
                                    <option value="CC">CC</option>
                                    <option value="CC/LP">CC/LP</option>
                                </select>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="GrupoNim">Grupo terapéutico *</label>
                                <select class="form-control" name="GrupoNim" id="GrupoNim" required>
                                    <option value="">Seleccione el grupo</option>
                                    <%
                                        try {
                                            con.conectar();
                                            ResultSet rset = con.consulta("SELECT * FROM tb_gruptera;");
                                            while (rset.next()) {
                                    %>
                                    <option value="<%=rset.getString(2)%>"><%=rset.getString(2)%></option>
                                    <%
                                            }
                                            con.cierraConexion();
                                        } catch (Exception e) {
                                            out.println(e.getMessage());
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="sialss-insumo__campo">
                                <label for="CantRecibir">Cantidad máxima a recibir *</label>
                                <input type="number" min="0" class="form-control" id="CantRecibir" name="CantRecibir" required>
                            </div>
                        </div>

                        <% java.util.Set<String> proyectosMarcados = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER); proyectosMarcados.add("F_ProIsem"); %>
                        <%@include file="jspf/proyectosInsumo.jspf" %>

                        <div class="sialss-insumo__botones">
                            <button class="btn btn-success" type="submit" name="accion" value="Agregar">Agregar</button>
                            <a href="medicamento.jsp" class="btn btn-warning">Regresar</a>
                        </div>
                    </form>
                    <p class="sialss-insumo__nota">Los campos marcados con * son obligatorios.</p>
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
            // Nivel de unidad: marcar o quitar todos.
            document.querySelectorAll('[data-niveles]').forEach(function (b) {
                b.addEventListener('click', function () {
                    var valor = b.getAttribute('data-niveles') === '1';
                    document.querySelectorAll('.sialss-insumo__casillas input[type=checkbox]').forEach(function (c) { c.checked = valor; });
                });
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


            function valida_alta() {
                var missinginfo = "";
                var radiotipo = ($("#radiotipo1")).val();
                alert(radiotipo);
                if ($("#Clave").val() == "") {
                    missinginfo += "\n El campo Clave no debe de estar vacío";
                }
                if ($("#Descripcion").val() == "") {
                    missinginfo += "\n El campo Descripcion no debe de estar vacío";
                }
                if ($("#radiosts").val() == "") {
                    missinginfo += "\n El campo Descripcion no debe de estar vacío";
                }
                if ($("#Costo").val() == "") {
                    missinginfo += "\n El campo Costo no debe de estar vacío";
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
    </body>
</html>



