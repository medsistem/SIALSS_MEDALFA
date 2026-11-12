/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Catalogos;

import conn.ConectionDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author dotor
 */
public class CatalogosImpl implements CatalogosDao {

    /*CONSULTAS*/
    public static String BuscaProyecto = "SELECT * FROM tb_proyectos ORDER BY F_DesProy;";
    
    public static String BuscaLotes = "SELECT F_ClaPro, F_ClaLot FROM tb_lote GROUP BY F_ClaLot ORDER BY F_ClaLot;;";
    
    public static String BuscaProductos = "SELECT F_ClaPro FROM tb_medica ORDER BY F_ClaPro+0;";

    public static String BuscaProyectoConsulta = "SELECT * FROM tb_proyectos WHERE F_Id IN (%s);";

    /*VARIABLES*/
    ConectionDB con = new ConectionDB();
    private ResultSet rs, rs2, rs3;
    private PreparedStatement ps, ps2, ps3;
    private String query, query2;
    private PreparedStatement psBuscaProyecto;
    private PreparedStatement psBuscaLote;
    private PreparedStatement psBuscaProducto;
    private PreparedStatement psConsulta;

    @Override
    public String GenerarMarbete(String Clave, String Lote, int Proyecto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public JSONArray ObtenerProyectos() {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(BuscaProyecto);
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerLotes() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(BuscaLotes);
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerProductos() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(BuscaProductos);
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(CatalogosImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

}
