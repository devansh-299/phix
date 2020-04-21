package com.thebiglosers.phix.model;


import com.google.gson.annotations.SerializedName;


public class Transaction {

    Float amount;
    String description;
    String source;
    @SerializedName("txn_date_time")
    String date;
    String destination;

    public Transaction(Float amount, String description, String source, String destination) {
        this.amount = amount;
        this.description = description;
        this.source = source;
        this.destination = destination;
    }

    public Float getAmount() { return amount; }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }


}
