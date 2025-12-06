package com.example.n2app_ex3_;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private final List<HistoricoItem> historicoItems;
    private final Context context;
    private final OnCancelListener cancelListener;

    public interface OnCancelListener {
        void onCancelRequested(HistoricoItem item, String reason);
    }

    public HistoricoAdapter(Context context, List<HistoricoItem> historicoItems, OnCancelListener cancelListener) {
        this.context = context;
        this.historicoItems = historicoItems;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoricoItem item = historicoItems.get(position);

        holder.textViewService.setText(item.getService());
        holder.textViewProfessional.setText("com: " + item.getProfessional());
        holder.textViewDateTime.setText(item.getDate() + " às " + item.getTime());

        // Show status if appointment was rescheduled by admin
        if (item.getJustification() != null && !item.getJustification().isEmpty()) {
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.textViewStatus.setText("Reagendado");
            holder.textViewStatus.setOnClickListener(v -> {
                 new AlertDialog.Builder(context)
                        .setTitle("Detalhes do Reagendamento")
                        .setMessage("Motivo: " + item.getJustification())
                        .setPositiveButton("OK", null)
                        .show();
            });
        } else {
            holder.textViewStatus.setVisibility(View.GONE);
        }

        // Setup cancellation button
        holder.btnCancelar.setOnClickListener(v -> {
            showCancellationDialog(item);
        });
    }

    private void showCancellationDialog(HistoricoItem item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_cancel_appointment, null);
        final TextInputEditText reasonInput = dialogView.findViewById(R.id.editTextCancellationReason);

        new AlertDialog.Builder(context)
                .setTitle("Solicitar Cancelamento")
                .setView(dialogView)
                .setPositiveButton("Enviar Solicitação", (dialog, which) -> {
                    String reason = reasonInput.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(context, "Por favor, informe um motivo para o cancelamento.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (cancelListener != null) {
                            cancelListener.onCancelRequested(item, reason);
                        }
                    }
                })
                .setNegativeButton("Voltar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return historicoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewService, textViewDateTime, textViewStatus, textViewProfessional;
        Button btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewService = itemView.findViewById(R.id.textViewAgendamentoServico);
            textViewProfessional = itemView.findViewById(R.id.textViewAgendamentoProfissional);
            textViewDateTime = itemView.findViewById(R.id.textViewAgendamentoDataHora);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            btnCancelar = itemView.findViewById(R.id.btnCancelarAgendamento);
        }
    }
}
