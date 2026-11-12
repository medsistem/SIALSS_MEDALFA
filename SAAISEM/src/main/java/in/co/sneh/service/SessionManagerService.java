package in.co.sneh.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Lleva el registro de las sesiones activas para poder cerrar la sesión previa
 * de un usuario. Antes se leían por reflexión del Manager de Tomcat; este
 * registro usa solo la API de Servlet, así que funciona en cualquier versión de Tomcat (no depende de org.apache.catalina).
 *
 * @author HP-MEDALFA
 */
public class SessionManagerService implements ServletContextListener, HttpSessionListener {
    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sessions.clear();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessions.put(se.getSession().getId(), se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessions.remove(se.getSession().getId());
    }

    public static boolean closeSessionId(String sessionID) throws IOException {
        for (HttpSession s : sessions.values()) {
            try {
                if (sessionID.equals(s.getAttribute("IdUsu"))) {
                    s.invalidate();
                    return true;
                }
            } catch (IllegalStateException e) {
                // La sesión ya estaba invalidada; sessionDestroyed la quitará.
                sessions.remove(s.getId());
            }
        }
        return false;
    }
}
