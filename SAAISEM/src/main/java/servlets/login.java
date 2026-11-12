package servlets;

import in.co.sneh.model.Usuario;
import in.co.sneh.service.SessionManagerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.LoginDao;

/**
 * Validación de usuarios para el ingreso al sistema.
 *
 * Mismas reglas que antes (MD5, suspensión al cuarto intento, cambio de contraseña a los 90 días,
 * cierre de la sesión previa), con consultas parametrizadas en {@link LoginDao} y una conexión
 * del pool por petición (antes era un campo compartido entre todas las peticiones).
 */
@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(login.class.getName());
    private static final String PAGINA_LOGIN = "indexalmacen.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nombre = valor(request.getParameter("nombre"));
        String pass = valor(request.getParameter("pass"));
        HttpSession sesion = request.getSession(true);

        if (nombre.isEmpty() || pass.isEmpty()) {
            sesion.setAttribute("mensaje", "Escriba su usuario y su contraseña.");
            response.sendRedirect(PAGINA_LOGIN);
            return;
        }

        try (Connection c = BaseDatos.GENERAL.conexion()) {
            LoginDao dao = new LoginDao(c);
            Usuario usuario = dao.buscarActivo(nombre, pass);

            if (usuario == null) {
                int fallidos = dao.registrarIntentoFallido(nombre);
                if (fallidos >= LoginDao.INTENTOS_MAXIMOS) {
                    dao.suspender(nombre);
                    sesion.setAttribute("mensaje", "Usuario suspendido, favor de contactar al departamento de sistemas.");
                } else {
                    sesion.setAttribute("mensaje", "Datos incorrectos, intente de nuevo.");
                }
                response.sendRedirect(PAGINA_LOGIN);
                return;
            }

            boolean otraSesion = SessionManagerService.closeSessionId(String.valueOf(usuario.getId()));
            // Sesión nueva tras autenticar: evita reutilizar un id de sesión anterior.
            sesion.invalidate();
            sesion = request.getSession(true);
            llenarSesion(sesion, usuario);
            dao.registrarEntradaCorrecta(nombre);

            Integer dias = dao.diasDesdeCambioContrasena(usuario.getId());
            if (dias == null || dias > 90) {
                response.sendRedirect("Password/EditaPasswordUsuario.jsp");
                return;
            }
            if (otraSesion) {
                sesion.setAttribute("mensaje", "Se cerró otra sesión que estaba activa");
            }
            if ("27".equals(usuario.getTipo())) {
                response.sendRedirect("consulta/isem.jsp?clues=&clave=&tipo=&fecha_ini=&fecha_fin=&pagina=");
            } else {
                response.sendRedirect("main_menu.jsp");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al validar el usuario " + nombre, e);
            request.getSession(true).setAttribute("mensaje", "No se pudo validar el usuario. Intente más tarde.");
            response.sendRedirect(PAGINA_LOGIN);
        }
    }

    private static void llenarSesion(HttpSession sesion, Usuario usuario) {
        sesion.setAttribute("Usuario", usuario.getUsuario());
        sesion.setAttribute("nombre", usuario.getNombre());
        sesion.setAttribute("Tipo", usuario.getTipo());
        sesion.setAttribute("IdUsu", usuario.getId() + "");
        sesion.setAttribute("Area", usuario.getArea());
        sesion.setAttribute("ProyectoCL", usuario.getProyecto());
        sesion.setAttribute("posClave", "0");
        sesion.setAttribute("tipoIngreso", "normal");
    }

    private static String valor(String s) {
        return s == null ? "" : s.trim();
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
        return "Inicio de sesión";
    }
}
