package com.ucenm.inspeccionesapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "InspeccionesAppPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_ROL = "rol";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void guardarSesion(String token, String nombre, String rol) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_NOMBRE, nombre);
        editor.putString(KEY_ROL, rol);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, null);
    }

    public String getRol() {
        return prefs.getString(KEY_ROL, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}