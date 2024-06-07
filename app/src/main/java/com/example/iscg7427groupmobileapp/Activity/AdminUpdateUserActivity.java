package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdminUpdateUserActivity extends AppCompatActivity {
    private EditText userNameEt, userPhoneEt;
    private TextView userTypeTv, userEmailEt, deactivateBtn, updateUserBtn;
    private DatabaseReference databaseReference;
    private String userId, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_user);

        TextView sendNewPasswordBtn = findViewById(R.id.sendNewPasswordBtn);
        updateUnderlinedTextView(sendNewPasswordBtn, "Send New Password");

        LinearLayout toAdminDashboard = findViewById(R.id.toAdminDashboard);
        toAdminDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminUpdateUserActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        });

        sendNewPasswordBtn.setOnClickListener(v -> {
            Toast.makeText(AdminUpdateUserActivity.this, "New password sent", Toast.LENGTH_SHORT).show();
            // Add logic to send new password here
        });

        userId = getIntent().getStringExtra("userId");
        userType = getIntent().getStringExtra("userType");
        if (userId == null || userId.isEmpty() || userType == null || userType.isEmpty()) {
            Toast.makeText(this, "Invalid User ID or Type", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/").getReference(userType).child(userId);

        userNameEt = findViewById(R.id.userNameEt);
        userEmailEt = findViewById(R.id.userEmailEt);
        userPhoneEt = findViewById(R.id.userPhoneEt);
        userTypeTv = findViewById(R.id.userTypeTv);
        updateUserBtn = findViewById(R.id.userUpdateBtn);
        deactivateBtn = findViewById(R.id.deactivateButton);

        fetchUserDetails();

        updateUserBtn.setOnClickListener(v -> updateUserDetails(userId));

        deactivateBtn.setOnClickListener(v -> toggleUserStatus());
    }

    private void updateUnderlinedTextView(TextView textView, String targetText) {
        SpannableString spannableString = new SpannableString(targetText);
        spannableString.setSpan(new UnderlineSpan(), 0, targetText.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void updateUserDetails(String userId) {
        String name = userNameEt.getText().toString();
        String email = userEmailEt.getText().toString();
        String phone = userPhoneEt.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    // Update the fields
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhoneNumber(phone);

                    // Save the updated fields
                    databaseReference.setValue(user)
                            .addOnSuccessListener(aVoid -> Toast.makeText(AdminUpdateUserActivity.this, "User details updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(AdminUpdateUserActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateUserActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userNameEt.setText(user.getName());
                    userEmailEt.setText(user.getEmail());
                    userPhoneEt.setText(user.getPhoneNumber());
                    userTypeTv.setText(user.getType());

                    if (user.isActive()) {
                        deactivateBtn.setText("Deactivate");
                    } else {
                        deactivateBtn.setText("Activate");
                    }
                }

                // Fetch the email from Firebase Authentication for the currently logged-in user
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String currentUserId = firebaseUser.getUid();
                    if (currentUserId.equals(userId)) {
                        userEmailEt.setText(firebaseUser.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateUserActivity.this, "Failed to fetch User details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleUserStatus() {
        databaseReference.child("active").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isActive = snapshot.getValue(Boolean.class);
                if (isActive != null) {
                    databaseReference.child("active").setValue(!isActive).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminUpdateUserActivity.this, isActive ? "User deactivated" : "User activated", Toast.LENGTH_SHORT).show();
                            deactivateBtn.setText(isActive ? "Activate" : "Deactivate");
                        } else {
                            Toast.makeText(AdminUpdateUserActivity.this, "Failed to update user status", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateUserActivity.this, "Failed to update user status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
