package com.example.iscg7427groupmobileapp;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class User {

    private HashMap<String, Transaction> transactions;
    private boolean active;
    private String industry;
    private String occupation;
    private String type;
    private String phoneNumber;
    private String name;

    public User() {
        this.name = "Anonymous";
        this.phoneNumber = "";
        this.type = "User";
        this.occupation = "";
        this.industry = "";
        this.active = true;
        this.transactions = new HashMap<>();
    }

    public HashMap<String, Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(HashMap<String, Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getType() {
        return type;
    }

    public void setType(String userType) {
        this.type = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String addNewTransaction(Transaction transaction) {
        String key = UUID.randomUUID().toString();
        this.transactions.put(key, transaction);
        return key;
    }

    public static class Transaction{

        private String description;
        private Date date;
        private double amount;
        private String category;
        private String type;


        public Transaction() {
            this.description = "";
            this.date = new Date();
            this.amount = 0;
            this.category = "";
            this.type = "";
        }

        public Transaction(String type, String category, double amount, Date date, String description) {
            this.description = description;
            this.date = date;
            this.amount = amount;
            this.category = category;
            this.type = type;
        }




        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


}
