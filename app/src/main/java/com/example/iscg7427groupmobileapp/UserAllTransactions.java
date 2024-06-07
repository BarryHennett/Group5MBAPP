package com.example.iscg7427groupmobileapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
import com.example.iscg7427groupmobileapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserAllTransactions extends AppCompatActivity {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private HashMap<String, User.Transaction> transactionMap;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_all_transactions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.useralltrarecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionMap = new HashMap<>();
        transactionAdapter = new TransactionAdapter(transactionMap, this);
        recyclerView.setAdapter(transactionAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://group5-6aa2b-default-rtdb.firebaseio.com/");
        loadTransactions();
    }

    private void loadTransactions() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionMap.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    DataSnapshot transactionsSnapshot = userSnapshot.child("transactions");
                    for (DataSnapshot transactionSnapshot : transactionsSnapshot.getChildren()) {
                        try {
                            User.Transaction transaction = transactionSnapshot.getValue(User.Transaction.class);
                            if (transaction != null) {
                                transaction.setDate(parseFirebaseDate(transactionSnapshot.child("date")));
                                transactionMap.put(transactionSnapshot.getKey(), transaction);
                            } else {
                                System.out.println("Warning: transaction is null for snapshot: " + transactionSnapshot);
                            }
                        } catch (DatabaseException e) {
                            System.err.println("Error converting transaction: " + e.getMessage());
                            System.err.println("Problematic snapshot: " + transactionSnapshot);
                        }
                    }
                }
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserAllTransactions.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date parseFirebaseDate(DataSnapshot dateSnapshot) {
        long time = dateSnapshot.child("time").getValue(Long.class);
        return new Date(time);
    }
}
