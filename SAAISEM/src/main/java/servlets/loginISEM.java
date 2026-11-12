package servlets;

import in.co.sneh.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.ServicioAcceso;

/**
 * Inicio de sesión de Compras (indexCompras.jsp e indexMedalfa.jsp), usuarios de tb_usuariocompra.
 * Mismas reglas que antes (proyecto asignado, cambio de contraseña a los 90 días) con las reglas
 * comunes de {@link ServicioAcceso} (bloqueo a los 3 intentos, consultas parametrizadas).
 */
public class loginISEM extends HttpServlet {

    private static final String PAGINA_LOGIN = "indexCompras.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ServicioAcceso.Resultado r = ServicioAcceso.autenticar(request, Acceso.COMPRAS,
                request.getParameter("nombre"), request.getParameter("pass"));

        if (r.estado() != ServicioAcceso.Estado.CORRECTO) {
            request.getSession(true).setAttribute("mensaje", r.mensaje());
            response.sendRedirect(PAGINA_LOGIN);
            return;
        }
        Usuario u = r.usuario();
        HttpSession sesion = r.sesion();
        if (u.getProyecto() == null || u.getProyecto().isEmpty()) {
            sesion.setAttribute("mensaje", "No tienes Proyecto asignado");
            response.sendRedirect(PAGINA_LOGIN);
            return;
        }
        sesion.setAttribute("Usuario", u.getUsuario());
        sesion.setAttribute("nombre", u.getNombre());
        sesion.setAttribute("Tipo", u.getTipo());
        sesion.setAttribute("IdUsu", u.getId() + "");
        sesion.setAttribute("ProyectoCL", u.getProyecto());
        sesion.setAttribute("posClave", "0");
        sesion.setAttribute("tipoIngreso", "compras");
        if (r.diasContrasena() != null && r.diasContrasena() < 90) {
            response.sendRedirect("main_menuCompras.jsp");
        } else {
            response.sendRedirect("Password/EditaPasswordUsuarioCompras.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(PAGINA_LOGIN);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Inicio de sesión de Compras";
    }
}
