package com.ucenm.inspeccionesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ucenm.inspeccionesapp.models.Fotografia;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {

    private List<Fotografia> listaFotos;
    private String baseUrl;

    public FotoAdapter(List<Fotografia> listaFotos, String baseUrl) {
        this.listaFotos = listaFotos;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Fotografia foto = listaFotos.get(position);
        String urlCompleta = baseUrl + foto.getRutaImagen();
        Glide.with(holder.itemView.getContext())
                .load(urlCompleta)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}