package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilBloqueadoActivity extends AppCompatActivity {

    ImageButton seguirBloqueadoBtn;
    ImageView imagenCandado;
    TextView nombreUsuarioBloqueadoTV, amistadesBloqueadoTxt, amistadesTextoCantidadBloqueado;

    SharedPreferences preferences;
    Bundle bundle;

    String usuario, idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_bloqueado);

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        bundle = getIntent().getExtras();

        seguirBloqueadoBtn = (ImageButton) findViewById(R.id.seguirBloqueadoBtn);
        imagenCandado = (ImageView) findViewById(R.id.imagenCandado);
        nombreUsuarioBloqueadoTV = (TextView) findViewById(R.id.nombreUsuarioBloqueadoTV);
        amistadesBloqueadoTxt = (TextView) findViewById(R.id.amistadesBloqueadoTxt);
        amistadesTextoCantidadBloqueado = (TextView) findViewById(R.id.amistadesTextoCantidadBloqueado);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnBloqueado);
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(PerfilBloqueadoActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(PerfilBloqueadoActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(PerfilBloqueadoActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(PerfilBloqueadoActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(PerfilBloqueadoActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        usuario = (String) bundle.getString("usuario");
        idUsuario = (String) bundle.getString("id");

        nombreUsuarioBloqueadoTV.setText(usuario);

        seguirBloqueadoBtn.setOnClickListener(view -> {
            enviarSolicitud(MainActivity.RED+"crearSolicitud.php");
        });

        amistadesBloqueadoTxt.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AmistadesActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

        cargarSolicitudes(MainActivity.RED+"buscar_solicitud.php");

    }

    //ENVIAR SOLICITUD DE AMISTAD
    private void enviarSolicitud(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //SE CAMBIA EL ICONO AL DE SOLICITUD ENVIADA
            seguirBloqueadoBtn.setImageResource(R.drawable.ic_baseline_check_circle_24);
            //AL BOTON SE LE ASIGNA LA FUNCION DE ELIMINAR LA SOLICITUD
            seguirBloqueadoBtn.setOnClickListener(view -> {
                eliminarSolicitud(MainActivity.RED+"crearSolicitud.php?eliminar");
            });
            Toast.makeText(getApplicationContext(), "SOLICITUD ENVIADA", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_usuario1",preferences.getString("id", "-1"));
                parametros.put("id_usuario2",idUsuario);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void eliminarSolicitud(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //CAMBIAS EL ICONO AL DE ENVIAR SOLICITUD
            seguirBloqueadoBtn.setImageResource(R.drawable.ic_baseline_add_24);
            //AL BOTON SE LE ASIGNA LA FUNCION DE CREAR LA SOLICITUD
            seguirBloqueadoBtn.setOnClickListener(view -> {
                enviarSolicitud(MainActivity.RED+"crearSolicitud.php");
            });
            Toast.makeText(getApplicationContext(), "SOLICITUD ELIMINADA", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_usuario1",preferences.getString("id", "-1"));
                parametros.put("id_usuario2",idUsuario);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //COMPRUEBA SI YA TIENE SOLICITUD ENVIADA
    //SI LA TIENE, COLOCA LA IMAGEN CORRECTA Y ASIGNA AL BOTON LA FUNCION CORRECTA
    private void cargarSolicitudes(String URL) {
        String miId = preferences.getString("id", "-1");
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean existe = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idUsuario1 = object.getString("id_usuario1").trim();
                    String idUsuario2 = object.getString("id_usuario2").trim();

                    if(idUsuario1.equalsIgnoreCase(miId) && idUsuario2.equalsIgnoreCase(idUsuario)) {
                        existe = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(existe) {
                //SI HAY SOLICITUD SE COLOCA EL ICONO SE SOLICITUD ENVIADA
                seguirBloqueadoBtn.setImageResource(R.drawable.ic_baseline_check_circle_24);
                //AL BOTON SE LE ASIGNA EL METODO DE ELIMINAR SOLICITUD
                seguirBloqueadoBtn.setOnClickListener(view -> {
                    eliminarSolicitud(MainActivity.RED+"crearSolicitud.php?eliminar");
                });
            } else {
                //SI NO HAY SOLICITUD ENVIADA SE ASIGNA EL METODO DE ENVIAR SOLICITUD
                seguirBloqueadoBtn.setOnClickListener(view -> {
                    enviarSolicitud(MainActivity.RED+"crearSolicitud.php");
                });
            }
        }, error -> Toast.makeText(PerfilBloqueadoActivity.this, "NO EXISTEN SOLICITUDES", Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

}