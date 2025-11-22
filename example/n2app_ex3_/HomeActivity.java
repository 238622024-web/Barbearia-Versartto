package com.example.n2app_ex3_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton btnAgendamento;
    private MaterialButton btnProfissionais;
    private MaterialButton btnGaleriaCortes;
    private MaterialButton btnPrecos;
    private MaterialButton btnHistoria;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAgendamento = findViewById(R.id.btn_agendamento);
        btnProfissionais = findViewById(R.id.btn_profissionais);
        btnGaleriaCortes = findViewById(R.id.btn_galeria_cortes);
        btnPrecos = findViewById(R.id.btn_precos);
        btnHistoria = findViewById(R.id.btn_historia);
        btnLogout = findViewById(R.id.btn_logout);

        btnAgendamento.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalendarioActivity.class);
            startActivity(intent);
        });

        btnHistoria.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoriaActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
