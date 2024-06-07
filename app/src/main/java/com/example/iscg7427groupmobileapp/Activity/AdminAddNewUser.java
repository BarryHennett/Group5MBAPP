package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddNewUser extends AppCompatActivity {

    EditText etUserName,etEmailAddress,etPhoneNumber;
    TextView etUserType;
    Button btnInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_user);
        setUpAttributes();
        clickInvitation();
    }

    public void setUpAttributes(){
        etUserName = findViewById(R.id.etUserName);
        etUserType = findViewById(R.id.etUserType);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnInvitation = findViewById(R.id.btnInvitation);
    }

    public void clickInvitation(){
        btnInvitation.setOnClickListener(v -> {
            String userName = etUserName.getText().toString();
            String userType = etUserType.getText().toString();
            String emailAddress = etEmailAddress.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            if(userName.isEmpty() || userType.isEmpty() || emailAddress.isEmpty() || phoneNumber.isEmpty()){
                Toast.makeText(this, "Please fill in the form", Toast.LENGTH_SHORT).show();
            }else{
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(emailAddress, phoneNumber).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "The user is created successfully", Toast.LENGTH_SHORT).show();
                        String key = mAuth.getCurrentUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        Accountant accounant = new Accountant();
                        accounant.setName(userName);
                        accounant.setType(userType);
                        accounant.setEmail(emailAddress);
                        String phone = phoneNumber.replace(" ", "");;
                        accounant.setPassword(phone);
                        accounant.setPhoneNumber(phoneNumber);
                        database.getReference().child("Accountants").child(key).setValue(accounant);
                    }else{
                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}