package mx.medalfa.sialss.inventario;

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

/**
 * Carga de inventario (CargaInventarioExcel.jsp):
 * GET {@code accion=plantilla} descarga el Excel de ejemplo con catálogos;
 * POST {@code accion=revisar} (multipart, campo {@code archivo}) lee y valida sin escribir y guarda la
 * revisión en la sesión; POST {@code accion=confirmar} con {@code token} y {@code contrasena} del usuario
 * de la sesión escribe en tb_lote y tb_movinv; POST {@code accion=cancelar} descarta la revisión.
 */
@WebServlet("/CargaInventarioExcel")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 11 * 1024 * 1024)
public class CargaInventarioServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CargaInventarioServlet.class.getName());
    private static final Gson GSON = new Gson();
    private static final String REVISION = "sialss.cargaInventario.revision";
    private static final String TOKEN = "sialss.cargaInventario.token";
    private static final String INTENTOS = "sialss.cargaInventario.intentos";
    private static final int INTENTOS_MAXIMOS = 3;

    /** Página del menú que da permiso (menú CARGA DE ARCHIVOS: perfiles 100 y 1). */
    public static final String PAGINA = "CargaInventarioExcel.jsp";

    /** ¿La sesión es de Almacén y tiene la opción en su menú? (la usa también la JSP). */
    public static boolean autorizado(HttpSession s) {
        SesionAlmacen a = SesionAlmacen.de(s);
        return a != null && mx.medalfa.sialss.menu.MenuSialss.tieneOpcion(a.acceso(),
                mx.medalfa.sialss.seguridad.PoliticaSesion.perfil(s), a.idUsuario(), PAGINA);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!autorizado(req.getSession(false))) {
            res.sendError(401, "Su sesión terminó o no es de Almacén.");
            return;
        }
        if (!"plantilla".equals(req.getParameter("accion"))) {
            json(res, 400, Map.of("error", "Acción no válida."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment; filename=\"plantilla_carga_inventario.xlsx\"");
            CargaInventario.plantilla(c, res.getOutputStream());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Plantilla de carga de inventario", e);
            res.sendError(500, "No se pudo generar la plantilla.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sesion = req.getSession(false);
        SesionAlmacen s = SesionAlmacen.de(sesion);
        if (s == null || !autorizado(sesion)) {
            json(res, 401, Map.of("error", "Su sesión terminó o no es de Almacén. Vuelva a iniciar sesión."));
            return;
        }
        switch (String.valueOf(req.getParameter("accion"))) {
            case "revisar" -> revisar(req, res, sesion);
            case "confirmar" -> confirmar(req, res, sesion, s);
            case "cancelar" -> {
                limpiar(sesion);
                json(res, 200, Map.of("ok", true));
            }
            default -> json(res, 400, Map.of("error", "Acción no válida."));
        }
    }

    private void revisar(HttpServletRequest req, HttpServletResponse res, HttpSession sesion) throws IOException, ServletException {
        limpiar(sesion);
        Part archivo = req.getPart("archivo");
        if (archivo == null || archivo.getSize() == 0) {
            json(res, 400, Map.of("error", "Elija el archivo de Excel."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion(); InputStream in = archivo.getInputStream()) {
            CargaInventario.Revision r = CargaInventario.revisar(c, in);
            Map<String, Object> m = new LinkedHashMap<>(CargaInventario.resumen(r));
            if (r.valida()) {
                String token = UUID.randomUUID().toString();
                sesion.setAttribute(REVISION, r);
                sesion.setAttribute(TOKEN, token);
                sesion.setAttribute(INTENTOS, 0);
                m.put("token", token);
            }
            json(res, 200, m);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Revisar carga de inventario", e);
            json(res, 500, Map.of("error", "No se pudo consultar la base de datos."));
        }
    }

    private void confirmar(HttpServletRequest req, HttpServletResponse res, HttpSession sesion, SesionAlmacen s) throws IOException {
        Object r = sesion.getAttribute(REVISION);
        Object t = sesion.getAttribute(TOKEN);
        if (!(r instanceof CargaInventario.Revision revision) || t == null || !t.equals(req.getParameter("token"))) {
            json(res, 409, Map.of("error", "No hay una revisión pendiente: vuelva a subir el archivo."));
            return;
        }
        String contrasena = req.getParameter("contrasena");
        if (contrasena == null || contrasena.isEmpty()) {
            json(res, 400, Map.of("error", "Escriba su contraseña para confirmar."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            if (!new LoginDao(c, s.acceso()).contrasenaCorrecta(s.idUsuario(), contrasena)) {
                int n = (sesion.getAttribute(INTENTOS) instanceof Integer i ? i : 0) + 1;
                if (n >= INTENTOS_MAXIMOS) {
                    limpiar(sesion);
                    json(res, 403, Map.of("error", "Contraseña incorrecta " + n + " veces: se canceló la carga. Vuelva a subir el archivo.",
                            "cancelada", true));
                } else {
                    sesion.setAttribute(INTENTOS, n);
                    json(res, 403, Map.of("error", "Contraseña incorrecta. Le quedan " + (INTENTOS_MAXIMOS - n) + " intentos."));
                }
                return;
            }
            CargaInventario.Aplicado a = CargaInventario.aplicar(c, revision.renglones(), s.usuario());
            limpiar(sesion);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("documento", a.documento());
            m.put("lotesNuevos", a.lotesNuevos());
            m.put("lotesSumados", a.lotesSumados());
            m.put("piezas", a.piezas());
            m.put("usuario", s.usuario());
            json(res, 200, m);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Aplicar carga de inventario", e);
            json(res, 500, Map.of("error", mx.medalfa.sialss.config.ErrorBase.mensaje(e)));
        }
    }

    private static void limpiar(HttpSession s) {
        s.removeAttribute(REVISION);
        s.removeAttribute(TOKEN);
        s.removeAttribute(INTENTOS);
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
