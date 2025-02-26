/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.gnk.dao.InterfaceSenderoDao;
import com.gnk.impl.InterfaceSendetoDaoImpl;
import com.medalfa.saa.model.Volumetria;
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
 * Recepción transaccional del ingreso de las compras
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
@WebServlet(name = "recepcionTransaccional", urlPatterns = {"/recepcionTransaccional"})
public class recepcionTransaccional extends HttpServlet {

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

        String accion = request.getParameter("accion");
        HttpSession sesion = request.getSession(true);
        JSONArray jsona;
        ServletContext conexto = request.getServletContext();
        String vOrden = request.getParameter("vOrden");
        String vRemi = request.getParameter("vRemi");

        switch (accion) {
            
            case "obtenerIdReg":
                try (PrintWriter out = response.getWriter()) {
                    InterfaceSenderoDao consultaDatos = new InterfaceSendetoDaoImpl();
                    jsona = consultaDatos.getRegistro(vOrden, vRemi);
                    out.println(jsona);
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
        JSONObject json = new JSONObject();
        JSONArray jsona;

        switch (accion) {
            
            case "Editar":
                int id = Integer.parseInt(request.getParameter("id"));
                
                InterfaceSenderoDao editar = new InterfaceSendetoDaoImpl();
                json = editar.datosAditar(id);
                out.print(json);
                out.close();

                break;
            case "EditarLotes":
                json = new JSONObject();
                int tarimas = 0,
                 tarimasI = 0,
                 cajas = 0,
                 pzacaja = 0,
                 cajasi = 0,
                 resto = 0;
                id = Integer.parseInt(request.getParameter("id"));
                String lote = request.getParameter("lote");
                String caducidad = request.getParameter("caducidad");
                String tarimas1 = request.getParameter("tarimas");
                String cajas1 = request.getParameter("cajas");
                String pzacaja1 = request.getParameter("pzacaja");
                String cajasi1 = request.getParameter("cajasi");
                String resto1 = request.getParameter("resto");
                String costo1 = request.getParameter("costo");
                String factorEmpaque = request.getParameter("factorEmpaque");
                String ordenSuministro = request.getParameter("ordenSuministro");
                String origen = request.getParameter("origen");
                String cartaCanje = request.getParameter("cartaCanje");
                String marcaComercial = request.getParameter("marcaComercial");
                String unidadFonsabi = request.getParameter("unidadFonsabi");
                System.out.println("imprime la marca comercial " + marcaComercial);

                Volumetria volumetria = this.buildVolumetria(request);
                
                if ((tarimas1.equals("")) || (tarimas1 == null)) {
                    tarimas1 = "0";
                }
                tarimas1 = tarimas1.replace(",", "");
                if ((cajas1.equals("")) || (cajas1 == null)) {
                    cajas1 = "0";
                }
                cajas1 = cajas1.replace(",", "");
                if ((costo1.equals("")) || (costo1 == null)) {
                    costo1 = "0";
                }
                costo1 = costo1.replace(",", "");
                if ((pzacaja1.equals("")) || (pzacaja1 == null)) {
                    pzacaja1 = "0";
                }
                pzacaja1 = pzacaja1.replace(",", "");
                if ((cajasi1.equals("")) || (cajasi1 == null)) {
                    cajasi1 = "0";
                }
                cajasi1 = cajasi1.replace(",", "");
                if ((resto1.equals("")) || (resto1 == null)) {
                    resto1 = "0";
                }
                
                resto1 = resto1.replace(",", "");
                tarimas = Integer.parseInt(tarimas1);
                cajas = Integer.parseInt(cajas1);
                pzacaja = Integer.parseInt(pzacaja1);
                cajasi = Integer.parseInt(cajasi1);
                resto = Integer.parseInt(resto1);

                //int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                int cantidad = ((((tarimas * cajas) + cajasi) * pzacaja) + resto);

                if (cajasi > 0) {
                    tarimasI = 1;
                }
                cajas = cajas * tarimas;
                String cb = request.getParameter("cb");
                String marca = request.getParameter("marca");
                String usuario = request.getParameter("usuario");
               

                InterfaceSenderoDao editarUpdate = new InterfaceSendetoDaoImpl();
//                boolean save = editarUpdate.ActualizarDatos(usuario, lote, caducidad, cantidad, cb, marca, id, tarimas, cajas, pzacaja, cajasi, resto, tarimasI, costo1);
                  boolean save = editarUpdate.ActualizarDatos(usuario, lote, caducidad, cantidad, cb, marca, id, tarimas, cajas, pzacaja, cajasi, resto, tarimasI, costo1, Integer.parseInt(factorEmpaque), ordenSuministro,  volumetria, cartaCanje, marcaComercial, unidadFonsabi);
                    System.out.println("cartaCanje: "+cartaCanje);
                    System.out.println("nombrecomercial recepcion: " + marcaComercial );
                    System.out.println("unidad: "  + unidadFonsabi);
                    json.put("msj", save);
                out.print(json);
                out.close();
                break;
                
                //liberacion de OC
            case "IngresarRemision":
                json = new JSONObject();
                String ordenCompra = request.getParameter("ordenCompra");
                String remision = request.getParameter("remision");
                String UbicaN = request.getParameter("UbicaN");
                String unidadFon = request.getParameter("unidadFon");
                String MarcaR = request.getParameter("MarcaR");
                if(unidadFon.equals("null")){
                    unidadFon = "";
                }
                InterfaceSenderoDao ingresarLermaSendero = new InterfaceSendetoDaoImpl();
                boolean actualizado = false;
                if (ingresarLermaSendero.Actualizarlerma(ordenCompra, remision , (String) sesion.getAttribute("nombre"), UbicaN, unidadFon)) {
                    json.put("msj", true);
                } else {
                    json.put("msj", true);
                }

                sesion.setAttribute("vOrden", "");
                sesion.setAttribute("vRemi", "");
                out.print(json);
                out.close();
                break;

//            case "IngresarRemisionCross":
//                json = new JSONObject();
//                String ordenCompraCross = request.getParameter("ordenCompra");
//                String remisionCross = request.getParameter("remision");
//                InterfaceSenderoDao ingresarLermaSenderoCross = new InterfaceSendetoDaoImpl();
//                boolean actualizadoCross = false;
//                if (ingresarLermaSenderoCross.ActualizarlermaCross(ordenCompraCross, remisionCross, (String) request.getSession().getAttribute("nombre"))) {
//                    json.put("msj", true);
//                } else {
//                    json.put("msj", true);
//                }
//
//                sesion.setAttribute("vOrden", "");
//                sesion.setAttribute("vRemi", "");
//                out.print(json);
//                out.close();
//                break;

            case "RegistrarDatosParcial":
                json = new JSONObject();
                String IdReg = request.getParameter("IdReg");
                String ordenCompraP = request.getParameter("vOrden");
                String remisionP = request.getParameter("vRemi");
                UbicaN = request.getParameter("UbicaN");
//                MarcaR = request.getParameter("MarcaR");
                InterfaceSenderoDao ingresarParcial = new InterfaceSendetoDaoImpl();
                boolean IngParcial = ingresarParcial.IngresoParcial(IdReg, ordenCompraP, remisionP, (String) request.getSession().getAttribute("nombre"),UbicaN);
                json.put("msj", IngParcial);

                sesion.setAttribute("vOrden", "");
                sesion.setAttribute("vRemi", "");
                out.print(json);
                out.close();
                break;
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

    
    private Volumetria buildVolumetria(HttpServletRequest request) {
        if(request.getParameter("idVolumetria")== null || request.getParameter("idVolumetria").isEmpty()){
            return null;
        }
        Volumetria v = new Volumetria();
        String altoCaja = request.getParameter("altoCaja");
        v.setAltoCaja(Double.parseDouble(request.getParameter("altoCaja")));
        v.setAltoConcentrada(Double.parseDouble(request.getParameter("altoConcentrada")));
        v.setAltoPieza(Double.parseDouble(request.getParameter("altoPieza")));
        v.setAltoTarima(Double.parseDouble(request.getParameter("altoTarima")));
        v.setAnchoCaja(Double.parseDouble(request.getParameter("anchoCaja")));
        v.setAnchoConcentrada(Double.parseDouble(request.getParameter("anchoConcentrada")));
        v.setAnchoPieza(Double.parseDouble(request.getParameter("anchoPieza")));
        v.setAnchoTarima(Double.parseDouble(request.getParameter("anchoTarima")));
        v.setId(Integer.parseInt(request.getParameter("idVolumetria")));
        v.setLargoCaja(Double.parseDouble(request.getParameter("largoCaja")));
        v.setLargoConcentrada(Double.parseDouble(request.getParameter("largoConcentrada")));
        v.setLargoPieza(Double.parseDouble(request.getParameter("largoPieza")));
        v.setLargoTarima(Double.parseDouble(request.getParameter("largoTarima")));
        v.setPesoCaja(Double.parseDouble(request.getParameter("pesoCaja")));
        v.setPesoConcentrada(Double.parseDouble(request.getParameter("pesoConcentrada")));
        v.setPesoPieza(Double.parseDouble(request.getParameter("pesoPieza")));
        v.setPesoTarima(Double.parseDouble(request.getParameter("pesoTarima")));
        v.setUnidadPesoCaja(request.getParameter("unidadPesoCaja"));
        v.setUnidadPesoConcentrada(request.getParameter("unidadPesoConcentrada"));
        v.setUnidadPesoPieza(request.getParameter("unidadPesoPieza"));
        v.setUnidadPesoTarima(request.getParameter("unidadPesoTarima"));
        v.setUnidadVolCaja(request.getParameter("unidadVolCaja"));
        v.setUnidadVolConcentrada(request.getParameter("unidadVolConcentrada"));
        v.setUnidadVolPieza(request.getParameter("unidadVolPieza"));
        v.setUnidadVolTarima(request.getParameter("unidadVolTarima"));
        
        return v;
    }
}
