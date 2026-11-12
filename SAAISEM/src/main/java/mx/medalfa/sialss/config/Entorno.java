package mx.medalfa.sialss.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Datos de conexión y claves del sistema, leídos de un archivo .env como en SALPE.
 *
 * Orden de búsqueda del archivo (el primero que exista):
 * <ol>
 *   <li>propiedad de sistema {@code sialss.env}</li>
 *   <li>variable de entorno {@code SIALSS_ENV}</li>
 *   <li>{@code C:\Despliegue\<contexto>\sialss.env}</li>
 *   <li>{@code <catalina.base>/conf/<contexto>.env}</li>
 * </ol>
 * Una variable de entorno del sistema con el mismo nombre gana sobre el archivo.
 */
public final class Entorno {

    private static final Logger LOG = Logger.getLogger(Entorno.class.getName());
    private static final Map<String, String> valores = new HashMap<>();
    private static volatile String contexto = "SIALSS";
    private static volatile Path archivo;
    private static volatile boolean cargado;

    private Entorno() {
    }

    /** Lo llama {@link ArranqueSialss} al iniciar la aplicación con el nombre del contexto (sin "/"). */
    public static synchronized void iniciar(String nombreContexto) {
        if (nombreContexto != null && !nombreContexto.isBlank()) {
            contexto = nombreContexto.replace("/", "");
        }
        cargado = false;
        cargar();
    }

    public static String get(String clave) {
        String sistema = System.getenv(clave);
        if (sistema != null && !sistema.isBlank()) {
            return sistema;
        }
        cargar();
        return valores.get(clave);
    }

    public static String get(String clave, String porDefecto) {
        String v = get(clave);
        return v == null || v.isBlank() ? porDefecto : v;
    }

    public static String requerido(String clave) {
        String v = get(clave);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Falta " + clave + " en " + descripcionArchivo());
        }
        return v;
    }

    public static int entero(String clave, int porDefecto) {
        try {
            return Integer.parseInt(get(clave, String.valueOf(porDefecto)).trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    public static String descripcionArchivo() {
        return archivo != null ? archivo.toString() : "el archivo .env (no se encontró; rutas buscadas: " + candidatos() + ")";
    }

    private static synchronized void cargar() {
        if (cargado) {
            return;
        }
        valores.clear();
        archivo = null;
        for (Path p : candidatos()) {
            if (Files.isRegularFile(p)) {
                archivo = p;
                break;
            }
        }
        if (archivo == null) {
            LOG.warning(() -> "No se encontró el .env de " + contexto + "; rutas buscadas: " + candidatos());
        } else {
            try {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    String l = linea.strip();
                    if (l.isEmpty() || l.startsWith("#") || !l.contains("=")) {
                        continue;
                    }
                    int i = l.indexOf('=');
                    String v = l.substring(i + 1).strip();
                    if (v.length() >= 2 && (v.startsWith("\"") && v.endsWith("\"") || v.startsWith("'") && v.endsWith("'"))) {
                        v = v.substring(1, v.length() - 1);
                    }
                    valores.put(l.substring(0, i).strip(), v);
                }
                LOG.info(() -> "Configuración leída de " + archivo);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer " + archivo, e);
            }
        }
        cargado = true;
    }

    private static List<Path> candidatos() {
        List<Path> l = new ArrayList<>();
        String prop = System.getProperty("sialss.env");
        if (prop != null && !prop.isBlank()) {
            l.add(Paths.get(prop));
        }
        String env = System.getenv("SIALSS_ENV");
        if (env != null && !env.isBlank()) {
            l.add(Paths.get(env));
        }
        l.add(Paths.get("C:\\Despliegue", contexto, "sialss.env"));
        String base = System.getProperty("catalina.base");
        if (base != null) {
            l.add(Paths.get(base, "conf", contexto + ".env"));
        }
        return l;
    }
}
