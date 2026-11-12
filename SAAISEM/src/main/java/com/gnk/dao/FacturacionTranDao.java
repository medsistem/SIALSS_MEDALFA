/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.dao;

import org.json.simple.JSONArray;

/**
 * Interface facturación transaccional
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public interface FacturacionTranDao {

    public JSONArray getRegistro(String ClaPro);

    public JSONArray getRegistroFact(String ClaUni, int Catalogo);

    public JSONArray getRegistroFactAuto(String ClaUni, String Catalogo);

    public JSONArray getRegistroFactAutoNivel(String ClaUni, String Catalogo);

    public JSONArray getRegistroFactAutoLote(String ClaUni, String Catalogo);

//    public JSONArray RegistroFactAutoCause(String ClaUni, String Catalogo, int Proyecto);

    public boolean RegistraDatosFactTemp(String Folio, String ClaUni, String IdLote, int CantMov, String FechaE, String Usuario);

    public boolean ConfirmarFactTemp(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC);

    public boolean ConfirmarFactTempL(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC, String ClaCliSelect);

//    public boolean ConfirmarFactTempSemiCause(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC);

//    public boolean ConfirmarFactTempCause(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC, String Cause);

    public boolean ConfirmarFactTempFOLIO(String Usuario, int Proyecto);

   // public boolean ConfirmarTranferenciaProyecto(String Usuario, String Observaciones, int Proyecto, int ProyectoFinal);

    public boolean RegistrarFolios(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

    public boolean RegistrarFoliosN1(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

    public boolean RegistrarFoliosApartarMich(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

//    public boolean RegistrarFoliosApartarAnestesia(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

   // public boolean RegistrarFoliosApartar5Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

    public boolean RegistrarFoliosMich(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

//    public boolean RegistrarFoliosAnestesia(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

   // public boolean Registro5Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

    public boolean RegistrarFoliosLote(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto);

//    public boolean RegistrarFoliosCause(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);

    //AUTOMATICA
    public boolean ActualizaREQ(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs, int CantidadReq);

//    public boolean ActualizaREQCause(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs);

    public boolean ActualizaREQLote(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs);

//    public boolean ConfirmarSugerencia(String ObsGral, String Solicitante);

//    public boolean ConfirmarSugerenciaCompra(String ObsGral, String Solicitante);

    public boolean RegistrarFoliosApartar2Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC, String agrupacion);

    public boolean Registro2Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC);
    
}
