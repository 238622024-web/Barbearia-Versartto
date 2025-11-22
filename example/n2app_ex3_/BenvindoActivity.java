package com.example.n2app_ex3_;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BenvindoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benvindo);

        Button btnProsseguir = findViewById(R.id.btn_prosseguir);
        btnProsseguir.setOnClickListener(v -> {
            Intent intent = new Intent(BenvindoActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
