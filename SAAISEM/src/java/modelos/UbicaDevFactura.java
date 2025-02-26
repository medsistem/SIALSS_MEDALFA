/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author SISTEMAS
 */
public class UbicaDevFactura {
    int id;
    String f_claUbi, estatus;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getF_claUbi() {
        return f_claUbi;
    }

    public void setF_claUbi(String f_claUbi) {
        this.f_claUbi = f_claUbi;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "UbicaDevFactura{" + "id=" + id + ", f_claUbi=" + f_claUbi + ", estatus=" + estatus + '}';
    }
    
    
    
}
