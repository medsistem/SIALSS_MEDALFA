/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.persistance;

import in.co.sneh.model.Semaforo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP-MEDALFA
 */
public class SemaforoDAOImpl {
    
    private Connection con;
    
    public SemaforoDAOImpl(Connection con){
        this.con = con;
    }
    
    public void guardar(Semaforo s){
        try {
            String query = "INSERT INTO tb_clave_semaforo VALUES(0,?,?);";
            PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, s.getClaPro());
            ps.setInt(2, s.getColor());
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public Semaforo getByClaPro(String claPro){
        try {
            String query = "SELECT * FROM tb_clave_semaforo where F_ClaPro = ?;";
            PreparedStatement ps = this.con.prepareStatement(query);
            ps.setString(1, claPro);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            return this.build(rs);
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void updateColor(Integer id, Integer color){
        try {
            String query = "UPDATE tb_clave_semaforo SET F_Color= ? where F_Id= ?;";
            PreparedStatement ps = this.con.prepareStatement(query);
            ps.setInt(1,color);
            ps.setInt(2, id);
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Semaforo> getAll(){
        List<Semaforo> list = new ArrayList<Semaforo>();
        try {
            String query = "SELECT * FROM tb_clave_semaforo";
            PreparedStatement ps = this.con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(this.build(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    private Semaforo build(ResultSet rs){
        Semaforo s = new Semaforo();
         try {
            s.setClaPro(rs.getString("F_ClaPro"));
            s.setColor(rs.getInt("F_Color"));
            s.setId(rs.getInt("F_Id"));
        } catch (SQLException ex) {
            Logger.getLogger(ApartadoDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return s;
    }
}
