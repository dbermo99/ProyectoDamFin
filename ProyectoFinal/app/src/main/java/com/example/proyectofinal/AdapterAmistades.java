package com.example.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class AdapterAmistades extends  RecyclerView.Adapter<AdapterAmistades.UsuarioHolder> implements View.OnClickListener {

    ArrayList<Usuario> usuarioList;
    ArrayList<Usuario> listaOriginal;
    private View.OnClickListener listener;
    RequestQueue request;
    Context context;

    public AdapterAmistades(ArrayList<Usuario> usuarioList, Context context) {
        this.usuarioList = usuarioList;
        listaOriginal = new ArrayList<Usuario>();
        listaOriginal.addAll(usuarioList);
        this.context = context;
        request = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public AdapterAmistades.UsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_buscar_layout,null,false);
        view.setOnClickListener(this);
        return new AdapterAmistades.UsuarioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAmistades.UsuarioHolder holder, int position) {
        holder.asignarDatos(usuarioList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) { //EL QUE VA A ESCUCHAR AL EVENTO, ES EL DE LA LINEA 21
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) {
            listener.onClick(view);
        }
    }

    public class UsuarioHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        ImageView imageView2;
        public UsuarioHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuarioCard2);
            imageView2 = itemView.findViewById(R.id.imageView2);
        }

        public void asignarDatos(Usuario usuario2, AdapterAmistades.UsuarioHolder holder) {
            usuario.setText(usuario2.getUsuario());

            String imagen = usuario2.getFoto();
            String url = MainActivity.RED+"imagenes/"+imagen;

            cargarImagen(url, holder);
        }
    }

    private void cargarImagen(String url, AdapterAmistades.UsuarioHolder holder) {
        url = url.replace(" ","%20");
        ImageRequest imageRequest = new ImageRequest(url,
                response -> holder.imageView2.setImageBitmap(response), 0, 0,
                ImageView.ScaleType.CENTER, null, error -> {
            Toast.makeText(context, "ERROR AL CARGAR LA IMAGEN", Toast.LENGTH_SHORT).show();
        });
        request.add(imageRequest);
    }

    public void filter(String strSearch) {
        if(strSearch.length() == 0) {
            usuarioList.clear();
            usuarioList.addAll(listaOriginal);
        } else {
            usuarioList.clear();
            for(Usuario u: listaOriginal) {
                if(u.getUsuario().toLowerCase().contains(strSearch)) {
                    usuarioList.add(u);
                }
            }
        }
        notifyDataSetChanged();
    }

}
