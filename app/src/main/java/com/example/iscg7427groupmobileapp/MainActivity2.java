package com.example.iscg7427groupmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(v -> {

            String email = "accountant_1@gmail.com";
            String password = "123456";

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Accountants");

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // get the uid of the user from authentication
                                String uid = mAuth.getCurrentUser().getUid();

                                // create a new user in the realtime database as well
                                Accountant accountant = new Accountant();
                                mRef.child(uid).setValue(accountant);
                            }
                        }
                    });
        });


        Button btnLogIn = (Button) findViewById(R.id.btn_login);
        btnLogIn.setOnClickListener(v -> {

            EditText txtEmail =  findViewById(R.id.tv_email);
            EditText txtPassword = findViewById(R.id.tv_password);

            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            String uid = mAuth.getCurrentUser().getUid();
                            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

                            if (task.isSuccessful()) {
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String type = "";
                                        // traverse all the children of the root node
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.child(uid).exists()) {
                                                type = dataSnapshot.child(uid).child("type").getValue(String.class);
                                                break;
                                            }
                                        }
                                        switch (type) {
                                            case "Administer":
                                                Toast.makeText(MainActivity2.this, "Authentication success. Administer",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent1 = new Intent(MainActivity2.this, AdminTest.class);
                                                startActivity(intent1);
                                                break;
                                            case "User":
                                                Toast.makeText(MainActivity2.this, "Authentication success. User",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent2 = new Intent(MainActivity2.this, UserTest.class);
                                                intent2.putExtra("uid", uid);
                                                startActivity(intent2);
                                                break;
                                            case "Accountant":
                                                Intent intent3 = new Intent(MainActivity2.this, AccountantTest.class);
                                                intent3.putExtra("uid", uid);
                                                startActivity(intent3);

                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity2.this, "Authentication success. User",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
        });

        Button btnLogOut = (Button) findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String uid = mAuth.getCurrentUser().getUid();
            mAuth.signOut();
            Toast.makeText(MainActivity2.this, uid,
                    Toast.LENGTH_SHORT).show();
        });


    }
}