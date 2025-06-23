package com.example.proyecto_academiavolley;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity implements View.OnClickListener{

    public static final String servidor = "http://10.0.2.2/Clubsanjose/";

    EditText usu, pass;
    Button ing;
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usu = findViewById(R.id.etUsuario);
        pass = findViewById(R.id.etPassword);
        ing = findViewById(R.id.btnLogin);
        progress = findViewById(R.id.progress);
        ing.setOnClickListener(this);

        //verificar si hay SharePreferences para inicio de sesion automatico
        String username = getSharedPreferences("usuario", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("usuario", MODE_PRIVATE).getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            IniciarSesion(username, password);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
            String username = usu.getText().toString();
            String password = pass.getText().toString();
            IniciarSesion(username, password);
        }
    }

    private void IniciarSesion(String username, String password) {

        String url = servidor + "user_autentificar.php";

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        progress.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progress.setVisibility(View.GONE);


                String id_usuario = new String(responseBody);

                if(id_usuario.equals("0")) {
                    Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                }
                else {

                    Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();

                    GrabarSharedPreferences(username,password);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("id_usuario", id_usuario);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GrabarSharedPreferences(String username, String password) {
        //quiero guardar el usuario y contraseña
        getSharedPreferences("usuario", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .putString("password", password)
                .apply();
    }


    private void LimpiarCampos() {
        usu.setText("");
        pass.setText("");
    }

}