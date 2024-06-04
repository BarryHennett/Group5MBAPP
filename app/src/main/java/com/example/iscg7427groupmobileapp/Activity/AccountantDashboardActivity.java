package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.AccountantClientAdapter;
import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountantDashboardActivity extends AppCompatActivity {

    private RecyclerView clientRv;
    private AccountantClientAdapter adapter;
    private DatabaseReference databaseReference;
    private TextView accountClientNumber;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant_dashboard);

        clientRv = findViewById(R.id.AccountantClientListRv);
        clientRv.setLayoutManager(new LinearLayoutManager(this));

        accountClientNumber = findViewById(R.id.AccountClientNumber);
        searchEditText = findViewById(R.id.searchEditText);

        String uid = getIntent().getStringExtra("uid");
        if (uid == null || uid.isEmpty()) {
            Log.e("AccountantDashboard", "UID cannot be null or empty");
            finish(); // Close the activity as there's no valid UID
            return;
        }

        databaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                .getReference();

        fetchAccountantData(uid);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void fetchAccountantData(String uid) {
        databaseReference.child("Accountants").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Accountant accountant = snapshot.getValue(Accountant.class);
                if (accountant != null) {
                    HashMap<String, String> users = accountant.getUsers();
                    ArrayList<String> uidList = new ArrayList<>(users.values());
                    fetchUserInfo(uidList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountantDashboard", "Error fetching accountant data", error.toException());
            }
        });
    }

    private void fetchUserInfo(ArrayList<String> uidList) {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, User> userMap = new HashMap<>();
                for (String uid : uidList) {
                    User user = snapshot.child(uid).getValue(User.class);
                    if (user != null) {
                        userMap.put(uid, user);
                    }
                }
                adapter = new AccountantClientAdapter(userMap, AccountantDashboardActivity.this);
                clientRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Set the number of clients
                accountClientNumber.setText(String.format("Clients(%d)", adapter.getItemCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountantDashboard", "Error fetching user data", error.toException());
            }
        });
    }
}
