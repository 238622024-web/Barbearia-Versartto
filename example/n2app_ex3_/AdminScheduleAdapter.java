package com.example.n2app_ex3_;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminScheduleAdapter extends RecyclerView.Adapter<AdminScheduleAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    public AdminScheduleAdapter(List<Appointment> appointments, OnItemClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment, listener, position);

        // Highlight the selected item
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#D3D3D3")); // Light Gray
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userService, userTime, userProfessional;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewAdminItemUserName);
            userService = itemView.findViewById(R.id.textViewAdminItemService);
            userTime = itemView.findViewById(R.id.textViewAdminItemTime);
            userProfessional = itemView.findViewById(R.id.textViewAdminItemProfessional);
            cardView = (CardView) itemView;
        }

        public void bind(final Appointment appointment, final OnItemClickListener listener, final int position) {
            userName.setText(appointment.getUserName());
            userService.setText(appointment.getService());
            userTime.setText(appointment.getTime());
            userProfessional.setText("com: " + appointment.getProfessional());

            itemView.setOnClickListener(v -> {
                listener.onItemClick(appointment);
                setSelectedPosition(position);
            });
        }
    }
}
