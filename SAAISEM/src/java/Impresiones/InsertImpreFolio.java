/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Impresiones;

import conn.ConectionDB;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marco
 */
public class InsertImpreFolio {
    
    private static InsertImpreFolio obj = null;
    private boolean verQuery = false;
    
    public InsertImpreFolio(boolean verQuery ) {
        this.verQuery = verQuery;
    }
    
    public static InsertImpreFolio instance(){
        if(Objects.isNull(obj)){
            obj = new InsertImpreFolio(false);
        }
        return obj;
    }
    
    public static InsertImpreFolio instanceLog(){
        if(Objects.isNull(obj)){
            obj = new InsertImpreFolio(true);
        }
        return obj;
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,"", "","","","","","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon, "","","","","","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,"","","","","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,"","","","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,"","","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,"","","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,"","","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,"","", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,"", "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion, "", "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato, "", "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc, "", "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF, "", "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris, "", "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni, "", "", "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni, String redF){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni,  redF, "", "", "", "","0");
    }
    /*
    Para estos metodos si es necesario ingresar el icono de APE
    -------------------------------------------------------------------
    */
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni, String redF, String apeF){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni,  redF,  apeF, "", "", "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni, String redF, String apeF, String  encabezado){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni,  redF,  apeF,   encabezado, "", "","0");
    }
   
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni, String redF, String apeF, String  encabezado, String clues){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni,  redF,  apeF,   encabezado,  clues, "","0");
    }
    
    public void insert( ConectionDB conexion, String clacli,String nomCli,String direc,String claDoc,String fecEnt,String claPro,String descPro,String clalot,String fecCad,String cantReq,String cantSur,String costo,String monto,String obs,String elabo,String razon, String user,String proyecto,String piezas,String subtotal,String montoF,String iva,String letra,String presentacion, String contrato, String oc, String proyectoF, String juris, String muni, String redF, String apeF, String  encabezado, String clues, String folio){
         this.insert(conexion, clacli, nomCli, direc, claDoc, fecEnt, claPro, descPro, clalot, fecCad, cantReq, cantSur, costo, monto, obs, elabo,razon,  user,proyecto,piezas,subtotal,montoF,iva,letra,presentacion,  contrato,  oc,  proyectoF,  juris,  muni,  redF,  apeF,   encabezado,  clues,  folio,"0");
    }
    
    
    public void insert( ConectionDB conexion, 
            String clacli ,
            String nomCli,
            String direc,
            String claDoc,
            String fecEnt,
            String claPro,
            String descPro,
            String clalot,
            String fecCad,
            String cantReq,
            String cantSur,
            String costo,
            String monto,
            String obs,
            String elabo,
            String razon,
            String user,
            String proyecto,
            String piezas,
            String subtotal,
            String montoF,
            String iva,
            String letra,
            String presentacion,
            String contrato,
            String oc,
            String proyectoF,
            String juris,
            String muni,
            String redF,
            String apeF,
            String encabezado,
            String clues,
            String folio,
            String id){

        try {
            String query = "INSERT INTO tb_imprefolio VALUES("
                    +"'"+ clacli  + "'," 
                    +"'"+ nomCli  + "'," 
                    +"'"+ direc + "'," 
                    +"'"+ claDoc + "'," 
                    +"'"+ fecEnt + "'," 
                    +"'"+ claPro + "',"
                    +"'"+ descPro + "'," 
                    +"'"+ clalot + "'," 
                    +"'"+ fecCad + "'," 
                    +"'"+ cantReq + "'," 
                    +"'"+ cantSur + "'," 
                    +"'"+ costo + "'," 
                    +"'"+ monto + "'," 
                    +"'"+ obs + "'," 
                    +"'"+ elabo + "'," 
                    +"'"+ razon + "'," 
                    +"'"+ user + "'," 
                    +"'"+ proyecto + "',"
                    +"'"+ piezas+"',"
                    +"'"+ subtotal+"',"
                    +"'"+ montoF+"',"
                    +"'"+ iva+"',"
                    +"'"+ letra+"',"
                    +"'"+ presentacion+"',"
                    +"'"+ contrato+"',"
                    +"'"+ oc+"',"
                    +"'"+ proyectoF +"',"
                    +"'"+ juris+"',"
                    +"'"+ muni+"',"
                    +"'"+ redF+"',"
                    +"'"+ apeF+"',"
                    +"'"+ encabezado+"',"
                    +"'"+ clues+"',"
                    +"'"+ folio+"',"
                    + id + ");";
            if(this.verQuery){
                System.out.println(query);
            }
                    conexion.actualizar(query);
        } catch (SQLException ex) {
            Logger.getLogger(InsertImpreFolio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
