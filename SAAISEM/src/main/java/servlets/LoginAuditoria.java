package servlets;

import in.co.sneh.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.ServicioAcceso;

/**
 * Inicio de sesión de Auditoría (indexAuditoria.jsp), usuarios de tb_usuarioauditoria.
 * Nota: en sialss_mdf esa tabla no existe; el acceso ya fallaba antes de la migración.
 */
@WebServlet("/LoginAuditoria")
public class LoginAuditoria extends HttpServlet {

    private static final String PAGINA_LOGIN = "indexAuditoria.jsp";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // La pantalla manda el usuario como "usuario"; se acepta también "nombre".
        String nombre = request.getParameter("usuario") != null ? request.getParameter("usuario") : request.getParameter("nombre");
        ServicioAcceso.Resultado r = ServicioAcceso.autenticar(request, Acceso.AUDITORIA, nombre, request.getParameter("pass"));

        if (r.estado() != ServicioAcceso.Estado.CORRECTO) {
            request.getSession(true).setAttribute("mensaje", r.mensaje());
            response.sendRedirect(PAGINA_LOGIN);
            return;
        }
        Usuario u = r.usuario();
        HttpSession sesion = r.sesion();
        sesion.setAttribute("Usuario", u.getUsuario());
        sesion.setAttribute("nombre", u.getNombre());
        sesion.setAttribute("Tipo", u.getTipo());
        sesion.setAttribute("IdUsu", u.getId() + "");
        sesion.setAttribute("Proyecto", u.getProyecto());
        sesion.setAttribute("posClave", "0");
        sesion.setAttribute("tipoIngreso", "compras");
        if (u.getProyecto() == null || u.getProyecto().isEmpty()) {
            sesion.setAttribute("mensaje", "No tienes Proyecto asignado");
            response.sendRedirect(PAGINA_LOGIN);
        } else {
            response.sendRedirect("main_menuAuditoria.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(PAGINA_LOGIN);
    }
}
