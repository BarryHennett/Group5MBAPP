package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddNewUser extends AppCompatActivity {

    EditText etUserName, etEmailAddress, etPhoneNumber;
    TextView etUserType;
    Button btnInvitation;
    String currentEmail = "administer_1@gmail.com", currentPassword = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_user);
        setUpAttributes();
        clickInvitation();
    }

    public void setUpAttributes() {
        etUserName = findViewById(R.id.etUserName);
        etUserType = findViewById(R.id.etUserType);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnInvitation = findViewById(R.id.btnInvitation);
    }

    public void clickInvitation() {
        btnInvitation.setOnClickListener(v -> {
            String userName = etUserName.getText().toString();
            String userType = etUserType.getText().toString();
            String emailAddress = etEmailAddress.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            if (userName.isEmpty() || userType.isEmpty() || emailAddress.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in the form", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(emailAddress, phoneNumber).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "The user is created successfully", Toast.LENGTH_SHORT).show();
                        String key = mAuth.getCurrentUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        Accountant accountant = new Accountant();
                        accountant.setName(userName);
                        accountant.setType(userType);
                        accountant.setEmail(emailAddress);
                        String phone = phoneNumber.replace(" ", "");
                        accountant.setPassword(phone);
                        accountant.setPhoneNumber(phoneNumber);
                        database.getReference().child("Accountants").child(key).setValue(accountant).addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                reauthenticateAdmin(mAuth);
                            } else {
                                Toast.makeText(this, "Failed to create user in database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void reauthenticateAdmin(FirebaseAuth mAuth) {
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Reauthenticated as admin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to reauthenticate as admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
