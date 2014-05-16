package com.reemmy.api.models.database;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;

import javax.jdo.annotations.*;
import java.util.Date;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Factura {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private Date fecha;

    @Persistent
    private String receptor;

    @Persistent
    private Text qr;

    @Persistent
    private Blob byteQr;

    @Persistent
    private String UUID;

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public Text getQr() {
        return qr;
    }

    public void setQr(Text qr) {
        this.qr = qr;
    }

    public Blob getByteQr() {
        return byteQr;
    }

    public void setByteQr(Blob byteQr) {
        this.byteQr = byteQr;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Text getEstructura() {
        return estructura;
    }

    public void setEstructura(Text estructura) {
        this.estructura = estructura;
    }
}