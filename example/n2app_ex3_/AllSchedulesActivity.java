package com.example.n2app_ex3_;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllSchedulesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAllSchedules;
    private ScheduleAdapter scheduleAdapter;
    private List<Appointment> appointmentList;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedules);

        bancoDados = new BancoDados(this);
        recyclerViewAllSchedules = findViewById(R.id.recyclerViewAllSchedules);
        recyclerViewAllSchedules.setLayoutManager(new LinearLayoutManager(this));

        appointmentList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(appointmentList);
        recyclerViewAllSchedules.setAdapter(scheduleAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedulesFromDatabase();
    }

    private void loadSchedulesFromDatabase() {
        new Thread(() -> {
            List<Appointment> appointments = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();

            final String query = "SELECT a.*, u.nome FROM appointments a INNER JOIN users u ON a.user_id = u.user_id ORDER BY a.data_agendamento, a.hora_agendamento";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("appointment_id"));
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("data_agendamento"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("hora_agendamento"));
                    String service = cursor.getString(cursor.getColumnIndexOrThrow("servico"));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                    String professional = cursor.getString(cursor.getColumnIndexOrThrow("profissional"));
                    appointments.add(new Appointment(id, userId, date, time, service, userName, professional));
                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                appointmentList.clear();
                appointmentList.addAll(appointments);
                scheduleAdapter.notifyDataSetChanged();
            });
        }).start();
    }
}
