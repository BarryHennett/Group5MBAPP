package com.example.iscg7427groupmobileapp.Activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iscg7427groupmobileapp.R;

public class StyleGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_guide);
        TextView underlineTextView = findViewById(R.id.underlineText);
        updateUnderlinedTextView(underlineTextView, "This is an underline text");
    }

    // Method to update TextView with underlined text
    private void updateUnderlinedTextView(TextView textView, String targetText) {
        String text = targetText;
        SpannableString spannableString = new SpannableString(text);

        // Apply UnderlineSpan to the entire text
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the modified SpannableString to the TextView
        textView.setText(spannableString);
    }
}
