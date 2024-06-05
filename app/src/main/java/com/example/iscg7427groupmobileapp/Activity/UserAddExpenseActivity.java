package com.example.iscg7427groupmobileapp.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserAddExpenseActivity extends AppCompatActivity {

    Button btnViewReceipt, btnChangeRecipt, btnSave;
    ImageButton btnReturn;
    ImageView imageView;
    Spinner spinner;
    EditText edt_date, edt_cost, edt_description;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_expense);

        init();
        // Open the photo collections to select a photo
        btnViewReceipt.setOnClickListener(this::selectImage);
        // same as above
        btnChangeRecipt.setOnClickListener(this::selectImage);
        // spinner for category
        String[] options = {"Option 1", "Option 2", "Option 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Option 1")) {
                    Toast.makeText(UserAddExpenseActivity.this, "Option 1 selected", Toast.LENGTH_SHORT).show();
                } else if (selectedItem.equals("Option 2")) {
                    Toast.makeText(UserAddExpenseActivity.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserAddExpenseActivity.this, "Option 3 selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // date picker
        edt_date.setOnClickListener(v -> showDatePickerDialog());
        // save new transaction
        btnSave.setOnClickListener(v -> {

            String dateSelected = edt_date.getText().toString();
            String description = edt_description.getText().toString();
            String cost = edt_cost.getText().toString();
            String category = spinner.getSelectedItem().toString();
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
            if (date == null || description.isEmpty() || cost.isEmpty()) {
                Toast.makeText(UserAddExpenseActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // save to realtime database
// hardcode for test
            String uid = "ziWsT2nePuTMV4zNh2iGDDvLYIy1";
            DatabaseReference mRef = database.getReference("Users").child(uid);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    User.Transaction transaction = new User.Transaction("Expense", category, Double.parseDouble(cost), finalDate, description);
                    String transactionKey = user.addNewTransaction(transaction);
                    mRef.setValue(user);
                    // save image to storage
                    if (imageView.getDrawable() != null) {
                        createAttachmentImage(transactionKey);
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        btnReturn.setOnClickListener(v -> {
            finish();
        });
    }

    private void init() {
        btnViewReceipt = findViewById(R.id.user_add_expense_view_receipt);
        btnViewReceipt.setText(Html.fromHtml(getString(R.string.view_receipt), Html.FROM_HTML_MODE_LEGACY));
        btnChangeRecipt = findViewById(R.id.user_add_expense_change_receipt);
        btnChangeRecipt.setText(Html.fromHtml(getString(R.string.change_receipt), Html.FROM_HTML_MODE_LEGACY));
        btnReturn = findViewById(R.id.user_add_expense_btn_return);
        btnSave = findViewById(R.id.user_add_expense_btn_save);
        imageView = findViewById(R.id.user_add_expense_image);
        spinner = findViewById(R.id.user_add_expense_spinner);
        edt_date = findViewById(R.id.user_add_expense_edt_date);
        edt_description = findViewById(R.id.user_add_expense_edt_description);
        edt_cost = findViewById(R.id.user_add_expense_edt_cost);
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
                UserAddExpenseActivity.this,
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

    private void createAttachmentImage(String transactionKey) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://group5-6aa2b.appspot.com");
        StorageReference mStorageRef = storage.getReference(transactionKey);
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
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(UserAddExpenseActivity.this, "Receipt Upload Success", Toast.LENGTH_SHORT).show();
            }
        });
    }
}