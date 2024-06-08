package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
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
import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
    private FirebaseUser currentUser;
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
        currentUser = auth.getCurrentUser();

        bottomNavigation.setVisibility(View.GONE);
        userProfile.setVisibility(View.GONE);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/");

            // Check all user types and update UI based on the result
            checkUserType(database, userId);

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

            setupBottomNavigation();
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);

                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    startActivity(new Intent(UserProfileActivity.this, UserDashboardActivity_1.class));
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    startActivity(new Intent(UserProfileActivity.this, UserIncomeDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    startActivity(new Intent(UserProfileActivity.this, UserExpenseDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    // Current activity
                    return true;
                } else {
                    Log.d("BottomNav", "Unknown item selected");
                    return false;
                }
            }
        });

        // Set the selected item as item_profile
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigation.setSelectedItemId(R.id.item_profile);
            }
        });
    }

    private void checkUserType(FirebaseDatabase database, String userId) {
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
                    DatabaseReference accountantRef = database.getReference("Accountants").child(userId);
                    accountantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Accountant accountant = snapshot.getValue(Accountant.class);
                                if (accountant != null) {
                                    updateUIForAccountant(accountant);
                                }
                            } else {
                                DatabaseReference adminRef = database.getReference("Administers").child(userId);
                                adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            User admin = snapshot.getValue(User.class); // Assuming the structure is similar to User
                                            if (admin != null) {
                                                updateUIForAdmin(admin);
                                            }
                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "User not found in any category", Toast.LENGTH_SHORT).show();
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UserProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private void updateUIForAccountant(Accountant accountant) {
        editTextUserName.setText(accountant.getName());
        editTextEmail.setText(accountant.getEmail());
        editTextPassword.setText(accountant.getPassword());
        editTextPhone.setText(accountant.getPhoneNumber());
        bottomNavigation.setVisibility(View.GONE);
        userProfile.setVisibility(View.VISIBLE);
    }

    private void updateUIForAdmin(User admin) {
        editTextUserName.setText(admin.getName());
        editTextEmail.setText(admin.getEmail());
        editTextPassword.setText(admin.getPassword());
        editTextPhone.setText(admin.getPhoneNumber());
        bottomNavigation.setVisibility(View.GONE);
        userProfile.setVisibility(View.GONE);
    }

    private void updateUserDetails() {
        String userName = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (userName.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/");
        DatabaseReference userRef = database.getReference("Users").child(userId);
        DatabaseReference accountantRef = database.getReference("Accountants").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        updatePasswordInFirebaseAuth(password);
                        user.setName(userName);
                        user.setPassword(password);
                        user.setPhoneNumber(phone);
                        userRef.setValue(user)
                                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    accountantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Accountant accountant = snapshot.getValue(Accountant.class);
                                if (accountant != null) {
                                    updatePasswordInFirebaseAuth(password);
                                    accountant.setName(userName);
                                    accountant.setPassword(password);
                                    accountant.setPhoneNumber(phone);
                                    accountantRef.setValue(accountant)
                                            .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "Accountant details updated successfully", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update accountant", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                Toast.makeText(UserProfileActivity.this, "User not found in any category", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordInFirebaseAuth(String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "Password updated in Firebase Auth", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update password in Firebase Auth", Toast.LENGTH_SHORT).show());
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(UserProfileActivity.this, SplashPage.class);
        startActivity(intent);
        finish();
    }
}
