package mx.medalfa.sialss.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Menú vs roles: qué módulos y opciones ve cada perfil (F_TipUsu) y cada usuario en cada acceso.
 * <p>
 * Origen: las tablas {@code tb_menu_opcion}, {@code tb_menu_perfil} y {@code tb_menu_usuario}
 * (script {@code despliegue/menu_roles_tablas.sql}). Mientras no existan se usa {@code menu-roles.csv}
 * (mismo paquete), sin excepciones por usuario.
 * <p>
 * Orden, igual para todos: módulos según {@link #ORDEN_MODULOS} y, dentro de cada módulo, opciones en orden
 * alfabético. Un usuario ve una opción si su perfil la tiene o si se le permitió (P), salvo que se le haya
 * bloqueado (B). Un bloqueo también niega la página (ver {@link #paginaBloqueada}).
 */
public final class MenuSialss {

    private static final Logger LOG = Logger.getLogger(MenuSialss.class.getName());

    /** Orden común de los módulos (los que no estén aquí van al final, en orden alfabético). */
    public static final List<String> ORDEN_MODULOS = List.of(
            "ENTRADA", "ÓRDENES DE COMPRA", "REMISIÓN", "INVENTARIO", "PDF",
            "CONSULTA", "REPORTE", "FARMACIA", "CATÁLOGOS", "USUARIO");

    /** Página de administración de este catálogo (sirve también para saber quién puede administrarlo). */
    public static final String PAGINA_ADMINISTRACION = "MenuRoles.jsp";

    public static final char PERMITIR = 'P';
    public static final char BLOQUEAR = 'B';

    public record Opcion(int id, String modulo, String etiqueta, String url, boolean ventana, Set<String> perfiles) {
        public boolean delPerfil(String perfil) {
            return perfil != null && perfiles.contains(perfil.trim());
        }
    }

    public record Modulo(String nombre, List<Opcion> opciones) {
    }

    /** Todo lo cargado de una vez: catálogo ordenado y excepciones (acceso → usuario → opción → P/B). */
    private record Datos(Map<Acceso, List<Modulo>> modulos, Map<Acceso, Map<Integer, Map<Integer, Character>>> excepciones,
                         boolean deBase) {
    }

    private static final String ARCHIVO = "menu-roles.csv";
    private static volatile Datos datos;

    private MenuSialss() {
    }

    private static Datos datos() {
        Datos d = datos;
        if (d == null) {
            synchronized (MenuSialss.class) {
                if (datos == null) {
                    datos = cargar();
                }
                d = datos;
            }
        }
        return d;
    }

    /** Vuelve a leer el catálogo (después de guardar cambios). */
    public static synchronized void recargar() {
        datos = cargar();
    }

    /** true si el menú sale de las tablas; false si todavía sale de menu-roles.csv. */
    public static boolean usaBase() {
        return datos().deBase();
    }

    /** Catálogo completo de un acceso, ya ordenado. */
    public static List<Modulo> catalogo(Acceso acceso) {
        return datos().modulos().getOrDefault(acceso, List.of());
    }

    /** Excepciones de un usuario en un acceso: id de opción → P (permitir) o B (bloquear). */
    public static Map<Integer, Character> excepciones(Acceso acceso, Integer idUsuario) {
        if (idUsuario == null) {
            return Map.of();
        }
        return datos().excepciones().getOrDefault(acceso, Map.of()).getOrDefault(idUsuario, Map.of());
    }

    /** ¿Ve el usuario esta opción? Perfil o permiso explícito, y sin bloqueo. */
    public static boolean visible(Opcion o, String perfil, Map<Integer, Character> excepciones) {
        Character e = excepciones.get(o.id());
        if (e != null && e == BLOQUEAR) {
            return false;
        }
        return o.delPerfil(perfil) || (e != null && e == PERMITIR);
    }

    /** Módulos y opciones que ve un perfil (sin excepciones por usuario). */
    public static List<Modulo> para(Acceso acceso, String perfil) {
        return para(acceso, perfil, null);
    }

    /** Módulos y opciones que ve un usuario; los módulos sin opciones visibles no aparecen. */
    public static List<Modulo> para(Acceso acceso, String perfil, Integer idUsuario) {
        Map<Integer, Character> exc = excepciones(acceso, idUsuario);
        List<Modulo> resultado = new ArrayList<>();
        for (Modulo m : catalogo(acceso)) {
            List<Opcion> visibles = m.opciones().stream().filter(o -> visible(o, perfil, exc)).toList();
            if (!visibles.isEmpty()) {
                resultado.add(new Modulo(m.nombre(), visibles));
            }
        }
        return resultado;
    }

    /** ¿Puede este usuario abrir la página de administración del menú? */
    public static boolean administra(Acceso acceso, String perfil, Integer idUsuario) {
        return tieneOpcion(acceso, perfil, idUsuario, PAGINA_ADMINISTRACION);
    }

    /** ¿Tiene el usuario en su menú la opción que lleva a esta página? (para proteger pantallas de administración). */
    public static boolean tieneOpcion(Acceso acceso, String perfil, Integer idUsuario, String url) {
        return acceso != null && para(acceso, perfil, idUsuario).stream()
                .flatMap(m -> m.opciones().stream())
                .anyMatch(o -> url.equals(o.url()));
    }

    /**
     * ¿Se le negó esta página al usuario? Solo cuando la página corresponde a opciones del menú que se le
     * bloquearon explícitamente y ninguna otra opción visible lleva a la misma página. Las páginas que no están
     * en el menú, o que su perfil simplemente no tiene, no se niegan (pueden abrirse desde otras pantallas).
     */
    public static boolean paginaBloqueada(Acceso acceso, String perfil, Integer idUsuario, String ruta,
                                          String consulta) {
        Map<Integer, Character> exc = excepciones(acceso, idUsuario);
        if (exc.isEmpty() || !exc.containsValue(BLOQUEAR)) {
            return false;
        }
        boolean bloqueada = false;
        for (Modulo m : catalogo(acceso)) {
            for (Opcion o : m.opciones()) {
                if (!coincide(o.url(), ruta, consulta)) {
                    continue;
                }
                if (visible(o, perfil, exc)) {
                    return false;
                }
                Character e = exc.get(o.id());
                bloqueada |= e != null && e == BLOQUEAR;
            }
        }
        return bloqueada;
    }

    /** Compara la ruta y, si la opción lleva parámetros (?Accion=...), que la consulta los traiga con ese valor. */
    private static boolean coincide(String url, String ruta, String consulta) {
        String r = ruta.startsWith("/") ? ruta.substring(1) : ruta;
        int q = url.indexOf('?');
        if (!(q < 0 ? url : url.substring(0, q)).equalsIgnoreCase(r)) {
            return false;
        }
        if (q < 0) {
            return true;
        }
        Set<String> pedidos = new HashSet<>();
        if (consulta != null) {
            for (String par : consulta.split("&")) {
                pedidos.add(URLDecoder.decode(par, StandardCharsets.UTF_8));
            }
        }
        for (String par : url.substring(q + 1).split("&")) {
            if (!pedidos.contains(par.contains("=") ? par : par + "=") && !pedidos.contains(par)) {
                return false;
            }
        }
        return true;
    }

    /** Las opciones del menú (los {@code <li class="dropdown">}) con el marcado de Bootstrap que espera sialss-menu.js. */
    public static String html(Acceso acceso, String perfil, String contexto) {
        return html(acceso, perfil, null, contexto);
    }

    public static String html(Acceso acceso, String perfil, Integer idUsuario, String contexto) {
        StringBuilder sb = new StringBuilder();
        for (Modulo m : para(acceso, perfil, idUsuario)) {
            sb.append("<li class=\"dropdown\">\n")
              .append("    <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">")
              .append(escapar(m.nombre())).append("<b class=\"caret\"></b></a>\n")
              .append("    <ul class=\"dropdown-menu\">\n");
            for (Opcion o : m.opciones()) {
                sb.append("        <li><a href=\"").append(escapar(contexto + "/" + o.url())).append('"');
                if (o.ventana()) {
                    sb.append(" onclick=\"window.open(this.href, '', 'width=1200,height=800,left=50,top=50,toolbar=no'); return false;\"");
                }
                sb.append('>').append(escapar(o.etiqueta())).append("</a></li>\n");
            }
            sb.append("    </ul>\n</li>\n");
        }
        return sb.toString();
    }

    /** Lee el id de usuario guardado en sesión ("IdUsu") sin fallar si no es número. */
    public static Integer idUsuario(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(String.valueOf(valor).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------ carga

    private record Crudo(int id, Acceso acceso, String modulo, String etiqueta, String url, boolean ventana,
                         Set<String> perfiles) {
    }

    private static Datos cargar() {
        try {
            return desdeBase();
        } catch (SQLException e) {
            if ("42S02".equals(e.getSQLState())) {
                LOG.info("Tablas de menú no creadas (despliegue/menu_roles_tablas.sql): se usa " + ARCHIVO);
            } else {
                LOG.log(Level.WARNING, "No se pudo leer el menú de la base; se usa " + ARCHIVO, e);
            }
        } catch (RuntimeException | LinkageError e) {
            // Sin conexión configurada o sin el pool de Tomcat (p. ej. herramientas fuera del contenedor).
            LOG.log(Level.FINE, "Menú desde " + ARCHIVO + ": " + e.getMessage());
        }
        return desdeArchivo();
    }

    private static Datos desdeBase() throws SQLException {
        List<Crudo> filas = new ArrayList<>();
        Map<Acceso, Map<Integer, Map<Integer, Character>>> excepciones = new EnumMap<>(Acceso.class);
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            Map<Integer, Set<String>> perfiles = new HashMap<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT F_IdOpcion, F_TipUsu FROM tb_menu_perfil");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    perfiles.computeIfAbsent(rs.getInt(1), k -> new LinkedHashSet<>()).add(rs.getString(2));
                }
            }
            Map<Integer, Acceso> accesoDe = new HashMap<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT F_IdOpcion, F_Acceso, F_Modulo, F_Opcion, F_Url, F_Ventana FROM tb_menu_opcion WHERE F_Status = 'A'");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Acceso a = accesoONulo(rs.getString(2));
                    if (a == null) {
                        continue;
                    }
                    int id = rs.getInt(1);
                    accesoDe.put(id, a);
                    filas.add(new Crudo(id, a, rs.getString(3).trim(), rs.getString(4).trim(), rs.getString(5).trim(),
                            rs.getInt(6) == 1, Set.copyOf(perfiles.getOrDefault(id, Set.of()))));
                }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT F_IdOpcion, F_IdUsu, F_Permiso FROM tb_menu_usuario");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Acceso a = accesoDe.get(rs.getInt(1));
                    String permiso = rs.getString(3);
                    if (a == null || permiso == null || permiso.isEmpty()) {
                        continue;
                    }
                    excepciones.computeIfAbsent(a, k -> new HashMap<>())
                               .computeIfAbsent(rs.getInt(2), k -> new HashMap<>())
                               .put(rs.getInt(1), permiso.charAt(0));
                }
            }
        }
        return new Datos(ordenar(filas), excepciones, true);
    }

    private static Datos desdeArchivo() {
        List<Crudo> filas = new ArrayList<>();
        try (InputStream in = MenuSialss.class.getResourceAsStream(ARCHIVO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + ARCHIVO + " junto a " + MenuSialss.class.getName());
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            boolean encabezado = true;
            int numero = 0;
            while ((linea = r.readLine()) != null) {
                numero++;
                if (linea.isBlank() || linea.startsWith("#")) {
                    continue;
                }
                if (encabezado) {
                    encabezado = false;
                    continue;
                }
                String[] c = linea.split(";", -1);
                if (c.length < 6) {
                    throw new IllegalStateException(ARCHIVO + " línea " + numero + ": se esperaban 6 columnas");
                }
                Set<String> perfiles = new LinkedHashSet<>();
                for (String p : c[5].split(",")) {
                    if (!p.isBlank()) {
                        perfiles.add(p.trim());
                    }
                }
                // Sin tablas no hay ids reales: se numeran en negativo para que nunca choquen con la base.
                filas.add(new Crudo(-numero, Acceso.valueOf(c[0].trim()), c[1].trim(), c[2].trim(), c[3].trim(),
                        "1".equals(c[4].trim()), Set.copyOf(perfiles)));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO, e);
        }
        return new Datos(ordenar(filas), new EnumMap<>(Acceso.class), false);
    }

    private static Acceso accesoONulo(String nombre) {
        try {
            return Acceso.valueOf(nombre.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Map<Acceso, List<Modulo>> ordenar(List<Crudo> filas) {
        Map<Acceso, Map<String, List<Opcion>>> crudo = new EnumMap<>(Acceso.class);
        for (Crudo f : filas) {
            crudo.computeIfAbsent(f.acceso(), k -> new LinkedHashMap<>())
                 .computeIfAbsent(f.modulo(), k -> new ArrayList<>())
                 .add(new Opcion(f.id(), f.modulo(), f.etiqueta(), f.url(), f.ventana(), f.perfiles()));
        }
        // Alfabético sin distinguir acentos ni mayúsculas, con los espacios contando ("Carga R..." antes que "Cargar C...").
        Comparator<String> alfabetico = Comparator.comparing(MenuSialss::claveOrden);
        Comparator<String> porModulo = Comparator
                .comparingInt((String m) -> ORDEN_MODULOS.contains(m) ? ORDEN_MODULOS.indexOf(m) : ORDEN_MODULOS.size())
                .thenComparing(alfabetico);
        Comparator<Opcion> porOpcion = Comparator.comparing(Opcion::etiqueta, alfabetico).thenComparing(Opcion::url);
        Map<Acceso, List<Modulo>> ordenado = new EnumMap<>(Acceso.class);
        crudo.forEach((acceso, modulos) -> ordenado.put(acceso, modulos.keySet().stream()
                .sorted(porModulo)
                .map(m -> new Modulo(m, modulos.get(m).stream().sorted(porOpcion).toList()))
                .toList()));
        return ordenado;
    }

    private static String claveOrden(String t) {
        return Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    static String escapar(String t) {
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
