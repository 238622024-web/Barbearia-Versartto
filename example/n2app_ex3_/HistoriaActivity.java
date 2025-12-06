package com.example.n2app_ex3_;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HistoriaActivity extends AppCompatActivity {

    private ImageView imageViewNoticia;
    private TextView textViewNoticiaData, textViewNoticiaTitulo, textViewNoticiaDescricao;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);

        bancoDados = new BancoDados(this);

        imageViewNoticia = findViewById(R.id.imageViewNoticia);
        textViewNoticiaData = findViewById(R.id.textViewNoticiaData);
        textViewNoticiaTitulo = findViewById(R.id.textViewNoticiaTitulo);
        textViewNoticiaDescricao = findViewById(R.id.textViewNoticiaDescricao);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeedData();
    }

    private void loadFeedData() {
        new Thread(() -> {
            Cursor cursor = null;
            try {
                SQLiteDatabase db = bancoDados.getReadableDatabase();
                cursor = db.query("feed_noticias", null, "feed_id = ?", new String[]{"1"}, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    final String title = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    final String date = cursor.getString(cursor.getColumnIndexOrThrow("data"));
                    final String description = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                    final String uriString = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                    runOnUiThread(() -> {
                        textViewNoticiaTitulo.setText(title);
                        textViewNoticiaData.setText(date);
                        textViewNoticiaDescricao.setText(description);
                        if (uriString != null && !uriString.isEmpty()) {
                            imageViewNoticia.setImageURI(Uri.parse(uriString));
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(HistoriaActivity.this, "Nenhuma notícia encontrada.", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("HistoriaActivity", "Erro ao carregar feed de notícias", e);
                runOnUiThread(() -> {
                    Toast.makeText(HistoriaActivity.this, "Erro ao carregar notícias.", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }
}
