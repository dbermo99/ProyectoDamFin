package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText emailText, usuarioText, claveText, clave2Text, nombreText, apellidosText;
    CheckBox privadaCbx;
    Button registrarseBtn, iniciarSesionBtn, btnBuscarRegistro;
    String usuario, password;
    RequestQueue requestQueue;

    ImageView iv;

    String nombreFichero; //NOMBRE CON EL QUE SE SUBIRÁ LA IMAGEN

    Bitmap bitmap; //MAPA DE BITS DE UNA IMAGEN (PARA REPRESENTAR IMAGENES)
    int PICK_IMAGE_REQUEST = 1; //PARA SABER QUE LA IMAGEN HA SIDO SELECCIONADA DESDE LA GALERÍA

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean imagenSeleccionada; //PARA SABER SI HAY UNA IMAGEN DE PERFIL SELECCIONADA, SI NO LA HAY NO SE PERMITE REGISTRARSE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        emailText = (EditText) findViewById(R.id.emailText);
        usuarioText = (EditText) findViewById(R.id.usuarioText);
        claveText = (EditText) findViewById(R.id.claveText);
        clave2Text = (EditText) findViewById(R.id.clave2Text);
        nombreText = (EditText) findViewById(R.id.nombreText);
        apellidosText = (EditText) findViewById(R.id.apellidosText);
        privadaCbx = (CheckBox) findViewById(R.id.privadaCbx);
        registrarseBtn = (Button) findViewById(R.id.registrarseBtn);
        iniciarSesionBtn = (Button) findViewById(R.id.iniciarSesionBtn);

        btnBuscarRegistro = findViewById(R.id.btnBuscarRegistro);
        iv = findViewById(R.id.imagenRegistro);

        imagenSeleccionada = false;

        contarUsuarios(MainActivity.RED+"contar_usuarios.php");

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        editor = preferences.edit();

        btnBuscarRegistro.setOnClickListener(v -> showFileChooser());

        registrarseBtn.setOnClickListener(view -> {
            usuario = usuarioText.getText().toString();
            password = claveText.getText().toString();

            String email = emailText.getText().toString();
            String usuario2 = usuarioText.getText().toString();
            String clave = claveText.getText().toString();
            String clave2 = clave2Text.getText().toString();
            String nombre = nombreText.getText().toString();
            String apellidos = apellidosText.getText().toString();

            if(imagenSeleccionada) {
                if(email.trim().length()>0 && usuario2.trim().length()>0 && clave.trim().length()>0 && clave2.trim().length()>0
                        && nombre.trim().length()>0 && apellidos.trim().length()>0) {
                    if(clave.equals(clave2))
                        comprobarUsuario(MainActivity.RED+"buscar_usuario.php");
                    else
                        Toast.makeText(getApplicationContext(), "LAS CONTRASEÑAS NO CINCIDEN", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "RELLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "SELECCIONE ALGUNA IMAGEN", Toast.LENGTH_SHORT).show();
            }

        });

        iniciarSesionBtn.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void contarUsuarios(String URL) {
        //OBTENEMOS LOS RESULTADOS DE LA CONSULTA GENERADA EN FORMATO JSON DESDE EL PHP Y SE CREA UN ARRAY
        //DESPUÉS EN CADA ITERACIÓN DEL BUCLE OBTENEMOS UN OBJETO DEL ARRAY

        //CON ESTE MÉTODO SE OBTIENE EL ID DEL PRÓXIMO USUARIO QUE SE VA A REGISTRAR Y SE AÑADE EL ID AL
        //NOMBRE DE LA IMAGEN DEL USUARIO
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String proximo = object.getString("AUTO_INCREMENT").trim();

                    nombreFichero = "usuario"+proximo;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            //Toast.makeText(context, "NO HAY USUARIOS QUE CONTAR", Toast.LENGTH_SHORT).show();
        });

        //PROCESAMOS LAS PETICIONES HECHAS PARA QUE LA LIBRERÍA Volley SE ENCARGUE DE EJECUTARLAS
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //ENVIAMOS LA SOLICITUD ENVIANDO EL OBJETO jsonArrayRequest DENTRO DEL REQUESTQUEUE
        requestQueue.add(jsonArrayRequest);
    }

    private void comprobarUsuario(String URL) {
        //CON ESTE MÉTO COMPROBAMOS SI EL USUARIO QUE SE INTENTA REGISTRAR YA EXISTE EN LA BASE DE DATOS
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
                    //SI EXISTE LO INDICAMOS
                    if(nombreU.equalsIgnoreCase(usuarioText.getText().toString()) || emailU.equalsIgnoreCase(emailText.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "EMAIL O USUARIO YA REGISTRADO", Toast.LENGTH_SHORT).show();
                        existe = true;
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            //SI NO EXISTE SE REGISTRA
            if(!existe) {
                registrar(MainActivity.RED+"registrar.php");
            }
        }, error -> {
            //Toast.makeText(context, "NO HAY USUARIOS QUE CONTAR", Toast.LENGTH_SHORT).show();
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private String cifrarClave() {
        String clave = claveText.getText().toString();
        char array[] = clave.toCharArray(); //convertimos la clave a un array de char
        for(int i = 0; i<array.length; i++) {
            array[i] = (char)(array[i] + (char)5); //a cada letra la convertimos en la letra que está en 5 puestos a la derecha
        }
        String encriptado = String.valueOf(array); //convertimos el array en string
        return encriptado;
    }

    private void registrar(String URL) {
        final ProgressDialog loading = ProgressDialog.show(this, "REGISTRANDO...", "Espere por favor");

        //CON EL STRINGREQUES DECLARAMOS UNA PETICION, IDICAMOS EL TIPO DE LLAMADA, EN ESTE CASO POST
        //LUEGO SE INDICA LA URL A LA QUE SE HARÁ LA PETICIÓN
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            //SI HA IDO BIEN ENTRARÁ AQUÍ
            loading.dismiss();
            Toast.makeText(getApplicationContext(), "REGISTRADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            iniciarSesion();
        }, error -> { //SI HA HABIDO ALGÚN FALLO ENTRARÁ AQUÍ
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //POR AQUÍ ENVIAMOS LOS DATOS AL PHP
                String imagen = getStringImagen(bitmap);
                String privada = "0";
                if(privadaCbx.isChecked())
                    privada = "1";

                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("usuario",usuarioText.getText().toString());
                parametros.put("password",cifrarClave());
                parametros.put("nombre",nombreText.getText().toString());
                parametros.put("apellidos",apellidosText.getText().toString());
                parametros.put("mail",emailText.getText().toString());
                parametros.put("privada",privada);
                parametros.put("foto", imagen);
                parametros.put("nombreFoto", nombreFichero);
                return parametros;
            }
        };

        //PROCESAMOS LAS PETICIONES HECHAS PARA QUE LA LIBRERÍA SE ENCARGUE DE EJECUTARLAS
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //ENVIAMOS LA SOLICITUD CON EL OBJETO STRINGREQUEST COMO PARÁMETRO
        requestQueue.add(stringRequest);
    }

    public String getStringImagen(Bitmap bmp) {
        //CONVIERTE LA IMAGEN EN UN STRING PARA QUE PUEDA SER ENVIADA AL SERVIDOR
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        //MOSTRAMOS LOS ARCHIVOS DE LA GALERÍA
        Intent intent = new Intent();
        intent.setType("image/*"); //MUESTRA LAS IMÁGENES DE LA GALERÍA
        intent.setAction(Intent.ACTION_GET_CONTENT); //PERMITE SELECCIONAR LA IMAGEN
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
        //EL MÉTODO ANTERIOR LLAMA AL onActivityResult
        imagenSeleccionada = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA GALERIA, HA SIDO CORRECTO Y LOS DATOS NO SON NULOS
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //OBTENEMOS EL BITMAP DE LA IMAGEN SELECCIONADA
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //ENVIAMOS LA IMAGEN AL IMAGEVIEW
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void obtenerId(String URL) {
        //OBTENEMOS EL ID DEL USUARIO QUE INICIA SESION Y LO GUARDAMOS EN EL SHAREDPREFERENCES PARA USARLO EN LAS
        //ACCIONES QUE LO NECESITEMOS, COMO CREAR PUBLICACIONES O COMENTARIOS
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            boolean existe = false;
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String usuarioU = object.getString("usuario").trim();
                    String idU = object.getString("id").trim();

                    if(usuario.equalsIgnoreCase(usuarioU)) {
                        existe = true;
                        editor.putString("id",idU);
                        editor.commit();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!existe)
                Toast.makeText(RegistroActivity.this, "NO EXISTE ESE USUARIO", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(RegistroActivity.this, "EXISTE", Toast.LENGTH_SHORT).show();
        }, error -> {
            //Toast.makeText(context, "NO HAY USUARIOS", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void  guardarPreferencias() {
        //GUARDAMOS EL NOMBRE DE USUARIO E INDICAMOS QUE HAY SESION INICIADA PARA ASI CUANDO SE VUELVA
        //A INICIAR LA APP, NO VUELVA A PEDIR LOS DATOS
        editor.putString("usuario",usuarioText.getText().toString());
        editor.putBoolean("sesion",true);
        editor.commit();
    }

    private void iniciarSesion() {
        obtenerId(MainActivity.RED+"buscar_usuario.php");
        guardarPreferencias();
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}