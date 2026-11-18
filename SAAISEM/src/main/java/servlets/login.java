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
 * Inicio de sesión de Almacén (indexalmacen.jsp). Reglas comunes en {@link ServicioAcceso}:
 * bloqueo a los 3 intentos fallidos, sesión nueva al entrar, cierre de la sesión previa.
 * {@link LoginAuditoria} lo extiende para el acceso de Auditoría (misma tabla tb_usuario).
 */
@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {

    /** Acceso que valida este servlet; LoginAuditoria lo cambia y hereda todo lo demás. */
    protected Acceso acceso() {
        return Acceso.ALMACEN;
    }

    /** Datos extra de la sesión propios del acceso (Auditoría agrega los suyos). */
    protected void completarSesion(HttpSession sesion, Usuario usuario) {
    }

    /** Campo del usuario en el formulario. */
    protected String nombreUsuario(HttpServletRequest request) {
        return request.getParameter("nombre");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ServicioAcceso.Resultado r = ServicioAcceso.autenticar(request, acceso(),
                nombreUsuario(request), request.getParameter("pass"));

        if (r.estado() != ServicioAcceso.Estado.CORRECTO) {
            request.getSession(true).setAttribute("mensaje", r.mensaje());
            response.sendRedirect(acceso().paginaLogin);
            return;
        }
        HttpSession sesion = r.sesion();
        llenarSesion(sesion, r.usuario());
        completarSesion(sesion, r.usuario());
        if (r.diasContrasena() == null || r.diasContrasena() > 90) {
            response.sendRedirect("Password/EditaPasswordUsuario.jsp");
            return;
        }
        if (r.cerroOtraSesion()) {
            sesion.setAttribute("mensaje", "Se cerró otra sesión que estaba activa");
        }
        if (acceso() == Acceso.ALMACEN && "27".equals(r.usuario().getTipo())) {
            response.sendRedirect("consulta/isem.jsp?clues=&clave=&tipo=&fecha_ini=&fecha_fin=&pagina=");
        } else {
            response.sendRedirect(acceso().menu);
        }
    }

    private static void llenarSesion(HttpSession sesion, Usuario usuario) {
        sesion.setAttribute("Usuario", usuario.getUsuario());
        sesion.setAttribute("nombre", usuario.getNombre());
        // Perfil real aparte; "Tipo" ve al superusuario (100) como ADMINISTRADOR (1) en las pantallas antiguas.
        sesion.setAttribute(mx.medalfa.sialss.seguridad.PoliticaSesion.PERFIL, usuario.getTipo());
        sesion.setAttribute("Tipo", mx.medalfa.sialss.seguridad.PoliticaSesion.tipoCompatible(usuario.getTipo()));
        sesion.setAttribute("IdUsu", usuario.getId() + "");
        sesion.setAttribute("Area", usuario.getArea());
        sesion.setAttribute("ProyectoCL", usuario.getProyecto());
        sesion.setAttribute("posClave", "0");
        sesion.setAttribute("tipoIngreso", "normal");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(acceso().paginaLogin);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Inicio de sesión de " + acceso();
    }
}
