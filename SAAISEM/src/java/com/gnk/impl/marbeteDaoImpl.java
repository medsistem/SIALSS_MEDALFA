/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.impl;

import com.gnk.dao.marbetesDao;
import conn.ConectionDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juan
 */
public class marbeteDaoImpl implements marbetesDao {

    ConectionDB con = new ConectionDB();
    private ResultSet rs, rs2;
    private PreparedStatement ps, ps2;
    private String query, query2;

    @Override
    public String nombreUnidad(int folio, int Proyecto) {
        String nombreUnidad = "", nombreUnidad1 = "";
        int Contar = 0, Contar1 = 0;
        try {
            con.conectar();
            
            query = "SELECT COUNT(R.F_ClaPro) FROM tb_redfria R INNER JOIN tb_factura F ON R.F_ClaPro = F.F_ClaPro WHERE F.F_ClaDoc = ? AND F.F_Proyecto = ? AND F.F_CantSur>0;";
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, Proyecto);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contar = rs.getInt(1);
            }
            ps.clearParameters();
            
            query = "SELECT COUNT(C.F_ClaPro) FROM tb_controlados C INNER JOIN tb_factura F ON C.F_ClaPro = F.F_ClaPro WHERE F.F_ClaDoc = ? AND F.F_Proyecto = ? AND F.F_CantSur>0;";
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, Proyecto);
            rs = ps.executeQuery();
            while (rs.next()) {
                    Contar1 = rs.getInt(1);
            }
            ps.clearParameters();
            query = "SELECT u.F_NomCli FROM tb_factura f INNER JOIN tb_uniatn u ON f.F_ClaCli = u.F_ClaCli WHERE f.F_ClaDoc = ? AND f.F_Proyecto = ? GROUP BY f.F_ClaDoc;";
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, Proyecto);
            rs = ps.executeQuery();
            while (rs.next()) {
                if(Contar > 0){
                    nombreUnidad = Contar + "/" + rs.getString("F_NomCli") + "/" + "RF";  
                } else if(Contar1 > 0){
                    nombreUnidad = Contar1 + "/" + rs.getString("F_NomCli") + "/" +"Cont";    
                } else{
                    nombreUnidad =  "0/" + rs.getString("F_NomCli") + "";    
                }
            }
            
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(marbeteDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(marbeteDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return nombreUnidad;
    }

    @Override
    public boolean insertar(String unidad, int folio, int marbetes, int Proyecto, String rutaParam, String RutaN) {
        boolean guardado = false;
        try {
            con.conectar();
            String Juris = "", Muni = "", TipoFactura = "", Rut = "", ruta = "", origen = "", clacli = "", rutaN1 = "" ;
            query = "DELETE FROM tb_marbetes_cajas WHERE F_Folio=?;";
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, folio);
            ps.execute();
            ps.clearParameters();
            
            
            String foliom = String.valueOf(folio);
            String Proy = String.valueOf(Proyecto);
            query2 = "select f.F_FecEnt as F_FecEnt  from tb_factura f  where f.F_ClaDoc = ? and f.F_Proyecto = ? group by  f.F_FecEnt";
            ps2 = con.getConn().prepareStatement(query2);
            ps2.setInt(1, folio);
            ps2.setInt(2, Proyecto);
            rs2 = ps2.executeQuery();
            while(rs2.next()){
                 Rut = rs2.getString(1);   
            }
            
            query = "insert into tb_monitor  VALUES (0,?,?,DATE(NOW()),TIME(NOW()),?,?,?)";
            ps = con.getConn().prepareStatement(query);
//            System.out.println(Rut);
            ps.setString(1, foliom);
            ps.setString(2, foliom);
            ps.setString(3, "2");
            ps.setString(4, Proy);
            ps.setString(5, Rut);
            ps.execute();
           ps.clearParameters();
           
            query = "SELECT F.F_ClaCli, U.F_NomCli, IFNULL(J.F_DesJurIS, '-') AS F_DesJurIS, IFNULL(M.F_DesMunIS, '-') AS F_DesMunIS, O.F_Tipo, F.F_Proyecto, O.F_Proyecto, extract(day from f.F_FecEnt) as F_FecEnt, ORI.F_DesOri FROM tb_factura F INNER JOIN tb_lote L ON L.F_FolLot = F.F_Lote AND L.F_Ubica = F.F_Ubicacion INNER JOIN tb_origen ORI ON L.F_Origen = ORI.F_ClaOri LEFT JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli LEFT JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS LEFT JOIN tb_muniis M ON U.F_ClaMun = M.F_ClaMunIS AND U.F_ClaJur = M.F_JurMunIS LEFT JOIN tb_obserfact O ON F.F_ClaDoc = O.F_IdFact AND F.F_Proyecto = O.F_Proyecto WHERE F_ClaDoc = ? AND F.F_Proyecto = ? GROUP BY F.F_ClaCli;";
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, folio);
            ps.setInt(2, Proyecto);
            rs = ps.executeQuery();
            if (rs.next()) {
                Juris = rs.getString(3);
                Muni = rs.getString(4);
                TipoFactura = rs.getString(5);
                ruta = rs.getString(8);
                origen = rs.getString(9);
                clacli = rs.getString(1);
            }

            if(rutaParam == null || rutaParam.isEmpty()){
                rutaParam= ruta;
            }
            ps.clearParameters();
            System.out.println("parametro"+RutaN);
            //aqui es lo del repo
            if (RutaN.equals("1")) {
                rutaN1 = "Ruta Ordinaria";
                System.out.println("Ruta Ordinaria" + rutaN1);
            } else if (RutaN.equals("2")) {

                query = "SELECT uc.F_TipoCovid FROM tb_unicovid AS uc WHERE uc.F_ClaCliCovid = ?";
                ps = con.getConn().prepareStatement(query);
                ps.setString(1, clacli);
                rs = ps.executeQuery();
                if (rs.next()) {
                   
                    if (rs != null) {
                        rutaN1 = rs.getString(1); 
                    }else{
                        rutaN1 = "Ruta Ordinaria";
                    }
                }
            } else {
                rutaN1 = "Urgente";
            }
            ps.clearParameters();
            query = "INSERT INTO tb_marbetes_cajas VALUES (?,?,?,?,DATE(NOW()),?,?,?,0,?,?,?)";
            for (int cajas = 1; cajas <= marbetes; cajas++) {

                ps = con.getConn().prepareStatement(query);
                ps.setString(1, unidad);
                ps.setInt(2, folio);
                ps.setInt(3, marbetes);
                ps.setInt(4, cajas);
                ps.setString(5, Juris);
                ps.setString(6, Muni);
                ps.setString(7, TipoFactura);
                ps.setString(8, rutaParam);
                ps.setString(9, origen);
                ps.setString(10, rutaN1);
                
                ps.execute();
            }

            guardado = true;
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(marbeteDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(marbeteDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return guardado;
    }
}
