package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecuperarClaveActivity extends AppCompatActivity {

    EditText correoUsu, usuarioRecuperar, nuevaClaveRecuperar, nuevaClaveRecuperar2;
    Button btnRecuperar, inicioBtnRecuperarClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);
        getSupportActionBar().hide();

        correoUsu = (EditText) findViewById(R.id.correoUsuRecuperar);
        usuarioRecuperar = (EditText) findViewById(R.id.usuarioRecuperar);
        nuevaClaveRecuperar = (EditText) findViewById(R.id.nuevaClaveRecuperar);
        nuevaClaveRecuperar2 = (EditText) findViewById(R.id.nuevaClaveRecuperar2);

        btnRecuperar = (Button) findViewById(R.id.btnRecuperar);
        inicioBtnRecuperarClave = (Button) findViewById(R.id.inicioBtnRecuperarClave);

        btnRecuperar.setOnClickListener(view -> {
            String correoU = correoUsu.getText().toString().trim();
            String usuR = usuarioRecuperar.getText().toString().trim();
            String nuevaClave = nuevaClaveRecuperar.getText().toString().trim();
            String nuevaClave2 = nuevaClaveRecuperar2.getText().toString().trim();

            if(correoU.length()>0 && usuR.length()>0 && nuevaClave.length()>0 && nuevaClave2.length()>0) {
                if(nuevaClave.equals(nuevaClave2)) {
                    comprobarUsuario(MainActivity.RED+"buscar_usuario.php", view.getContext());
                }else {
                    Toast.makeText(getApplicationContext(), "LAS CLAVES NO COINCIDEN", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "RELLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
            }

        });

        inicioBtnRecuperarClave.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private void comprobarUsuario(String URL, Context context) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, response -> {
            JSONObject jsonObject = null;
            String nombreU = "";
            String emailU = "";
            boolean existe = false;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);

                    nombreU = jsonObject.getString("usuario");
                    emailU = jsonObject.getString("email");
                    if(nombreU.equalsIgnoreCase(usuarioRecuperar.getText().toString()) && emailU.equalsIgnoreCase(correoUsu.getText().toString())) {
                        existe = true;
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if(existe) {
                recuperarClave(MainActivity.RED+"recuperarClave.php", context);
            } else {
                Toast.makeText(getApplicationContext(), "NO EXISTE ESE USUARIO CON ESE CORREO", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void recuperarClave(String URL, Context context) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            Toast.makeText(getApplicationContext(), "CLAVE MODIFICADA CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("usuario",usuarioRecuperar.getText().toString());
                parametros.put("password",nuevaClaveRecuperar.getText().toString());
                parametros.put("mail",correoUsu.getText().toString());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



}