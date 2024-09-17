package com.example.lets_snack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    NavFragment navFragment = new NavFragment();

    HomeFragment homeFragment = new HomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //iniciar na home
        FragmentTransaction transactionMain = getSupportFragmentManager().beginTransaction();
        transactionMain.replace(R.id.mainContainer, homeFragment);
        transactionMain.commit();

        //iniciar navbar
        FragmentTransaction transactionNav = getSupportFragmentManager().beginTransaction();
        transactionNav.replace(R.id.navbarContainer, navFragment);
        transactionNav.commit();
    }
}