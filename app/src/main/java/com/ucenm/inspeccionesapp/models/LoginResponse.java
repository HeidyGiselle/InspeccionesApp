package com.ucenm.inspeccionesapp.models;

public class LoginResponse {
    private String token;
    private String nombre;
    private String rol;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}