package com.example.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class AdapterComentario extends  RecyclerView.Adapter<AdapterComentario.ComentarioHolder>{

    ArrayList<Comentario> comentariosList;

    public AdapterComentario(ArrayList<Comentario> comentariosList) {
        this.comentariosList = comentariosList;
    }

    @NonNull
    @Override
    public AdapterComentario.ComentarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comentario_layout,null,false);
        return new AdapterComentario.ComentarioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComentario.ComentarioHolder holder, int position) {
        holder.asignarDatosComentarios(comentariosList.get(position));
    }

    @Override
    public int getItemCount() {
        return comentariosList.size();
    }

    public class ComentarioHolder extends RecyclerView.ViewHolder {
        TextView usuComentario, textoComentario;
        public ComentarioHolder(@NonNull View itemView) {
            super(itemView);
            usuComentario = itemView.findViewById(R.id.usuComentario);
            textoComentario = itemView.findViewById(R.id.textoComentario);
        }

        public void asignarDatosComentarios(Comentario c1) {
            usuComentario.setText(c1.getUsuario());
            textoComentario.setText(c1.getComentario());
        }
    }


}
