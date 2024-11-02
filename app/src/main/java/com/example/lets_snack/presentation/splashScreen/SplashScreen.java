package com.example.lets_snack.presentation.splashScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
                .load(R.drawable.gif_splash)
                .centerCrop()
                .into(imgSplash);

        // Solicita permissões
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Verifica se o dispositivo está no Android 13 (TIRAMISU) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                // Solicita a permissão de notificações se ainda não foi concedida
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1 // Código de solicitação de permissão
                );
            } else {
                // Se a permissão já foi concedida, continue para a tela de login
                openScreenAfterDelay();
            }
        } else {
            // Em versões anteriores ao Android 13, continue para a tela de login
            openScreenAfterDelay();
        }
    }

    // Executa a transição para a tela de login após o delay
    private void openScreenAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(this::openScreen, 3000);
    }

    // Método chamado após o resultado da solicitação de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, vai para a tela de login após o delay
                openScreenAfterDelay();
            } else {
                // Permissão negada, vai para a tela de login após o delay
                openScreenAfterDelay();
            }
        }
    }

    public void openScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
