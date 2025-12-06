package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PerfilAdminActivity extends AppCompatActivity {

    private static final String TAG = "PerfilAdminActivity";

    private ShapeableImageView profileImageAdmin;
    private Button btnChangePhotoAdmin, btnSelectVacationDate, btnLogout;
    private TextView textViewWelcomeAdmin, textViewSelectedVacation;
    private long userId;
    private BancoDados bancoDados;

    private final ActivityResultLauncher<String[]> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        Log.d(TAG, "Image URI selected: " + uri.toString());
                        Glide.with(this).load(uri).into(profileImageAdmin);
                        updateProfileImageInDatabase(uri.toString());
                        Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to select image", e);
                        Toast.makeText(this, "Falha ao selecionar a imagem.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_admin);

        userId = getIntent().getLongExtra("USER_ID", -1);
        bancoDados = new BancoDados(this);

        profileImageAdmin = findViewById(R.id.profile_image_admin);
        btnChangePhotoAdmin = findViewById(R.id.btn_change_photo_admin);
        btnSelectVacationDate = findViewById(R.id.btn_select_vacation_date);
        btnLogout = findViewById(R.id.btn_perfil_admin_logout);
        textViewWelcomeAdmin = findViewById(R.id.textViewWelcomeAdmin);
        textViewSelectedVacation = findViewById(R.id.textViewSelectedVacation);

        setupClickListeners();
        loadAdminProfile();
    }

    private void loadAdminProfile() {
        Log.d(TAG, "Loading admin profile for userId: " + userId);
        SQLiteDatabase db = bancoDados.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"nome", "profile_image_uri"}, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            String adminName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            textViewWelcomeAdmin.setText("Bem-vindo, " + adminName);

            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_uri"));
            Log.d(TAG, "Loaded image URI from DB: " + imageUriString);
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                Glide.with(this).load(imageUri).placeholder(R.drawable.ic_person).error(R.drawable.ic_person).into(profileImageAdmin);
            } else {
                Log.d(TAG, "Image URI is null, using default drawable");
                profileImageAdmin.setImageResource(R.drawable.ic_person);
            }
        }
        cursor.close();
    }

    private void updateProfileImageInDatabase(String imageUri) {
        Log.d(TAG, "Updating profile image in DB for userId: " + userId + " with URI: " + imageUri);
        SQLiteDatabase db = bancoDados.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profile_image_uri", imageUri);
        int rowsAffected = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Rows affected: " + rowsAffected);
    }

    private void setupClickListeners() {
        btnChangePhotoAdmin.setOnClickListener(v -> pickImageLauncher.launch(new String[]{"image/*"}));

        btnSelectVacationDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Selecione o Período de Férias");
            final MaterialDatePicker<androidx.core.util.Pair<Long, Long>> datePicker = builder.build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String startDate = sdf.format(selection.first);
                String endDate = sdf.format(selection.second);
                textViewSelectedVacation.setText("Férias de: " + startDate + " a " + endDate);
                // TODO: Save vacation dates to the database
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = userSession.edit();
            editor.remove("user_id");
            editor.apply();

            Intent intent = new Intent(PerfilAdminActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
