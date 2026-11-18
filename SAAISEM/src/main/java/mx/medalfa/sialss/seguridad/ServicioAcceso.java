package mx.medalfa.sialss.seguridad;

import in.co.sneh.model.Usuario;
import in.co.sneh.service.SessionManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.LoginDao;

/**
 * Regla común de inicio de sesión para todos los accesos del sistema:
 * al llegar a {@link PoliticaSesion#intentosMaximos()} intentos fallidos el usuario se bloquea
 * (F_Status = 'S') y se le pide comunicarse con el administrador para desbloquearlo.
 */
public final class ServicioAcceso {

    private static final Logger LOG = Logger.getLogger(ServicioAcceso.class.getName());

    public enum Estado { CORRECTO, INCORRECTO, BLOQUEADO_AHORA, BLOQUEADO, ERROR }

    /** Resultado del intento. En CORRECTO trae el usuario, la sesión nueva y los días desde el cambio de contraseña. */
    public record Resultado(Estado estado, Usuario usuario, HttpSession sesion, Integer diasContrasena,
                            boolean cerroOtraSesion, String mensaje) {
    }

    private ServicioAcceso() {
    }

    /**
     * Valida usuario y contraseña, o usuario y PIN cuando el formulario manda {@code modo=pin}
     * (el PIN llega en el mismo campo que la contraseña). Los intentos fallidos con PIN o contraseña suman juntos.
     */
    public static Resultado autenticar(HttpServletRequest request, Acceso acceso, String nombre, String contrasena) {
        nombre = nombre == null ? "" : nombre.trim();
        contrasena = contrasena == null ? "" : contrasena;
        boolean conPin = "pin".equals(request.getParameter("modo"));
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            return new Resultado(Estado.INCORRECTO, null, null, null, false,
                    conPin ? "Escriba su usuario y su PIN." : "Escriba su usuario y su contraseña.");
        }
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            LoginDao dao = new LoginDao(c, acceso);
            Usuario u = conPin ? dao.buscarConPin(nombre, contrasena.trim()) : dao.buscar(nombre, contrasena);

            if (u != null && !"A".equals(u.getStatus())) {
                // Contraseña o PIN correcto pero el usuario está bloqueado o dado de baja.
                return new Resultado(Estado.BLOQUEADO, u, null, null, false, PoliticaSesion.MENSAJE_BLOQUEADO);
            }
            if (u != null && !acceso.permitePerfil(u.getTipo())) {
                // Contraseña correcta pero entró por la puerta equivocada: no cuenta como intento fallido.
                return new Resultado(Estado.INCORRECTO, null, null, null, false, PoliticaSesion.MENSAJE_PERFIL_ACCESO);
            }
            if (u != null && acceso.usaTbUsuario() && mx.medalfa.sialss.usuarios.PerfilDao.suspendido(c, u.getTipo())) {
                // Perfil suspendido en la pantalla "Perfiles de Usuario": nadie con ese perfil entra.
                return new Resultado(Estado.BLOQUEADO, u, null, null, false, PoliticaSesion.MENSAJE_PERFIL_SUSPENDIDO);
            }
            if (u == null) {
                if (!dao.existe(nombre)) {
                    dao.registrarIntentoFallido(nombre);
                    return new Resultado(Estado.INCORRECTO, null, null, null, false, mensajeIncorrecto());
                }
                int fallidos = dao.registrarIntentoFallido(nombre);
                if (fallidos >= PoliticaSesion.intentosMaximos()) {
                    dao.suspender(nombre);
                    String bloqueado = nombre.replaceAll("[\\r\\n]", "");
                    LOG.warning(() -> "Usuario bloqueado por intentos fallidos: " + acceso + "/" + bloqueado);
                    return new Resultado(Estado.BLOQUEADO_AHORA, null, null, null, false, PoliticaSesion.mensajeBloqueoPorIntentos());
                }
                int restan = PoliticaSesion.intentosMaximos() - fallidos;
                return new Resultado(Estado.INCORRECTO, null, null, null, false, mensajeIncorrecto()
                        + (restan == 1 ? " Le queda 1 intento antes de que su usuario se bloquee." : ""));
            }

            boolean otra = SessionManagerService.closeSessionId(String.valueOf(u.getId()));
            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession s = request.getSession(true);
            PoliticaSesion.iniciar(s, acceso, u);
            dao.registrarEntradaCorrecta(nombre);
            return new Resultado(Estado.CORRECTO, u, s, dao.diasDesdeCambioContrasena(u.getId()), otra, null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al validar el usuario en " + acceso, e);
            return new Resultado(Estado.ERROR, null, null, null, false, "No se pudo validar el usuario. Intente más tarde.");
        }
    }

    private static String mensajeIncorrecto() {
        return "Datos incorrectos, intente de nuevo.";
    }
}
