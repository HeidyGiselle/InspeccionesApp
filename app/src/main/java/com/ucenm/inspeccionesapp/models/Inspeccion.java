package com.ucenm.inspeccionesapp.models;

public class Inspeccion {
    private int id;
    private int usuarioId;
    private String titulo;
    private String descripcion;
    private String fechaInspeccion;
    private Double latitud;
    private Double longitud;
    private String estado;

    // Constructor vacío (para crear nuevas inspecciones)
    public Inspeccion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaInspeccion() { return fechaInspeccion; }
    public void setFechaInspeccion(String fechaInspeccion) { this.fechaInspeccion = fechaInspeccion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}