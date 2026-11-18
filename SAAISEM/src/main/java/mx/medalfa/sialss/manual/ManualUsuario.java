package mx.medalfa.sialss.manual;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.config.Entorno;
import mx.medalfa.sialss.menu.MenuSialss;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.usuarios.PerfilDao;

/**
 * Manual de usuario filtrado por perfil.
 * <p>
 * Origen: los archivos Markdown de {@code SAAISEM/Manuales} (los mantiene el agente manual-usuario-sialss en
 * cada commit) que el WAR copia a {@code /WEB-INF/manuales}. Cada archivo es {@code <acceso>-<modulo>.md} y
 * cada opción del menú es una sección {@code ## <opción>}. Aquí se arma el manual con los mismos módulos y
 * opciones que el usuario ve en su menú ({@link MenuSialss#para}), en el mismo orden.
 */
public final class ManualUsuario {

    private static final Logger LOG = Logger.getLogger(ManualUsuario.class.getName());
    public static final String CARPETA = "/WEB-INF/manuales/";
    private static final String SIN_DOCUMENTAR = "Esta opción todavía no está documentada en el manual.";
    private static final String APAGADA = "Esta opción está apagada: sigue en el menú, pero ya no se usa.";

    private ManualUsuario() {
    }

    // ------------------------------------------------------------------ modelo

    public enum Tipo { TITULO, PARRAFO, LISTA, LISTA_NUMERADA, TABLA, NOTA }

    /** Un bloque de texto: título (con nivel), párrafo, lista, tabla (primera fila = encabezado) o nota. */
    public record Bloque(Tipo tipo, int nivel, List<String> lineas, List<List<String>> filas) {
        static Bloque de(Tipo t, String texto) {
            return new Bloque(t, 0, List.of(texto), List.of());
        }
    }

    /** Pedazo de texto en línea: normal, **negrita** o `código`. */
    public record Segmento(String texto, boolean negrita, boolean codigo) {
    }

    public record Seccion(String titulo, List<Bloque> bloques) {
    }

    public record Capitulo(String titulo, List<Bloque> introduccion, List<Seccion> secciones) {
    }

    /** El manual ya filtrado: generalidades y un capítulo por módulo del menú del usuario. */
    public record Manual(String acceso, String perfil, String nombrePerfil, String version,
                         List<Seccion> generales, List<Capitulo> capitulos) {
    }

    /** Archivo Markdown leído: título, bloques antes del primer ## y secciones por título normalizado. */
    private record Archivo(String titulo, List<Bloque> introduccion, Map<String, Seccion> secciones) {
    }

    // ------------------------------------------------------------------ armado

    /** Arma el manual de lo que ve este perfil (y este usuario, si se da, con sus permisos y bloqueos). */
    public static Manual para(ServletContext ctx, Acceso acceso, String perfil, Integer idUsuario) {
        Archivo portada = leer(ctx, "README.md");
        List<Seccion> generales = new ArrayList<>();
        if (portada != null) {
            for (Seccion s : portada.secciones().values()) {
                String t = clave(s.titulo());
                if (!t.startsWith("indice")) {
                    generales.add(s);
                }
            }
        }
        List<Capitulo> capitulos = new ArrayList<>();
        Map<String, Archivo> cache = new LinkedHashMap<>();
        for (MenuSialss.Modulo m : MenuSialss.para(acceso, perfil, idUsuario)) {
            String nombre = archivo(acceso, m.nombre());
            Archivo a = cache.computeIfAbsent(nombre, n -> leer(ctx, n));
            List<Seccion> secciones = new ArrayList<>();
            for (MenuSialss.Opcion o : m.opciones()) {
                Seccion s = a == null ? null : a.secciones().get(clave(o.etiqueta()));
                String nota = clave(o.etiqueta()).endsWith(" apagado") ? APAGADA : SIN_DOCUMENTAR;
                if (s == null) {
                    secciones.add(new Seccion(o.etiqueta(), List.of(Bloque.de(Tipo.NOTA, nota))));
                    continue;
                }
                List<Bloque> contenido = new ArrayList<>(s.bloques());
                Seccion igual = acceso == Acceso.ALMACEN ? null : equivalenteAlmacen(ctx, cache, o, s);
                if (igual != null) {
                    // "Igual que en Almacén": el usuario de Compras o Auditoría no tiene esa sección en su manual.
                    contenido.add(new Bloque(Tipo.TITULO, 3, List.of("Detalle (igual que en Almacén)"), List.of()));
                    for (Bloque b : igual.bloques()) {
                        String t = b.lineas().isEmpty() ? "" : clave(b.lineas().get(0));
                        if (!t.startsWith("quien la ve") && !t.startsWith("como llegar")) {
                            contenido.add(b);
                        }
                    }
                }
                secciones.add(new Seccion(o.etiqueta(), contenido));
            }
            capitulos.add(new Capitulo(titulo(m.nombre()), a == null ? List.of() : a.introduccion(), secciones));
        }
        return new Manual(titulo(acceso.name()), perfil, nombrePerfil(acceso, perfil), version(ctx),
                generales, capitulos);
    }

