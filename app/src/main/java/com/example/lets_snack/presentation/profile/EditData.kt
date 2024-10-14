package com.example.lets_snack.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lets_snack.R
import com.example.lets_snack.databinding.ActivityEditDataBinding
import com.example.lets_snack.presentation.transform.RoundedTransformation
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            if (currentUser.photoUrl != null) {

                Picasso.get()
                    .load(currentUser.photoUrl)
                    .resize(300, 300)
                    .centerCrop() // Centraliza e corta a imagem
                    .placeholder(R.drawable.profile_default)
                    .transform(RoundedTransformation(180, 0))
                    .into(binding.imageProfile)
            } else {
                binding.imageProfile.setImageResource(R.drawable.profile_default)
            }

        }
    }
}