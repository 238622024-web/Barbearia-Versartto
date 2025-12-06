package com.example.n2app_ex3_;

public class HistoricoItem {
    private final long appointmentId;
    private final String service;
    private final String date;
    private final String time;
    private final String justification;
    private final String professional;

    public HistoricoItem(long appointmentId, String service, String date, String time, String justification, String professional) {
        this.appointmentId = appointmentId;
        this.service = service;
        this.date = date;
        this.time = time;
        this.justification = justification;
        this.professional = professional;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public String getService() {
        return service;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getJustification() {
        return justification;
    }

    public String getProfessional() {
        return professional;
    }
}
