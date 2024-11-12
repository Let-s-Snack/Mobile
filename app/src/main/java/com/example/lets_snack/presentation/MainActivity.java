package com.example.lets_snack.presentation;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivityMainBinding;
import com.example.lets_snack.presentation.BaseActivity;
import com.example.lets_snack.presentation.itensNavBar.HomeFragment;
import com.example.lets_snack.presentation.navBar.NavFragment;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;

    NavFragment navFragment = new NavFragment();
    HomeFragment homeFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction transactionMain = getSupportFragmentManager().beginTransaction();
        transactionMain.replace(R.id.mainContainer, homeFragment);
        transactionMain.commit();

        FragmentTransaction transactionNav = getSupportFragmentManager().beginTransaction();
        transactionNav.replace(R.id.navbarContainer, navFragment);
        transactionNav.commit();

//         Desativa o botão de voltar
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Deixe vazio para bloquear o botão de voltar do dispositivo
            }
        });
    }
}