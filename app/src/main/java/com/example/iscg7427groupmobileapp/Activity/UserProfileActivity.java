package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editTextPassword, editTextUserName, editTextPhone;
    private ImageView showPassword;
    private TextView editTextEmail;
    private Button updateButton, signOutButton;
    private boolean isPasswordVisible = false;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private boolean isUserFound = false; // To track if user is found
    private BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        editTextUserName = findViewById(R.id.userNameEt);
        editTextEmail = findViewById(R.id.userEmailEt);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.userPhoneEt);
        showPassword = findViewById(R.id.showPassword);
        updateButton = findViewById(R.id.updateButton);
        signOutButton = findViewById(R.id.toSignOut);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/");

            // Check "Users" path first
            userRef = database.getReference("Users").child(userId);
            fetchUserDetails(userRef, "Users");

            // If not found in "Users", check "Accountants" path
            if (!isUserFound) {
                userRef = database.getReference("Accountants").child(userId);
                fetchUserDetails(userRef, "Accountants");
            }

            showPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPasswordVisible) {
                        // Hide Password
                        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        showPassword.setImageResource(R.drawable.password_hide);
                    } else {
                        // Show Password
                        editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        showPassword.setImageResource(R.drawable.password_show);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    // Move cursor to the end of the text
                    editTextPassword.setSelection(editTextPassword.getText().length());
                }
            });

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUserDetails();
                }
            });

            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }
    }

    private void fetchUserDetails(DatabaseReference ref, String userType) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userType.equals("Users")) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        isUserFound = true;
                        editTextUserName.setText(user.getName());
                        editTextEmail.setText(user.getEmail());
                        editTextPassword.setText(user.getPassword());
                        editTextPhone.setText(user.getPhoneNumber());
                        bottomNavigation.setVisibility(View.VISIBLE);
                    }
                } else if (userType.equals("Accountants")) {
                    Accountant accountant = snapshot.getValue(Accountant.class);
                    if (accountant != null) {
                        isUserFound = true;
                        editTextUserName.setText(accountant.getName());
                        editTextEmail.setText(accountant.getEmail());
                        editTextPassword.setText(accountant.getPassword());
                        editTextPhone.setText(accountant.getPhoneNumber());
                        bottomNavigation.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserDetails() {
        String userName = editTextUserName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("type") && snapshot.child("type").getValue().equals("Accountants")) {
                        Accountant accountant = snapshot.getValue(Accountant.class);
                        if (accountant != null) {
                            accountant.setName(userName);
                            accountant.setEmail(email);
                            accountant.setPassword(password);
                            accountant.setPhoneNumber(phone);
                            userRef.setValue(accountant)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setName(userName);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setPhoneNumber(phone);
                            userRef.setValue(user)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(UserProfileActivity.this, SplashPage.class);
        startActivity(intent);
        finish();
    }
}
