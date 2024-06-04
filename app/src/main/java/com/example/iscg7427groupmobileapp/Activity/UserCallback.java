package com.example.iscg7427groupmobileapp.Activity;

import com.example.iscg7427groupmobileapp.Model.User;

import java.util.HashMap;

public interface UserCallback {
    void onCallback(HashMap<String, User> userHashMap);
}
