package com.example.n2app_ex3_;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.ViewHolder> {

    private final List<HistoricoItem> notificationList; // Reusing HistoricoItem as it has needed data
    private final OnActionListener actionListener;

    public interface OnActionListener {
        void onAccept(HistoricoItem item);
        void onDecline(HistoricoItem item);
    }

    public NotificacaoAdapter(List<HistoricoItem> notificationList, OnActionListener actionListener) {
        this.notificationList = notificationList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoricoItem item = notificationList.get(position);

        holder.userName.setText("Cliente: " + item.getService()); // We need to fetch the user name, will adjust this
        holder.appointmentDetails.setText("Agendamento: " + item.getDate() + " - " + item.getTime());
        holder.reason.setText("Motivo: \"" + item.getJustification() + "\"");

        holder.acceptButton.setOnClickListener(v -> actionListener.onAccept(item));
        holder.declineButton.setOnClickListener(v -> actionListener.onDecline(item));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, appointmentDetails, reason;
        Button acceptButton, declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewNotificacaoUser);
            appointmentDetails = itemView.findViewById(R.id.textViewNotificacaoAppointment);
            reason = itemView.findViewById(R.id.textViewNotificacaoReason);
            acceptButton = itemView.findViewById(R.id.btnAceitarCancelamento);
            declineButton = itemView.findViewById(R.id.btnRecusarCancelamento);
        }
    }
}
