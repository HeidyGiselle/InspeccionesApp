package com.ucenm.inspeccionesapp.models;

public class Observacion {
    private int id;
    private int inspeccionId;
    private String comentario;
    private String fechaRegistro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInspeccionId() { return inspeccionId; }
    public void setInspeccionId(int inspeccionId) { this.inspeccionId = inspeccionId; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}