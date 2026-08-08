package com.ucenm.inspeccionesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.inspeccionesapp.api.ApiClient;
import com.ucenm.inspeccionesapp.api.ApiService;
import com.ucenm.inspeccionesapp.models.Inspeccion;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaInspeccionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_inspecciones);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarInspecciones();

        findViewById(R.id.fabAgregar).setOnClickListener(v -> {
            startActivity(new Intent(this, CrearInspeccionActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarInspecciones();
    }

    private void cargarInspecciones() {
        progressBar.setVisibility(View.VISIBLE);

        String token = "Bearer " + sessionManager.getToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getInspecciones(token).enqueue(new Callback<List<Inspeccion>>() {
            @Override
            public void onResponse(Call<List<Inspeccion>> call, Response<List<Inspeccion>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Inspeccion> lista = response.body();

                    InspeccionAdapter adapter = new InspeccionAdapter(lista, inspeccion -> {
                        Intent intent = new Intent(ListaInspeccionesActivity.this, DetalleInspeccionActivity.class);
                        intent.putExtra("id", inspeccion.getId());
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(adapter);

                    if (lista.isEmpty()) {
                        Toast.makeText(ListaInspeccionesActivity.this,
                                "No tienes inspecciones registradas todavía",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaInspeccionesActivity.this,
                            "Error al cargar inspecciones (código " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Inspeccion>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListaInspeccionesActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}