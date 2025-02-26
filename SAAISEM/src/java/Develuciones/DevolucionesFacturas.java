/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Develuciones;

import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import modelos.DevolucionesFact;
import modelos.UbicaDevFactura;

/**
 * Proceso para las devoluciones
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class DevolucionesFacturas extends HttpServlet {

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
        Date fechaActual = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formateador2 = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSistema = "";
        String fechaSistema2 = "";
        ConectionDB con = new ConectionDB();
        HttpSession sesion = request.getSession(true);
        List<DevolucionesFact> Listaremisiones = new ArrayList<DevolucionesFact>();
        DevolucionesFact remisiones;
        List<DevolucionesFact> DatosUnidad = new ArrayList<DevolucionesFact>();
        DevolucionesFact Unidad;
        List<DevolucionesFact> ListaDevolucion = new ArrayList<DevolucionesFact>();
        List<UbicaDevFactura> ListaUbicaDev = new ArrayList<UbicaDevFactura>();
        UbicaDevFactura ubicaDevFact;
        DevolucionesFact Devolucion;
        PreparedStatement ps, ps2, ps3 = null;
        ResultSet rs, rs2, rs3 = null;
        String Consulta = "", Consulta2 = "", ubicaDev = "";
        String TipoFact = "", usua = "", Concepto = "";

        try {
            usua = (String) sesion.getAttribute("nombre");
            String Accion = request.getParameter("Accion");
            System.out.println("accion" + Accion);
            con.conectar();
            if (Accion.equals("ListaRemision")) {
                request.getRequestDispatcher("/DevolicionFactura.jsp").forward(request, response);
            }

            if (Accion.equals("ListaDevolucion")) {

                sesion.setAttribute("fecha_ini", fechaSistema);
                sesion.setAttribute("fecha_fin", fechaSistema2);
                System.out.println("Fecha" + fechaSistema);
                System.out.println("Fecha" + fechaSistema2);

                if (fechaSistema.equals("") || fechaSistema2.equals("")) {
                    request.getRequestDispatcher("/DevolucionesAdmin.jsp").forward(request, response);
                } else {
                    // Consulta = "SELECT DATE_FORMAT(D.F_FecDev, '%d/%m/%Y') AS F_FecDev, D.F_DocDev, F.F_ClaCli, U.F_NomCli, DATE_FORMAT(F.F_FecEnt, '%d/%m/%Y') AS F_FecEnt, D.F_DocRef, D.F_IdDev, D.F_Proyecto FROM tb_devoluciones D INNER JOIN tb_factura F ON D.F_DocRef = F.F_ClaDoc AND D.F_Proyecto = F.F_Proyecto INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli WHERE D.F_FecDev BETWEEN ? AND ? GROUP BY D.F_DocDev ORDER BY D.F_DocDev;";
                    Consulta = "SELECT DATE_FORMAT(L.F_FecDev,'%d/%m/%Y') AS F_FecDev, L.F_DocDev, UA.F_ClaCli, UA.F_NomCli,DATE_FORMAT(L.F_FecEnt,'%d/%m/%Y') AS F_FecEnt, L.F_DocRef, L.F_IdDev, L.F_Proyecto FROM tb_uniatn AS UA INNER JOIN (SELECT D.F_FecDev,D.F_DocDev,D.F_DocRef,F.F_ClaCli,f.F_FecEnt,D.F_IdDev,D.F_Proyecto FROM tb_devoluciones AS D INNER JOIN tb_factura as f on D.F_DocRef = f.F_ClaDoc AND D.F_Proyecto = F.F_Proyecto WHERE D.F_FecDev BETWEEN ?  AND ? GROUP BY D.F_IdDev) AS L ON L.F_ClaCli = UA.F_ClaCli;";

                    ps = con.getConn().prepareStatement(Consulta);
                    ps.setString(1, fechaSistema + " 00:00:00");
                    ps.setString(2, fechaSistema2 + " 23:59:59");

                    rs = ps.executeQuery();

                    while (rs.next()) {
                        Devolucion = new DevolucionesFact();
                        Devolucion.setFechamov(rs.getString(1));
                        Devolucion.setDocmovimiento(rs.getString(2));
                        Devolucion.setClaveuni(rs.getString(3));
                        Devolucion.setUnidad(rs.getString(4));
                        Devolucion.setFechaentrega(rs.getString(5));
                        Devolucion.setDocreferencia(rs.getString(6));
                        Devolucion.setIddevolucion(rs.getString(7));
                        Devolucion.setProyecto(rs.getInt(8));
                        ListaDevolucion.add(Devolucion);
                    }
                    request.setAttribute("ListaDevolucion", ListaDevolucion);
                    request.getRequestDispatcher("/DevolucionesAdmin.jsp").forward(request, response);
                }
            }

            /*administrar devolucion de facturas**/
            if (Accion.equals("btnMostrarDev")) {
                String fecha_ini = request.getParameter("fecha_ini");
                String fecha_fin = request.getParameter("fecha_fin");
                sesion.setAttribute("fecha_ini", fecha_ini);
                sesion.setAttribute("fecha_fin", fecha_fin);
                System.out.println("fecha_ini:" + fecha_ini + " fecha_fin:" + fecha_fin);

                Consulta = "SELECT DATE_FORMAT(L.F_FecDev,'%d/%m/%Y') AS F_FecDev,L.F_DocDev,UA.F_ClaCli,UA.F_NomCli,DATE_FORMAT(L.F_FecEnt,'%d/%m/%Y') AS F_FecEnt,L.F_DocRef,L.F_IdDev FROM tb_uniatn AS UA INNER JOIN (SELECT D.F_FecDev,D.F_DocDev,D.F_DocRef,F.F_ClaCli,f.F_FecEnt,D.F_IdDev FROM tb_devoluciones AS D INNER JOIN tb_factura as f on D.F_DocRef = f.F_ClaDoc  WHERE D.F_FecDev BETWEEN ?  AND ? GROUP BY D.F_IdDev) AS L ON L.F_ClaCli = UA.F_ClaCli;";

                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, fecha_ini + " 00:00:00");
                ps.setString(2, fecha_fin + " 23:59:59");
                rs = ps.executeQuery();
                System.out.println("Consulta Devolucion: " + rs + Consulta);
                while (rs.next()) {
                    Devolucion = new DevolucionesFact();
                    Devolucion.setFechamov(rs.getString(1));
                    Devolucion.setDocmovimiento(rs.getString(2));
                    Devolucion.setClaveuni(rs.getString(3));
                    Devolucion.setUnidad(rs.getString(4));
                    Devolucion.setFechaentrega(rs.getString(5));
                    Devolucion.setDocreferencia(rs.getString(6));
                    Devolucion.setIddevolucion(rs.getString(7));
                    ListaDevolucion.add(Devolucion);
                }
                request.setAttribute("ListaDevolucion", ListaDevolucion);
                request.getRequestDispatcher("/DevolucionesAdmin.jsp").forward(request, response);
            }

            if (Accion.equals("btnMostrar")) {
                int Proyectos = 0;
                String Folio = request.getParameter("Folio");
                String Proyecto = request.getParameter("Nombre");
                if ((Proyecto == null) || (Proyecto.equals("")) || (Proyecto.equals("--Selecciona Proyecto--"))) {
                    Proyectos = 0;
                } else {
                    Proyectos = Integer.parseInt(Proyecto);
                }

                Consulta2 = "DELETE FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                ps2.execute();

                Consulta = "SELECT F.F_ClaPro,SUBSTR(M.F_DesPro,1,60) AS F_DesPro,L.F_ClaLot,F_FecCad,F.F_CantReq, F.F_Ubicacion,F.F_CantSur,F.F_Costo,F.F_Monto,F.F_ClaDoc, F.F_IdFact, F.F_Lote FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli WHERE F.F_ClaDoc=? AND F_CantSur>0 AND F.F_StsFact='A' AND F.F_Proyecto = ? GROUP BY F.F_IdFact ORDER BY F.F_IdFact+0;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Consulta2 = "INSERT INTO tb_devoglobalfact VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
                    ps2 = con.getConn().prepareStatement(Consulta2);
                    ps2.setString(1, rs.getString(1));
                    ps2.setString(2, rs.getString(2));
                    ps2.setString(3, rs.getString(3));
                    ps2.setString(4, rs.getString(4));
                    ps2.setString(5, rs.getString(5));
                    ps2.setString(6, rs.getString(6));
                    ps2.setString(7, rs.getString(7));
                    ps2.setString(8, rs.getString(7));
                    ps2.setString(9, rs.getString(8));
                    ps2.setString(10, rs.getString(9));
                    ps2.setString(11, rs.getString(10));
                    ps2.setString(12, rs.getString(11));
                    ps2.setString(13, rs.getString(12));
                    ps2.execute();
                }

                Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc=? AND f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Unidad = new DevolucionesFact();
                    Unidad.setFolio(rs.getInt(1));
                    Unidad.setClaveuni(rs.getString(2));
                    Unidad.setUnidad(rs.getString(3));
                    Unidad.setFechaentrega(rs.getString(4));
                    Unidad.setUsuario(usua);
                    Unidad.setProyecto(Proyectos);
                    DatosUnidad.add(Unidad);
                }

                Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    remisiones = new DevolucionesFact();
                    remisiones.setClavepro(rs2.getString(1));
                    remisiones.setDescripcion(rs2.getString(2));
                    remisiones.setLote(rs2.getString(3));
                    remisiones.setCaducidad(rs2.getString(4));
                    remisiones.setRequerido(rs2.getString(5));
                    remisiones.setUbicacion(rs2.getString(6));
                    remisiones.setSurtido(rs2.getString(7));
                    remisiones.setDevolucion(rs2.getString(8));
                    remisiones.setCosto(rs2.getString(9));
                    remisiones.setMonto(rs2.getString(10));
                    remisiones.setDocumento(rs2.getString(11));
                    remisiones.setIddocumento(rs2.getString(12));
                    remisiones.setProyecto(Proyectos);
                    Listaremisiones.add(remisiones);
                }
                ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                ps3 = con.getConn().prepareStatement(ubicaDev);
                rs3 = ps3.executeQuery();
                while (rs3.next()) {
                    ubicaDevFact = new UbicaDevFactura();
                    ubicaDevFact.setId(rs3.getInt(1));
                    ubicaDevFact.setF_claUbi(rs3.getString(2));
                    ubicaDevFact.setEstatus(rs3.getString(3));
                    ListaUbicaDev.add(ubicaDevFact);
                }
                request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                request.setAttribute("DatosUnidad", DatosUnidad);
                request.setAttribute("listaRemision", Listaremisiones);
                request.getRequestDispatcher("/DevolicionFactura.jsp").forward(request, response);
            }

            if (Accion.equals("btnMostrarClave")) {
                int Proyectos = 0;
                String Folio = request.getParameter("Folio");
                String Proyecto = request.getParameter("Nombre");
                if ((Proyecto == null) || (Proyecto.equals("")) || (Proyecto.equals("--Selecciona Proyecto--"))) {
                    Proyectos = 0;
                } else {
                    Proyectos = Integer.parseInt(Proyecto);
                }

                Consulta2 = "DELETE FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                ps2.execute();

                Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc=? AND f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Unidad = new DevolucionesFact();
                    Unidad.setFolio(rs.getInt(1));
                    Unidad.setClaveuni(rs.getString(2));
                    Unidad.setUnidad(rs.getString(3));
                    Unidad.setFechaentrega(rs.getString(4));
                    Unidad.setUsuario(usua);
                    Unidad.setProyecto(Proyectos);
                    DatosUnidad.add(Unidad);
                }

                request.setAttribute("DatosUnidad", DatosUnidad);
                request.getRequestDispatcher("/DevolicionFacturaClave.jsp").forward(request, response);
            }

            if (Accion.equals("btnModificar")) {
                int Cantidad = 0, Diferencia = 0;
                String Folio = request.getParameter("Folio");
                String IdFol = request.getParameter("Identi");
                String Devolver = request.getParameter("Devolver");
                String proyecto = request.getParameter("proyecto");
                int Surtida = Integer.parseInt(request.getParameter("Surtida"));
                System.out.println("Folio: " + Folio + " Id: " + IdFol);
                if (!(Devolver == "")) {
                    Cantidad = Integer.parseInt(Devolver);
                    if (Cantidad <= 0) {
                        out.println("<script>alert('Cantidad a Devolver debe ser Mayor a Cero')</script>");
                        out.println("<script>window.history.back()</script>");
                    } else {
                        Diferencia = Surtida - Cantidad;
                        if (Diferencia < 0) {
                            out.println("<script>alert('Cantidad a Devolver es Mayor a lo Surtido')</script>");
                            out.println("<script>window.history.back()</script>");
                        } else {
                            Consulta = "UPDATE tb_devoglobalfact SET F_CantDevo =? WHERE F_IdFact=? AND F_ClaDoc=?;";
                            ps = con.getConn().prepareStatement(Consulta);
                            ps.setString(1, Devolver);
                            ps.setString(2, IdFol);
                            ps.setString(3, Folio);
                            ps.execute();

                            Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc = ? AND f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                            ps = con.getConn().prepareStatement(Consulta);
                            ps.setString(1, Folio);
                            ps.setString(2, proyecto);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                Unidad = new DevolucionesFact();
                                Unidad.setFolio(rs.getInt(1));
                                Unidad.setClaveuni(rs.getString(2));
                                Unidad.setUnidad(rs.getString(3));
                                Unidad.setFechaentrega(rs.getString(4));
                                Unidad.setUsuario(usua);
                                Unidad.setProyecto(Integer.parseInt(proyecto));
                                DatosUnidad.add(Unidad);
                            }

                            Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                            ps2 = con.getConn().prepareStatement(Consulta2);
                            ps2.setString(1, Folio);
                            rs2 = ps2.executeQuery();
                            while (rs2.next()) {
                                remisiones = new DevolucionesFact();
                                remisiones.setClavepro(rs2.getString(1));
                                remisiones.setDescripcion(rs2.getString(2));
                                remisiones.setLote(rs2.getString(3));
                                remisiones.setCaducidad(rs2.getString(4));
                                remisiones.setRequerido(rs2.getString(5));
                                remisiones.setUbicacion(rs2.getString(6));
                                remisiones.setSurtido(rs2.getString(7));
                                remisiones.setDevolucion(rs2.getString(8));
                                remisiones.setCosto(rs2.getString(9));
                                remisiones.setMonto(rs2.getString(10));
                                remisiones.setDocumento(rs2.getString(11));
                                remisiones.setIddocumento(rs2.getString(12));
                                remisiones.setProyecto(Integer.parseInt(proyecto));
                                Listaremisiones.add(remisiones);
                            }
                            ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                            ps3 = con.getConn().prepareStatement(ubicaDev);
                            rs3 = ps3.executeQuery();
                            while (rs3.next()) {
                                ubicaDevFact = new UbicaDevFactura();
                                ubicaDevFact.setId(rs3.getInt(1));
                                ubicaDevFact.setF_claUbi(rs3.getString(2));
                                ubicaDevFact.setEstatus(rs3.getString(3));
                                ListaUbicaDev.add(ubicaDevFact);
                            }
                            request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                            request.setAttribute("DatosUnidad", DatosUnidad);
                            request.setAttribute("listaRemision", Listaremisiones);
                            request.getRequestDispatcher("/DevolicionFactura.jsp").forward(request, response);
                        }
                    }
                } else {
                    out.println("<script>alert('Ingrese Datos')</script>");
                    out.println("<script>window.history.back()</script>");
                }
            }

            if (Accion.equals("btnModificarClave")) {
                int Cantidad = 0, Diferencia = 0;
                String Folio = request.getParameter("Folio");
                String IdFol = request.getParameter("Identi");
                String Devolver = request.getParameter("Devolver");
                String proyecto = request.getParameter("proyecto");
                int Surtida = Integer.parseInt(request.getParameter("Surtida"));
                System.out.println("Folio: " + Folio + " Id: " + IdFol);
                if (!(Devolver == "")) {
                    Cantidad = Integer.parseInt(Devolver);
                    if (Cantidad <= 0) {
                        out.println("<script>alert('Cantidad a Devolver debe ser Mayor a Cero')</script>");
                        out.println("<script>window.history.back()</script>");
                    } else {
                        Diferencia = Surtida - Cantidad;
                        if (Diferencia < 0) {
                            out.println("<script>alert('Cantidad a Devolver es Mayor a lo Surtido')</script>");
                            out.println("<script>window.history.back()</script>");
                        } else {
                            Consulta = "UPDATE tb_devoglobalfact SET F_CantDevo =? WHERE F_IdFact=? AND F_ClaDoc=?;";
                            ps = con.getConn().prepareStatement(Consulta);
                            ps.setString(1, Devolver);
                            ps.setString(2, IdFol);
                            ps.setString(3, Folio);
                            ps.execute();

                            Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc = ? AND f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                            ps = con.getConn().prepareStatement(Consulta);
                            ps.setString(1, Folio);
                            ps.setString(2, proyecto);
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                Unidad = new DevolucionesFact();
                                Unidad.setFolio(rs.getInt(1));
                                Unidad.setClaveuni(rs.getString(2));
                                Unidad.setUnidad(rs.getString(3));
                                Unidad.setFechaentrega(rs.getString(4));
                                Unidad.setUsuario(usua);
                                Unidad.setProyecto(Integer.parseInt(proyecto));
                                DatosUnidad.add(Unidad);
                            }

                            Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                            ps2 = con.getConn().prepareStatement(Consulta2);
                            ps2.setString(1, Folio);
                            rs2 = ps2.executeQuery();
                            while (rs2.next()) {
                                remisiones = new DevolucionesFact();
                                remisiones.setClavepro(rs2.getString(1));
                                remisiones.setDescripcion(rs2.getString(2));
                                remisiones.setLote(rs2.getString(3));
                                remisiones.setCaducidad(rs2.getString(4));
                                remisiones.setRequerido(rs2.getString(5));
                                remisiones.setUbicacion(rs2.getString(6));
                                remisiones.setSurtido(rs2.getString(7));
                                remisiones.setDevolucion(rs2.getString(8));
                                remisiones.setCosto(rs2.getString(9));
                                remisiones.setMonto(rs2.getString(10));
                                remisiones.setDocumento(rs2.getString(11));
                                remisiones.setIddocumento(rs2.getString(12));
                                remisiones.setProyecto(Integer.parseInt(proyecto));
                                Listaremisiones.add(remisiones);
                            }

                            ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                            ps3 = con.getConn().prepareStatement(ubicaDev);
                            rs3 = ps3.executeQuery();
                            while (rs3.next()) {
                                ubicaDevFact = new UbicaDevFactura();
                                ubicaDevFact.setId(rs3.getInt(1));
                                ubicaDevFact.setF_claUbi(rs3.getString(2));
                                ubicaDevFact.setEstatus(rs3.getString(3));
                                ListaUbicaDev.add(ubicaDevFact);
                            }
                            request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                            request.setAttribute("DatosUnidad", DatosUnidad);
                            request.setAttribute("listaRemision", Listaremisiones);
                            request.getRequestDispatcher("/DevolicionFacturaClave.jsp").forward(request, response);
                        }
                    }
                } else {
                    out.println("<script>alert('Ingrese Datos')</script>");
                    out.println("<script>window.history.back()</script>");
                }
            }

            if (Accion.equals("btnEliminar")) {
                String Folio = request.getParameter("folio");
                String Proyecto = request.getParameter("Proyecto");

                Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc = ? and f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Unidad = new DevolucionesFact();
                    Unidad.setFolio(rs.getInt(1));
                    Unidad.setClaveuni(rs.getString(2));
                    Unidad.setUnidad(rs.getString(3));
                    Unidad.setFechaentrega(rs.getString(4));
                    Unidad.setUsuario(usua);
                    Unidad.setProyecto(Integer.parseInt(Proyecto));
                    DatosUnidad.add(Unidad);
                }

                Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    remisiones = new DevolucionesFact();
                    remisiones.setClavepro(rs2.getString(1));
                    remisiones.setDescripcion(rs2.getString(2));
                    remisiones.setLote(rs2.getString(3));
                    remisiones.setCaducidad(rs2.getString(4));
                    remisiones.setRequerido(rs2.getString(5));
                    remisiones.setUbicacion(rs2.getString(6));
                    remisiones.setSurtido(rs2.getString(7));
                    remisiones.setDevolucion(rs2.getString(8));
                    remisiones.setCosto(rs2.getString(9));
                    remisiones.setMonto(rs2.getString(10));
                    remisiones.setDocumento(rs2.getString(11));
                    remisiones.setIddocumento(rs2.getString(12));
                    remisiones.setProyecto(Integer.parseInt(Proyecto));
                    Listaremisiones.add(remisiones);
                }

                ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                ps3 = con.getConn().prepareStatement(ubicaDev);
                rs3 = ps3.executeQuery();
                while (rs3.next()) {
                    ubicaDevFact = new UbicaDevFactura();
                    ubicaDevFact.setId(rs3.getInt(1));
                    ubicaDevFact.setF_claUbi(rs3.getString(2));
                    ubicaDevFact.setEstatus(rs3.getString(3));
                    ListaUbicaDev.add(ubicaDevFact);
                }
                request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                request.setAttribute("DatosUnidad", DatosUnidad);
                request.setAttribute("listaRemision", Listaremisiones);
                request.getRequestDispatcher("/DevolicionFactura.jsp").forward(request, response);

            }

            if (Accion.equals("btnClave")) {
                int Proyectos = 0;
                String Folio = request.getParameter("FolioC");
                String Proyecto = request.getParameter("ProyectoC");
                String Clave = request.getParameter("Clave");
                if ((Proyecto == null) || (Proyecto.equals("")) || (Proyecto.equals("--Selecciona Proyecto--"))) {
                    Proyectos = 0;
                } else {
                    Proyectos = Integer.parseInt(Proyecto);
                }

                Consulta2 = "DELETE FROM tb_devoglobalfact WHERE F_ClaDoc=? AND F_ClaPro = ?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                ps2.setString(2, Clave);
                ps2.execute();

                Consulta = "SELECT F.F_ClaPro,SUBSTR(M.F_DesPro,1,60) AS F_DesPro,L.F_ClaLot,F_FecCad,F.F_CantReq, F.F_Ubicacion,F.F_CantSur,F.F_Costo,F.F_Monto,F.F_ClaDoc, F.F_IdFact, F.F_Lote FROM tb_factura F INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot INNER JOIN tb_uniatn U ON F.F_ClaCli=U.F_ClaCli WHERE F.F_ClaDoc=? AND F_CantSur>0 AND F.F_StsFact='A' AND F.F_Proyecto = ? AND F.F_ClaPro = ? GROUP BY F.F_IdFact ORDER BY F.F_IdFact+0;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                ps.setString(3, Clave);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Consulta2 = "INSERT INTO tb_devoglobalfact VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
                    ps2 = con.getConn().prepareStatement(Consulta2);
                    ps2.setString(1, rs.getString(1));
                    ps2.setString(2, rs.getString(2));
                    ps2.setString(3, rs.getString(3));
                    ps2.setString(4, rs.getString(4));
                    ps2.setString(5, rs.getString(5));
                    ps2.setString(6, rs.getString(6));
                    ps2.setString(7, rs.getString(7));
                    ps2.setString(8, rs.getString(7));
                    ps2.setString(9, rs.getString(8));
                    ps2.setString(10, rs.getString(9));
                    ps2.setString(11, rs.getString(10));
                    ps2.setString(12, rs.getString(11));
                    ps2.setString(13, rs.getString(12));
                    ps2.execute();
                }

                Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc=? AND f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Unidad = new DevolucionesFact();
                    Unidad.setFolio(rs.getInt(1));
                    Unidad.setClaveuni(rs.getString(2));
                    Unidad.setUnidad(rs.getString(3));
                    Unidad.setFechaentrega(rs.getString(4));
                    Unidad.setUsuario(usua);
                    Unidad.setProyecto(Proyectos);
                    DatosUnidad.add(Unidad);
                }

                Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    remisiones = new DevolucionesFact();
                    remisiones.setClavepro(rs2.getString(1));
                    remisiones.setDescripcion(rs2.getString(2));
                    remisiones.setLote(rs2.getString(3));
                    remisiones.setCaducidad(rs2.getString(4));
                    remisiones.setRequerido(rs2.getString(5));
                    remisiones.setUbicacion(rs2.getString(6));
                    remisiones.setSurtido(rs2.getString(7));
                    remisiones.setDevolucion(rs2.getString(8));
                    remisiones.setCosto(rs2.getString(9));
                    remisiones.setMonto(rs2.getString(10));
                    remisiones.setDocumento(rs2.getString(11));
                    remisiones.setIddocumento(rs2.getString(12));
                    remisiones.setFolLot(rs2.getInt(13));
                    remisiones.setProyecto(Proyectos);
                    Listaremisiones.add(remisiones);
                }
                ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                ps3 = con.getConn().prepareStatement(ubicaDev);
                rs3 = ps3.executeQuery();
                while (rs3.next()) {
                    ubicaDevFact = new UbicaDevFactura();
                    ubicaDevFact.setId(rs3.getInt(1));
                    ubicaDevFact.setF_claUbi(rs3.getString(2));
                    ubicaDevFact.setEstatus(rs3.getString(3));
                    ListaUbicaDev.add(ubicaDevFact);
                }

                request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                request.setAttribute("DatosUnidad", DatosUnidad);
                request.setAttribute("listaRemision", Listaremisiones);
                request.getRequestDispatcher("/DevolicionFacturaClave.jsp").forward(request, response);
            }

            if (Accion.equals("btnEliminarClave")) {
                String Folio = request.getParameter("folio");
                String Proyecto = request.getParameter("Proyecto");

                Consulta = "SELECT F_ClaDoc,f.F_ClaCli,u.F_NomCli,DATE_FORMAT(F_FecEnt,'%d/%m/%Y') AS F_FecEnt FROM tb_factura f INNER JOIN tb_uniatn u on f.F_ClaCli=u.F_ClaCli WHERE F_ClaDoc = ? and f.F_Proyecto = ? GROUP BY F_ClaDoc,f.F_ClaCli;";
                ps = con.getConn().prepareStatement(Consulta);
                ps.setString(1, Folio);
                ps.setString(2, Proyecto);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Unidad = new DevolucionesFact();
                    Unidad.setFolio(rs.getInt(1));
                    Unidad.setClaveuni(rs.getString(2));
                    Unidad.setUnidad(rs.getString(3));
                    Unidad.setFechaentrega(rs.getString(4));
                    Unidad.setUsuario(usua);
                    Unidad.setProyecto(Integer.parseInt(Proyecto));
                    DatosUnidad.add(Unidad);
                }

                Consulta2 = "SELECT * FROM tb_devoglobalfact WHERE F_ClaDoc=?;";
                ps2 = con.getConn().prepareStatement(Consulta2);
                ps2.setString(1, Folio);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    remisiones = new DevolucionesFact();
                    remisiones.setClavepro(rs2.getString(1));
                    remisiones.setDescripcion(rs2.getString(2));
                    remisiones.setLote(rs2.getString(3));
                    remisiones.setCaducidad(rs2.getString(4));
                    remisiones.setRequerido(rs2.getString(5));
                    remisiones.setUbicacion(rs2.getString(6));
                    remisiones.setSurtido(rs2.getString(7));
                    remisiones.setDevolucion(rs2.getString(8));
                    remisiones.setCosto(rs2.getString(9));
                    remisiones.setMonto(rs2.getString(10));
                    remisiones.setDocumento(rs2.getString(11));
                    remisiones.setIddocumento(rs2.getString(12));
                    remisiones.setProyecto(Integer.parseInt(Proyecto));
                    Listaremisiones.add(remisiones);
                }
                
                ubicaDev = "SELECT ud.id, ud.F_ClaUbi, ud.estatus FROM tb_ubicadevfactura AS ud WHERE ud.estatus = 1 ORDER BY ud.id ASC;";
                ps3 = con.getConn().prepareStatement(ubicaDev);
                rs3 = ps3.executeQuery();
                while (rs3.next()) {
                    ubicaDevFact = new UbicaDevFactura();
                    ubicaDevFact.setId(rs3.getInt(1));
                    ubicaDevFact.setF_claUbi(rs3.getString(2));
                    ubicaDevFact.setEstatus(rs3.getString(3));
                    ListaUbicaDev.add(ubicaDevFact);
                }

                request.setAttribute("ListaUbicaDev", ListaUbicaDev);
                request.setAttribute("DatosUnidad", DatosUnidad);
                request.setAttribute("listaRemision", Listaremisiones);
                request.getRequestDispatcher("/DevolicionFacturaClave.jsp").forward(request, response);

            }

            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger(DevolucionesFacturas.class.getName()).log(Level.SEVERE, null, e);
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
