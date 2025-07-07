package com.example.proyecto_academiavolley.ui.alumno;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.proyecto_academiavolley.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AlumnoFragment extends Fragment implements View.OnClickListener {

    ListView lista;
    Button boton;
    EditText etBuscarAlumno; // Campo de búsqueda por DNI
    List<Alumno> alumnoList = new ArrayList<>();
    AlumnoAdapter adapter;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla el layout de tu fragmento
        View rootView = inflater.inflate(R.layout.fragment_alumno, container, false);

        // Inicializar campos de texto y botones
        lista = rootView.findViewById(R.id.listViewAlumnos);
        boton = rootView.findViewById(R.id.btnRegistrar);
        etBuscarAlumno = rootView.findViewById(R.id.etBuscarAlumno); // EditText para búsqueda

        boton.setOnClickListener(this);

        // Obtener los datos para los spinners
        ListarAlumnos();

        // Agregar un TextWatcher para filtrar los alumnos según el DNI
        etBuscarAlumno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
    }

    // Método para filtrar la lista de alumnos por el DNI ingresado
    private void filterList(String query) {
        List<Alumno> filteredList = new ArrayList<>();
        for (Alumno alumno : alumnoList) {
            if (alumno.ndc.contains(query)) { // Filtrar por DNI
                filteredList.add(alumno);
            }
        }
        adapter = new AlumnoAdapter(getActivity(), filteredList);
        lista.setAdapter(adapter); // Actualizar el ListView con la lista filtrada
    }

    // Clase Adapter para el ListView
    public class AlumnoAdapter extends BaseAdapter {

        private Context context;
        private final List<Alumno> alumnoList;

        public AlumnoAdapter(Context context, List<Alumno> alumnoList) {
            this.context = context;
            this.alumnoList = alumnoList;
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

            // Obtener el alumno
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

            // Editar alumno
            editar.setOnClickListener(v -> EditarAlumno(alumno.id));

            // Eliminar alumno
            eliminar.setOnClickListener(v -> EliminarAlumno(alumno.id));

            return convertView;
        }
    }

    // Método para listar los alumnos desde el servidor
    private void ListarAlumnos() {
        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_mostrar.php";
        RequestParams params = new RequestParams();

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    alumnoList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Alumno alumno = new Alumno(
                                jsonObject.getString("id_alumno"),
                                jsonObject.getString("nom_alumno"),
                                jsonObject.getString("ape_alumno"),
                                jsonObject.getString("ndc_alumno"),
                                jsonObject.getString("edad_alumno"),
                                jsonObject.getString("fnacimiento_alumno"),
                                jsonObject.getString("nom_grupo"),
                                jsonObject.getString("dias_horario") + " " + jsonObject.getString("hinicio_horario") + " - " + jsonObject.getString("hfin_horario"),
                                jsonObject.getString("nom_apoderado"),
                                jsonObject.getString("ape_apoderado"),
                                jsonObject.getString("cel_apoderado"),
                                jsonObject.getString("dir_apoderado")
                        );
                        alumnoList.add(alumno);
                    }

                    // Asignar el adaptador al ListView
                    adapter = new AlumnoAdapter(getActivity(), alumnoList);
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

    // Función para editar el alumno
    private void EditarAlumno(String idAlum) {
        Bundle bundle = new Bundle();
        bundle.putString("idAlum", idAlum);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_alumno_to_alumnoEditar, bundle);
    }

    // Función para eliminar el alumno
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
                String response = new String(responseBody);
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();

                // Actualiza el mismo fragment
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

    // Método para navegar a la pantalla de registrar alumno
    @Override
    public void onClick(View v) {
        if (v == boton) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_alumno_to_alumnoRegistrar);
        }
    }
}