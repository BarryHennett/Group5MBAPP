package com.example.iscg7427groupmobileapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

public class UserTest extends AppCompatActivity {

    //  String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //    uid = getIntent().getStringExtra("uid");

        getData();

        Button btnAddTransaction = (Button) findViewById(R.id.user_btn_add_transaction);
        btnAddTransaction.setOnClickListener(v -> {

            String uid = getIntent().getStringExtra("uid");
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    User.Transaction transaction = new User.Transaction("Income", "Wage",
                            2000, new Date(), "Wage for June");

                    // This key serves as a reference for both the new transaction in the Realtime Database
                    // and the attachment image in Firebase Storage
                    String transactionKey = user.addNewTransaction(transaction);
                    // update user
                    mRef.setValue(user);
                    // create the attachment image in Firebase Storage
                    createAttachmentImage(transactionKey);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });


        });






    }

    private void createAttachmentImage(String transactionKey) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://group5-6aa2b.appspot.com");
        StorageReference mStorageRef = storage.getReference(transactionKey);
        // use a local image as for testing
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_attachment_2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            }
        });
    }

    private void getData(){
        String uid = getIntent().getStringExtra("uid");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                HashMap<String, User.Transaction> transactions = user.getTransactions();

                RecyclerView rvTransactions = findViewById(R.id.user_recycler_view);
                rvTransactions.setLayoutManager(new LinearLayoutManager(UserTest.this));
                rvTransactions.setAdapter(new AdapterTransaction(transactions,UserTest.this));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}