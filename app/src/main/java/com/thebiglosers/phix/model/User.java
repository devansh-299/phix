package com.thebiglosers.phix.model;

public class User {

    String name;
    String imageString;

    public User(String name, String imageString) {
        this.name = name;
        this.imageString = imageString;
    }

    public String getName() {
        return name;
    }

    public String getImageString() {
        return imageString;
    }
}
