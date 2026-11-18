package mx.medalfa.sialss.proyectos;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.menu.MenuSialss;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Alta de proyecto (ProyectoNuevo.jsp): GET {@code accion=base} (proyectos y responsables a precargar),
 * GET {@code accion=previa} (qué se va a crear, con errores), POST {@code accion=alta}.
 * Solo para quien tiene la opción "Alta de Proyecto" en su menú.
 */
@WebServlet("/ProyectoNuevo")
public class ProyectoNuevoServlet extends HttpServlet {

    public static final String PAGINA = "ProyectoNuevo.jsp";
    private static final Logger LOG = Logger.getLogger(ProyectoNuevoServlet.class.getName());
    private static final String TOKEN = "sialss.proyecto.token";
    private static final Gson GSON = new Gson();

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

    public static boolean autorizado(HttpSession s) {
        return s != null && PoliticaSesion.esAdministrador(s) && MenuSialss.tieneOpcion(PoliticaSesion.acceso(s), PoliticaSesion.perfil(s),
                MenuSialss.idUsuario(s.getAttribute("IdUsu")), PAGINA);
    }

    private static AltaProyecto.Datos datos(HttpServletRequest r) {
        return new AltaProyecto.Datos(r.getParameter("nombre"), r.getParameter("abreviatura"), r.getParameter("direccion"),
                r.getParameter("rfc"), r.getParameter("encabezado"), r.getParameter("contrato"), r.getParameter("qfbMedicamento"),
                r.getParameter("qfbDispositivos"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        if (!autorizado(req.getSession(false))) {
            json(res, 403, Map.of("error", "No tiene permiso para dar de alta proyectos."));
            return;
        }
        try {
            switch (String.valueOf(req.getParameter("accion"))) {
                case "base" -> {
                    Map<String, Object> r = new LinkedHashMap<>();
                    r.put("responsables", AltaProyecto.responsablesBase());
                    r.put("proyectos", proyectos());
                    json(res, 200, r);
                }
                case "previa" -> json(res, 200, AltaProyecto.planear(datos(req)));
                case "sugerir" -> json(res, 200, Map.of("abreviatura", AltaProyecto.sugerirAbreviatura(req.getParameter("nombre"))));
                default -> json(res, 400, Map.of("error", "Acción no válida."));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Alta de proyecto", e);
            json(res, 500, Map.of("error", "No se pudo leer la base de datos."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession(false);
        if (!autorizado(s)) {
            json(res, 403, Map.of("error", "No tiene permiso para dar de alta proyectos."));
            return;
        }
        if (!token(s).equals(req.getHeader("X-SIALSS-Token"))) {
            json(res, 403, Map.of("error", "La página caducó. Vuelva a abrirla."));
            return;
        }
        try {
            AltaProyecto.Plan p = AltaProyecto.ejecutar(datos(req));
            if (!p.valido()) {
                json(res, 409, Map.of("error", "Revise los datos.", "plan", p));
                return;
            }
            LOG.info(() -> "Proyecto dado de alta por " + s.getAttribute("Usuario") + ": " + p.id() + " " + p.nomenclatura());
            json(res, 200, Map.of("mensaje", "Se dio de alta el proyecto " + p.id() + " (" + p.nomenclatura() + ").", "plan", p,
                    "proyectos", proyectos()));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Alta de proyecto", e);
            json(res, 500, Map.of("error", mx.medalfa.sialss.config.ErrorBase.mensaje(e)));
        }
    }

    private static List<Map<String, String>> proyectos() throws SQLException {
        List<Map<String, String>> r = new ArrayList<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (Fila f : Consultas.lista(c, "SELECT F_Id, F_DesProy, F_Nomenclatura, F_Campo FROM tb_proyectos ORDER BY F_Id")) {
                r.add(Map.of("id", f.get(1), "nombre", f.get(2), "nomenclatura", String.valueOf(f.get(3)), "campo", String.valueOf(f.get(4))));
            }
        }
        return r;
    }

    private static void json(HttpServletResponse res, int estado, Object cuerpo) throws IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }
}
