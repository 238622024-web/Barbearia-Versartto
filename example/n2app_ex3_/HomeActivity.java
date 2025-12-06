package com.example.n2app_ex3_;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class HomeActivity extends AppCompatActivity {

    private long userId;
    private ImageView profileCardIcon;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bancoDados = new BancoDados(this);

        // Get user ID from session
        SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = userSession.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erro: Sessão inválida. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        profileCardIcon = findViewById(R.id.profile_card_icon);

        setupClickListeners();
        loadUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Reload data when returning to the activity
    }

    private void setupClickListeners() {
        findViewById(R.id.card_agendamento).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalendarioActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_profissionais).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfissionaisActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_galeria_cortes).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GaleriaActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_historia).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoriaActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_perfil).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_precos).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PrecosActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Clear user session
            SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = userSession.edit();
            editor.remove("user_id");
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserProfile() {
        SQLiteDatabase db = bancoDados.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"nome", "profile_image_uri"}, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            TextView profileCardName = findViewById(R.id.profile_card_name);
            TextView profileCardId = findViewById(R.id.profile_card_id);
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            profileCardName.setText(userName);
            profileCardId.setText("ID: " + userId);

            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_uri"));
            Uri imageUri = null;
            if (imageUriString != null) {
                imageUri = Uri.parse(imageUriString);
            }

            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person) // Show default icon if URI is null or loading fails
                    .into(profileCardIcon);
        }
        cursor.close();
    }
}
