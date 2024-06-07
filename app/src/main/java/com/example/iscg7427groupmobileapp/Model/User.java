package com.example.iscg7427groupmobileapp.Model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private String id;

    private String name;



    private String phoneNumber;
    private String email;
    private String type;

    private String occupation;
    private String industry;
    private boolean active;
    private HashMap<String, Transaction> transactions;
    private String password;

    public User() {
        this.name = "Anonymous";
        this.email = "";
        this.phoneNumber = "";
        this.type = "User";
        this.occupation = "";
        this.industry = "";
        this.active = true;
        this.password = "";
        this.transactions = new HashMap<>();
    }

    public User(String name, String email, String phoneNumber, String type, String occupation, String industry, boolean active, HashMap<String, Transaction> transactions, String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.occupation = occupation;
        this.industry = industry;
        this.active = active;
        this.transactions = transactions;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    // Method to calculate total income, expenses, and net income
    public double[] calculateTotals() {
        double totalIncome = 0;
        double totalExpenses = 0;

        for (Map.Entry<String, Transaction> entry : transactions.entrySet()) {
            Transaction transaction = entry.getValue();
            if ("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else if ("Expense".equals(transaction.getType())) {
                totalExpenses += transaction.getAmount();
            }
        }

        double netIncome = totalIncome - totalExpenses;
        return new double[]{totalIncome, totalExpenses, netIncome};
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
