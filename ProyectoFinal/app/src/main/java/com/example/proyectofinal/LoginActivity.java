package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsuario, edtPassword;
    Button btnLogin, registroIrBtn;
    String usuario, password;
    TextView contrasenaOlvidada;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        edtUsuario = (EditText) findViewById(R.id.edtUsuario);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        contrasenaOlvidada = (TextView)  findViewById(R.id.contrasenaOlvidada);
        registroIrBtn = (Button) findViewById(R.id.registroIrBtn);

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        editor = preferences.edit();

        btnLogin.setOnClickListener(view -> {
            usuario = edtUsuario.getText().toString();
            password = edtPassword.getText().toString();
            if(usuario.trim().length()>0 && password.trim().length()>0) {
                obtenerId(MainActivity.RED+"buscar_usuario.php");
                validarUsuario(MainActivity.RED+"validar_usuario.php");
            } else {
                Toast.makeText(LoginActivity.this, "No se permiten campos vacios", Toast.LENGTH_LONG).show();
            }
        });

        contrasenaOlvidada.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecuperarClaveActivity.class);
            startActivity(intent);
        });

        registroIrBtn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RegistroActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private String cifrarClave() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validarUsuario(String URL) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            if(!response.isEmpty()) {
                guardarPreferencias();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else { //SI NO HAY RESULTADOS, ES QUE USUARIO Y CLAVE Y NO COINCIDEN POR  LO QUE ES INCORRECTO
                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("usuario", usuario);
                parametros.put("password", cifrarClave());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void obtenerId(String URL) {
        //OBTENEMOS LOS RESULTADOS DE LA CONSULTA GENERADA EN FORMATO JSON DESDE EL PHP Y SE CREA UN ARRAY
        //DESPUÉS EN CADA ITERACIÓN DEL BUCLE OBTENEMOS UN OBJETO DEL ARRAY

        //OBTENEMOS EL ID DEL USUARIO QUE INICIA SESION Y LO GUARDAMOS EN EL SHAREDPREFERENCES PARA USARLO EN LAS
        //ACCIONES QUE LO NECESITEMOS, COMO CREAR PUBLICACIONES O COMENTARIOS
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String usuarioU = object.getString("usuario").trim();
                    String idU = object.getString("id").trim();

                    if(usuario.equalsIgnoreCase(usuarioU)) {
                        editor.putString("id",idU);
                        editor.commit();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show());

        //PROCESAMOS LAS PETICIONES HECHAS PARA QUE LA LIBRERÍA Volley SE ENCARGUE DE EJECUTARLAS
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //ENVIAMOS LA SOLICITUD ENVIANDO EL OBJETO jsonArrayRequest DENTRO DEL REQUESTQUEUE
        requestQueue.add(jsonArrayRequest);
    }

    private void  guardarPreferencias() {
        //GUARDAMOS EL NOMBRE DE USUARIO E INDICAMOS QUE HAY SESION INICIADA PARA ASI CUANDO SE VUELVA
        //A INICIAR LA APP, NO VUELVA A PEDIR LOS DATOS
        editor.putString("usuario",usuario);
        editor.putBoolean("sesion",true);
        editor.commit();
    }

}