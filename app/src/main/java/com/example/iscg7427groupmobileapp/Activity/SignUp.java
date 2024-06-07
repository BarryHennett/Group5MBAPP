package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignUpActivity";
    ImageView imEye;
    EditText etName, etEmail, etPassword;
    Button btnSignUp;
    TextView tvAccount, tvUser, toLogin;
    SignInButton btnGoogle;
    Boolean passwordVis = false;
    int chooseRole = 0;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setUpAttributes();
        setUpGoogleSignIn();
        clickBtnSignUp();
        clickTvAccount();
        clickTvUser();
        clickLogin();
        clickEyes();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void setUpAttributes() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvUser = findViewById(R.id.tvUser);
        tvAccount = findViewById(R.id.tvAccount);
        btnGoogle = findViewById(R.id.btnGoogle);
        toLogin = findViewById(R.id.toLogin);
        imEye = findViewById(R.id.ivEye);
        mAuth = FirebaseAuth.getInstance();
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

    public void clickBtnSignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in the form", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener() {
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
                                        accountant.setPassword(password);
                                        database.getReference("Accountants").child(uid).setValue(accountant);
                                        Toast.makeText(SignUp.this, "Accountant created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this, Login.class);
                                        startActivity(intent);
                                    } else {
                                        User newUser = new User();
                                        newUser.setName(name);
                                        newUser.setEmail(email);
                                        newUser.setPassword(password);
                                        database.getReference().child("Users").child(uid).setValue(newUser);
                                        Toast.makeText(SignUp.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(SignUp.this, Login.class);
                                        startActivity(intent1);
                                    }
                                }
                            } else {
                                Toast.makeText(SignUp.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        String key = user.getUid();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference().child("Users").child(key);

                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(SignUp.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, UserDashboardActivity_1.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUp.this, "User not found. Please register first.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    Intent intent = new Intent(SignUp.this, Login.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(SignUp.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(SignUp.this, "ERROR: Couldn't log in with Google", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clickLogin(){
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public void clickEyes(){
        imEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!passwordVis){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imEye.setImageResource(R.drawable.password_show);
                }else{
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imEye.setImageResource(R.drawable.password_hide);
                }
                etPassword.setTransformationMethod(passwordVis ?
                        android.text.method.PasswordTransformationMethod.getInstance() :
                        android.text.method.HideReturnsTransformationMethod.getInstance());
                // Move the cursor to the end of the text
                etPassword.setSelection(etPassword.length());
                // Toggle the passwordVisible boolean
                passwordVis = !passwordVis;
            }
        });
    }
}
