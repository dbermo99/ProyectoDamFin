package com.example.proyectofinal;

public class Comentario {

    private int id;
    private String usuario;
    private String comentario;

    public Comentario(int id, String usuario, String comentario) {
        this.id = id;
        this.usuario = usuario;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
