package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.AdminUserAdapter;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private DatabaseReference databaseReference;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        searchEditText = findViewById(R.id.searchEditText);

        databaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                .getReference();

        fetchUsersAndAccountants();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void fetchUsersAndAccountants() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, String>> userAndAccountantList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.child("Accountants").getChildren()) {
                    Boolean isActive = ds.child("active").getValue(Boolean.class);
                    if (isActive != null && isActive) {
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("key", ds.getKey());
                        userMap.put("type", "Accountant");
                        userMap.put("name", getValueOrDefault(ds.child("name")));
                        userMap.put("active", isActive.toString());

                        userAndAccountantList.add(userMap);
                    }
                }

                for (DataSnapshot ds : snapshot.child("Users").getChildren()) {
                    Boolean isActive = ds.child("active").getValue(Boolean.class);
                    if (isActive != null && isActive) {
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("key", ds.getKey());
                        userMap.put("type", "User");
                        userMap.put("name", getValueOrDefault(ds.child("name")));
                        userMap.put("active", isActive.toString());

                        userAndAccountantList.add(userMap);
                    }
                }

                recyclerView = findViewById(R.id.AdminUserListRv);
                recyclerView.setLayoutManager(new LinearLayoutManager(AdminDashboardActivity.this));
                adapter = new AdminUserAdapter(userAndAccountantList, AdminDashboardActivity.this);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("AdminDashboard", "Error fetching data", error.toException());
            }
        });
    }

    private String getValueOrDefault(DataSnapshot snapshot) {
        if (snapshot.exists()) {
            return snapshot.getValue() != null ? snapshot.getValue().toString() : "";
        }
        return "";
    }
}
