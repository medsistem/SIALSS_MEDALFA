/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facturacion;

import Correo.Correo;
import com.gnk.dao.FacturacionTranDao;
import com.gnk.impl.FacturacionTranDaoImpl;
import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Facturación transaccional
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
@WebServlet(name = "FacturacionTran", urlPatterns = {"/FacturacionTran"})
public class FacturacionTran extends HttpServlet {

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
        response.setContentType("text/html; charset=UTF-8");
        ConectionDB con = new ConectionDB();
        JSONArray jsona;
        ServletContext conexto = request.getServletContext();
        HttpSession sesion = request.getSession(true);

        String accion = request.getParameter("accion");
        String Clapro = request.getParameter("Clapro");
        String ClaUni = request.getParameter("ClaUni");

        String Usuario = (String) sesion.getAttribute("nombre");
        JSONObject json = new JSONObject();
        String ClaUnidad = request.getParameter("ClaUni");
        String ClaPro = request.getParameter("ClaPro");
        String Tipo = request.getParameter("Tipo");
        String FecEnt = request.getParameter("FecEnt");
        String Obs = request.getParameter("Obs");
        String OC = request.getParameter("OC");

        switch (accion) {
            case "obtenerIdReg":
                try (PrintWriter out = response.getWriter()) {
                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
                    jsona = consultaDatos.getRegistro(Clapro);
                    out.println(jsona);
                }
                break;

            case "obtenerIdRegFact":
                try (PrintWriter out = response.getWriter()) {
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
                    jsona = consultaDatos.getRegistroFact(ClaUni, Catalogo);
                    out.println(jsona);
                }
                break;
                //Automatica paso 1
            case "obtenerIdRegFactAuto":
                try (PrintWriter out = response.getWriter()) {
                    String Catalogo = request.getParameter("Catalogo");
                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
                    jsona = consultaDatos.getRegistroFactAuto(ClaUni, Catalogo);
                    out.println(jsona);
                }
                break;

            case "obtenerIdRegFactAutoNivel":
                try (PrintWriter out = response.getWriter()) {
                    String Catalogo = request.getParameter("Catalogo");
                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
                    jsona = consultaDatos.getRegistroFactAutoNivel(ClaUni, Catalogo);
                    out.println(jsona);
                }
                break;

            case "obtenerIdRegFactAutoLote":
                try (PrintWriter out = response.getWriter()) {
                    String Catalogo = request.getParameter("Catalogo");
                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
                    jsona = consultaDatos.getRegistroFactAutoLote(ClaUni, Catalogo);
                    out.println(jsona);
                }
                break;

//            case "obtenerIdRegFactAutoCause":
//                try (PrintWriter out = response.getWriter()) {
//                    String Catalogo = request.getParameter("Catalogo");
//                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
//                    FacturacionTranDaoImpl consultaDatos = new FacturacionTranDaoImpl();
//                    jsona = consultaDatos.RegistroFactAutoCause(ClaUni, Catalogo, Proyecto);
//                    out.println(jsona);
//                }
//                break;

            case "RegresarCaptura":
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                response.sendRedirect("facturacionManual.jsp");
                break;

            case "RegresarCapturaSemiCause":
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                response.sendRedirect("facturacionSemiManual.jsp");
                break;

            case "RegresarCapturaCause":
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                response.sendRedirect("facturacionManualCause.jsp");
                break;

            case "RegresarCapturaFOLIO":
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                response.sendRedirect("facturacionManualFolio.jsp");
                break;

            case "RegresarCapturaTranferProductoProy":
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                response.sendRedirect("productotransferencia.jsp");
                break;

            case "TerminoCaptura":
                sesion.setAttribute("ClaCliFM", "");
                sesion.setAttribute("FechaEntFM", "");
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                sesion.setAttribute("F_IndGlobal", null);
                response.sendRedirect("facturacionManual.jsp");
                break;

            case "TerminoCapturaLote":
                sesion.setAttribute("ClaCliFM", "");
                sesion.setAttribute("FechaEntFM", "");
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                sesion.setAttribute("F_IndGlobal", null);
                response.sendRedirect("facturaAtomaticaLote.jsp");
                break;

            case "TerminoCapturaSemiCause":
                sesion.setAttribute("ClaCliFM", "");
                sesion.setAttribute("FechaEntFM", "");
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                sesion.setAttribute("F_IndGlobal", null);
                response.sendRedirect("facturacionSemiManual.jsp");
                break;

            case "TerminoCapturaCause":
                sesion.setAttribute("ClaCliFM", "");
                sesion.setAttribute("FechaEntFM", "");
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                sesion.setAttribute("F_IndGlobal", null);
                response.sendRedirect("facturacionManualCause.jsp");
                break;

            case "TerminoCapturaTransfer":
                sesion.setAttribute("ClaCliFM", "");
                sesion.setAttribute("FechaEntFM", "");
                sesion.setAttribute("ClaProFM", "");
                sesion.setAttribute("DesProFM", "");
                sesion.setAttribute("F_IndGlobal", null);
                response.sendRedirect("productotransferencia.jsp");
                break;

            case "TerminoSugerencia":
                response.sendRedirect("Sugerencia.jsp");
                break;

            case "TerminoSugerenciaCompra":
                response.sendRedirect("SugerenciaCompra.jsp");
                break;
                
                //automatica
            case "ActualizaReqId":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Cantidad = Integer.parseInt(request.getParameter("Cantidad"));
                    String cantString =request.getParameter("CantidadReq");
                    int CantidadReq = Cantidad;
                    if(cantString != null){
                        Integer.parseInt(request.getParameter("CantidadReq"));
                    }
                    int IdREg = Integer.parseInt(request.getParameter("IdRegistro"));
                    byte[] a = request.getParameter("Obs").getBytes("ISO-8859-1");
                    String Observaciones = (new String(a, "UTF-8")).toUpperCase();
                    
                    FacturacionTranDao Requerimiento = new FacturacionTranDaoImpl();
                    
                    boolean ActRequerimiento = Requerimiento.ActualizaREQ(ClaUnidad, ClaPro, Cantidad, Catalogo, IdREg, Observaciones, CantidadReq);
                    json.put("msj", ActRequerimiento);
                    out.print(json);
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case "ActualizaReqIdLote":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Cantidad = Integer.parseInt(request.getParameter("Cantidad"));
                    int IdREg = Integer.parseInt(request.getParameter("IdRegistro"));
                    byte[] a = request.getParameter("Obs").getBytes("ISO-8859-1");
                    String Observaciones = (new String(a, "UTF-8")).toUpperCase();
                    System.out.println("Obs=" + Observaciones);
                    FacturacionTranDao Requerimiento = new FacturacionTranDaoImpl();
                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                    boolean ActRequerimiento = Requerimiento.ActualizaREQLote(ClaUnidad, ClaPro, Cantidad, Catalogo, IdREg, Observaciones);
                    json.put("msj", ActRequerimiento);
                    out.print(json);
                    out.close();
                }
                break;

//            case "ActualizaReqIdCause":
//                try (PrintWriter out = response.getWriter()) {
//                    json = new JSONObject();
//                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
//                    int Cantidad = Integer.parseInt(request.getParameter("Cantidad"));
//                    int IdREg = Integer.parseInt(request.getParameter("IdRegistro"));
//                    byte[] a = request.getParameter("Obs").getBytes("ISO-8859-1");
//                    String Observaciones = (new String(a, "UTF-8")).toUpperCase();
//                    System.out.println("Obs=" + Observaciones);
//                    FacturacionTranDao Requerimiento = new FacturacionTranDaoImpl();
//                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
//                    boolean ActRequerimiento = Requerimiento.ActualizaREQCause(ClaUnidad, ClaPro, Cantidad, Catalogo, IdREg, Observaciones);
//                    json.put("msj", ActRequerimiento);
//                    out.print(json);
//                    out.close();
//                }
//                break;

