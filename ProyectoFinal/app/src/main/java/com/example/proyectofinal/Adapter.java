package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter extends  RecyclerView.Adapter<Adapter.PublicacionHolder> {

    ArrayList<Publicacion> publicacionList;
    RequestQueue request; //SOPORTE PARA EL VOLLEY, PARA PODER CONVERTIR LA IMAGEN
    Context context; //PARA DAR SOPORTE AL REQUEST
    SharedPreferences preferences;

    public Adapter(ArrayList<Publicacion> publicacionList, Context context, SharedPreferences preferences) {
        this.publicacionList = publicacionList;
        this.context = context;
        request = Volley.newRequestQueue(context);
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public PublicacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ENLAZAMOS EL ADAPTADOR CON EL CARD_LAYOUT
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,null,false);
        return new PublicacionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionHolder holder, int position) {
        //ASIGNAMOS LOS DATOS A CADA PUBLICACIÓN, PASAMOS EL HOLDER PARA PODER MOSTRAR
        //LA IMAGEN DE LA PUBLICACIÓN
        holder.asignarDatos(publicacionList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return publicacionList.size();
    }

    //CREAMOS LA CLASE PublicacionHolder Y EXTENDEMOS DE RecyclerView.ViewHolder
    public class PublicacionHolder extends RecyclerView.ViewHolder {
        TextView usuario,texto, cantidadMg, verComentariosPub;
        ImageView imagenPub;
        ImageButton mgButton, comentarBtn;
        String idUsuario;
        //CREAMOS EL CONSTRUCTOR QUE NOS OBLIGA, PARA ASIGANR LAS REFERENCIAS A LOS CAMPOS DE CADA PUBLICACIÓN (card_layout)
        public PublicacionHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.usuPub);
            texto = itemView.findViewById(R.id.textoPub);
            cantidadMg = itemView.findViewById(R.id.cantidadMg);
            verComentariosPub = itemView.findViewById(R.id.verComentariosPub);
            imagenPub = itemView.findViewById(R.id.imagenPub);
            mgButton = itemView.findViewById(R.id.mgButton);
            comentarBtn = itemView.findViewById(R.id.comentarBtn);
        }

        public void asignarDatos(Publicacion publicacion, PublicacionHolder holder) {
            usuario.setText(publicacion.getUsuario());
            texto.setText(publicacion.getTexto());

            String imagen = publicacion.getFoto(); //OBTENEMOS EL NOMBRE DE LA FOTO
            String url = MainActivity.RED+"imagenes/"+imagen; //CREAMOS LA URL DE LA IMAGEN
            cargarImagen(url, holder); //SE CARGA LA IMAGEN

            comprobarMg(MainActivity.RED+"buscar_mg.php", publicacion.getId());
            contarMg(MainActivity.RED+"contar_mg.php", publicacion.getId());
            contarComentarios(MainActivity.RED+"contar_comentarios.php", publicacion.getId());
            obtenerIdUsuario(MainActivity.RED+"buscar_publicacion.php", publicacion.getId());

            usuario.setOnClickListener(view -> { //AL PINCHAR EN EL NOMBRE DEL USUARIO VAMOS A SU PERFIL
                comprobarAmistad(MainActivity.RED+"buscar_amistad.php",publicacion.getUsuario());
            });

            verComentariosPub.setOnClickListener(view -> { //VEMOS TODOS LOS COMENTARIOS DE ESA PUBLICACIÓN
                Intent intent = new Intent(context, VerComentariosActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",publicacion.getId()+"");
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            comentarBtn.setOnClickListener(view -> { //VAMOS AL ACTIVITY PARA PODER COMENTAR
                Intent intent = new Intent(context, ComentarioActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",publicacion.getId()+"");
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

        }


        private void comprobarAmistad(String URL, String usuarioPub) {
            //ESTE MÉTODO SE UTILIZA PARA SABER SI TIENES AMISTAD O NO CON EL QUE HA CREADO LA PUBLICACIÓN,
            //SI LA TIENES, AL PINCHAR SOBRE SU NOMBRE VAS A SU PERFIL Y LO PUEDES VER ENTERO,
            //SI NO LA TIENES, COMPROBARÁ SI EL PERFIL ES PRIVADO
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
                    Intent intent = new Intent(context, PerfilActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id",idUsuario);
                    bundle.putString("usuario",usuarioPub);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    comprobarPrivacidad(MainActivity.RED+"buscar_usuario.php",usuarioPub, idUsuario);
                }
            }, error -> {
                //Toast.makeText(context, "NO HAY AMISTADES", Toast.LENGTH_SHORT).show();
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                    Intent intent = new Intent(context, PerfilBloqueadoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id",idUsuario);
                    bundle.putString("usuario",usuarioPub);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();
                    //context.finish();
                } else {
                    Intent intent = new Intent(context, PerfilActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id",idUsuario);
                    bundle.putString("usuario",usuarioPub);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();
                    //context.finish();
                }
            }, error -> {
                //Toast.makeText(BuscarActivity.this, "NO EXISTEN USUARIOS", Toast.LENGTH_SHORT).show();
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonArrayRequest);
        }

        private void cargarImagen(String url, PublicacionHolder holder) {
            url = url.replace(" ","%20");
            //CREAMOS UN IMAGEREQUEST CON LA URL DE LA IMAGEN
            ImageRequest imageRequest = new ImageRequest(url,
                    //AL IMAGEVIEW DE LA PUBLICACIÓN LE ENVIAMOS LA RESPUESTA, QUE ES EL BITMAP DE LA IMAGEN
                    response -> holder.imagenPub.setImageBitmap(response), 0, 0,
                    ImageView.ScaleType.CENTER, null, error -> {
                //SI HAY ALGÚN ERROR AL CARGAR LA IMAGEN, LO INDICAMOS
                Toast.makeText(context, "ERROR AL CARGAR LA IMAGEN", Toast.LENGTH_SHORT).show();
            });
            request.add(imageRequest); //AÑADIMOS EL IMAGEREQUEST AL REAQUEST
        }

        private void crearMg(String URL, int idPub) {
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
                mgButton.setImageResource(R.drawable.ic_corazonrojo); //cambiar a rojo
                mgButton.setOnClickListener(view -> { //CAMBIAMOS LA ACCIÓN DEL BOTÓNB, PARA QUE LA PRÓXIMA VEZ SEA PARA QUITAR EL MG
                    borrarMg(MainActivity.RED+"borrar_mg.php", idPub);
                });
                int mgsActuales = Integer.parseInt(cantidadMg.getText().toString()); //OBTENEMOS LOS MG QUE TIENE LA FOTO
                cantidadMg.setText((mgsActuales+1)+""); //SUMAMOS 1 Y LO ACTUALIZAMOS

            }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parametros=new HashMap<String,String>();
                    parametros.put("idPublicacion",idPub+"");
                    parametros.put("idUsuario",preferences.getString("id", "-1"));
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

        private void borrarMg(String URL, int idPub) {
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
                mgButton.setImageResource(R.drawable.ic_corazonblanco);
                mgButton.setOnClickListener(view -> {
                    crearMg(MainActivity.RED+"crear_mg.php", idPub);
                });
                int mgsActuales = Integer.parseInt(cantidadMg.getText().toString());
                cantidadMg.setText((mgsActuales-1)+"");

            }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parametros=new HashMap<String,String>();
                    parametros.put("idPublicacion",idPub+"");
                    parametros.put("idUsuario",preferences.getString("id", "-1"));
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

        private void comprobarMg(String URL, int idPub) {
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
                boolean existe = false;
                for(int i=0; i<array.length();i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String idPubConsulta = object.getString("id_publicacion").trim();
                        String idUsuarioConsulta = object.getString("id_usuario").trim();

                        if(idPubConsulta.equalsIgnoreCase(idPub+"") &&
                                idUsuarioConsulta.equalsIgnoreCase(preferences.getString("id", "-1"))) {
                            existe = true;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(existe) {
                    //SI HAY MG SE COLOCA EL ICONO DE MG PULSADO
                    mgButton.setImageResource(R.drawable.ic_corazonrojo); //cambiar
                    //AL BOTON SE LE ASIGNA EL METODO DE ELIMINAR MG
                    mgButton.setOnClickListener(view -> {
                        borrarMg(MainActivity.RED+"borrar_mg.php", idPub);
                    });
                } else {
                    //SI NO HAY MG SE COLOCA EL ICONO DE MG NO PULSADO
                    mgButton.setImageResource(R.drawable.ic_corazonblanco);
                    //SI NO HAY MG SE LE ASIGNA EL METODO CREAR MG
                    mgButton.setOnClickListener(view -> {
                        crearMg(MainActivity.RED+"crear_mg.php", idPub);
                    });
                }
            }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
                //Toast.makeText(context, "NO EXISTEN MGs", Toast.LENGTH_SHORT).show();
                //SI NO HAY MG SE COLOCA EL ICONO DE MG NO PULSADO
                mgButton.setImageResource(R.drawable.ic_corazonblanco);
                //SI NO HAY MG SE LE ASIGNA EL METODO CREAR MG
                mgButton.setOnClickListener(view -> {
                    crearMg(MainActivity.RED+"crear_mg.php", idPub);
                });
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonArrayRequest);
        }

        private void contarMg(String URL, int idPub) {
            //CONTAMOS LOS MG's QUE TIENE LA PUBLICACIÓN Y LO ASIGNAMOS A LA PUBLICACIÓN
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
                for(int i=0; i<array.length();i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String idPubConsulta = object.getString("id_publicacion").trim();
                        String cantidadMgConsulta = object.getString("count(*)").trim();

                        if(idPubConsulta.equalsIgnoreCase(idPub+"")) {
                            cantidadMg.setText(cantidadMgConsulta);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
                //Toast.makeText(context, "NO EXISTEN MGs", Toast.LENGTH_SHORT).show();
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonArrayRequest);
        }

        private void contarComentarios(String URL, int idPub) {
            //CONTAMOS CUÁNTOS COMENTARIOS TIENE LA PUBLICACIÓN
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {

                for(int i=0; i<array.length();i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String idPubConsulta = object.getString("id_publicacion").trim();
                        String cantidadComentariosConsulta = object.getString("count(*)").trim();

                        if(idPubConsulta.equalsIgnoreCase(idPub+"")) {
                            verComentariosPub.setText("VER COMENTARIOS ("+cantidadComentariosConsulta+")");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
                //Toast.makeText(context, "NO EXISTEN COMENTARIOS", Toast.LENGTH_SHORT).show();
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonArrayRequest);
        }

        private void obtenerIdUsuario(String URL, int idPub) {
            //OBTENEMOS EL ID DEL USUARIO PARA ASÍ PODER ENTRAR A SU PERFIL PINCHANDO EN SU NOMBRE
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {
                for(int i=0; i<array.length();i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String idPubConsulta = object.getString("id").trim();
                        String idUsuConsulta = object.getString("id_usuario").trim();

                        if(idPubConsulta.equalsIgnoreCase(idPub+"")) {
                            //Toast.makeText(context, idUsuConsulta, Toast.LENGTH_SHORT).show();
                            idUsuario = idUsuConsulta;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, error -> { //SI NO HAY DATOS EN UNA TABLA DE LA BBDD, ENTRA EN LOS ERRORES
                //Toast.makeText(context, "NO EXISTEN PUBLICACIONES", Toast.LENGTH_SHORT).show();
            });

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonArrayRequest);
        }

    }

}
