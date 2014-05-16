package com.reemmy.api.models;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class PeticionTimbrado {

    private Comprobante comprobante;

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }
}
