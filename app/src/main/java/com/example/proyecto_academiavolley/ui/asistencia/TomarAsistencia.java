package com.example.proyecto_academiavolley.ui.asistencia;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_academiavolley.R;
import com.example.proyecto_academiavolley.ui.alumno.Alumno;
import com.example.proyecto_academiavolley.ui.alumno.AlumnoFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class TomarAsistencia extends Fragment {

    ListView lista;
    TextView nomGrupo;
    Button btnGuardarAsistencia;
    String idGrupo;
    List<Grupo> gruposList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idGrupo = getArguments().getString("idGrupo");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla el layout de tu fragmento
        View rootView = inflater.inflate(R.layout.fragment_tomar_asistencia, container, false);
        lista = rootView.findViewById(R.id.listViewAsistencia);  // Asegúrate de que el ID de ListView coincida
        nomGrupo = rootView.findViewById(R.id.textViewNombreGrupo);
        btnGuardarAsistencia = rootView.findViewById(R.id.btnGuardarAsistencia); // Obtenemos el botón desde el Fragment

        // Asignar el OnClickListener al botón
        btnGuardarAsistencia.setOnClickListener(v -> {
            // Llamar a GuardarAsistencia desde el Fragment
            GuardarAsistencia();
        });

        cargarDatosGrupo();
        // Llamar a la función para listar los grupos
        ListarAlumnos();

        return rootView;
    }

    public class AsistenciaAdapter extends BaseAdapter {

        private Context context;
        private final List<Asistencia> asistenciaList;

        public AsistenciaAdapter(Context context, List<Asistencia> asistenciaList) {
            this.context = context;
            this.asistenciaList = asistenciaList;
        }

        @Override
        public int getCount() {
            return asistenciaList.size();
        }

        @Override
        public Object getItem(int position) {
            return asistenciaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_tomar_asistencia, null);
            }

            // Obtener los elementos de la vista
            TextView id = convertView.findViewById(R.id.tvIdAlumno);
            TextView nombresAlumno = convertView.findViewById(R.id.textViewNombres);
            TextView apellidosAlumno = convertView.findViewById(R.id.textViewApellidos);
            RadioButton estAsistio = convertView.findViewById(R.id.radioButtonAsistio);
            RadioButton estFalto = convertView.findViewById(R.id.radioButtonFalto);

            // Obtener la asistencia
            Asistencia asistencia = asistenciaList.get(position);

            // Asignar los valores de la asistencia
            id.setText(asistencia.getId_alumno());
            nombresAlumno.setText(asistencia.getNombre());
            apellidosAlumno.setText(asistencia.getApellido());

            // Asignar los RadioButton a la asistencia
            asistencia.setEstAsistio(estAsistio);
            asistencia.setEstFalto(estFalto);
            return convertView;
        }
    }

    private void ListarAlumnos() {
        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_asistencia.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idGrupo", idGrupo); // Agregar el idGrupo como parámetro

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);
                    List<Asistencia> alumnos = new ArrayList<>();

                    // Aquí obtenemos el contexto correctamente
                    Context context = getActivity();  // Usamos getActivity() para obtener el contexto

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id_alumno = jsonObject.getString("id_alumno");
                        String nombre = jsonObject.getString("nom_alumno");
                        String apellido = jsonObject.getString("ape_alumno");

                        // Crear los RadioButton para cada alumno con el contexto adecuado
                        RadioButton radioAsistio = new RadioButton(context);
                        RadioButton radioFalto = new RadioButton(context);

                        // Crear un objeto Asistencia y agregarlo a la lista
                        alumnos.add(new Asistencia(id_alumno, nombre, apellido, radioAsistio, radioFalto));
                    }

                    // Crear el adaptador y asignarlo al ListView
                    TomarAsistencia.AsistenciaAdapter adapter = new TomarAsistencia.AsistenciaAdapter(getActivity(), alumnos);
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

    private void cargarDatosGrupo() {
        String url = servidor + "grupo_consultar.php"; // URL para consultar el grupo

        RequestParams params = new RequestParams();
        params.put("idGrupo", idGrupo);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0); // Suponemos que solo hay un grupo
                        String nombre = jsonObject.getString("nom_grupo");
                        // Asignar los valores obtenidos a los EditText
                        nomGrupo.setText(nombre);
                    } else {
                        Toast.makeText(getActivity(), "No se encontró el grupo", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al cargar el nombre del grupo", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GuardarAsistencia() {
        // Obtener el adaptador del ListView
        AsistenciaAdapter adapter = (AsistenciaAdapter) lista.getAdapter();
        List<Asistencia> asistenciaList = new ArrayList<>();

        // Recorrer todos los items en el ListView
        for (int i = 0; i < adapter.getCount(); i++) {
            // Obtener la asistencia del item actual
            Asistencia asistencia = (Asistencia) adapter.getItem(i);

            // Obtener el estado de asistencia (Asistió o Faltó)
            String tipoAsistencia = "";
            if (asistencia.getEstAsistio().isChecked()) {
                tipoAsistencia = "Asistió";  // Si el radioButton 'Asistió' está seleccionado
            } else if (asistencia.getEstFalto().isChecked()) {
                tipoAsistencia = "Faltó";  // Si el radioButton 'Faltó' está seleccionado
            }

            // Si no se ha seleccionado "Asistió" o "Faltó", salimos y mostramos un mensaje
            if (tipoAsistencia.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, seleccione si asistió o faltó para todos los alumnos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Asignar el tipo de asistencia al objeto Asistencia
            asistencia.setTipoAsistencia(tipoAsistencia);
            asistenciaList.add(asistencia);
        }

        // Ahora realizamos la solicitud para guardar las asistencias
        for (Asistencia asistencia : asistenciaList) {
            // Hacer la solicitud POST para cada asistencia
            String url = servidor + "asistencia_registrar.php";  // URL de tu script PHP

            // Crear los parámetros para enviar por POST
            RequestParams params = new RequestParams();
            params.put("id_alumno", asistencia.getId_alumno());
            params.put("id_grupo", idGrupo);  // El id del grupo ya está disponible en la variable
            params.put("tipo_asistencia", asistencia.getTipoAsistencia());

            // Crear una instancia de AsyncHttpClient
            AsyncHttpClient client = new AsyncHttpClient();

            // Hacer la solicitud POST
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);

                    // Registrar la respuesta en el log para verificar
                    Log.d("Server Response", response);  // Muestra la respuesta completa del servidor

                    // Mostrar la respuesta en un Toast
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                    Toast.makeText(getActivity(), "Error al guardar la asistencia: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
