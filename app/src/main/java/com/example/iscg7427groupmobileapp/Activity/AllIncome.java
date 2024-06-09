package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AllIncome extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton btnReturn;
    Spinner spinner;
    EditText searchEditText;
    NavigationBarView nav;
    String uid;
    private BottomNavigationView bottomNavigation;
    TransactionAdapter transactionAdapter;
    HashMap<String, User.Transaction> allTransactions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_income);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        init();
        setupBottomNavigation();

        retrieveUserData(new OnTransactionListener() {
            @Override
            public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                allTransactions = transactions;
                List<Map.Entry<String, User.Transaction>> list = new ArrayList<>(transactions.entrySet());
                list.sort(Comparator.comparing(o -> o.getValue().getDate(), Comparator.reverseOrder()));
                LinkedHashMap<String, User.Transaction> recentTransactions = new LinkedHashMap<>();
                for (Map.Entry<String, User.Transaction> entry : list) {
                    recentTransactions.put(entry.getKey(), entry.getValue());
                }
                transactionAdapter = new TransactionAdapter(recentTransactions, AllIncome.this, uid);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllIncome.this));
                recyclerView.setAdapter(transactionAdapter);
            }
        });

        // Set spinner
        String[] options = {"Current Tax Year", "Last 12 Months", "Financial Year: 2023/24"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, options);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                List<Map.Entry<String, User.Transaction>> filteredList = filterTransactions(selectedItem, allTransactions);
                LinkedHashMap<String, User.Transaction> recentTransactions = new LinkedHashMap<>();
                for (Map.Entry<String, User.Transaction> entry : filteredList) {
                    recentTransactions.put(entry.getKey(), entry.getValue());
                }


                transactionAdapter = new TransactionAdapter(recentTransactions, AllIncome.this, uid);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllIncome.this));
                recyclerView.setAdapter(transactionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (transactionAdapter != null) {
                    transactionAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        btnReturn.setOnClickListener(v -> finish());
    }

    private void init() {
        recyclerView = findViewById(R.id.all_income_recyclerView);
        btnReturn = findViewById(R.id.all_income_btn_return);
        spinner = findViewById(R.id.all_income_spinner);
        searchEditText = findViewById(R.id.searchEditText);
        bottomNavigation = findViewById(R.id.bottom_navigation);
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

        // Set the selected item as item_income
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigation.setSelectedItemId(R.id.item_income);
            }
        });
    }

    private void retrieveUserData(OnTransactionListener listener) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    HashMap<String, User.Transaction> transactions = user.getTransactions();
                    HashMap<String, User.Transaction> incomeTransactions = new HashMap<>();
                    for (Map.Entry<String, User.Transaction> entry : transactions.entrySet()) {
                        User.Transaction transaction = entry.getValue();
                        if (transaction.getType().equals("Income")) {
                            incomeTransactions.put(entry.getKey(), transaction);
                        }
                    }
                    listener.onDateRetrieved(incomeTransactions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    public interface OnTransactionListener {
        void onDateRetrieved(HashMap<String, User.Transaction> transactions);
    }

    private List<Map.Entry<String, User.Transaction>> filterTransactions(String selectedItem, HashMap<String, User.Transaction> transactions) {
        List<Map.Entry<String, User.Transaction>> filteredList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null, endDate = new Date();
        Calendar calendar = Calendar.getInstance();

        try {
            if (selectedItem.equals("Current Tax Year")) {
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                calendar.set(Calendar.DAY_OF_MONTH, 31);
                endDate = calendar.getTime();
            } else if (selectedItem.equals("Last 12 Months")) {
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -1);
                startDate = calendar.getTime();
            } else if (selectedItem.equals("Financial Year: 2023/24")) {
                startDate = dateFormat.parse("2023-04-01");
                endDate = dateFormat.parse("2024-03-31");
            } else {
                return filteredList; // Return an empty list if invalid selection
            }
        } catch (Exception e) {
            Toast.makeText(AllIncome.this, "Error parsing date", Toast.LENGTH_SHORT).show();
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
