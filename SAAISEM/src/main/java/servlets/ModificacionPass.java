/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import Correo.CorreoModiPass;
import com.gnk.util.Calendario;
import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Creación y Modificaciones de las credenciales de los usuarios
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class ModificacionPass extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        ConectionDB con = new ConectionDB();
        CorreoModiPass correo = new CorreoModiPass();
        HttpSession sesion = request.getSession(true);
        response.setContentType("text/html;charset=UTF-8");
        try {
            con.conectar();
            String F_Nombre = "", F_Apellido = "", F_ApellidoM = "", F_Correo = "", F_Usu = "";

            if (request.getParameter("action").equals("Buscar")) {
                String Usuario = request.getParameter("usuario");

                ResultSet Consulta = con.consulta("select F_Nombre,F_Apellido,F_ApellidoM,F_Correo,F_Usu from tb_usuario WHERE F_IdUsu='" + Usuario + "';");
                if (Consulta.next()) {
                    F_Nombre = Consulta.getString(1);
                    F_Apellido = Consulta.getString(2);
                    F_ApellidoM = Consulta.getString(3);
                    F_Correo = Consulta.getString(4);
                    F_Usu = Consulta.getString(5);
                }
                sesion.setAttribute("F_Nombre", F_Nombre);
                sesion.setAttribute("F_Apellido", F_Apellido);
                sesion.setAttribute("F_ApellidoM", F_ApellidoM);
                sesion.setAttribute("F_Correo", F_Correo);
                sesion.setAttribute("F_Id", Usuario);
                sesion.setAttribute("F_Usu", F_Usu);
                response.sendRedirect("CambioContra.jsp");

            }

            if (request.getParameter("action").equals("Actualizar")) {
                String Id = request.getParameter("Id");
                String Usu = request.getParameter("Usu");
                String Nombre = request.getParameter("Nombre");
                String ApellidoP = request.getParameter("ApellidoP");
                String ApellidoM = request.getParameter("ApellidoM");
                String Correo = request.getParameter("Correo");
                String pswd = request.getParameter("pswd");

                con.actualizar("UPDATE tb_usuario SET F_Pass=MD5('" + pswd + "'),F_ApellidoM='" + ApellidoM + "',F_Correo='" + Correo + "' WHERE F_IdUsu='" + Id + "' AND F_Usu='" + Usu + "';");
                correo.enviaCorreo(Correo, Usu, pswd);
                response.sendRedirect("CambioContra.jsp");
            }

            if (request.getParameter("action").equals("AltaUsuario")) {
                int Contar = 0;
                String Password = "", Concat = "";
                
                
       
                String Juris = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19";
                String Nombre = request.getParameter("Nombre");
                String ApellidoP = request.getParameter("ApellidoP");
                String ApellidoM = request.getParameter("ApellidoM");
                String Correo = request.getParameter("Correo");
                String Usu = request.getParameter("Usuario");
                String pswd = request.getParameter("pswd");
                String TipUsu = request.getParameter("TipUsuario");
                String Proyecto = request.getParameter("ListProyect");
                // Solo superusuario, ADMINISTRADOR y ADMIN ALMACEN crean usuarios; los perfiles 1 y 100 solo los asigna
                // el superusuario, y nunca un perfil suspendido.
                boolean superusuario = mx.medalfa.sialss.seguridad.PoliticaSesion.esSuperusuario(sesion);
                if (!mx.medalfa.sialss.seguridad.PoliticaSesion.esAdministrador(sesion) || TipUsu == null
                        || !TipUsu.trim().matches("\\d+")
                        || (!superusuario && (TipUsu.trim().equals("1") || TipUsu.trim().equals(mx.medalfa.sialss.seguridad.PoliticaSesion.SUPERUSUARIO)))
                        || mx.medalfa.sialss.usuarios.PerfilDao.suspendido(con.getConn(), TipUsu)) {
                    out.println("<script>alert('No tiene permiso para crear usuarios con ese perfil')</script>");
                    out.println("<script>window.location='NuevoUsuario.jsp'</script>");
                    con.cierraConexion();
                    return;
                }
                TipUsu = TipUsu.trim();

                ResultSet Consulta = con.consulta("SELECT COUNT(F_Usu) FROM tb_usuario WHERE F_Usu='" + Usu + "';");
                if (Consulta.next()) {
                    Contar = Consulta.getInt(1);
                }

                if (Contar == 0) {
                    ResultSet PassW = con.consulta("SELECT MD5('" + pswd + "');");
                    if (PassW.next()) {
                        Password = PassW.getString(1);
                    }

                    Concat = Nombre + " " + ApellidoP;

                    if (Correo != null || TipUsu != null || TipUsu.equals("Tipo usuario") || Proyecto != null) {

                        String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)";

                        if (Correo.matches(regex)) {
                            
                            if(TipUsu.equals("13") || TipUsu.equals("14") || TipUsu.equals("15")){
                            con.insertar("INSERT INTO tb_usuariocompra (F_IdUsu, F_Usu, F_Pass, F_Nombre, F_Apellido, F_ApellidoM, F_Status, F_TipUsu, F_Proyecto, F_Correo, F_FecMod) VALUES(0,'" + Usu + "','" + Password + "','" + Usu + "','" + Concat + "','" + ApellidoM + "','A','" + TipUsu + "','" + Proyecto + "','" + Correo + "',CURDATE());");
                            out.println("<script>alert('Usuario creado correctamente')</script>");
                            }else{
                            
                            con.insertar("INSERT INTO tb_usuario (F_IdUsu, F_Usu, F_Pass, F_Nombre, F_Apellido, F_ApellidoM, F_Status, F_TipUsu, F_Juris, F_Correo, F_Area, F_Proyecto, F_FecMod) VALUES(0,'" + Usu + "','" + Password + "','" + Usu + "','" + Concat + "','" + ApellidoM + "','A','" + TipUsu + "','" + Juris + "','" + Correo + "','" + (String) sesion.getAttribute("Area") + "','" + Proyecto + "',CURDATE());");
                            out.println("<script>alert('Usuario creado correctamente')</script>");
                            }
                            correo.NuevoUsuario(Correo, Usu, pswd);

                            Contar = 0;
                            Password = "";
                            Usu = "";
                            Nombre = "";
                            ApellidoP = "";
                            ApellidoM = "";
                            Correo = "";
                            Juris = "";

                            out.println("<script>alert('Usuario creado correctamente')</script>");
                            out.println("<script>window.history.back()</script>");
                            response.sendRedirect("NuevoUsuario.jsp");
                        } else {
                            out.println("<script>alert('Correo no valido')</script>");
                            out.println("<script>window.history.back()</script>");
                            response.sendRedirect("NuevoUsuario.jsp");
                        }
                    } else {
                        out.println("<script>alert('Falta Datos Por Ingresar')</script>");
                        out.println("<script>window.history.back()</script>");
                        response.sendRedirect("NuevoUsuario.jsp");
                    }
                } else {
                    out.println("<script>alert('Usuario Ingreaso Existente, Favor de agregar diferente Usuario')</script>");
                    out.println("<script>window.history.back()</script>");
                }

            }


            con.cierraConexion();
        } catch (Exception e) {
            System.out.println(e);
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
