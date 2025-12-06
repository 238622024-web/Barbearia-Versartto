package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioAdminActivity extends AppCompatActivity implements AdminScheduleAdapter.OnItemClickListener {

    private CalendarView calendarViewAdmin;
    private RecyclerView recyclerViewAdminSchedules;
    private EditText editTextJustification;
    private Button buttonReschedule;
    private TextView textViewSelectedDate;

    private BancoDados bancoDados;
    private AdminScheduleAdapter adapter;
    private List<Appointment> dailyAppointments;

    private Appointment selectedAppointment = null;
    private String dateForReschedule = null;
    private String dateForLoadingList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_admin);

        bancoDados = new BancoDados(this);
        calendarViewAdmin = findViewById(R.id.calendarViewAdmin);
        recyclerViewAdminSchedules = findViewById(R.id.recyclerViewAdminSchedules);
        editTextJustification = findViewById(R.id.editTextJustification);
        buttonReschedule = findViewById(R.id.buttonReschedule);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        dailyAppointments = new ArrayList<>();
        adapter = new AdminScheduleAdapter(dailyAppointments, this);
        recyclerViewAdminSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdminSchedules.setAdapter(adapter);

        calendarViewAdmin.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String queryDate = formatDate(year, month, dayOfMonth, "yyyy-MM-dd");
            String displayDate = formatDate(year, month, dayOfMonth, "dd/MM/yyyy");
            textViewSelectedDate.setText("Agendamentos para: " + displayDate);

            if (selectedAppointment != null) {
                dateForReschedule = queryDate;
                Toast.makeText(this, "Nova data selecionada: " + displayDate, Toast.LENGTH_SHORT).show();
            } else {
                dateForLoadingList = queryDate;
                loadAppointmentsForDate(dateForLoadingList);
            }
        });

        buttonReschedule.setOnClickListener(v -> rescheduleAppointment());

        loadInitialDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCancellationRequests();
        if(dateForLoadingList != null) {
            loadAppointmentsForDate(dateForLoadingList);
        }
    }

    private void loadInitialDate() {
        long today = System.currentTimeMillis();
        calendarViewAdmin.setDate(today, true, true);
        Calendar cal = Calendar.getInstance();
        String initialDate = formatDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), "yyyy-MM-dd");
        String initialDisplayDate = formatDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), "dd/MM/yyyy");
        textViewSelectedDate.setText("Agendamentos para: " + initialDisplayDate);
        dateForLoadingList = initialDate;
        loadAppointmentsForDate(initialDate);
    }

    private void checkForCancellationRequests() {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            String query = "SELECT a.*, u.nome FROM appointments a JOIN users u ON a.user_id = u.user_id WHERE a.cancelamento_pendente = 1 LIMIT 1";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                long appointmentId = cursor.getLong(cursor.getColumnIndexOrThrow("appointment_id"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("data_agendamento"));
                String reason = cursor.getString(cursor.getColumnIndexOrThrow("justificativa_cancelamento"));

                runOnUiThread(() -> new AlertDialog.Builder(this)
                        .setTitle("Solicitação de Cancelamento")
                        .setMessage(userName + " solicitou o cancelamento do agendamento do dia " + date + ".\n\nMotivo: \"" + reason + "\"")
                        .setPositiveButton("Confirmar Cancelamento", (dialog, which) -> deleteAppointment(appointmentId))
                        .setNegativeButton("Manter Agendamento", (dialog, which) -> rejectCancellation(appointmentId))
                        .setCancelable(false)
                        .show());
            }
            cursor.close();
        }).start();
    }

    private void deleteAppointment(long appointmentId) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            int deletedRows = db.delete("appointments", "appointment_id = ?", new String[]{String.valueOf(appointmentId)});
            runOnUiThread(() -> {
                if (deletedRows > 0) {
                    Toast.makeText(this, "Agendamento cancelado e removido.", Toast.LENGTH_SHORT).show();
                    loadAppointmentsForDate(dateForLoadingList); // Refresh the view
                } else {
                    Toast.makeText(this, "Erro ao remover agendamento.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void rejectCancellation(long appointmentId) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("cancelamento_pendente", 0);
            values.putNull("justificativa_cancelamento");
            int updatedRows = db.update("appointments", values, "appointment_id = ?", new String[]{String.valueOf(appointmentId)});
            runOnUiThread(() -> {
                if (updatedRows > 0) {
                    Toast.makeText(this, "Solicitação de cancelamento recusada.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void loadAppointmentsForDate(String date) {
        new Thread(() -> {
            List<Appointment> appointments = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            String query = "SELECT a.*, u.nome FROM appointments a JOIN users u ON a.user_id = u.user_id WHERE a.data_agendamento = ? ORDER BY a.hora_agendamento ASC";
            Cursor cursor = db.rawQuery(query, new String[]{date});

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("appointment_id"));
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                    String service = cursor.getString(cursor.getColumnIndexOrThrow("servico"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("hora_agendamento"));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                    String professional = cursor.getString(cursor.getColumnIndexOrThrow("profissional"));
                    appointments.add(new Appointment(id, userId, date, time, service, userName, professional));
                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                dailyAppointments.clear();
                dailyAppointments.addAll(appointments);
                adapter.notifyDataSetChanged();
                adapter.setSelectedPosition(-1);
                selectedAppointment = null;
            });
        }).start();
    }

    @Override
    public void onItemClick(Appointment appointment) {
        this.selectedAppointment = appointment;
        Toast.makeText(this, "Agendamento de " + appointment.getUserName() + " selecionado. Escolha a nova data no calendário.", Toast.LENGTH_SHORT).show();
    }

    private void rescheduleAppointment() {
        String justification = editTextJustification.getText().toString().trim();

        if (selectedAppointment == null) {
            Toast.makeText(this, "Selecione um agendamento para reagendar.", Toast.LENGTH_LONG).show();
            return;
        }
        if (dateForReschedule == null) {
            Toast.makeText(this, "Selecione uma NOVA data no calendário.", Toast.LENGTH_LONG).show();
            return;
        }
        if (dateForReschedule.equals(selectedAppointment.getDate())) {
            Toast.makeText(this, "A nova data não pode ser a mesma da original.", Toast.LENGTH_LONG).show();
            return;
        }
        if (justification.isEmpty()) {
            Toast.makeText(this, "Escreva uma justificativa para o reagendamento.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("data_agendamento", dateForReschedule);
            values.put("justificativa", justification);
            values.put("notificacao_pendente", 1);

            int rows = db.update("appointments", values, "appointment_id = ?", new String[]{String.valueOf(selectedAppointment.getId())});

            runOnUiThread(() -> {
                if (rows > 0) {
                    Toast.makeText(CalendarioAdminActivity.this, "Agendamento reagendado com sucesso!", Toast.LENGTH_LONG).show();
                    editTextJustification.setText("");
                    selectedAppointment = null;
                    dateForReschedule = null;
                    adapter.setSelectedPosition(-1);
                    loadAppointmentsForDate(dateForLoadingList);
                } else {
                    Toast.makeText(CalendarioAdminActivity.this, "Falha ao reagendar.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String formatDate(int year, int month, int day, @NonNull String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
