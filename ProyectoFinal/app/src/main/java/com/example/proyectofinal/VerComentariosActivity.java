package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VerComentariosActivity extends AppCompatActivity {

    RecyclerView recyclerViewComentarios;
    ArrayList<Comentario> comentariosList;
    AdapterComentario adapter;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_comentarios);
        getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnComentarios);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(VerComentariosActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(VerComentariosActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(VerComentariosActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(VerComentariosActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(VerComentariosActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        recyclerViewComentarios = findViewById(R.id.comentariosRv);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        comentariosList = new ArrayList<Comentario>();
        bundle = getIntent().getExtras();

        cargarComentarios();
    }

    public void cargarComentarios() {
        String URL = MainActivity.RED+"buscar_comentarios.php?id_publicacion="+bundle.getString("id");
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("id").trim();
                    String usuario = object.getString("usuario").trim();
                    String comentario = object.getString("comentario").trim();

                    comentariosList.add(new Comentario(Integer.parseInt(id), usuario, comentario));
                    adapter = new AdapterComentario(comentariosList);
                    recyclerViewComentarios.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(VerComentariosActivity.this, "SIN COMENTARIOS", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }
}