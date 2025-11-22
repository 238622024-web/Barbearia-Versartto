package com.example.n2app_ex3_;

public class Appointment {
    private long id;
    private long userId;
    private String date;
    private String time;
    private String service;
    private String userName; // To show the user's name in the list

    public Appointment(long id, long userId, String date, String time, String service, String userName) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.service = service;
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getService() {
        return service;
    }

    public String getUserName() {
        return userName;
    }
}
