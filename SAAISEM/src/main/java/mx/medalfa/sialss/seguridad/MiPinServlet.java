package mx.medalfa.sialss.seguridad;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.LoginDao;

/**
 * "Mi PIN" (menú USUARIO): el usuario de la sesión crea, cambia o quita su PIN de inicio de sesión.
 * Siempre pide la contraseña actual. Responde redirigiendo a MiPin.jsp con el mensaje en sesión.
 */
@WebServlet("/MiPin")
public class MiPinServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MiPinServlet.class.getName());
    public static final String MENSAJE = "sialss.pin.mensaje";
    public static final String MENSAJE_OK = "sialss.pin.ok";
    private static final String TOKEN = "sialss.pin.token";

    public static String token(HttpSession s) {
        Object t = s.getAttribute(TOKEN);
        if (t == null) {
            byte[] b = new byte[24];
            new SecureRandom().nextBytes(b);
            t = HexFormat.of().formatHex(b);
            s.setAttribute(TOKEN, t);
        }
        return (String) t;
    }

    /** Fecha del PIN del usuario de la sesión, null si no tiene o si faltan las columnas. */
    public static String fechaPin(HttpSession s) {
        Acceso acceso = PoliticaSesion.acceso(s);
        Object id = s.getAttribute(PoliticaSesion.ID_USUARIO);
        if (acceso == null || !(id instanceof Integer idUsuario)) {
            return null;
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            return new LoginDao(c, acceso).fechaPin(idUsuario);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(false);
        Acceso acceso = PoliticaSesion.acceso(s);
        Object id = s == null ? null : s.getAttribute(PoliticaSesion.ID_USUARIO);
        if (acceso == null || !(id instanceof Integer idUsuario)) {
            res.sendRedirect(req.getContextPath() + "/indexalmacen.jsp");
            return;
        }
        if (!token(s).equals(req.getParameter("token"))) {
            avisar(s, "La página caducó. Vuelva a intentarlo.", false);
            res.sendRedirect(req.getContextPath() + "/MiPin.jsp");
            return;
        }
        String accion = String.valueOf(req.getParameter("accion"));
        String contrasena = req.getParameter("contrasena");
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            LoginDao dao = new LoginDao(c, acceso);
            if (contrasena == null || contrasena.isEmpty() || !dao.contrasenaCorrecta(idUsuario, contrasena)) {
                avisar(s, "La contraseña actual no es correcta. No se cambió el PIN.", false);
            } else if ("quitar".equals(accion)) {
                dao.guardarPin(idUsuario, null);
                avisar(s, "Se quitó su PIN. Desde ahora solo puede entrar con su contraseña.", true);
            } else {
                String pin = req.getParameter("pin");
                String problema = Pin.problema(pin);
                if (problema != null) {
                    avisar(s, problema, false);
                } else if (!pin.equals(req.getParameter("pin2"))) {
                    avisar(s, "Los dos PIN no son iguales. Escríbalo otra vez.", false);
                } else {
                    dao.guardarPin(idUsuario, Pin.cifrar(pin));
                    avisar(s, "Se guardó su PIN. Ya puede iniciar sesión con su PIN o con su contraseña.", true);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "No se pudo guardar el PIN", e);
            avisar(s, "No se pudo guardar el PIN. Si el problema sigue, avise al administrador del sistema.", false);
        }
        res.sendRedirect(req.getContextPath() + "/MiPin.jsp");
    }

    private static void avisar(HttpSession s, String mensaje, boolean ok) {
        s.setAttribute(MENSAJE, mensaje);
        s.setAttribute(MENSAJE_OK, ok);
    }
}
