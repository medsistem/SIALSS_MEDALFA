/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.persistance;

import in.co.sneh.model.Apartado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Israel Vigueras
 */
public class ApartadoDAOImpl {
    
    Connection con;
    SimpleDateFormat df;
    
    public ApartadoDAOImpl(Connection con){
        this.con=con;
        this.df = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    /**
     * Inserta en tb_apartado
     * 
     * @param a Datos a insertar
     */
    public void guardar(Apartado a){
        try {
            String query = "INSERT INTO tb_apartado VALUES(0,?,?,?,?,?);";
            PreparedStatement ps = this.con.prepareStatement(query);
            ps.setInt(1, a.getIdLote());
            ps.setInt(2, a.getCant());
            ps.setInt(3, 0);
            ps.setInt(4, 1);
            ps.setString(5,a.getClaDoc());
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Obtiene la lista de los insumos apartados para el folio
     * 
     * @param claDoc clave (folio) del documento
     * @return lista de los insumos apartados para el documento
     */
    public List<Apartado> getByClaDoc(String claDoc){
        List<Apartado> lista = new ArrayList<Apartado>();
        try{
            String query = "SELECT * from tb_apartado where F_ClaDoc = ?;";
            PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, claDoc);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                lista.add(this.buildApartado(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lista;
    }
    
    public Apartado buildApartado(ResultSet rs){
        Apartado a = new Apartado();
        try {
            a.setId(rs.getInt("F_Id"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
          a.setIdLote(rs.getInt(rs.getInt("F_IdLot")));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            a.setCant(rs.getInt("F_Cant"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            a.setProyecto(rs.getInt("F_Proyecto"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            a.setStatus(rs.getInt("F_Status"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
       try {
            a.setClaDoc(rs.getString("F_ClaDoc"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return a;
    }
}
