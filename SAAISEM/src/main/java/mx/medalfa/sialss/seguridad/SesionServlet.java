package mx.medalfa.sialss.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.LoginDao;

/**
 * Servicio de la pantalla de bloqueo (js/sialss-sesion.js):
 * <ul>
 *   <li>{@code accion=latido}: la pantalla avisa que el usuario está trabajando (teclea, mueve el ratón).</li>
 *   <li>{@code accion=estado}: segundos que faltan para el bloqueo y para el cierre.</li>
 *   <li>{@code accion=desbloquear&contrasena=...}: valida la contraseña del usuario de la sesión.
 *       Con {@code LOGIN_INTENTOS} fallos seguidos se bloquea el usuario y se cierra la sesión.</li>
 * </ul>
 * Responde JSON.
 */
@WebServlet(name = "SesionServlet", urlPatterns = {"/sesion"})
public class SesionServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(SesionServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        HttpSession s = req.getSession(false);
        Acceso acceso = PoliticaSesion.acceso(s);
        if (acceso == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"cerrada\":true}");
            return;
        }
        if (PoliticaSesion.vencida(s)) {
            String login = acceso.paginaLogin;
            s.invalidate();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"cerrada\":true,\"login\":\"" + login + "\"}");
            return;
        }
        String accion = String.valueOf(req.getParameter("accion"));
        switch (accion) {
            case "latido" -> {
                if (PoliticaSesion.revisarBloqueo(s)) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"bloqueada\":true}");
                } else {
                    PoliticaSesion.registrarActividad(s);
                    res.getWriter().write(estado(s));
                }
            }
            case "estado" -> {
                PoliticaSesion.revisarBloqueo(s);
                res.getWriter().write(estado(s));
            }
            case "bloquear" -> {
                // La pantalla cumplió el tiempo sin actividad: el servidor queda igual.
                s.setAttribute(PoliticaSesion.BLOQUEADA, Boolean.TRUE);
                res.getWriter().write("{\"bloqueada\":true}");
            }
            case "desbloquear" -> desbloquear(req, res, s, acceso);
            default -> {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\":\"accion\"}");
            }
        }
    }

    private void desbloquear(HttpServletRequest req, HttpServletResponse res, HttpSession s, Acceso acceso) throws IOException {
        String contrasena = req.getParameter("contrasena");
        Object id = s.getAttribute(PoliticaSesion.ID_USUARIO);
        String usuario = String.valueOf(s.getAttribute(PoliticaSesion.USUARIO));
        if (contrasena == null || contrasena.isEmpty() || !(id instanceof Integer idUsuario)) {
            res.getWriter().write("{\"ok\":false,\"mensaje\":\"Escriba su contraseña o su PIN.\"}");
            return;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            LoginDao dao = new LoginDao(c, acceso);
            // Se acepta la contraseña o, si el usuario creó uno, su PIN.
            if (dao.contrasenaCorrecta(idUsuario, contrasena)
                    || (contrasena.trim().matches("\\d{4,8}") && pinSeguro(dao, idUsuario, contrasena.trim()))) {
                PoliticaSesion.desbloquear(s);
                res.getWriter().write("{\"ok\":true}");
                return;
            }
            Object n = s.getAttribute(PoliticaSesion.INTENTOS_DESBLOQUEO);
            int intentos = (n instanceof Integer i ? i : 0) + 1;
            s.setAttribute(PoliticaSesion.INTENTOS_DESBLOQUEO, intentos);
            dao.registrarIntentoFallido(usuario);
            if (intentos >= PoliticaSesion.intentosMaximos()) {
                dao.suspender(usuario);
                String login = acceso.paginaLogin;
                s.invalidate();
                LOG.warning(() -> "Usuario bloqueado al desbloquear pantalla: " + acceso + "/" + usuario.replaceAll("[\\r\\n]", ""));
                HttpSession nueva = req.getSession(true);
                nueva.setAttribute("mensaje", PoliticaSesion.mensajeBloqueoPorIntentos());
                res.getWriter().write("{\"ok\":false,\"bloqueado\":true,\"login\":\"" + login + "\",\"mensaje\":\""
                        + json(PoliticaSesion.mensajeBloqueoPorIntentos()) + "\"}");
                return;
            }
            int restan = PoliticaSesion.intentosMaximos() - intentos;
            res.getWriter().write("{\"ok\":false,\"mensaje\":\"Contraseña o PIN incorrecto. "
                    + (restan == 1 ? "Le queda 1 intento antes de que su usuario se bloquee." : "Le quedan " + restan + " intentos.") + "\"}");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al desbloquear la sesión", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"ok\":false,\"mensaje\":\"No se pudo validar. Intente de nuevo.\"}");
        }
    }

    private static String estado(HttpSession s) {
        long ultima = s.getAttribute(PoliticaSesion.ULTIMA_ACTIVIDAD) instanceof Long l ? l : System.currentTimeMillis();
        long pasado = System.currentTimeMillis() - ultima;
        boolean bloqueada = Boolean.TRUE.equals(s.getAttribute(PoliticaSesion.BLOQUEADA));
        return "{\"bloqueada\":" + bloqueada
                + ",\"segundosBloqueo\":" + Math.max(0, (PoliticaSesion.bloqueoMs() - pasado) / 1000)
                + ",\"segundosCierre\":" + Math.max(0, (PoliticaSesion.cierreMs() - pasado) / 1000) + "}";
    }

    private static String json(String t) {
        return t.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** El PIN solo existe si se aplicó despliegue/pin_usuarios.sql; sin esas columnas se ignora. */
    private static boolean pinSeguro(LoginDao dao, int idUsuario, String pin) {
        try {
            return dao.pinCorrecto(idUsuario, pin);
        } catch (java.sql.SQLException e) {
            return false;
        }
    }
}
