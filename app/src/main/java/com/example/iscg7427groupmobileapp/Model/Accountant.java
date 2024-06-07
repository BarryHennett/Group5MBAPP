package com.example.iscg7427groupmobileapp.Model;

import java.util.HashMap;

public class Accountant {
    private String id;
    private String name;
    private String phoneNumber;
    private String type;
    private boolean active;
    private HashMap<String, String> users;
    private String email;
    private String password;

    public Accountant() {
        this.id = "";
        this.name = "";
        this.phoneNumber = "";
        this.type = "Accountant";
        this.active = true;
        this.users = new HashMap<>();
        this.email = "";
        this.password= "";
    }

    public Accountant(String id, String name, String phoneNumber, String type, boolean active, HashMap<String, String> users, String email, String password) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.active = active;
        this.users = users;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Accountant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", active=" + active +
                ", users=" + users +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static class User{
        private String key;

        public User(String key) {
            this.key = key;
        }

        public User(){}

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "User{" +
                    "key='" + key + '\'' +
                    '}';
        }
    }
}
