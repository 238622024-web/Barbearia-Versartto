package com.example.n2app_ex3_;

public class User {
    private long id;
    private String name;
    private String userType;
    private String email;
    private String profileImageUri;

    public User(long id, String name, String userType, String email, String profileImageUri) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.email = email;
        this.profileImageUri = profileImageUri;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }
}
