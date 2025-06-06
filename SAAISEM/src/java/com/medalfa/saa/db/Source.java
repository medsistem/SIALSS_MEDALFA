/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.db;

/**
 *
 * @author IngMa
 */
public enum Source 
{
    SAA_RECEPTION("java:comp/env/jdbc/recepcion_sialss_mdf"),
    SAA_AUDIT("java:comp/env/jdbc/auditoria_sialss_mdf"),
    SAA_WAREHOUSE("java:comp/env/jdbc/almacen_sialss_mdf"),
    SAA_BILLING("java:comp/env/jdbc/facturacion_sialss_mdf");
    public final String text;

    private Source(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.name(), this.text);
    }
    
}
