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
    /** Perfil real (F_TipUsu). "Tipo" puede valer "1" para el superusuario, por compatibilidad con pantallas antiguas. */
    public static final String PERFIL = "sialss.perfil";

    /** Perfil SUPERUSUARIO (tb_tipusuario 100): todo lo del administrador más perfiles y menús por perfil. */
    public static final String SUPERUSUARIO = "100";
    /** Perfiles que administran usuarios y proyectos: superusuario, ADMINISTRADOR y ADMIN ALMACEN. */
    public static final java.util.Set<String> ADMINISTRADORES = java.util.Set.of(SUPERUSUARIO, "1", "5");

    public static final String MENSAJE_BLOQUEADO =
            "Su usuario está bloqueado. Comuníquese con el administrador del sistema para desbloquearlo.";

    public static final String MENSAJE_PERFIL_ACCESO =
            "Su usuario no tiene el perfil de este acceso. Entre por el acceso que le corresponde.";

    public static final String MENSAJE_PERFIL_SUSPENDIDO =
            "Su perfil de usuario está suspendido. Comuníquese con el administrador del sistema.";

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

    /**
     * Perfil real del usuario (F_TipUsu): el guardado al iniciar sesión o, si no hay, el atributo "Tipo".
     * Úsese para el menú y los permisos nuevos; "Tipo" queda para las pantallas antiguas.
     */
    public static String perfil(HttpSession s) {
        if (s == null) {
            return null;
        }
        Object p = s.getAttribute(PERFIL);
        if (p == null) {
            p = s.getAttribute("Tipo");
        }
        return p == null ? null : p.toString().trim();
    }

    /** Valor de "Tipo" para las pantallas antiguas: el superusuario se ve como ADMINISTRADOR (1). */
    public static String tipoCompatible(String perfil) {
        return SUPERUSUARIO.equals(perfil) ? "1" : perfil;
    }

    public static boolean esSuperusuario(HttpSession s) {
        return acceso(s) == Acceso.ALMACEN && SUPERUSUARIO.equals(perfil(s));
    }

    /** Superusuario, ADMINISTRADOR o ADMIN ALMACEN del acceso de Almacén. */
    public static boolean esAdministrador(HttpSession s) {
        return acceso(s) == Acceso.ALMACEN && ADMINISTRADORES.contains(perfil(s));
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
