package com.example.proyectofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    EditText emailText2, usuarioText2, claveText2, clave2Text2, nombreText2, apellidosText2;
    CheckBox privadaActualizarCbx;
    Button guardarCambiosBtn, volverBtn;
    SharedPreferences preferences;

    String emailTxt, usuarioTxt, claveTxt, clave2Txt, nombreTxt, apellidosTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);

        emailText2 = (EditText) findViewById(R.id.emailText2);
        usuarioText2 = (EditText) findViewById(R.id.usuarioText2);
        claveText2 = (EditText) findViewById(R.id.claveText2);
        clave2Text2 = (EditText) findViewById(R.id.clave2Text2);
        nombreText2 = (EditText) findViewById(R.id.nombreText2);
        apellidosText2 = (EditText) findViewById(R.id.apellidosText2);
        privadaActualizarCbx = (CheckBox) findViewById(R.id.privadaActualizarCbx);
        guardarCambiosBtn = (Button) findViewById(R.id.guardarCambiosBtn);
        volverBtn = (Button) findViewById(R.id.volverBtn);

        cargar_datos(MainActivity.RED+"buscar_usuario.php");

        guardarCambiosBtn.setOnClickListener(view -> {
            nombreTxt = nombreText2.getText().toString();
            usuarioTxt = usuarioText2.getText().toString();
            claveTxt = claveText2.getText().toString();
            clave2Txt = clave2Text2.getText().toString();
            nombreTxt = nombreText2.getText().toString();
            apellidosTxt = apellidosText2.getText().toString();
            emailTxt = emailText2.getText().toString();

            //CREAMOS UNA ALERTA PARA QUE EL USUARIO CONFIRME O CANCELE
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
            myBuilder.setMessage("¿DESEA EDITAR EL PERFIL?");
            myBuilder.setTitle("EDITAR PERFIL");
            myBuilder.setPositiveButton("SI", (dialogInterface, i) -> {

                if(claveTxt.equals(clave2Txt))
                    actualizar(MainActivity.RED+"editar_usuario.php");
                else
                    Toast.makeText(getApplicationContext(), "LAS CLAVES NO COINCIDEN", Toast.LENGTH_SHORT).show();
            });
            myBuilder.setNegativeButton("NO", (dialog, i) -> dialog.cancel());

            AlertDialog dialog = myBuilder.create();
            dialog.show();

        });

        volverBtn.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(), MiPerfilActivity.class);
            startActivity(intent);
            finish();
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
                String privada = "0";
                if(privadaActualizarCbx.isChecked())
                    privada = "1";

                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id",preferences.getString("id", "-1"));
                parametros.put("usuario",usuarioTxt);
                parametros.put("password",claveTxt);
                parametros.put("nombre",nombreTxt);
                parametros.put("apellidos",apellidosTxt);
                parametros.put("privada",privada);
                parametros.put("mail",emailTxt);
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
                    String email = object.getString("email").trim();
                    String usuario = object.getString("usuario").trim();
                    String contrasenna = object.getString("contrasenna").trim();
                    String nombre = object.getString("nombre").trim();
                    String apellidos = object.getString("apellidos").trim();
                    String privada = object.getString("privada").trim();

                    if(id.equalsIgnoreCase(preferences.getString("id", "-1"))) {
                        emailText2.setText(email);
                        usuarioText2.setText(usuario);
                        claveText2.setText(contrasenna);
                        clave2Text2.setText(contrasenna);
                        nombreText2.setText(nombre);
                        apellidosText2.setText(apellidos);
                        if(privada.equalsIgnoreCase("1"))
                            privadaActualizarCbx.setChecked(true);
                        else
                            privadaActualizarCbx.setChecked(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
            //Toast.makeText(context, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

}