package mx.medalfa.sialss.manual;

import com.lowagie.text.DocumentException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.menu.MenuSialss;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Manual de usuario en PDF (iText 2.1.7): GET /ManualUsuario?formato=pdf[&perfil=N].
 * La versión web es ManualUsuario.jsp. Cada usuario recibe solo los módulos de su menú;
 * los administradores de Almacén pueden pedir el de otro perfil con {@code perfil}.
 */
@WebServlet("/ManualUsuario")
public class ManualUsuarioServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ManualUsuarioServlet.class.getName());

    /** Acceso, perfil e id de usuario con que se arma el manual; null si no hay sesión válida. */
    public record Solicitud(Acceso acceso, String perfil, Integer idUsuario, boolean otroPerfil) {
    }

    /** Lo usan este servlet y ManualUsuario.jsp, para que web y PDF muestren lo mismo. */
    public static Solicitud solicitud(HttpSession s, String perfilPedido) {
        Acceso acceso = PoliticaSesion.acceso(s);
        if (acceso == null || s.getAttribute("nombre") == null) {
            return null;
        }
        String propio = PoliticaSesion.perfil(s);
        Integer id = MenuSialss.idUsuario(s.getAttribute("IdUsu"));
        if (perfilPedido != null && !perfilPedido.isBlank() && !perfilPedido.trim().equals(propio)
                && PoliticaSesion.esAdministrador(s) && ManualUsuario.perfiles(acceso).contains(perfilPedido.trim())) {
            // Manual de otro perfil: sin los permisos ni bloqueos propios del administrador.
            return new Solicitud(acceso, perfilPedido.trim(), null, true);
        }
        return new Solicitud(acceso, propio, id, false);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        Solicitud sol = s == null ? null : solicitud(s, req.getParameter("perfil"));
        if (sol == null) {
            res.sendRedirect(req.getContextPath() + "/indexalmacen.jsp");
            return;
        }
        if (!"pdf".equals(req.getParameter("formato"))) {
            res.sendRedirect(req.getContextPath() + "/ManualUsuario.jsp"
                    + (sol.otroPerfil() ? "?perfil=" + sol.perfil() : ""));
            return;
        }
        ManualUsuario.Manual manual = ManualUsuario.para(getServletContext(), sol.acceso(), sol.perfil(), sol.idUsuario());
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        try {
            ManualPdf.escribir(manual, sol.otroPerfil() ? null : String.valueOf(s.getAttribute("nombre")), pdf);
        } catch (DocumentException e) {
            LOG.log(Level.SEVERE, "Manual de usuario en PDF", e);
            res.sendError(500, "No se pudo generar el PDF del manual.");
            return;
        }
        String nombre = "Manual_usuario_SIALSS_MDF_" + sol.acceso().name().toLowerCase() + "_perfil_" + sol.perfil()
                + "_" + LocalDate.now() + ".pdf";
        res.setContentType("application/pdf");
        res.setHeader("Content-Disposition", "inline; filename=\"" + nombre + "\"");
        res.setHeader("Cache-Control", "no-store");
        res.setContentLength(pdf.size());
        pdf.writeTo(res.getOutputStream());
    }
}
