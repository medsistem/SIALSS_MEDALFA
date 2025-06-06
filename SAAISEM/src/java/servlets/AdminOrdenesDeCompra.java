/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 * Modificación del status de la ORI
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
@WebServlet(name = "AdminOrdenesDeCompra", urlPatterns = {"/AdminOrdenesDeCompra"})
public class AdminOrdenesDeCompra extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        String detallePedido;
        PreparedStatement ps;
        ConectionDB con = new ConectionDB();
        JSONObject json = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            try {
                con.conectar();
                detallePedido = request.getParameter("detallePedido");
                if (detallePedido == null) {
                    detallePedido = "";
                    json.put("msg", "error");
                }
                if (!detallePedido.isEmpty()) {
                    ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Recibido=0 WHERE F_IdIsem=?");
                    ps.setString(1, detallePedido);
                    ps.executeUpdate();
                    json.put("msg", "ok");

                }
            } catch (SQLException ex) {
                Logger.getLogger(AdminOrdenesDeCompra.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.println(json);
        }
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
