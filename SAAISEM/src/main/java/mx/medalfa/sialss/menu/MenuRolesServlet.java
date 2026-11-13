package mx.medalfa.sialss.menu;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Servicio JSON de la pantalla MenuRoles.jsp: asignar opciones del menú por perfil y permitir o bloquear opciones
 * por usuario. Solo lo usa quien tenga la opción "Menús por Perfil y Usuario" en su propio menú.
 */
@WebServlet("/MenuRoles")
public class MenuRolesServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MenuRolesServlet.class.getName());
    private static final String TOKEN = "sialss.menuRoles.token";
    private static final Gson GSON = new Gson();

    /** Token contra envíos desde otros sitios; la página lo pone en el encabezado X-SIALSS-Token. */
    public static String token(HttpSession s) {
        Object t = s.getAttribute(TOKEN);
        if (t == null) {
            byte[] b = new byte[24];
            new SecureRandom().nextBytes(b);
            t = HexFormat.of().formatHex(b);
            s.setAttribute(TOKEN, t);
        }
        return (String) t;
    }

    /** ¿La sesión puede administrar el menú? (la usa también MenuRoles.jsp). */
    public static boolean autorizado(HttpSession s) {
        if (s == null) {
            return false;
        }
        return MenuSialss.administra(PoliticaSesion.acceso(s), (String) s.getAttribute("Tipo"),
                MenuSialss.idUsuario(s.getAttribute("IdUsu")));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (!autorizado(s)) {
            responder(res, 403, Map.of("error", "No tiene permiso para administrar el menú."));
            return;
        }
        try {
            Acceso acceso = acceso(req);
            switch (String.valueOf(req.getParameter("accion"))) {
                case "datos" -> responder(res, 200, datos(acceso, s));
                case "usuario" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Map<String, String> exc = new TreeMap<>();
                    MenuSialss.excepciones(acceso, id).forEach((k, v) -> exc.put(String.valueOf(k), String.valueOf(v)));
                    responder(res, 200, Map.of("excepciones", exc));
                }
                case "bitacora" -> {
                    List<Map<String, String>> filas = new ArrayList<>();
                    if (MenuSialss.usaBase()) {
                        for (Fila f : MenuDao.bitacora(30)) {
                            filas.add(Map.of("fecha", f.get(1), "quien", f.get(2), "acceso", f.get(3), "tipo", f.get(4),
                                    "destino", f.get(5), "detalle", f.get(6)));
                        }
                    }
                    responder(res, 200, Map.of("cambios", filas));
                }
                default -> responder(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (IllegalArgumentException e) {
            responder(res, 400, Map.of("error", "Datos no válidos."));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "MenuRoles", e);
            responder(res, 500, Map.of("error", "No se pudo leer la base de datos."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(false);
        if (!autorizado(s)) {
            responder(res, 403, Map.of("error", "No tiene permiso para administrar el menú."));
            return;
        }
        if (!token(s).equals(req.getHeader("X-SIALSS-Token"))) {
            responder(res, 403, Map.of("error", "La página caducó. Vuelva a abrirla."));
            return;
        }
        if (!MenuSialss.usaBase()) {
            responder(res, 409, Map.of("error", "Faltan las tablas de menú en la base (despliegue/menu_roles_tablas.sql)."));
            return;
        }
        String quien = String.valueOf(s.getAttribute("Usuario"));
        try {
            Acceso acceso = acceso(req);
            switch (String.valueOf(req.getParameter("accion"))) {
                case "guardarPerfil" -> responder(res, 200, guardarPerfil(req, s, acceso, quien));
                case "guardarUsuario" -> responder(res, 200, guardarUsuario(req, s, acceso, quien));
                default -> responder(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (Rechazo e) {
            responder(res, 409, Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            responder(res, 400, Map.of("error", "Datos no válidos."));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "MenuRoles", e);
            responder(res, 500, Map.of("error", "No se pudo guardar. No se cambió nada."));
        }
    }

    private static final class Rechazo extends RuntimeException {
        Rechazo(String m) {
            super(m);
        }
    }

    private static Map<String, Object> datos(Acceso acceso, HttpSession s) throws SQLException {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("usaBase", MenuSialss.usaBase());
        r.put("acceso", acceso.name());
        List<Map<String, Object>> modulos = new ArrayList<>();
        for (MenuSialss.Modulo m : MenuSialss.catalogo(acceso)) {
            List<Map<String, Object>> ops = new ArrayList<>();
            for (MenuSialss.Opcion o : m.opciones()) {
                ops.add(Map.of("id", o.id(), "etiqueta", o.etiqueta(), "url", o.url(), "perfiles", o.perfiles()));
            }
            modulos.add(Map.of("nombre", m.nombre(), "opciones", ops));
        }
        r.put("modulos", modulos);
        r.put("perfiles", MenuDao.perfiles(acceso));
        try {
            r.put("usuarios", MenuDao.usuarios(acceso));
        } catch (SQLException e) {
            r.put("usuarios", List.of());
            r.put("errorUsuarios", "Este acceso no tiene tabla de usuarios en la base (" + acceso.tabla + ").");
        }
        r.put("yo", Map.of("acceso", String.valueOf(PoliticaSesion.acceso(s)), "perfil", String.valueOf(s.getAttribute("Tipo")),
                "id", String.valueOf(s.getAttribute("IdUsu"))));
        return r;
    }

    private static Map<String, Object> guardarPerfil(HttpServletRequest req, HttpSession s, Acceso acceso, String quien)
            throws SQLException {
        int perfil = Integer.parseInt(req.getParameter("perfil"));
        Map<Integer, MenuSialss.Opcion> catalogo = indice(acceso);
        Set<Integer> nuevas = ids(req.getParameter("opciones"), catalogo);
        if (esMio(s, acceso) && String.valueOf(perfil).equals(String.valueOf(s.getAttribute("Tipo")))) {
            Integer admin = idAdministracion(catalogo);
            Integer yo = MenuSialss.idUsuario(s.getAttribute("IdUsu"));
            Character mio = admin == null ? null : MenuSialss.excepciones(acceso, yo).get(admin);
            if (admin != null && !nuevas.contains(admin) && (mio == null || mio != MenuSialss.PERMITIR)) {
                throw new Rechazo("No puede quitarle a su propio perfil la opción \"Menús por Perfil y Usuario\": "
                        + "se quedaría sin acceso a esta pantalla.");
            }
        }
        StringJoiner detalle = new StringJoiner("; ");
        for (MenuSialss.Opcion o : catalogo.values()) {
            boolean antes = o.delPerfil(String.valueOf(perfil));
            boolean despues = nuevas.contains(o.id());
            if (antes != despues) {
                detalle.add((despues ? "+ " : "- ") + o.modulo() + " / " + o.etiqueta());
            }
        }
        if (detalle.length() == 0) {
            return Map.of("mensaje", "No había cambios que guardar.");
        }
        MenuDao.guardarPerfil(acceso, perfil, nuevas, quien, detalle.toString());
        return Map.of("mensaje", "Se guardó el menú del perfil " + perfil + ".");
    }

    private static Map<String, Object> guardarUsuario(HttpServletRequest req, HttpSession s, Acceso acceso, String quien)
            throws SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        MenuDao.Usuario u = MenuDao.usuario(acceso, id);
        if (u == null) {
            throw new IllegalArgumentException("usuario");
        }
        Map<Integer, MenuSialss.Opcion> catalogo = indice(acceso);
        Map<Integer, Character> nuevas = new TreeMap<>();
        String texto = req.getParameter("permisos");
        if (texto != null && !texto.isBlank()) {
            for (String par : texto.split(",")) {
                String[] kv = par.trim().split(":");
                int op = Integer.parseInt(kv[0]);
                char permiso = kv[1].trim().charAt(0);
                if (!catalogo.containsKey(op) || (permiso != MenuSialss.PERMITIR && permiso != MenuSialss.BLOQUEAR)) {
                    throw new IllegalArgumentException("permiso");
                }
                nuevas.put(op, permiso);
            }
        }
        Integer admin = idAdministracion(catalogo);
        if (esMio(s, acceso) && Integer.valueOf(id).equals(MenuSialss.idUsuario(s.getAttribute("IdUsu")))
                && admin != null && Character.valueOf(MenuSialss.BLOQUEAR).equals(nuevas.get(admin))) {
            throw new Rechazo("No puede bloquearse a sí mismo la opción \"Menús por Perfil y Usuario\".");
        }
        StringJoiner detalle = new StringJoiner("; ");
        Map<Integer, Character> antes = MenuSialss.excepciones(acceso, id);
        for (MenuSialss.Opcion o : catalogo.values()) {
            Character a = antes.get(o.id());
            Character d = nuevas.get(o.id());
            if (a == null ? d != null : !a.equals(d)) {
                detalle.add(nombrePermiso(d) + ": " + o.modulo() + " / " + o.etiqueta());
            }
        }
        if (detalle.length() == 0) {
            return Map.of("mensaje", "No había cambios que guardar.");
        }
        MenuDao.guardarUsuario(acceso, id, nuevas, quien, u.usuario() + " (" + id + ")", detalle.toString());
        return Map.of("mensaje", "Se guardaron los permisos de " + u.usuario() + ".");
    }

    private static String nombrePermiso(Character p) {
        if (p == null) {
            return "Según perfil";
        }
        return p == MenuSialss.PERMITIR ? "Permitir" : "Bloquear";
    }

    private static boolean esMio(HttpSession s, Acceso acceso) {
        return acceso == PoliticaSesion.acceso(s);
    }

    private static Integer idAdministracion(Map<Integer, MenuSialss.Opcion> catalogo) {
        return catalogo.values().stream().filter(o -> MenuSialss.PAGINA_ADMINISTRACION.equals(o.url()))
                .map(MenuSialss.Opcion::id).findFirst().orElse(null);
    }

    private static Map<Integer, MenuSialss.Opcion> indice(Acceso acceso) {
        Map<Integer, MenuSialss.Opcion> r = new LinkedHashMap<>();
        MenuSialss.catalogo(acceso).forEach(m -> m.opciones().forEach(o -> r.put(o.id(), o)));
        return r;
    }

    private static Set<Integer> ids(String texto, Map<Integer, MenuSialss.Opcion> catalogo) {
        Set<Integer> r = new LinkedHashSet<>();
        if (texto != null && !texto.isBlank()) {
            for (String p : texto.split(",")) {
                int id = Integer.parseInt(p.trim());
                if (!catalogo.containsKey(id)) {
                    throw new IllegalArgumentException("opcion");
                }
                r.add(id);
            }
        }
        return r;
    }

    private static Acceso acceso(HttpServletRequest req) {
        return Acceso.valueOf(String.valueOf(req.getParameter("acceso")));
    }

    private static void responder(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
