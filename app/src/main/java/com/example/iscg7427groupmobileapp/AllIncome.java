
package com.example.iscg7427groupmobileapp;

        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import androidx.activity.EdgeToEdge;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.graphics.Insets;
        import androidx.core.view.ViewCompat;
        import androidx.core.view.WindowInsetsCompat;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.iscg7427groupmobileapp.Activity.UserIncomeDashboardActivity;
        import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.text.DateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

public class AllIncome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private DatabaseReference databaseReference;

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
