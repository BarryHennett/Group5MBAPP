package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.AccountantClientAdapter;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountantDashboardActivity extends AppCompatActivity {
    private RecyclerView clientRv;
    private AccountantClientAdapter adapter;
    private List<User> userList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accountant_dashboard);

        clientRv = findViewById(R.id.AccountantClientListRv);
        userList = new ArrayList<>();
        adapter = new AccountantClientAdapter(userList, this);
        clientRv.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Accountants");

        fetchClientList();


    }

    private void fetchClientList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot accountantSnapshot : snapshot.getChildren()) {
                    DataSnapshot usersSnapshot = accountantSnapshot.child("users");
                    for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                        String userId = userSnapshot.getValue(String.class);
                        if (userId != null) {
                            fetchUserDetails(userId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void fetchUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                .getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}