package mx.medalfa.sialss.etiquetas;

import com.google.gson.Gson;
import com.lowagie.text.DocumentException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Papel;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Tipo;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Ubicacion;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Etiquetas de ubicación (EtiquetasUbicacion.jsp):
 * GET {@code accion=secciones} y {@code accion=buscar} (JSON).
 * POST {@code accion=pdf} genera el PDF, lo guarda en la sesión y redirige (303) a
 * GET {@code accion=pdf&id=...}, que lo entrega. Así la pestaña del PDF se puede recargar
 * y el visor del navegador lo puede descargar: ambos repiten la petición como GET.
 */
@WebServlet("/EtiquetasUbicacion")
public class EtiquetasUbicacionServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(EtiquetasUbicacionServlet.class.getName());
    private static final Gson GSON = new Gson();
    /** Tope por PDF (una hoja carta lleva 24 de poste; 3 000 son 125 hojas). */
    public static final int MAXIMO = 3000;
    private static final int MAXIMO_LISTA = 2000;
    /** PDF generados que se conservan en la sesión (los más recientes). */
    private static final int PDF_EN_SESION = 3;
    private static final String ATRIBUTO_PDF = "sialss.etiquetasUbicacion.pdf";

    /** PDF generado y guardado en la sesión hasta que se descarga o lo desplaza uno nuevo. */
    private record PdfGenerado(String nombre, byte[] bytes) implements Serializable {
    }

    private static boolean conSesion(HttpSession s) {
        return s != null && PoliticaSesion.acceso(s) != null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!conSesion(req.getSession(false))) {
            json(res, 401, Map.of("error", "Su sesión terminó. Vuelva a iniciar sesión."));
            return;
        }
        try {
            switch (String.valueOf(req.getParameter("accion"))) {
                case "secciones" -> json(res, 200, Map.of("secciones", UbicacionDao.secciones()));
                case "buscar" -> {
                    List<UbicacionDao.Fila2> filas = UbicacionDao.buscar(req.getParameter("seccion"), req.getParameter("texto"), MAXIMO_LISTA + 1);
                    boolean mas = filas.size() > MAXIMO_LISTA;
                    json(res, 200, Map.of("ubicaciones", mas ? filas.subList(0, MAXIMO_LISTA) : filas, "hayMas", mas,
                            "maximoLista", MAXIMO_LISTA));
                }
                case "pdf" -> entregarPdf(req, res);
                default -> json(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Etiquetas de ubicación", e);
            json(res, 500, Map.of("error", "No se pudieron leer las ubicaciones."));
        }
    }

    /**
     * Genera el PDF. Con {@code respuesta=json} (lo usa sialss-etiquetas-ubicacion.js) contesta
     * {@code {"url": ...}} y los errores en JSON; sin él, redirige (303) a la dirección del PDF.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        boolean comoJson = "json".equals(req.getParameter("respuesta"));
        if (!conSesion(req.getSession(false))) {
            error(res, comoJson, HttpServletResponse.SC_UNAUTHORIZED, "Su sesión terminó. Vuelva a iniciar sesión.");
            return;
        }
        Tipo tipo;
        Papel papel;
        try {
            tipo = Tipo.valueOf(String.valueOf(req.getParameter("tipo")));
            papel = req.getParameter("papel") == null ? Papel.CARTA : Papel.valueOf(req.getParameter("papel"));
        } catch (IllegalArgumentException e) {
            error(res, comoJson, HttpServletResponse.SC_BAD_REQUEST, "Tipo de etiqueta o papel no válido.");
            return;
        }
        Set<String> claves = new LinkedHashSet<>();
        String texto = req.getParameter("claves");
        if (texto != null) {
            for (String c : texto.split("[,\\r\\n]+")) {
                if (!c.isBlank()) {
                    claves.add(c.trim());
                }
            }
        }
        if (claves.isEmpty() || claves.size() > MAXIMO) {
            error(res, comoJson, HttpServletResponse.SC_BAD_REQUEST, claves.isEmpty()
                    ? "Seleccione al menos una ubicación." : "Son más de " + MAXIMO + " etiquetas: divídalas en varios PDF.");
            return;
        }
        try {
            List<Ubicacion> ubicaciones = UbicacionDao.porClaves(claves);
            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            EtiquetaUbicacionPdf.generar(ubicaciones, tipo, papel, pdf);
            String nombre = "etiquetas_ubicacion_" + tipo.name().toLowerCase()
                    + (tipo == Tipo.POSTE ? "_" + papel.name().toLowerCase() : "") + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            String id = guardarPdf(req.getSession(false), new PdfGenerado(nombre, pdf.toByteArray()));
            String url = req.getContextPath() + "/EtiquetasUbicacion?accion=pdf&id=" + id;
            if (comoJson) {
                json(res, 200, Map.of("url", url, "etiquetas", ubicaciones.size()));
            } else {
                res.setStatus(HttpServletResponse.SC_SEE_OTHER);
                res.setHeader("Location", url);
            }
        } catch (SQLException | DocumentException e) {
            LOG.log(Level.SEVERE, "PDF de etiquetas de ubicación", e);
            error(res, comoJson, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.");
        }
    }

    private static void error(HttpServletResponse res, boolean comoJson, int estado, String mensaje) throws IOException {
        if (comoJson) {
            json(res, estado, Map.of("error", mensaje));
        } else {
            res.sendError(estado, mensaje);
        }
    }

    @SuppressWarnings("unchecked")
    private static String guardarPdf(HttpSession sesion, PdfGenerado pdf) {
        String id = UUID.randomUUID().toString().replace("-", "");
        synchronized (sesion) {
            LinkedHashMap<String, PdfGenerado> guardados = (LinkedHashMap<String, PdfGenerado>) sesion.getAttribute(ATRIBUTO_PDF);
            if (guardados == null) {
                guardados = new LinkedHashMap<>();
            }
            guardados.put(id, pdf);
            while (guardados.size() > PDF_EN_SESION) {
                guardados.remove(guardados.keySet().iterator().next());
            }
            sesion.setAttribute(ATRIBUTO_PDF, guardados);
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    private static void entregarPdf(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession sesion = req.getSession(false);
        PdfGenerado pdf = null;
        synchronized (sesion) {
            Map<String, PdfGenerado> guardados = (Map<String, PdfGenerado>) sesion.getAttribute(ATRIBUTO_PDF);
            if (guardados != null) {
                pdf = guardados.get(String.valueOf(req.getParameter("id")));
            }
        }
        if (pdf == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.setContentType("text/html;charset=UTF-8");
            res.getWriter().write("<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\"><title>PDF no disponible</title></head>"
                    + "<body style=\"font-family:Segoe UI,Arial,sans-serif;padding:24px\"><h1 style=\"font-size:20px\">El PDF ya no está disponible</h1>"
                    + "<p>Solo se conservan los últimos " + PDF_EN_SESION + " PDF de la sesión. Vuelva a generarlo desde "
                    + "<a href=\"" + req.getContextPath() + "/EtiquetasUbicacion.jsp\">Etiquetas de Ubicación</a>.</p></body></html>");
            return;
        }
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "inline; filename=\"" + pdf.nombre() + "\"");
        // Privado y por poco tiempo: el visor del navegador lo reutiliza al descargar o imprimir.
        res.setHeader("Cache-Control", "private, max-age=600");
        res.setContentLength(pdf.bytes().length);
        res.getOutputStream().write(pdf.bytes());
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
