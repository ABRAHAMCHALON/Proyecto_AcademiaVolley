package com.example.proyecto_academiavolley.ui.asistencia;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class VerRegistrosAsistencia extends Fragment {

    ListView listaAsistencia;
    TextView DiaGrupo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout
        View rootView = inflater.inflate(R.layout.fragment_ver_registros_asistencia, container, false);

        listaAsistencia = rootView.findViewById(R.id.listViewAsistencia);
        DiaGrupo = rootView.findViewById(R.id.tvFechaRegistro);
        // Obtener los parámetros del Bundle
        Bundle args = getArguments();
        if (args != null) {
            String idGrupo = args.getString("idGrupo");
            String fecha = args.getString("fecha");

            DiaGrupo.setText("Día del registro: " + fecha);

            // Llamar a la función que obtiene la asistencia
            obtenerAsistencia(idGrupo, fecha);
        }

        return rootView;
    }

    // Función para obtener la asistencia desde el servidor
    private void obtenerAsistencia(String idGrupo, String fecha) {
        String url = servidor + "asistencia_ver_registro.php";  // URL del archivo PHP
        RequestParams params = new RequestParams();
        params.put("id_grupo", idGrupo);
        params.put("fecha", fecha);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    // Parsear la respuesta JSON
                    JSONArray jsonArray = new JSONArray(response);
                    List<VerAsistencia> listaAsistenciaData = new ArrayList<>(); // Renombrado a listaAsistenciaData

                    // Procesar cada uno de los registros de asistencia
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String idAsistencia = jsonObject.getString("id_asistencia");
                        String nombreAlumno = jsonObject.getString("nom_alumno");
                        String apellidoAlumno = jsonObject.getString("ape_alumno");
                        String tipoAsistencia = jsonObject.getString("tipo_asistencia");

                        // Crear un objeto VerAsistencia y agregarlo a la lista
                        listaAsistenciaData.add(new VerAsistencia(idAsistencia, nombreAlumno, apellidoAlumno, tipoAsistencia));
                    }

                    // Crear el adaptador y asignarlo al ListView
                    AsistenciaAdapter adapter = new AsistenciaAdapter(getActivity(), listaAsistenciaData);
                    listaAsistencia.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Adaptador para mostrar la lista de asistencia
    public class AsistenciaAdapter extends BaseAdapter {

        private Context context;
        private final List<VerAsistencia> asistenciaList;

        public AsistenciaAdapter(Context context, List<VerAsistencia> asistenciaList) {
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
                convertView = inflater.inflate(R.layout.item_ver_asistencia, null);
            }

            TextView nombreAlumno = convertView.findViewById(R.id.tvNombreAlumno);
            TextView apellidoAlumno = convertView.findViewById(R.id.tvApellidoAlumno);
            TextView tipoAsistencia = convertView.findViewById(R.id.tvTipoAsistencia);
            TextView idAsistencia = convertView.findViewById(R.id.tvIdVerAsistencia); // Usamos este TextView

            // Obtener la asistencia correspondiente a la posición actual
            VerAsistencia asistencia = asistenciaList.get(position);

            // Asignar los valores a los TextViews visibles
            nombreAlumno.setText(asistencia.getNombreAlumno());
            apellidoAlumno.setText(asistencia.getApellidoAlumno());
            tipoAsistencia.setText(asistencia.getTipoAsistencia());

            // Asignar el id_asistencia al TextView invisible
            idAsistencia.setText(asistencia.getIdAsistencia());  // Asegúrate de que VerAsistencia tenga este método

            // Aquí puedes agregar lógica para ordenar los elementos si es necesario

            return convertView;
        }
    }
}
