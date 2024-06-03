package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editTextPassword, editTextUserName, editTextEmail, editTextPhone;
    private ImageView showPassword;
    private Button updateButton;
    private boolean isPasswordVisible = false;
    private DatabaseReference userRef;
    private String userId;

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                    .getReference("Users").child(userId);

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

            fetchUserDetails();
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }
    }

    private void fetchUserDetails() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    editTextUserName.setText(user.getName());
                    editTextEmail.setText(user.getEmail());
                    editTextPassword.setText(user.getPassword());
                    editTextPhone.setText(user.getPhoneNumber());
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
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    // Update the fields
                    user.setName(userName);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setPhoneNumber(phone);

                    // Save the updated user
                    userRef.setValue(user)
                            .addOnSuccessListener(aVoid -> Toast.makeText(UserProfileActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
