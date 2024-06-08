package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddNewUser extends AppCompatActivity {

    EditText etUserName, etEmailAddress, etPhoneNumber;
    TextView etUserType;
    Button btnInvitation;
    String currentEmail = "administer_1@gmail.com", currentPassword = "123456";
    TextView tvAccount, tvUser;
    int chooseRole = 0;
    private LinearLayout toAdminDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_user);
        toAdminDashboard = findViewById(R.id.toAdminDashboard);
        toAdminDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddNewUser.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });
        setUpAttributes();
        clickInvitation();
        clickTvAccount();
        clickTvUser();
    }

    public void setUpAttributes() {
        etUserName = findViewById(R.id.etUserName);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnInvitation = findViewById(R.id.btnInvitation);
        tvUser = findViewById(R.id.tvUser);
        tvAccount = findViewById(R.id.tvAccount);
    }
    public void clickTvAccount() {
        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAccount.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button_border_green));
                tvUser.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.background_black));
                chooseRole = 0;
            }
        });
    }
    public void clickTvUser() {
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUser.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.button_border_green));
                tvAccount.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.background_black));
                chooseRole = 1;
            }
        });
    }
//    public void clickInvitation() {
//        btnInvitation.setOnClickListener(v -> {
//            String userName = etUserName.getText().toString();
//            String userType = etUserType.getText().toString();
//            String emailAddress = etEmailAddress.getText().toString();
//            String phoneNumber = etPhoneNumber.getText().toString();
//            if (userName.isEmpty() || userType.isEmpty() || emailAddress.isEmpty() || phoneNumber.isEmpty()) {
//                Toast.makeText(this, "Please fill in the form", Toast.LENGTH_SHORT).show();
//            } else {
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                mAuth.createUserWithEmailAndPassword(emailAddress, phoneNumber).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this, "The user is created successfully", Toast.LENGTH_SHORT).show();
//                        String key = mAuth.getCurrentUser().getUid();
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        Accountant accountant = new Accountant();
//                        accountant.setName(userName);
//                        accountant.setType(userType);
//                        accountant.setEmail(emailAddress);
//                        String phone = phoneNumber.replace(" ", "");
//                        accountant.setPassword(phone);
//                        accountant.setPhoneNumber(phoneNumber);
//                        database.getReference().child("Accountants").child(key).setValue(accountant).addOnCompleteListener(dbTask -> {
//                            if (dbTask.isSuccessful()) {
//                                reauthenticateAdmin(mAuth);
//                            } else {
//                                Toast.makeText(this, "Failed to create user in database", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
public void clickInvitation() {
    btnInvitation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = etUserName.getText().toString();
            String email = etEmailAddress.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(AdminAddNewUser.this, "Please fill in the form", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, phoneNumber).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                if (chooseRole == 0) {
                                    Accountant accountant = new Accountant();
                                    accountant.setName(name);
                                    accountant.setEmail(email);
                                    accountant.setPhoneNumber(phoneNumber);
                                    String phone = phoneNumber.replace(" ","");
                                    accountant.setPassword(phone);
                                    database.getReference("Accountants").child(uid).setValue(accountant);
                                    Toast.makeText(AdminAddNewUser.this, "Accountant created successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    User newUser = new User();
                                    newUser.setName(name);
                                    newUser.setEmail(email);
                                    newUser.setPhoneNumber(phoneNumber);
                                    String phone = phoneNumber.replace(" ","");
                                    newUser.setPassword(phone);
                                    database.getReference().child("Users").child(uid).setValue(newUser);
                                    Toast.makeText(AdminAddNewUser.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                //Intent intent = new Intent(AdminAddNewUser.this, AdminDashboardActivity.class);
                                //startActivity(intent);
                            }
                        } else {
                            Toast.makeText(AdminAddNewUser.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
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
