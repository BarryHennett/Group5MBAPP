package com.example.iscg7427groupmobileapp;

import java.util.HashMap;

public class Accountant {

    private HashMap<String, String> users;
    private boolean active;
    private String type;
    private String phoneNumber;
    private String name;

    public Accountant() {
        this.name = "Anonymous";
        this.phoneNumber = "";
        this.type = "Accountant";
        this.active = true;
        this.users = new HashMap<>();
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
