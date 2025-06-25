package com.example.proyecto_academiavolley.ui.grupo;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.proyecto_academiavolley.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class GrupoRegistrar extends Fragment implements View.OnClickListener {

    private EditText etNomGrupo, etDiasGrupo, etHinicioGrupo, etHfinGrupo;
    private Button btnRegistrarGrupo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_grupo_registrar, container, false);

        // Inicialización de los campos EditText y el botón
        etNomGrupo = rootView.findViewById(R.id.etNomGrupo);
        etDiasGrupo = rootView.findViewById(R.id.etDiasGrupo);
        etHinicioGrupo = rootView.findViewById(R.id.etHinicioGrupo);
        etHfinGrupo = rootView.findViewById(R.id.etHfinGrupo);
        btnRegistrarGrupo = rootView.findViewById(R.id.btnRegistrarGrupo);

        // Asignamos el evento click al botón
        btnRegistrarGrupo.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegistrarGrupo) {
            // Obtener los valores de los campos
            String nombreGrupo = etNomGrupo.getText().toString();
            String diasGrupo = etDiasGrupo.getText().toString();
            String hinicioGrupo = etHinicioGrupo.getText().toString();
            String hfinGrupo = etHfinGrupo.getText().toString();

            // Validación de los campos
            if (nombreGrupo.isEmpty() || diasGrupo.isEmpty() || hinicioGrupo.isEmpty() || hfinGrupo.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Si los campos están completos, registrar el grupo
                RegistrarGrupo(nombreGrupo, diasGrupo, hinicioGrupo, hfinGrupo);
            }
        }
    }

    private void RegistrarGrupo(String nombreGrupo, String diasHorario, String hinicioHorario, String hfinHorario) {
        // Crear la URL para hacer la solicitud
        String url = servidor+"grupo_registrar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("nomGrupo", nombreGrupo);
        params.put("diasHorario", diasHorario);
        params.put("hinicioHorario", hinicioHorario);
        params.put("hfinHorario", hfinHorario);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Grupo registrado exitosamente: " + response, Toast.LENGTH_LONG).show();
                LimpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error al registrar el grupo: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void LimpiarCampos() {
        // Limpiar los campos después de registrar
        etNomGrupo.setText("");
        etDiasGrupo.setText("");
        etHinicioGrupo.setText("");
        etHfinGrupo.setText("");
        etNomGrupo.requestFocus();
    }
}
