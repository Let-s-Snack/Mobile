package com.example.lets_snack;

import androidx.appcompat.app.AppCompatActivity;

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
        String linkImg = "https://media1.giphy.com/media/j5gxrTMpUbjnRNX10h/giphy.gif?cid=6c09b9520pdvvqye1oi5g71fmttpw9ppmbhwcaz0b1rvfref&ep=v1_internal_gif_by_id&rid=giphy.gif&ct=g";
        Glide.with(this)
                .load(linkImg).centerCrop().into(imgSplash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() { openScreen(); }
        }, 3000);
    }
    public void openScreen(){

    }
}