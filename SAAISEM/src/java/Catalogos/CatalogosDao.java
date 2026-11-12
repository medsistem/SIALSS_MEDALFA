/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Catalogos;

import org.json.simple.JSONArray;

/**
 *
 * @author dotor
 */
public interface CatalogosDao {

    public String GenerarMarbete(String Clave, String Lote, int Proyecto);

    public JSONArray ObtenerProyectos();
    
    public JSONArray ObtenerLotes();
    
    public JSONArray ObtenerProductos();
    
}
