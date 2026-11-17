package mx.medalfa.sialss.usuarios;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Perfiles de usuario (PerfilesUsuario.jsp), solo para el SUPERUSUARIO: GET lista; POST {@code accion=alta}
 * ({@code nombre}) o {@code accion=estatus} ({@code id}, {@code activo=true|false}). Los POST llevan el token de
 * la página en el encabezado X-SIALSS-Token.
 */
@WebServlet("/PerfilesUsuario")
public class PerfilesServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(PerfilesServlet.class.getName());
    private static final Gson GSON = new Gson();
    private static final String TOKEN = "sialss.perfiles.token";

    public static boolean autorizado(HttpSession s) {
        return PoliticaSesion.esSuperusuario(s);
    }

    public static String token(HttpSession s) {
        Object t = s.getAttribute(TOKEN);
        if (t == null) {
            t = UUID.randomUUID().toString();
            s.setAttribute(TOKEN, t);
        }
        return (String) t;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (!autorizado(s)) {
            json(res, 403, Map.of("error", "Solo el superusuario administra los perfiles."));
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("perfiles", PerfilDao.lista(c));
            r.put("conEstatus", PerfilDao.tieneEstatus(c));
            r.put("miPerfil", PoliticaSesion.perfil(s));
            json(res, 200, r);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Lista de perfiles", e);
            json(res, 500, Map.of("error", "No se pudo consultar la base de datos."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(false);
        if (!autorizado(s)) {
            json(res, 403, Map.of("error", "Solo el superusuario administra los perfiles."));
            return;
        }
        if (!token(s).equals(req.getHeader("X-SIALSS-Token"))) {
            json(res, 403, Map.of("error", "La página caducó: recárguela."));
            return;
        }
        String quien = String.valueOf(s.getAttribute(PoliticaSesion.USUARIO));
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            switch (String.valueOf(req.getParameter("accion"))) {
                case "alta" -> {
                    PerfilDao.Perfil p = PerfilDao.alta(c, req.getParameter("nombre"));
                    LOG.info(() -> "Perfil dado de alta por " + quien + ": " + p.id() + " " + p.nombre());
                    json(res, 200, Map.of("mensaje", "Se dio de alta el perfil " + p.nombre() + " con el número " + p.id()
                            + ". Asígnele sus opciones en \"Menús por perfil y usuario\"."));
                }
                case "estatus" -> {
                    int id = Integer.parseInt(String.valueOf(req.getParameter("id")).trim());
                    boolean activo = Boolean.parseBoolean(req.getParameter("activo"));
                    PerfilDao.cambiarEstatus(c, id, activo, PoliticaSesion.perfil(s));
                    LOG.info(() -> "Perfil " + id + (activo ? " reactivado" : " suspendido") + " por " + quien);
                    json(res, 200, Map.of("mensaje", activo
                            ? "Perfil " + id + " reactivado: sus usuarios ya pueden entrar."
                            : "Perfil " + id + " suspendido: sus usuarios ya no pueden iniciar sesión (las sesiones abiertas siguen hasta que salgan)."));
                }
                default -> json(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (PerfilDao.Rechazo e) {
            json(res, 400, Map.of("error", e.getMessage()));
        } catch (NumberFormatException e) {
            json(res, 400, Map.of("error", "Perfil no válido."));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Guardar perfil", e);
            json(res, 500, Map.of("error", "No se guardó: " + e.getMessage()));
        }
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
