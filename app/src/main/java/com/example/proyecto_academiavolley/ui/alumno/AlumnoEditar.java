package com.example.proyecto_academiavolley.ui.alumno;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.proyecto_academiavolley.R;
import com.example.proyecto_academiavolley.ui.CallbackSimple;
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


public class AlumnoEditar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    int idSex=-1, idHorario = -1, idEstado = -1;
    String idAlum,idApo;
    private EditText nomAlum,apeAlum,dni,edad,fnac,nomApo,apeApo,dniApo,cel,dir;
    private Spinner sex,hor,est;
    private Button reg;
    ArrayList<Item> lista_sexo = new ArrayList<>();
    ArrayList<Item> lista_horario  = new ArrayList<>();
    ArrayList<Item> lista_estado  = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alumno_editar, container, false);
        //hacer
        idAlum = getArguments() != null ? getArguments().getString("idAlum") : null;
        idApo = getArguments() != null ? getArguments().getString("idApo") : null;

        //Toast.makeText(getActivity(),"idProd: "+idProd,Toast.LENGTH_SHORT).show();

        nomAlum = (EditText) rootView.findViewById(R.id.etNombresEstudianteEdit);
        apeAlum = (EditText) rootView.findViewById(R.id.etApellidosEstudianteEdit);
        dni = (EditText) rootView.findViewById(R.id.etDniEstudianteEdit);
        edad = (EditText) rootView.findViewById(R.id.etEdadEstudianteEdit);
        fnac = (EditText) rootView.findViewById(R.id.etFechaNacimientoEdit);
        sex = (Spinner) rootView.findViewById(R.id.spinnerSexoEdit);
        sex.setOnItemSelectedListener(this);
        hor = (Spinner) rootView.findViewById(R.id.spinnerHorarioEdit);
        hor.setOnItemSelectedListener(this);
        est = (Spinner) rootView.findViewById(R.id.spinnerEstadoEdit);
        est.setOnItemSelectedListener(this);

        nomApo = (EditText) rootView.findViewById(R.id.etNombresApoderadoEdit);
        apeApo = (EditText) rootView.findViewById(R.id.etApellidosApoderadoEdit);
        dniApo = (EditText) rootView.findViewById(R.id.etDocumentoIdentidadEdit);
        cel = (EditText) rootView.findViewById(R.id.etCelularEdit);
        dir = (EditText) rootView.findViewById(R.id.etDireccionEdit);

        reg = (Button) rootView.findViewById(R.id.btnGuardarEdit);
        reg.setOnClickListener(this);

        ejecutarEnOrden();

        return rootView;
    }

    private void ejecutarEnOrden() {
        ObtenerHorario(() ->
                ObtenerEstado(() ->
                        ObtenerSexo(() ->
                                ConsultarAlumno(() ->
                                        Log.d("DEBUG", "Todas las funciones completadas")
                                )
                        )
                )
        );
    }

    private void ConsultarAlumno(final CallbackSimple callback) {

        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_consultar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idAlum",idAlum);

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

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id_alumno = jsonObject.getString("id_alumno");
                        String nom_alumno = jsonObject.getString("nom_alumno");
                        String ape_alumno = jsonObject.getString("ape_alumno");
                        String ndc_alumno = jsonObject.getString("ndc_alumno");
                        String edad_alumno = jsonObject.getString("edad_alumno");
                        String fnacimiento_alumno = jsonObject.getString("fnacimiento_alumno");

                        int id_sexo = jsonObject.getInt("id_sexo");
                        int id_horario = jsonObject.getInt("id_grupo");
                        int id_estado = jsonObject.getInt("id_estado");

                        String id_apoderadoA = jsonObject.getString("id_apoderadoA");
                        String nom_apoderadoA = jsonObject.getString("nom_apoderadoA");
                        String ape_apoderadoA = jsonObject.getString("ape_apoderadoA");
                        String ndc_apoderadoA = jsonObject.getString("ndc_apoderadoA");
                        String cel_apoderadoA = jsonObject.getString("cel_apoderadoA");
                        String dir_apoderadoA = jsonObject.getString("dir_apoderadoA");


                        //Colocar datos en los EditText
                        nomAlum.setText(nom_alumno);
                        apeAlum.setText(ape_alumno);
                        dni.setText(ndc_alumno);
                        edad.setText(edad_alumno);
                        fnac.setText(fnacimiento_alumno);

                        nomApo.setText(nom_apoderadoA);
                        apeApo.setText(ape_apoderadoA);
                        dniApo.setText(ndc_apoderadoA);
                        cel.setText(cel_apoderadoA);
                        dir.setText(dir_apoderadoA);

                        //Toast.makeText(getActivity(),String.valueOf(lista_categoria.size()),Toast.LENGTH_SHORT).show();
                        //cat.setSelection(1);


                        for (int x = 0; x < lista_sexo.size(); x++) {
                            if (lista_sexo.get(x).getId() == id_sexo) {
                                sex.setSelection(x);
                                break;
                            }
                        }

                        for (int x = 0; x < lista_horario.size(); x++) {
                            if (lista_horario.get(x).getId() == id_horario) {
                                hor.setSelection(x);
                                break;
                            }
                        }
                        for (int x = 0; x < lista_estado.size(); x++) {
                            if (lista_estado.get(x).getId() == id_estado) {
                                est.setSelection(x);
                                break;
                            }
                        }


                        // 👇 Llama al siguiente paso
                        if (callback != null) callback.onComplete();


                    }

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

    private void ObtenerHorario(final CallbackSimple callback) {

        String url =  servidor+"horario_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                lista_horario.add(new Item(-1, "Seleccionar horario"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista_horario.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista_horario);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                hor.setAdapter(adapter);

                // 👇 Llama al siguiente paso
                if (callback != null) callback.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ObtenerEstado(final CallbackSimple callback) {

        String url =  servidor+"estado_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                lista_estado.add(new Item(-1, "Seleccionar estado"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista_estado.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista_estado);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                est.setAdapter(adapter);

                // 👇 Llama al siguiente paso
                if (callback != null) callback.onComplete();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ObtenerSexo(final CallbackSimple callback) {

        String url =  servidor+"sexo_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                lista_sexo.add(new Item(-1, "Seleccionar sexo"));

                //Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista_sexo.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                //Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista_sexo);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sex.setAdapter(adapter);

                // 👇 Llama al siguiente paso
                if (callback != null) callback.onComplete();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });

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
    public void ActualizarAlumno(String nomAlumno, String apeAlumno, String ndcAlumno,
                                 String edadAlumno, String fnacimientoAlumno, int id_sexo, int id_grupo, int id_estado,
                                 String nomApo, String apeApo, String ndcApo, String celApo, String dirApo) {

        // Crear la URL para hacer la solicitud
        String url = servidor + "alumno_actualizar.php";

        // Crear un objeto RequestParams para almacenar los parámetros
        RequestParams params = new RequestParams();
        params.put("idAlumno", idAlum);
        params.put("nomAlum", nomAlumno);
        params.put("apeAlum", apeAlumno);
        params.put("dni", ndcAlumno);
        params.put("edad", edadAlumno);
        params.put("fnac", fnacimientoAlumno);
        params.put("idSex", id_sexo);
        params.put("idHor", id_grupo);
        params.put("idEst", id_estado);

        params.put("nomApo", nomApo);
        params.put("apeApo", apeApo);
        params.put("dniApo", ndcApo);
        params.put("cel", celApo);
        params.put("dir", dirApo);


        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("RESP_CRUDO", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String mensaje = jsonObject.getString("respuesta");

                    Toast.makeText(getActivity(), "Respuesta: " + mensaje, Toast.LENGTH_LONG).show();

                    // Ir al fragmento de lista de alumnos
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_alumnoEditar_to_nav_alumno);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al procesar JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        if(v==reg) //si presiono e boton registrar
        {
            String nom_alum = nomAlum.getText().toString();
            String ape_alum = apeAlum.getText().toString();
            String ndc_alum = dni.getText().toString();
            String edad_alum = edad.getText().toString();
            String fnac_alum = fnac.getText().toString();
            String nom_apo = nomApo.getText().toString();

            //Validaciones
            if (idSex == -1)
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
                ActualizarAlumno(nom_alum, ape_alum, ndc_alum, edad_alum, fnac_alum, idSex, idHorario,
                        idEstado, nom_apo, apeApo.getText().toString(), dniApo.getText().toString(),
                        cel.getText().toString(), dir.getText().toString());
            }
            String ape_apo = apeApo.getText().toString();
            String ndc_apo = dniApo.getText().toString();
            String cel_apo = cel.getText().toString();
            String dir_apo = dir.getText().toString();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==sex)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar categoría"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar categoría"
                //Toast.makeText(getActivity(), "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                idSex = selectedId;
            }
        }
        else if(parent==hor)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar marca"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar marca"
                //Toast.makeText(getActivity(), "Por favor, seleccione una marca", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                idHorario = selectedId;
            }
        }
        else if(parent==est)
        {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar marca"
            if (selectedItem.id == -1) {
                // No hacer nada si es la opción "Seleccionar marca"
                //Toast.makeText(getActivity(), "Por favor, seleccione una marca", Toast.LENGTH_SHORT).show();
            } else {
                // Si no es el item ficticio, maneja la selección normalmente
                int selectedId = selectedItem.id;
                String selectedNombre = selectedItem.nombre;
                //Toast.makeText(getActivity(), "Seleccionado: " + selectedId + " - " + selectedNombre, Toast.LENGTH_SHORT).show();
                idEstado = selectedId;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}