
package servlets;

import conn.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Alta y modificación de catálogo de unidades
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
@WebServlet("/AltaUnidad")
public class AltaUnidad extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ConectionDB con = new ConectionDB();

        java.text.DateFormat df2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.DateFormat df3 = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);
        String Clave = request.getParameter("Clave");
        String Nombre = request.getParameter("Nombre");
        String Direccion = request.getParameter("Direccion");
        String Juris = request.getParameter("juris");
        String Tipo = request.getParameter("tipo");
        String IdReporte = request.getParameter("IdReporte");
        String RegSa = request.getParameter("Regsa");
        String RespSa = request.getParameter("Respsa");


        if (request.getParameter("accion").equals("guardar")) {
            int Contar = 0;
            try {
                con.conectar();
                int Mun = 0;
                String Jurisdiccion = "";

                ResultSet rset1 = con.consulta("SELECT COUNT(F_ClaCli) FROM tb_uniatn WHERE F_ClaCli='" + Clave + "';");
                while (rset1.next()) {
                    Contar = rset1.getInt(1);
                }
                if (Contar > 0) {
                    out.println("<script>alert('Clave de la Unidad Existente')</script>");
                    out.println("<script>window.history.back()</script>");
                } else {
                    rset1 = con.consulta("SELECT F_ClaMunIS,F_JurMunIS FROM tb_muniis WHERE F_ClaMunIS=" + Juris + ";");
                    while (rset1.next()) {
                        Mun = rset1.getInt(1);
                        Jurisdiccion = rset1.getString(2);
                    }

                    if (!Tipo.equals("")) {
                        con.insertar("INSERT INTO tb_uniatn VALUES('" + Clave + "','" + Nombre + "','A','" + Jurisdiccion + "','" + Juris + "','" + Tipo + "','" + Mun + "','" + Direccion + "','R1001','','','','1','" + IdReporte + "','" + RegSa + "','" + RespSa + "');");
                        con.insertar("INSERT INTO tb_fecharuta VALUES('" + Clave + "',CURDATE(),'R1001','Z01',0);");
                        response.sendRedirect("catalogoUnidades.jsp");
                    } else {
                        out.println("<script>alert('Seleccione Tipo Unidad')</script>");
                        out.println("<script>window.history.back()</script>");
                    }

                }
                con.cierraConexion();
            } catch (IOException | SQLException e) {
                e.printStackTrace(System.out);
            }

        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConectionDB con = new ConectionDB();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);
        String accion = request.getParameter("accion");
        String claveMod = request.getParameter("claveMod");
        
        String nombreMod = request.getParameter("nombreMod");
        String direccionMod = request.getParameter("direccionMod");
        String estatusMod = request.getParameter("estatusMod");
        String tipoMod = request.getParameter("tipoMod");
        String regSaMod = request.getParameter("regSaMod");
        String resSaMod = request.getParameter("resSaMod");
        String clues = request.getParameter("cluesMod");
        
        if(regSaMod == null){
            regSaMod = "";
        }

        try {
            if (request.getParameter("accion").equals("Modificar")) {
                try {
                    con.conectar();
                    if (!(claveMod == "")) {
                        if ((estatusMod.equals("A")) || (estatusMod.equals("C"))) {
                            if (!(nombreMod == "")) {
                                if (!(direccionMod == "")) {
                                    con.actualizar("UPDATE tb_uniatn SET F_NomCli='" + nombreMod + "',F_StsCli='" + estatusMod + "', F_Direc='" + direccionMod + "', F_Clues = '" + clues + "', F_Tipo='" + tipoMod + "', F_RegSan ='" + regSaMod + "',F_RespSan ='" + resSaMod + "'  WHERE F_ClaCli='" + claveMod + "'");
                                    response.sendRedirect("catalogoUnidades.jsp");
                                } else {
                                    out.println("<script>alert('Ingrese Datos En el campo Dirección')</script>");
                                    out.println("<script>window.history.back()</script>");
                                }
                            } else {
                                out.println("<script>alert('Ingrese Datos En el campo Nombre')</script>");
                                out.println("<script>window.history.back()</script>");
                            }
                        } else {
                            out.println("<script>alert('Ingrese Datos Correctos en el campo Sts es: A o S')</script>");
                            out.println("<script>window.history.back()</script>");
                        }
                    } else {
                        out.println("<script>alert('Ingrese Datos')</script>");
                        out.println("<script>window.history.back()</script>");
                    }
                    con.cierraConexion();
                } catch (IOException | SQLException e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
}
