package mx.medalfa.sialss.cargas;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.LoginDao;
import mx.medalfa.sialss.menu.MenuSialss;

/**
 * Cargas por Excel del módulo "Carga de Archivos", con el modelo de CargaInventarioServlet:
 * GET {@code accion=plantilla}; POST {@code accion=revisar} (multipart, campo {@code archivo}) valida sin escribir
 * y guarda la revisión en la sesión; POST {@code accion=confirmar} ({@code token}, {@code contrasena} del usuario
 * de la sesión, 3 intentos) aplica en una transacción; POST {@code accion=cancelar}.
 * La carga se elige por la ruta.
 */
@WebServlet({"/CargaArchivoRequerimiento", "/CargaArchivoCaducos", "/CargaArchivoOC", "/CargaArchivoEquipos"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 11 * 1024 * 1024)
public class CargaArchivoServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CargaArchivoServlet.class.getName());
    private static final Gson GSON = new Gson();
    private static final int INTENTOS_MAXIMOS = 3;
    private static final int MAXIMO_ERRORES = 200;

    private static final Map<String, CargaArchivo> CARGAS = Map.of(
            "/CargaArchivoRequerimiento", new CargaRequerimiento(),
            "/CargaArchivoCaducos", new CargaCaducos(),
            "/CargaArchivoOC", new CargaOC(),
            "/CargaArchivoEquipos", new CargaEquipos());

    /** ¿Puede la sesión usar la carga de esta página? (lo usan también las JSP). */
    public static boolean autorizado(HttpSession s, String ruta) {
        CargaArchivo carga = CARGAS.get(ruta);
        CargaArchivo.Usuario u = CargaArchivo.Usuario.de(s);
        return carga != null && u != null && carga.accesos().contains(u.acceso())
                && MenuSialss.tieneOpcion(u.acceso(), u.perfil(), u.idUsuario(), carga.pagina());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        CargaArchivo carga = CARGAS.get(req.getServletPath());
        if (!autorizado(req.getSession(false), req.getServletPath())) {
            res.sendError(403, "Su sesión terminó o no tiene esta opción en su menú.");
            return;
        }
        if (!"plantilla".equals(req.getParameter("accion"))) {
            json(res, 400, Map.of("error", "Acción no válida."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=\"plantilla_carga_" + carga.nombre() + ".xlsx\"");
            carga.plantilla(c, res.getOutputStream());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Plantilla de carga " + carga.nombre(), e);
            res.sendError(500, "No se pudo generar la plantilla.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sesion = req.getSession(false);
        String ruta = req.getServletPath();
        if (!autorizado(sesion, ruta)) {
            json(res, 401, Map.of("error", "Su sesión terminó o no tiene esta opción en su menú. Vuelva a iniciar sesión."));
            return;
        }
        CargaArchivo carga = CARGAS.get(ruta);
        CargaArchivo.Usuario u = CargaArchivo.Usuario.de(sesion);
        switch (String.valueOf(req.getParameter("accion"))) {
            case "revisar" -> revisar(req, res, sesion, carga, u);
            case "confirmar" -> confirmar(req, res, sesion, carga, u);
            case "cancelar" -> {
                limpiar(sesion, carga);
                json(res, 200, Map.of("ok", true));
            }
            default -> json(res, 400, Map.of("error", "Acción no válida."));
        }
    }

    private void revisar(HttpServletRequest req, HttpServletResponse res, HttpSession sesion, CargaArchivo carga,
                         CargaArchivo.Usuario u) throws IOException, ServletException {
        limpiar(sesion, carga);
        Part archivo = req.getPart("archivo");
        if (archivo == null || archivo.getSize() == 0) {
            json(res, 400, Map.of("error", "Elija el archivo de Excel."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion(); InputStream in = archivo.getInputStream()) {
            CargaArchivo.Revision r = carga.revisar(c, in, u);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("valida", r.valida());
            m.put("errores", r.errores().size() > MAXIMO_ERRORES ? r.errores().subList(0, MAXIMO_ERRORES) : r.errores());
            m.put("totalErrores", r.errores().size());
            m.put("muestra", r.muestra());
            m.put("resumen", r.resumen());
            if (r.valida()) {
                String token = UUID.randomUUID().toString();
                sesion.setAttribute(clave(carga, "revision"), r);
                sesion.setAttribute(clave(carga, "token"), token);
                sesion.setAttribute(clave(carga, "intentos"), 0);
                m.put("token", token);
            }
            json(res, 200, m);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Revisar carga " + carga.nombre(), e);
            json(res, 500, Map.of("error", "No se pudo consultar la base de datos."));
        }
    }

    private void confirmar(HttpServletRequest req, HttpServletResponse res, HttpSession sesion, CargaArchivo carga,
                           CargaArchivo.Usuario u) throws IOException {
        Object r = sesion.getAttribute(clave(carga, "revision"));
        Object t = sesion.getAttribute(clave(carga, "token"));
        if (!(r instanceof CargaArchivo.Revision revision) || t == null || !t.equals(req.getParameter("token"))) {
            json(res, 409, Map.of("error", "No hay una revisión pendiente: vuelva a subir el archivo."));
            return;
        }
        String contrasena = req.getParameter("contrasena");
        if (contrasena == null || contrasena.isEmpty()) {
            json(res, 400, Map.of("error", "Escriba su contraseña para confirmar."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            if (!new LoginDao(c, u.acceso()).contrasenaCorrecta(u.idUsuario(), contrasena)) {
                String intentos = clave(carga, "intentos");
                int n = (sesion.getAttribute(intentos) instanceof Integer i ? i : 0) + 1;
                if (n >= INTENTOS_MAXIMOS) {
                    limpiar(sesion, carga);
                    json(res, 403, Map.of("error", "Contraseña incorrecta " + n + " veces: se canceló la carga. Vuelva a subir el archivo.",
                            "cancelada", true));
                } else {
                    sesion.setAttribute(intentos, n);
                    int quedan = INTENTOS_MAXIMOS - n;
                    json(res, 403, Map.of("error", "Contraseña incorrecta. Le " + (quedan == 1 ? "queda 1 intento." : "quedan " + quedan + " intentos.")));
                }
                return;
            }
            String mensaje = carga.aplicar(c, revision, u);
            limpiar(sesion, carga);
            LOG.info(() -> "Carga " + carga.nombre() + " aplicada por " + u.usuario() + ": " + mensaje);
            json(res, 200, Map.of("mensaje", mensaje));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Aplicar carga " + carga.nombre(), e);
            json(res, 500, Map.of("error", mx.medalfa.sialss.config.ErrorBase.mensaje(e)));
        }
    }

    private static String clave(CargaArchivo carga, String que) {
        return "sialss.carga." + carga.nombre() + "." + que;
    }

    private static void limpiar(HttpSession s, CargaArchivo carga) {
        s.removeAttribute(clave(carga, "revision"));
        s.removeAttribute(clave(carga, "token"));
        s.removeAttribute(clave(carga, "intentos"));
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
