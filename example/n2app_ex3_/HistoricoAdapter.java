package com.example.n2app_ex3_;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoricoAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;
    private SharedPreferences sharedPreferences;

    public HistoricoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.sharedPreferences = context.getSharedPreferences(CalendarioActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String agendamento = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewAgendamento = convertView.findViewById(R.id.textViewAgendamento);
        Button btnCancelar = convertView.findViewById(R.id.btnCancelar);

        textViewAgendamento.setText(agendamento);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> agendamentos = sharedPreferences.getStringSet(CalendarioActivity.AGENDAMENTOS_KEY, new HashSet<String>());
                agendamentos.remove(agendamento);
                sharedPreferences.edit().putStringSet(CalendarioActivity.AGENDAMENTOS_KEY, agendamentos).apply();
                remove(agendamento);
                notifyDataSetChanged();
                Toast.makeText(mContext, "Agendamento cancelado.", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}