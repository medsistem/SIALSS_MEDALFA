/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.dao;

import org.json.simple.JSONArray;

/**
 *
 * @author CEDIS TOLUCA3
 */
public interface ExistenciaProyectoDao {

    public JSONArray ObtenerProyectos();

    public JSONArray ObtenerProyectosClientes(String ProyectoCL);

    public JSONArray ObtenerProyectosConsulta(String Proyecto);

    public JSONArray MostrarRegistros(String Proyecto, String Tipo);

    public JSONArray MostrarRegistrosCompra(String Proyecto, String Tipo);
    
    public JSONArray MostrarRegistrosCompraFonsabi(String Proyecto, String Tipo);

    public JSONArray MostrarRegistrosCompraPrograma(String Proyecto, String Tipo);

    public JSONArray MostrarRegistrosCompraDis(String Proyecto, String Tipo);

    public JSONArray MostrarTodos(String Tipo);

    public JSONArray MostrarTodosClientes(String ProyectoCL);

    public JSONArray MostrarTodosCompra(String Tipo);
    
    public JSONArray MostrarAuditoria(String Tipo);

    public JSONArray MostrarTodosConsulta(String Proyecto);

    public JSONArray ObtenerTipoUnidad();

    public JSONArray MostrarRegistrosFact(String TipoUnidad, String Fecha);

    public JSONArray MostrarTodosFact(String Fecha);
}
