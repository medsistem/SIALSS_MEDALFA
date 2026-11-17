<%-- Manual de usuario filtrado por el perfil de la sesión: solo los módulos y opciones de su menú.
     Contenido: SAAISEM/Manuales (copiado a WEB-INF/manuales). PDF: servlet /ManualUsuario?formato=pdf (iText 2.1.7). --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sesion = request.getSession();
    mx.medalfa.sialss.manual.ManualUsuarioServlet.Solicitud sol =
            mx.medalfa.sialss.manual.ManualUsuarioServlet.solicitud(sesion, request.getParameter("perfil"));
    if (sol == null) {
        response.sendRedirect(request.getContextPath() + "/indexalmacen.jsp");
        return;
    }
    mx.medalfa.sialss.seguridad.Acceso menuAcceso = sol.acceso();
    String tipo = (String) sesion.getAttribute("Tipo");
    mx.medalfa.sialss.manual.ManualUsuario.Manual manual =
            mx.medalfa.sialss.manual.ManualUsuario.para(application, sol.acceso(), sol.perfil(), sol.idUsuario());
    boolean administra = mx.medalfa.sialss.seguridad.PoliticaSesion.esAdministrador(sesion);
    String perfilPropio = mx.medalfa.sialss.seguridad.PoliticaSesion.perfil(sesion);
    String ligaPdf = request.getContextPath() + "/ManualUsuario?formato=pdf" + (sol.otroPerfil() ? "&perfil=" + sol.perfil() : "");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="css/bootstrap.css" rel="stylesheet">
        <title>SIALSS · Manual de usuario</title>
        <style>
            .sialss-manual { max-width: 960px; padding-bottom: 48px; }
            .sialss-manual__cabeza { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; justify-content: space-between; margin-bottom: 16px; }
            .sialss-manual__cabeza h1 { margin: 0 0 4px; }
            .sialss-manual__datos { color: #555; margin: 0; }
            .sialss-manual__acciones { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
            .sialss-manual__acciones select { width: auto; display: inline-block; }
            .sialss-manual__indice { background: #f5f7fa; border: 1px solid #dde3ea; border-radius: 4px; padding: 12px 20px; margin-bottom: 24px; }
            .sialss-manual__indice h2 { font-size: 18px; margin-top: 4px; }
            .sialss-manual__indice ol ol { margin-bottom: 6px; }
            .sialss-manual__capitulo { border-top: 2px solid #1f4e79; margin-top: 32px; padding-top: 4px; }
            .sialss-manual__capitulo > h2 { color: #1f4e79; }
            .sialss-manual__opcion h3 { color: #1f4e79; font-size: 19px; margin-top: 26px; }
            .sialss-manual__opcion h4 { font-size: 15px; }
            .sialss-manual__nota { background: #fff6d9; border-left: 4px solid #e0a800; padding: 8px 12px; }
            .sialss-manual h2[id], .sialss-manual h3[id] { scroll-margin-top: 70px; }
            @media print { .sialss-menu, .sialss-barra, .sialss-manual__acciones, .sialss-manual__indice { display: none !important; } }
        </style>
    </head>
    <body>
        <%@include file="jspf/sialssBarra.jspf"%>
        <%@include file="jspf/menuSialss.jspf"%>
        <div class="container sialss-manual">
            <div class="sialss-manual__cabeza">
                <div>
                    <h1>Manual de usuario</h1>
                    <p class="sialss-manual__datos">
                        Acceso <strong><%=mx.medalfa.sialss.manual.ManualUsuario.esc(manual.acceso())%></strong>
                        · Perfil <strong><%=mx.medalfa.sialss.manual.ManualUsuario.esc(manual.perfil())%><%=manual.nombrePerfil().isBlank() ? "" : " · " + mx.medalfa.sialss.manual.ManualUsuario.esc(manual.nombrePerfil())%></strong>
                        <%=manual.version().isBlank() ? "" : "· Versión " + mx.medalfa.sialss.manual.ManualUsuario.esc(manual.version())%>
                    </p>
                    <p class="sialss-manual__datos">Contiene solo los módulos y opciones de <%=sol.otroPerfil() ? "este perfil" : "tu menú"%>.</p>
                </div>
                <div class="sialss-manual__acciones">
                    <% if (administra) { %>
                    <form method="get" action="ManualUsuario.jsp">
                        <label for="manual-perfil" class="sr-only">Perfil</label>
                        <select id="manual-perfil" name="perfil" class="form-control" onchange="this.form.submit()">
                            <% for (String p : mx.medalfa.sialss.manual.ManualUsuario.perfiles(sol.acceso())) { %>
                            <option value="<%=p%>" <%=p.equals(sol.perfil()) ? "selected" : ""%>>Perfil <%=p%><%=p.equals(perfilPropio) ? " (el mío)" : ""%></option>
                            <% } %>
                        </select>
                    </form>
                    <% } %>
                    <a class="btn btn-primary" href="<%=ligaPdf%>" target="_blank" rel="noopener">
                        <span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Descargar PDF
                    </a>
                </div>
            </div>
            <% if (manual.capitulos().isEmpty()) { %>
            <p class="sialss-manual__nota">Este perfil no tiene opciones en el menú, por eso el manual no tiene módulos.</p>
            <% } %>
            <%=mx.medalfa.sialss.manual.ManualUsuario.html(manual)%>
        </div>
        <%@include file="jspf/piePagina.jspf" %>
    </body>
</html>
