package com.example.proyectofinal;

public class Notificacion {

    private int id;
    private int id_usuario1;
    private String usuarioEmisor;
    private String fotoUsuarioEmisor;
    private int id_usuario2;

    public Notificacion(int id, int id_usuario1, String usuarioEmisor, String fotoUsuarioEmisor, int id_usuario2) {
        this.id = id;
        this.id_usuario1 = id_usuario1;
        this.usuarioEmisor = usuarioEmisor;
        this.fotoUsuarioEmisor = fotoUsuarioEmisor;
        this.id_usuario2 = id_usuario2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario1() {
        return id_usuario1;
    }

    public void setId_usuario1(int id_usuario1) {
        this.id_usuario1 = id_usuario1;
    }

    public String getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public void setUsuarioEmisor(String usuarioEmisor) {
        this.usuarioEmisor = usuarioEmisor;
    }

    public String getFotoUsuarioEmisor() {
        return fotoUsuarioEmisor;
    }

    public void setFotoUsuarioEmisor(String fotoUsuarioEmisor) {
        this.fotoUsuarioEmisor = fotoUsuarioEmisor;
    }

    public int getId_usuario2() {
        return id_usuario2;
    }

    public void setId_usuario2(int id_usuario2) {
        this.id_usuario2 = id_usuario2;
    }
}
