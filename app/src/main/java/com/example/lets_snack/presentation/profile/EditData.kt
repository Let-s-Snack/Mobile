package com.example.lets_snack.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.PersonDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.databinding.ActivityEditDataBinding
import com.example.lets_snack.presentation.transform.RoundedTransformation
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private val personsRepository = PersonsRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        val user = FirebaseAuth.getInstance().currentUser
        setContentView(binding.root)
        val call = personsRepository.listPersonByEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
        call.enqueue(object : retrofit2.Callback<PersonDto> {
            override fun onResponse(
                call: retrofit2.Call<PersonDto>,
                response: retrofit2.Response<PersonDto>
            ) {
                if(response.code() == 200) {
                    binding.textInputLayout3.hint = response.body()?.nickname
                    binding.textInputLayout.hint = response.body()?.name
                    binding.textInputLayoutEmailLogin.hint = response.body()?.email
                    val listRestrictions = response.body()?.restrictions
                    listRestrictions?.forEach { restriction ->
                        val chip = Chip(this@EditData).apply {
                            text =restriction?.restrictionId
                            isCheckable = true
                            isClickable = true
                            isCloseIconVisible = true
                            setChipBackgroundColorResource(R.color.laranja_pastel)
                            setOnCloseIconClickListener {
                                binding.chipGroup.removeView(this)
                            }
                        }
                        binding.chipGroup.addView(chip)
                    }
                    binding.genderInput.hint = response.body()?.gender
                }
                Log.d("CallEditData", response.code().toString())
            }

            override fun onFailure(call: retrofit2.Call <PersonDto>, t: Throwable) {
                Log.e("CallEditDataError", t.message.toString())
            }
        })
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