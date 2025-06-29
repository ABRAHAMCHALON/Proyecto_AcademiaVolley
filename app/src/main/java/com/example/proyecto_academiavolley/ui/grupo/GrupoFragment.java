package com.example.proyecto_academiavolley.ui.grupo;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GrupoFragment extends Fragment implements View.OnClickListener {

    ListView lista;
    Button nuevo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el layout de tu fragmento
        View rootView = inflater.inflate(R.layout.fragment_grupo, container, false);

        lista = (ListView) rootView.findViewById(R.id.listViewGrupos);
        nuevo = (Button) rootView.findViewById(R.id.btnRegistrarGrupo);
        nuevo.setOnClickListener(this);

        ListarGrupos();

        return rootView;
    }

    public class GrupoAdapter extends BaseAdapter {

        private Context context;
        private final List<Grupo> grupoList;

        public GrupoAdapter(Context context, List<Grupo> grupoList) {
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
                convertView = inflater.inflate(R.layout.item_grupomenu, null);
            }

            // Obtener los elementos de la vista
            TextView nombreGrupo = convertView.findViewById(R.id.nombreGrupo);
            TextView diasGrupo = convertView.findViewById(R.id.diasGrupo);
            TextView horarioGrupo = convertView.findViewById(R.id.horarioGrupo);
            Button editar = convertView.findViewById(R.id.btnEditar);
            Button eliminar = convertView.findViewById(R.id.btnEliminar);
            TextView tvId = convertView.findViewById(R.id.tvId);

            // Obtener el grupo
            Grupo grupo = grupoList.get(position);

            // Asignar los valores
            nombreGrupo.setText("Grupo: " + grupo.getNom_grupo());
            diasGrupo.setText("Días: " + grupo.getDias_horario());
            horarioGrupo.setText("Horario: " + grupo.getHinicio_horario() + " - " + grupo.getHfin_horario());
            tvId.setText(grupo.getId_grupo()); // El ID debe estar oculto

            editar.setOnClickListener(v -> {
                // Editar el grupo
                EditarGrupo(grupo.getId_grupo());
            });

            eliminar.setOnClickListener(v -> {
                // Eliminar el grupo
                EliminarGrupo(grupo.getId_grupo());
            });

            return convertView;
        }
    }

    private void ListarGrupos() {
        // Crear la URL para hacer la solicitud
        String url = servidor+"grupo_mostrar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();

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
                    List<Grupo> grupos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id_grupo = jsonObject.getString("id_grupo");
                        String nom_grupo = jsonObject.getString("nom_grupo");
                        String dias_horario = jsonObject.getString("dias_horario");
                        String hinicio_horario = jsonObject.getString("hinicio_horario");
                        String hfin_horario = jsonObject.getString("hfin_horario");

                        // Crear un objeto Grupo y agregarlo a la lista
                        grupos.add(new Grupo(id_grupo, nom_grupo, dias_horario, hinicio_horario, hfin_horario));
                    }

                    // Crear el adaptador y asignarlo al ListView
                    GrupoAdapter adapter = new GrupoAdapter(getActivity(), grupos);
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


    private void EditarGrupo(String idGrupo) {
        // Enviar el ID del grupo al fragmento de edición
        Bundle bundle = new Bundle();
        bundle.putString("idGrupo", idGrupo);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_grupo_to_grupoEditar, bundle);
    }

    private void EliminarGrupo(String idGrupo) {
        // Crear la URL para hacer la solicitud
        String url = servidor+"grupo_eliminar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idGrupo", idGrupo);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Grupo eliminado: " + response, Toast.LENGTH_LONG).show();

                // Actualizar el fragmento
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_grupo_self);
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
        if (v == nuevo) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_grupo_to_grupoRegistrar);
        }
    }
}
