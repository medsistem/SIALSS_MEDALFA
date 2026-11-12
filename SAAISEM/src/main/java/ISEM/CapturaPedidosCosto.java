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
import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//import modelos.Proyectos;

/**
 * Captura ORI por el cliente
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class CapturaPedidosCosto extends HttpServlet {

    ConectionDB con = new ConectionDB();
    DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    DateFormat df21 = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat df31 = new SimpleDateFormat("dd/MM/yyyy");
    CancelaCompra correoCancela = new CancelaCompra();
    Correo correo = new Correo();

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

        String clave = "", descr = "", Tipo = "", Marca = "", moneda = "", noCompra = "";
        String Cuenta = "", codbar2 = "", PresPro = "", tipo = "";
        String NoCompra2 = "", proveedor2 = "", fechaCaducidad = "", descuento = "0", iva = "0", Precio = "", factorEmpaque = "", CBPro = "", ClaveSS = "";
        String oc = "", prove = "", remi = "", remipin = "", ocpin = "";
        String ClaPro = "", Priori = "", Lote = "", Cadu = "", Cant = "", Observaciones = "", F_IdIsem = "";
        String WHERE = "", Proyecto = "", DesProyecto = "", Campo = "", NomOC = "", IdOC = "", BanOC = "", noCompra2 = "";
        String NoCompra = "", Fecha = "", Proveedor = "", Prove_entrega = "", FechaVencimiento = "", dias = "", fecEnt = "", horEnt = "", FechaSur = "";
        String mensajeError = "";
        String cfdi = "";
        int ban1 = 0, contador = 0, Clonado = 0, Provepin = 0, banpin = 0, oripin = 0, proyectpin = 0, banblock = 0;
        int i = 0, cantAnt = 0, ProyectoS = 0, IdOC1 = 0;
        int ban = 0, Banp = 0;

        try {

            /////////////Ver Folios///////////////////////
            /////////////////////////////////////////////
            if (request.getParameter("accion").equals("recalendarizar")) {
                con.conectar();

                try {
                    FechaSur = request.getParameter("F_FecEnt");
                    FechaVencimiento = request.getParameter("FechaVencimiento");

                    System.out.println("FechaVencimiento: " + FechaVencimiento + " / FechaSur: " + FechaSur);
                    con.insertar("update tb_pedido_sialss set F_FecSur = '" + FechaSur + "', F_HorSur='" + request.getParameter("HoraN") + "' where F_NoCompra ='" + request.getParameter("NoCompra") + "';");
                    out.println("<script>alert('Actualización correcta')</script>");
                } catch (Exception e) {
                    out.println("<script>alert('Error al actualizar')</script>");
                }
                out.println("<script>window.location='verFoliosMCompras.jsp'</script>");
                con.cierraConexion();
            }

            if (request.getParameter("accion").equals("vencimiento")) {
                con.conectar();

                try {
                    FechaSur = request.getParameter("Entrega");
                    FechaVencimiento = request.getParameter("F_FecVen");
                    System.out.println("FechaVencimiento: " + FechaVencimiento + " / FechaSur: " + FechaSur);
                    con.insertar("update tb_pedido_sialss set Fecha_Vencimiento = '" + request.getParameter("F_FecVen") + "' where F_NoCompra ='" + request.getParameter("NoCompra") + "';");
                    out.println("<script>alert('Actualización correcta')</script>");
                } catch (Exception e) {
                    out.println("<script>alert('Error al actualizar')</script>");
                }
                out.println("<script>window.location='verFoliosMCompras.jsp'</script>");
                con.cierraConexion();
            }

            if (request.getParameter("accion").equals("plazo")) {
                con.conectar();

                try {

                    con.insertar("update tb_pedido_sialss set Plazo_Pago = '" + request.getParameter("plazo") + "' where F_NoCompra ='" + request.getParameter("NoCompra") + "';");
                    out.println("<script>alert('Actualización correcta')</script>");
                } catch (Exception e) {
                    out.println("<script>alert('Error al actualizar')</script>");
                }
                out.println("<script>window.location='verFoliosMCompras.jsp'</script>");
                con.cierraConexion();
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
                    sesion.setAttribute("FechaVencimiento", "");
                    sesion.setAttribute("dias", "");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                response.sendRedirect("verFoliosMCompras.jsp");
            }

//            if (request.getParameter("accion").equals("verFolio")) {
//                con.conectar();
//                
//                PreparedStatement ps = con.getConn().prepareStatement("SELECT F_ClaPro, F_DesPro FROM tb_medica where F_ClaPro = ?");
//                ps.setString(1, request.getParameter("NoCompra"));
//                ResultSet rset = ps.executeQuery();
//                //ResultSet rset = con.consulta("SELECT o.F_NoCompra, p.F_NomPro, DATE_FORMAT(o.F_FecSur, '%d/%m/%Y'), F_HorSur, p.F_ClaProve FROM tb_pedido_sialss o, tb_proveedor p WHERE o.F_Provee = p.F_ClaProve AND F_NoCompra = '" + request.getParameter("NoCompra") + "'  GROUP BY o.F_NoCompra");
//                while (rset.next()) {
//                    sesion.setAttribute("NoOrdCompra", rset.getString(1));
//                    sesion.setAttribute("proveedor", rset.getString(2));
//                    sesion.setAttribute("fec_entrega", rset.getString(3));
//                    sesion.setAttribute("hor_entrega", rset.getString(4));
//                }
//                response.sendRedirect("verFoliosIsem.jsp");
//            }
            ////////////////////////////////////////////////
            //////////////////fin de ver folio///////////////////////////
            if (request.getParameter("accion").equals("Actualizar")) {
                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                out.println("<script>window.location='capturaMedalfaCosto.jsp'</script>");
            }

            ///////// Captura OC ///////////////////////
            ////////////////////////////////////////////
            if (request.getParameter("accion").equals("eliminaClave")) {
                con.conectar();
                Proyecto = request.getParameter("Proyecto");
                DesProyecto = request.getParameter("DesProyecto");
                Campo = request.getParameter("Campo");
                NomOC = request.getParameter("NomOC");
                IdOC = request.getParameter("IdOC");
                BanOC = request.getParameter("BanOC");
                //con.insertar("delete from tb_pedido_sialss where F_IdIsem = '" + request.getParameter("id") + "' ");
                PreparedStatement ps = con.getConn().prepareStatement("DELETE FROM tb_pedido_sialss WHERE F_IdIsem = ? ");
                ps.setString(1, request.getParameter("id"));
                ps.executeUpdate();

                ban = 3;
                Banp = 2;
                con.cierraConexion();
                //response.sendRedirect("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");
                out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");

                // response.sendRedirect("capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + BanOC + "");
            }

            if (request.getParameter("accion").equals("LimpiarOC")) {

//                NoCompra = request.getParameter("NoCompra");;
//                Fecha = request.getParameter("Fecha");
//                Proveedor = request.getParameter("Proveedor");
//                FechaVencimiento = request.getParameter("FechaVencimiento");
//                dias = request.getParameter("dias");
                NoCompra = request.getParameter("");;
                Fecha = request.getParameter("");
                Proveedor = request.getParameter("");
                FechaVencimiento = request.getParameter("");
                dias = request.getParameter("");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
//                if (dias == null) {
//                    dias = "";
//                }
//                Proyecto = request.getParameter("Proyecto");
//                DesProyecto = request.getParameter("DesProyecto");
//                Campo = request.getParameter("Campo");
//                NomOC = request.getParameter("NomOC");
//                IdOC = request.getParameter("IdOC");

                Proyecto = request.getParameter("Proyecto");
                DesProyecto = request.getParameter("DesProyecto");
                Campo = request.getParameter("Campo");
                NomOC = request.getParameter("");
                IdOC = request.getParameter("");

                ban = 2;
                Banp = 1;

                sesion.setAttribute("NoCompra", NoCompra);
                System.out.println("que paso");
                out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&dias=" + dias + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");

            }

            if (request.getParameter("accion").equals("Validar_OC")) {

                try {
                    con.conectar();
                    ResultSet rsenvalidaoc;
                    PreparedStatement psvoc;
                    NoCompra = request.getParameter("NoCompra");;
                    Proveedor = request.getParameter("Proveedor");
                    Prove_entrega = request.getParameter("Prove_entrega");
                    Fecha = request.getParameter("Fecha");
                    FechaVencimiento = request.getParameter("FechaVencimiento");
                    dias = request.getParameter("dias");
                    moneda = request.getParameter("moneda");
                    cfdi = request.getParameter("cfdi");

                    System.out.println(NoCompra + " " + Proveedor + " " + Prove_entrega + " moneda: " + moneda + " cfdi: " + cfdi);

                    Campo = request.getParameter("Campo");
                    Proyecto = request.getParameter("Proyecto");
                    DesProyecto = request.getParameter("DesProyecto");
                    NomOC = request.getParameter("NomOC");
                    IdOC = request.getParameter("IdOC");

                    if ((NoCompra != null && NoCompra != "") && (Proveedor != null && Proveedor != "") && (Fecha != null && Fecha != "") && (FechaVencimiento != null && FechaVencimiento != "") && (dias != null && dias != "")) {
                        System.out.println("entre a query ");
                        psvoc = con.getConn().prepareStatement("SELECT IFNULL(COUNT(F_NoCompra),0) as OC FROM tb_pedido_sialss pi LEFT JOIN tb_compra c ON pi.F_NoCompra = c.F_OrdCom WHERE pi.F_NoCompra = ? GROUP BY pi.F_NoCompra ORDER BY pi.F_NoCompra;");
                        psvoc.setString(1, request.getParameter("NoCompra"));
                        rsenvalidaoc = psvoc.executeQuery();

                        while (rsenvalidaoc.next()) {
                            Clonado = rsenvalidaoc.getInt(1);
                        }
                        if (Clonado == 0) {
                            System.out.println("Todo nuevo");
                            mensajeError = "OC Nueva: " + oc + "";
                            out.println("<script>alert('OC Nueva:  " + NoCompra + "  ')</script>");
                            //  request.setAttribute("errorMessage", mensajeError);
                            ban = 3;
                            Banp = 2;
                        } else {
                            System.out.println("ya existe");
                            mensajeError = "Ya existe OC : " + NoCompra + "";
                            // request.setAttribute("errorMessage", mensajeError);
                            ban = 1;
                            Banp = 1;
                            out.println("<script>alert('Ya existe OC:  " + NoCompra + " Repetida ')</script>");
                        }

                    } else {
                        ban = 1;
                        Banp = 1;
                        mensajeError = "Faltan campos por ingresar: " + NoCompra + "";
                        out.println("<script>alert('Faltan campos por ingresar:  ')</script>");
                    }

                    con.cierraConexion();
                    request.setAttribute("errorMessage", mensajeError);
                    sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                    sesion.setAttribute("Prove_entrega", request.getParameter("Prove_entrega"));
                    sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                    sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                    sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                    sesion.setAttribute("FechaVencimiento", request.getParameter("FechaVencimiento"));
                    sesion.setAttribute("dias", request.getParameter("dias"));
                    sesion.setAttribute("NoCompra", NoCompra);
                    sesion.setAttribute("moneda", moneda);
                    sesion.setAttribute("cfdi", cfdi);

                    System.out.println("ya paso");
                    out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");

                } catch (SQLException e) {
                    e.getMessage();
                }

            }

            /////clave
            if (request.getParameter("accion").equals("ClaveB")) {
                System.out.println("entre al boton");
                PreparedStatement ps;

                NoCompra = (String) sesion.getAttribute("NoCompra");
                Proveedor = (String) sesion.getAttribute("proveedor");
                Prove_entrega = (String) sesion.getAttribute("Prove_entrega");
                fecEnt = (String) sesion.getAttribute("fec_entrega");
                horEnt = (String) sesion.getAttribute("hor_entrega");
                FechaVencimiento = (String) sesion.getAttribute("FechaVencimiento");
                dias = (String) sesion.getAttribute("dias");
//                 

                System.out.println(NomOC + " / " + NoCompra + " " + Proveedor + " " + Prove_entrega + " " + fecEnt + " " + horEnt + " " + FechaVencimiento + " " + dias);
                // System.out.println("NoCompra: " + NoCompra + "  " + request.getParameter("NoCompra"));

                Campo = request.getParameter("Campo");
                Proyecto = request.getParameter("Proyecto");
                DesProyecto = request.getParameter("DesProyecto");
                NomOC = request.getParameter("NomOC");
                IdOC = request.getParameter("IdOC");

                System.out.println(" / " + Campo + " / " + Proyecto + " / " + DesProyecto + " / " + NomOC + " / " + IdOC);

                try {
                    con.conectar();
                    ps = con.getConn().prepareStatement("SELECT F_ClaPro, F_DesPro FROM tb_medica where F_ClaPro = ?");
                    ps.setString(1, request.getParameter("Clave"));
                    ResultSet rset = ps.executeQuery();

                    while (rset.next()) {
                        ban = 1;
                        sesion.setAttribute("clave", rset.getString(1));
                        sesion.setAttribute("descripcion", rset.getString(2));
                    }

                    con.cierraConexion();
                    sesion.setAttribute("NoCompra", NoCompra);
                    sesion.setAttribute("proveedor", Proveedor);
                    sesion.setAttribute("Prove_entrega", Prove_entrega);
                    sesion.setAttribute("fec_entrega", fecEnt);
                    sesion.setAttribute("hor_entrega", horEnt);
                    sesion.setAttribute("NoOrdCompra", NoCompra);
                    sesion.setAttribute("FechaVencimiento", FechaVencimiento);
                    sesion.setAttribute("dias", dias);

                    ban = 3;
                    Banp = 2;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");

            }

            if (request.getParameter("accion").equals("capturar")) {

                try {
                    con.conectar();
                    NoCompra = (String) sesion.getAttribute("NoCompra");
                    ClaPro = request.getParameter("ClaPro");
                    moneda = (String) sesion.getAttribute("moneda");
                    cfdi = (String) sesion.getAttribute("cfdi");
                    Proyecto = request.getParameter("Proyecto");
                    Priori = request.getParameter("Prioridad");
                    Lote = request.getParameter("LotPro");
                    Cadu = request.getParameter("CadPro");
                    Cant = request.getParameter("CanPro");
                    DesProyecto = request.getParameter("DesProyecto");
                    NomOC = request.getParameter("NomOC");
                    IdOC = request.getParameter("IdOC");
                    Campo = request.getParameter("Campo");
                    Precio = request.getParameter("Precio");
                    descuento = request.getParameter("descuento");
                    iva = request.getParameter("iva");
                    BanOC = request.getParameter("BanOC");
//                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
//                    Observaciones = (new String(a, "UTF-8")).toUpperCase();

                    System.out.println(" / " + Campo + " / " + Proyecto + " / " + DesProyecto + " / " + NomOC + " / " + IdOC + " / " + Priori);

                    if (Priori.isEmpty() || Priori == null) {
                        Priori = "-";
                    }
                    if (Lote.isEmpty()) {
                        Lote = "-";
                    }
                    if (Cadu.isEmpty()) {
                        Cadu = "00/00/0000";
                    }

                    System.out.println("moneda: " + moneda + "  ClaPro: " + ClaPro + "  NoCompra = " + NoCompra + " campo: " + Campo);
                    PreparedStatement ps = null;

                    ResultSet rset = con.consulta("select F_IdIsem, F_Cant from tb_pedido_sialss where F_NoCompra = '" + NoCompra + "' and F_Clave = '" + ClaPro + "' ;");
                    System.out.println("");
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
                        System.out.println("i es igual a cero");
                        ResultSet rset2 = con.consulta("SELECT F_ClaProSS FROM tb_medica WHERE " + Campo + " = 1 AND F_ClaPro = '" + ClaPro + "';");

                        rset2.next();
                        ClaveSS = rset2.getString(1);
                        System.out.println("ClaveSS" + ClaveSS);
                        System.out.println("ps: " + ps);
                        // ResultSet rset3 = con.consulta("INSERT INTO tb_pedido_sialss VALUES(0,'" + (String) sesion.getAttribute("NoOrdCompra") + "','" + (String) sesion.getAttribute("proveedor") + "','" + ClaPro + "','" + ClaveSS + "','','" + Priori + "','" + Lote + "','" + df.format(df2.parse(Cadu)) + "','" + Cant + "',' ',CURRENT_TIMESTAMP(),'" + (String) sesion.getAttribute("fec_entrega") + "','" + (String) sesion.getAttribute("hor_entrega") + "','" + (String) sesion.getAttribute("IdUsu") + "','0','0','" + Proyecto + "','" + (String) sesion.getAttribute("FechaVencimiento") + "','" + (String) sesion.getAttribute("dias") + "','0',' ','" + (String) sesion.getAttribute("Prove_entrega") + "','" + iva + "','" + Precio + "','" + descuento + "','" + moneda + "','" + cfdi + "','0','','');");
                        ResultSet rset3 = con.consulta("INSERT INTO tb_pedido_sialss VALUES(0,'" + (String) sesion.getAttribute("NoOrdCompra") + "','" + (String) sesion.getAttribute("proveedor") + "','" + ClaPro + "','" + ClaveSS + "','','" + Priori + "','" + Lote + "','" + df.format(df2.parse(Cadu)) + "','" + Cant + "',' ',CURRENT_TIMESTAMP(),'" + (String) sesion.getAttribute("fec_entrega") + "','" + (String) sesion.getAttribute("hor_entrega") + "','" + (String) sesion.getAttribute("IdUsu") + "','0','0','" + Proyecto + "','" + (String) sesion.getAttribute("FechaVencimiento") + "','" + (String) sesion.getAttribute("dias") + "','0',' ','" + (String) sesion.getAttribute("Prove_entrega") + "','" + iva + "','" + Precio + "','" + descuento + "','" + moneda + "','" + cfdi + "','0','','');");
                        System.out.println("rset3: " + rset3);
                    }
                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("origen", "");

                } catch (SQLException | NumberFormatException | ParseException e) {
                    System.out.println(e.getMessage());
                }

                con.cierraConexion();
                ban = 3;
                Banp = 2;
                out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&NomOC=" + NomOC + "&IdOC=" + IdOC + "&Campo=" + Campo + "&Ban=" + ban + "&Banp=" + Banp + "'</script>");
            }

            if (request.getParameter("accion").equals("confirmar")) {

                //   String Proyecto = "", DesProyecto = "", Campo = "", NomOC = "", noCompra, noCompra2 = "", BanOC = "";
                //   int IdOC = 0, ProyectoS = 0;
                try {
                    con.conectar();
                    PreparedStatement ps = null;
                    ResultSet rset = null;
                    Proyecto = request.getParameter("Proyecto");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
                    NomOC = request.getParameter("NomOC");
                    BanOC = request.getParameter("BanOC");
                    noCompra = request.getParameter("NoCompra");
                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
                    Observaciones = (new String(a, "UTF-8")).toUpperCase();

                    System.out.println(Proyecto + " / " + Campo + " /BanOC: " + BanOC + " / Observaciones:" + Observaciones);
                    //con.insertar("update tb_pedido_sialss set F_StsPed = '1' where F_NoCompra = '" + (String) sesion.getAttribute("NoOrdCompra") + "'  and F_IdUsu = '" + (String) sesion.getAttribute("Usuario") + "' ");

                    if ((BanOC.equals("3")) && (!(NomOC.equals("")))) {
                        ProyectoS = Integer.parseInt(Proyecto);
                        System.out.println("ProyectoS: " + ProyectoS);
                        ps = con.getConn().prepareStatement("SELECT F_IdOC FROM tb_proyectos WHERE F_Id = ?;");
                        ps.setInt(1, ProyectoS);
                        rset = ps.executeQuery();
                        if (rset.next()) {
                            IdOC1 = rset.getInt(1);
                        }
                        System.out.println(" IdOC1: " + IdOC1);
                        ps.clearParameters();

                        noCompra2 = NomOC + IdOC1;
                        System.out.println("noCompra2: " + noCompra2);

                        ps = con.getConn().prepareStatement("UPDATE tb_proyectos SET F_IdOC = ? WHERE F_Id = ?;");
                        ps.setInt(1, IdOC1 + 1);
                        ps.setString(2, Proyecto);
                        ps.execute();

                        ps.clearParameters();

                        System.out.println("noCompra: " + noCompra + "  / " + (String) sesion.getAttribute("IdUsu"));
                        ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_StsPed = '1', F_Obser = ? WHERE F_NoCompra = ?  AND F_IdUsu = ? ");
                        ps.setString(1, Observaciones);
                        ps.setString(2, noCompra);
                        ps.setString(3, (String) sesion.getAttribute("IdUsu"));
                        ps.executeUpdate();
                        System.out.println(ps.executeUpdate());

                    } else {

                        System.out.println(" no se encuentra la oc");

                        ps = con.getConn().prepareStatement("UPDATE tb_pedido_sialss SET F_StsPed = '1', F_Obser = ? WHERE F_NoCompra = ?  AND F_IdUsu = ? ");
                        ps.setString(1, Observaciones);
                        ps.setString(2, noCompra);
                        ps.setString(3, (String) sesion.getAttribute("IdUsu"));
                        ps.executeUpdate();
                    }

                    sesion.setAttribute("clave", "");
                    sesion.setAttribute("descripcion", "");
                    sesion.setAttribute("proveedor", "");
                    sesion.setAttribute("fec_entrega", "");
                    sesion.setAttribute("hor_entrega", "");
                    sesion.setAttribute("NoOrdCompra", "");
                    sesion.setAttribute("NoCompra", null);

                    //correo.enviaCorreo(noCompra);
                } catch (Exception e) {
                    Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    try {
                        con.cierraConexion();
                    } catch (SQLException ex) {
                        Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                response.sendRedirect("crearOC.jsp");
            }

            if (request.getParameter("accion").equals("cancelar")) {
                con.conectar();

                try {
                    Proyecto = request.getParameter("Proyecto");
                    DesProyecto = request.getParameter("DesProyecto");
                    Campo = request.getParameter("Campo");
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
                response.sendRedirect("crearOC.jsp");
            }

            /////////////////// Orden de Compras /////////////
            //////////////////////////////////////////////////
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

            if (request.getParameter("accion").equals("MostrarProvee")) {

                PreparedStatement ps;

                NoCompra = request.getParameter("NoCompra");
                Fecha = request.getParameter("Fecha");
                Proveedor = request.getParameter("Proveedor");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
                Proyecto = request.getParameter("Proyecto");
                DesProyecto = request.getParameter("DesProyecto");
                Campo = request.getParameter("Campo");
                String TipoOC = request.getParameter("TipoOC");
                ban = 0;

                sesion.setAttribute("proveedor", request.getParameter("Proveedor"));
                sesion.setAttribute("fec_entrega", request.getParameter("Fecha"));
                sesion.setAttribute("hor_entrega", request.getParameter("Hora"));
                sesion.setAttribute("NoOrdCompra", request.getParameter("NoCompra"));
                sesion.setAttribute("zona", request.getParameter("Zona"));

                sesion.setAttribute("NoCompra", NoCompra);
                out.println("<script>window.location='capturaMedalfaCosto.jsp?Proyecto=" + Proyecto + "&DesProyecto=" + DesProyecto + "&Campo=" + Campo + "&TipoOC=" + TipoOC + "'</script>");
            }

            if (request.getParameter("accion").equals("ClaveAgregar")) {

                PreparedStatement ps;
                int ContarCompra = 0;
                NoCompra = request.getParameter("NoCompra");
                Fecha = request.getParameter("Fecha");
                Proveedor = request.getParameter("Proveedor");
                String Zona = request.getParameter("Zona");
                String Obs = request.getParameter("Obs");
                Tipo = request.getParameter("Tipo");
                if (NoCompra == null) {
                    NoCompra = "";
                }
                if (Fecha == null) {
                    Fecha = "";
                }
                Proyecto = request.getParameter("Proyecto");
                DesProyecto = request.getParameter("DesProyecto");
                Campo = request.getParameter("Campo");
                ban = 0;
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

        } catch (SQLException | IOException e) {
            Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(CapturaPedidosCosto.class.getName()).log(Level.SEVERE, null, ex);
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
