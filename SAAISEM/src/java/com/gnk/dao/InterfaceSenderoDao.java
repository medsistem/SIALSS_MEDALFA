/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.dao;

import com.medalfa.saa.model.Volumetria;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Interface ingreso de compras transaccional
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public interface InterfaceSenderoDao {

///editar
    public JSONObject datosAditar(int id);
    public boolean ActualizarDatos(String usuario, String lote, String Caducidad, int cantidad, String cb, String marca, int id, int tarimas, int cajas, int pzacaja, int cajasi, int resto, int tarimasI, String Costo, int factorEmpaque, String ordenSuministro, Volumetria volumetria, String cartaCanje,String marcaComercial, String unidadFonsabi);
//libera oc

    public boolean Actualizarlerma(String ordenCompra, String remision, String Usuario, String UbicaN, String unidadFon);

//    public boolean ActualizarlermaCross(String ordenCompra, String remision, String usuario);
    public boolean insertSendero(String ordenCompra, String remision);

    public boolean agregarSendero(String ordenCompra, String remision, String usuario);

   

    public JSONArray getRegistro(String vOrden, String vRemi);

/*INGRESO PARCIAL*/
    public boolean IngresoParcial(String IdReg, String ordenCompra, String remision, String usuario, String UbicaN);

 

    
}
