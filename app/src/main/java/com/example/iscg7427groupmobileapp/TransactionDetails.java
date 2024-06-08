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
import java.util.Locale;
import java.util.Map;

public class TransactionDetails extends AppCompatActivity {

    private static final String TAG = "TransactionDetails";
    private String transactionId;
    private String uid;
    private TextView expenseCategoryTextView, dateTextView, costTextView, descriptionTextView, typeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        // Log the start of onCreate process
        Log.d(TAG, "onCreate: started");

        // Initialize TextViews
        expenseCategoryTextView = findViewById(R.id.expensecategory);
        dateTextView = findViewById(R.id.expensedate);
        costTextView = findViewById(R.id.expensecost);
        descriptionTextView = findViewById(R.id.expensedesc);
        typeTextView = findViewById(R.id.expensetype);

        // Retrieve transaction ID and uid from Intent extras
        transactionId = getIntent().getStringExtra("transactionId");
        uid = getIntent().getStringExtra("uid"); // Retrieve uid from Intent extras

        // Log the transactionId and uid for debugging
        Log.d(TAG, "onCreate: Received Transaction ID: " + transactionId);
        Log.d(TAG, "onCreate: Received User ID: " + uid);

        // Ensure transactionId and uid are not null
        if (transactionId == null || uid == null) {
            Log.e(TAG, "onCreate: Transaction ID or User ID is missing");
            Toast.makeText(this, "Transaction ID or User ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch transaction details from Firebase
        fetchTransactionDetails(uid, transactionId); // Pass uid to fetchTransactionDetails method
    }

    private void fetchTransactionDetails(String uid, String transactionId) {
        Log.d(TAG, "fetchTransactionDetails: started with uid: " + uid + " and transactionId: " + transactionId);

        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("transactions")
                .child(transactionId);

        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve data as a Map
                    Map<String, Object> transactionMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (transactionMap != null) {
                        double amount = (double) transactionMap.get("amount");
                        String category = (String) transactionMap.get("category");
                        Date date = (Date) transactionMap.get("date");
                        String description = (String) transactionMap.get("description");
                        String type = (String) transactionMap.get("type");
                        // Create Transaction object manually
                        Transaction transaction = new Transaction(amount, category, date, description, type);

                        // Proceed with displaying the transaction data
                        displayTransactionData(transaction);
                    } else {
                        Log.e(TAG, "onDataChange: Transaction data is null");
                        Toast.makeText(TransactionDetails.this, "Transaction data is null", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "onDataChange: Transaction not found");
                    Toast.makeText(TransactionDetails.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Database error: " + databaseError.getMessage());
                Toast.makeText(TransactionDetails.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



    private void displayTransactionData(Transaction transaction) {
        Log.d(TAG, "displayTransactionData: started");

        try {
            // Extract data from Transaction object
            double amount = transaction.getAmount();
            String category = transaction.getCategory();
            Date date = transaction.getDate();
            String description = transaction.getDescription();
            String type = transaction.getType();

            // Log the retrieved data
            Log.d(TAG, "displayTransactionData: amount=" + amount + ", category=" + category + ", date=" + date + ", description=" + description + ", type=" + type);

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Display data in TextViews
            expenseCategoryTextView.setText(category);
            dateTextView.setText(formattedDate);
            costTextView.setText(String.valueOf(amount));
            descriptionTextView.setText(description);
            typeTextView.setText(type);

            Log.d(TAG, "displayTransactionData: data displayed successfully");
        } catch (Exception e) {
            Log.e(TAG, "displayTransactionData: Error displaying transaction data: " + e.getMessage());
            Toast.makeText(TransactionDetails.this, "Error displaying transaction data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: started");
    }


    public class Transaction {
        private double amount;
        private String category;
        private Date date;
        private String description;
        private String type;

        // Default constructor required for Firebase
        public Transaction() {
        }

        public Transaction(double amount, String category, Date date, String description, String type) {
            this.amount = amount;
            this.category = category;
            this.date = date;
            this.description = description;
            this.type = type;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "amount=" + amount +
                    ", category='" + category + '\'' +
                    ", date=" + date +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

}
