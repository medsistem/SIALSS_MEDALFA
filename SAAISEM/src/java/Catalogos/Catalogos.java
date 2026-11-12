/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Catalogos;

import Marbetes.marbetesGenerar;
import com.gnk.impl.ExistenciaProyectoDaoImpl;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

/**
 *
 * @author dotor
 */
@WebServlet(name = "Catalogos", urlPatterns = {"/Catalogos"}, initParams = {
    @WebInitParam(name = "Proyecto", value = "Value"),
    @WebInitParam(name = "Clave", value = "Value"),
    @WebInitParam(name = "Lote", value = "Value")})
public class Catalogos extends HttpServlet {

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
        JSONArray jsona;
       int accion = Integer.parseInt(request.getParameter("ban"));
        String Proyecto = request.getParameter("Proyecto");

        switch (accion) {

            case 1:
                try (PrintWriter out = response.getWriter()) {
                    CatalogosImpl consultaProductos = new CatalogosImpl();
                    jsona = consultaProductos.ObtenerProyectos();
                    out.println(jsona);
                }
                break;
            case 2:
                try (PrintWriter out = response.getWriter()) {
                    CatalogosImpl consultaProductos = new CatalogosImpl();
                    jsona = consultaProductos.ObtenerLotes();
                    out.println(jsona);
                }
                break;
            case 3:
                try (PrintWriter out = response.getWriter()) {
                    CatalogosImpl consultaProductos = new CatalogosImpl();
                    jsona = consultaProductos.ObtenerProductos();
                    out.println(jsona);
                }
                break;    
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
        int accion = Integer.parseInt(request.getParameter("ban"));
        switch (accion) {
            case 1:
                try {
                    String Clave = request.getParameter("clave");
                    String Lote = request.getParameter("lote");
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    Map CatalogosMap = new LinkedHashMap();
                    CatalogosDao Catalogo = new CatalogosImpl();

                    String GenMar = Catalogo.GenerarMarbete(Clave, Lote, Proyecto);

                    CatalogosMap.put("Clave", Clave);
                    CatalogosMap.put("Lote", Lote);
                    CatalogosMap.put("Proyecto", Proyecto);
                    String json1;
                    json1 = new Gson().toJson(CatalogosMap);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(json1);

                } catch (NumberFormatException | IOException e) {
                    Logger.getLogger(marbetesGenerar.class.getName()).log(Level.SEVERE, null, e);
                }
                break;

        }
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
