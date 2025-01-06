package com.example.clientesimulacion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etNumeroCorredores, etDistancia;
    private Button btnIniciarCarrera, btnVerProgreso, btnReiniciar;
    private TextView tvResultados;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los componentes de la interfaz
        etNumeroCorredores = findViewById(R.id.etNumeroCorredores);
        etDistancia = findViewById(R.id.etDistancia);
        btnIniciarCarrera = findViewById(R.id.btnIniciarCarrera);
        btnVerProgreso = findViewById(R.id.btnVerProgreso);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        tvResultados = findViewById(R.id.tvResultados);

        // Configuración de Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // URL del servidor
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Acciones de los botones
        btnIniciarCarrera.setOnClickListener(this::iniciarCarrera);
        btnVerProgreso.setOnClickListener(this::verProgreso);
        btnReiniciar.setOnClickListener(this::reiniciarCarrera);
    }

    private void iniciarCarrera(View view) {
        int numeroCorredores = Integer.parseInt(etNumeroCorredores.getText().toString());
        int distancia = Integer.parseInt(etDistancia.getText().toString());

        Call<CarreraResponse> call = apiService.iniciarCarrera(new CarreraRequest(numeroCorredores, distancia));
        call.enqueue(new Callback<CarreraResponse>() {
            @Override
            public void onResponse(Call<CarreraResponse> call, Response<CarreraResponse> response) {
                if (response.isSuccessful()) {
                    CarreraResponse carreraResponse = response.body();
                    tvResultados.setText("Carrera iniciada con " + carreraResponse.getCorredores().size() + " corredores.");
                } else {
                    Toast.makeText(MainActivity.this, "Error al iniciar la carrera", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarreraResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verProgreso(View view) {
        Call<CarreraResponse> call = apiService.verProgreso();
        call.enqueue(new Callback<CarreraResponse>() {
            @Override
            public void onResponse(Call<CarreraResponse> call, Response<CarreraResponse> response) {
                if (response.isSuccessful()) {
                    CarreraResponse carreraResponse = response.body();
                    StringBuilder progreso = new StringBuilder();
                    for (Corredor corredor : carreraResponse.getCorredores()) {
                        progreso.append("Corredor ")
                                .append(corredor.getId())
                                .append(": ")
                                .append(corredor.getPosicion())
                                .append(" km\n");
                    }
                    tvResultados.setText(progreso.toString());
                } else {
                    Toast.makeText(MainActivity.this, "No hay carrera en progreso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarreraResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reiniciarCarrera(View view) {
        Call<Void> call = apiService.reiniciarCarrera();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tvResultados.setText("Carrera reiniciada");
                } else {
                    Toast.makeText(MainActivity.this, "Error al reiniciar la carrera", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
