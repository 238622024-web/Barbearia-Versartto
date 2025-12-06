package com.example.n2app_ex3_;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AdminFeedActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewFeed;
    private Button buttonChangeImage, buttonSaveChanges;
    private EditText editTextFeedTitle, editTextFeedDate, editTextFeedDescription;

    private BancoDados bancoDados;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feed);

        bancoDados = new BancoDados(this);

        imageViewFeed = findViewById(R.id.imageViewFeed);
        buttonChangeImage = findViewById(R.id.buttonChangeImage);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        editTextFeedTitle = findViewById(R.id.editTextFeedTitle);
        editTextFeedDate = findViewById(R.id.editTextFeedDate);
        editTextFeedDescription = findViewById(R.id.editTextFeedDescription);

        loadFeedData();

        editTextFeedDate.setOnClickListener(v -> showDatePickerDialog());
        buttonChangeImage.setOnClickListener(v -> openFileChooser());
        buttonSaveChanges.setOnClickListener(v -> saveFeedData());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    editTextFeedDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewFeed.setImageURI(imageUri);
        }
    }

    private void loadFeedData() {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            Cursor cursor = db.query("feed_noticias", null, "feed_id = ?", new String[]{"1"}, null, null, null);

            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("data"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                String uriString = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                runOnUiThread(() -> {
                    editTextFeedTitle.setText(title);
                    editTextFeedDate.setText(date);
                    editTextFeedDescription.setText(description);
                    if (uriString != null && !uriString.isEmpty()) {
                        imageUri = Uri.parse(uriString);
                        imageViewFeed.setImageURI(imageUri);
                    }
                });
            }
            cursor.close();
        }).start();
    }

    private void saveFeedData() {
        new Thread(() -> {
            String title = editTextFeedTitle.getText().toString();
            String date = editTextFeedDate.getText().toString();
            String description = editTextFeedDescription.getText().toString();

            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("titulo", title);
            values.put("data", date);
            values.put("descricao", description);
            if (imageUri != null) {
                values.put("image_uri", imageUri.toString());
            }

            int rows = db.update("feed_noticias", values, "feed_id = ?", new String[]{"1"});

            runOnUiThread(() -> {
                if (rows > 0) {
                    Toast.makeText(AdminFeedActivity.this, "Feed atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminFeedActivity.this, "Erro ao atualizar o feed.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
