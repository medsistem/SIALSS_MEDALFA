<%--
    Document   : GeneraMarbeteManual
    Corregido  : el reporte en disco se llama MarbeteManual.jasper, sin guion
                 bajo. La ruta decía Marbete_Manual.jasper y por eso tronaba
                 con FileNotFoundException.

    Otros cambios:
      - una sola conexión en vez de tres (dos se quedaban abiertas por marbete);
      - sendRedirect con return y con la ruta relativa correcta;
      - parámetros validados antes de usarlos;
      - si algo falla, el usuario ve el motivo en pantalla en vez de una
        página en blanco.
--%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="net.sf.jasperreports.engine.JasperRunManager"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.File"%>
<%@page import="java.sql.Connection"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    private static final Logger LOG = Logger.getLogger("GeneraMarbeteManual.jsp");

    /* El reporte en web/reportes/ se llama MarbeteManual.jasper. */
    private static final String RUTA_REPORTE = "/reportes/MarbeteManual.jasper";
%>
<%
    HttpSession sesion = request.getSession();

    if (sesion.getAttribute("nombre") == null) {
        // El return es indispensable: sendRedirect solo marca la respuesta,
        // no corta la ejecución. Y la ruta lleva ../ porque este JSP vive en
        // /Marbetes/, no en la raíz.
        response.sendRedirect("../index.jsp");
        return;
    }

    String clave    = request.getParameter("clave");
    String lote     = request.getParameter("lote");
    String proyecto = request.getParameter("Proyecto");

    if (clave == null || clave.trim().isEmpty()
            || lote == null || lote.trim().isEmpty()
            || proyecto == null || proyecto.trim().isEmpty()) {
        responderError(response, "Faltan datos para generar el marbete. "
                               + "Regresa a la pantalla anterior y elige producto, lote y proyecto.");
        return;
    }

    // Se valida que sea numérico, pero se manda como texto: el reporte declara
    // el parámetro Proyecto como java.lang.String.
    try {
        Integer.parseInt(proyecto.trim());
    } catch (NumberFormatException e) {
        LOG.log(Level.WARNING, "Proyecto no numérico recibido: {0}", proyecto);
        responderError(response, "El proyecto recibido no es válido.");
        return;
    }

    File archivoReporte = new File(application.getRealPath(RUTA_REPORTE));
    if (!archivoReporte.isFile()) {
        // Decirlo aquí ahorra ir a buscarlo al log del servidor.
        LOG.log(Level.SEVERE, "No se encontró el reporte: {0}", archivoReporte.getAbsolutePath());
        responderError(response, "No se encontró el archivo del reporte en el servidor: "
                               + RUTA_REPORTE + ". Avisa a sistemas.");
        return;
    }

    ConectionDB con = new ConectionDB();
    Connection conexion = null;

    try {
        /*
         * Una sola conexión.
         *
         * Antes había tres: conectar() abre una y la guarda en el campo conn,
         * conectar() otra vez la sobrescribe, y getConn() abre una tercera y
         * vuelve a sobrescribir. cierraConexion() solo cierra la última, así
         * que cada marbete impreso dejaba dos conexiones abiertas.
         *
         * getConn() ya abre y guarda la conexión, así que basta con él.
         */
        conexion = con.getConn();
        if (conexion == null) {
            responderError(response, "No se pudo conectar a la base de datos.");
            return;
        }

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("Clave",    clave.trim());
        parametros.put("Lote",     lote.trim());
        parametros.put("Proyecto", proyecto.trim());

        byte[] pdf = JasperRunManager.runReportToPdf(
                archivoReporte.getPath(), parametros, conexion);

        response.setContentType("application/pdf");
        response.setContentLength(pdf.length);
        response.setHeader("Content-Disposition",
                "inline; filename=\"marbete-" + clave.trim() + ".pdf\"");

        ServletOutputStream salida = response.getOutputStream();
        salida.write(pdf, 0, pdf.length);
        salida.flush();
        salida.close();

    } catch (Exception e) {
        LOG.log(Level.SEVERE, "Error al generar el marbete manual. clave=" + clave
                            + " lote=" + lote + " proyecto=" + proyecto, e);
        responderError(response, "No se pudo generar el marbete. "
                               + "El detalle quedó en la bitácora del servidor.");
    } finally {
        if (conexion != null) {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "No se pudo cerrar la conexión", ex);
            }
        }
    }
%>
<%!
    /**
     * Muestra el motivo en pantalla. La versión anterior atrapaba la excepción
     * y no escribía nada: el usuario veía una ventana en blanco y el único
     * rastro quedaba en el log del servidor.
     */
    private void responderError(jakarta.servlet.http.HttpServletResponse response, String mensaje)
            throws java.io.IOException {
        if (!response.isCommitted()) {
            response.reset();
        }
        response.setContentType("text/html; charset=UTF-8");
        java.io.PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html lang=\"es-MX\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Marbete</title></head><body style=\"font-family:sans-serif;padding:2rem\">");
        out.println("<h3>No se generó el marbete</h3>");
        out.println("<p>" + escapar(mensaje) + "</p>");
        out.println("</body></html>");
        out.flush();
    }

    private String escapar(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
%>
