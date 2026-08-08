package com.ucenm.inspeccionesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.inspeccionesapp.models.Inspeccion;

import java.util.List;

public class InspeccionAdapter extends RecyclerView.Adapter<InspeccionAdapter.InspeccionViewHolder> {

    private List<Inspeccion> listaInspecciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Inspeccion inspeccion);
    }

    public InspeccionAdapter(List<Inspeccion> listaInspecciones, OnItemClickListener listener) {
        this.listaInspecciones = listaInspecciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InspeccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inspeccion, parent, false);
        return new InspeccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InspeccionViewHolder holder, int position) {
        Inspeccion inspeccion = listaInspecciones.get(position);
        holder.tvTitulo.setText(inspeccion.getTitulo());
        holder.tvFecha.setText(inspeccion.getFechaInspeccion());
        holder.tvEstado.setText(inspeccion.getEstado());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(inspeccion));
    }

    @Override
    public int getItemCount() {
        return listaInspecciones.size();
    }

    static class InspeccionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvEstado;

        public InspeccionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}