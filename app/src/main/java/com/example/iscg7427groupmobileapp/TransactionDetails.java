package com.example.iscg7427groupmobileapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionDetails extends AppCompatActivity {

    private String transactionId;
    private String uid;
    private TextView expenseCategoryTextView, dateTextView, costTextView, descriptionTextView, typeTextView;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Initialize TextViews
        expenseCategoryTextView = findViewById(R.id.expensecategory);
        dateTextView = findViewById(R.id.expensedate);
        costTextView = findViewById(R.id.expensecost);
        descriptionTextView = findViewById(R.id.expensedesc);
        typeTextView = findViewById(R.id.expensetype);

        // Retrieve transaction ID and uid from Intent extras
        transactionId = getIntent().getStringExtra("transactionId");
        uid = getIntent().getStringExtra("uid");

        // Log the transactionId and uid for debugging
        Log.d("TransactionDetails", "Received Transaction ID: " + transactionId);
        Log.d("TransactionDetails", "Received User ID: " + uid);

        // Ensure transactionId and uid are not null
        if (transactionId == null || uid == null) {
            Log.e("TransactionDetails", "Transaction ID or User ID is missing");
            Toast.makeText(this, "Transaction ID or User ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                .child("users")
                .child(uid) // Use uid from intent extras
                .child("transactions")
                .child(transactionId);

        // Set a timeout of 10 seconds for Firebase operations
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve data from Firebase
                    Map<String, Object> transactionData = (Map<String, Object>) dataSnapshot.getValue();
                    if (transactionData != null) {
                        // Handle data retrieval and display
                        displayTransactionData(transactionData);
                    } else {
                        // Handle case where transaction data is null
                        Log.e("TransactionDetails", "Transaction data is null");
                        Toast.makeText(TransactionDetails.this, "Transaction data is null", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } /*else {
                    // Handle case where transaction data is not found
                    Log.e("TransactionDetails", "Transaction not found");
                    Toast.makeText(TransactionDetails.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    finish();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
                Toast.makeText(TransactionDetails.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        // Add the ValueEventListener to the database reference
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener to prevent memory leaks
        if (valueEventListener != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                    .child("users")
                    .child(uid) // Use uid from intent extras
                    .child("transactions")
                    .child(transactionId);
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    private void displayTransactionData(Map<String, Object> transactionData) {
        Log.d("TransactionDetails", "Displaying transaction data...");

        try {
            // Extract data from transactionData map
            double amount = transactionData.containsKey("amount") ? (double) transactionData.get("amount") : 0.0;
            String category = transactionData.containsKey("category") ? (String) transactionData.get("category") : "";
            HashMap<String, Long> dateMap = transactionData.containsKey("date") ? (HashMap<String, Long>) transactionData.get("date") : new HashMap<>();
            String description = transactionData.containsKey("description") ? (String) transactionData.get("description") : "";
            String type = transactionData.containsKey("type") ? (String) transactionData.get("type") : "";

            // Extract date components
            long time = dateMap.containsKey("time") ? dateMap.get("time") : 0L;
            Date date = new Date(time);

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(date
            );

            // Display data in TextViews
            expenseCategoryTextView.setText(category);
            dateTextView.setText(formattedDate);
            costTextView.setText(String.valueOf(amount));
            descriptionTextView.setText(description);
            typeTextView.setText(type);
        } catch (Exception e) {
            Log.e("TransactionDetails", "Error displaying transaction data: " + e.getMessage());
            Toast.makeText(TransactionDetails.this, "Error displaying transaction data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
