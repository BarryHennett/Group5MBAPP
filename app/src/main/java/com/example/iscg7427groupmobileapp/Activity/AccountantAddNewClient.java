package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AccountantAddNewClient extends AppCompatActivity {
    EditText etUserName,etOccupation,etIndustry,etEmailAddress,etPhoneNumber;
    Button btnInvite;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant_add_new_client);
        setUpAttributes();
        clickBtnInvite();
    }

    public void setUpAttributes(){
        etUserName = findViewById(R.id.etUserName);
        etOccupation = findViewById(R.id.etOccupation);
        etIndustry = findViewById(R.id.etIndustry);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnInvite = findViewById(R.id.btnInvite);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void clickBtnInvite(){
        btnInvite.setOnClickListener(v -> {
            String name = etUserName.getText().toString();
            String occupation = etOccupation.getText().toString();
            String industry = etIndustry.getText().toString();
            String email = etEmailAddress.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            if(name.isEmpty() || occupation.isEmpty() || industry.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()){
                Toast.makeText(this, "Please fill in the form ", Toast.LENGTH_SHORT).show();
            }else{
                FirebaseAuth mAuth = FirebaseAuth.getInstance();mAuth.createUserWithEmailAndPassword(email, phoneNumber).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            String key = mAuth.getCurrentUser().getUid(); // becuase we use createUserWithEmailAndPassword, which means the new user will be logged in
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            Toast.makeText(AccountantAddNewClient.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            database.getReference().child("Accountants").child(uid).child("users").setValue(key);
                            User user = new User();
                            user.setName(name);
                            user.setOccupation(occupation);
                            user.setIndustry(industry);
                            user.setEmail(email);
                            user.setPhoneNumber(phoneNumber);
                            database.getReference().child("Users").setValue(user);
                        }else{
                            Toast.makeText(AccountantAddNewClient.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast.makeText(this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();

                //Intent to another page
            }
        });
    }
}