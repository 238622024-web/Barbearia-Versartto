package com.example.n2app_ex3_;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);

        ImageView imageViewBarbearia = findViewById(R.id.imageViewBarbearia);
        imageViewBarbearia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoriaActivity.this, BottonActivity.class);
                startActivity(intent);
            }
        });
    }
}
