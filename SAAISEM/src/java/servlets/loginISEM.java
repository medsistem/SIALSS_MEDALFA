/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.medalfa.saa.dao.impl.UsuarioDAO;
import conn.ConectionDB;
import in.co.sneh.model.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Validación de usuarios para el ingreso al sistema SAA Captura ORI
 *
 * @author MEDALFA SOFTWARE
 * @param usuario
 * @param paswword
 * @version 1.40
 */
public class loginISEM extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ConectionDB con = new ConectionDB();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);
        /* TODO output your page here. You may use following sample code. */
        try {
            con.conectar();
            UsuarioDAO dao = new UsuarioDAO(con.getConn());
            try {
                String F_Usu = "", F_nombre = "", F_TipUsu = "", F_IdUsu = "", F_Area = "", F_Proyecto = "";
                int ban = 0;
                Usuario usuario = null;
                ResultSet rset = con.consulta("SELECT F_Usu, F_nombre, F_Status, F_TipUsu, F_IdUsu, F_Proyecto FROM tb_usuariocompra WHERE F_Usu = '" + request.getParameter("nombre") + "' AND F_Pass = MD5( '" + request.getParameter("pass") + "' ) AND F_Status = 'A';;");
                if (rset.next()) {
                    ban = 1;
                    usuario = this.getUsuario(rset);

                }

                if (ban == 1) {//----------------------EL USUARIO ES VÁLIDO
                    Integer daysAgo = dao.getLastModifiedCompras(usuario.getId());
                    if (!(usuario.getProyecto().equals(""))) {
                        con.insertar("insert into tb_registroentradas values ('" + request.getParameter("nombre") + "',NOW(),1,0)");
                        this.llenarSesion(request, sesion, usuario);                        
                        if (daysAgo < 90) {
                            System.out.println("que show");
                            
                            response.sendRedirect("main_menuCompras.jsp");
                        } else {
                            System.out.println("error");
//                    response.sendRedirect("main_menuCompras.jsp");
                        
                            response.sendRedirect("Password/EditaPasswordUsuarioCompras.jsp");
                        }

                    } else {
                        sesion.setAttribute("mensaje", "No tienes Proyecto asignado");
                        response.sendRedirect("indexMedalfa.jsp");
                    }
                } else {//--------------------------EL USUARIO NO ES VÁLIDO
                    Integer Contar = null;
                    String mensaje = "Datos inválidos, intente otra vez...";
                    out.println("hola");
                    con.insertar("insert into tb_registroentradas values ('" + request.getParameter("nombre") + "',NOW(),0,0)");
                    ResultSet rsetContar = con.consulta("SELECT COUNT(F_Usu) FROM tb_registroentradas WHERE F_Usu = '" + request.getParameter("nombre") + "' AND F_Correcto = 0;");
                    if (rsetContar.next()) {
                        Contar = rsetContar.getInt(1);
                    }
                    if (Contar >= 4) {
                        con.insertar("UPDATE tb_usuariocompra SET F_Status = 'S' WHERE F_Usu = '" + request.getParameter("nombre") + "';");
                        mensaje = "Usuario Suspendido, favor de contactar al departamento de sistemas.";
                    }
                    sesion.setAttribute("mensaje", mensaje);
                    response.sendRedirect("indexMedalfa.jsp");
                }

            } catch (SQLException | IOException e) {
                Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (Exception e) {
            Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(loginISEM.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            out.close();
        }
    }

    private Usuario getUsuario(ResultSet rset) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setUsuario(rset.getString("F_Usu"));
        usuario.setNombre(rset.getString("F_nombre"));
        usuario.setTipo(rset.getString("F_TipUsu"));
        usuario.setId(rset.getInt("F_IdUsu"));
        usuario.setProyecto(rset.getString("F_Proyecto"));
        return usuario;
    }

    private void llenarSesion(HttpServletRequest request, HttpSession sesion, Usuario usuario) throws SQLException {
        sesion.setAttribute("Usuario", usuario.getUsuario());
        sesion.setAttribute("nombre", usuario.getNombre());
        sesion.setAttribute("Tipo", usuario.getTipo());
        sesion.setAttribute("IdUsu", usuario.getId() + "");
        sesion.setAttribute("ProyectoCL", usuario.getProyecto());
        sesion.setAttribute("posClave", "0");
        sesion.setAttribute("tipoIngreso", "compras");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
