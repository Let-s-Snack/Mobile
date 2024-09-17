package com.example.lets_snack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ImageView imgSplash;
    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imgSplash = binding.imgSplash;
//        String linkImg = R.drawable.gif_splash;

        Glide.with(this)
                .load(R.drawable.gif_splash).centerCrop().into(imgSplash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() { openScreen(); }
        }, 3000);
    }
    public void openScreen(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}