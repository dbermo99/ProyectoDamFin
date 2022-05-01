package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    ImageButton seguirBtn;
    TextView nombreUsuarioTV, amistadesTxt, amistadesTextoCantidad;
    RecyclerView recyclerView;
    ArrayList<Publicacion> publicacionList;

    SharedPreferences preferences;
    Bundle bundle;

    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getSupportActionBar().hide();

        seguirBtn = (ImageButton) findViewById(R.id.seguirBtn);
        nombreUsuarioTV = (TextView) findViewById(R.id.nombreUsuarioTV);
        amistadesTextoCantidad = (TextView) findViewById(R.id.amistadesTextoCantidad);
        amistadesTxt = (TextView) findViewById(R.id.amistadesTxt);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn6);
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(PerfilActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(PerfilActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(PerfilActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(PerfilActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(PerfilActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewPerfil);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        publicacionList = new ArrayList<Publicacion>();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        bundle = getIntent().getExtras();

        String usuario = (String) bundle.getString("usuario");
        idUsuario = (String) bundle.getString("id");

        nombreUsuarioTV.setText(usuario);

        comprobarAmistad(MainActivity.RED+"buscar_amistad.php");
        cargarAmistades(MainActivity.RED+"buscar_amistad.php?idUsuario="+idUsuario);

        cargarPublicaciones(MainActivity.RED+"buscar_publicacion.php?idUsuario="+idUsuario);

        amistadesTxt.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AmistadesActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

    }

    //COMPRUBA SI TIENES AMISTAD CON ESE USUARIO
    private void comprobarAmistad(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean amigo = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idUsuario1 = object.getString("id_usuario1").trim();
                    String idUsuario2 = object.getString("id_usuario2").trim();

                    if(((idUsuario1.equalsIgnoreCase(idUsuario)) && (idUsuario2.equalsIgnoreCase(preferences.getString("id", "-1")))) ||
                            ((idUsuario2.equalsIgnoreCase(idUsuario)) && (idUsuario1.equalsIgnoreCase(preferences.getString("id", "-1"))))) {
                        amigo = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(amigo) {
                seguirBtn.setImageResource(R.drawable.ic_baseline_contacts_24);
                seguirBtn.setOnClickListener(view -> {
                    eliminarAmistad(MainActivity.RED+"crearAmistad.php?eliminar");
                });
            } else {
                comprobarSolicitud(MainActivity.RED+"buscar_solicitud.php");
            }
        }, error -> {
            //Toast.makeText(context, "NO HAY AMISTADES", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void comprobarSolicitud(String URL) {

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean solicitud = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idUsuario1 = object.getString("id_usuario1").trim();
                    String idUsuario2 = object.getString("id_usuario2").trim();

                    if(((idUsuario1.equalsIgnoreCase(idUsuario)) && (idUsuario2.equalsIgnoreCase(preferences.getString("id", "-1")))) ||
                            ((idUsuario2.equalsIgnoreCase(idUsuario)) && (idUsuario1.equalsIgnoreCase(preferences.getString("id", "-1"))))) {
                        solicitud = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(solicitud) {
                seguirBtn.setImageResource(R.drawable.ic_baseline_check_circle_24);
                seguirBtn.setOnClickListener(view -> {
                    eliminarSolicitud(MainActivity.RED+"crearSolicitud.php?eliminar");
                });
            } else {
                seguirBtn.setOnClickListener(view -> {
                    enviarSolicitud(MainActivity.RED+"crearSolicitud.php");
                });
            }
        }, error -> {
            //Toast.makeText(context, "NO HAY SOLICITUDES", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    //CARGA LA CANTIDAD DE AMISTADES
    private void cargarAmistades(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String cantAmistades = object.getString("COUNT(*)").trim();

                    amistadesTextoCantidad.setText(cantAmistades);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            //Toast.makeText(PerfilActivity.this, "NO HAY AMISTADES", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void cargarPublicaciones(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    int id = Integer.parseInt(object.getString("id").trim());
                    String usuario = object.getString("usuario").trim();
                    String texto = object.getString("texto").trim();
                    String foto = object.getString("foto").trim();

                    publicacionList.add(new Publicacion(id, usuario, texto, foto));

                    Adapter adapter = new Adapter(publicacionList, PerfilActivity.this, preferences);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            //Toast.makeText(context, "NO HAY PUBLICACIONES", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    public void eliminarAmistad(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //SI ELIMINAMOS LA AMISTAD, VAMOS AL PERFIL BLOQUEADO
            Intent intent = new Intent(PerfilActivity.this, PerfilBloqueadoActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "AMISTAD ELIMINADA", Toast.LENGTH_SHORT).show();
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

    //ENVIAR SOLICITUD DE AMISTAD
    private void enviarSolicitud(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //SE CAMBIA EL ICONO AL DE SOLICITUD ENVIADA
            seguirBtn.setImageResource(R.drawable.ic_baseline_check_circle_24);
            //AL BOTON SE LE ASIGNA LA FUNCION DE ELIMINAR LA SOLICITUD
            seguirBtn.setOnClickListener(view -> {
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
            seguirBtn.setImageResource(R.drawable.ic_baseline_add_24);
            //AL BOTON SE LE ASIGNA LA FUNCION DE CREAR LA SOLICITUD
            seguirBtn.setOnClickListener(view -> {
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

}