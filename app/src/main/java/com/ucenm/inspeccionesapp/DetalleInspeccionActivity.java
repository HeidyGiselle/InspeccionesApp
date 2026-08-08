package com.ucenm.inspeccionesapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.inspeccionesapp.api.ApiClient;
import com.ucenm.inspeccionesapp.api.ApiService;
import com.ucenm.inspeccionesapp.models.Audio;
import com.ucenm.inspeccionesapp.models.Fotografia;
import com.ucenm.inspeccionesapp.models.Inspeccion;
import com.ucenm.inspeccionesapp.models.Observacion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInspeccionActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion;
    private Spinner spinnerEstado;
    private TextView tvFecha;
    private Button btnEditar, btnEliminar, btnGuardarCambios, btnTomarFoto;
    private ProgressBar progressBar;
    private RecyclerView recyclerFotos;

    private SessionManager sessionManager;
    private ApiService apiService;
    private int inspeccionId;

    private List<Fotografia> listaFotos = new ArrayList<>();
    private FotoAdapter fotoAdapter;

    private Uri fotoUriActual;
    private File archivoFotoActual;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> permisoCamaraLauncher;

    private LinearLayout layoutAudios;
    private Button btnGrabarAudio;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean grabando = false;
    private String archivoAudioActual;
    private List<Audio> listaAudios = new ArrayList<>();
    private ActivityResultLauncher<String> permisoAudioLauncher;

    private LinearLayout layoutObservaciones;
    private EditText etNuevaObservacion;
    private Button btnAgregarObservacion;
    private List<Observacion> listaObservaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_inspeccion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Toast.makeText(DetalleInspeccionActivity.this, "Click detectado", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        etTitulo = findViewById(R.id.etDetalleTitulo);
        etDescripcion = findViewById(R.id.etDetalleDescripcion);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        progressBar = findViewById(R.id.progressBarDetalle);
        recyclerFotos = findViewById(R.id.recyclerFotos);
        layoutAudios = findViewById(R.id.layoutAudios);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        layoutObservaciones = findViewById(R.id.layoutObservaciones);
        etNuevaObservacion = findViewById(R.id.etNuevaObservacion);
        btnAgregarObservacion = findViewById(R.id.btnAgregarObservacion);

        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Abierta", "Cerrada", "Revisada"));
        spinnerEstado.setAdapter(estadoAdapter);

        recyclerFotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fotoAdapter = new FotoAdapter(listaFotos, ApiClient.BASE_URL);
        recyclerFotos.setAdapter(fotoAdapter);

        inspeccionId = getIntent().getIntExtra("id", -1);
        if (inspeccionId == -1) {
            Toast.makeText(this, "Inspección inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        registrarLaunchers();

        cargarDetalle();
        cargarFotos();
        cargarAudios();
        cargarObservaciones();

        btnEditar.setOnClickListener(v -> activarEdicion());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());
        btnTomarFoto.setOnClickListener(v -> verificarPermisoYTomarFoto());
        btnGrabarAudio.setOnClickListener(v -> toggleGrabacion());
        btnAgregarObservacion.setOnClickListener(v -> agregarObservacion());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void registrarLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), exito -> {
            if (exito) {
                subirFoto();
            } else {
                Toast.makeText(this, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show();
            }
        });

        permisoCamaraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
            if (concedido) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show();
            }
        });

        permisoAudioLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
            if (concedido) {
                iniciarGrabacion();
            } else {
                Toast.makeText(this, "Se necesita permiso de micrófono", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarPermisoYTomarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            String nombreArchivo = "foto_" + System.currentTimeMillis() + ".jpg";
            archivoFotoActual = new File(getExternalFilesDir("Pictures"), nombreArchivo);
            fotoUriActual = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", archivoFotoActual);
            cameraLauncher.launch(fotoUriActual);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir la cámara: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void subirFoto() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), archivoFotoActual);
            MultipartBody.Part body = MultipartBody.Part.createFormData("archivo", archivoFotoActual.getName(), requestFile);

            apiService.subirFotografia(token, inspeccionId, body).enqueue(new Callback<Fotografia>() {
                @Override
                public void onResponse(Call<Fotografia> call, Response<Fotografia> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(DetalleInspeccionActivity.this,
                                "Foto subida correctamente", Toast.LENGTH_SHORT).show();
                        cargarFotos();
                    } else {
                        Toast.makeText(DetalleInspeccionActivity.this,
                                "Error al subir foto (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Fotografia> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error al preparar la foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarFotos() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getFotografias(token, inspeccionId).enqueue(new Callback<List<Fotografia>>() {
            @Override
            public void onResponse(Call<List<Fotografia>> call, Response<List<Fotografia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaFotos.clear();
                    listaFotos.addAll(response.body());
                    fotoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Fotografia>> call, Throwable t) {
                // silencioso, no interrumpe la pantalla principal
            }
        });
    }

    private void cargarDetalle() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        apiService.getInspeccion(token, inspeccionId).enqueue(new Callback<Inspeccion>() {
            @Override
            public void onResponse(Call<Inspeccion> call, Response<Inspeccion> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Inspeccion i = response.body();
                    etTitulo.setText(i.getTitulo());
                    etDescripcion.setText(i.getDescripcion());
                    tvFecha.setText("Fecha: " + i.getFechaInspeccion());

                    String[] estados = {"Abierta", "Cerrada", "Revisada"};
                    for (int idx = 0; idx < estados.length; idx++) {
                        if (estados[idx].equalsIgnoreCase(i.getEstado())) {
                            spinnerEstado.setSelection(idx);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al cargar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inspeccion> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void activarEdicion() {
        etTitulo.setEnabled(true);
        etDescripcion.setEnabled(true);
        spinnerEstado.setEnabled(true);
        btnGuardarCambios.setVisibility(View.VISIBLE);
        btnEditar.setVisibility(View.GONE);
    }

    private void guardarCambios() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setTitulo(etTitulo.getText().toString().trim());
        inspeccion.setDescripcion(etDescripcion.getText().toString().trim());
        inspeccion.setEstado(spinnerEstado.getSelectedItem().toString());

        apiService.actualizarInspeccion(token, inspeccionId, inspeccion).enqueue(new Callback<Inspeccion>() {
            @Override
            public void onResponse(Call<Inspeccion> call, Response<Inspeccion> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Cambios guardados", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al guardar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inspeccion> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar inspección")
                .setMessage("¿Estás seguro de que quieres eliminar esta inspección? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarInspeccion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarInspeccion() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        apiService.eliminarInspeccion(token, inspeccionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Inspección eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al eliminar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleGrabacion() {
        if (!grabando) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                iniciarGrabacion();
            } else {
                permisoAudioLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        } else {
            detenerGrabacionYSubir();
        }
    }

    private void iniciarGrabacion() {
        try {
            String nombreArchivo = "audio_" + System.currentTimeMillis() + ".m4a";
            archivoAudioActual = new File(getExternalFilesDir("Audios"), nombreArchivo).getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.setOutputFile(archivoAudioActual);
            mediaRecorder.prepare();
            mediaRecorder.start();

            grabando = true;
            btnGrabarAudio.setText("Detener grabación");
            Toast.makeText(this, "Grabando... habla cerca del micrófono", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al grabar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void detenerGrabacionYSubir() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            grabando = false;
            btnGrabarAudio.setText("Grabar audio");

            subirAudio();
        } catch (Exception e) {
            Toast.makeText(this, "Error al detener grabación: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void subirAudio() {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        File archivo = new File(archivoAudioActual);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mp4"), archivo);
        MultipartBody.Part body = MultipartBody.Part.createFormData("archivo", archivo.getName(), requestFile);

        apiService.subirAudio(token, inspeccionId, body).enqueue(new Callback<Audio>() {
            @Override
            public void onResponse(Call<Audio> call, Response<Audio> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleInspeccionActivity.this, "Audio subido", Toast.LENGTH_SHORT).show();
                    cargarAudios();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al subir audio (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Audio> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarAudios() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getAudios(token, inspeccionId).enqueue(new Callback<List<Audio>>() {
            @Override
            public void onResponse(Call<List<Audio>> call, Response<List<Audio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaAudios.clear();
                    listaAudios.addAll(response.body());
                    dibujarListaAudios();
                }
            }

            @Override
            public void onFailure(Call<List<Audio>> call, Throwable t) {
                // silencioso
            }
        });
    }

    private void dibujarListaAudios() {
        layoutAudios.removeAllViews();
        for (Audio audio : listaAudios) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 8, 0, 8);

            TextView tv = new TextView(this);
            tv.setText("Audio #" + audio.getId() + " - " + audio.getFechaRegistro());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            Button btnPlay = new Button(this);
            btnPlay.setText("Reproducir");
            btnPlay.setOnClickListener(v -> reproducirAudio(audio));

            Button btnDelete = new Button(this);
            btnDelete.setText("Eliminar");
            btnDelete.setOnClickListener(v -> confirmarEliminarAudio(audio));

            fila.addView(tv);
            fila.addView(btnPlay);
            fila.addView(btnDelete);
            layoutAudios.addView(fila);
        }
    }

    private void confirmarEliminarAudio(Audio audio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar audio")
                .setMessage("¿Eliminar este audio? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarAudio(audio))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarAudio(Audio audio) {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        apiService.eliminarAudio(token, audio.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleInspeccionActivity.this, "Audio eliminado", Toast.LENGTH_SHORT).show();
                    cargarAudios();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al eliminar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reproducirAudio(Audio audio) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            String url = ApiClient.BASE_URL + audio.getRutaAudio().replaceFirst("^/", "");
            android.util.Log.d("AudioDebug", "URL a reproducir: " + url);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setOnPreparedListener(mp -> {
                android.util.Log.d("AudioDebug", "Preparado, iniciando reproducción");
                mp.start();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                android.util.Log.e("AudioDebug", "Error MediaPlayer: what=" + what + " extra=" + extra);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error al reproducir audio (" + what + "/" + extra + ")", Toast.LENGTH_LONG).show();
                return true;
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                android.util.Log.d("AudioDebug", "Reproducción completada");
            });
            mediaPlayer.prepareAsync();
            Toast.makeText(this, "Reproduciendo...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void cargarObservaciones() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getObservaciones(token, inspeccionId).enqueue(new Callback<List<Observacion>>() {
            @Override
            public void onResponse(Call<List<Observacion>> call, Response<List<Observacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaObservaciones.clear();
                    listaObservaciones.addAll(response.body());
                    dibujarListaObservaciones();
                }
            }

            @Override
            public void onFailure(Call<List<Observacion>> call, Throwable t) {
                // silencioso
            }
        });
    }

    private void dibujarListaObservaciones() {
        layoutObservaciones.removeAllViews();
        for (Observacion obs : listaObservaciones) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 8, 0, 8);

            TextView tv = new TextView(this);
            tv.setText(obs.getComentario());
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            Button btnDelete = new Button(this);
            btnDelete.setText("Eliminar");
            btnDelete.setOnClickListener(v -> confirmarEliminarObservacion(obs));

            fila.addView(tv);
            fila.addView(btnDelete);
            layoutObservaciones.addView(fila);
        }
    }

    private void agregarObservacion() {
        String texto = etNuevaObservacion.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Escribe una observación", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        Observacion obs = new Observacion();
        obs.setInspeccionId(inspeccionId);
        obs.setComentario(texto);

        apiService.crearObservacion(token, obs).enqueue(new Callback<Observacion>() {
            @Override
            public void onResponse(Call<Observacion> call, Response<Observacion> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    etNuevaObservacion.setText("");
                    Toast.makeText(DetalleInspeccionActivity.this, "Observación agregada", Toast.LENGTH_SHORT).show();
                    cargarObservaciones();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al agregar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Observacion> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmarEliminarObservacion(Observacion obs) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar observación")
                .setMessage("¿Eliminar esta observación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarObservacion(obs))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarObservacion(Observacion obs) {
        progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + sessionManager.getToken();

        apiService.eliminarObservacion(token, obs.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleInspeccionActivity.this, "Observación eliminada", Toast.LENGTH_SHORT).show();
                    cargarObservaciones();
                } else {
                    Toast.makeText(DetalleInspeccionActivity.this,
                            "Error al eliminar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetalleInspeccionActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}