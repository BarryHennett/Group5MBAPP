package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AccountantAddNewClient extends AppCompatActivity {
    EditText etUserName, etOccupation, etIndustry, etEmailAddress, etPhoneNumber;
    Button btnInvite;
    String uid;
    private LinearLayout toAccountant;
    private String currentEmail;
    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant_add_new_client);
        toAccountant = findViewById(R.id.toAccountant);
        toAccountant.setOnClickListener(v -> {
            Intent intent = new Intent(AccountantAddNewClient.this, AccountantDashboardActivity.class);
            startActivity(intent);
        });

        setUpAttributes();
        clickBtnInvite();
    }

    public void setUpAttributes() {
        etUserName = findViewById(R.id.etUserName);
        etOccupation = findViewById(R.id.etOccupation);
        etIndustry = findViewById(R.id.etIndustry);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnInvite = findViewById(R.id.btnInvite);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Retrieve the accountant's password from the database
        retrieveCurrentUserPassword(new PasswordCallback() {
            @Override
            public void onCallback(String password) {
                currentPassword = password;
            }
        });
    }

    public void clickBtnInvite() {
        btnInvite.setOnClickListener(v -> {
            String name = etUserName.getText().toString();
            String occupation = etOccupation.getText().toString();
            String industry = etIndustry.getText().toString();
            String email = etEmailAddress.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString().replace(" ", ""); // Remove spaces from phone number

            if (name.isEmpty() || occupation.isEmpty() || industry.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in the form", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference().child("Users");

                // Check if the email already exists
                usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(AccountantAddNewClient.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Temporarily store the current user
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Create a new user
                            mAuth.createUserWithEmailAndPassword(email, phoneNumber).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userKey = mAuth.getCurrentUser().getUid();
                                        Toast.makeText(AccountantAddNewClient.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                        // Add the new user's key under the current accountant's users node
                                        DatabaseReference accountantRef = database.getReference().child("Accountants").child(uid).child("users");
                                        accountantRef.child(userKey).setValue(userKey);

                                        // Create the new user object and save it
                                        User user = new User(name, email, phoneNumber, "User", occupation, industry, true, new HashMap<>(), phoneNumber);
                                        database.getReference().child("Users").child(userKey).setValue(user);

                                        // Sign back in as the original user
                                        mAuth.signOut(); // Sign out the new user
                                        mAuth.signInWithEmailAndPassword(currentEmail, currentPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AccountantAddNewClient.this, "Reauthenticated as original user", Toast.LENGTH_SHORT).show();
                                                    finish(); // Close the activity to prevent showing the newly created user details
                                                } else {
                                                    Toast.makeText(AccountantAddNewClient.this, "Failed to reauthenticate as original user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AccountantAddNewClient.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
            }
        });
    }



    private void retrieveCurrentUserPassword(PasswordCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onCallback(user.getPassword());
                    } else {
                        callback.onCallback(null);
                    }
                } else {
                    callback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(null);
            }
        });
    }

    private interface PasswordCallback {
        void onCallback(String password);
    }
}
