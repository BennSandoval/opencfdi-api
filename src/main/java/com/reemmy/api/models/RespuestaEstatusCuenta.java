package com.reemmy.api.models;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
public class RespuestaEstatusCuenta {

    private String rfc;
    private String razon;
    private String codigo;
    private String email;
    private String descripcion;
    private Double timbresAsignados;
    private Double timbresDisponibles;

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getTimbresAsignados() {
        return timbresAsignados;
    }

    public void setTimbresAsignados(Double timbresAsignados) {
        this.timbresAsignados = timbresAsignados;
    }

    public Double getTimbresDisponibles() {
        return timbresDisponibles;
    }

    public void setTimbresDisponibles(Double timbresDisponibles) {
        this.timbresDisponibles = timbresDisponibles;
    }
}
