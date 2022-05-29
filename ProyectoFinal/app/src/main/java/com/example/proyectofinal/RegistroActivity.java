package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class RegistroActivity extends AppCompatActivity {

    EditText emailText, usuarioText, claveText, clave2Text, nombreText, apellidosText, diaText, mesText, annoText;
    CheckBox privadaCbx;
    Button registrarseBtn, iniciarSesionBtn, btnBuscarRegistro;
    String usuario, password;
    RequestQueue requestQueue;

    ImageView iv;

    String nombreFichero; //NOMBRE CON EL QUE SE SUBIRÁ LA IMAGEN

    Bitmap bitmap; //MAPA DE BITS DE UNA IMAGEN (PARA REPRESENTAR IMAGENES)
    int IMAGEN_GALERIA = 1; //PARA SABER QUE LA IMAGEN HA SIDO SELECCIONADA DESDE LA GALERÍA
    int IMAGEN_CAMARA = 101;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean imagenSeleccionada; //PARA SABER SI HAY UNA IMAGEN DE PERFIL SELECCIONADA, SI NO LA HAY NO SE PERMITE REGISTRARSE

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        diaText = (EditText) findViewById(R.id.diaText);
        mesText = (EditText) findViewById(R.id.mesText);
        annoText = (EditText) findViewById(R.id.annoText);
        privadaCbx = (CheckBox) findViewById(R.id.privadaCbx);
        registrarseBtn = (Button) findViewById(R.id.registrarseBtn);
        iniciarSesionBtn = (Button) findViewById(R.id.iniciarSesionBtn);

        btnBuscarRegistro = findViewById(R.id.btnBuscarRegistro);
        iv = findViewById(R.id.imagenRegistro);

        imagenSeleccionada = false;

        contarUsuarios(MainActivity.RED+"contar_usuarios.php");

        preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        editor = preferences.edit();

        btnBuscarRegistro.setOnClickListener(v -> mostrarOpciones());

        registrarseBtn.setOnClickListener(view -> {
            usuario = usuarioText.getText().toString();
            password = claveText.getText().toString();

            String email = emailText.getText().toString();
            String usuario2 = usuarioText.getText().toString();
            String clave = claveText.getText().toString();
            String clave2 = clave2Text.getText().toString();
            String nombre = nombreText.getText().toString();
            String apellidos = apellidosText.getText().toString();
            String dia = diaText.getText().toString();
            String mes = mesText.getText().toString();
            String anno = annoText.getText().toString();

            if(imagenSeleccionada) {
                    if(email.trim().length()>0 && usuario2.trim().length()>0 && clave.trim().length()>0 && clave2.trim().length()>0
                            && nombre.trim().length()>0 && apellidos.trim().length()>0 && dia.trim().length()>0
                            && mes.trim().length()>0 && anno.trim().length()>0) {
                        if(validarEdad()) {
                            if(validarEmail(email)) {
                                if(clave.equals(clave2)) {
                                    comprobarUsuario(MainActivity.RED + "buscar_usuario.php");
                                } else {
                                    Toast.makeText(getApplicationContext(), "LAS CONTRASEÑAS NO CINCIDEN", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "EMAIL NO VÁLIDO", Toast.LENGTH_SHORT).show();
                            }
                        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarFecha() {
        boolean correcto = true;
        int dia = Integer.parseInt(diaText.getText().toString());
        int mes = 0;
        if(mesText.getText().toString().length()==1) {
            mes = Integer.parseInt("0"+mesText.getText().toString());
        } else {
            mes = Integer.parseInt(mesText.getText().toString());
        }
        int anno = Integer.parseInt(annoText.getText().toString());
        try {
            LocalDate today = LocalDate.of(anno, mes, dia); //comprpamos si es una fecha correcta
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "iNTRODUCE UNA FECHA VÁLIDA", Toast.LENGTH_SHORT).show();
            correcto = false;
        }
        return correcto;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean validarEdad() {
        //validamos si es mayor de edad
        boolean correcto = false;
        if(this.validarFecha()) {
            int dia = Integer.parseInt(diaText.getText().toString());
            int mes = 0;
            if(mesText.getText().toString().length()==1) {
                mes = Integer.parseInt("0"+mesText.getText().toString());
            } else {
                mes = Integer.parseInt(mesText.getText().toString());
            }
            int anno = Integer.parseInt(annoText.getText().toString());

            LocalDate fHoy= LocalDate.now();
            LocalDate cumple= LocalDate.of(anno, mes, dia);
            long edad= ChronoUnit.YEARS.between(cumple, fHoy);
            if(edad >= 18) {
                correcto = true;
            } else {
                Toast.makeText(getApplicationContext(), "PARA PODER REGISTRATE DEBES SER MAYOR DE EDAD", Toast.LENGTH_SHORT).show();
            }
        }
        return correcto;
    }

    private boolean validarEmail(String email) {
        boolean valido = false;
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);
        if (mather.find() == true) {
            valido = true;
        }
        return valido;
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
                    if(nombreU.equalsIgnoreCase(usuarioText.getText().toString()) ||
                            emailU.equalsIgnoreCase(emailText.getText().toString())) {
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

    private void mostrarOpciones() {
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setMessage("ELIJA UNA OPCIÓN");
        myBuilder.setTitle("SELECCIONAR IMAGEN");
        myBuilder.setPositiveButton("DESDE GALERÍA", (dialogInterface, i) -> showFileChooser());
        myBuilder.setNegativeButton("DESDE CÁMARA", (dialog, i) -> abrirCamara());
        //myBuilder.setNeutralButton("OTRA OPCIÓN", (dialog, i) -> abrirCamara());//--SI HUBIERA MÁS OPCIONES SE PONE setNeutralButton

        AlertDialog dialog = myBuilder.create();
        dialog.show();
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGEN_CAMARA);
        //LA LINEA ANTERIOR LLAMA AL onActivityResult
        //IMAGEN_CAMARA ES EL CODIGO QUE ENVIAMOS PARA SABER QUE LA ACCIÓN QUE QUEREMOS HACER ES ABRIR LA CÁMARA
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
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), IMAGEN_GALERIA);
        //LA LINEA ANTERIOR LLAMA AL onActivityResult
        imagenSeleccionada = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA GALERIA, HA SIDO CORRECTO Y LOS DATOS NO SON NULOS
        if (requestCode == IMAGEN_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //OBTENEMOS EL BITMAP DE LA IMAGEN SELECCIONADA
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA CÁMARA Y HA SIDO CORRECTO
        if(requestCode == IMAGEN_CAMARA && resultCode == RESULT_OK) {
            //CREAMOS UN BITMAP CON LA IMAGEN TOMADA DE LA CÁMARA
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        //ENVIAMOS LA IMAGEN AL IMAGEVIEW
        iv.setImageBitmap(bitmap);
        //INDICAMOS QUE HEMOS SELECCIONADO UNA IMAGEN PARA PODER CREAR LA PUBLICACIÓN
        imagenSeleccionada = true;
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