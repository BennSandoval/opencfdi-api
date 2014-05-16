package com.reemmy.api.models.database;

import com.google.appengine.api.users.User;

import javax.jdo.annotations.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Usuario {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private String rfc;

    @Persistent
    private String razon;

    @Persistent (mappedBy = "usuario")
    @Element(dependent = "true")
    private List<GoogleCloudMessage> gcmKeys = new ArrayList<GoogleCloudMessage>();

    @Persistent
    private String estatus;

    @Persistent
    private int timbresAsignados;

    @Persistent
    private int timbresDisponibles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public List<GoogleCloudMessage> getGcmKeys() {
        if(gcmKeys==null){
            gcmKeys = new ArrayList<GoogleCloudMessage>();
        }
        return gcmKeys;
    }

    public void setGcmKeys(List<GoogleCloudMessage> gcmKeys) {
        this.gcmKeys = gcmKeys;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getTimbresAsignados() {
        return timbresAsignados;
    }

    public void setTimbresAsignados(int timbresAsignados) {
        this.timbresAsignados = timbresAsignados;
    }

    public int getTimbresDisponibles() {
        return timbresDisponibles;
    }

    public void setTimbresDisponibles(int timbresDisponibles) {
        this.timbresDisponibles = timbresDisponibles;
    }
}
