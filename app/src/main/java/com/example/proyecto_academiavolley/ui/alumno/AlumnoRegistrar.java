package com.example.proyecto_academiavolley.ui.alumno;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_academiavolley.R;
import com.example.proyecto_academiavolley.ui.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class AlumnoRegistrar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int idSex=-1, idHorario = -1, idEstado = -1;

    private EditText nomAlum,apeAlum,dni,edad,fnac,nomApo,apeApo,dniApo,cel,dir;
    private Spinner sex,hor,est;
    private Button reg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alumno_registrar, container, false);

        nomAlum = (EditText) rootView.findViewById(R.id.etNombresEstudiante);
        apeAlum = (EditText) rootView.findViewById(R.id.etApellidosEstudiante);
        dni = (EditText) rootView.findViewById(R.id.etDniEstudiante);
        edad = (EditText) rootView.findViewById(R.id.etEdadEstudiante);
        fnac = (EditText) rootView.findViewById(R.id.etFechaNacimiento);
        sex = (Spinner) rootView.findViewById(R.id.spinnerSexo);
        sex.setOnItemSelectedListener(this);
        hor = (Spinner) rootView.findViewById(R.id.spinnerHorario);
        hor.setOnItemSelectedListener(this);
        est = (Spinner) rootView.findViewById(R.id.spinnerEstado);
        est.setOnItemSelectedListener(this);

        nomApo = (EditText) rootView.findViewById(R.id.etNombresApoderado);
        apeApo = (EditText) rootView.findViewById(R.id.etApellidosApoderado);
        dniApo = (EditText) rootView.findViewById(R.id.etDocumentoIdentidad);
        cel = (EditText) rootView.findViewById(R.id.etCelular);
        dir = (EditText) rootView.findViewById(R.id.etDireccion);

        reg = (Button) rootView.findViewById(R.id.btnRegistrar);
        reg.setOnClickListener(this);

        ObtenerSexo();
        ObtenerHorario();
        ObtenerEstado();

        return rootView;
    }

    private void ObtenerHorario() {

        String url =  servidor+"horario_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar horario"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                hor.setAdapter(adapter);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ObtenerSexo() {

        String url =  servidor+"sexo_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar sexo"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sex.setAdapter(adapter);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ObtenerEstado() {

        String url =  servidor+"estado_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar estado"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                est.setAdapter(adapter);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v==reg) //si presiono e boton registrar
        {
            String nombreAlumno  = nomAlum.getText().toString();
            String apellidoAlumno = apeAlum.getText().toString();
            String dniAlumno = dni.getText().toString();
            String edadAlumno = edad.getText().toString();
            String fechaNacimiento = fnac.getText().toString();
            // idSex ya se obtiene en onItemSelected
            // idHorario ya se obtiene en onItemSelected
            String nombreApoderado = nomApo.getText().toString();
            String apellidoApoderado = apeApo.getText().toString();
            String dniApoderado = dniApo.getText().toString();
            String celularApoderado = cel.getText().toString();
            String direccionApoderado = dir.getText().toString();
            //Completar con los demas campos


            //Validaciones
            if(nombreAlumno.isEmpty() || apellidoAlumno.isEmpty() || dniAlumno.isEmpty() || edadAlumno.isEmpty() || fechaNacimiento.isEmpty() || nombreApoderado.isEmpty() || apellidoApoderado.isEmpty() || dniApoderado.isEmpty() || celularApoderado.isEmpty() || direccionApoderado.isEmpty())
            {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
            else if (idSex == -1)
            {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            }
            else  if (idHorario == -1)
            {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            }
            else  if (idEstado == -1)
            {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            }
            else
            {
                RegistrarAlumno(nombreAlumno, apellidoAlumno, dniAlumno, edadAlumno, fechaNacimiento,idSex, idHorario, idEstado, nombreApoderado, apellidoApoderado, dniApoderado, celularApoderado, direccionApoderado);
            }
        }

    }

    private void LimpiarCampos()
    {
        nomAlum.setText("");
        apeAlum.setText("");
        dni.setText("");
        edad.setText("");
        fnac.setText("");
        sex.setSelection(0);
        hor.setSelection(0);
        est.setSelection(0);
        nomApo.setText("");
        apeApo.setText("");
        dniApo.setText("");
        cel.setText("");
        dir.setText("");
        nomAlum.requestFocus();

    }
    public void RegistrarAlumno(String nombreAlumno, String apellidoAlumno, String dniAlumno, String edadAlumno, String fechaNacimiento, int idSexo, int idHorario, int idEstado, String nombreApoderado, String apellidoApoderado, String dniApoderado, String celularApoderado, String direccionApoderado ) {

        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_registrar.php"; //TODO: CAMBIAR PHP

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("nomAlum", nombreAlumno);
        params.put("apeAlum", apellidoAlumno);
        params.put("dni", dniAlumno);
        params.put("edad", edadAlumno);
        params.put("fnac", fechaNacimiento);
        params.put("idSex", idSexo);
        params.put("idHor", idHorario);
        params.put("idEst", idEstado);
        params.put("nomApo", nombreApoderado);
        params.put("apeApo", apellidoApoderado);
        params.put("dniApo", dniApoderado);
        params.put("cel", celularApoderado);
        params.put("dir", direccionApoderado);


        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);  // Obtener la respuesta del servidor como String
                Toast.makeText(getActivity(), "Respuesta: " + response, Toast.LENGTH_LONG).show();
                LimpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==sex)
        { //TODO: Cambiar cat por sex
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idSex = selectedItem.id;
            // Verifica si es el item "Seleccionar categoría"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar categoría"
                //Toast.makeText(getActivity(), "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                //idCat = selectedId; //TODO: No es necesario, idSex ya tiene el valor
            }
        }
        else if(parent==hor) //TODO: Cambiar mar por hor
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idHorario = selectedItem.id;

            // Verifica si es el item "Seleccionar marca"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar marca"
                //Toast.makeText(getActivity(), "Por favor, seleccione una marca", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                //idMar = selectedId; //TODO: No es necesario, idHorario ya tiene el valor
            }
        }
        else if(parent==est)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idEstado = selectedItem.id;

            // Verifica si es el item "Seleccionar marca"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar marca"
                //Toast.makeText(getActivity(), "Por favor, seleccione una marca", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                //idMar = selectedId; //TODO: No es necesario, idHorario ya tiene el valor
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}