package servlets;

import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginAuditoria")
public class LoginAuditoria extends HttpServlet{
    
       
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ConectionDB con = new ConectionDB();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);
        /* TODO output your page here. You may use following sample code. */
        try {
            con.conectar();
            try {
                String F_Usu = "", F_nombre = "", F_TipUsu = "", F_IdUsu = "", F_Area = "", F_Proyecto = "";
                int ban = 0;
                ResultSet rset = con.consulta("SELECT F_Usu, F_nombre, F_Status, F_TipUsu, F_IdUsu, F_Proyecto FROM tb_usuarioauditoria WHERE F_Usu = '" + request.getParameter("usuario") + "' AND F_Pass = MD5( '" + request.getParameter("pass") + "' );");
                if (rset.next()) {
                    ban = 1;
                    F_Usu = rset.getString("F_Usu");
                    F_nombre = rset.getString("F_nombre");
                    F_TipUsu = rset.getString("F_TipUsu");
                    F_IdUsu = rset.getString("F_IdUsu");
                    F_Proyecto = rset.getString("F_Proyecto");

                }

                if (ban == 1) {//----------------------EL USUARIO ES VÁLIDO
                    sesion.setAttribute("Usuario", F_Usu);
                    sesion.setAttribute("nombre", F_nombre);
                    sesion.setAttribute("Tipo", F_TipUsu);
                    sesion.setAttribute("IdUsu", F_IdUsu);
                    sesion.setAttribute("Proyecto", F_Proyecto);
                    sesion.setAttribute("posClave", "0");
                    sesion.setAttribute("tipoIngreso", "compras");
                    if (!(F_Proyecto.equals(""))) {
                        con.insertar("insert into tb_registroentradas values ('" + request.getParameter("nombre") + "',NOW(),1,0)");
                        response.sendRedirect("main_menuAuditoria.jsp");
                    } else {
                        sesion.setAttribute("mensaje", "No tienes Proyecto asignado");
                        response.sendRedirect("indexAuditoria.jsp");
                    }
                } else {//--------------------------EL USUARIO NO ES VÁLIDO
                    out.println("hola");
                    con.insertar("insert into tb_registroentradas values ('" + request.getParameter("nombre") + "',NOW(),0,0)");
                    sesion.setAttribute("mensaje", "Datos inválidos, intente otra vez...");
                    response.sendRedirect("indexAuditoria.jsp");
                }

            } catch (SQLException | IOException e) {
                Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            out.close();
        }
    }

    

}

    

