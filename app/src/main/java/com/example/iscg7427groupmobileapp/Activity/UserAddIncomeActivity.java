package com.example.iscg7427groupmobileapp.Activity;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.iscg7427groupmobileapp.R;

import java.util.Calendar;

public class UserAddIncomeActivity extends AppCompatActivity {

    Button btnViewReceipt, btnChangeRecipt;
    ImageView imageView;
    Spinner spinner;
    EditText edt_date;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_income);

        init();

        // Open the receipt
        btnViewReceipt.setOnClickListener(this::selectImage);

        String[] options = {"Option 1", "Option 2", "Option 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // 执行相应操作
                if(selectedItem.equals("Option 1")){
                    Toast.makeText(UserAddIncomeActivity.this, "Option 1 selected", Toast.LENGTH_SHORT).show();
                }

                else if(selectedItem.equals("Option 2")){
                    Toast.makeText(UserAddIncomeActivity.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UserAddIncomeActivity.this, "Option 3 selected", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 未选择任何项目时的逻辑
            }
        });

        edt_date.setOnClickListener(v -> showDatePickerDialog());

    }
    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void init() {

        btnViewReceipt = findViewById(R.id.user_add_income_view_receipt);
        btnViewReceipt.setText(Html.fromHtml(getString(R.string.view_receipt), Html.FROM_HTML_MODE_LEGACY));
        btnChangeRecipt = findViewById(R.id.user_add_income_change_receipt);
        btnChangeRecipt.setText(Html.fromHtml(getString(R.string.change_receipt), Html.FROM_HTML_MODE_LEGACY));
        imageView = findViewById(R.id.user_add_income_image);
        spinner = findViewById(R.id.user_add_income_spinner);
        edt_date = findViewById(R.id.user_add_income_edt_date);



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            // 这里可以处理选中的图片URI，例如显示在ImageView中

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
                UserAddIncomeActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    // month1 is 0-based, add 1 to get the actual month number
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    edt_date.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    }
