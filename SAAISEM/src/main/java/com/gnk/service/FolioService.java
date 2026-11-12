/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.service;

import static com.gnk.impl.FacturacionTranDaoImpl.BUSCA_EXILOTE;
import static com.gnk.impl.FacturacionTranDaoImpl.INSERTA_OBSFACTURA;
import com.gnk.model.DetalleFactura;
import conn.ConectionDBTrans;
import in.co.sneh.model.Apartado;
import in.co.sneh.model.FolioStatus;
import in.co.sneh.persistance.ApartadoDAOImpl;
import in.co.sneh.persistance.FolioStatusDAOImpl;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author HP-MEDALFA
 */
public class FolioService {
    
    private PreparedStatement psBuscaExiFol;
    private PreparedStatement psActualizaLote;
    private PreparedStatement psInsertarMov;
    private PreparedStatement psInsertarFact;
    private PreparedStatement psInsertarObs;
    
    private ResultSet rsBuscaExiFol;
    
    private ConectionDBTrans con;
    
    public FolioService(ConectionDBTrans con){
        this.con = con;
    }
    
    private void crearFolio(List<DetalleFactura> detalles, String ubicaDesc, String catalogo, String usuario, int folioFactura, String unidad2, String tipos, String observaciones) throws Exception {
        int proyecto = 0;
        ApartadoDAOImpl apartadoDAO = new ApartadoDAOImpl(con.getConn());
        FolioStatusDAOImpl stDao = new FolioStatusDAOImpl(this.con.getConn());
        for (DetalleFactura detalle : detalles) {
            String clave = detalle.getClave();
            int folioLote = detalle.getFolioLote();
            int piezas = detalle.getPiezas();
            String ubicaLote = detalle.getUbicaLote();
            int proyectoSelect = detalle.getProyectoSelect();
            proyecto = proyectoSelect;
            double costo = detalle.getCosto();
            double monto = detalle.getMonto();
            double iva = detalle.getIva();
            String fecEnt = detalle.getFecEnt();
            String contratoSelect = detalle.getContratoSelect();
            String oc = detalle.getOc();
            int solicitado = detalle.getSolicitado();
            int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
            System.out.println("Folio= " + folioFactura + " Clave: " + clave);
            psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
            psBuscaExiFol.setString(1, clave);
            psBuscaExiFol.setInt(2, folioLote);
            psBuscaExiFol.setString(3, ubicaLote);
            psBuscaExiFol.setInt(4, proyectoSelect);

            System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
            rsBuscaExiFol = psBuscaExiFol.executeQuery();
            while (rsBuscaExiFol.next()) {
                F_IdLote = rsBuscaExiFol.getInt(1);
                F_ExiLot = rsBuscaExiFol.getInt(2);
                ClaProve = rsBuscaExiFol.getInt(3);

                if ((F_ExiLot >= piezas) && (piezas > 0)) {
                    diferencia = F_ExiLot - piezas;
                    CanSur = piezas;

                    psActualizaLote.setInt(1, diferencia);
                    psActualizaLote.setInt(2, F_IdLote);
                    System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + clave);
                    psActualizaLote.addBatch();
                    // INSERTAR EN APARTADO
                    Apartado a = new Apartado();
                    a.setId(0);
                    a.setIdLote(F_IdLote);
                    a.setCant(CanSur);
                    a.setStatus(2);
                    a.setProyecto(proyectoSelect);
                    a.setClaDoc(folioFactura + "");
                    apartadoDAO.guardar(a);

                    psInsertarMov.setInt(1, folioFactura);
                    psInsertarMov.setInt(2, 51);
                    psInsertarMov.setString(3, clave);
                    psInsertarMov.setInt(4, CanSur);
                    psInsertarMov.setDouble(5, costo);
                    psInsertarMov.setDouble(6, monto);
                    psInsertarMov.setString(7, "-1");
                    psInsertarMov.setInt(8, folioLote);
                    psInsertarMov.setString(9, ubicaLote);
                    psInsertarMov.setInt(10, ClaProve);
                    psInsertarMov.setString(11, usuario);
                    System.out.println("Mov1" + psInsertarMov);
                    psInsertarMov.addBatch();
                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact1" + psInsertarFact);
                    psInsertarFact.addBatch();

                    piezas = 0;
                    solicitado = 0;
                    break;

                } else if ((piezas > 0) && (F_ExiLot > 0)) {
                    diferencia = piezas - F_ExiLot;
                    CanSur = F_ExiLot;

                    psActualizaLote.setInt(1, 0);
                    psActualizaLote.setInt(2, F_IdLote);
                    System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + clave);
                    psActualizaLote.addBatch();
                    psInsertarMov.setInt(1, folioFactura);
                    psInsertarMov.setInt(2, 51);
                    psInsertarMov.setString(3, clave);
                    psInsertarMov.setInt(4, CanSur);
                    psInsertarMov.setDouble(5, costo);
                    psInsertarMov.setDouble(6, monto);
                    psInsertarMov.setString(7, "-1");
                    psInsertarMov.setInt(8, folioLote);
                    psInsertarMov.setString(9, ubicaLote);
                    psInsertarMov.setInt(10, ClaProve);
                    psInsertarMov.setString(11, usuario);
                    System.out.println("Mov2" + psInsertarMov);
                    psInsertarMov.addBatch();
                    Apartado a = new Apartado();
                    a.setId(0);
                    a.setIdLote(F_IdLote);
                    a.setCant(CanSur);
                    a.setStatus(2);
                    a.setClaDoc(folioFactura + "");
                    apartadoDAO.guardar(a);

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();

                    solicitado = solicitado - CanSur;

                    piezas = piezas - CanSur;
                    F_ExiLot = 0;

                } else if ((piezas == 0) && (F_ExiLot == 0)) {

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, 0);
                    psInsertarFact.setDouble(6, 0.00);
                    psInsertarFact.setDouble(7, 0.00);
                    psInsertarFact.setDouble(8, 0.00);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();
                }

            }

        }

        psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
        psInsertarObs.setInt(1, folioFactura);
        psInsertarObs.setString(2, observaciones);
        psInsertarObs.setString(3, tipos);
        psInsertarObs.setInt(4, proyecto);
        psInsertarObs.execute();
        psInsertarObs.clearParameters();
        psInsertarObs.close();

        FolioStatus st = new FolioStatus();
        st.setId(0);
        st.setClaDoc(folioFactura);
        st.setProyecto(proyecto);
        st.setStatus(2);

        stDao.guardar(st);
    }
    
    private void crearPrefolio(List<DetalleFactura> detalles, String ubicaDesc, String catalogo, String usuario, int folioFactura, String unidad2, String tipos, String observaciones) throws Exception {
        int proyecto = 0;
        ApartadoDAOImpl apartadoDAO = new ApartadoDAOImpl(con.getConn());
        FolioStatusDAOImpl stDao = new FolioStatusDAOImpl(this.con.getConn());
        for (DetalleFactura detalle : detalles) {
            String clave = detalle.getClave();
            int folioLote = detalle.getFolioLote();
            int piezas = detalle.getPiezas();
            String ubicaLote = detalle.getUbicaLote();
            int proyectoSelect = detalle.getProyectoSelect();
            proyecto = proyectoSelect;
            double costo = detalle.getCosto();
            double monto = detalle.getMonto();
            double iva = detalle.getIva();
            String fecEnt = detalle.getFecEnt();
            String contratoSelect = detalle.getContratoSelect();
            String oc = detalle.getOc();
            int solicitado = detalle.getSolicitado();
            int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
            System.out.println("Folio= " + folioFactura + " Clave: " + clave);
            psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
            psBuscaExiFol.setString(1, clave);
            psBuscaExiFol.setInt(2, folioLote);
            psBuscaExiFol.setString(3, ubicaLote);
            psBuscaExiFol.setInt(4, proyectoSelect);

            System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
            rsBuscaExiFol = psBuscaExiFol.executeQuery();
            while (rsBuscaExiFol.next()) {
                F_IdLote = rsBuscaExiFol.getInt(1);
                F_ExiLot = rsBuscaExiFol.getInt(2);
                ClaProve = rsBuscaExiFol.getInt(3);

                if ((F_ExiLot >= piezas) && (piezas > 0)) {
                    diferencia = F_ExiLot - piezas;
                    CanSur = piezas;

                    Apartado a = new Apartado();
                    a.setId(0);
                    a.setIdLote(F_IdLote);
                    a.setCant(CanSur);
                    a.setStatus(1);
                    a.setProyecto(proyectoSelect);
                    a.setClaDoc(folioFactura + "");
                    apartadoDAO.guardar(a);

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact1" + psInsertarFact);
                    psInsertarFact.addBatch();

                    piezas = 0;
                    solicitado = 0;
                    break;

                } else if ((piezas > 0) && (F_ExiLot > 0)) {
                    diferencia = piezas - F_ExiLot;
                    CanSur = F_ExiLot;

                    Apartado a = new Apartado();
                    a.setId(0);
                    a.setIdLote(F_IdLote);
                    a.setCant(CanSur);
                    a.setStatus(1);
                    a.setClaDoc(folioFactura + "");
                    apartadoDAO.guardar(a);

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();

                    solicitado = solicitado - CanSur;

                    piezas = piezas - CanSur;
                    F_ExiLot = 0;

                } else if ((piezas == 0) && (F_ExiLot == 0)) {

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, 0);
                    psInsertarFact.setDouble(6, 0.00);
                    psInsertarFact.setDouble(7, 0.00);
                    psInsertarFact.setDouble(8, 0.00);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();
                }

            }

        }

        psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
        psInsertarObs.setInt(1, folioFactura);
        psInsertarObs.setString(2, observaciones);
        psInsertarObs.setString(3, tipos);
        psInsertarObs.setInt(4, proyecto);
        psInsertarObs.execute();
        psInsertarObs.clearParameters();
        psInsertarObs.close();

        FolioStatus st = new FolioStatus();
        st.setId(0);
        st.setClaDoc(folioFactura);
        st.setProyecto(proyecto);
        st.setStatus(1);

        stDao.guardar(st);
    }
}
