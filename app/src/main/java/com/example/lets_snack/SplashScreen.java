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
//    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//    String savedToken = sharedPreferences.getString("auth_token", null);

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
//        if (savedToken != null) {
////            Intent intent = new Intent(this, MainActivity.class); colocar a tela inicial aqui quando estiver pronta
////            startActivity(intent);
//            finish();
//        } else {
//            // Redirecionar o usuário para a tela de login
////            Intent intent = new Intent(this, LoginActivity.class); colocar a tela de login aqui quando estiver pronta
////            startActivity(intent);
//            finish();
//        }
    }
}