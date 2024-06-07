package com.example.iscg7427groupmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Activity.AccountantDashboardActivity;
import com.example.iscg7427groupmobileapp.Activity.UserExpenseDashboardActivity;

public class AllExpenses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_expenses);
        LinearLayout toExpensesDashboard = findViewById(R.id.toExpensesDashboard);
        toExpensesDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AllExpenses.this, UserExpenseDashboardActivity.class);
            startActivity(intent);
        });
    }
}