            case "GeneraFolio":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao RegistroFolio = new FacturacionTranDaoImpl();
                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistrarFolio = RegistroFolio.RegistrarFolios(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
                    json.put("msj", RegistrarFolio);
                    out.print(json);
                    out.close();
                }
                break;

            case "GeneraFolioN1":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao RegistroFolion1 = new FacturacionTranDaoImpl();
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistrarFolion1 = RegistroFolion1.RegistrarFoliosN1(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
                    json.put("msj", RegistrarFolion1);
                    out.print(json);
                    out.close();
                }
                break;

            case "GeneraFolioLote":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao RegistroFolio = new FacturacionTranDaoImpl();
                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistrarFolio = RegistroFolio.RegistrarFoliosLote(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto);
                    json.put("msj", RegistrarFolio);
                    out.print(json);
                    out.close();
                }
                break;

            case "GeneraFolioApartMich":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao RegistroFoliomich = new FacturacionTranDaoImpl();
                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistrarFolioApartMich = RegistroFoliomich.RegistrarFoliosApartarMich(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);

                    json.put("msj", RegistrarFolioApartMich);
                    out.print(json);
                    out.close();
                }
                break;

//            case "GeneraFolioApartAnestesia":
//                try (PrintWriter out = response.getWriter()) {
//                    json = new JSONObject();
//                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
//                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
//                    FacturacionTranDao RegistroFolioAnestesia = new FacturacionTranDaoImpl();
//                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
//                    if (Obs == null) {
//                        Obs = "";
//                    }
//                    boolean RegistrarFolioApartAnestesia = RegistroFolioAnestesia.RegistrarFoliosApartarAnestesia(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
//
//                    json.put("msj", RegistrarFolioApartAnestesia);
//                    out.print(json);
//                    out.close();
//                }
//                break;

