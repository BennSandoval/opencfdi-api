package com.reemmy.api.models;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class PeticionUsuario {

    private String rfc;
    private String razon;
    private String gcmKey;
    private boolean certificados;

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public String getGcmKey() {
        return gcmKey;
    }

    public void setGcmKey(String gcmKey) {
        this.gcmKey = gcmKey;
    }

    public boolean isCertificados() {
        return certificados;
    }

    public void setCertificados(boolean certificados) {
        this.certificados = certificados;
    }
}
