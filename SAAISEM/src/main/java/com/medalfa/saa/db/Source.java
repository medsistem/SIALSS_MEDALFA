/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.db;

import mx.medalfa.sialss.config.BaseDatos;

/**
 *
 * @author IngMa
 */
public enum Source 
{
    SAA_RECEPTION(BaseDatos.RECEPCION),
    SAA_AUDIT(BaseDatos.AUDITORIA),
    SAA_WAREHOUSE(BaseDatos.ALMACEN),
    SAA_BILLING(BaseDatos.FACTURACION);
    public final String text;
    /** Pool real: JNDI del context.xml o el .env (ver {@link BaseDatos}). */
    public final BaseDatos baseDatos;

    private Source(final BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
        this.text = "java:comp/env/" + baseDatos.jndi();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.name(), this.text);
    }
    
}
