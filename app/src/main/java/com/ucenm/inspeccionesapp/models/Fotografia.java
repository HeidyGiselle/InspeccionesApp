package com.ucenm.inspeccionesapp.models;

public class Fotografia {
    private int id;
    private int inspeccionId;
    private String rutaImagen;
    private String fechaRegistro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInspeccionId() { return inspeccionId; }
    public void setInspeccionId(int inspeccionId) { this.inspeccionId = inspeccionId; }

    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}