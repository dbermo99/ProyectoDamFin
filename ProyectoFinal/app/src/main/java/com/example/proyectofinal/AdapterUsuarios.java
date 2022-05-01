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

public class AdapterUsuarios extends  RecyclerView.Adapter<AdapterUsuarios.UsuarioHolder> implements View.OnClickListener {
    //IMPLEMENTAMOS PARA PODER USAR ONCLICKLISTENER EN CADA ITEM

    ArrayList<Usuario> usuarioList; //ESTA LISTA AL PRINCIPIO TIENE TODOS LOS USUARIOS, IRÁ CAMBIANDO SEGUN BUSQUEMOS
    ArrayList<Usuario> listaOriginal; //EN ESTA LISTA TENDREMOS SIMPRE A TODOS LOS USUARIO
    private View.OnClickListener listener; //ESCUCHADOR DEL EVENTO ONCLICK
    RequestQueue request;
    Context context;

    public AdapterUsuarios(ArrayList<Usuario> usuarioList, Context context) {
        this.usuarioList = usuarioList;
        listaOriginal = new ArrayList<Usuario>();
        listaOriginal.addAll(usuarioList); //AL INICIAR GUARDAMOS LOS USUARIOS QUE HAY EN USUARIOlIST, QUE EN ESTE MOMENTO TIENE TODOS
        this.context = context;
        request = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public UsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_buscar_layout,null,false);
        view.setOnClickListener(this); //ACTIVAMOS EL ESCUCHADOR
        return new UsuarioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUsuarios.UsuarioHolder holder, int position) {
        holder.asignarDatos(usuarioList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) { //EL QUE VA A ESCUCHAR AL EVENTO, ES EL DE LA LINEA 21
        this.listener = listener;
    }

    //METODO ONCLICK PARA CADA ITEM
    @Override
    public void onClick(View view) {
        if(listener != null) { //si es distinto de null puede reconocer el evento onClick
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

        public void asignarDatos(Usuario usuario2, UsuarioHolder holder) {
            usuario.setText(usuario2.getUsuario());

            String imagen = usuario2.getFoto();
            String url = MainActivity.RED+"imagenes/"+imagen;

            cargarImagen(url, holder);
        }
    }

    private void cargarImagen(String url, AdapterUsuarios.UsuarioHolder holder) {
        url = url.replace(" ","%20");
        ImageRequest imageRequest = new ImageRequest(url,
                response -> holder.imageView2.setImageBitmap(response), 0, 0,
                ImageView.ScaleType.CENTER, null, error -> {
            Toast.makeText(context, "ERROR AL CARGAR LA IMAGEN", Toast.LENGTH_SHORT).show();
        });
        request.add(imageRequest);
    }

    //CON ESTE METODO IREMOS REALIZANDO LAS BUSQUEDAS
    public void filter(String strSearch) { //RECIBIMOS POR PARAMETRO EL TEXTO DEL BUSCADOR
        if(strSearch.length() == 0) { //SI LA LONGITUD ES 0, LIMPIAMOS LA LISTA Y LA VOLVEMOS A LLENAR CON TODOS LOS USUARIOS
            usuarioList.clear();
            usuarioList.addAll(listaOriginal);
        } else { //SI RECIBE TEXTO, LIMPIAMOS LA LISTA, Y VAMOS AÑADIENDO LOS USUARIOS QUE CONTENGAN ESE TEXTO
            usuarioList.clear();
            for(Usuario u: listaOriginal) {
                if(u.getUsuario().toLowerCase().contains(strSearch)) {
                    usuarioList.add(u);
                }
            }
        }
        notifyDataSetChanged(); //NOTIFICAMOS LOS CAMBIOS PARA QUE SE HAGAN EFECTIVOS
    }


}
