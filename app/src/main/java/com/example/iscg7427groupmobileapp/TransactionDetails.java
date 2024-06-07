package com.example.iscg7427groupmobileapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Retrieve transaction ID and uid from Intent extras
        String transactionId = getIntent().getStringExtra("transactionId");
        String uid = getIntent().getStringExtra("uid");

        // Log the transactionId and uid for debugging
        Log.d("TransactionDetails", "Received Transaction ID: " + transactionId);
        Log.d("TransactionDetails", "Received User ID: " + uid);

        // Reference to Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid) // Use uid from intent extras
                .child("transactions")
                .child(transactionId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve data from Firebase
                    Map<String, Object> transactionData = (Map<String, Object>) dataSnapshot.getValue();
                    if (transactionData != null) {
                        Log.d("TransactionDetails", "Transaction data: " + transactionData.toString());
                        double amount = (double) transactionData.get("amount");
                        String category = (String) transactionData.get("category");
                        HashMap<String, Long> dateMap = (HashMap<String, Long>) transactionData.get("date"); // Assuming date is stored as a HashMap
                        String description = (String) transactionData.get("description");
                        String type = (String) transactionData.get("type");

                        // Extract date components
                        long time = dateMap.get("time");
                        Date date = new Date(time);

                        // Format date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(date);

                        // Display data in TextViews
                        TextView expenseCategoryTextView = findViewById(R.id.expensecategory);
                        TextView dateTextView = findViewById(R.id.expensedate);
                        TextView costTextView = findViewById(R.id.expensecost);
                        TextView descriptionTextView = findViewById(R.id.expensedesc);
                        TextView typeTextView = findViewById(R.id.expensetype);

                        expenseCategoryTextView.setText(category);
                        dateTextView.setText(formattedDate);
                        costTextView.setText(String.valueOf(amount));
                        descriptionTextView.setText(description);
                        typeTextView.setText(type);
                    } else {
                        // Handle case where transaction data is null
                        Log.e("TransactionDetails", "Transaction data is null");
                        Toast.makeText(TransactionDetails.this, "Transaction data is null", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // Handle case where transaction data is not found
                    Log.e("TransactionDetails", "Transaction not found");
                    Toast.makeText(TransactionDetails.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
                Toast.makeText(TransactionDetails.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
