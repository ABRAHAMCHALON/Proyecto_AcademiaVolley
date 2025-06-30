package com.example.proyecto_academiavolley.ui.alumno;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.proyecto_academiavolley.Login;
import com.example.proyecto_academiavolley.R;
import com.example.proyecto_academiavolley.databinding.FragmentAlumnoBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AlumnoFragment extends Fragment implements View.OnClickListener{
    ListView lista;
    Button boton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla el layout de tu fragmento
        View rootView = inflater.inflate(R.layout.fragment_alumno, container, false);

        lista = (ListView) rootView.findViewById(R.id.listViewAlumnos);
        boton = (Button) rootView.findViewById(R.id.btnRegistrar);
        boton.setOnClickListener(this);

        ListarAlumnos();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.busqueda_alumno, menu);

        MenuItem searchItem = menu.findItem(R.id.action_buscar_dni);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar por DNI");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarAlumnoPorDni(query); // Aquí llamas al método que consulta por DNI
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // puedes implementar búsqueda en vivo si deseas
            }

            //------------------------------
            private void buscarAlumnoPorDni(String dni) {

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("dni", dni);

                client.post(Login.servidor + "alumno_buscar.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        List<Alumno> alumnosEncontrados = new ArrayList<>();

                        try {
                            String response = new String(responseBody, "UTF-8");
                            JSONArray jsonArray = new JSONArray(response);


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                Alumno alumno = new Alumno(
                                        obj.getString("id"),
                                        obj.getString("nombres"),
                                        obj.getString("apellidos"),
                                        obj.getString("ndc"),
                                        obj.getString("edad"),
                                        obj.getString("fnacimiento"),
                                        obj.getString("grupo"),
                                        obj.getString("horario"),
                                        obj.getString("nombresApoderado"),
                                        obj.getString("apellidosApoderado"),
                                        obj.getString("cel"),
                                        obj.getString("dir")
                                );

                                alumnosEncontrados.add(alumno);
                            }

                            actualizarListaAlumnos(alumnosEncontrados);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        }

                        if (alumnosEncontrados.size() > 0) {
                            Alumno alumno = alumnosEncontrados.get(0);
                            mostrarDialogoAlumno(alumno);
                        } else {
                            Toast.makeText(getContext(), "No se encontró ningún alumno", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void mostrarDialogoAlumno(Alumno alumno) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Datos del Alumno");

                        String mensaje = "Nombre: " + alumno.nombres + " " + alumno.apellidos + "\n"
                                + "DNI: " + alumno.ndc + "\n"
                                + "Edad: " + alumno.edad + "\n"
                                + "Nacimiento: " + alumno.fnacimiento + "\n"
                                + "Grupo: " + alumno.grupo + "\n"
                                + "Horario: " + alumno.horario + "\n"
                                + "Apoderado: " + alumno.nombresApoderado + " " + alumno.apellidosApoderado + "\n"
                                + "Celular: " + alumno.cel + "\n"
                                + "Dirección: " + alumno.dir;

                        builder.setMessage(mensaje);

                        builder.setPositiveButton("Cerrar", null);
                        builder.show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //------------------------------

            private void actualizarListaAlumnos(List<Alumno> listaFiltrada) {
                AlumnoAdapter adapter = new AlumnoAdapter(getActivity(), listaFiltrada);
                lista.setAdapter(adapter);
            }


        });


    }

    public class AlumnoAdapter extends BaseAdapter {

        private Context context;
        private final List<Alumno> alumnoList;

        public AlumnoAdapter(Context context, List<Alumno> contactList) {
            this.context = context;
            this.alumnoList = contactList;
        }

        @Override
        public int getCount() {
            return alumnoList.size();
        }

        @Override
        public Object getItem(int position) {
            return alumnoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_alumno, null);
            }

            // Obtener los elementos de la vista
            TextView id = convertView.findViewById(R.id.tvId);
            TextView nombres = convertView.findViewById(R.id.tvNombres);
            TextView apellidos = convertView.findViewById(R.id.tvApellidos);
            TextView ndc = convertView.findViewById(R.id.tvDocumento);
            TextView edad = convertView.findViewById(R.id.tvEdad);
            TextView fnacimiento = convertView.findViewById(R.id.tvFechaNacimiento);
            TextView grupo = convertView.findViewById(R.id.tvGrupo);
            TextView horario = convertView.findViewById(R.id.tvHorario);
            TextView nombresApoderado = convertView.findViewById(R.id.tvNombreApoderado);
            TextView apellidosApoderado = convertView.findViewById(R.id.tvApellidosApoderado);
            TextView cel = convertView.findViewById(R.id.tvCelular);
            TextView dir = convertView.findViewById(R.id.tvDireccion);

            Button editar = convertView.findViewById(R.id.btnEditar);
            Button eliminar = convertView.findViewById(R.id.btnEliminar);

            // Obtener el contacto
            Alumno alumno = alumnoList.get(position);

            // Asignar los valores
            id.setText(alumno.id);
            nombres.setText("Nombres: "+alumno.nombres);
            apellidos.setText("Apellidos: "+alumno.apellidos);
            ndc.setText("DNI: "+alumno.ndc);
            edad.setText("Edad: "+alumno.edad);
            fnacimiento.setText("Fecha de nacimiento: "+alumno.fnacimiento);
            grupo.setText("Grupo: "+alumno.grupo);
            horario.setText("Horario: "+alumno.horario);
            nombresApoderado.setText("Nombre: "+alumno.nombresApoderado);
            apellidosApoderado.setText("Apellidos: "+alumno.apellidosApoderado);
            cel.setText("Celular: "+alumno.cel);
            dir.setText("Dirección: "+alumno.dir);

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),"Editar "+producto.id,Toast.LENGTH_SHORT).show();
                    EditarAlumno(alumno.id);
                }
            });

            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),"Eliminar "+producto.id,Toast.LENGTH_SHORT).show();
                    EliminarAlumno(alumno.id);
                }
            });


            return convertView;
        }
    }

    private void ListarAlumnos() {

        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_mostrar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                //Toast.makeText(getApplicationContext(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);
                    List<Alumno> productos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id_alumno = jsonObject.getString("id_alumno");
                        String nom_alumno = jsonObject.getString("nom_alumno");
                        String ape_alumno = jsonObject.getString("ape_alumno");
                        String ndc_alumno = jsonObject.getString("ndc_alumno");
                        String edad_alumno = jsonObject.getString("edad_alumno");
                        String fnacimiento_alumno = jsonObject.getString("fnacimiento_alumno");
                        String nom_grupo = jsonObject.getString("nom_grupo");
                        String dias = jsonObject.getString("dias_horario");
                        String horaInicio = jsonObject.getString("hinicio_horario");
                        String horaFin = jsonObject.getString("hfin_horario");
                        String dias_horario = dias + " " + horaInicio + " - " + horaFin;
                        String nom_apoderado = jsonObject.getString("nom_apoderado");
                        String ape_apoderado = jsonObject.getString("ape_apoderado");
                        String cel_apoderado = jsonObject.getString("cel_apoderado");
                        String dir_apoderado = jsonObject.getString("dir_apoderado");

                        // Crear un objeto Producto y agregarlo a la lista
                        productos.add(new Alumno(id_alumno,
                                nom_alumno,
                                ape_alumno,
                                ndc_alumno,
                                edad_alumno,
                                fnacimiento_alumno,
                                nom_grupo,
                                dias_horario,
                                nom_apoderado,
                                ape_apoderado,
                                cel_apoderado,
                                dir_apoderado
                        ));
                    }

                    // Crear el adaptador y asignarlo al ListView
                    AlumnoAdapter adapter = new AlumnoAdapter(getActivity(), productos);
                    // Asegúrate de que tu ListView tenga el ID correcto
                    lista.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void EditarAlumno(String idAlum) {

        Bundle bundle = new Bundle();
        bundle.putString("idAlum", idAlum);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_alumno_to_alumnoEditar,bundle);
    }

    private void EliminarAlumno(String idAlumno) {

        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_eliminar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idAlumno", idAlumno);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                //Actualiza el mismo fragment
                NavController navController = NavHostFragment.findNavController(AlumnoFragment.this);
                navController.navigate(R.id.action_nav_alumno_self);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onClick(View v) {
        if(v==boton)
        {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_alumno_to_alumnoRegistrar);
        }

    }
}