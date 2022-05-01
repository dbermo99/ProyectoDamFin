package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AmistadesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    AdapterAmistades adapter;

    SharedPreferences preferences;
    Bundle bundle;

    String idUsuario;

    RecyclerView recyclerBuscarAmistades;
    ArrayList<Usuario> usuariosListAmistades;

    SearchView svSearchAmistades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amistades);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        bundle = getIntent().getExtras();

        if(bundle != null) {
            idUsuario = bundle.getString("id");
        } else {
            idUsuario = preferences.getString("id", "-1");
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnAmistades);
        bottomNavigationView.setSelectedItemId(R.id.nav_buscar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(AmistadesActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(AmistadesActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(AmistadesActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(AmistadesActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(AmistadesActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        svSearchAmistades = (SearchView) findViewById(R.id.svSearchAmistades);

        recyclerBuscarAmistades = findViewById(R.id.recyclerBuscarAmistades);
        recyclerBuscarAmistades.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        usuariosListAmistades = new ArrayList<Usuario>();

        initListener();

        cargarUsuarios();
    }

    private void initListener() {
        svSearchAmistades.setOnQueryTextListener(this);
    }

    public void cargarUsuarios() {
        String URL = MainActivity.RED+"buscar_amistad.php?amistadesUsuario="+idUsuario;
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("idUsuario").trim();
                    String email = object.getString("email").trim();
                    String usuario = object.getString("usuario").trim();
                    String contrasenna = object.getString("contrasenna").trim();
                    String nombre = object.getString("nombre").trim();
                    String apellidos = object.getString("apellidos").trim();
                    String foto = object.getString("foto").trim();

                    usuariosListAmistades.add(new Usuario(Integer.parseInt(id),email, usuario, contrasenna, nombre, apellidos, foto));

                    adapter = new AdapterAmistades(usuariosListAmistades, AmistadesActivity.this);

                    adapter.setOnClickListener(view -> {
                        //ESTA ES LA IMPLEMENTACION AL METODO ONCLICK QUE SE HA GENERADO EN EL ADAPTADOR
                        //SIRVE PARA IR AL PERFIL DE CADA USUARIO PINCHANDO EN EL ITEM DE RECYCLERVIEW
                        String idUsu = usuariosListAmistades.get(recyclerBuscarAmistades.getChildAdapterPosition(view)).getId()+"";
                        String usu = usuariosListAmistades.get(recyclerBuscarAmistades.getChildAdapterPosition(view)).getUsuario()+"";
                        comprobarAmistad(MainActivity.RED+"buscar_amistad.php",usu,idUsu);
                    });

                    recyclerBuscarAmistades.setAdapter(adapter);
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

                    String miId = preferences.getString("id", "-1");

                    if((idUsuario1.equalsIgnoreCase(miId) && idUsuario2.equalsIgnoreCase(idUsuario)) ||
                            (idUsuario1.equalsIgnoreCase(idUsuario) && idUsuario2.equalsIgnoreCase(miId))) {
                        existe = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(existe) {
                Intent intent = new Intent(AmistadesActivity.this, PerfilActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                startActivity(intent);
                AmistadesActivity.this.finish();
            } else {
                comprobarPrivacidad(MainActivity.RED+"buscar_usuario.php",usuarioPub, idUsuario);
            }
        }, error -> {
            //Toast.makeText(BuscarActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(AmistadesActivity.this);
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
                Intent intent = new Intent(AmistadesActivity.this, PerfilBloqueadoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                startActivity(intent);
                AmistadesActivity.this.finish();
            } else {
                Intent intent = new Intent(AmistadesActivity.this, PerfilActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",idUsuario);
                bundle.putString("usuario",usuarioPub);
                intent.putExtras(bundle);
                startActivity(intent);
                AmistadesActivity.this.finish();
            }
        }, error -> {
            //Toast.makeText(BuscarActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(AmistadesActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}