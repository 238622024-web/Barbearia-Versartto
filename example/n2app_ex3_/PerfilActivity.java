package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

public class PerfilActivity extends AppCompatActivity {

    private ShapeableImageView profileImage;
    private Button btnChangePhoto, btnLogout, btnAlterarCadastro;
    private TextView textViewWelcomeUser;

    private BancoDados bancoDados;
    private long userId;

    private final ActivityResultLauncher<String[]> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        // Take persistable URI permission
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        // Use Glide to load the image
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(profileImage);

                        // Save the image URI to the database
                        updateProfileImageInDatabase(uri.toString());
                        Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Falha ao selecionar a imagem.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        bancoDados = new BancoDados(this);

        // Get user ID from session
        SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = userSession.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erro: Sessão inválida. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        profileImage = findViewById(R.id.profile_image);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
        btnLogout = findViewById(R.id.btn_perfil_logout);
        btnAlterarCadastro = findViewById(R.id.btn_alterar_cadastro);
        textViewWelcomeUser = findViewById(R.id.textViewWelcomeUser);

        loadUserProfile();
        setupClickListeners();
    }

    private void loadUserProfile() {
        SQLiteDatabase db = bancoDados.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"nome", "profile_image_uri"}, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            textViewWelcomeUser.setText("Bem-vindo, " + userName);

            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_uri"));
            Uri imageUri = null;
            if (imageUriString != null) {
                imageUri = Uri.parse(imageUriString);
            }

            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person) // Show default icon if URI is null or loading fails
                    .into(profileImage);
        }
        cursor.close();
    }

    private void updateProfileImageInDatabase(String imageUri) {
        SQLiteDatabase db = bancoDados.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profile_image_uri", imageUri);
        db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    private void setupClickListeners() {
        btnChangePhoto.setOnClickListener(v -> {
            pickImageLauncher.launch(new String[]{"image/*"});
        });

        btnAlterarCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, AlteracaoActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear user session
            SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = userSession.edit();
            editor.remove("user_id");
            editor.apply();

            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
