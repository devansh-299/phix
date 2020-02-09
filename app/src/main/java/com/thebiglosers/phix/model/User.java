package com.thebiglosers.phix.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

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

    protected User(Parcel in) {
        username = in.readString();
        imageString = in.readString();
        fullName = in.readString();
        email = in.readString();
        upiString = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserName() {
        return username;
    }

    public String getImageString() {
        return imageString;
    }

    public String getFullName() { return fullName; }

    public String getUpiString() { return upiString; }
    
    public String getEmail() { return email; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(imageString);
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeString(upiString);
    }
}
