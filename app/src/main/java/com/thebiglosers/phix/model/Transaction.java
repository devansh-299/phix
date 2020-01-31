package com.thebiglosers.phix.model;

public class Transaction {

    Float amount;
    String transactionID;

    public Transaction(Float amount, String description, String date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    String description;
    String source;
    String date;
    String destination;

    public Float getAmount() {
        return amount;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }


}
