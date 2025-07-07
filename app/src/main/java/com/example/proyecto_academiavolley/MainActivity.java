package com.example.proyecto_academiavolley;

import static com.example.proyecto_academiavolley.Login.servidor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_academiavolley.databinding.ActivityMainBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    TextView navUsername;
    TextView navEmail;
    ImageView navImage;

    String id_user;
    String opc_asistencia, opc_alumno, opc_grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //--------------------------------------------------------------------
        // Recibir el id_usuario del login
        id_user = getIntent().getStringExtra("id_usuario");

        // Acceder al nav_header_main.xml
        View headerView = binding.navView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.NomUsu);
        navEmail = headerView.findViewById(R.id.EmailUsu);
        navImage = headerView.findViewById(R.id.imageView);
        //--------------------------------------------------------------------

        // Establecer la barra de acción
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Inicializar DrawerLayout y NavigationView
        DrawerLayout drawer = binding.drawerLayout;

        // Ahora inicializamos navigationView correctamente después de la inicialización de binding
        NavigationView navigationView = binding.navView;

        // Consultar usuario para llenar la información en el encabezado
        ConsultarUsuario(id_user, navigationView);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_asistencia, R.id.nav_alumno, R.id.nav_grupo)
                .setOpenableLayout(drawer)
                .build();

        // Configurar NavController y NavigationUI
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void ConsultarUsuario(String idUsuario, NavigationView navigationView) {
        String url = servidor + "usuario_consultar.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.d("RESPUESTA_SERVIDOR", response);
                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nom_usuario = jsonObject.getString("nom_usuario");
                        String ape_usuario = jsonObject.getString("ape_usuario");
                        String cel_usuario = jsonObject.getString("cel_usuario");
                        String email_usuario = jsonObject.getString("email_usuario");
                        String foto_personal = jsonObject.getString("foto_usuario");
                        String nom_cargo = jsonObject.getString("nom_cargo");
                        // Asignar los datos al header del NavigationView
                        navUsername.setText(nom_usuario);
                        navEmail.setText(email_usuario);
                        //foto_personal es una URL
                        Glide.with(getApplicationContext())
                                .load(servidor+foto_personal);



                        opc_asistencia = jsonObject.getString("opc_asistencia");
                        opc_alumno = jsonObject.getString("opc_alumno");
                        opc_grupo = jsonObject.getString("opc_grupo");

                        if (opc_asistencia != null && opc_asistencia.equals("0")) {
                            navigationView.getMenu().findItem(R.id.nav_asistencia).setVisible(false);
                        }
                        if (opc_alumno != null && opc_alumno.equals("0")) {
                            navigationView.getMenu().findItem(R.id.nav_alumno).setVisible(false);
                        }
                        if (opc_grupo != null && opc_grupo.equals("0")) {
                            navigationView.getMenu().findItem(R.id.nav_grupo).setVisible(false);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Error de carga", "Código de estado: " + statusCode);
                if (responseBody != null) {
                    Log.e("Error de carga", new String(responseBody));
                }
                // Manejo del error
                Toast.makeText(MainActivity.this, "Error al cargar la imagen", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // al presionar action_salir hacer una accion
        MenuItem logoutItem = menu.findItem(R.id.action_salir);
        logoutItem.setOnMenuItemClickListener(item -> {

            getSharedPreferences("usuario", MODE_PRIVATE).edit().clear().apply();
            // dirigirme hacia el Login
            Intent intent = new Intent(MainActivity.this, Inicio.class);
            startActivity(intent);

            return true;
        });

        MenuItem passwordItem = menu.findItem(R.id.action_password);
        passwordItem.setOnMenuItemClickListener(item -> {
            // Mostrar AlertDialog para cambiar contraseña
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Cambiar Contraseña");

            // Inflar el layout personalizado para el AlertDialog
            View viewInflated = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
            final android.widget.EditText inputOldPassword = viewInflated.findViewById(R.id.old_password);
            final android.widget.EditText inputNewPassword = viewInflated.findViewById(R.id.new_password);
            final android.widget.EditText inputConfirmPassword = viewInflated.findViewById(R.id.confirm_password);

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String oldPassword = inputOldPassword.getText().toString();
                String newPassword = inputNewPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    return;
                }
                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(this, "La nueva contraseña no puede ser igual a la antigua", Toast.LENGTH_LONG).show();
                    return;
                }
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("DEBUG_CALL", "ID antes de cambiar contraseña: " + id_user);
                CambiarContrasena(id_user, newPassword);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();

            return true;
        });

        MenuItem actualizarItem = menu.findItem(R.id.action_datos);
        actualizarItem.setOnMenuItemClickListener(item -> {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Actualizar Datos");

            // Inflar el layout personalizado para el AlertDialog
            View viewInflated = getLayoutInflater().inflate(R.layout.dialog_actualizar_usuario, null);
            final android.widget.EditText inputNombre = viewInflated.findViewById(R.id.update_nombre);
            final android.widget.EditText inputApellido = viewInflated.findViewById(R.id.update_apellidop);
            final android.widget.EditText inputDni = viewInflated.findViewById(R.id.update_ndc);
            final android.widget.EditText inputCelular = viewInflated.findViewById(R.id.update_celular);
            final android.widget.EditText inputEmail = viewInflated.findViewById(R.id.update_email);

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String nombre = inputNombre.getText().toString();
                String apellido = inputApellido.getText().toString();
                String dni = inputDni.getText().toString();
                String celular = inputCelular.getText().toString();
                String email = inputEmail.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || celular.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }
                ActualizarDatosUsuario(id_user, nombre, apellido, dni, celular, email);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
            return true;
        });

        return true;
    }

    private void CambiarContrasena(String idUsuario, String nuevaPassword) {
        String url = servidor + "cambiar_password.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("nueva_password", nuevaPassword);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("STATUS_CODE", String.valueOf(statusCode));
                Log.d("RESPUESTA_RAW", response);

                try {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");

                    if (success) {
                        Toast.makeText(MainActivity.this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("JSON_ERROR", e.toString());
                    Toast.makeText(MainActivity.this, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Fallo en la conexión con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ActualizarDatosUsuario(String idUsuario, String nombre, String apellido, String dni, String celular, String email) {
        String url = servidor + "actualizar_datos_usuario.php"; // Asegúrate de que este endpoint exista y funcione

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("nom_usuario", nombre);
        params.put("ape_usuario", apellido);
        params.put("ndc_usuario", dni);
        params.put("cel_usuario", celular);
        params.put("email_usuario", email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject json = new JSONObject(response);

                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");

                    if (success) {
                        Toast.makeText(MainActivity.this, "Datos actualizados correctamente", Toast.LENGTH_LONG).show();
                        ConsultarUsuario(id_user, binding.navView); // Actualizar la UI del NavHeader
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Fallo en la conexión con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}