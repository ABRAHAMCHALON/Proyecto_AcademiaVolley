package com.example.proyecto_academiavolley.ui.grupo;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_academiavolley.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GrupoEditar extends Fragment implements View.OnClickListener {

    private EditText nomGrupo, diasGrupo, horaInicio, horaFin;
    private Button btnActualizarGrupo;
    private String idGrupo;

    public GrupoEditar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idGrupo = getArguments().getString("idGrupo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grupo_editar, container, false);

        nomGrupo = rootView.findViewById(R.id.etNomGrupoE);
        diasGrupo = rootView.findViewById(R.id.etDiasGrupoE);
        horaInicio = rootView.findViewById(R.id.etHinicioGrupoE);
        horaFin = rootView.findViewById(R.id.etHfinGrupoE);
        btnActualizarGrupo = rootView.findViewById(R.id.btnActualizarGrupo);

        btnActualizarGrupo.setOnClickListener(this);

        // Llamamos a cargarDatosGrupo() para obtener los datos actuales
        cargarDatosGrupo();

        return rootView;
    }

    // Método para cargar los datos actuales del grupo en los campos EditText
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
                        String dias = jsonObject.getString("dias_horario"); // Corregir a "dias_horario"
                        String horaInicioStr = jsonObject.getString("hinicio_horario");
                        String horaFinStr = jsonObject.getString("hfin_horario");

                        // Asignar los valores obtenidos a los EditText
                        nomGrupo.setText(nombre);
                        diasGrupo.setText(dias);
                        horaInicio.setText(horaInicioStr);
                        horaFin.setText(horaFinStr);
                    } else {
                        Toast.makeText(getActivity(), "No se encontró el grupo", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al cargar los datos del grupo", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Método para actualizar el grupo
    private void actualizarGrupo(String nombre, String dias, String inicio, String fin) {
        String url = servidor + "grupo_actualizar.php"; // URL para actualizar el grupo

        // Verifica que todos los parámetros estén completos
        if (idGrupo == null || nombre.isEmpty() || dias.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto RequestParams y asignar los parámetros
        RequestParams params = new RequestParams();
        params.put("idGrupo", idGrupo);        // ID del grupo
        params.put("nomGrupo", nombre);        // Nombre del grupo
        params.put("diasHorario", dias);       // Días del grupo
        params.put("hinicioHorario", inicio);  // Hora de inicio
        params.put("hfinHorario", fin);        // Hora de fin

        // Verificar que los parámetros están siendo enviados correctamente
        Log.d("GrupoEditar", "Parametros enviados: " +
                "idGrupo=" + idGrupo + ", " +
                "nomGrupo=" + nombre + ", " +
                "diasHorario=" + dias + ", " +
                "hinicioHorario=" + inicio + ", " +
                "hfinHorario=" + fin);

        // Crear una instancia de AsyncHttpClient para hacer la solicitud POST
        AsyncHttpClient client = new AsyncHttpClient();

        // Realizar la solicitud POST
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // Convertir la respuesta del servidor en un String
                    String response = new String(responseBody);
                    Log.d("GrupoEditar", "Respuesta del servidor: " + response); // Imprimir la respuesta del servidor

                    // Parsear el JSON de la respuesta
                    JSONArray jsonResponseArray = new JSONArray(response);
                    JSONObject jsonResponse = jsonResponseArray.getJSONObject(0);  // Obtener el primer objeto del array
                    String mensaje = jsonResponse.getString("respuesta");

                    // Mostrar el mensaje del servidor
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
                    // Navegar de vuelta al fragmento de lista de grupos
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_grupoEditar_to_nav_grupo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al actualizar el grupo", Toast.LENGTH_LONG).show();
                }
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
        if (v == btnActualizarGrupo) {
            String nombre = nomGrupo.getText().toString();
            String dias = diasGrupo.getText().toString();
            String inicio = horaInicio.getText().toString();
            String fin = horaFin.getText().toString();

            if (nombre.isEmpty() || dias.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Llamamos al método para actualizar el grupo
                actualizarGrupo(nombre, dias, inicio, fin);
            }
        }
    }
}
