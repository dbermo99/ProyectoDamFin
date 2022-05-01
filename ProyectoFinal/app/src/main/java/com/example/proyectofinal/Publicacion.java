package com.example.proyectofinal;

public class Publicacion {

    private int id;
    private String usuario;
    private String texto;
    private String foto;

    public Publicacion() {

    }

    public Publicacion(int id, String usuario, String texto, String foto) {
        this.id = id;
        this.usuario = usuario;
        this.texto = texto;
        this.foto = foto;
    }

    /*public Publicacion(String usuario, String texto) {
        this.usuario = usuario;
        this.texto = texto;
    }*/

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }


}