//            case "GeneraFolioApart5Folio":
//                try (PrintWriter out = response.getWriter()) {
//                    json = new JSONObject();
//                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
//                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
//                    FacturacionTranDao RegistroFolio5folio = new FacturacionTranDaoImpl();
//                    if (Obs == null) {
//                        Obs = "";
//                    }
//                    boolean RegistroFolio5Folio = RegistroFolio5folio.RegistrarFoliosApartar5Folio(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
//
//                    json.put("msj", RegistroFolio5Folio);
//                    out.print(json);
//                    out.close();
//                }
//                break;

            case "GeneraFolioMich":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao RegistroFoliomich = new FacturacionTranDaoImpl();
                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistrarFolioMich = RegistroFoliomich.RegistrarFoliosMich(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);

                    json.put("msj", RegistrarFolioMich);
                    out.print(json);
                    out.close();
                }
                break;

//            case "GeneraFolioAnestesia":
//                try (PrintWriter out = response.getWriter()) {
//                    json = new JSONObject();
//                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
//                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
//                    FacturacionTranDao RegistroFolioanestesia = new FacturacionTranDaoImpl();
//                    if (Obs == null) {
//                        Obs = "";
//                    }
//                    boolean RegistroFolioAnestesia = RegistroFolioanestesia.RegistrarFoliosAnestesia(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
//
//                    json.put("msj", RegistroFolioAnestesia);
//                    out.print(json);
//                    out.close();
//                }
//                break;

            /*case "GeneraFolio5Folio":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao Registro5folio = new FacturacionTranDaoImpl();
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean Registro5Folio = Registro5folio.Registro5Folio(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);

                    json.put("msj", Registro5Folio);
                    out.print(json);
                    out.close();
                }
                break;*/

