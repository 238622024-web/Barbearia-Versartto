package com.example.n2app_ex3_;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Appointment> appointmentList;

    public ScheduleAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Appointment currentAppointment = appointmentList.get(position);
        holder.textViewUserName.setText(currentAppointment.getUserName());
        holder.textViewService.setText(currentAppointment.getService());
        holder.textViewProfessional.setText("Profissional: " + currentAppointment.getProfessional());
        String dateTime = currentAppointment.getDate() + " - " + currentAppointment.getTime();
        holder.textViewDateTime.setText(dateTime);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewService, textViewDateTime, textViewProfessional;

        ScheduleViewHolder(View view) {
            super(view);
            textViewUserName = view.findViewById(R.id.textViewItemUserName);
            textViewService = view.findViewById(R.id.textViewItemService);
            textViewDateTime = view.findViewById(R.id.textViewItemDateTime);
            textViewProfessional = view.findViewById(R.id.textViewItemProfessional);
        }
    }
}
