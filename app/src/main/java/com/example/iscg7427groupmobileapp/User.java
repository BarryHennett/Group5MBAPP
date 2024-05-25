package com.example.iscg7427groupmobileapp;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class User {

    private String name;
    private String phoneNumber;
    private String type;
    private String occupation;
    private String industry;
    private boolean active;
    private HashMap<String, Transaction> transactions;

    public User() {
        this.name = "Anonymous";
        this.phoneNumber = "";
        this.type = "User";
        this.occupation = "";
        this.industry = "";
        this.active = true;
        this.transactions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public HashMap<String, Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(HashMap<String, Transaction> transactions) {
        this.transactions = transactions;
    }

    public String addNewTransaction(Transaction transaction) {
        String key = UUID.randomUUID().toString();
        this.transactions.put(key, transaction);
        return key;
    }

    public static class Transaction {

        private String type;
        private String category;
        private double amount;
        private Date date;
        private String description;

        public Transaction() {
        }

        public Transaction(String type, String category, double amount, Date date, String description) {
            this.description = description;
            this.date = date;
            this.amount = amount;
            this.category = category;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
