package com.example.lets_snack.presentation.splashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivitySplashScreenBinding;
import com.example.lets_snack.presentation.login.LoginActivity;

public class SplashScreen extends AppCompatActivity {

    ImageView imgSplash;
    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imgSplash = binding.imgSplash;

        Glide.with(this)
                .load(R.drawable.gif_splash).centerCrop().into(imgSplash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> openScreen(), 3000);
    }

    //abrindo tela de login
    public void openScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}