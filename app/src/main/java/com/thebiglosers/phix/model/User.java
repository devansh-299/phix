package com.thebiglosers.phix.model;

import com.google.gson.annotations.SerializedName;

public class User {

    String username;

    @SerializedName("image_string")
    String imageString;

    @SerializedName("get_full_name")
    String fullName;

    @SerializedName("email")
    String email;

    @SerializedName("upi_id")
    String upiString;

    public User(String fullName, String imageString, String email, String upiString) {
        this.fullName = fullName;
        this.imageString = imageString;
        this.email = email;
        this.upiString = upiString;
    }

    public String getName() {
        return username;
    }

    public String getImageString() {
        return imageString;
    }

    public String getFullName() { return fullName; }

    public String getUpiString() { return upiString; }
}
