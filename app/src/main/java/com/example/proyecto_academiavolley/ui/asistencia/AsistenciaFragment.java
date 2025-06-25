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

public class AsistenciaFragment extends Fragment {

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
            TextView nombreGrupo = convertView.findViewById(R.id.nombreGrupo);
            TextView diasGrupo = convertView.findViewById(R.id.diasGrupo);
            TextView horarioGrupo = convertView.findViewById(R.id.horarioGrupo);

            // Obtener el grupo
            Grupo grupo = grupoList.get(position);

            // Asignar los valores
            nombreGrupo.setText(grupo.getNom_grupo());
            diasGrupo.setText("Días: " + grupo.getDias_horario());
            horarioGrupo.setText("Horario: " + grupo.getHinicio_horario() + " - " + grupo.getHfin_horario());

            return convertView;
        }
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
}
