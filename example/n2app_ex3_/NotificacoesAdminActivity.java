package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificacoesAdminActivity extends AppCompatActivity implements NotificacaoAdapter.OnActionListener {

    private RecyclerView recyclerViewNotificacoes;
    private NotificacaoAdapter adapter;
    private List<HistoricoItem> notificationList;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes_admin);

        bancoDados = new BancoDados(this);
        recyclerViewNotificacoes = findViewById(R.id.recyclerViewNotificacoes);
        recyclerViewNotificacoes.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        adapter = new NotificacaoAdapter(notificationList, this);
        recyclerViewNotificacoes.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCancellationRequests();
    }

    private void loadCancellationRequests() {
        new Thread(() -> {
            List<HistoricoItem> items = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();

            String query = "SELECT a.*, u.nome FROM appointments a JOIN users u ON a.user_id = u.user_id WHERE a.cancelamento_pendente = 1 ORDER BY a.data_agendamento ASC";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    long appointmentId = cursor.getLong(cursor.getColumnIndexOrThrow("appointment_id"));
                    String service = cursor.getString(cursor.getColumnIndexOrThrow("servico"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("data_agendamento"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("hora_agendamento"));
                    String cancellationReason = cursor.getString(cursor.getColumnIndexOrThrow("justificativa_cancelamento"));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome")); // User name from the join
                    String professional = cursor.getString(cursor.getColumnIndexOrThrow("profissional"));

                    // Using userName for the service field as per original logic, and adding the professional
                    items.add(new HistoricoItem(appointmentId, userName, date, time, cancellationReason, professional));

                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                notificationList.clear();
                notificationList.addAll(items);
                adapter.notifyDataSetChanged();

                if (notificationList.isEmpty()) {
                    Toast.makeText(this, "Nenhuma solicitação de cancelamento.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onAccept(HistoricoItem item) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            int deletedRows = db.delete("appointments", "appointment_id = ?", new String[]{String.valueOf(item.getAppointmentId())});

            runOnUiThread(() -> {
                if (deletedRows > 0) {
                    Toast.makeText(this, "Cancelamento aceito. O agendamento foi removido.", Toast.LENGTH_SHORT).show();
                    loadCancellationRequests(); // Refresh the list
                } else {
                    Toast.makeText(this, "Erro ao aceitar o cancelamento.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onDecline(HistoricoItem item) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("cancelamento_pendente", 0);
            values.putNull("justificativa_cancelamento");

            int updatedRows = db.update("appointments", values, "appointment_id = ?", new String[]{String.valueOf(item.getAppointmentId())});

            runOnUiThread(() -> {
                if (updatedRows > 0) {
                    Toast.makeText(this, "Cancelamento recusado.", Toast.LENGTH_SHORT).show();
                    loadCancellationRequests(); // Refresh the list
                } else {
                    Toast.makeText(this, "Erro ao recusar o cancelamento.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
