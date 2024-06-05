package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    FirebaseDatabase database;
    EditText etName, etEmail, etPassword;
    TextView tvAccount, tvUser;
    Button btnSignUp;
    String name, email, password, role;
    int chooseRole = 0;
    private TextView toLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        toLogin= findViewById(R.id.toLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
        setUpAttributes();
        clickSignUp();
        clickTvAccount();
        clickTvUser();

    }

    public void setUpAttributes() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvAccount = findViewById(R.id.tvAccount);
        tvUser = findViewById(R.id.tvUser);
        btnSignUp = findViewById(R.id.btnSignUp);
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

    public void clickSignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in the form first", Toast.LENGTH_SHORT).show();
                } else {
                    database = FirebaseDatabase.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String uid = mAuth.getCurrentUser().getUid();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                if (chooseRole == 0) {
                                    Accountant accountant = new Accountant();
                                    accountant.setName(name);
                                    database.getReference("Accountants").setValue(accountant);
                                    Toast.makeText(SignUp.this, "Accountant created successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    User user = new User();
                                    user.setName(name);
                                    user.setPassword(password);
                                    database.getReference().child("Users").child(uid).setValue(user);
                                    Toast.makeText(SignUp.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}