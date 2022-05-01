package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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
import java.util.HashMap;
import java.util.Map;

public class MiPerfilActivity extends AppCompatActivity {

    TextView nombreUsuarioTV, MisAmistadesTxt, MisAmistadesTextoCantidad;
    RecyclerView recyclerView;
    ArrayList<Publicacion> publicacionList;
    ImageButton salirBtn, editarPerfilBtn, eliminarCuentaBtn;
    String fotoUsu; //CARGAMOS EL NOMBRE DE LA FOTO DE MI PERFIL POR SI ELIMINO MI CUENTA, QUE SE ELIMINE LA FOTO DEL SERVIDOR
    String idUsuario;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        getSupportActionBar().hide();

        nombreUsuarioTV = (TextView) findViewById(R.id.nombreMiUsuarioTV);
        MisAmistadesTextoCantidad = (TextView) findViewById(R.id.MisAmistadesTextoCantidad);
        MisAmistadesTxt = (TextView) findViewById(R.id.MisAmistadesTxt);
        salirBtn = (ImageButton) findViewById(R.id.salirBtn);
        editarPerfilBtn = (ImageButton) findViewById(R.id.editarPerfilBtn);
        eliminarCuentaBtn = (ImageButton) findViewById(R.id.eliminarCuentaBtn);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn3);
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(MiPerfilActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(MiPerfilActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(MiPerfilActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(MiPerfilActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(MiPerfilActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewMiPerfil);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        publicacionList = new ArrayList<Publicacion>();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("id", "-1");
        String usuario = preferences.getString("usuario", "Usuario");

        nombreUsuarioTV.setText(usuario);

        cargarNombreFoto(MainActivity.RED+"buscar_usuario.php");

        cargarPublicaciones(MainActivity.RED+"buscar_publicacion.php?idUsuario="+idUsuario);
        cargarAmistades(MainActivity.RED+"buscar_amistad.php?idUsuario="+idUsuario);

        MisAmistadesTxt.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AmistadesActivity.class);
            startActivity(intent);
            finish();
        });

        editarPerfilBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EditarPerfilActivity.class);
            startActivity(intent);
            finish();
        });

        salirBtn.setOnClickListener(view -> {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
            myBuilder.setMessage("¿DESEA CERRAR SESIÓN?");
            myBuilder.setTitle("CERRAR SESIÓN");
            myBuilder.setPositiveButton("SI", (dialogInterface, i) -> {
                preferences.edit().clear().commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            });
            myBuilder.setNegativeButton("NO", (dialog, i) -> dialog.cancel());

            AlertDialog dialog = myBuilder.create();
            dialog.show();
        });

        eliminarCuentaBtn.setOnClickListener(view -> {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
            myBuilder.setMessage("¿DESEA ELIMINAR SU CUENTA?");
            myBuilder.setTitle("ELIMINACIÓN DE CUENTA PERSONAL");
            myBuilder.setPositiveButton("SI", (dialogInterface, i) -> eliminarCuenta(MainActivity.RED+"borrar_usuario.php"));
            myBuilder.setNegativeButton("NO", (dialog, i) -> dialog.cancel());

            AlertDialog dialog = myBuilder.create();
            dialog.show();
        });

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

                    AdapterMiPub adapter = new AdapterMiPub(publicacionList, MiPerfilActivity.this, preferences);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(MiPerfilActivity.this, "NO TIENS PUBLICACIONES", Toast.LENGTH_SHORT).show();
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

                    MisAmistadesTextoCantidad.setText(cantAmistades);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(MiPerfilActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void cargarNombreFoto(String URL) {
        //CARGAMOS EL NOMBRE DE LA FOTO DE MI PERFIL POR SI ELIMINO MI CUENTA, QUE SE ELIMINE LA FOTO DEL SERVIDOR
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String idConsulta = object.getString("id").trim();
                    String foto = object.getString("foto").trim();
                    if(idConsulta.equalsIgnoreCase(idUsuario))
                        fotoUsu = foto;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(MiPerfilActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void eliminarCuenta(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            preferences.edit().clear().commit();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("idUsuario",preferences.getString("id", "-1"));
                parametros.put("fotoUsu",fotoUsu);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}