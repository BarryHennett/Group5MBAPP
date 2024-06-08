package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AllExpenses extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private EditText searchEditText;

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
        bottomNavigation = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();
        searchEditText = findViewById(R.id.searchEditText);


    }
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);

                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    startActivity(new Intent(AllExpenses.this, UserDashboardActivity_1.class));
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    startActivity(new Intent(AllExpenses.this, UserIncomeDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    startActivity(new Intent(AllExpenses.this, UserProfileActivity.class));
                    return true;
                } else {
                    Log.d("BottomNav", "Unknown item selected");
                    return false;
                }
            }
        });

        // Set the selected item as item_profile
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigation.setSelectedItemId(R.id.item_expenses);
            }
        });
    }

}