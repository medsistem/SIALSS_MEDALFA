package mx.medalfa.sialss.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Menú vs roles: qué módulos y opciones ve cada perfil (F_TipUsu) en cada acceso.
 * <p>
 * El catálogo vive en {@code menu-roles.csv} (mismo paquete). El orden lo pone esta clase y es el mismo para
 * todos los perfiles: módulos según {@link #ORDEN_MODULOS} y, dentro de cada módulo, opciones en orden
 * alfabético. Así un perfil con menos permisos ve los mismos módulos en la misma posición, solo con menos opciones.
 */
public final class MenuSialss {

    /** Orden común de los módulos (los que no estén aquí van al final, en orden alfabético). */
    public static final List<String> ORDEN_MODULOS = List.of(
            "ENTRADA", "ÓRDENES DE COMPRA", "REMISIÓN", "INVENTARIO", "PDF",
            "CONSULTA", "REPORTE", "FARMACIA", "CATÁLOGOS", "USUARIO");

    public record Opcion(String etiqueta, String url, boolean ventana, Set<String> perfiles) {
        public boolean visiblePara(String perfil) {
            return perfil != null && perfiles.contains(perfil.trim());
        }
    }

    public record Modulo(String nombre, List<Opcion> opciones) {
    }

    private static final String ARCHIVO = "menu-roles.csv";
    private static final Map<Acceso, List<Modulo>> CATALOGO = cargar();

    private MenuSialss() {
    }

    /** Catálogo completo de un acceso, ya ordenado (para reportes y pruebas). */
    public static List<Modulo> catalogo(Acceso acceso) {
        return CATALOGO.getOrDefault(acceso, List.of());
    }

    /** Módulos y opciones que ve un perfil; los módulos sin opciones para ese perfil no aparecen. */
    public static List<Modulo> para(Acceso acceso, String perfil) {
        List<Modulo> resultado = new ArrayList<>();
        for (Modulo m : catalogo(acceso)) {
            List<Opcion> visibles = m.opciones().stream().filter(o -> o.visiblePara(perfil)).toList();
            if (!visibles.isEmpty()) {
                resultado.add(new Modulo(m.nombre(), visibles));
            }
        }
        return resultado;
    }

    /** Las opciones del menú (los {@code <li class="dropdown">}) con el marcado de Bootstrap que espera sialss-menu.js. */
    public static String html(Acceso acceso, String perfil, String contexto) {
        StringBuilder sb = new StringBuilder();
        for (Modulo m : para(acceso, perfil)) {
            sb.append("<li class=\"dropdown\">\n")
              .append("    <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">")
              .append(escapar(m.nombre())).append("<b class=\"caret\"></b></a>\n")
              .append("    <ul class=\"dropdown-menu\">\n");
            for (Opcion o : m.opciones()) {
                String destino = escapar(contexto + "/" + o.url());
                sb.append("        <li><a href=\"").append(destino).append('"');
                if (o.ventana()) {
                    sb.append(" onclick=\"window.open(this.href, '', 'width=1200,height=800,left=50,top=50,toolbar=no'); return false;\"");
                }
                sb.append('>').append(escapar(o.etiqueta())).append("</a></li>\n");
            }
            sb.append("    </ul>\n</li>\n");
        }
        return sb.toString();
    }

    private static Map<Acceso, List<Modulo>> cargar() {
        Map<Acceso, Map<String, List<Opcion>>> crudo = new EnumMap<>(Acceso.class);
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
                crudo.computeIfAbsent(Acceso.valueOf(c[0].trim()), k -> new LinkedHashMap<>())
                     .computeIfAbsent(c[1].trim(), k -> new ArrayList<>())
                     .add(new Opcion(c[2].trim(), c[3].trim(), "1".equals(c[4].trim()), Set.copyOf(perfiles)));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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

    private static String escapar(String t) {
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
