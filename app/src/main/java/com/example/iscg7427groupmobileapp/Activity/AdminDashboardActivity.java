package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private LinearLayout toAddUser;
    private ImageView toProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        toAddUser = findViewById(R.id.toAddUser);
        toAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminAddNewUser.class);
            startActivity(intent);
        });

        toProfile = findViewById(R.id.toProfile);
        toProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.AdminUserListRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, String>> userAndAccountantList = new ArrayList<>();
                userAndAccountantList.clear();
                for (DataSnapshot ds : snapshot.child("Accountants").getChildren()) {
                    Boolean isActive = ds.child("active").getValue(Boolean.class);
                    if (isActive != null) {
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("key", ds.getKey());
                        userMap.put("type", "Accountants");
                        userMap.put("name", getValueOrDefault(ds.child("name")));
                        userMap.put("active", isActive.toString());

                        userAndAccountantList.add(userMap);
                    }
                }

                for (DataSnapshot ds : snapshot.child("Users").getChildren()) {
                    Boolean isActive = ds.child("active").getValue(Boolean.class);
                    if (isActive != null) {
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("key", ds.getKey());
                        userMap.put("type", "Users");
                        userMap.put("name", getValueOrDefault(ds.child("name")));
                        userMap.put("active", isActive.toString());

                        userAndAccountantList.add(userMap);
                    }
                }

                adapter = new AdminUserAdapter(userAndAccountantList, AdminDashboardActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
