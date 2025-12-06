package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TimePicker timePicker;
    private Spinner spinnerEspecialidades;
    private Spinner spinnerProfissionais;
    private Button btnAgendar;
    private Button btnHistorico;

    private BancoDados bancoDados;
    private long userId;
    private String selectedDateForDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        bancoDados = new BancoDados(this);

        // Get user ID from session
        SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = userSession.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erro: Sessão inválida. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        spinnerEspecialidades = findViewById(R.id.spinnerEspecialidades);
        spinnerProfissionais = findViewById(R.id.spinnerProfissionais);
        btnAgendar = findViewById(R.id.btn_agendar);
        btnHistorico = findViewById(R.id.btn_historico);

        ArrayAdapter<CharSequence> especialidadesAdapter = ArrayAdapter.createFromResource(this,
                R.array.especialidades_array, R.layout.custom_spinner_item);
        especialidadesAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerEspecialidades.setAdapter(especialidadesAdapter);

        ArrayAdapter<CharSequence> profissionaisAdapter = ArrayAdapter.createFromResource(this,
                R.array.profissionais_array, R.layout.custom_spinner_item);
        profissionaisAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerProfissionais.setAdapter(profissionaisAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateForDb = formatDateForDb(year, month, dayOfMonth);
        });

        // Set initial date
        Calendar cal = Calendar.getInstance();
        selectedDateForDb = formatDateForDb(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        btnAgendar.setOnClickListener(v -> {
            if (selectedDateForDb == null) {
                Toast.makeText(CalendarioActivity.this, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show();
                return;
            }

            String especialidade = spinnerEspecialidades.getSelectedItem().toString();
            if (especialidade.equals("Escolha o Serviço")) {
                Toast.makeText(CalendarioActivity.this, "Por favor, escolha um serviço.", Toast.LENGTH_SHORT).show();
                return;
            }

            String profissional = spinnerProfissionais.getSelectedItem().toString();
            if (profissional.equals("Escolha o Profissional")) {
                Toast.makeText(CalendarioActivity.this, "Por favor, escolha um profissional.", Toast.LENGTH_SHORT).show();
                return;
            }

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            String confirmationMessage = "Confirmar agendamento para " + selectedDateForDb + " às " + time + " com " + profissional + " - " + especialidade + "?";

            new AlertDialog.Builder(CalendarioActivity.this)
                    .setTitle("Confirmar Agendamento")
                    .setMessage(confirmationMessage)
                    .setPositiveButton("Confirmar", (dialog, which) -> {
                        salvarAgendamentoNoDb(selectedDateForDb, time, especialidade, profissional);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarioActivity.this, HistoricoActivity.class);
            startActivity(intent);
        });
    }

    private String formatDateForDb(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void salvarAgendamentoNoDb(String date, String time, String service, String professional) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("data_agendamento", date);
            values.put("hora_agendamento", time);
            values.put("servico", service);
            values.put("profissional", professional);
            values.put("notificacao_pendente", 0); // Not pending by default

            long newRowId = db.insert("appointments", null, values);

            runOnUiThread(() -> {
                if (newRowId != -1) {
                    Toast.makeText(this, "Agendamento confirmado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erro ao salvar agendamento.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
