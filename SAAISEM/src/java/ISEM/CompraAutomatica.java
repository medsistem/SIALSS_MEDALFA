/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISEM;

import Correo.CorreoConfirmaRemision;
import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Captura de compra
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class CompraAutomatica extends HttpServlet {

    ConectionDB con = new ConectionDB();
    NuevoISEM nuevo = new NuevoISEM();
    CorreoConfirmaRemision correoConfirma = new CorreoConfirmaRemision();

    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");

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
        HttpSession sesion = request.getSession(true);
        PrintWriter out = response.getWriter();
        try {
            System.out.println(request.getParameter("accion") + "*****");
            if (request.getParameter("accion").equals("seleccionaClave")) {
                String folio = request.getParameter("folio");
                String folioRemi = request.getParameter("folioRemi");
                String seleccionaClave = request.getParameter("selectClave");
                sesion.setAttribute("NoCompra", request.getParameter("folio"));
                sesion.setAttribute("folioRemi", folioRemi);
                sesion.setAttribute("Lote", "");
                sesion.setAttribute("Cadu", "");
                sesion.setAttribute("CodBar", "");
                sesion.setAttribute("claveSeleccionada", seleccionaClave);
                response.sendRedirect("compraAuto2.jsp");
            }
            if (request.getParameter("accion").equals("CodigoBarras")) {
                try {

                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    String CodBar = request.getParameter("codbar");
                    String lote = request.getParameter("lot").toUpperCase();
                    String cadu = request.getParameter("cad");
                    String seleccionaClave = request.getParameter("ClaPro");
                    System.out.println(seleccionaClave + "----6666666");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", CodBar);
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                    sesion.setAttribute("claveSeleccionada", seleccionaClave);
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("GeneraCodigo")) {
                String CodBar = "";
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("SELECT MAX(F_IdCb) AS F_IdCb FROM tb_gencb");
                    while (rset.next()) {
                        CodBar = rset.getString("F_IdCb");
                    }
                    System.out.println(CodBar);
                    Long CB = Long.parseLong(CodBar) + 1;
                    con.insertar("insert into tb_gencb values('" + CB + "','MEDALFA')");

                    con.cierraConexion();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    String lote = request.getParameter("lot").toUpperCase();
                    String cadu = request.getParameter("cad");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", CodBar);
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("verFolio")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    String CodBar = request.getParameter("codbar");
                    String lote = request.getParameter("lot").toUpperCase();
                    String cadu = request.getParameter("cad");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", CodBar);
                    sesion.setAttribute("Lote", lote);
                    sesion.setAttribute("Cadu", cadu);
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("refresh")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    String CodBar = request.getParameter("codbar");
                    String lote = request.getParameter("lot").toUpperCase();
                    String cadu = request.getParameter("cad");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", CodBar);
                    sesion.setAttribute("Lote", lote);
                    sesion.setAttribute("Cadu", cadu);
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("siguiente")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    int posClave = Integer.parseInt(posCla);
                    posClave++;
                    System.out.println("/////" + posClave);
                    sesion.setAttribute("posClave", posClave);
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", "");
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("anterior")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    int posClave = Integer.parseInt(posCla);
                    posClave--;
                    System.out.println("/////" + posClave);
                    sesion.setAttribute("posClave", posClave);
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", folioRemi);
                    sesion.setAttribute("CodBar", "");
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                    response.sendRedirect("compraAuto2.jsp");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            if (request.getParameter("accion").equals("guardarLote")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", "");
                    sesion.setAttribute("CodBar", "");
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.conectar();
                Calendar c1 = GregorianCalendar.getInstance();
                String Tipo = "";
                double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
                String lote = request.getParameter("lot").toUpperCase();
                String Clave = request.getParameter("ClaPro");
                String cadu = df2.format(df3.parse(request.getParameter("cad")));
                c1.setTime(df3.parse(request.getParameter("cad")));

                ResultSet rset_medica = con.consulta("SELECT F_TipMed,F_Costo FROM tb_medica WHERE F_ClaPro='" + Clave + "'");
                while (rset_medica.next()) {
                    Tipo = rset_medica.getString("F_TipMed");
                    Costo = Double.parseDouble(rset_medica.getString("F_Costo"));
                    if (Tipo.equals("2504")) {
                        c1.add(Calendar.YEAR, -3);
                        IVA = 0.0;
                    } else {
                        IVA = 0.16;
                        c1.add(Calendar.YEAR, -5);
                    }
                }
                Calendar FecAct = GregorianCalendar.getInstance();
                FecAct.setTime(new Date());
                while (c1.after(FecAct)) {
                    c1.add(Calendar.YEAR, -1);
                }
                String fecFab = (df2.format(c1.getTime()));
                String CodBar = request.getParameter("codbar");
                String Tarimas = request.getParameter("Tarimas");
                String claPro = request.getParameter("claPro");
                String Marca = request.getParameter("list_marca");
                byte[] a = request.getParameter("Obser").getBytes("ISO-8859-1");
                String F_Obser = (new String(a, "UTF-8")).toUpperCase();

                String TCajas = request.getParameter("TCajas");
                TCajas = TCajas.replace(",", "");

                if (Tarimas.equals("")) {
                    Tarimas = "0";
                }
                Tarimas = Tarimas.replace(",", "");
                String Cajas = request.getParameter("Cajas");
                if (Cajas.equals("")) {
                    Cajas = "0";
                }
                Cajas = Cajas.replace(",", "");
                String Piezas = request.getParameter("Piezas");
                if (Piezas.equals("")) {
                    Piezas = "0";
                }
                Piezas = Piezas.replace(",", "");
                String TarimasI = request.getParameter("TarimasI");
                if (TarimasI.equals("")) {
                    TarimasI = "0";
                }
                TarimasI = TarimasI.replace(",", "");
                String CajasxTI = request.getParameter("CajasxTI");
                if (CajasxTI.equals("")) {
                    CajasxTI = "0";
                }
                CajasxTI = CajasxTI.replace(",", "");
                String Resto = request.getParameter("Resto");
                if (Resto.equals("")) {
                    Resto = "0";
                }
                Resto = Resto.replace(",", "");
                IVAPro = (Double.parseDouble(Piezas) * Costo) * IVA;
                Monto = Double.parseDouble(Piezas) * Costo;
                MontoIva = Monto + IVAPro;
                con.insertar("insert into tb_compratemp values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','" + Marca + "','" + claPro + "','" + CodBar + "','" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + claPro + "','" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', curtime())"
                );
                con.insertar("insert into tb_compraregistro values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','" + Marca + "','" + claPro + "','" + CodBar + "','" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + claPro + "','" + sesion.getAttribute("nombre") + "')"
                );
                try {
                    con.insertar("insert into tb_pzcaja values (0,'" + claPro + "','" + Marca + "','" + request.getParameter("PzsxCC") + "','" + Clave + "')");
                } catch (Exception e) {
                }
//                con.insertar("insert into tb_cb values(0,'" + CodBar + "','" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "', '" + Marca + "')");
                //con.insertar("update tb_pedidoisem set F_Recibido = '1' where F_Clave = '" + Clave + "' and  ");

                con.cierraConexion();
                response.sendRedirect("compraAuto2.jsp");
            }
            if (request.getParameter("accion").equals("guardarLoteISEM")) {
                try {
                    String posCla = sesion.getAttribute("posClave").toString();
                    String folio = request.getParameter("folio");
                    String folioRemi = request.getParameter("folioRemi");
                    sesion.setAttribute("NoCompra", request.getParameter("folio"));
                    sesion.setAttribute("folioRemi", "");
                    sesion.setAttribute("CodBar", "");
                    sesion.setAttribute("Lote", "");
                    sesion.setAttribute("Cadu", "");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.conectar();
                Calendar c1 = GregorianCalendar.getInstance();
                String Tipo = "";
                double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
                String lote = request.getParameter("lot").toUpperCase();
                String Clave = request.getParameter("ClaPro");
                String cadu = df2.format(df3.parse(request.getParameter("cad")));
                c1.setTime(df3.parse(request.getParameter("cad")));

                ResultSet rset_medica = con.consulta("SELECT F_TipMed,F_Costo FROM tb_medica WHERE F_ClaPro='" + Clave + "'");
                while (rset_medica.next()) {
                    Tipo = rset_medica.getString("F_TipMed");
                    Costo = Double.parseDouble(rset_medica.getString("F_Costo"));
                    if (Tipo.equals("2504")) {
                        c1.add(Calendar.YEAR, -3);
                        IVA = 0.0;
                    } else {
                        IVA = 0.16;
                        c1.add(Calendar.YEAR, -5);
                    }
                }
                Calendar FecAct = GregorianCalendar.getInstance();
                FecAct.setTime(new Date());
                while (c1.after(FecAct)) {
                    c1.add(Calendar.YEAR, -1);
                }
                String fecFab = (df2.format(c1.getTime()));
                String CodBar = request.getParameter("codbar");
                String Tarimas = request.getParameter("Tarimas");
                String claPro = request.getParameter("claPro");
                String Marca = request.getParameter("list_marca");
                byte[] a = request.getParameter("Obser").getBytes("ISO-8859-1");
                String F_Obser = (new String(a, "UTF-8")).toUpperCase();

                String TCajas = request.getParameter("TCajas");
                TCajas = TCajas.replace(",", "");

                if (Tarimas.equals("")) {
                    Tarimas = "0";
                }
                Tarimas = Tarimas.replace(",", "");
                String Cajas = request.getParameter("Cajas");
                if (Cajas.equals("")) {
                    Cajas = "0";
                }
                Cajas = Cajas.replace(",", "");
                String Piezas = request.getParameter("Piezas");
                if (Piezas.equals("")) {
                    Piezas = "0";
                }
                Piezas = Piezas.replace(",", "");
                String TarimasI = request.getParameter("TarimasI");
                if (TarimasI.equals("")) {
                    TarimasI = "0";
                }
                TarimasI = TarimasI.replace(",", "");
                String CajasxTI = request.getParameter("CajasxTI");
                if (CajasxTI.equals("")) {
                    CajasxTI = "0";
                }
                CajasxTI = CajasxTI.replace(",", "");
                String Resto = request.getParameter("Resto");
                if (Resto.equals("")) {
                    Resto = "0";
                }
                Resto = Resto.replace(",", "");
                IVAPro = (Double.parseDouble(Piezas) * Costo) * IVA;
                Monto = Double.parseDouble(Piezas) * Costo;
                MontoIva = Monto + IVAPro;
                con.insertar("insert into tb_compratempisem values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','" + Marca + "','" + claPro + "','" + CodBar + "','" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + claPro + "','" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', curtime())"
                );
                //con.insertar("insert into tb_compraregistro values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','" + Marca + "','" + claPro + "','" + CodBar + "','" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + claPro + "','" + sesion.getAttribute("nombre") + "')");
                try {
                    con.insertar("insert into tb_pzcaja values (0,'" + claPro + "','" + Marca + "','" + request.getParameter("PzsxCC") + "','" + Clave + "')");
                } catch (Exception e) {
                }
                con.insertar("insert into tb_cb values(0,'" + CodBar + "','" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "', '" + Marca + "')");
                //con.insertar("update tb_pedidoisem set F_Recibido = '1' where F_Clave = '" + Clave + "' and  ");

                con.cierraConexion();
                response.sendRedirect("compraAuto2.jsp");
            }
            if (request.getParameter("accion").equals("confirmar")) {
                con.conectar();
                try {
                    ResultSet rset = con.consulta("select * from tb_pedidoisem where F_NoCompra = '" + request.getParameter("folio") + "' and F_Recibido = '0'");
                    while (rset.next()) {
                        Calendar c1 = GregorianCalendar.getInstance();
                        String Tipo = "";
                        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
                        String lote = request.getParameter("lot_" + rset.getString("F_IdIsem"));
                        String Clave = rset.getString("F_Clave");
                        String cadu = df2.format(df3.parse(request.getParameter("cad_" + rset.getString("F_IdIsem"))));
                        c1.setTime(df3.parse(request.getParameter("cad_" + rset.getString("F_IdIsem"))));

                        ResultSet rset_medica = con.consulta("SELECT F_TipMed,F_Costo FROM tb_medica WHERE F_ClaPro='" + Clave + "'");
                        while (rset_medica.next()) {
                            Tipo = rset_medica.getString("F_TipMed");
                            Costo = Double.parseDouble(rset_medica.getString("F_Costo"));
                            if (Tipo.equals("2504")) {
                                c1.add(Calendar.YEAR, -3);
                                IVA = 0.0;
                            } else {
                                c1.add(Calendar.YEAR, -5);
                                IVA = 0.16;
                            }
                        }

                        Calendar FecAct = GregorianCalendar.getInstance();
                        FecAct.setTime(new Date());
                        while (c1.after(FecAct)) {
                            c1.add(Calendar.YEAR, -1);
                        }
                        String fecFab = (df2.format(c1.getTime()));
                        String CodBar = request.getParameter("codbar_" + rset.getString("F_IdIsem"));
                        String Tarimas = request.getParameter("Tarimas_" + rset.getString("F_IdIsem"));
                        String Marca = request.getParameter("list_marca");
                        byte[] a = request.getParameter("Obser_" + rset.getString("F_IdIsem")).getBytes("ISO-8859-1");
                        String F_Obser = (new String(a, "UTF-8")).toUpperCase();

                        if (Tarimas.equals("")) {
                            Tarimas = "0";
                        }
                        String Cajas = request.getParameter("Cajas_" + rset.getString("F_IdIsem"));
                        if (Cajas.equals("")) {
                            Cajas = "0";
                        }
                        Cajas = Cajas.replace(",", "");
                        String Piezas = request.getParameter("Piezas_" + rset.getString("F_IdIsem"));
                        if (Piezas.equals("")) {
                            Piezas = "0";
                        }
                        Piezas = Piezas.replace(",", "");
                        String TarimasI = request.getParameter("TarimasI_" + rset.getString("F_IdIsem"));
                        if (TarimasI.equals("")) {
                            TarimasI = "0";
                        }
                        String CajasxTI = request.getParameter("CajasxTI_" + rset.getString("F_IdIsem"));
                        if (CajasxTI.equals("")) {
                            CajasxTI = "0";
                        }
                        String Resto = request.getParameter("Resto_" + rset.getString("F_IdIsem"));
                        if (Resto.equals("")) {
                            Resto = "0";
                        }
                        IVAPro = (Double.parseDouble(Piezas) * Costo) * IVA;
                        Monto = Double.parseDouble(Piezas) * Costo;
                        MontoIva = Monto + IVAPro;
                        con.insertar("insert into tb_compratemp values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','1','" + rset.getString("F_Provee") + "','" + CodBar + "','" + Tarimas + "','" + Cajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + rset.getString("F_Provee") + "','" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', curtime())"
                        );
                        con.insertar("insert into tb_compraregistro values(0,CURDATE(),'" + Clave + "','" + lote + "','" + cadu + "','" + fecFab + "','1','" + rset.getString("F_Provee") + "','" + CodBar + "','" + Tarimas + "','" + Cajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "','" + request.getParameter("folioRemi") + "','" + request.getParameter("folio") + "','" + rset.getString("F_Provee") + "','" + sesion.getAttribute("nombre") + "')"
                        );

                        System.out.println("update tb_pedidoisem set F_Recibido = '1' where F_IdIsem = '" + rset.getString("F_IdIsem") + "' ");
                        con.insertar("update tb_pedidoisem set F_Recibido = '1' where F_IdIsem = '" + rset.getString("F_IdIsem") + "' ");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                con.cierraConexion();
                int F_IndCom = nuevo.Guardar((String) sesion.getAttribute("nombre"));
                sesion.setAttribute("folioRemi", "");
                out.println("<script>window.open('reimpReporte.jsp?fol_gnkl=" + F_IndCom + "','_blank')</script>");
                out.println("<script>window.open('reimp_marbete.jsp?fol_gnkl=" + F_IndCom + "','_blank')</script>");
                //correoConfirma.enviaCorreo(F_IndCom);
                out.println("<script>window.location='Ubicaciones/Consultas.jsp'</script>");
            }
        } catch (Exception e) {
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
