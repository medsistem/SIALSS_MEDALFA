/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultas;

//import com.mysql.jdbc.CallableStatement;
import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Proyectos;
import modelos.Remisiones;

/**
 * Administración de los folios generados
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class AdministraRemisiones extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        Date fechaActual = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formateador2 = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSistema = formateador.format(fechaActual);
        String fechaSistema2 = formateador2.format(fechaActual);
        PrintWriter out = response.getWriter();
        ConectionDB con = new ConectionDB();
        HttpSession sesion = request.getSession(true);
        List<Remisiones> Listaremisiones = new ArrayList<Remisiones>();
        List<Proyectos> Listaproyecto = new ArrayList<Proyectos>();
        Remisiones remisiones;
        Proyectos proyecto;
        PreparedStatement ps = null;
        CallableStatement call0 = null;
        PreparedStatement psProyecto = null;
        ResultSet rs = null;
        ResultSet rsProyecto = null;
        String Consulta = "", Condicion = "", Sts = "", ConsultaProyecto = "";
        String TipoFact = "", usua = "", tipo = "";
        try {
            usua = (String) sesion.getAttribute("nombre");
            String Accion = request.getParameter("Accion");
            String FechaIni = request.getParameter("fecha_ini");
            String FechaFin = request.getParameter("fecha_fin");
            String Folio1 = request.getParameter("folio1");
            String Folio2 = request.getParameter("folio2");
            String Proyecto = request.getParameter("Proyecto");
            tipo = (String) sesion.getAttribute("Tipo");
            con.conectar();

            ConsultaProyecto = "SELECT * FROM tb_proyectos;";
            psProyecto = con.getConn().prepareStatement(ConsultaProyecto);
            rsProyecto = psProyecto.executeQuery();
            while (rsProyecto.next()) {
                proyecto = new Proyectos();
                proyecto.setIdproyecto(rsProyecto.getInt(1));
                proyecto.setDesproyecto(rsProyecto.getString(2));
                Listaproyecto.add(proyecto);
            }

            if (Accion.equals("ListaRemision")) {

               // sesion.setAttribute("fecha_ini", fechaSistema2);
                //sesion.setAttribute("fecha_fin", fechaSistema2);
                sesion.setAttribute("folio1", Folio1);
                sesion.setAttribute("folio2", Folio2);

                System.out.println("sin filtros");
                call0 = con.getConn().prepareCall("CALL AdminRemisiones()");

                rs = call0.executeQuery();
                while (rs.next()) {
                    remisiones = new Remisiones();
                    Sts = rs.getString(4);
                    if (Sts.equals("A")) {
                        Date fechaDate1 = formateador.parse(rs.getString(5));
                        Date fechaDate2 = formateador.parse(fechaSistema);
                        if (fechaDate1.before(fechaDate2)) {
                            remisiones.setCancela("NO");
                        } else if (fechaDate2.before(fechaDate1)) {
                            remisiones.setCancela("NO");
                        } else {
                            remisiones.setCancela("SI");
                        }
                        remisiones.setVer("SI");
                    } else {
                        remisiones.setCancela("NO");
                        remisiones.setVer("NO");
                    }
                    remisiones.setFolio(rs.getInt(1));
                    remisiones.setClaveuni(rs.getString(2));
                    remisiones.setUnidad(rs.getString(3));
                    remisiones.setSts(rs.getString(4));
                    remisiones.setFechaa(rs.getString(5));
                    remisiones.setFechae(rs.getString(6));
                    TipoFact = rs.getString(7);
                    if (TipoFact == null) {
                        remisiones.setTipofact("M");
                    } else {
                        remisiones.setTipofact(rs.getString(7));
                    }
                    remisiones.setTipou(rs.getString(8));
                    remisiones.setIdproyecto(rs.getString(9));
                    remisiones.setProyecto(rs.getString(10));
                    remisiones.setProyectfactura(rs.getString(11));
                    remisiones.setBan(rs.getInt(14));
                    remisiones.setUbi(rs.getString(13));
                    remisiones.setUsuario(usua);
                    remisiones.setBantip(rs.getInt(12));

                    Listaremisiones.add(remisiones);
                }
                request.setAttribute("listaRemision", Listaremisiones);
                request.setAttribute("listaProyecto", Listaproyecto);
                request.getRequestDispatcher("/reimp_remisiones.jsp").forward(request, response);
            }


            /*BOTON PARA MOSTAR  LOS PDF DE REMISIONES**/
            if (Accion.equals("mostrar")) {
                int ProyectoSelect = 0;
                int Ban = 0;
                String Proy = "", Fechapdf = "", foliopdf = "";
                sesion.setAttribute("fecha_ini", FechaIni);
                sesion.setAttribute("fecha_fin", FechaFin);
                sesion.setAttribute("folio1", Folio1);
                sesion.setAttribute("folio2", Folio2);
                ProyectoSelect = Integer.parseInt(Proyecto);

                if ((FechaIni != "") && (FechaFin != "") && (Folio1 != "") && (Folio2 != "")) {
                    System.out.println("Filtro Folios y fechas");
                    call0 = con.getConn().prepareCall("CALL AdminRemiFolio(?,?,?,?,?,?)");

                    call0.setString(1, Folio1);
                    call0.setString(2, Folio2);
                    call0.setString(3, FechaIni);
                    call0.setString(4, FechaFin);
                    call0.setInt(5, ProyectoSelect);
                    call0.setInt(6, 3);

                } else if ((FechaIni != "") && (FechaFin != "")) {

                    System.out.println("Filtro Fechas");
                    call0 = con.getConn().prepareCall("CALL AdminRemiFolio(?,?,?,?,?,?)");
                    call0.setString(1, Folio1);
                    call0.setString(2, Folio2);
                    call0.setString(3, FechaIni);
                    call0.setString(4, FechaFin);
                    call0.setInt(5, ProyectoSelect);
                    call0.setInt(6, 2);

                } else if ((Folio1 != "") && (Folio2 != "")) {
                    call0 = con.getConn().prepareCall("CALL AdminRemiFolio(?,?,?,?,?,?)");
                    System.out.println("Filtro Folios");
                    call0.setString(1, Folio1);
                    call0.setString(2, Folio2);
                    call0.setString(3, FechaIni);
                    call0.setString(4, FechaFin);
                    call0.setInt(5, ProyectoSelect);
                    call0.setInt(6, 1);

                }

                System.out.println(call0);
                rs = call0.executeQuery();
                while (rs.next()) {
                    remisiones = new Remisiones();
                    Sts = rs.getString(4);
                    if (Sts.equals("A")) {
                        Date fechaDate1 = formateador.parse(rs.getString(5));
                        Date fechaDate2 = formateador.parse(fechaSistema);
                        if (fechaDate1.before(fechaDate2)) {
                            remisiones.setCancela("NO");
                        } else if (fechaDate2.before(fechaDate1)) {
                            remisiones.setCancela("NO");
                        } else {
                            remisiones.setCancela("SI");
                        }
                        remisiones.setVer("SI");
                    } else {
                        remisiones.setCancela("NO");
                    }
                    remisiones.setFolio(rs.getInt(1));
                    remisiones.setClaveuni(rs.getString(2));
                    remisiones.setUnidad(rs.getString(3));
                    remisiones.setSts(rs.getString(4));
                    remisiones.setFechaa(rs.getString(5));
                    remisiones.setFechae(rs.getString(6));

                    TipoFact = rs.getString(7);
                    if (TipoFact == null) {
                        remisiones.setTipofact("M");
                    } else {
                        remisiones.setTipofact(rs.getString(7));
                    }
                    remisiones.setTipou(rs.getString(8));
                    remisiones.setIdproyecto(rs.getString(9));
                    remisiones.setProyecto(rs.getString(10));
                    remisiones.setProyectfactura(rs.getString(11));
                    remisiones.setBan(rs.getInt(14));
                    remisiones.setUbi(rs.getString(13));
                    remisiones.setUsuario(usua);
                    remisiones.setBantip(rs.getInt(12));
                    Listaremisiones.add(remisiones);
                }
                request.setAttribute("listaRemision", Listaremisiones);
                request.setAttribute("listaProyecto", Listaproyecto);
                request.getRequestDispatcher("/reimp_remisiones.jsp").forward(request, response);
            }

            if (Accion.equals("exportar")) {
                request.getRequestDispatcher("ExportarAbasto?accion=exportar&fecha_fin=" + FechaFin + "").forward(request, response);
            }
            if (Accion.equals("exportarDispen")) {
                request.getRequestDispatcher("ExportarAbasto?accion=exportarDispen&fecha_fin=" + FechaFin + "").forward(request, response);
            }

            if (Accion.equals("DevoGlobal")) {
                String Folio = request.getParameter("fol_gnkl");
                request.getRequestDispatcher("DevolucionGlobal?accion=DevoGlobal&fol_gnkl=" + Folio + "").forward(request, response);
            }

            if (Accion.equals("ReenviarFactura")) {
                String Folio = request.getParameter("fol_gnkl");
                request.getRequestDispatcher("FacturacionManual?accion=ReenviarFactura&fol_gnkl=" + Folio + "").forward(request, response);
            }
            
             if (Accion.equals("exportarPDF")) {
                request.getRequestDispatcher("ExportarAbasto?accion=exportar&fecha_fin=" + FechaFin + "").forward(request, response);
            }

            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger(AdministraRemisiones.class.getName()).log(Level.SEVERE, null, e);
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
