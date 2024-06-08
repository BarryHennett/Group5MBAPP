package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.R;

public class AccountantClientTransactions extends AppCompatActivity {

    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accountant_client_transactions);
        LinearLayout toAccountant = findViewById(R.id.toAccountant);
        toAccountant.setOnClickListener(v -> {
            Intent intent = new Intent(AccountantClientTransactions.this, AccountantDashboardActivity.class);
            startActivity(intent);
        });

    }
}