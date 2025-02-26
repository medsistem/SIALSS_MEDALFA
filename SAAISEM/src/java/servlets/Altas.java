/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import conn.*;
import java.sql.ResultSet;
import javax.servlet.http.HttpSession;

/**
 * Validación y captura de registro del ingreso de las compras
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class Altas extends HttpServlet {

    ConectionDB con = new ConectionDB();
    //ConectionDB_SQLServer consql = new ConectionDB_SQLServer();
    java.text.DateFormat df2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
    java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    java.text.DateFormat df3 = new java.text.SimpleDateFormat("yyyy-MM-dd");

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
        String clave = "", descr = "", cb = "", Cuenta = "", Marca = "", codbar2 = "", PresPro = "";
        int ban1 = 0;
        String boton = request.getParameter("accion");
        String ancla = "";
        try {
            if (request.getParameter("accion").equals("codigo")) {
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("SELECT F_Cb,COUNT(F_Cb) as cuenta FROM tb_cb WHERE F_Cb='" + request.getParameter("codigo") + "' GROUP BY F_Cb");
                    while (rset.next()) {
                        ban1 = 1;
                        cb = rset.getString("F_Cb");
                        Cuenta = rset.getString("cuenta");
                    }
                    if (Cuenta.equals("")) {
                        Cuenta = "0";
                        cb = request.getParameter("codigo");
                        ban1 = 2;
                    }
                    ancla = "#codigo";
                    con.cierraConexion();
                } catch (Exception e) {

                }
            }
            if (request.getParameter("accion").equals("clave")) {
                try {
                    con.conectar();
                    cb = request.getParameter("cb");
                    ResultSet rset = con.consulta("select F_ClaPro, F_DesPro,F_PrePro from tb_medica where F_ClaPro='" + request.getParameter("clave") + "' AND F_StsPro ='A'");
                    while (rset.next()) {
                        ban1 = 1;
                        clave = rset.getString("F_ClaPro");
                        descr = rset.getString("F_DesPro");
                        PresPro = rset.getString("F_PrePro");
                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            }
            if (request.getParameter("accion").equals("CodBar")) {
                try {
                    con.conectar();
                    ResultSet rset = con.consulta("SELECT MAX(F_IdCb) AS F_IdCb FROM tb_gencb");
                    while (rset.next()) {
                        ban1 = 1;
                        codbar2 = rset.getString("F_IdCb");
                    }
                    System.out.println(codbar2);
                    Long CB = Long.parseLong(codbar2) + 1;
                    con.insertar("insert into tb_gencb values('" + CB + "','MEDALFA')");
                    descr = request.getParameter("descripci");
                    clave = request.getParameter("clave1");
                    rset = con.consulta("select F_PrePro from tb_medica where F_ClaPro='" + clave + "'");
                    while (rset.next()) {
                        PresPro = rset.getString("F_PrePro");
                    }
                    Marca = request.getParameter("Marca");
                    con.cierraConexion();
                } catch (Exception e) {

                }
            }
            if (request.getParameter("accion").equals("descripcion")) {
                try {
                    con.conectar();
                    cb = request.getParameter("cb");
                    ResultSet rset = con.consulta("select F_ClaPro, F_DesPro, F_PrePro from tb_medica where F_DesPro='" + request.getParameter("descr") + "' AND F_StsPro='A'");
                    while (rset.next()) {
                        ban1 = 1;
                        clave = rset.getString("F_ClaPro");
                        descr = rset.getString("F_DesPro");
                        PresPro = rset.getString("F_PrePro");
                    }
                    con.cierraConexion();
                } catch (Exception e) {

                }
            }
            if (request.getParameter("accion").equals("refresh")) {
                try {
                    ban1 = 1;
                    descr = request.getParameter("descripci");
                    clave = request.getParameter("clave1");
                    PresPro = request.getParameter("Presentaci");
                } catch (Exception e) {

                }
            }
            if (request.getParameter("accion").equals("capturar")) {
                ban1 = 1;
                String cla_pro = request.getParameter("clave1");
                String Tipo = "", FechaC = "", FechaF = "", CostoCap = "", PzsxCC = "", claveLarga = "";
                double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
                int fcdu = 0, anofec = 0;
                String lot_pro = request.getParameter("Lote").toUpperCase();
                String cdd = request.getParameter("cdd");
                String cmm = request.getParameter("cmm");
                String caa = request.getParameter("caa");
                String FeCad = caa + "-" + cmm + "-" + cdd;
                CostoCap = request.getParameter("F_Costo");
                CostoCap = CostoCap.replace(",", "");
                Costo = Double.parseDouble(CostoCap);
                try {
                    int cajas = Integer.parseInt((request.getParameter("Cajas")).replace(",", ""));
                    int piezas = Integer.parseInt((request.getParameter("Piezas")).replace(",", ""));
                    int tarimas = Integer.parseInt((request.getParameter("TarimasC")).replace(",", ""));

                    con.conectar();

                    ResultSet rset_medica = con.consulta("SELECT F_ClaProSS, F_TipMed,F_Costo FROM tb_medica WHERE F_ClaPro='" + cla_pro + "'");
                    while (rset_medica.next()) {
                        Tipo = rset_medica.getString("F_TipMed");
                        claveLarga = rset_medica.getString("F_ClaProSS");
                        //Costo = Double.parseDouble(rset_medica.getString("F_Costo"));
                        if (Tipo.equals("2504")) {
                            IVA = 0.0;
                            fcdu = Integer.parseInt(caa);
                            anofec = fcdu - 3;
                        } else {
                            IVA = 0.16;
                            fcdu = Integer.parseInt(caa);
                            anofec = fcdu - 5;
                        }
                    }
                    String FeFab = anofec + "-" + cmm + "-" + cdd;
                    String CodBar = request.getParameter("codbar");
                    String Tarimas = request.getParameter("Tarimas");
                    String claPro = request.getParameter("claPro");
                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
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
                        throw new Exception("Piezas en 0");
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
                    PzsxCC = request.getParameter("PzsxCC");
                    if (PzsxCC.equals("")) {
                        PzsxCC = "0";
                    }
                    String factorEmpaque = request.getParameter("factorEmpaque");
                    String ordenSuministro = request.getParameter("ordenSuministro");
                    if (ordenSuministro == null) {
                        ordenSuministro = "";
                    }

                    if (factorEmpaque.equals("")) {
                        factorEmpaque = "1";
                    }
                    String unidadFonsabi = request.getParameter("unidadFonsabi");
                    if(unidadFonsabi == null || unidadFonsabi.equals("null")) {
                        System.out.println("unidad" + unidadFonsabi);
                        unidadFonsabi = "";
                    }
                                      
                   
                    PzsxCC = PzsxCC.replace(",", "");
                    Resto = Resto.replace(",", "");
                    IVAPro = (Double.parseDouble(Piezas) * Costo) * IVA;
                    Monto = Double.parseDouble(Piezas) * Costo;
                    MontoIva = Monto + IVAPro;
                    String nombrecomercial = request.getParameter("lMarcaR");
                    String CartaCanje = request.getParameter("");
                    String FuenteFinan = request.getParameter("");

//                  con.insertar("insert into tb_compratemp values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + tarimas + "','" + cajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + PzsxCC + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', '" + request.getParameter("F_Proyectos") + "', curtime()) ");
                    System.out.println("si estor aqui por clave");
                    con.insertar("insert into tb_compratemp values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + PzsxCC + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "','" + request.getParameter("F_Proyectos") + "',curtime(), '" + factorEmpaque + "', '" + ordenSuministro + "' ,null,'','','','" + nombrecomercial + "', '" + unidadFonsabi + "'); ");
                    con.insertar("insert into tb_compraregistro values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','" + request.getParameter("F_Proyectos") + "', '') ");

                    con.insertar("insert into tb_pzcaja values (0,'" + request.getParameter("provee") + "','" + request.getParameter("Marca") + "','" + request.getParameter("PzsxCC") + "','" + cla_pro.toUpperCase() + "', '" + claveLarga + "')");
//                    con.insertar("insert into tb_cb values(0,'" + request.getParameter("cb") + "','" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "', '" + request.getParameter("Marca") + ", "+factorEmpaque+"')");
                    con.cierraConexion();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (request.getParameter("accion").equals("capturarcb")) {
                ban1 = 1;
                String cla_pro = request.getParameter("clave1");
                String Tipo = "", FechaC = "", FechaF = "", PzsxCC = "";
                double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
                String lot_pro = request.getParameter("Lote");
                String FeCad = df3.format(df2.parse(request.getParameter("cdd")));
                String FeFab = df3.format(df2.parse(request.getParameter("fdd")));

                try {
                    int cajas = Integer.parseInt((request.getParameter("Cajas")).replace(",", ""));
                    int piezas = Integer.parseInt((request.getParameter("Piezas")).replace(",", ""));
                    int tarimas = Integer.parseInt((request.getParameter("Tarimas")).replace(",", ""));

                    con.conectar();

                    ResultSet rset_medica = con.consulta("SELECT F_TipMed,F_Costo FROM tb_medica WHERE F_ClaPro='" + cla_pro + "'");
                    while (rset_medica.next()) {
                        Tipo = rset_medica.getString("F_TipMed");
                        Costo = Double.parseDouble(rset_medica.getString("F_Costo"));
                        if (Tipo.equals("2504")) {
                            IVA = 0.0;
                        } else {
                            IVA = 0.16;
                        }
                    }

                    String Tarimas = request.getParameter("Tarimas");
                    byte[] a = request.getParameter("Observaciones").getBytes("ISO-8859-1");
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
                    PzsxCC = request.getParameter("PzsxCC");
                    if (PzsxCC.equals("")) {
                        PzsxCC = "0";
                    }
                    String factorEmpaque = request.getParameter("factorEmpaque");
                    String ordenSuministro = request.getParameter("ordenSuministro");
                    if (factorEmpaque.equals("")) {
                        factorEmpaque = "1";
                    }
                    Resto = Resto.replace(",", "");
                    IVAPro = (Double.parseDouble(Piezas) * Costo) * IVA;
                    Monto = Double.parseDouble(Piezas) * Costo;
                    MontoIva = Monto + IVAPro;
//                  con.insertar("insert into tb_compratemp values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', curtime()) ");
//                  con.insertar("insert into tb_compratemp values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + Cajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + PzsxCC + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "', '" + request.getParameter("F_Proyectos") + "','','','', curtime(), "+factorEmpaque+") ");
                    System.out.println("si estor aqui");
                    con.insertar("insert into tb_compratemp values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + PzsxCC + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "','1', '" + request.getParameter("F_Origen") + "','" + request.getParameter("F_Proyectos") + "',curtime(), '" + factorEmpaque + "', '" + ordenSuministro + "', null,'','') ");
                    con.insertar("insert into tb_compraregistro values (0,curdate(),'" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "','" + request.getParameter("Marca") + "','" + request.getParameter("provee") + "','" + request.getParameter("cb") + "', '" + Tarimas + "','" + TCajas + "','" + Piezas + "','" + TarimasI + "','" + CajasxTI + "','" + Resto + "','" + Costo + "','" + IVAPro + "','" + MontoIva + "','" + F_Obser + "' , '" + request.getParameter("folio_remi") + "', '" + request.getParameter("orden") + "','" + request.getParameter("provee") + "' ,'" + sesion.getAttribute("nombre") + "') ");
                    con.insertar("insert into tb_pzcaja values (0,'" + request.getParameter("provee") + "','" + Marca + "','" + request.getParameter("PzsxCC") + "','" + cla_pro.toUpperCase() + "')");
//                     con.insertar("insert into tb_cb values(0,'" + request.getParameter("cb") + "','" + cla_pro.toUpperCase() + "','" + lot_pro + "','" + FeCad + "','" + FeFab + "', '" + request.getParameter("Marca") + ","+factorEmpaque+"')");

                    con.cierraConexion();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        request.getSession().setAttribute("folio", request.getParameter("folio"));
        request.getSession().setAttribute("fecha", request.getParameter("fecha"));
        request.getSession().setAttribute("folio_remi", request.getParameter("folio_remi"));
        request.getSession().setAttribute("orden", request.getParameter("orden"));
        request.getSession().setAttribute("provee", request.getParameter("provee"));
        request.getSession().setAttribute("recib", request.getParameter("recib"));
        request.getSession().setAttribute("entrega", request.getParameter("entrega"));
        request.getSession().setAttribute("clave", clave);
        request.getSession().setAttribute("descrip", descr);
        request.getSession().setAttribute("cuenta", Cuenta);
        request.getSession().setAttribute("cb", cb);
        request.getSession().setAttribute("codbar2", codbar2);
        request.getSession().setAttribute("Marca", Marca);
        request.getSession().setAttribute("PresPro", PresPro);

        //String original = "hello world";
        //byte[] utf8Bytes = original.getBytes("UTF8");
        //String value = new String(utf8Bytes, "UTF-8"); 
        //out.println(value);
        if (ban1 == 0) {
            out.println("<script>alert('Clave Inexistente')</script>");
            out.println("<script>window.location='captura.jsp'</script>");
        } else if (ban1 == 1) {
            out.println("<script>window.location='captura.jsp'</script>");
        } else if (ban1 == 2) {
            request.getSession().setAttribute("CBInex", "1");
            out.println("<script>alert('CB Inexistente, Favor de Llenar todos los Campos')</script>");
            out.println("<script>window.location='captura.jsp'</script>");
        }
        //response.sendRedirect("captura.jsp");
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
