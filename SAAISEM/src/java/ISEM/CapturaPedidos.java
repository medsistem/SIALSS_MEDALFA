/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISEM;

import Correo.*;
import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Proyectos;
import org.json.simple.JSONObject;

/**
 * Captura ORI por el cliente
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class CapturaPedidos extends HttpServlet {

    ConectionDB con = new ConectionDB();
    DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    CancelaCompra correoCancela = new CancelaCompra();
    Correo correo = new Correo();
    JSONObject json = new JSONObject();

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
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);
        try {
            if (request.getParameter("accion").equals("recalendarizar")) {
                con.conectar();

                try {

                    con.insertar("update tb_pedido_sialss set F_FecSur = '" + request.getParameter("F_FecEnt") + "', F_HorSur='" + request.getParameter("HoraN") + "' where F_NoCompra ='" + request.getParameter("NoCompra") + "';");
                    out.println("<script>alert('Actualización correcta')</script>");
                } catch (Exception e) {
                    out.println("<script>alert('Error al actualizar')</script>");
                }
                out.println("<script>window.location='verFoliosMedalfa.jsp'</script>");
                con.cierraConexion();
            }
            
            if (request.getParameter("accion").equals("verFolio")) {
                con.conectar();

                PreparedStatement ps = con.getConn().prepareStatement("SELECT F_ClaPro, F_DesPro FROM tb_medica where F_ClaPro = ?");
                ps.setString(1, request.getParameter("NoCompra"));
                ResultSet rset = ps.executeQuery();
                //ResultSet rset = con.consulta("SELECT o.F_NoCompra, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y'), F_HorSur, p.F_ClaProve FROM tb_pedido_sialss o, tb_proveedor p WHERE o.F_Provee = p.F_ClaProve AND F_NoCompra = '" + request.getParameter("NoCompra") + "'  GROUP BY o.F_NoCompra");
                while (rset.next()) {
                    sesion.setAttribute("NoOrdCompra", rset.getString(1));
                    sesion.setAttribute("proveedor", rset.getString(2));
                    sesion.setAttribute("fec_entrega", rset.getString(3));
                    sesion.setAttribute("hor_entrega", rset.getString(4));
                }
                response.sendRedirect("verFoliosIsem.jsp");
            }
            if (request.getParameter("accion").equals("eliminaClave")) {
                con.conectar();
                String Proyecto = request.getParameter("Proyecto");
                String DesProyecto = request.getParameter("DesProyecto");
                String Campo = request.getParameter("Campo");
                String TipoOC = request.getParameter("TipoOC");
                //con.insertar("delete from tb_pedido_sialss where F_IdIsem = '" + request.getParameter("id") + "' ");
                PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_pedido_sialss WHERE F_IdIsem = ? ");
                ps.setString(1, request.getParameter("id"));
                ps.executeUpdate();
                con.cierraConexion();
                response.sendRedirect("capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "");
            }

//            if (request.getParameter("accion").equals("eliminaEnseres")) {
//                con.conectar();
//                PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_enseresoc WHERE F_Id = ? ");
//                ps.setString(1, request.getParameter("id"));
//                ps.executeUpdate();
//                con.cierraConexion();
//                response.sendRedirect("capturaEnseres.jsp");
//            }

            if (request.getParameter("accion").equals("Actualizar")) {
                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                out.println("<script>window.location='capturaMedalfa.jsp'</script>");
            }

            if (request.getParameter("accion").equals("MostrarProvee")) {

                PreparedStatement ps;

                String NoCompra = request.getParameter("NoCompra");
                String Fecha = request.getParameter("Fecha");
                String Proveedor = request.getParameter("Proveedor");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
                String Proyecto = request.getParameter("Proyecto");
                String DesProyecto = request.getParameter("DesProyecto");
                String Campo = request.getParameter("Campo");
                String TipoOC = request.getParameter("TipoOC");
                int ban = 0;

                sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                sesion.setAttribute("zona", request.getParameter("Zona"));

                sesion.setAttribute("NoCompra", NoCompra);
                out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
            }

            if (request.getParameter("accion").equals("Clave")) {

                PreparedStatement ps;

                String NoCompra = request.getParameter("NoCompra");
                String Fecha = request.getParameter("Fecha");
                String Proveedor = request.getParameter("Proveedor");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
                String Proyecto = request.getParameter("Proyecto");
                String DesProyecto = request.getParameter("DesProyecto");
                String Campo = request.getParameter("Campo");
                String TipoOC = request.getParameter("TipoOC");
                String Zona = request.getParameter("Zona");
                int ban = 0;
                if (!(NoCompra.equals(""))) {
                    if (!(Proveedor.equals(""))) {
                        if (!(Fecha.equals(""))) {
                            con.conectar();
                            try {

                                ps = con.getConn().prepareStatement("SELECT F_ClaPro, F_DesPro FROM tb_medica where F_ClaPro = ?");
                                ps.setString(1, request.getParameter("Clave"));
                                ResultSet rset = ps.executeQuery();

                                //ResultSet rset = con.consulta("select F_ClaPro, F_DesPro from tb_medica where F_ClaPro = '" + request.getParameter("Clave") + "'");
                                while (rset.next()) {
                                    ban = 1;
                                    sesion.setAttribute("clave", rset.getString(1));
                                    sesion.setAttribute("descripcion", rset.getString(2));
                                }

                                ps = con.getConn().prepareStatement("SELECT o.F_DesOri FROM tb_origen AS o, tb_prodprov2017 AS pp WHERE o.F_ClaOri = pp.F_Origen AND pp.F_ClaPro=? AND pp.F_ClaProve=?");
                                ps.setString(1, request.getParameter("Clave"));
                                ps.setString(2, request.getParameter("Proveedor"));
                                rset = ps.executeQuery();
                                while (rset.next()) {
                                    sesion.setAttribute("origen", rset.getString(1));
                                }
                                sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                                sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                                sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                                sesion.setAttribute("zona", request.getParameter("Zona"));

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            con.cierraConexion();

                            if (ban == 1) {
                                try {
                                    if (NoCompra.isEmpty()) {
                                        try {
                                            int ban2 = 0;
                                            con.conectar();
                                            ps = con.getConn().prepareStatement("select F_IdIsem from tb_pedido_sialss where F_NoCompra = ?");
                                            ps.setString(1, request.getParameter("NoCompra"));
                                            ResultSet rset = ps.executeQuery();
                                            //ResultSet rset = con.consulta("select F_IdIsem from tb_pedido_sialss where F_NoCompra = '" + request.getParameter("NoCompra") + "'");
                                            while (rset.next()) {
                                                ban2 = 1;
                                            }
                                            con.cierraConexion();
                                            if (ban2 == 1) {
                                                sesion.setAttribute("NoCompra", NoCompra);
                                                sesion.setAttribute("NoOrdCompra", "");
                                                sesion.setAttribute("clave", "");
                                                sesion.setAttribute("descripcion", "");
                                                out.println("<script>alert('Número de Compra ya utilizado')</script>");
                                                out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                                            }
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                sesion.setAttribute("NoCompra", NoCompra);
                                out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                            } else {
                                sesion.setAttribute("NoCompra", NoCompra);
                                sesion.setAttribute("clave", "");
                                sesion.setAttribute("descripcion", "");
                                out.println("<script>alert('Insumo Inexistente')</script>");
                                out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                            }
                        } else {
                            sesion.setAttribute("NoCompra", NoCompra);
                            sesion.setAttribute("clave", "");
                            sesion.setAttribute("descripcion", "");
                            out.println("<script>alert('Favor de agregar Fecha de Entrega')</script>");
                            out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                        }
                    } else {
                        sesion.setAttribute("NoCompra", NoCompra);
                        sesion.setAttribute("clave", "");
                        sesion.setAttribute("descripcion", "");
                        out.println("<script>alert('Favor de seleccionar Proveedor')</script>");
                        out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                    }
                } else {
                    sesion.setAttribute("NoCompra", NoCompra);
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    out.println("<script>alert('Favor de agregar No OC')</script>");
                    out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
                }

            }

            if (request.getParameter("accion").equals("ClaveAgregar")) {

                PreparedStatement ps;
                int ContarCompra = 0;
                String NoCompra = request.getParameter("NoCompra");
                String Fecha = request.getParameter("Fecha");
                String Proveedor = request.getParameter("Proveedor");
                String Zona = request.getParameter("Zona");
                String Obs = request.getParameter("Obs");
                String Tipo = request.getParameter("Tipo");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
                String Proyecto = request.getParameter("Proyecto");
                String DesProyecto = request.getParameter("DesProyecto");
                String Campo = request.getParameter("Campo");
                int ban = 0;
                if (!(NoCompra.equals(""))) {

                    if (!(Proveedor.equals(""))) {
                        if (!(Fecha.equals(""))) {
                            con.conectar();
                            try {
                                ps = con.getConn().prepareStatement("SELECT F_ClaPro, F_DesPro FROM tb_medica where F_ClaPro = ?");
                                ps.setString(1, request.getParameter("Clave"));
                                ResultSet rset = ps.executeQuery();

                                //ResultSet rset = con.consulta("select F_ClaPro, F_DesPro from tb_medica where F_ClaPro = '" + request.getParameter("Clave") + "'");
                                while (rset.next()) {
                                    ban = 1;
                                    sesion.setAttribute("clave", rset.getString(1));
                                    sesion.setAttribute("descripcion", rset.getString(2));
                                }
                                sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                                sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                                sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            con.cierraConexion();

                            if (ban == 1) {

                                sesion.setAttribute("NoCompra", NoCompra);
                                out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&OC=" + NoCompra + "'</script>");
                            } else {
                                sesion.setAttribute("NoCompra", NoCompra);
                                sesion.setAttribute("clave", "");
                                sesion.setAttribute("descripcion", "");
                                out.println("<script>alert('Insumo Inexistente')</script>");
                                out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "'</script>");
                            }
                        } else {
                            sesion.setAttribute("NoCompra", NoCompra);
                            sesion.setAttribute("clave", "");
                            sesion.setAttribute("descripcion", "");
                            out.println("<script>alert('Favor de agregar Fecha de Entrega')</script>");
                            out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "'</script>");
                        }
                    } else {
                        sesion.setAttribute("NoCompra", NoCompra);
                        sesion.setAttribute("clave", "");
                        sesion.setAttribute("descripcion", "");
                        out.println("<script>alert('Favor de seleccionar Proveedor')</script>");
                        out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "'</script>");
                    }

                } else {
                    sesion.setAttribute("NoCompra", NoCompra);
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    out.println("<script>alert('Favor de agregar No OC')</script>");
                    out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "'</script>");
                }

            }

            if (request.getParameter("accion").equals("Descripcion")) {
                String NoCompra = (String) sesion.getAttribute("NoCompra");
                int ban = 0;
                con.conectar();
                try {
                    PreparedStatement ps = con.getConn().prepareStatement("select F_ClaPro, F_DesPro from tb_medica where F_DesPro = ?");
                    ps.setString(1, request.getParameter("Descripcion"));
                    ResultSet rset = ps.executeQuery();
                    //ResultSet rset = con.consulta("select F_ClaPro, F_DesPro from tb_medica where F_DesPro = '" + request.getParameter("Descripcion") + "'");
                    while (rset.next()) {
                        ban = 1;
                        sesion.setAttribute("clave", rset.getString(1));
                        sesion.setAttribute("descripcion", rset.getString(2));
                    }
                    sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                    sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                    sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                    sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();

                if (ban == 1) {
                    try {
                        if (NoCompra.isEmpty()) {
                            try {
                                int ban2 = 0;
                                con.conectar();
                                //ResultSet rset = con.consulta("select F_IdIsem from tb_pedido_sialss where F_NoCompra = '" + request.getParameter("NoCompra") + "'");
                                PreparedStatement ps = con.getConn().prepareStatement("select F_IdIsem from tb_pedido_sialss where F_NoCompra = ?");
                                ps.setString(1, request.getParameter("NoCompra"));
                                ResultSet rset = ps.executeQuery();
                                while (rset.next()) {
                                    ban2 = 1;
                                }
                                con.cierraConexion();
                                if (ban2 == 1) {
                                    sesion.setAttribute("NoOrdCompra", "");
                                    sesion.setAttribute("clave", "");
                                    sesion.setAttribute("descripcion", "");
                                    out.println("<script>alert('Número de Compra ya utilizado')</script>");
                                    out.println("<script>window.location='capturaMedalfa.jsp'</script>");
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    out.println("<script>window.location='capturaMedalfa.jsp'</script>");
                } else {
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    out.println("<script>alert('Insumo Inexistente')</script>");
                    out.println("<script>window.location='capturaMedalfa.jsp'</script>");
                }
            }
            if (request.getParameter("accion").equals("capturar")) {
                String Proyecto = "", DesProyecto = "", Campo = "", TipoOC = "", Zona = "", ZonaOC="";
                try {
                    con.conectar();
                    String ClaPro, Priori, Lote, Cadu, Cant, Observaciones, WHERE = "", ClaveSS = "";
                    ClaPro = request.getParameter("ClaPro");
                    Proyecto = request.getParameter("Proyecto");
                    Priori = request.getParameter("Prioridad");
                    Lote = request.getParameter("LotPro");
                    Cadu = request.getParameter("CadPro");
                    Cant = request.getParameter("CanPro");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
                  //  TipoOC = request.getParameter("TipoOC");
                  TipoOC = "NORMAL";
                    ZonaOC = request.getParameter("ZonaOC");
                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
                    Observaciones = (new String(a, "UTF-8")).toUpperCase();
                    if (Priori.isEmpty()) {
                        Priori = "-";
                    }
                    if (Lote.isEmpty()) {
                        Lote = "-";
                    }
                    if (Cadu.isEmpty()) {
                        Cadu = "00/00/0000";
                    }

                    if (TipoOC.equals("CC")) {
                        Zona = "COMPRA CONSOLIDADA";
                    } else {
                        Zona = ZonaOC;
                    }
                    int i = 0, cantAnt = 0;
                    String F_IdIsem = "";
                    PreparedStatement ps = con.getConn().prepareStatement("select F_IdIsem, F_Cant from tb_pedido_sialss where F_NoCompra = ? and F_Clave = ? ");
                    ps.setString(1, (String) sesion.getAttribute("NoOrdCompra"));
                    ps.setString(2, ClaPro);
                    ResultSet rset = ps.executeQuery();
                    while (rset.next()) {
                        F_IdIsem = rset.getString("F_IdIsem");
                        i = 1;
                        cantAnt = rset.getInt("F_Cant");
                    }
                    if (i == 1) {
                        ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Cant = ? WHERE F_IdIsem= ?");
                        ps.setInt(1, (cantAnt + Integer.parseInt(Cant)));
                        ps.setString(2, F_IdIsem);
                        ps.executeUpdate();
                    } else {
                        ps.clearParameters();
                        ps = con.getConn().prepareStatement("SELECT F_ClaProSS FROM tb_medica WHERE " + Campo + " = 1 AND F_ClaPro=?;");
                        ps.setString(1, ClaPro);
                        rset = ps.executeQuery();
                        rset.next();
                        ClaveSS = rset.getString(1);

                        ps.clearParameters();
                        ps = con.getConn().prepareStatement("INSERT INTO tb_pedido_sialss VALUES(0,?,?,?,?,'',?,?,?,?,?,CURRENT_TIMESTAMP(),?,?,?,'0','0',?,?,?,'',0,'')");
                        ps.setString(1, (String) sesion.getAttribute("NoOrdCompra"));
                        ps.setString(2, (String) sesion.getAttribute("proveedor"));
                        ps.setString(3, ClaPro);
                        ps.setString(4, ClaveSS);
                        ps.setString(5, Priori);
                        ps.setString(6, Lote);
                        ps.setString(7, df.format(df2.parse(Cadu)));
                        ps.setString(8, Cant);
                        ps.setString(9, Observaciones);
                        ps.setString(10, (String) sesion.getAttribute("fec_entrega"));
                        ps.setString(11, (String) sesion.getAttribute("hor_entrega"));
                        ps.setString(12, (String) sesion.getAttribute("IdUsu"));
                        ps.setString(13, Proyecto);
                        ps.setString(14, Zona);
                        ps.setString(15, TipoOC);
                        System.out.println(ps);
                        ps.executeUpdate();

                    }
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("origen", "");
                } catch (SQLException | UnsupportedEncodingException | NumberFormatException | ParseException e) {
                    System.out.println(e.getMessage());
                }

                con.cierraConexion();
                out.println("<script>window.location='capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
            }

//            if (request.getParameter("accion").equals("capturarEnseres")) {
//
//                String ClaPro, Cant;
//                ClaPro = request.getParameter("ClaPro");
//                Cant = request.getParameter("CanPro");
//                String NoCompra = request.getParameter("NoCompra");
//                String Fecha = request.getParameter("Fecha");
//                String Proveedor = request.getParameter("Proveedor");
//                if (NoCompra == null) {
//                    NoCompra = "";
//                }
//                if (Fecha == null) {
//                    Fecha = "";
//                }
//                if (!(NoCompra.equals(""))) {
//                    if (!(Proveedor.equals(""))) {
//                        if (!(Fecha.equals(""))) {
//                            try {
//                                con.conectar();
//                                int i = 0, cantAnt = 0;
//                                String F_IdIsem = "";
//                                PreparedStatement ps = con.getConn().prepareStatement("SELECT F_Id, F_Cant FROM tb_enseresoc WHERE F_Oc = ? AND F_IdEnseres = ?;");
//                                ps.setString(1, NoCompra);
//                                ps.setString(2, ClaPro);
//                                ResultSet rset = ps.executeQuery();
//                                while (rset.next()) {
//                                    F_IdIsem = rset.getString(1);
//                                    i = 1;
//                                    cantAnt = rset.getInt(2);
//                                }
//                                if (i == 1) {
//                                    ps = con.getConn().prepareStatement("UPDATE tb_enseresoc SET F_Cant = ?, F_CantIngresar ? WHERE F_Id= ?");
//                                    ps.setInt(1, (cantAnt + Integer.parseInt(Cant)));
//                                    ps.setInt(2, (cantAnt + Integer.parseInt(Cant)));
//                                    ps.setString(3, F_IdIsem);
//                                    ps.executeUpdate();
//                                } else {
//                                    ps.clearParameters();
//                                    ps = con.getConn().prepareStatement("INSERT INTO tb_enseresoc VALUES(0,?,?,?,?,?,?,?,?,?,NOW())");
//                                    ps.setString(1, ClaPro);
//                                    ps.setString(2, Proveedor);
//                                    ps.setString(3, Cant);
//                                    ps.setString(4, Cant);
//                                    ps.setString(5, Fecha);
//                                    ps.setString(6, NoCompra);
//                                    ps.setInt(7, 0);
//                                    ps.setInt(8, 0);
//                                    ps.setString(9, (String) sesion.getAttribute("IdUsu"));
//                                    ps.executeUpdate();
//
//                                }
//                                sesion.setAttribute("NoCompra", NoCompra);
//                                sesion.setAttribute("Proveedor", Proveedor);
//                                sesion.setAttribute("fec_entrega", Fecha);
//                                con.cierraConexion();
//                                out.println("<script>window.location='capturaEnseres.jsp'</script>");
//                            } catch (Exception e) {
//                                System.out.println(e.getMessage());
//                            }
//                        } else {
//                            sesion.setAttribute("NoCompra", NoCompra);
//                            sesion.setAttribute("Proveedor", Proveedor);
//                            sesion.setAttribute("fec_entrega", Fecha);
//                            out.println("<script>alert('Favor de agregar Fecha de Entrega')</script>");
//                            out.println("<script>window.location='capturaEnseres.jsp'</script>");
//                        }
//                    } else {
//                        sesion.setAttribute("NoCompra", NoCompra);
//                        sesion.setAttribute("Proveedor", Proveedor);
//                        sesion.setAttribute("fec_entrega", Fecha);
//                        out.println("<script>alert('Favor de seleccionar Proveedor')</script>");
//                        out.println("<script>window.location='capturaEnseres.jsp'</script>");
//                    }
//                } else {
//                    sesion.setAttribute("NoCompra", NoCompra);
//                    sesion.setAttribute("Proveedor", Proveedor);
//                    sesion.setAttribute("fec_entrega", Fecha);
//                    out.println("<script>alert('Favor de agregar No OC')</script>");
//                    out.println("<script>window.location='capturaEnseres.jsp'</script>");
//                }
//
//            }

            if (request.getParameter("accion").equals("capturarRegistro")) {
                String Proyecto = "", DesProyecto = "", Campo = "", NoCompra = "";
                try {
                    con.conectar();
                    String ClaPro, Priori, Lote, Cadu, Cant, Observaciones, WHERE = "", ClaveSS = "";
                    ClaPro = request.getParameter("ClaPro");
                    Proyecto = request.getParameter("Proyecto");
                    Priori = "1";
                    Lote = "-";
                    Cadu = "2000-01-01";
                    Cant = request.getParameter("CanPro");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
                    String Zona = request.getParameter("Zona");
                    String Obs = request.getParameter("Obs");
                    String Tipo = request.getParameter("Tipo");
                    NoCompra = request.getParameter("NoCompra");
                    String Fecha = request.getParameter("Fecha");
                    String Proveedor = request.getParameter("Proveedor");

                    int i = 0, cantAnt = 0;
                    String F_IdIsem = "";
                    PreparedStatement ps = con.getConn().prepareStatement("select F_IdIsem, F_Cant from tb_pedido_sialss where F_NoCompra = ? and F_Clave = ? ");
                    ps.setString(1, NoCompra);
                    ps.setString(2, ClaPro);
                    ResultSet rset = ps.executeQuery();
                    while (rset.next()) {
                        F_IdIsem = rset.getString("F_IdIsem");
                        i = 1;
                        cantAnt = rset.getInt("F_Cant");
                    }
                    if (i == 1) {
                        ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Cant = ? WHERE F_IdIsem= ?");
                        ps.setInt(1, (cantAnt + Integer.parseInt(Cant)));
                        ps.setString(2, F_IdIsem);
                        ps.executeUpdate();
                    } else {
                        ps.clearParameters();
                        ps = con.getConn().prepareStatement("SELECT F_ClaProSS FROM tb_medica WHERE " + Campo + " = 1 AND F_ClaPro=?;");
                        ps.setString(1, ClaPro);
                        rset = ps.executeQuery();
                        rset.next();
                        ClaveSS = rset.getString(1);

                        ps.clearParameters();
                        ps = con.getConn().prepareStatement("INSERT INTO tb_pedido_sialss VALUES(0,?,?,?,?,'-',?,?,?,?,?,CURRENT_TIMESTAMP(),?,CURTIME(),?,'1','0',?,?,?)");
                        ps.setString(1, NoCompra);
                        ps.setString(2, Proveedor);
                        ps.setString(3, ClaPro);
                        ps.setString(4, ClaveSS);
                        ps.setString(5, Priori);
                        ps.setString(6, Lote);
                        ps.setString(7, Cadu);
                        ps.setString(8, Cant);
                        ps.setString(9, Obs);
                        ps.setString(10, Fecha);
                        ps.setString(11, (String) sesion.getAttribute("IdUsu"));
                        ps.setString(12, Proyecto);
                        ps.setString(13, Zona);
                        ps.setString(14, Tipo);
                        ps.executeUpdate();

                    }
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                } catch (SQLException | NumberFormatException e) {
                    System.out.println(e.getMessage());
                }

                con.cierraConexion();
                out.println("<script>alert('OC Actualizado')</script>");
                out.println("<script>window.location='AgregarMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&OC=" + NoCompra + "'</script>");
            }

            if (request.getParameter("accion").equals("cancelaOrden")) {
                con.conectar();
                try {
                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
                    String Obser = (new String(a, "UTF-8")).toUpperCase();
                    try {
                        //con.insertar("update tb_pedido_sialss set F_StsPed = '2' where F_NoCompra = '" + request.getParameter("NoCompra") + "'  ");
                        PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_StsPed = '2' WHERE F_NoCompra = ? ");
                        ps.setString(1, request.getParameter("NoCompra"));
                        ps.executeUpdate();
                    } catch (Exception e) {
                    }
                    try {
                        //con.insertar("INSERT INTO tb_obscancela VALUES('" + request.getParameter("NoCompra") + "','" + Obser + "')");
                        PreparedStatement ps = con.getConn().prepareStatement("INSERT INTO tb_obscancela VALUES(?,?)");
                        ps.setString(1, request.getParameter("NoCompra"));
                        ps.setString(2, Obser);
                        ps.executeUpdate();
                    } catch (Exception e) {
                    }
                    try {
                        correoCancela.cancelaCompra(request.getParameter("NoCompra"), (String) sesion.getAttribute("Usuario"));
                    } catch (Exception e) {
                    }
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("proveedor", "");
                    sesion.setAttribute("fec_entrega", "");
                    sesion.setAttribute("hor_entrega", "");
                    sesion.setAttribute("NoOrdCompra", "");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                response.sendRedirect("verFoliosMedalfa.jsp");
            }
            if (request.getParameter("accion").equals("cancelar")) {
                con.conectar();
                String Proyecto = "", DesProyecto = "", Campo = "", TipoOC = "";
                try {
                    Proyecto = request.getParameter("Proyecto");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
                    TipoOC = request.getParameter("TipoOC");
                    //con.insertar("delete from tb_pedido_sialss where F_IdUsu = '" + (String) sesion.getAttribute("Usuario") + "'  ");
                    PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_pedido_sialss WHERE F_IdUsu = ? AND F_StsPed=0 ");
                    ps.setString(1, (String) sesion.getAttribute("IdUsu"));
                    ps.executeUpdate();
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("proveedor", "");
                    sesion.setAttribute("fec_entrega", "");
                    sesion.setAttribute("hor_entrega", "");
                } catch (Exception e) {
                }
                con.cierraConexion();
                response.sendRedirect("capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "");
            }

//            if (request.getParameter("accion").equals("cancelarEnseres")) {
//                con.conectar();
//                try {
//                    PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_enseresoc WHERE F_Usuario = ? AND F_Sts = 0;");
//                    ps.setString(1, (String) sesion.getAttribute("IdUsu"));
//                    ps.executeUpdate();
//                    sesion.setAttribute("Proveedor", "");
//                    sesion.setAttribute("fec_entrega", "");
//                    sesion.setAttribute("NoCompra", "");
//                } catch (Exception e) {
//                }
//                con.cierraConexion();
//                response.sendRedirect("capturaEnseres.jsp");
//            }

            if (request.getParameter("accion").equals("eliminarRemi")) {
                con.conectar();
                try {
                    //con.insertar("delete from tb_pedido_sialss where F_NoCompra = '" + request.getParameter("F_NoCompra") + "'");
                    PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_pedido_sialss WHERE F_NoCompra = ?");
                    ps.setString(1, request.getParameter("F_NoCompra"));
                    ps.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                out.println("<script>alert('Se eliminó la orden " + request.getParameter("F_NoCompra") + " corrercetamente')</script>");
                out.println("<script>window.location='ordenesCompra.jsp'</script>");
            }
            if (request.getParameter("accion").equals("confirmarRemi")) {
                con.conectar();
                try {
                    //con.insertar("update tb_pedido_sialss set F_StsPed = '1' where F_NoCompra = '" + request.getParameter("F_NoCompra") + "'");
                    PreparedStatement ps = con.getConn().prepareStatement("update tb_pedido_sialss set F_StsPed = '1' where F_NoCompra = ?");
                    ps.setString(1, request.getParameter("F_NoCompra"));
                    ps.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                out.println("<script>alert('Se validó la orden " + request.getParameter("F_NoCompra") + " corrercetamente')</script>");
                out.println("<script>window.location='ordenesCompra.jsp'</script>");
            }
            
            if (request.getParameter("accion").equals("confirmar")) {
                con.conectar();
                String Proyecto = "", DesProyecto = "", Campo = "", TipoOC = "";
                try {
                    Proyecto = request.getParameter("Proyecto");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
                    TipoOC = request.getParameter("TipoOC");
                    //con.insertar("update tb_pedido_sialss set F_StsPed = '1' where F_NoCompra = '" + (String) sesion.getAttribute("NoOrdCompra") + "'  and F_IdUsu = '" + (String) sesion.getAttribute("Usuario") + "' ");
                    PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_StsPed = '1' WHERE F_NoCompra = ?  AND F_IdUsu = ? ");
                    String noCompra;
                    noCompra = request.getParameter("NoCompra");
                    ps.setString(1, noCompra);
                    ps.setString(2, (String) sesion.getAttribute("IdUsu"));
                    ps.executeUpdate();
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("proveedor", "");
                    sesion.setAttribute("fec_entrega", "");
                    sesion.setAttribute("hor_entrega", "");
                    sesion.setAttribute("NoOrdCompra", "");
                    sesion.setAttribute("NoCompra", null);

                    //correo.enviaCorreo(noCompra);
                } catch (Exception e) {
                    Logger.getLogger(CapturaPedidos.class
                            .getName()).log(Level.SEVERE, null, e);
                } finally {
                    try {
                        con.cierraConexion();

                    } catch (SQLException ex) {
                        Logger.getLogger(CapturaPedidos.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
                response.sendRedirect("capturaMedalfa.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "");
            }

//            if (request.getParameter("accion").equals("confirmarEnseres")) {
//                con.conectar();
//                try {
//                    PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_enseresoc SET F_Sts = '1' WHERE F_Oc = ?  AND F_Usuario = ? ");
//                    String noCompra;
//                    noCompra = request.getParameter("NoCompra");
//                    ps.setString(1, noCompra);
//                    ps.setString(2, (String) sesion.getAttribute("IdUsu"));
//                    ps.executeUpdate();
//                    sesion.setAttribute("Proveedor", "");
//                    sesion.setAttribute("fec_entrega", "");
//                    sesion.setAttribute("NoCompra", "");
//                } catch (Exception e) {
//                    Logger.getLogger(CapturaPedidos.class
//                            .getName()).log(Level.SEVERE, null, e);
//                } finally {
//                    try {
//                        con.cierraConexion();
//
//                    } catch (SQLException ex) {
//                        Logger.getLogger(CapturaPedidos.class
//                                .getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//                response.sendRedirect("capturaEnseres.jsp");
//            }

            if (request.getParameter("accion").equals("reactivar")) {
                con.conectar();
                try {
                    //con.insertar("update tb_pedido_sialss set F_Recibido='0' where F_NoCompra = '" + request.getParameter("NoCompra") + "'  ");
                    PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Recibido='0' WHERE F_NoCompra = ?  ");
                    ps.setString(1, request.getParameter("NoCompra"));
                    ps.executeUpdate();
                } catch (Exception e) {
                }
                con.cierraConexion();
                out.println("<script>alert('Se reactivo la orden " + request.getParameter("NoCompra") + " corrercetamente')</script>");
                out.println("<script>window.location='ordenesCompra.jsp'</script>");
            }
            if (request.getParameter("accion").equals("cerrar")) {
                con.conectar();
                try {
                    //con.insertar("update tb_pedido_sialss set F_Recibido='1' where F_NoCompra = '" + request.getParameter("NoCompra") + "'  ");
                    PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Recibido='1' WHERE F_NoCompra = ?  ");
                    ps.setString(1, request.getParameter("NoCompra"));
                    ps.executeUpdate();
                    ps.clearParameters();
                    //con.insertar("delete from tb_compratemp where F_OrdCom = '" + request.getParameter("NoCompra") + "'  ");
                    ps = con.getConn().prepareStatement("DELETE FROM tb_compratemp WHERE F_OrdCom = ?  ");
                    ps.setString(1, request.getParameter("NoCompra"));
                    ps.executeUpdate();
                } catch (Exception e) {
                }
                con.cierraConexion();
                out.println("<script>alert('Se cerró la orden " + request.getParameter("NoCompra") + " corrercetamente')</script>");
                out.println("<script>window.location='ordenesCompra.jsp'</script>");

            }

            if (request.getParameter("accion").equals("Modificar")) {
                PreparedStatement ps = null;
                String detallePedido = request.getParameter("detalleModificar");
                String CantidadM = request.getParameter("CantidadM");
                String CantSol = request.getParameter("CantSol");
                CantSol = CantSol.replace(",", "");
                if (detallePedido == null || CantidadM == null) {
                    detallePedido = "";
                    json.put("msg", "error");
                }
                if (!detallePedido.isEmpty()) {
                    ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Cant = ? WHERE F_IdIsem = ?;");
                    ps.setString(1, CantidadM);
                    ps.setString(2, detallePedido);
                    ps.executeUpdate();
                    json.put("msg", "ok");

                    ps.clearParameters();

                    ps = con.getConn().prepareStatement("INSERT INTO tb_registrocambiooc VALUES (0 , ?, ?, ?, ?,NOW());");
                    ps.setString(1, detallePedido);
                    ps.setString(2, (String) sesion.getAttribute("IdUsu"));
                    ps.setInt(3, Integer.parseInt(CantSol));
                    ps.setString(4, CantidadM);
                    ps.execute();

                    ps.close();
                }
                out.println(json);
            }

            if (request.getParameter("accion").equals("Eliminar")) {
                PreparedStatement ps = null;
                String detallePedido = request.getParameter("detalleModificar");

                if (detallePedido == null) {
                    detallePedido = "";
                    json.put("msg", "error");
                }
                if (!detallePedido.isEmpty()) {
                    ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_StsPed=3 , F_Recibido = 3 WHERE F_IdIsem = ?;");
                    ps.setString(1, detallePedido);
                    ps.executeUpdate();
                    json.put("msg", "ok");

                    ps.clearParameters();

                    ps.close();
                }
                out.println(json);
            }

            if (request.getParameter("accion").equals("AbrirOrden")) {
                con.conectar();
                try {
                    try {
                        PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_Recibido = 0 WHERE F_NoCompra = ? ");
                        ps.setString(1, request.getParameter("NoCompra"));
                        ps.executeUpdate();
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                response.sendRedirect("verFoliosMedalfa.jsp");
            }

        } catch (SQLException | IOException e) {
            Logger.getLogger(CapturaPedidos.class
                    .getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                con.cierraConexion();

            } catch (Exception ex) {
                Logger.getLogger(CapturaPedidos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String noCompra() {
        String indice = "0";
        try {
            con.conectar();
            ResultSet rset = con.consulta("select F_IndIsem from tb_indice");
            while (rset.next()) {
                indice = rset.getString(1);
                //con.insertar("update tb_indice set F_IndIsem = '" + (rset.getInt(1) + 1) + "'");
                PreparedStatement ps = con.getConn().prepareStatement("UPDATE tb_indice SET F_IndIsem = ?");
                ps.setInt(1, (rset.getInt(1) + 1));
                ps.executeUpdate();

            }
        } catch (Exception e) {
            Logger.getLogger(CapturaPedidos.class
                    .getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                con.cierraConexion();

            } catch (Exception ex) {
                Logger.getLogger(CapturaPedidos.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return indice;
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
