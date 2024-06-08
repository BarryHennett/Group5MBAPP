package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.example.iscg7427groupmobileapp.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccountantClientTransactions extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton btnReturn;
    Spinner spinner;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant_client_transactions);

        uid = getIntent().getStringExtra("uid");
        init();
        retrieveUserData(new OnTransactionListener() {
            @Override
            public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                List<Map.Entry<String, User.Transaction>> list = new ArrayList<>(transactions.entrySet());
                list.sort(Comparator.comparing(o -> o.getValue().getDate(), Comparator.reverseOrder()));
                LinkedHashMap<String, User.Transaction> recentTransactions = new LinkedHashMap<>();
                for (Map.Entry<String, User.Transaction> entry : list) {
                    recentTransactions.put(entry.getKey(), entry.getValue());
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AccountantClientTransactions.this));
                recyclerView.setAdapter(new TransactionAdapter(recentTransactions, AccountantClientTransactions.this, uid));
            }
        });

        // set spinner
        String[] options = {"Financial Year: 2023/24", "Financial Year: 2022/23", "Financial Year: 2021/22"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, options);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                retrieveUserData(new OnTransactionListener() {
                    @Override
                    public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                        List<Map.Entry<String, User.Transaction>> filteredList = filterTransactions(selectedItem, transactions);
                        LinkedHashMap<String, User.Transaction> recentTransactions = new LinkedHashMap<>();
                        for (Map.Entry<String, User.Transaction> entry : filteredList) {
                            recentTransactions.put(entry.getKey(), entry.getValue());
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(AccountantClientTransactions.this));
                        recyclerView.setAdapter(new TransactionAdapter(recentTransactions, AccountantClientTransactions.this, uid));
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnReturn.setOnClickListener(v -> finish());
    }

    private void init() {
        recyclerView = findViewById(R.id.accountant_client_transactions_recyclerView);
        btnReturn = findViewById(R.id.accountant_client_transactions_btn_return);
        spinner = findViewById(R.id.accountant_client_transactions_spinner);
    }

    private void retrieveUserData(OnTransactionListener listener) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                HashMap<String, User.Transaction> transactions = user.getTransactions();
                listener.onDateRetrieved(transactions);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface OnTransactionListener {
        void onDateRetrieved(HashMap<String, User.Transaction> transactions);
    }

    private List<Map.Entry<String, User.Transaction>> filterTransactions(String selectedItem, HashMap<String, User.Transaction> transactions) {
        List<Map.Entry<String, User.Transaction>> filteredList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate, endDate;
        try {
            if (selectedItem.equals("Financial Year: 2023/24")) {
                startDate = dateFormat.parse("2023-04-01");
                endDate = dateFormat.parse("2024-03-31");
            } else if (selectedItem.equals("Financial Year: 2022/23")) {
                startDate = dateFormat.parse("2022-04-01");
                endDate = dateFormat.parse("2023-03-31");
            } else if (selectedItem.equals("Financial Year: 2021/22")) {
                startDate = dateFormat.parse("2021-04-01");
                endDate = dateFormat.parse("2022-03-31");
            } else {
                return filteredList; // Return an empty list if invalid selection
            }
        } catch (Exception e) {
            Toast.makeText(AccountantClientTransactions.this, "Error parsing date", Toast.LENGTH_SHORT).show();
            return filteredList;
        }

        for (Map.Entry<String, User.Transaction> entry : transactions.entrySet()) {
            Date transactionDate = entry.getValue().getDate();
            if (transactionDate != null && !transactionDate.before(startDate) && !transactionDate.after(endDate)) {
                filteredList.add(entry);
            }
        }

        filteredList.sort(Comparator.comparing(o -> o.getValue().getDate(), Comparator.reverseOrder()));
        return filteredList;
    }

}