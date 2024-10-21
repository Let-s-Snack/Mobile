package com.example.lets_snack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.lets_snack.databinding.ActivityMainBinding;
import com.example.lets_snack.presentation.itensNavBar.HomeFragment;
import com.example.lets_snack.presentation.navBar.NavFragment;

public class MainActivity extends AppCompatActivity {

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

        hideSystemUI();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
