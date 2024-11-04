package com.example.lets_snack.presentation.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.PersonDtoResponseEmail
import com.example.lets_snack.data.remote.dto.RestrictionsDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.data.remote.repository.rest.RestrictionsRepository
import com.example.lets_snack.databinding.ActivityEditDataBinding
import com.example.lets_snack.presentation.transform.RoundedTransformation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private val restrictionsRepository = RestrictionsRepository()
    private val personsRepository = PersonsRepository()
    private var chipGroup: ChipGroup? = null
    private var restrictionsArray:  ArrayList<String> =  ArrayList()
    private var restrictionListObject: List<RestrictionsDto>? = listOf()
    private lateinit var adapterTypeRestrictions: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val typeRestriction = binding.typeRestrictionInput
        chipGroup = binding.chipGroup
        adapterTypeRestrictions = ArrayAdapter(this, android.R.layout.simple_list_item_checked, restrictionsArray)
        typeRestriction.setAdapter(adapterTypeRestrictions)
        typeRestriction.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        val call = restrictionsRepository.getRestrictions()
        call.enqueue(object : retrofit2.Callback<List<RestrictionsDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<RestrictionsDto>>,
                response: retrofit2.Response<List<RestrictionsDto>>
            ) {
                restrictionListObject = response.body()
                val restrictionsList: List<String>? = response.body()?.map { it.name }
                restrictionsList?.let {
                    restrictionsArray.clear()
                    restrictionsArray.addAll(it)
                    adapterTypeRestrictions.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<RestrictionsDto>>, t: Throwable) {
                Log.e("CallRestrictionsError", t.message.toString())
            }
        })

        val callPersons = personsRepository.listPersonByEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
        callPersons.enqueue(object : retrofit2.Callback<PersonDtoResponseEmail> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponseEmail>,
                response: retrofit2.Response<PersonDtoResponseEmail>
            ) {
                if(response.code() == 200) {
                    binding.textInputLayout3.hint = response.body()?.nickname
                    binding.textInputLayout.hint = response.body()?.name
                    binding.textInputLayoutEmailLogin.hint = response.body()?.email
                    val listRestrictions = response.body()?.restrictions
                    listRestrictions?.forEach { restriction ->
                        val chip = Chip(this@EditData).apply {
                            text =restriction?.name
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

            override fun onFailure(call: retrofit2.Call <PersonDtoResponseEmail>, t: Throwable) {
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