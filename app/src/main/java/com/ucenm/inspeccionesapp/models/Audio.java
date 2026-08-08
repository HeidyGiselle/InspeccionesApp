package com.ucenm.inspeccionesapp.models;

public class Audio {
    private int id;
    private int inspeccionId;
    private String rutaAudio;
    private int duracion;
    private String fechaRegistro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInspeccionId() { return inspeccionId; }
    public void setInspeccionId(int inspeccionId) { this.inspeccionId = inspeccionId; }

    public String getRutaAudio() { return rutaAudio; }
    public void setRutaAudio(String rutaAudio) { this.rutaAudio = rutaAudio; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}