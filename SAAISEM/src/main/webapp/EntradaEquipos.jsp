<%-- Entrada de equipos: cada equipo es un lote de una pieza (tb_lote), su movimiento 49 ENTRADA DE EQUIPOS
     (tb_movinv) y sus datos de equipo (tb_equipo). Servlet /EntradaEquipos (mx.medalfa.sialss.inventario). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = mx.medalfa.sialss.seguridad.PoliticaSesion.acceso(sesion);
    if (menuAcceso == null || sesion.getAttribute("nombre") == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    String tipo = (String) sesion.getAttribute("Tipo");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Entrada de equipos</title>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-mr sialss-captura">
            <h1>Entrada de equipos</h1>
            <p class="sialss-secundario">Cada equipo entra como un lote de una pieza con su número de serie. Los campos con
                <span class="sialss-captura__requerido" aria-hidden="true">*</span> son obligatorios.</p>

            <form id="eq-forma" novalidate>
                <input type="hidden" name="accion" value="guardar">
                <input type="hidden" name="clave" id="eq-clave">

                <fieldset>
                    <legend>Equipo</legend>
                    <div class="sialss-filtros">
                        <div class="sialss-filtros__campo sialss-filtros__campo--ancho">
                            <label for="eq-buscar">Buscar por clave o descripción <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <input type="search" class="form-control" id="eq-buscar" autocomplete="off" placeholder="Ejemplo: EQ0000032 o máquina de anestesia"
                                   aria-describedby="eq-elegido" aria-controls="eq-resultados">
                            <ul class="sialss-captura__resultados" id="eq-resultados" aria-label="Insumos encontrados"></ul>
                            <p class="sialss-captura__elegido" id="eq-elegido" aria-live="polite"></p>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-marca">Marca <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <select class="form-control" id="eq-marca" name="marca"><option value="">Cargando…</option></select>
                            <input type="text" class="form-control" id="eq-marca-nueva" name="marcaNueva" maxlength="80" placeholder="Nombre de la marca nueva" hidden>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-modelo">Modelo</label>
                            <select class="form-control" id="eq-modelo" name="modelo"><option value="">Cargando…</option></select>
                            <input type="text" class="form-control" id="eq-modelo-nuevo" name="modeloNuevo" maxlength="80" placeholder="Nombre del modelo nuevo" hidden>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-categoria">Categoría</label>
                            <input type="text" class="form-control" id="eq-categoria" name="categoria" maxlength="80" list="eq-categorias" placeholder="Ejemplo: Anestesia, Monitoreo">
                            <datalist id="eq-categorias"></datalist>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-condicion">Condición</label>
                            <select class="form-control" id="eq-condicion" name="condicion"><option value="">-- Seleccione --</option></select>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-serie">No. de serie o lote</label>
                            <input type="text" class="form-control" id="eq-serie" name="serie" maxlength="150" autocomplete="off"
                                   aria-describedby="eq-serie-ayuda">
                            <p class="sialss-filtros__ayuda" id="eq-serie-ayuda">Si no tiene, use "Generar CB": el código queda también como serie.</p>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-cb">Código de barras</label>
                            <div class="sialss-filtros__grupo">
                                <input type="text" class="form-control" id="eq-cb" name="cb" maxlength="255" autocomplete="off">
                                <button type="button" class="sialss-boton" id="eq-generar-cb">Generar CB</button>
                            </div>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-costo">Costo</label>
                            <input type="text" class="form-control" id="eq-costo" name="costo" inputmode="decimal" placeholder="0.00">
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-tarima">Tarima</label>
                            <input type="text" class="form-control" id="eq-tarima" name="tarima" maxlength="100">
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-compra">Fecha de compra</label>
                            <input type="date" class="form-control" id="eq-compra" name="fechaCompra">
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-garantia">Garantía hasta</label>
                            <input type="date" class="form-control" id="eq-garantia" name="garantiaHasta">
                        </div>
                        <div class="sialss-filtros__campo sialss-filtros__campo--ancho">
                            <label for="eq-accesorios">Accesorios</label>
                            <textarea class="form-control" id="eq-accesorios" name="accesorios" maxlength="500" rows="2" placeholder="Ejemplo: cable de corriente, sensor, manual"></textarea>
                        </div>
                        <div class="sialss-filtros__campo sialss-filtros__campo--ancho">
                            <label for="eq-notas">Notas</label>
                            <textarea class="form-control" id="eq-notas" name="notas" maxlength="1000" rows="2"></textarea>
                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <legend>Entrada</legend>
                    <div class="sialss-filtros">
                        <div class="sialss-filtros__campo">
                            <label for="eq-proyecto">Proyecto <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <select class="form-control" id="eq-proyecto" name="proyecto" required><option value="">Cargando…</option></select>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-origen">Origen <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <select class="form-control" id="eq-origen" name="origen" required><option value="">Cargando…</option></select>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-proveedor">Proveedor <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <select class="form-control" id="eq-proveedor" name="proveedor" required><option value="">Cargando…</option></select>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-ubicacion">Ubicación <span class="sialss-captura__requerido" aria-hidden="true">*</span></label>
                            <input type="text" class="form-control" id="eq-ubicacion" name="ubicacion" maxlength="40" value="NUEVA" required>
                        </div>
                        <div class="sialss-filtros__campo">
                            <label for="eq-remision">Remisión o factura</label>
                            <input type="text" class="form-control" id="eq-remision" name="remision" maxlength="40">
                        </div>
                    </div>
                </fieldset>

                <div class="sialss-captura__acciones">
                    <button type="submit" class="sialss-boton sialss-boton--primario" id="eq-guardar">Guardar entrada</button>
                    <button type="button" class="sialss-boton" id="eq-limpiar">Limpiar equipo</button>
                </div>
                <p class="sialss-mr__estado" id="eq-estado" role="status" aria-live="polite"></p>
            </form>

            <h2 class="sialss-etq__subtitulo">Últimas entradas de equipos</h2>
            <table class="sialss-mr__tabla">
                <caption class="sr-only">Últimas 20 entradas de equipos</caption>
                <thead><tr><th scope="col">Fecha</th><th scope="col">Clave</th><th scope="col">Equipo</th><th scope="col">Serie</th>
                        <th scope="col">CB</th><th scope="col">Marca</th><th scope="col">Modelo</th><th scope="col">Categoría</th>
                        <th scope="col">Condición</th><th scope="col">Costo</th><th scope="col">Ubicación</th><th scope="col">Folio</th></tr></thead>
                <tbody id="eq-recientes"><tr><td colspan="12" class="sialss-mr__vacio">Cargando…</td></tr></tbody>
            </table>
        </div>
        <script src="<%=request.getContextPath()%>/js/sialss-entrada-equipos.js?v=1" defer data-ctx="<%=request.getContextPath()%>"></script>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
