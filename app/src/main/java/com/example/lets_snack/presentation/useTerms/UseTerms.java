package com.example.lets_snack.presentation.useTerms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.lets_snack.R;

public class UseTerms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_terms);

        ((ImageButton) findViewById(R.id.return_btn)).setOnClickListener(v -> finish());
    }
}