    /**
     * Sección de Almacén de la misma pantalla, cuando la de otro acceso remite a ella ("igual que en Almacén",
     * "los mismos que en Almacén"). null si no remite o no existe.
     */
    private static Seccion equivalenteAlmacen(ServletContext ctx, Map<String, Archivo> cache, MenuSialss.Opcion o,
                                              Seccion s) {
        boolean remite = s.bloques().stream().flatMap(b -> b.lineas().stream())
                .map(ManualUsuario::clave)
                .anyMatch(t -> t.contains("igual que en") && t.contains("almacen") || t.contains("mismos que en almacen"));
        if (!remite) {
            return null;
        }
        for (MenuSialss.Modulo m : MenuSialss.catalogo(Acceso.ALMACEN)) {
            for (MenuSialss.Opcion a : m.opciones()) {
                if (a.url().equalsIgnoreCase(o.url())) {
                    Archivo arch = cache.computeIfAbsent(archivo(Acceso.ALMACEN, m.nombre()), n -> leer(ctx, n));
                    Seccion r = arch == null ? null : arch.secciones().get(clave(a.etiqueta()));
                    if (r != null) {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    /** Perfiles que aparecen en el menú de un acceso (para que un administrador consulte el manual de otro). */
    public static List<String> perfiles(Acceso acceso) {
        TreeSet<String> todos = new TreeSet<>((x, y) -> {
            try {
                return Integer.compare(Integer.parseInt(x), Integer.parseInt(y));
            } catch (NumberFormatException e) {
                return x.compareTo(y);
            }
        });
        MenuSialss.catalogo(acceso).forEach(m -> m.opciones().forEach(o -> todos.addAll(o.perfiles())));
        return List.copyOf(todos);
    }

    /** Nombre del perfil (tb_tipusuario, el catálogo de perfiles de todos los accesos); si no está, cadena vacía. */
    public static String nombrePerfil(Acceso acceso, String perfil) {
        return perfil == null ? "" : nombresPerfiles().getOrDefault(perfil.trim(), "");
    }

    /** id → nombre de los perfiles de tb_tipusuario (vacío si no se puede leer). */
    public static Map<String, String> nombresPerfiles() {
        Map<String, String> m = new java.util.HashMap<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (PerfilDao.Perfil p : PerfilDao.lista(c)) {
                m.put(String.valueOf(p.id()), p.nombre());
            }
        } catch (Exception | LinkageError e) {
            LOG.log(Level.FINE, "Nombres de perfil", e);
        }
        return m;
    }

    /** Texto para elegir un perfil: su nombre, o "Perfil N" si no está en tb_tipusuario. */
    public static String etiquetaPerfil(Map<String, String> nombres, String perfil) {
        String n = nombres.get(perfil);
        return n == null || n.isBlank() ? "Perfil " + perfil : n;
    }

    /** Commit corto con el que se actualizó el manual por última vez. */
    private static String version(ServletContext ctx) {
        try (InputStream in = abrir(ctx, ".ultimo_commit")) {
            if (in != null) {
                String v = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
                return v.length() > 7 ? v.substring(0, 7) : v;
            }
        } catch (IOException e) {
            LOG.log(Level.FINE, "Versión del manual", e);
        }
        return "";
    }

    /** ALMACEN + CATÁLOGOS → almacen-catalogos.md */
    static String archivo(Acceso acceso, String modulo) {
        return clave(acceso.name()).replace(' ', '-') + "-" + clave(modulo).replace(' ', '-') + ".md";
    }

    /** CATÁLOGOS → Catálogos; ÓRDENES DE COMPRA → Órdenes de compra. */
    static String titulo(String mayusculas) {
        String t = mayusculas.toLowerCase(new Locale("es", "MX"));
        return t.isEmpty() ? t : Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }

    /** Sin acentos, minúsculas, solo letras/números y un espacio entre palabras. */
    static String clave(String t) {
        String s = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        return s.replaceAll("[^a-z0-9]+", " ").trim();
    }

    // ------------------------------------------------------------------ lectura de Markdown

    /**
     * Abre un archivo del manual: de la carpeta MANUALES_DIR del .env si está configurada (en local se ven los
     * cambios del agente sin volver a empaquetar), si no de WEB-INF/manuales dentro del WAR.
     */
    private static InputStream abrir(ServletContext ctx, String nombre) throws IOException {
        String dir = Entorno.get("MANUALES_DIR", "");
        if (dir != null && !dir.isBlank()) {
            Path p = Paths.get(dir.trim()).resolve(nombre);
            if (Files.isRegularFile(p)) {
                return Files.newInputStream(p);
            }
        }
        return ctx.getResourceAsStream(CARPETA + nombre);
    }

    private static Archivo leer(ServletContext ctx, String nombre) {
        try (InputStream in = abrir(ctx, nombre)) {
            if (in == null) {
                return null;
            }
            return analizar(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "No se pudo leer el manual " + nombre, e);
            return null;
        }
    }

    private static final Pattern TITULO = Pattern.compile("^(#{1,6})\\s+(.*?)\\s*#*\\s*$");
    private static final Pattern NUMERADA = Pattern.compile("^\\s*\\d+[.)]\\s+(.*)$");
    private static final Pattern VINETA = Pattern.compile("^\\s*[-*+]\\s+(.*)$");
    private static final Pattern SEPARADOR_TABLA = Pattern.compile("^\\s*\\|?\\s*:?-{2,}.*$");

    static Archivo analizar(String texto) {
        String titulo = "";
        List<Bloque> intro = new ArrayList<>();
        Map<String, Seccion> secciones = new LinkedHashMap<>();
        List<Bloque> actual = intro;
        String[] lineas = texto.replace("\r\n", "\n").split("\n");
        int i = 0;
        while (i < lineas.length) {
            String l = lineas[i];
            if (l.isBlank() || l.trim().matches("-{3,}|\\*{3,}")) {
                i++;
                continue;
            }
            Matcher t = TITULO.matcher(l);
            if (t.matches()) {
                int nivel = t.group(1).length();
                String txt = t.group(2);
                if (nivel == 1) {
                    titulo = txt;
                } else if (nivel == 2) {
                    actual = new ArrayList<>();
                    secciones.putIfAbsent(clave(txt), new Seccion(txt, actual));
                } else {
                    actual.add(new Bloque(Tipo.TITULO, nivel, List.of(txt), List.of()));
                }
                i++;
                continue;
            }
            if (l.trim().startsWith("|")) {
                List<List<String>> filas = new ArrayList<>();
                while (i < lineas.length && lineas[i].trim().startsWith("|")) {
                    if (!SEPARADOR_TABLA.matcher(lineas[i].replace("|", " ")).matches()) {
                        filas.add(celdas(lineas[i]));
                    }
                    i++;
                }
                actual.add(new Bloque(Tipo.TABLA, 0, List.of(), filas));
                continue;
            }
            if (l.trim().startsWith(">")) {
                StringBuilder sb = new StringBuilder();
                while (i < lineas.length && lineas[i].trim().startsWith(">")) {
                    sb.append(sb.length() > 0 ? " " : "").append(lineas[i].trim().replaceFirst("^>\\s?", ""));
                    i++;
                }
                actual.add(Bloque.de(Tipo.NOTA, sb.toString().trim()));
                continue;
            }
            Matcher n = NUMERADA.matcher(l);
            Matcher v = VINETA.matcher(l);
            if (n.matches() || v.matches()) {
                boolean numerada = n.matches();
                Pattern p = numerada ? NUMERADA : VINETA;
                List<String> items = new ArrayList<>();
                while (i < lineas.length && !lineas[i].isBlank()) {
                    Matcher m = p.matcher(lineas[i]);
                    Matcher otra = (numerada ? VINETA : NUMERADA).matcher(lineas[i]);
                    if (m.matches() && !(lineas[i].startsWith(" ") && !items.isEmpty())) {
                        items.add(m.group(1).trim());
                    } else if (!items.isEmpty() && (lineas[i].startsWith(" ") || !otra.matches())
                            && !TITULO.matcher(lineas[i]).matches() && !lineas[i].trim().startsWith("|")) {
                        // Continuación o subpunto: se agrega al último elemento.
                        String extra = lineas[i].trim().replaceFirst("^([-*+]|\\d+[.)])\\s+", "");
                        items.set(items.size() - 1, items.get(items.size() - 1) + (m.matches() || otra.matches() ? "; " : " ") + extra);
                    } else {
                        break;
                    }
                    i++;
                }
                actual.add(new Bloque(numerada ? Tipo.LISTA_NUMERADA : Tipo.LISTA, 0, items, List.of()));
                continue;
            }
            StringBuilder sb = new StringBuilder();
            while (i < lineas.length && !lineas[i].isBlank() && !TITULO.matcher(lineas[i]).matches()
                    && !lineas[i].trim().startsWith("|") && !lineas[i].trim().startsWith(">")
                    && !NUMERADA.matcher(lineas[i]).matches() && !VINETA.matcher(lineas[i]).matches()) {
                sb.append(sb.length() > 0 ? " " : "").append(lineas[i].trim());
                i++;
            }
            actual.add(Bloque.de(Tipo.PARRAFO, sb.toString()));
        }
        return new Archivo(titulo, intro, secciones);
    }

    private static List<String> celdas(String fila) {
        String f = fila.trim();
        if (f.startsWith("|")) {
            f = f.substring(1);
        }
        if (f.endsWith("|")) {
            f = f.substring(0, f.length() - 1);
        }
        List<String> r = new ArrayList<>();
        for (String c : f.split("(?<!\\\\)\\|", -1)) {
            r.add(c.trim().replace("\\|", "|"));
        }
        return r;
    }

    private static final Pattern EN_LINEA = Pattern.compile("\\*\\*(.+?)\\*\\*|`([^`]+)`|\\[([^\\]]+)]\\([^)]*\\)");

    /** Separa negritas y código; las ligas quedan como su texto (en el manual filtrado no hay a dónde ir). */
    public static List<Segmento> segmentos(String texto) {
        List<Segmento> r = new ArrayList<>();
        Matcher m = EN_LINEA.matcher(texto);
        int desde = 0;
        while (m.find()) {
            if (m.start() > desde) {
                r.add(new Segmento(texto.substring(desde, m.start()), false, false));
            }
            if (m.group(1) != null) {
                r.add(new Segmento(m.group(1).replace("`", ""), true, false));
            } else if (m.group(2) != null) {
                r.add(new Segmento(m.group(2), false, true));
            } else {
                r.add(new Segmento(m.group(3).replace("**", ""), false, false));
            }
            desde = m.end();
        }
        if (desde < texto.length()) {
            r.add(new Segmento(texto.substring(desde), false, false));
        }
        return r;
    }

    // ------------------------------------------------------------------ HTML

    /** Contenido del manual en HTML (sin encabezado de página), con índice al inicio. */
    public static String html(Manual manual) {
        StringBuilder sb = new StringBuilder();
        sb.append("<nav class=\"sialss-manual__indice\" aria-label=\"Contenido\"><h2>Contenido</h2><ol>");
        int c = 0;
        for (Capitulo cap : manual.capitulos()) {
            c++;
            sb.append("<li><a href=\"#m").append(c).append("\">").append(esc(cap.titulo())).append("</a><ol>");
            int s = 0;
            for (Seccion sec : cap.secciones()) {
                s++;
                sb.append("<li><a href=\"#m").append(c).append('-').append(s).append("\">")
                  .append(esc(sec.titulo())).append("</a></li>");
            }
            sb.append("</ol></li>");
        }
        sb.append("</ol></nav>");
        if (!manual.generales().isEmpty()) {
            sb.append("<section class=\"sialss-manual__capitulo\"><h2>Generalidades</h2>");
            for (Seccion s : manual.generales()) {
                sb.append("<h3>").append(esc(s.titulo())).append("</h3>");
                bloques(sb, s.bloques(), 1);
            }
            sb.append("</section>");
        }
        c = 0;
        for (Capitulo cap : manual.capitulos()) {
            c++;
            sb.append("<section class=\"sialss-manual__capitulo\"><h2 id=\"m").append(c).append("\">")
              .append(esc(cap.titulo())).append("</h2>");
            bloques(sb, cap.introduccion(), 1);
            int s = 0;
            for (Seccion sec : cap.secciones()) {
                s++;
                sb.append("<article class=\"sialss-manual__opcion\"><h3 id=\"m").append(c).append('-').append(s).append("\">")
                  .append(esc(sec.titulo())).append("</h3>");
                bloques(sb, sec.bloques(), 1);
                sb.append("</article>");
            }
            sb.append("</section>");
        }
        return sb.toString();
    }

    private static void bloques(StringBuilder sb, List<Bloque> bloques, int desplazar) {
        for (Bloque b : bloques) {
            switch (b.tipo()) {
                case TITULO -> {
                    int h = Math.min(6, b.nivel() + desplazar);
                    sb.append("<h").append(h).append('>').append(enLinea(b.lineas().get(0))).append("</h").append(h).append('>');
                }
                case PARRAFO -> sb.append("<p>").append(enLinea(b.lineas().get(0))).append("</p>");
                case NOTA -> sb.append("<p class=\"sialss-manual__nota\">").append(enLinea(b.lineas().get(0))).append("</p>");
                case LISTA, LISTA_NUMERADA -> {
                    String et = b.tipo() == Tipo.LISTA ? "ul" : "ol";
                    sb.append('<').append(et).append('>');
                    b.lineas().forEach(x -> sb.append("<li>").append(enLinea(x)).append("</li>"));
                    sb.append("</").append(et).append('>');
                }
                case TABLA -> {
                    sb.append("<div class=\"table-responsive\"><table class=\"table table-bordered table-condensed\">");
                    for (int f = 0; f < b.filas().size(); f++) {
                        String celda = f == 0 ? "th" : "td";
                        sb.append(f == 0 ? "<thead><tr>" : "<tr>");
                        b.filas().get(f).forEach(x -> sb.append('<').append(celda).append('>').append(enLinea(x))
                                .append("</").append(celda).append('>'));
                        sb.append(f == 0 ? "</tr></thead><tbody>" : "</tr>");
                    }
                    sb.append("</tbody></table></div>");
                }
            }
        }
    }

    private static String enLinea(String texto) {
        StringBuilder sb = new StringBuilder();
        for (Segmento s : segmentos(texto)) {
            String t = esc(s.texto());
            sb.append(s.negrita() ? "<strong>" + t + "</strong>" : s.codigo() ? "<code>" + t + "</code>" : t);
        }
        return sb.toString();
    }

    public static String esc(String t) {
        return t == null ? "" : t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
