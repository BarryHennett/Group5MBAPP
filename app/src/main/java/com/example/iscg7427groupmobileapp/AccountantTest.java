package com.example.iscg7427groupmobileapp;

import android.os.Bundle;
import android.widget.Button;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountantTest extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accountant_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnAdd = findViewById(R.id.accountant_btn_add);
        btnAdd.setOnClickListener(v -> {
            // get the accountant uid
            String uid = getIntent().getStringExtra("uid");
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Accountants").child(uid);
            // hard code user uid for testing
            // Change method in Class Accountant of adding new user if needed
         //   mRef.child("users").push().setValue("dFyWocJZDdWmlDg3A5lcL2TiEOl1");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Accountant accountant = snapshot.getValue(Accountant.class);
                       accountant.setPhoneNumber("1357924680");
                       mRef.setValue(accountant);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getCode());
                }
            });


        });

        Button btnShow = findViewById(R.id.accountant_btn_show);
        btnShow.setOnClickListener(v -> {

            String uid = getIntent().getStringExtra("uid");
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Accountants").child(uid);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Accountant accountant = snapshot.getValue(Accountant.class);
                    if (accountant != null) {
                        HashMap<String, String> users = accountant.getUsers();

                        Toast.makeText(AccountantTest.this, "Customers: " + users.size(), Toast.LENGTH_SHORT).show();

                        Toast.makeText(AccountantTest.this, users.get(users.keySet().toArray()[0]).toString(), Toast.LENGTH_SHORT).show();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getCode());
                }
            });

        });

        Button btnGet = findViewById(R.id.accountant_btn_get);
        btnGet.setOnClickListener(v -> {
            String uid = getIntent().getStringExtra("uid");
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Accountants").child(uid);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Accountant accountant = snapshot.getValue(Accountant.class);
                    if (accountant != null) {
                        // get the HashMap of users
                        HashMap<String, String> users = accountant.getUsers();
                        // get the list of user uid from Hashmap
                        ArrayList<String> uidList = new ArrayList<>(users.values());
                        // get the user information from the uid
                        // note that the getUserInfo method is asynchronous, implement the callback function
                        getUserInfo(uidList, new UserCallback() {
                            @Override
                            public void onCallback(HashMap<String, User> userMap) {
                                // set the recycler view
                                RecyclerView recyclerView = findViewById(R.id.accountant_recycler_view);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AccountantTest.this));
                                recyclerView.setAdapter(new AdapterAccountant(userMap, AccountantTest.this));
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getCode());
                }
            });
        });
    }
    private void getUserInfo(ArrayList<String> uidList, UserCallback callback) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // create a HashMap to store the user matched
                HashMap<String, User> userMap = new HashMap<>();
                // get the user instance from the uid
                for (String uid : uidList) {
                    User user = snapshot.child(uid).getValue(User.class);
                    userMap.put(uid, user);
                }

                callback.onCallback(userMap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
    }
    public interface UserCallback {
        void onCallback(HashMap<String, User> userMap);
    }
}