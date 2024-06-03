package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminUpdateUserActivity extends AppCompatActivity {
    private EditText userNameEt, userEmailEt, userPhoneEt;
   private TextView userTypeTv;
    private Button updateUserBtn;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_user);

        TextView sendNewPasswordBtn = findViewById(R.id.sendNewPasswordBtn);
        updateUnderlinedTextView(sendNewPasswordBtn, "Send New Password");

        ImageView toAdminDashboard = findViewById(R.id.toAdminDashboard);
        toAdminDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUpdateUserActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        sendNewPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminUpdateUserActivity.this, "Sent new password", Toast.LENGTH_SHORT).show();
            }
        });

        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/").getReference("Users").child(userId);

        userNameEt = findViewById(R.id.userNameEt);
        userEmailEt = findViewById(R.id.userEmailEt);
        userPhoneEt = findViewById(R.id.userPhoneEt);
        userTypeTv = findViewById(R.id.userTypeTv);
        updateUserBtn = findViewById(R.id.userUpdateBtn);
        fetchUserDetails();
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails(userId);
            }
        });
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
                if (user !=null){
                    //Update the fields
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhoneNumber(phone);

                    //Save the updated field
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
    private void fetchUserDetails(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user !=null){
                    userNameEt.setText(user.getName());
                    userEmailEt.setText(user.getEmail());
                    userPhoneEt.setText(user.getPhoneNumber());
                    userTypeTv.setText(user.getType());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateUserActivity.this, "Failed to fetch User details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
