package com.example.iscg7427groupmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Activity.UserExpenseDashboardActivity;
import com.example.iscg7427groupmobileapp.Activity.UserIncomeDashboardActivity;

public class AllIncome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_income);
        LinearLayout toIncomeDashboard;
        toIncomeDashboard = findViewById(R.id.toIncomeDashboard);
        toIncomeDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AllIncome.this, UserIncomeDashboardActivity.class);
            startActivity(intent);
        });
    }
}