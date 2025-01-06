package com.example.clientesimulacion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btnIniciar, btnProgreso, btnReiniciar;
    EditText etNumeroCorredores, etDistancia;
    TextView txtResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los elementos de la interfaz
        btnIniciar = findViewById(R.id.btnIniciarCarrera);
        btnProgreso = findViewById(R.id.btnVerProgreso);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        etNumeroCorredores = findViewById(R.id.etNumeroCorredores);
        etDistancia = findViewById(R.id.etDistancia);
        txtResultados = findViewById(R.id.tvResultados);

        // Acción para iniciar la carrera
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroCorredores = etNumeroCorredores.getText().toString().trim();
                String distancia = etDistancia.getText().toString().trim();

                if (numeroCorredores.isEmpty() || distancia.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingrese ambos valores (corredores y distancia)", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "http://192.168.10.109:3000/carrera"; // Asegúrate de que la IP sea correcta
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("numeroCorredores", numeroCorredores);
                        jsonObject.put("distancia", distancia);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    realizarSolicitudPOST(url, jsonObject);
                }
            }
        });

        // Acción para ver el progreso de la carrera
        btnProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.10.109:3000/carrera/avance?horas=1&distancia=100"; // Por ejemplo, se pasa 1 hora y 100 km
                realizarSolicitudGET(url);
            }
        });

        // Acción para reiniciar la carrera
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.10.109:3000/carrera"; // Asegúrate de que la IP sea correcta
                realizarSolicitudDELETE(url);
            }
        });
    }

    private void realizarSolicitudPOST(String URL, JSONObject jsonObject) {
        Log.d("URL", URL);  // Imprimir URL en los logs
        Log.d("Request Body", jsonObject.toString());  // Imprimir cuerpo de la solicitud

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Si la respuesta es un JSON, puedes modificar esto según el formato de respuesta de tu servidor
                            String mensaje = response.getString("mensaje");
                            txtResultados.setText("Carrera iniciada: " + mensaje);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void realizarSolicitudGET(String URL) {
        Log.d("URL", URL);  // Imprimir URL en los logs

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        txtResultados.setText("Progreso de la carrera: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void realizarSolicitudDELETE(String URL) {
        Log.d("URL", URL);  // Imprimir URL en los logs

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        txtResultados.setText("Carrera reiniciada.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
