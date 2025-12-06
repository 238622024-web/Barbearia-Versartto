package com.example.n2app_ex3_;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class PrecosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precos);

        MaterialToolbar toolbar = findViewById(R.id.toolbarPrecos);
        toolbar.setNavigationOnClickListener(v -> finish()); // Handle back button click
    }
}
