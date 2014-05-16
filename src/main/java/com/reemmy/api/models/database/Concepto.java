package com.reemmy.api.models.database;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

import javax.jdo.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * To change this template use File | Settings | File Templates.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Concepto {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private String unidad;

    @Persistent
    private String noIdentificacion;

    @Persistent
    private String descripcion;

    @Persistent
    private Text estructura;

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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getNoIdentificacion() {
        return noIdentificacion;
    }

    public void setNoIdentificacion(String noIdentificacion) {
        this.noIdentificacion = noIdentificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Text getEstructura() {
        return estructura;
    }

    public void setEstructura(Text estructura) {
        this.estructura = estructura;
    }
}
