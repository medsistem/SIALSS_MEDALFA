/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consultas;

import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
 
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Proyectos;
import modelos.Remisiones;

/**
 *
 * @author MEDALFA
 */
@WebServlet(name = "AdministraRemisionesCompras", urlPatterns = {"/AdministraRemisionesCompras"})
public class AdministraRemisionesCompras extends HttpServlet {

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
        PreparedStatement psProyecto = null;
        CallableStatement call0 = null;
        ResultSet rs = null;
        ResultSet rsProyecto = null;
        String Consulta = "", Condicion = "", Sts = "", ConsultaProyecto = "";
        String TipoFact = "", usua = "";
        try {
            usua = (String) sesion.getAttribute("nombre");
            String Accion = request.getParameter("Accion");
            String FechaIni = request.getParameter("fecha_ini");
            String FechaFin = request.getParameter("fecha_fin");
            String Folio1 = request.getParameter("folio1");
            String Folio2 = request.getParameter("folio2");
            String Proyecto = request.getParameter("Proyecto");
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

                sesion.setAttribute("fecha_ini", fechaSistema2);
                sesion.setAttribute("fecha_fin", fechaSistema2);
                sesion.setAttribute("folio1", Folio1);
                sesion.setAttribute("folio2", Folio2);
             //   Consulta = "( SELECT f.F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli, ' - ',IFNULL(CONT.CONTROL,'NORMAL') ) AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '1' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id LEFT JOIN (SELECT F_ClaDoc,'CONTROLADO' AS CONTROL FROM tb_factura WHERE F_Ubicacion='CONTROLADO' AND F_FecEnt = CURDATE() GROUP BY F_ClaDoc) AS CONT ON f.F_ClaDoc=CONT.F_ClaDoc WHERE F_FecEnt = CURDATE() AND F_CantSur > 0 GROUP BY f.F_ClaDoc, f.F_ClaCli, F_StsFact, f.F_Proyecto ORDER BY f.F_ClaDoc + 0 ) UNION ( SELECT F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli,' - CERO') AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '0' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id WHERE F_FecEnt = CURDATE() AND f.F_CantSur = 0 GROUP BY F_ClaDoc, f.F_ClaCli, F_StsFact, f.F_Proyecto ORDER BY F.F_ClaDoc + 0 )";
               // ps = con.getConn().prepareStatement(Consulta);
                call0 = con.getConn().prepareCall("CALL AdminRemisiones()");
                rs = call0.executeQuery(); 
//               rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("sin filtros");
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
                request.getRequestDispatcher("/reimp_remisionesCompras.jsp").forward(request, response);
            }

