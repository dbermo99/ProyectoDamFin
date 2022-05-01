package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class BuscarActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //IMPLEMENTAMOS PARA PODER PODER HACER LAS BUSQUEDAS

    AdapterUsuarios adapter;

    RecyclerView recyclerBuscar;
    ArrayList<Usuario> usuariosList;

    SharedPreferences preferencias;

    SearchView svSearch; //CREAMOS  EL OBJETO SEARCHVIEW PARA LAS BUSQUEDAS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn2);
        bottomNavigationView.setSelectedItemId(R.id.nav_buscar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(BuscarActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(BuscarActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(BuscarActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(BuscarActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(BuscarActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        svSearch = (SearchView) findViewById(R.id.svSearch);

        recyclerBuscar = findViewById(R.id.recyclerBuscar);
        recyclerBuscar.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        usuariosList = new ArrayList<Usuario>();

        preferencias = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        initListener();

        cargarUsuarios();

    }

    public void cargarUsuarios() {
        String URL = MainActivity.RED+"buscar_usuario.php";
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("id").trim();
                    String email = object.getString("email").trim();
                    String usuario = object.getString("usuario").trim();
                    String contrasenna = object.getString("contrasenna").trim();
                    String nombre = object.getString("nombre").trim();
                    String apellidos = object.getString("apellidos").trim();
                    String foto = object.getString("foto").trim();

                    //SI NO SOY YO, AGREGO EL USUARIO A LA LISTA
                    if(!id.equalsIgnoreCase(preferencias.getString("id", "-1")))
                        usuariosList.add(new Usuario(Integer.parseInt(id),email, usuario, contrasenna, nombre, apellidos, foto));

                    adapter = new AdapterUsuarios(usuariosList, BuscarActivity.this);

                    adapter.setOnClickListener(view -> {
                        //ESTA ES LA IMPLEMENTACION AL METODO ONCLICK QUE SE HA GENERADO EN EL ADAPTADOR
                        //SIRVE PARA IR AL PERFIL DE CADA USUARIO PINCHANDO EN EL ITEM DE RECYCLERVIEW
                        String idUsu = usuariosList.get(recyclerBuscar.getChildAdapterPosition(view)).getId()+"";
                        String usu = usuariosList.get(recyclerBuscar.getChildAdapterPosition(view)).getUsuario()+"";
                        comprobarAmistad(MainActivity.RED+"buscar_amistad.php",usu,idUsu);
                    });

                    recyclerBuscar.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            //Toast.makeText(BuscarActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void comprobarAmistad(String URL, String usuarioPub, String idUsuario) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean existe = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idUsuario1 = object.getString("id_usuario1").trim();
                    String idUsuario2 = object.getString("id_usuario2").trim();

                    String miId = preferencias.getString("id", "-1");

                    if((idUsuario1.equalsIgnoreCase(miId) && idUsuario2.equalsIgnoreCase(idUsuario)) ||
                            (idUsuario1.equalsIgnoreCase(idUsuario) && idUsuario2.equalsIgnoreCase(miId))) {
                        existe = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(existe) {
                Intent intent = new Intent(BuscarActivity.this, PerfilActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                BuscarActivity.this.startActivity(intent);
                BuscarActivity.this.finish();
            } else {
                comprobarPrivacidad(MainActivity.RED+"buscar_usuario.php",usuarioPub, idUsuario);
            }
        }, error -> {
            //Toast.makeText(BuscarActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(BuscarActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    private void comprobarPrivacidad(String URL, String usuarioPub, String idUsuario) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean privada = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idUsuario1 = object.getString("id").trim();
                    String privada1 = object.getString("privada").trim();

                    if((idUsuario1.equalsIgnoreCase(idUsuario)) && (privada1.equalsIgnoreCase("1"))) {
                        privada = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(privada) {
                Intent intent = new Intent(BuscarActivity.this, PerfilBloqueadoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                BuscarActivity.this.startActivity(intent);
                BuscarActivity.this.finish();
            } else {
                Intent intent = new Intent(BuscarActivity.this, PerfilActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                BuscarActivity.this.startActivity(intent);
                BuscarActivity.this.finish();
            }
        }, error -> {
            //Toast.makeText(BuscarActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(BuscarActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    //PARA QUE SE INICIE EL ESCUCHADOR
    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }

    //AL IMPLEMENTAR EL SEARCHVIEW NOS OBLIGA A CREAR ESTOS METODOS
    //ESTE SE USA SI PULSAMOS EL BOTON PARA BUSCAR
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //ESTE SE USA PARA BUSCAR CADA VEZ QUE SE ESCRIBE O BORRA UNA LETRA, CUNADO SE MODIFICA EL TEXTO
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        //CADA VEZ QUE PULSEMOS UNA LETRA LLAMAMOS AL METODO FILTER DEL ADAPTER Y LE ENVIAMOS EL TEXTO QUE HAY EN EL BUSCADOR
        return false;
    }
}