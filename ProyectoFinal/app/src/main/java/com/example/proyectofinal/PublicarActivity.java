package com.example.proyectofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PublicarActivity extends AppCompatActivity {

    EditText publicacionTexto;
    Button publicarBtn, btnBuscar;

    ImageView iv;

    String nombreFichero; //NOMBRE CON EL QUE SE SUBIRÁ LA IMAGEN

    Bitmap bitmap;
    int IMAGEN_GALERIA = 1;
    int IMAGEN_CAMARA = 101;

    boolean imagenSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);
        getSupportActionBar().hide();

        publicacionTexto = (EditText) findViewById(R.id.publicacionTexto);
        publicarBtn = (Button) findViewById(R.id.publicarBtn);

        btnBuscar = findViewById(R.id.btnBuscar);
        iv = findViewById(R.id.imageView);

        imagenSeleccionada = false;

        //CON ESTE MÉTODO OBTENEMOS EL ID DE LA PUBLICACIÓN QUE SE VA A CREAR
        //PARA INDICARLO EN EL NOMBRE DE LA FOTO
        contarPublicaciones(MainActivity.RED+"contar_publicaciones.php");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bn4);
        bottomNavigationView.setSelectedItemId(R.id.nav_publicar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent intentHome = new Intent(PublicarActivity.this, MainActivity.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                case R.id.nav_buscar:
                    Intent intentBuscar = new Intent(PublicarActivity.this, BuscarActivity.class);
                    startActivity(intentBuscar);
                    finish();
                    return true;
                case R.id.nav_perfil:
                    Intent intentPerfil = new Intent(PublicarActivity.this, MiPerfilActivity.class);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                case R.id.nav_publicar:
                    Intent intentPublicar = new Intent(PublicarActivity.this, PublicarActivity.class);
                    startActivity(intentPublicar);
                    finish();
                    return true;
                case R.id.nav_notificaciones:
                    Intent intentNotificaciones = new Intent(PublicarActivity.this, NotificacionActivity.class);
                    startActivity(intentNotificaciones);
                    finish();
                    return true;
                default: return true;
            }
        });

        btnBuscar.setOnClickListener(view -> {
            mostrarOpciones();
        });

        publicarBtn.setOnClickListener(view -> {
            if(imagenSeleccionada) {
                if(publicacionTexto.getText().toString().trim().length()>0)
                    publicar(MainActivity.RED+"crearPublicacion.php");
                else
                    Toast.makeText(getApplicationContext(), "INTRODUCE ALGÚN TEXTO", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "SELECCIONE ALGUNA IMAGEN", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void publicar(String URL) {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
            loading.dismiss();
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "PUBLICADO CORRECTAMENTE", Toast.LENGTH_SHORT).show();
        }, error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                String imagen = getStringImagen(bitmap);

                Map<String,String> parametros=new HashMap<String,String>();
                parametros.put("id_usuario",preferences.getString("id", "-1"));
                parametros.put("texto",publicacionTexto.getText().toString());
                parametros.put("foto", imagen); //IMAGEN CODIFICADA
                parametros.put("nombre", nombreFichero); //NOMBRE IMAGEN

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void contarPublicaciones(String URL) {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
            for(int i=0; i<array.length();i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    String proximo = object.getString("AUTO_INCREMENT").trim();

                    nombreFichero = "publicacion"+proximo;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> { //SI NO HAY REGISTOS EN LA TABLA, ENTRARÁ EN LOS FALLOS
            //Toast.makeText(getApplicationContext(), "NO HAY PUBLICACIONES", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), IMAGEN_GALERIA);
        //LA LINEA ANTERIOR LLAMA AL onActivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA GALERIA, HA SIDO CORRECTO Y LOS DATOS NO SON NULOS
        if (requestCode == IMAGEN_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //CREAMOS UN BITMAP CON LA IMAGEN SELECCIONADA DE LA GALERÍA
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

    /*
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA GALERIA, HA SIDO CORRECTO Y LOS DATOS NO SON NULOS
        if (requestCode == IMAGEN_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //CREAMOS UN BITMAP CON LA IMAGEN SELECCIONADA DE LA GALERÍA
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //ENVIAMOS LA IMAGEN AL IMAGEVIEW
                iv.setImageBitmap(bitmap);
                //INDICAMOS QUE HEMOS SELECCIONADO UNA IMAGEN PARA PODER CREAR LA PUBLICACIÓN
                imagenSeleccionada = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //COMPROBAMOS SI LA IMAGEN LA HEMOS OBTENIDO DESDE LA CÁMARA Y HA SIDO CORRECTO
        if(requestCode == IMAGEN_CAMARA && resultCode == RESULT_OK) {
            //CREAMOS UN BITMAP CON LA IMAGEN TOMADA DE LA CÁMARA
            bitmap = (Bitmap) data.getExtras().get("data");
            //ENVIAMOS LA IMAGEN AL IMAGEVIEW
            iv.setImageBitmap(bitmap);
            //INDICAMOS QUE HEMOS SELECCIONADO UNA IMAGEN PARA PODER CREAR LA PUBLICACIÓN
            imagenSeleccionada = true;
        }

    }*/

}