//            case "GeneraFolioCause":
//                try (PrintWriter out = response.getWriter()) {
//                    json = new JSONObject();
//                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
//                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
//                    FacturacionTranDao RegistroFolio = new FacturacionTranDaoImpl();
//                    //System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
//                    if (Obs == null) {
//                        Obs = "";
//                    }
//                    boolean RegistrarFolio = RegistroFolio.RegistrarFoliosCause(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);
//                    json.put("msj", RegistrarFolio);
//                    out.print(json);
//                    out.close();
//                }
//                break;

            case "GeneraFolioApart2Folio":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    String agrupacion = request.getParameter("agrupacion");
                    FacturacionTranDao RegistroFolio2folio = new FacturacionTranDaoImpl();
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean RegistroFolio2Folio = RegistroFolio2folio.RegistrarFoliosApartar2Folio(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC, agrupacion);

                    json.put("msj", RegistroFolio2Folio);
                    out.print(json);
                    out.close();
                }
                break;
                 //AUTOMATICA
            case "GeneraFolio2Folio":
                try (PrintWriter out = response.getWriter()) {
                    json = new JSONObject();
                    int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                    int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                    FacturacionTranDao Registro2folio = new FacturacionTranDaoImpl();
                    if (Obs == null) {
                        Obs = "";
                    }
                    boolean Registro2Folio = Registro2folio.Registro2Folio(ClaUnidad, Catalogo, Tipo, FecEnt, Usuario, Obs, Proyecto, OC);

                    json.put("msj", Registro2Folio);
                    out.print(json);
                    out.close();
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
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        Correo correo = new Correo();
        switch (accion) {
//            case "ConfirmarSugerencia":
//                String ObsGral = request.getParameter("ObsGral");
//                String Solicitante = request.getParameter("Solicitante");
//                json = new JSONObject();
//                FacturacionTranDao confirmarS = new FacturacionTranDaoImpl();
//                boolean ConfirmarS = confirmarS.ConfirmarSugerencia(ObsGral, Solicitante);
//                if (ConfirmarS == true) {
//                    correo.enviaCorreoSugerencia(ObsGral, Solicitante);
//                }
//                json.put("msj", ConfirmarS);
//                out.print(json);
//                out.close();
//                break;
//            case "ConfirmarSugerenciaCompra":
//                String ObsGrals = request.getParameter("ObsGral");
//                String Solicitantes = request.getParameter("Solicitante");
//                json = new JSONObject();
//                FacturacionTranDao confirmar = new FacturacionTranDaoImpl();
//                boolean Confirmar = confirmar.ConfirmarSugerenciaCompra(ObsGrals, Solicitantes);
//                if (Confirmar == true) {
//                    correo.enviaCorreoSugerenciaCompra(ObsGrals, Solicitantes);
//                }
//                json.put("msj", Confirmar);
//                out.print(json);
//                out.close();
//                break;
        }//

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
        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession();
        PrintWriter out = response.getWriter();
        String Usuario = (String) sesion.getAttribute("nombre");
        String FolioFact = (String) sesion.getAttribute("F_IndGlobal");
        String FechaEnt = (String) sesion.getAttribute("FechaEntFM");
        String ClaUni = (String) sesion.getAttribute("ClaCliFM");
        String ClaUnidad = request.getParameter("ClaUni");
        String ClaPro = request.getParameter("ClaPro");
        String Cause = request.getParameter("Cause");
        String[] F_CLaCliArray = ClaUni.split(" - ");
        String OC = request.getParameter("OC");
        String Tipo = request.getParameter("Tipo");
        ClaUni = F_CLaCliArray[0];
        JSONObject json = new JSONObject();

        switch (accion) {
            case "RegistrarDatos":
                json = new JSONObject();
                String IdLote = request.getParameter("IdLote");
                int CantMov = Integer.parseInt(request.getParameter("CantMov"));
                FacturacionTranDao facturacion = new FacturacionTranDaoImpl();
                boolean insertFacTemp = facturacion.RegistraDatosFactTemp(FolioFact, ClaUni, IdLote, CantMov, FechaEnt, Usuario);
                json.put("msj", insertFacTemp);
                out.print(json);
                out.close();
                break;

            case "ActualizaReq":
                json = new JSONObject();
                int Catalogo = Integer.parseInt(request.getParameter("Catalogo"));
                int Cantidad = Integer.parseInt(request.getParameter("Cantidad"));
                FacturacionTranDao Requerimiento = new FacturacionTranDaoImpl();
                System.out.println("ClaUni=" + ClaUnidad + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
                //boolean ActRequerimiento = Requerimiento.ActualizaREQ(ClaUnidad, ClaPro, Cantidad, Catalogo);
                //json.put("msj", ActRequerimiento);
                out.print(json);
                out.close();
                break;

            case "ConformarFactTemp":
                json = new JSONObject();
                int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
                byte[] a = request.getParameter("Obs").getBytes("ISO-8859-1");
                String Observaciones = (new String(a, "UTF-8")).toUpperCase();
                FacturacionTranDao registrarFactTemp = new FacturacionTranDaoImpl();
                boolean RegisFacTemp = registrarFactTemp.ConfirmarFactTemp(Usuario, Observaciones, Tipo, Proyecto, OC);
                json.put("msj", RegisFacTemp);
                out.print(json);
                out.close();
                break;

            case "ConformarFactTempLote":
                json = new JSONObject();
                int ProyectoL = Integer.parseInt(request.getParameter("Proyecto"));
                String ClaCliSelect = request.getParameter("ClaCli");
                String ObservacionesL = request.getParameter("Obs").toUpperCase();
                
                FacturacionTranDao registrarFactTempL = new FacturacionTranDaoImpl();
                boolean RegisFacTempLote = registrarFactTempL.ConfirmarFactTempL(Usuario, ObservacionesL, Tipo, ProyectoL, OC, ClaCliSelect);
                //FacturacionTranDao registrarFactTempL = new FacturacionTranDaoImpl();
                //boolean RegisFacTempL = registrarFactTempL.ConfirmarFactTempLote(Usuario, ObservacionesL, Tipo, ProyectoL, OC, ClaCliSelect);
                json.put("msj", RegisFacTempLote);
                out.print(json);
                out.close();
                break;

//            case "ConformarFactTempSemiCause":
//                json = new JSONObject();
//                int ProyectoSemi = Integer.parseInt(request.getParameter("Proyecto"));
//                byte[] as = request.getParameter("Obs").getBytes("ISO-8859-1");
//                String Observacionesemi = (new String(as, "UTF-8")).toUpperCase();
//                FacturacionTranDao registrarFactTempsemi = new FacturacionTranDaoImpl();
//                boolean RegisFacTempsemi = registrarFactTempsemi.ConfirmarFactTempSemiCause(Usuario, Observacionesemi, Tipo, ProyectoSemi, OC);
//                json.put("msj", RegisFacTempsemi);
//                out.print(json);
//                out.close();
//                break;

//            case "ConformarFactTempCause":
//                json = new JSONObject();
//                String TipoC = request.getParameter("Tipo");
//                int ProyectoC = Integer.parseInt(request.getParameter("Proyecto"));
//                byte[] aC = request.getParameter("Obs").getBytes("ISO-8859-1");
//                String ObservacionesC = (new String(aC, "UTF-8")).toUpperCase();
//                FacturacionTranDao registrarFactTempC = new FacturacionTranDaoImpl();
//                boolean RegisFacTempC = registrarFactTempC.ConfirmarFactTempCause(Usuario, ObservacionesC, TipoC, ProyectoC, OC, Cause);
//                json.put("msj", RegisFacTempC);
//                out.print(json);
//                out.close();
//                break;

            case "ConformarFactTempFOLIO":
                json = new JSONObject();
                int ProyectoFOLIO = Integer.parseInt(request.getParameter("Proyecto"));
                FacturacionTranDao registrarFactTempFOLIO = new FacturacionTranDaoImpl();
                boolean RegisFacTempFOLIO = registrarFactTempFOLIO.ConfirmarFactTempFOLIO(Usuario, ProyectoFOLIO);
                json.put("msj", RegisFacTempFOLIO);
                out.print(json);
                out.close();
                break;

            /*case "ConfirmarTranferenciaProyecto":
                json = new JSONObject();
                int ProyectoIni = Integer.parseInt(request.getParameter("Proyecto"));
                int ProyectoFinal = Integer.parseInt(request.getParameter("ProyectoFinal"));
                byte[] aT = request.getParameter("Obs").getBytes("ISO-8859-1");
                String ObservacionesTP = (new String(aT, "UTF-8")).toUpperCase();
                FacturacionTranDao registrarTranferProyecto = new FacturacionTranDaoImpl();
                boolean RegisTranferProyecto = registrarTranferProyecto.ConfirmarTranferenciaProyecto(Usuario, ObservacionesTP, ProyectoIni, ProyectoFinal);
                json.put("msj", RegisTranferProyecto);
                out.print(json);
                out.close();
                break;*/
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
