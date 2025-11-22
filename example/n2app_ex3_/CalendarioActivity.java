package com.example.n2app_ex3_;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class CalendarioActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TimePicker timePicker;
    private Spinner spinnerEspecialidades;
    private Button btnAgendar;
    private Button btnHistorico;
    private SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "AgendamentosPrefs";
    public static final String AGENDAMENTOS_KEY = "agendamentos";
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        spinnerEspecialidades = findViewById(R.id.spinnerEspecialidades);
        btnAgendar = findViewById(R.id.btn_agendar);
        btnHistorico = findViewById(R.id.btn_historico);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.especialidades_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerEspecialidades.setAdapter(adapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate == null) {
                    Toast.makeText(CalendarioActivity.this, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String time = String.format("%02d:%02d", hour, minute);
                String especialidade = spinnerEspecialidades.getSelectedItem().toString();

                String agendamento = selectedDate + " às " + time + " - " + especialidade;

                new AlertDialog.Builder(CalendarioActivity.this)
                        .setTitle("Confirmar Agendamento")
                        .setMessage("Deseja confirmar o agendamento para " + agendamento + "?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                salvarAgendamento(agendamento);
                                Toast.makeText(CalendarioActivity.this, "Agendamento confirmado!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarioActivity.this, HistoricoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void salvarAgendamento(String agendamento) {
        Set<String> agendamentos = sharedPreferences.getStringSet(AGENDAMENTOS_KEY, new HashSet<String>());
        agendamentos.add(agendamento);
        sharedPreferences.edit().putStringSet(AGENDAMENTOS_KEY, agendamentos).apply();
    }
}
