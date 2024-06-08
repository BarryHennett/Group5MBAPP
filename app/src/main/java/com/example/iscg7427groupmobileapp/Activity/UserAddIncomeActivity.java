package com.example.iscg7427groupmobileapp.Activity;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class UserAddIncomeActivity extends AppCompatActivity {

    Button  btnSave;
    TextView btnViewReceipt, btnChangeRecipt;
    ImageButton btnReturn;
    ImageView imageView;
    Spinner spinner;
    EditText edt_date, edt_income, edt_description;
    NavigationBarView nav;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;
    private BottomNavigationView bottomNavigation;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_income);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        setupBottomNavigation();

        updateUnderlinedTextView(btnViewReceipt, "Upload receipt");
        updateUnderlinedTextView(btnChangeRecipt, "Change receipt");

        // Open the photo collections to select a photo
        btnViewReceipt.setOnClickListener(this::selectImage);
        // same as above
        btnChangeRecipt.setOnClickListener(this::selectImage);
        // spinner for category
        String[] options = {"Salary", "Bonus", "Investment", "Freelance", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, options);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Last 3 Month")) {

                } else if (selectedItem.equals("Last 6 Month")) {

                } else {

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
            String income = edt_income.getText().toString();
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
            if (date == null || description.isEmpty() || income.isEmpty()) {
                Toast.makeText(UserAddIncomeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // save to realtime database
            DatabaseReference mRef = database.getReference("Users").child(uid);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    User.Transaction transaction = new User.Transaction("Income", category, Double.parseDouble(income), finalDate, description);
                    String transactionKey = user.addNewTransaction(transaction);
                    mRef.setValue(user);
                    // save image to storage
                    if (imageView.getDrawable() != null) {
                        createAttachmentImage(transactionKey);
                    }
                    Toast.makeText(UserAddIncomeActivity.this, "Income Added", Toast.LENGTH_SHORT).show();
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

        btnViewReceipt = findViewById(R.id.user_add_income_view_receipt);
        btnChangeRecipt = findViewById(R.id.user_add_income_change_receipt);
        btnReturn = findViewById(R.id.user_add_income_btn_return);
        btnSave = findViewById(R.id.user_add_income_btn_save);
        imageView = findViewById(R.id.user_add_income_image);
        spinner = findViewById(R.id.user_add_income_spinner);
        edt_date = findViewById(R.id.user_add_income_edt_date);
        edt_description = findViewById(R.id.user_add_income_edt_description);
        edt_income = findViewById(R.id.user_add_income_edt_income);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);

                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    startActivity(new Intent(UserAddIncomeActivity.this, UserDashboardActivity_1.class));
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    startActivity(new Intent(UserAddIncomeActivity.this, UserExpenseDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    startActivity(new Intent(UserAddIncomeActivity.this, UserProfileActivity.class));
                    return true;
                } else {
                    Log.d("BottomNav", "Unknown item selected");
                    return false;
                }
            }
        });

        // Set the selected item as item_profile
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigation.setSelectedItemId(R.id.item_income);
            }
        });
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
    private void updateUnderlinedTextView(TextView textView, String targetText) {
        String text = targetText;
        SpannableString spannableString = new SpannableString(text);

        // Apply UnderlineSpan to the entire text
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the modified SpannableString to the TextView
        textView.setText(spannableString);
    }
    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                UserAddIncomeActivity.this,
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
                Toast.makeText(UserAddIncomeActivity.this, "Receipt Upload Success", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
