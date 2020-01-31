package com.thebiglosers.phix.model;

import com.google.gson.annotations.SerializedName;

public class User {

    String username;

    String imageString;

    @SerializedName("get_full_name")
    String fullName;

    public User(String fullName, String imageString) {
        this.fullName = fullName;
        this.imageString = imageString;
    }

    public String getName() {
        return username;
    }

    public String getImageString() {
        return imageString;
    }

    public String getFullName () { return fullName; }
}
