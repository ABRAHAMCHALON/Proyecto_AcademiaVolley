package com.example.proyecto_academiavolley.ui.alumno;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class AlumnoRegistrar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int idSex = -1, idHorario = -1, idEstado = -1;

    private EditText nomAlum, apeAlum, dni, edad, fnac, nomApo, apeApo, dniApo, cel, dir;
    private Spinner sex, hor, est;
    private Button reg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alumno_registrar, container, false);

        // Inicializar campos de texto y botones
        nomAlum = rootView.findViewById(R.id.etNombresEstudiante);
        apeAlum = rootView.findViewById(R.id.etApellidosEstudiante);
        dni = rootView.findViewById(R.id.etDniEstudiante);
        edad = rootView.findViewById(R.id.etEdadEstudiante);
        fnac = rootView.findViewById(R.id.etFechaNacimiento);
        sex = rootView.findViewById(R.id.spinnerSexo);
        sex.setOnItemSelectedListener(this);
        hor = rootView.findViewById(R.id.spinnerHorario);
        hor.setOnItemSelectedListener(this);
        est = rootView.findViewById(R.id.spinnerEstado);
        est.setOnItemSelectedListener(this);

        nomApo = rootView.findViewById(R.id.etNombresApoderado);
        apeApo = rootView.findViewById(R.id.etApellidosApoderado);
        dniApo = rootView.findViewById(R.id.etDocumentoIdentidad);
        cel = rootView.findViewById(R.id.etCelular);
        dir = rootView.findViewById(R.id.etDireccion);

        reg = rootView.findViewById(R.id.btnRegistrar);
        reg.setOnClickListener(this);

        // Obtener los datos para los spinners
        ObtenerSexo();
        ObtenerHorario();
        ObtenerEstado();

        // Al hacer click en el campo de fecha de nacimiento, abrir el DatePicker
        fnac.setOnClickListener(v -> showDatePickerDialog());

        return rootView;
    }

    // Mostrar el DatePickerDialog para seleccionar la fecha de nacimiento
    // Mostrar el DatePickerDialog para seleccionar la fecha de nacimiento
    private void showDatePickerDialog() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();

        // Establecer la fecha del DatePickerDialog con la fecha actual
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    // Asegurarse de que el día y el mes tengan siempre dos dígitos
                    String formattedDay = String.format("%02d", dayOfMonth); // "03" en vez de "3"
                    String formattedMonth = String.format("%02d", monthOfYear + 1); // "03" en vez de "3"

                    // Establecer la fecha seleccionada en el EditText
                    String selectedDate = formattedDay + "-" + formattedMonth + "-" + yearSelected;
                    fnac.setText(selectedDate);

                    // Calcular la edad automáticamente
                    int edadCalculated = calculateAge(yearSelected, monthOfYear, dayOfMonth);
                    edad.setText(String.valueOf(edadCalculated)); // Mostrar la edad en el campo
                }, year, month, day);

        datePickerDialog.show();
    }

    // Método para calcular la edad en base a la fecha de nacimiento
    private int calculateAge(int birthYear, int birthMonth, int birthDay) {
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthYear;

        // Ajustar si el cumpleaños aún no ha ocurrido este año
        if (today.get(Calendar.MONTH) < birthMonth || (today.get(Calendar.MONTH) == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthDay)) {
            age--;
        }

        return age;
    }

    // Obtener los datos para el Spinner de Sexo
    private void ObtenerSexo() {
        String url = servidor + "sexo_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar sexo"));

                // Respuesta del servidor
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

                // Llenar el Spinner
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

    // Obtener los datos para el Spinner de Horario
    private void ObtenerHorario() {
        String url = servidor + "horario_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar horario"));

                // Respuesta del servidor
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

                // Llenar el Spinner
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

    // Obtener los datos para el Spinner de Estado
    private void ObtenerEstado() {
        String url = servidor + "estado_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar estado"));

                // Respuesta del servidor
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

                // Llenar el Spinner
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
        if (v == reg) {
            // Recoger los valores de los campos
            String nombreAlumno = nomAlum.getText().toString();
            String apellidoAlumno = apeAlum.getText().toString();
            String dniAlumno = dni.getText().toString();
            String edadAlumno = edad.getText().toString();
            String fechaNacimiento = fnac.getText().toString();
            String nombreApoderado = nomApo.getText().toString();
            String apellidoApoderado = apeApo.getText().toString();
            String dniApoderado = dniApo.getText().toString();
            String celularApoderado = cel.getText().toString();
            String direccionApoderado = dir.getText().toString();

            // Validar los campos
            if (nombreAlumno.isEmpty() || apellidoAlumno.isEmpty() || dniAlumno.isEmpty() || edadAlumno.isEmpty() || fechaNacimiento.isEmpty() || nombreApoderado.isEmpty() || apellidoApoderado.isEmpty() || dniApoderado.isEmpty() || celularApoderado.isEmpty() || direccionApoderado.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else if (idSex == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            } else if (idHorario == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            } else if (idEstado == -1) {
                Toast.makeText(getActivity(), "Por favor, seleccione una opción", Toast.LENGTH_SHORT).show();
            } else {
                RegistrarAlumno(nombreAlumno, apellidoAlumno, dniAlumno, edadAlumno, fechaNacimiento, idSex, idHorario, idEstado, nombreApoderado, apellidoApoderado, dniApoderado, celularApoderado, direccionApoderado);
            }
        }
    }

    // Método para registrar al alumno
    public void RegistrarAlumno(String nombreAlumno, String apellidoAlumno, String dniAlumno, String edadAlumno, String fechaNacimiento, int idSexo, int idHorario, int idEstado, String nombreApoderado, String apellidoApoderado, String dniApoderado, String celularApoderado, String direccionApoderado) {
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

    // Limpiar los campos después del registro
    private void LimpiarCampos() {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == sex) {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idSex = selectedItem.id;
        } else if (parent == hor) {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idHorario = selectedItem.id;
        } else if (parent == est) {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            idEstado = selectedItem.id;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
