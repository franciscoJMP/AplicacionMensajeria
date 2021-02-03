package com.example.aplicaciondemensajeria.Modelos;

import java.io.Serializable;
import java.util.Date;

public class Mensajes implements Serializable {
    private String emisor,receptor,mensaje;
    private long mensajeTime;

    public Mensajes(String emisor,String receptor,String mensaje){
        this.emisor=emisor;
        this.receptor=receptor;
        this.mensaje=mensaje;
        mensajeTime=new Date().getTime();
    }

    public Mensajes() {
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getMensajeTime() {
        return mensajeTime;
    }

    public void setMensajeTime(long mensajeTime) {
        this.mensajeTime = mensajeTime;
    }
}
