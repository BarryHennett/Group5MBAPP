package com.example.iscg7427groupmobileapp.Model;

import java.util.HashMap;

public class Accountant {
    private String id;
    private String name;
    private String phoneNumber;
    private String type;
    private boolean active;
    private HashMap<String, String> users;

    public Accountant() {
        this.id = "";
        this.name = "Anonymous";
        this.phoneNumber = "";
        this.type = "Accountant";
        this.active = true;
        this.users = new HashMap<>();
    }
    public Accountant(String id, String name, String phoneNumber, String type, boolean active, HashMap<String, String> users) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.active = active;
        this.users = users;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }
}
