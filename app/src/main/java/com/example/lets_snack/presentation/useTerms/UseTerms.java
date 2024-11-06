package com.example.lets_snack.presentation.useTerms;


import android.os.Bundle;
import android.widget.ImageButton;

import com.example.lets_snack.R;
import com.example.lets_snack.presentation.BaseActivity;

public class UseTerms extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_terms);

        ((ImageButton) findViewById(R.id.return_btn)).setOnClickListener(v -> finish());
    }
}