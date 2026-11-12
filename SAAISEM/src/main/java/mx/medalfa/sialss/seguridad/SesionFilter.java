package mx.medalfa.sialss.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Bloqueo por inactividad del lado del servidor (la pantalla solo lo refleja):
 * si una sesión autenticada pasa {@code SESION_BLOQUEO_MIN} sin actividad, toda petición a una página
 * o servicio se detiene hasta que el usuario vuelva a escribir su contraseña (/sesion).
 * El cierre a los {@code SESION_CIERRE_MIN} lo hace el contenedor (maxInactiveInterval de la sesión).
 */
public class SesionFilter extends HttpFilter {

    /** Recursos y pantallas que no requieren sesión desbloqueada. */
    private static final Pattern LIBRES = Pattern.compile(
            "(?i)^/(sesion|login|loginISEM|LoginAuditoria|bloqueo\\.jsp|index[^/]*\\.jsp|)$"
            + "|(?i)\\.(css|js|map|png|jpe?g|gif|svg|ico|woff2?|ttf|eot|otf)$");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String ruta = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession s = req.getSession(false);
        if (LIBRES.matcher(ruta).find() || PoliticaSesion.acceso(s) == null) {
            chain.doFilter(req, res);
            return;
        }
        boolean deFondo = esLlamadaDeFondo(req);
        if (PoliticaSesion.vencida(s)) {
            Acceso acceso = PoliticaSesion.acceso(s);
            s.invalidate();
            if (deFondo) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("X-SIALSS-Sesion", "cerrada");
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().write("{\"cerrada\":true}");
            } else {
                res.sendRedirect(req.getContextPath() + "/" + acceso.paginaLogin + "?motivo=inactividad");
            }
            return;
        }
        if (!PoliticaSesion.revisarBloqueo(s)) {
            // Las consultas automáticas (AJAX) no cuentan como actividad: solo páginas y formularios.
            if (!deFondo) {
                PoliticaSesion.registrarActividad(s);
            }
            chain.doFilter(req, res);
            return;
        }
        // Sesión bloqueada.
        if (deFondo) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("X-SIALSS-Sesion", "bloqueada");
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"bloqueada\":true}");
            return;
        }
        String volver = "GET".equalsIgnoreCase(req.getMethod())
                ? ruta + (req.getQueryString() == null ? "" : "?" + req.getQueryString())
                : "/main_menu.jsp";
        res.sendRedirect(req.getContextPath() + "/bloqueo.jsp?volver=" + URLEncoder.encode(volver, StandardCharsets.UTF_8));
    }

    private static boolean esLlamadaDeFondo(HttpServletRequest req) {
        String xrw = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(xrw)
                || (accept != null && accept.contains("application/json") && !accept.contains("text/html"));
    }
}
