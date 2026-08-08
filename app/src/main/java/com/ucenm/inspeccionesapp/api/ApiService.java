package com.ucenm.inspeccionesapp.api;

import com.ucenm.inspeccionesapp.models.Inspeccion;
import com.ucenm.inspeccionesapp.models.LoginRequest;
import com.ucenm.inspeccionesapp.models.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.ucenm.inspeccionesapp.models.Fotografia;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import com.ucenm.inspeccionesapp.models.Audio;
import com.ucenm.inspeccionesapp.models.Observacion;


public interface
ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/inspecciones")
    Call<List<Inspeccion>> getInspecciones(@Header("Authorization") String token);

    @GET("api/inspecciones/{id}")
    Call<Inspeccion> getInspeccion(@Header("Authorization") String token, @Path("id") int id);

    @POST("api/inspecciones")
    Call<Inspeccion> crearInspeccion(@Header("Authorization") String token, @Body Inspeccion inspeccion);

    @PUT("api/inspecciones/{id}")
    Call<Inspeccion> actualizarInspeccion(@Header("Authorization") String token, @Path("id") int id, @Body Inspeccion inspeccion);

    @DELETE("api/inspecciones/{id}")
    Call<Void> eliminarInspeccion(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/fotografias/inspeccion/{inspeccionId}")
    Call<List<Fotografia>> getFotografias(@Header("Authorization") String token, @Path("inspeccionId") int inspeccionId);

    @Multipart
    @POST("api/fotografias/inspeccion/{inspeccionId}")
    Call<Fotografia> subirFotografia(@Header("Authorization") String token, @Path("inspeccionId") int inspeccionId, @Part MultipartBody.Part archivo);

    @DELETE("api/fotografias/{id}")
    Call<Void> eliminarFotografia(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/audios/inspeccion/{inspeccionId}")
    Call<List<Audio>> getAudios(@Header("Authorization") String token, @Path("inspeccionId") int inspeccionId);

    @Multipart
    @POST("api/audios/inspeccion/{inspeccionId}")
    Call<Audio> subirAudio(@Header("Authorization") String token, @Path("inspeccionId") int inspeccionId, @Part MultipartBody.Part archivo);

    @DELETE("api/audios/{id}")
    Call<Void> eliminarAudio(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/observaciones/inspeccion/{inspeccionId}")
    Call<List<Observacion>> getObservaciones(@Header("Authorization") String token, @Path("inspeccionId") int inspeccionId);

    @POST("api/observaciones")
    Call<Observacion> crearObservacion(@Header("Authorization") String token, @Body Observacion observacion);
    @DELETE("api/observaciones/{id}")
    Call<Void> eliminarObservacion(@Header("Authorization") String token, @Path("id") int id);

}

