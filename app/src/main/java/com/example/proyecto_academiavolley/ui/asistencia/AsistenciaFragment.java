package com.example.proyecto_academiavolley.ui.asistencia;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AsistenciaFragment extends Fragment{

    ListView lista;
    List<Grupo> gruposList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Infla el layout de tu fragmento
        View rootView = inflater.inflate(R.layout.fragment_asistencia, container, false);
        lista = rootView.findViewById(R.id.listViewGrupos);  // Asegúrate de que el ID de ListView coincida
        // Llamar a la función para listar los grupos
        ListarGrupos();

        return rootView;
    }

    // Adaptador para los grupos
    public class GruposAdapter extends BaseAdapter {

        private Context context;
        private final List<Grupo> grupoList;

        public GruposAdapter(Context context, List<Grupo> grupoList) {
            this.context = context;
            this.grupoList = grupoList;
        }

        @Override
        public int getCount() {
            return grupoList.size();
        }

        @Override
        public Object getItem(int position) {
            return grupoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_grupo, null);
            }

            // Obtener los elementos de la vista
            TextView id = convertView.findViewById(R.id.tvIdGrupo);
            TextView nombreGrupo = convertView.findViewById(R.id.nombreGrupo);
            TextView diasGrupo = convertView.findViewById(R.id.diasGrupo);
            TextView horarioGrupo = convertView.findViewById(R.id.horarioGrupo);

            // Obtener el grupo
            Grupo grupo = grupoList.get(position);

            // Asignar los valores
            id.setText(grupo.id_grupo);
            nombreGrupo.setText(grupo.getNom_grupo());
            diasGrupo.setText("Días: " + grupo.getDias_horario());
            horarioGrupo.setText("Horario: " + grupo.getHinicio_horario() + " - " + grupo.getHfin_horario());
            Button btnAsistencia = convertView.findViewById(R.id.btnTomarAsistencia);
            Button btnVerAsistencia = convertView.findViewById(R.id.btnListarAsistencia);



            btnAsistencia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TomarAsistencia(grupo.id_grupo);
                }
            });

            btnVerAsistencia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mostrar AlertDialog para ingresar la fecha
                    mostrarDialogoFecha(grupo.id_grupo);
                }
            });
            return convertView;
        }

    }

    private void mostrarDialogoFecha(String idGrupo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fecha, null);
        builder.setView(dialogView);

        EditText etFecha = dialogView.findViewById(R.id.etFecha);

        // Configurar el botón para abrir el DatePickerDialog
        Button btnSeleccionarFecha = dialogView.findViewById(R.id.btnSeleccionarFecha);
        btnSeleccionarFecha.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int anio = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH);
            int dia = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Mostrar la fecha seleccionada en el EditText
                        etFecha.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                    }, anio, mes, dia);
            datePickerDialog.show();
        });

        // Acción para el botón "Ver Asistencia"
        builder.setPositiveButton("Ver Asistencia", (dialog, which) -> {
            String fechaSeleccionada = etFecha.getText().toString();
            if (!fechaSeleccionada.isEmpty()) {
                VerAsistencia(idGrupo, fechaSeleccionada); // Tu método para manejar la asistencia
            } else {
                Toast.makeText(getContext(), "Por favor, seleccione una fecha", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para el botón "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void TomarAsistencia(String idGrupo) {

        Bundle bundle = new Bundle();
        bundle.putString("idGrupo", idGrupo);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_asistencia_to_tomarAsistencia,bundle);
    }

    // Función para listar los grupos desde la API
    private void ListarGrupos() {

        // Crear la URL para hacer la solicitud
        String url = servidor + "grupo_mostrar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Log.d("AsistenciaFragment", "Respuesta del servidor: " + response);  // Imprime la respuesta para depurar

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);
                    gruposList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Verificar si la clave 'id_grupo' existe
                        if (jsonObject.has("id_grupo")) {
                            String id_grupo = jsonObject.getString("id_grupo");
                            String nom_grupo = jsonObject.getString("nom_grupo");
                            String dias_horario = jsonObject.getString("dias_horario");
                            String hinicio_horario = jsonObject.getString("hinicio_horario");
                            String hfin_horario = jsonObject.getString("hfin_horario");

                            // Crear un objeto Grupo y agregarlo a la lista
                            gruposList.add(new Grupo(id_grupo, nom_grupo, dias_horario, hinicio_horario, hfin_horario));
                        } else {
                            Log.e("AsistenciaFragment", "No se encontró el campo id_grupo en el JSON");
                        }
                    }

                    // Crear el adaptador y asignarlo al ListView
                    GruposAdapter adapter = new GruposAdapter(getActivity(), gruposList);
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

    private void VerAsistencia(String idGrupo, String fecha){
        Bundle bundle = new Bundle();
        bundle.putString("idGrupo", idGrupo);
        bundle.putString("fecha", fecha);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_asistencia_to_verRegistrosAsistencia, bundle);
    }
}
