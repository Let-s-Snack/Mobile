package com.example.lets_snack.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.lets_snack.R;

public class IA extends AppCompatActivity {

    WebView web;
    ProgressBar load;
    ImageButton returnBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);
        web = findViewById(R.id.web_view_ia);
        load = findViewById(R.id.progress_bar_ia);
        returnBtn = findViewById(R.id.web_ia_return_btn);

        web.loadUrl("http://ec2-52-20-248-152.compute-1.amazonaws.com:3000/ia");
        web.getSettings().setJavaScriptEnabled(true);

        returnBtn.setOnClickListener(v -> finish());

        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                load.setVisibility(View.INVISIBLE);
            }
        });
    }
}