package com.ucenm.inspeccionesapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.ucenm.inspeccionesapp.api.ApiClient;
import com.ucenm.inspeccionesapp.api.ApiService;
import com.ucenm.inspeccionesapp.models.Inspeccion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearInspeccionActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion;
    private TextView tvUbicacion;
    private Button btnGuardar;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitud = 0.0;
    private double longitud = 0.0;

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_inspeccion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnGuardar = findViewById(R.id.btnGuardar);
        progressBar = findViewById(R.id.progressBar);

        verificarPermisoUbicacion();

        btnGuardar.setOnClickListener(v -> guardarInspeccion());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                tvUbicacion.setText("Ubicación: permiso denegado");
                Toast.makeText(this, "Se necesita permiso de ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                tvUbicacion.setText("Ubicación: " + latitud + ", " + longitud);
            } else {
                tvUbicacion.setText("Ubicación: no disponible");
            }
        });
    }

    private void guardarInspeccion() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setTitulo(titulo);
        inspeccion.setDescripcion(descripcion);
        inspeccion.setLatitud(latitud);
        inspeccion.setLongitud(longitud);
        inspeccion.setFechaInspeccion(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()));

        apiService.crearInspeccion(token, inspeccion).enqueue(new Callback<Inspeccion>() {
            @Override
            public void onResponse(Call<Inspeccion> call, Response<Inspeccion> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CrearInspeccionActivity.this,
                            "Inspección creada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearInspeccionActivity.this,
                            "Error al crear (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inspeccion> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CrearInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}