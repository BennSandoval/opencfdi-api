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
public class Receptor {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private String nombre;

    @Persistent
    private String rfc;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Text getEstructura() {
        return estructura;
    }

    public void setEstructura(Text estructura) {
        this.estructura = estructura;
    }
}
