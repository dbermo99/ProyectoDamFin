package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class AdapterMiPub extends  RecyclerView.Adapter<AdapterMiPub.PublicacionHolder> {

    ArrayList<Publicacion> publicacionList;
    RequestQueue request;
    Context context;
    SharedPreferences preferences;

    public AdapterMiPub(ArrayList<Publicacion> publicacionList, Context context, SharedPreferences preferences) {
        this.publicacionList = publicacionList;
        this.context = context;
        request = Volley.newRequestQueue(context);
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public PublicacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mi_publicacion,null,false);
        return new PublicacionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionHolder holder, int position) {
        holder.asignarDatos(publicacionList.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return publicacionList.size();
    }

    //implementamos para poder hacer click en el menu de cada item, y para hacer click en cada opcion del menu
    public class PublicacionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView usuario,texto, cantidadMg, verComentariosMiPub;
        ImageView imagenPub;
        ImageButton mgButton, menuPub, editarPub, eliminarPub;
        public PublicacionHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.miUsuPub);
            texto = itemView.findViewById(R.id.miTextoPub);
            cantidadMg = itemView.findViewById(R.id.miCantidadMg);
            imagenPub = itemView.findViewById(R.id.miImagenPub);
            mgButton = itemView.findViewById(R.id.miMgButton);
            menuPub = itemView.findViewById(R.id.menuPub);
            menuPub.setOnClickListener(this);
            verComentariosMiPub = itemView.findViewById(R.id.verComentariosMiPub);
        }

        public void asignarDatos(Publicacion publicacion, PublicacionHolder holder) {
            usuario.setText(publicacion.getUsuario());
            texto.setText(publicacion.getTexto());

            String imagen = publicacion.getFoto();
            String url = MainActivity.RED+"imagenes/"+imagen;
            cargarImagen(url, holder);

            comprobarMg(MainActivity.RED+"buscar_mg.php", publicacion.getId());
            contarMg(MainActivity.RED+"contar_mg.php", publicacion.getId());

            contarComentarios(MainActivity.RED+"contar_comentarios.php", publicacion.getId());

            verComentariosMiPub.setOnClickListener(view -> {
                Intent intent = new Intent(context, VerComentariosActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("id",publicacion.getId()+"");

                intent.putExtras(bundle);

                context.startActivity(intent);
                //finish();
            });

        }

        private void cargarImagen(String url, PublicacionHolder holder) {
            url = url.replace(" ","%20");

            ImageRequest imageRequest = new ImageRequest(url,
                    response -> holder.imagenPub.setImageBitmap(response), 0, 0,
                    ImageView.ScaleType.CENTER, null, error -> {
                Toast.makeText(context, "ERROR AL CARGAR LA IMAGEN", Toast.LENGTH_SHORT).show();
            });
            request.add(imageRequest);
        }

        private void crearMg(String URL, int idPub) {
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
                mgButton.setImageResource(R.drawable.ic_corazonrojo);
                mgButton.setOnClickListener(view -> {
                    borrarMg(MainActivity.RED+"borrar_mg.php", idPub);
                });
                int mgsActuales = Integer.parseInt(cantidadMg.getText().toString());
                cantidadMg.setText((mgsActuales+1)+"");

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
                    mgButton.setImageResource(R.drawable.ic_corazonrojo);
                    //AL BOTON SE LE ASIGNA EL METODO DE ELIMINAR AMISTAD
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

        private void borrarPub(String URL, int idPub, String nombreFoto) {
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, response -> {
                Toast.makeText(context, "ELIMINACIÓN CORRECTA", Toast.LENGTH_SHORT).show();
            }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parametros=new HashMap<String,String>();
                    parametros.put("idPublicacion",idPub+"");
                    parametros.put("nombrePub",nombreFoto);
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

        private void contarComentarios(String URL, int idPub) {
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, array -> {

                for(int i=0; i<array.length();i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String idPubConsulta = object.getString("id_publicacion").trim();
                        String cantidadComentariosConsulta = object.getString("count(*)").trim();

                        if(idPubConsulta.equalsIgnoreCase(idPub+"")) {
                            verComentariosMiPub.setText("VER COMENTARIOS ("+cantidadComentariosConsulta+")");
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

        //metodo generado por el implements para hacer click en el menu de cada publicacion
        @Override
        public void onClick(View view) {
            mostrarMenuItem(view);
        }

        private void mostrarMenuItem(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.opciones_publicacion);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Publicacion p = publicacionList.get(getAbsoluteAdapterPosition());
            switch(menuItem.getItemId()) {
                case R.id.pub_editar:
                    Intent intent=new Intent(context, EditarPublicacionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id",p.getId()+"");
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    //finish();
                    return true;
                case R.id.pub_eliminar:
                    AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
                    myBuilder.setMessage("¿DESEA ELIMINAR LA PUBLICACIÓN?");
                    myBuilder.setTitle("BORRAR PUBLICACIÓN");
                    myBuilder.setPositiveButton("SI", (dialogInterface, i) -> {
                        borrarPub(MainActivity.RED+"borrar_publicacion.php", p.getId(), p.getFoto());
                        publicacionList.remove(getAbsoluteAdapterPosition());
                        notifyDataSetChanged();
                    });
                    myBuilder.setNegativeButton("NO", (dialog, i) -> dialog.cancel());

                    AlertDialog dialog = myBuilder.create();
                    dialog.show();
                    return true;
                default:
                    return false;
            }

        }
    }

}
