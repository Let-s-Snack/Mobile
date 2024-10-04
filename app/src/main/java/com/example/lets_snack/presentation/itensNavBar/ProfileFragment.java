package com.example.lets_snack.presentation.itensNavBar;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lets_snack.R;
import com.example.lets_snack.databinding.FragmentProfileBinding;
import com.example.lets_snack.presentation.login.LoginActivity;
import com.example.lets_snack.presentation.transform.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getPhotoUrl() != null) {

                Picasso.get()
                        .load(currentUser.getPhotoUrl())
                        .resize(300, 300)
                        .centerCrop() // Centraliza e corta a imagem
                        .placeholder(R.drawable.profile_default)
                        .transform(new RoundedTransformation(180, 0) )
                        .into(binding.profileImage);
            } else {
                binding.profileImage.setImageResource(R.drawable.profile_default);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fazendo logout
        binding.logoutBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
