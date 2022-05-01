package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificacionActivity extends AppCompatActivity {

    RecyclerView recyclerViewNotificaciones;
    ArrayList<Notificacion> notificacionesList;
    AdapterNotificacion adapter;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn7);
        bottomNavigationView.setSelectedItemId(R.id.nav_notificaciones);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(NotificacionActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(NotificacionActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(NotificacionActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(NotificacionActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(NotificacionActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });


        recyclerViewNotificaciones = findViewById(R.id.notificacionesRv);
        recyclerViewNotificaciones.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        notificacionesList = new ArrayList<Notificacion>();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);

        cargarSolicitudes();
    }

    public void cargarSolicitudes() {
        String URL = MainActivity.RED+"buscar_solicitud.php?id_receptor="+preferences.getString("id", "-1");;
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("id").trim();
                    String id_usuario1 = object.getString("id_usuario1").trim();
                    String usuarioEmisor = object.getString("usuario").trim();
                    String fotoUsuarioEmisor = object.getString("foto").trim();
                    String id_usuario2 = object.getString("id_usuario2").trim();

                    notificacionesList.add(new Notificacion(Integer.parseInt(id),Integer.parseInt(id_usuario1),usuarioEmisor,fotoUsuarioEmisor,Integer.parseInt(id_usuario2)));
                    adapter = new AdapterNotificacion(notificacionesList, NotificacionActivity.this);
                    recyclerViewNotificaciones.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(NotificacionActivity.this, "SIN NOTIFICACIONES", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }


}