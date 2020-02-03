package com.thebiglosers.phix.model;

public class Transaction {

    Float amount;
    String description;
    String source;
    String date;
    String destination;

    public Transaction(Float amount, String description, String date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }


}
