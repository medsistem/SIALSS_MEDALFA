package mx.medalfa.sialss.seguridad;

import in.co.sneh.model.Usuario;
import jakarta.servlet.http.HttpSession;
import mx.medalfa.sialss.config.Entorno;

/**
 * Reglas de acceso y de inactividad, configurables en el .env:
 * <ul>
 *   <li>{@code LOGIN_INTENTOS} (3): intentos fallidos con los que se bloquea el usuario.</li>
 *   <li>{@code SESION_BLOQUEO_MIN} (5): minutos sin actividad para bloquear la pantalla y pedir la contraseña.</li>
 *   <li>{@code SESION_CIERRE_MIN} (60): minutos sin actividad para cerrar la sesión.</li>
 * </ul>
 */
public final class PoliticaSesion {

    public static final String ACCESO = "sialss.acceso";
    public static final String ID_USUARIO = "sialss.idUsuario";
    public static final String USUARIO = "sialss.usuario";
    public static final String ULTIMA_ACTIVIDAD = "sialss.ultimaActividad";
    public static final String BLOQUEADA = "sialss.bloqueada";
    public static final String INTENTOS_DESBLOQUEO = "sialss.intentosDesbloqueo";

    public static final String MENSAJE_BLOQUEADO =
            "Su usuario está bloqueado. Comuníquese con el administrador del sistema para desbloquearlo.";

    private PoliticaSesion() {
    }

    public static int intentosMaximos() {
        return Math.max(1, Entorno.entero("LOGIN_INTENTOS", 3));
    }

    public static long bloqueoMs() {
        return Math.max(1, Entorno.entero("SESION_BLOQUEO_MIN", 5)) * 60_000L;
    }

    public static long cierreMs() {
        return Math.max(2, Entorno.entero("SESION_CIERRE_MIN", 60)) * 60_000L;
    }

    public static String mensajeBloqueoPorIntentos() {
        return "Su usuario quedó bloqueado por " + intentosMaximos() + " intentos fallidos. "
                + "Comuníquese con el administrador del sistema para desbloquearlo.";
    }

    /** Marca la sesión recién autenticada y fija su vida máxima sin actividad (cierre). */
    public static void iniciar(HttpSession s, Acceso acceso, Usuario u) {
        s.setAttribute(ACCESO, acceso.name());
        s.setAttribute(ID_USUARIO, u.getId());
        s.setAttribute(USUARIO, u.getUsuario());
        s.setAttribute(ULTIMA_ACTIVIDAD, System.currentTimeMillis());
        s.removeAttribute(BLOQUEADA);
        s.removeAttribute(INTENTOS_DESBLOQUEO);
        s.setMaxInactiveInterval((int) (cierreMs() / 1000));
    }

    /** Acceso con el que se autenticó la sesión, o null si no pasó por ServicioAcceso. */
    public static Acceso acceso(HttpSession s) {
        Object a = s == null ? null : s.getAttribute(ACCESO);
        return a == null ? null : Acceso.valueOf(a.toString());
    }

    /** true si ya pasó el tiempo de bloqueo; en ese caso deja la sesión marcada como bloqueada. */
    public static boolean revisarBloqueo(HttpSession s) {
        if (Boolean.TRUE.equals(s.getAttribute(BLOQUEADA))) {
            return true;
        }
        Object u = s.getAttribute(ULTIMA_ACTIVIDAD);
        long ultima = u instanceof Long l ? l : System.currentTimeMillis();
        if (System.currentTimeMillis() - ultima >= bloqueoMs()) {
            s.setAttribute(BLOQUEADA, Boolean.TRUE);
            return true;
        }
        return false;
    }

    /**
     * true si ya pasó el tiempo de cierre sin actividad del usuario. No basta el maxInactiveInterval del
     * contenedor: las pantallas con consultas automáticas (AJAX con temporizador) lo reinician sin que
     * haya nadie trabajando.
     */
    public static boolean vencida(HttpSession s) {
        Object u = s.getAttribute(ULTIMA_ACTIVIDAD);
        return u instanceof Long l && System.currentTimeMillis() - l >= cierreMs();
    }

    public static void registrarActividad(HttpSession s) {
        s.setAttribute(ULTIMA_ACTIVIDAD, System.currentTimeMillis());
    }

    public static void desbloquear(HttpSession s) {
        s.removeAttribute(BLOQUEADA);
        s.removeAttribute(INTENTOS_DESBLOQUEO);
        registrarActividad(s);
    }
}
