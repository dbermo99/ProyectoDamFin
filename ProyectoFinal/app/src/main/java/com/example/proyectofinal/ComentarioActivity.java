package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class ComentarioActivity extends AppCompatActivity {

    EditText comentarioTexto;
    Button comentarBtn;
    SharedPreferences preferences;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);
        getSupportActionBar().hide();

        comentarioTexto = (EditText) findViewById(R.id.comentarioTexto);
        comentarBtn = (Button) findViewById(R.id.comentarBtn);
        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        bundle = getIntent().getExtras();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnComentar);
        bottomNavigationView.setSelectedItemId(R.id.nav_publicar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(ComentarioActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(ComentarioActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(ComentarioActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(ComentarioActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(ComentarioActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        comentarBtn.setOnClickListener(view -> {
            comentar(MainActivity.RED+"crearComentario.php");
        });
    }

    private void comentar(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "PUBLICADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_publicacion",bundle.getString("id"));
                parametros.put("id_usuario",preferences.getString("id", "-1"));
                parametros.put("comentario",comentarioTexto.getText().toString());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}