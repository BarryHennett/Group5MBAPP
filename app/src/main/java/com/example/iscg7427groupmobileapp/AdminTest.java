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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getData();


    }

    private void getData() {
        // get data from root node
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // create a list for user and accountant
                ArrayList <HashMap<String, String>> userAndAccountantList = new ArrayList<>();
                // loop through the Users node
                for (DataSnapshot child : snapshot.child("Users").getChildren()) {
                    // create a hashmap for each user
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("key", child.getKey());
                    userMap.put("type", "User");
                    userMap.put("name", child.child("name").getValue().toString());
                    userMap.put("active", child.child("active").getValue().toString());
                    // add the hashmap to the list
                    userAndAccountantList.add(userMap);
                }
                // loop through the Accountants node
                for (DataSnapshot child : snapshot.child("Accountants").getChildren()) {
                    HashMap<String, String> accountantMap = new HashMap<>();
                    accountantMap.put("key", child.getKey());
                    accountantMap.put("type", "Accountant");
                    accountantMap.put("name", child.child("name").getValue().toString());
                    accountantMap.put("active", child.child("active").getValue().toString());
                    userAndAccountantList.add(accountantMap);
                }
                // set up recycler view
                RecyclerView recyclerView = findViewById(R.id.admin_rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(AdminTest.this));
                AdapterAdmin adapter = new AdapterAdmin(userAndAccountantList, AdminTest.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminTest.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}