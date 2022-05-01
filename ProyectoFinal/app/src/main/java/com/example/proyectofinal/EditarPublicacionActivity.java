package com.example.proyectofinal;

import androidx.appcompat.app.AlertDialog;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditarPublicacionActivity extends AppCompatActivity {

    SharedPreferences preferences;
    EditText publicacionTextoEditar;
    Button editar;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_publicacion);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        bundle = getIntent().getExtras();

        publicacionTextoEditar = (EditText) findViewById(R.id.publicacionTextoEditar);
        editar = (Button) findViewById(R.id.editarPublicacionBtn);

        cargar_datos(MainActivity.RED+"buscar_publicacion.php");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnEDitarPub);
        bottomNavigationView.setSelectedItemId(R.id.nav_perfil); //SELECCIONAMOS EL ITEM DEL PERFIL
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(EditarPublicacionActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(EditarPublicacionActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(EditarPublicacionActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(EditarPublicacionActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(EditarPublicacionActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });


        editar.setOnClickListener(view -> {
            //CREAMOS UNA ALERTA PARA QUE EL USUARIO CONFIRME O CANCELE
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
            myBuilder.setMessage("¿DESEA EDITAR LA PUBLICACIÓN?");
            myBuilder.setTitle("EDITAR PUBLICACION");
            myBuilder.setPositiveButton("SI", (dialogInterface, i) -> {

                actualizar(MainActivity.RED+"editar_publicacion.php");

            });
            myBuilder.setNegativeButton("NO", (dialog, i) -> dialog.cancel());

            AlertDialog dialog = myBuilder.create();
            dialog.show();

        });

    }

    private void actualizar(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            Toast.makeText(getApplicationContext(), "CORRECTO", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(), MiPerfilActivity.class);
            startActivity(intent);
            finish();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id",bundle.getString("id"));
                parametros.put("texto",publicacionTextoEditar.getText().toString());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void cargar_datos(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String id = object.getString("id").trim();
                    String texto = object.getString("texto").trim();

                    if(id.equalsIgnoreCase(bundle.getString("id"))) {
                        publicacionTextoEditar.setText(texto);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(context, "NO EXISTEN Publicaciones", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }
}