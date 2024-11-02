package com.example.lets_snack.presentation.itensNavBar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.lets_snack.R;
import com.example.lets_snack.databinding.FragmentHomeBinding;
import com.example.lets_snack.presentation.transform.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("email", param1);
        args.putString("username", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            binding.nameUser.setText("Olá, "+currentUser.getDisplayName());
            if (currentUser.getPhotoUrl() != null) {

                Picasso.get()
                        .load(currentUser.getPhotoUrl())
                        .resize(300, 300)
                        .centerCrop() // Centraliza e corta a imagem
                        .placeholder(R.drawable.profile_default)
                        .transform(new RoundedTransformation(180, 0) )
                        .into(binding.imageUser2);
            } else {
                binding.imageUser2.setImageResource(R.drawable.profile_default);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
