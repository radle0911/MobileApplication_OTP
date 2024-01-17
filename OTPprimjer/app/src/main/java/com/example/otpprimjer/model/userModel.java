package com.example.otpprimjer.model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class userModel {
    private String phone;
    private String username;
    private Timestamp timestam_created_profile;

    public userModel() {
    }

    public userModel(String phone, String username, Timestamp timestam_created_profile) {
        this.phone = phone;
        this.username = username;
        this.timestam_created_profile = timestam_created_profile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestam_created_profile() {
        return timestam_created_profile;
    }

    public void setTimestam_created_profile(Timestamp timestam_created_profile) {
        this.timestam_created_profile = timestam_created_profile;
    }
}
