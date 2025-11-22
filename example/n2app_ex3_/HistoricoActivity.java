package com.example.n2app_ex3_;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

public class HistoricoActivity extends AppCompatActivity {

    private ListView listViewHistorico;
    private Button btnLimparHistorico;
    private SharedPreferences sharedPreferences;
    private HistoricoAdapter adapter;
    private ArrayList<String> agendamentosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        listViewHistorico = findViewById(R.id.listViewHistorico);
        btnLimparHistorico = findViewById(R.id.btnLimparHistorico);
        sharedPreferences = getSharedPreferences(CalendarioActivity.PREFS_NAME, Context.MODE_PRIVATE);

        agendamentosList = new ArrayList<>(sharedPreferences.getStringSet(CalendarioActivity.AGENDAMENTOS_KEY, new HashSet<String>()));
        adapter = new HistoricoAdapter(this, R.layout.item_historico, agendamentosList);
        listViewHistorico.setAdapter(adapter);

        listViewHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String agendamento = agendamentosList.get(position);
                new AlertDialog.Builder(HistoricoActivity.this)
                        .setTitle("Detalhes do Agendamento")
                        .setMessage(agendamento)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        btnLimparHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HistoricoActivity.this)
                        .setTitle("Limpar Histórico")
                        .setMessage("Deseja realmente limpar todo o histórico de agendamentos?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(CalendarioActivity.AGENDAMENTOS_KEY);
                                editor.apply();
                                agendamentosList.clear();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(HistoricoActivity.this, "Histórico de agendamentos limpo.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }
}