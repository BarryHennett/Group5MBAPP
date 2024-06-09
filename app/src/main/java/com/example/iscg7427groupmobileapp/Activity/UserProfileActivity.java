package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private BottomNavigationView bottomNavigation;
    private LinearLayout userProfile;

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
        userProfile = findViewById(R.id.userProfile);
        auth = FirebaseAuth.getInstance();
        bottomNavigation.setVisibility(View.GONE);
        userProfile.setVisibility(View.GONE);
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/");

            // Fetch user details and update UI
            fetchUserDetails(database, userId);

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

    private void fetchUserDetails(FirebaseDatabase database, String userId) {
        DatabaseReference userRef = database.getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        updateUIForUser(user);
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    userProfile.setVisibility(View.GONE);
                    bottomNavigation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIForUser(User user) {
        editTextUserName.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPassword.setText(user.getPassword());
        editTextPhone.setText(user.getPhoneNumber());
        bottomNavigation.setVisibility(View.VISIBLE);
        userProfile.setVisibility(View.VISIBLE);
    }

    private void updateUserDetails() {
        String userName = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (userName.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Password updated in Firebase Authentication, now update user details in the database
                        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null) {
                                        user.setName(userName);
                                        user.setPassword(password);
                                        user.setPhoneNumber(phone);
                                        userRef.setValue(user)
                                                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to update password in Authentication", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(UserProfileActivity.this, SplashPage.class);
        startActivity(intent);
        finish();
    }
}
