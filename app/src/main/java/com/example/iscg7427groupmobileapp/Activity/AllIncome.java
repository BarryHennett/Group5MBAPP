
package com.example.iscg7427groupmobileapp.Activity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MenuItem;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import androidx.activity.EdgeToEdge;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
        import com.example.iscg7427groupmobileapp.R;
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.google.android.material.navigation.NavigationBarView;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;

public class AllIncome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private DatabaseReference databaseReference;
    private BottomNavigationView bottomNavigation;
    private EditText searchEditText;


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
        bottomNavigation = findViewById(R.id.bottom_navigation);

        setupBottomNavigation();
        searchEditText = findViewById(R.id.searchEditText);


        recyclerView = findViewById(R.id.userallincome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        //transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(transactionAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        loadTransactions();
    }

    private void loadTransactions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                transactionList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot transactionSnapshot : userSnapshot.getChildren()) {
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        transactionList.add(transaction);
                    }
                }
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AllIncome.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);

                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    startActivity(new Intent(AllIncome.this, UserDashboardActivity_1.class));
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    startActivity(new Intent(AllIncome.this, UserExpenseDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    startActivity(new Intent(AllIncome.this, UserProfileActivity.class));
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
                bottomNavigation.setSelectedItemId(R.id.item_income);
            }
        });
    }

    public static class Transaction {
        private String id;
        private String description;
        private double amount;
        private long timestamp;

        public Transaction() {
            // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
        }

        public Transaction(String id, String description, double amount, long timestamp) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public double getAmount() {
            return amount;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
