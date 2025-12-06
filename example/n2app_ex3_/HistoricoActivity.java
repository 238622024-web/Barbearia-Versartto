package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity implements HistoricoAdapter.OnCancelListener {

    private RecyclerView recyclerViewHistorico;
    private Button btnLimparHistorico;
    private BancoDados bancoDados;
    private HistoricoAdapter adapter;
    private List<HistoricoItem> historicoItems;
    private long userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        bancoDados = new BancoDados(this);

        // Get user ID from session
        SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = userSession.getLong("user_id", -1);

        recyclerViewHistorico = findViewById(R.id.recyclerViewHistorico);
        btnLimparHistorico = findViewById(R.id.btnLimparHistorico);

        setupRecyclerView();
        loadHistoryFromDatabase();

        btnLimparHistorico.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Limpar Histórico")
                    .setMessage("Deseja apagar todos os seus agendamentos? Esta ação não pode ser desfeita.")
                    .setPositiveButton("Confirmar", (dialog, which) -> clearHistory())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void setupRecyclerView() {
        historicoItems = new ArrayList<>();
        adapter = new HistoricoAdapter(this, historicoItems, this);
        recyclerViewHistorico.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistorico.setAdapter(adapter);
    }

    private void loadHistoryFromDatabase() {
        if (userId == -1) {
            Toast.makeText(this, "ID do usuário não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            List<HistoricoItem> items = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            Cursor cursor = db.query("appointments", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "data_agendamento DESC");

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("appointment_id"));
                    String service = cursor.getString(cursor.getColumnIndexOrThrow("servico"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("data_agendamento"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("hora_agendamento"));
                    String justification = cursor.getString(cursor.getColumnIndexOrThrow("justificativa"));
                    String professional = cursor.getString(cursor.getColumnIndexOrThrow("profissional"));
                    items.add(new HistoricoItem(id, service, date, time, justification, professional));
                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                historicoItems.clear();
                historicoItems.addAll(items);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onCancelRequested(HistoricoItem item, String reason) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("justificativa_cancelamento", reason);
            values.put("cancelamento_pendente", 1);

            int rows = db.update("appointments", values, "appointment_id = ?", new String[]{String.valueOf(item.getAppointmentId())});

            runOnUiThread(() -> {
                if (rows > 0) {
                    Toast.makeText(this, "Solicitação de cancelamento enviada.", Toast.LENGTH_SHORT).show();
                    loadHistoryFromDatabase(); // Refresh the list
                } else {
                    Toast.makeText(this, "Erro ao enviar solicitação.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void clearHistory() {
        if (userId == -1) return;
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            db.delete("appointments", "user_id = ?", new String[]{String.valueOf(userId)});
            runOnUiThread(() -> {
                historicoItems.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Histórico limpo.", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
