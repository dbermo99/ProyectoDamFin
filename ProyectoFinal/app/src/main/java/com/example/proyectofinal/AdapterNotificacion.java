package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterNotificacion extends  RecyclerView.Adapter<AdapterNotificacion.NotificacionHolder>{

    ArrayList<Notificacion> solicitudesList;
    RequestQueue request;
    Context context;

    public AdapterNotificacion(ArrayList<Notificacion> solicitudesList, Context context) {
        this.solicitudesList = solicitudesList;
        this.context = context;
        request = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public NotificacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notificacion_layout,null,false);
        return new NotificacionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotificacion.NotificacionHolder holder, int position) {
        holder.asignarDatosNotificacion(solicitudesList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return solicitudesList.size();
    }

    public class NotificacionHolder extends RecyclerView.ViewHolder {
        TextView usuario;
        ImageButton aceptarSolicitusBtn, eliminarSolicitudBtn;
        ImageView imageView3;
        public NotificacionHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuarioCard3);
            imageView3 = itemView.findViewById(R.id.imageView3);
            aceptarSolicitusBtn = itemView.findViewById(R.id.aceptarSolicitudBtn);
            eliminarSolicitudBtn = itemView.findViewById(R.id.eliminarSolicitudBtn);
        }

        public void asignarDatosNotificacion(Notificacion n2, NotificacionHolder holder) {
            usuario.setText(n2.getUsuarioEmisor());

            String imagen = n2.getFotoUsuarioEmisor();
            String url = MainActivity.RED+"imagenes/"+imagen;

            cargarImagen(url, holder);

            //ASIGNO ACCIONES A LOS BOTONES
            aceptarSolicitusBtn.setOnClickListener(view -> {
                crearAmistad(n2.getId_usuario1(), n2.getId_usuario2());
                eliminarSolicitud(n2.getId_usuario1(), n2.getId_usuario2());
                solicitudesList.remove(n2); //ELIMINAMOS LA SOLICITUD DE LA LISTA
                notifyDataSetChanged(); //NOTIFICAMOS EL CAMBIO PARA QUE SE ACTUALICE LA LISTA
            });
            eliminarSolicitudBtn.setOnClickListener(view -> {
                eliminarSolicitud(n2.getId_usuario1(), n2.getId_usuario2());
                solicitudesList.remove(n2);
                notifyDataSetChanged();
            });
        }
    }

    private void cargarImagen(String url, AdapterNotificacion.NotificacionHolder holder) {
        url = url.replace(" ","%20");

        ImageRequest imageRequest = new ImageRequest(url,
                response -> holder.imageView3.setImageBitmap(response), 0, 0,
                ImageView.ScaleType.CENTER, null, error -> {
            Toast.makeText(context, "ERROR AL CARGAR LA IMAGEN", Toast.LENGTH_SHORT).show();
        });
        request.add(imageRequest);
    }

    public void crearAmistad(int id1, int id2) {
        String URL = MainActivity.RED+"crearAmistad.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_usuario1",id1+"");
                parametros.put("id_usuario2",id2+"");
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    public void eliminarSolicitud(int id1, int id2) {
        String URL = MainActivity.RED+"crearSolicitud.php?eliminar";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //Toast.makeText(context, "Solicitud eliminada", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_usuario1",id1+"");
                parametros.put("id_usuario2",id2+"");
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}



