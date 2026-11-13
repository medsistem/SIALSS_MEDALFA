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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Papel;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Tipo;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Ubicacion;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Etiquetas de ubicación (EtiquetasUbicacion.jsp):
 * GET {@code accion=secciones} y {@code accion=buscar} (JSON); POST {@code accion=pdf} devuelve el PDF.
 */
@WebServlet("/EtiquetasUbicacion")
public class EtiquetasUbicacionServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(EtiquetasUbicacionServlet.class.getName());
    private static final Gson GSON = new Gson();
    /** Tope por PDF (una hoja carta lleva 24 de poste; 3 000 son 125 hojas). */
    public static final int MAXIMO = 3000;
    private static final int MAXIMO_LISTA = 2000;

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
                default -> json(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Etiquetas de ubicación", e);
            json(res, 500, Map.of("error", "No se pudieron leer las ubicaciones."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        if (!conSesion(req.getSession(false))) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Su sesión terminó. Vuelva a iniciar sesión.");
            return;
        }
        Tipo tipo;
        Papel papel;
        try {
            tipo = Tipo.valueOf(String.valueOf(req.getParameter("tipo")));
            papel = req.getParameter("papel") == null ? Papel.CARTA : Papel.valueOf(req.getParameter("papel"));
        } catch (IllegalArgumentException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de etiqueta o papel no válido.");
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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, claves.isEmpty()
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
            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition", "inline; filename=\"" + nombre + "\"");
            res.setHeader("Cache-Control", "no-store");
            res.setContentLength(pdf.size());
            pdf.writeTo(res.getOutputStream());
        } catch (SQLException | DocumentException e) {
            LOG.log(Level.SEVERE, "PDF de etiquetas de ubicación", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo generar el PDF.");
        }
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
