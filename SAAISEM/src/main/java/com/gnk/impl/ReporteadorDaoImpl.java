/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.impl;

import com.gnk.dao.ReporteadorDao;
import conn.ConectionDBTrans;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author MEDALFA
 */
public class ReporteadorDaoImpl implements ReporteadorDao {

    public static String BuscaJurisdicciones = "SELECT U.F_ClaJur, CONCAT( J.F_DesJurIS, ' [', J.F_ClaJurIS, ']' ) AS F_DesJurIS FROM tb_uniatn U INNER JOIN tb_juriis J ON U.F_ClaJur = J.F_ClaJurIS GROUP BY U.F_ClaJur;";

    public static String BuscaMunicipio = "SELECT U.F_ClaMun, CONCAT( M.F_DesMunIS, ' [', M.F_ClaMunIS, ']' ) AS F_DesMunIS FROM tb_uniatn U INNER JOIN tb_muniis M ON U.F_ClaMun = M.F_ClaMunIS WHERE U.F_ClaJur = ? GROUP BY U.F_ClaMun;";

    public static String BuscaUnidad = "SELECT F_ClaCli, CONCAT(F_ClaCli, ' [', F_NomCli, ']') AS F_NomCli FROM tb_uniatn WHERE F_ClaJur =? AND F_ClaMun =?;";

    public static String BuscaUnidades = "SELECT F_ClaCli, CONCAT(F_ClaCli, ' [', F_NomCli, ']') AS F_NomCli FROM tb_uniatn;";

    public static String BuscaTipoUnidad = "SELECT F_Tipo FROM tb_uniatn GROUP BY F_Tipo;";

    private final ConectionDBTrans con = new ConectionDBTrans();
    private PreparedStatement psConsulta;
    private ResultSet rs;

    @Override
    public JSONArray ObtenerJurisdicciones() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(BuscaJurisdicciones);
            rs = psConsulta.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerMunicipio(String Jurisdicciones) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(BuscaMunicipio);
            psConsulta.setString(1, Jurisdicciones);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerUnidad(String Jurisdicciones, String Municipio) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(BuscaUnidad);
            psConsulta.setString(1, Jurisdicciones);
            psConsulta.setString(2, Municipio);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerTipoUnidad() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(BuscaTipoUnidad);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(1));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerUnidades() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(BuscaUnidades);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ReporteadorDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

}
