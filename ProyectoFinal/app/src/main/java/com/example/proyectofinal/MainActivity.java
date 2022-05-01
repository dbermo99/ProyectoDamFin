package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Publicacion> publicacionList;
    SharedPreferences preferencias;

    public static final String RED = "http://192.168.0.14/PruebasProyecto/";
    //public static final String RED = "http://10.102.29.160/PruebasProyecto/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.reciclerId);
        //LE INDICAMOS QUE TENGA UN LAYOUT DE TIPO LINEAL, DE FORMA VERITCAL
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        publicacionList = new ArrayList<Publicacion>();

        preferencias = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); //SELECCIONAMOS EL ITEM DEL MAIN
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(MainActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(MainActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(MainActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(MainActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });




        //SI NO HAY SESION INICIADA VAMOS DIRECTAMENTRE AL LOGIN
        boolean sesion = preferencias.getBoolean("sesion", false);
        if(!sesion) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            //CUANDO YA HAY SESIÓN INICIADA, MUESTRO MIS PUBLICACIONES Y LA DE LOS USUARIOS A LOS QUE SIGO
            cargarPublicaciones(MainActivity.RED+"buscar_publicacion.php?misSeguidos="+preferencias.getString("id", "-1"));
            Toast.makeText(MainActivity.this, preferencias.getString("id", "-1"), Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPublicaciones(String URL) {
        //OBTENEMOS LOS RESULTADOS DE LA CONSULTA GENERADA EN FORMATO JSON DESDE EL PHP
        //Y SE CREA UN ARRAY
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    //OBTENEMOS UN OBJETO POR CADA TUPLA DEL ARRAY
                    JSONObject object = array.getJSONObject(i);
                    int id = Integer.parseInt(object.getString("id").trim());
                    String usuario = object.getString("usuario").trim();
                    String texto = object.getString("texto").trim();
                    String foto = object.getString("foto").trim();
                    //CREAMOS UNA PUBLICACION CON LOS DATOS DE CADA OBJETO Y LO AÑADIMOS AL ARRAYLIST
                    publicacionList.add(new Publicacion(id, usuario, texto, foto));
                    //CREAMOS EL ADAPTER PARA EL RECYCLERVIEW, ENVIANDO POR PARÁMETROS EL ARRAYLIST,
                    //EL CONTEXT Y EL SHAREDPREFERENCES PARA PODER USARLOS EN EL ADAPTER
                    Adapter adapter = new Adapter(publicacionList, MainActivity.this, preferencias);
                    //ENVIAMOS EL ADAPTER EL RECYCLERVIEW PARA MOSTRAR TODAS LAS PUBLICACIONES
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(MainActivity.this, "NO EXISTEN PUBLICACIONES", Toast.LENGTH_SHORT).show();
        });

        //PROCESAMOS LAS PETICIONES HECHAS PARA QUE LA LIBRERÍA SE ENCARGUE DE EJECUTARLAS
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //ENVIAMOS LA SOLICITUD ENVIANDO EL OBJETO jsonArrayRequest DENTRO DEL REQUESTQUEUE
        requestQueue.add(jsonArrayRequest);

    }


}