package com.example.iscg7427groupmobileapp.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AccountantClientTransactionDetail extends AppCompatActivity {

    Button btnViewReceipt, btnChangeRecipt, btnSave, btnDelete;
    ImageButton btnReturn;
    ImageView imageView;
    EditText edt_date, edt_amount, edt_description;
    TextView txt_category;
    NavigationBarView nav;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;
    String uid;
    String transactionId;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountant_client_transaction_detail);

        uid = getIntent().getStringExtra("uid");
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        transactionId = getIntent().getStringExtra("transactionId");
        mStorageRef = FirebaseStorage.getInstance("gs://group5-6aa2b.appspot.com").getReference(transactionId);

        init();
        getTransactionDetails(transactionId);
        // Open the photo collections to select a photo
        btnViewReceipt.setOnClickListener(this::selectImage);
        // same as above
        btnChangeRecipt.setOnClickListener(this::selectImage);

        // date picker
        edt_date.setOnClickListener(v -> showDatePickerDialog());
        // save new transaction
        btnSave.setOnClickListener(v -> {

            String dateSelected = edt_date.getText().toString();
            String description = edt_description.getText().toString();
            String amount = edt_amount.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = null;
            // convert a String Type into a Date Type
            try {
                date = sdf.parse(dateSelected);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date finalDate = date;
            // validate inout
            if (date == null || description.isEmpty() || amount.isEmpty()) {
                Toast.makeText(AccountantClientTransactionDetail.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // save to realtime database
            DatabaseReference mRef = database.getReference("Users").child(uid).child("transactions").child(transactionId);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User.Transaction transaction = dataSnapshot.getValue(User.Transaction.class);
                    if (transaction != null) {
                        transaction.setDate(finalDate);
                        transaction.setDescription(description);
                        transaction.setAmount(Double.parseDouble(amount));
                        mRef.setValue(transaction);

                        if (imageView.getDrawable() != null) {
                            createAttachmentImage();
                        }

                        Toast.makeText(AccountantClientTransactionDetail.this, "Transaction Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        btnDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this transaction?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DatabaseReference mRef = database.getReference("Users").child(uid).child("transactions").child(transactionId);
                    mRef.removeValue();
                    if (mStorageRef != null) {
                        mStorageRef.delete();
                    }
                    Toast.makeText(AccountantClientTransactionDetail.this, "Transaction Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.create().show();
        });

        btnReturn.setOnClickListener(v -> {
            finish();
        });

    }
    private void init() {
        btnViewReceipt = findViewById(R.id.accountant_client_transaction_detail_btn_view_receipt);
        btnViewReceipt.setText(Html.fromHtml(getString(R.string.view_receipt), Html.FROM_HTML_MODE_LEGACY));
        btnChangeRecipt = findViewById(R.id.accountant_client_transaction_detail_btn_change_receipt);
        btnChangeRecipt.setText(Html.fromHtml(getString(R.string.change_receipt), Html.FROM_HTML_MODE_LEGACY));
        btnReturn = findViewById(R.id.accountant_client_transaction_detail_btn_return);
        btnSave = findViewById(R.id.accountant_client_transaction_detail_btn_save);
        btnDelete = findViewById(R.id.accountant_client_transaction_detail_btn_delete);
        imageView = findViewById(R.id.accountant_client_transaction_detail_image);
        edt_date = findViewById(R.id.accountant_client_transaction_detail_edt_date);
        edt_description = findViewById(R.id.accountant_client_transaction_detail_edt_description);
        edt_amount = findViewById(R.id.accountant_client_transaction_detail_edt_amount);
        txt_category = findViewById(R.id.accountant_client_transaction_detail_txt_category);
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AccountantClientTransactionDetail.this,
                (view, year1, month1, dayOfMonth) -> {
                    // month1 is 0-based, add 1 to get the actual month number
                    // thanks to GPT
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    edt_date.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void createAttachmentImage() {

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        // start upload process
        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(AccountantClientTransactionDetail.this, "Receipt Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(AccountantClientTransactionDetail.this, "Receipt Upload Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTransactionDetails(String transactionId) {
        DatabaseReference mRef = database.getReference("Users").child(uid).child("transactions").child(transactionId);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.Transaction transaction = dataSnapshot.getValue(User.Transaction.class);
                if (transaction != null) {
                    edt_date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.getDate()));
                    edt_description.setText(transaction.getDescription());
                    edt_amount.setText(String.valueOf(transaction.getAmount()));
                    txt_category.setText(transaction.getCategory());

                    Glide.with(AccountantClientTransactionDetail.this)
                            .load(mStorageRef)
                            .into(imageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}