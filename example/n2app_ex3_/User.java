package com.example.n2app_ex3_;

public class User {
    private long id;
    private String name;
    private String email;
    private String userType;

    public User(long id, String name, String email, String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
}