            if (Accion.equals("mostrar")) {
                int ProyectoSelect = 0;
                String AND = "";
                sesion.setAttribute("fecha_ini", FechaIni);
                sesion.setAttribute("fecha_fin", FechaFin);
                sesion.setAttribute("folio1", Folio1);
                sesion.setAttribute("folio2", Folio2);
//                ProyectoSelect = Integer.parseInt(Proyecto);

//                if (ProyectoSelect == 0) {
//                    AND = "";
//                } else {
//                    AND = " AND f.F_Proyecto = '" + ProyectoSelect + "'";
//                }
//
//                if ((FechaIni != "") && (FechaFin != "") && (Folio1 != "") && (Folio2 != "")) {
//                    Consulta = "(SELECT f.F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli, ' - ',IFNULL(CONT.CONTROL,'NORMAL') ) AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '1' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id LEFT JOIN ( SELECT FTOTAL.F_ClaDoc, ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) AS DIF, CASE WHEN ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) = 0 THEN 'CONTROLADO' ELSE 'NORMAL' END AS CONTROL FROM tb_factura AS FTOTAL LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) TOTAL2 FROM tb_factura WHERE F_Ubicacion = 'CONTROLADO' GROUP BY F_ClaDoc ) AS CONTAR ON FTOTAL.F_ClaDoc = CONTAR.F_ClaDoc GROUP BY FTOTAL.F_ClaDoc ) AS CONT ON f.F_ClaDoc=CONT.F_ClaDoc WHERE F_FecEnt BETWEEN ? AND ? AND F_ClaDoc BETWEEN ? AND ? " + AND + " AND f.F_CantSur > 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0) UNION (SELECT F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli,' - CERO') AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '0' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id WHERE F_FecEnt BETWEEN ? AND ? AND f.F_ClaDoc BETWEEN ? AND ? " + AND + " AND f.F_CantSur = 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0);";
//                    ps = con.getConn().prepareStatement(Consulta);
//                    ps.setString(1, FechaIni);
//                    ps.setString(2, FechaFin);
//                    ps.setString(3, Folio1);
//                    ps.setString(4, Folio2);
//                    ps.setString(5, FechaIni);
//                    ps.setString(6, FechaFin);
//                    ps.setString(7, Folio1);
//                    ps.setString(8, Folio2);
//                }
//
//                if ((FechaIni != "") && (FechaFin != "")) {
//                    Consulta = "(SELECT f.F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli, ' - ',IFNULL(CONT.CONTROL,'NORMAL') ) AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '1' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id LEFT JOIN ( SELECT FTOTAL.F_ClaDoc, ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) AS DIF, CASE WHEN ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) = 0 THEN 'CONTROLADO' ELSE 'NORMAL' END AS CONTROL FROM tb_factura AS FTOTAL LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) TOTAL2 FROM tb_factura WHERE F_Ubicacion = 'CONTROLADO' GROUP BY F_ClaDoc ) AS CONTAR ON FTOTAL.F_ClaDoc = CONTAR.F_ClaDoc GROUP BY FTOTAL.F_ClaDoc ) AS CONT ON f.F_ClaDoc=CONT.F_ClaDoc WHERE F_FecEnt BETWEEN ? AND ? " + AND + " AND f.F_CantSur > 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0) UNION (SELECT F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli,' - CERO') AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '0' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id WHERE F_FecEnt BETWEEN ? AND ? " + AND + " AND f.F_CantSur = 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0);";
//                    ps = con.getConn().prepareStatement(Consulta);
//                    ps.setString(1, FechaIni);
//                    ps.setString(2, FechaFin);
//                    ps.setString(3, FechaIni);
//                    ps.setString(4, FechaFin);
//                }
//
//                if ((Folio1 != "") && (Folio2 != "")) {
//                    Consulta = "(SELECT f.F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli, ' - ',IFNULL(CONT.CONTROL,'NORMAL') ) AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '1' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id LEFT JOIN ( SELECT FTOTAL.F_ClaDoc, ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) AS DIF, CASE WHEN ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) = 0 THEN 'CONTROLADO' ELSE 'NORMAL' END AS CONTROL FROM tb_factura AS FTOTAL LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) TOTAL2 FROM tb_factura WHERE F_Ubicacion = 'CONTROLADO' GROUP BY F_ClaDoc ) AS CONTAR ON FTOTAL.F_ClaDoc = CONTAR.F_ClaDoc GROUP BY FTOTAL.F_ClaDoc ) AS CONT ON f.F_ClaDoc=CONT.F_ClaDoc WHERE f.F_ClaDoc BETWEEN ? AND ? " + AND + " AND f.F_CantSur > 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0) UNION (SELECT F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli,' - CERO') AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '0' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id WHERE f.F_ClaDoc BETWEEN ? AND ? " + AND + " AND f.F_CantSur = 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0);";
//                    ps = con.getConn().prepareStatement(Consulta);
//                    ps.setString(1, Folio1);
//                    ps.setString(2, Folio2);
//                    ps.setString(3, Folio1);
//                    ps.setString(4, Folio2);
//                }
//
//                if ((FechaIni == "") && (FechaFin == "") && (Folio1 == "") && (Folio2 == "")) {
//                    Consulta = "(SELECT f.F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli, ' - ',IFNULL(CONT.CONTROL,'NORMAL') ) AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '1' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id LEFT JOIN ( SELECT FTOTAL.F_ClaDoc, ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) AS DIF, CASE WHEN ( COUNT(*) - IFNULL(CONTAR.TOTAL2, 0)) = 0 THEN 'CONTROLADO' ELSE 'NORMAL' END AS CONTROL FROM tb_factura AS FTOTAL LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) TOTAL2 FROM tb_factura WHERE F_Ubicacion = 'CONTROLADO' GROUP BY F_ClaDoc ) AS CONTAR ON FTOTAL.F_ClaDoc = CONTAR.F_ClaDoc GROUP BY FTOTAL.F_ClaDoc ) AS CONT ON f.F_ClaDoc=CONT.F_ClaDoc WHERE F_FecEnt= CURDATE() " + AND + " AND f.F_CantSur > 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY f.F_ClaDoc+0) UNION (SELECT F_ClaDoc, f.F_ClaCli, CONCAT(u.F_NomCli,' - CERO') AS F_NomCli, F_StsFact, DATE_FORMAT(F_FecApl, '%d/%m/%Y') AS F_FecApl, DATE_FORMAT(F_FecEnt, '%d/%m/%Y') AS F_FecEnt, o.F_Req, u.F_Tipo, f.F_Proyecto, p.F_DesProy, ps.F_DesProy AS F_DesProyFact, '0' AS BAN FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli LEFT JOIN tb_obserfact o ON f.F_ClaDoc = o.F_IdFact INNER JOIN tb_proyectos p ON u.F_Proyecto = p.F_Id INNER JOIN tb_proyectos ps ON f.F_Proyecto = ps.F_Id WHERE F_FecEnt= CURDATE() " + AND + " AND f.F_CantSur = 0 GROUP BY f.F_ClaDoc,f.F_ClaCli,F_StsFact,f.F_Proyecto ORDER BY F.F_ClaDoc+0);";
//                    ps = con.getConn().prepareStatement(Consulta);
//                }

                ProyectoSelect = Integer.parseInt(Proyecto);

                if ((FechaIni != "") && (FechaFin != "") && (Folio1 != "") && (Folio2 != "")) {
                    System.out.println("Filtro Folios");
                    call0 = con.getConn().prepareCall("CALL AdminRemiFolio(?,?,?,?,?,?)");

                    call0.setString(1, Folio1);
                    call0.setString(2, Folio2);
                    call0.setString(3, FechaIni);
                    call0.setString(4, FechaFin);
                    call0.setInt(5, ProyectoSelect);
                    call0.setInt(6, 3);

                } else if ((FechaIni != "") && (FechaFin != "") ) {

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
                    System.out.println("Filtro Folios y fechas");
                    call0.setString(1, Folio1);
                    call0.setString(2, Folio2);
                    call0.setString(3, FechaIni);
                    call0.setString(4, FechaFin);
                    call0.setInt(5, ProyectoSelect);
                    call0.setInt(6, 1);

                }

                System.out.println(call0);
                rs = call0.executeQuery();
               
//                System.out.println(ps);
//                rs = ps.executeQuery();
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
                request.getRequestDispatcher("/reimp_remisionesCompras.jsp").forward(request, response);
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

            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger(AdministraRemisionesCompras.class.getName()).log(Level.SEVERE, null, e);
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
