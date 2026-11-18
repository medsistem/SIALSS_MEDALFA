package servlets;

import in.co.sneh.model.Usuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Inicio de sesión de Auditoría (indexAuditoria.jsp): usuarios de tb_usuario con perfil 28 AUDITORIA,
 * 1 ADMINISTRADOR o 100 SUPERUSUARIO ({@link Acceso#AUDITORIA}). Hereda de {@link login} las reglas de Almacén:
 * bloqueo a los 3 intentos (cuentan junto con Almacén), sesión única, cambio de contraseña a los 90 días.
 */
@WebServlet("/LoginAuditoria")
public class LoginAuditoria extends login {

    @Override
    protected Acceso acceso() {
        return Acceso.AUDITORIA;
    }

    @Override
    protected String nombreUsuario(HttpServletRequest request) {
        // La pantalla manda el usuario como "usuario"; se acepta también "nombre".
        String u = request.getParameter("usuario");
        return u != null ? u : request.getParameter("nombre");
    }

    @Override
    protected void completarSesion(HttpSession sesion, Usuario usuario) {
        // Las pantallas de Auditoría leen el proyecto de "Proyecto" (las de Almacén, de "ProyectoCL").
        sesion.setAttribute("Proyecto", usuario.getProyecto());
    }
}
