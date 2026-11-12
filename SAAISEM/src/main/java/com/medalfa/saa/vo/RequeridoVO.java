/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.vo;

/**
 *
 * @author HP-MEDALFA
 */
public class RequeridoVO {

    private String clave;
    private String claveTrimed;
    private String descripcion;
    private Integer solicitado;
    private Integer requerido;
    private Integer existencia;

    private Boolean proxCaducar;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getSolicitado() {
        return solicitado;
    }

    public void setSolicitado(Integer solicitado) {
        this.solicitado = solicitado;
    }

    public Integer getRequerido() {
        return requerido;
    }

    public void setRequerido(Integer requerido) {
        this.requerido = requerido;
    }

    public Integer getExistencia() {
        return existencia;
    }

    public void setExistencia(Integer existencia) {
        this.existencia = existencia;
    }

    public String getClaveTrimed() {
        return claveTrimed;
    }

    public void setClaveTrimed(String claveTrimed) {
        this.claveTrimed = claveTrimed;
    }

    public Boolean getProxCaducar() {
        if (proxCaducar == null) {
            return false;
        }
        return proxCaducar;
    }

    public void setProxCaducar(Integer proxCaducar) {
        this.proxCaducar = proxCaducar > 0;
    }

    public int compareTo(Object o) {
        RequeridoVO otra = (RequeridoVO) o;

        return this.getClave().compareTo(otra.getClave());
    }
}
