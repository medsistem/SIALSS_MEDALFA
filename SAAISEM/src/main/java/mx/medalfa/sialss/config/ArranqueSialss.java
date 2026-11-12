package mx.medalfa.sialss.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Carga el .env con el nombre del contexto al iniciar y cierra los pools al detener la aplicación.
 * Debe ser el primer listener del web.xml.
 */
public class ArranqueSialss implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Entorno.iniciar(sce.getServletContext().getContextPath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BaseDatos.cerrarTodo();
    }
}
