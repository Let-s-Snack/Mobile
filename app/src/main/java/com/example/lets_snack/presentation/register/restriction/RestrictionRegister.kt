package com.example.lets_snack.presentation.register.restriction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.lets_snack.MainActivity
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.PersonDto
import com.example.lets_snack.data.remote.dto.RestrictionsDto
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.data.remote.repository.rest.RestrictionsRepository
import com.example.lets_snack.databinding.ActivityRestrictionRegisterBinding
import com.example.lets_snack.presentation.BaseActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RestrictionRegister : BaseActivity() {
    private lateinit var binding: ActivityRestrictionRegisterBinding
    private val restrictionsRepository = RestrictionsRepository()
    private val personsRepository = PersonsRepository()
    private var chipGroup: ChipGroup? = null
    private var restrictionsArray:  ArrayList<String> =  ArrayList()
    private var restrictionListObject: List<RestrictionsDto>? = listOf()
    private lateinit var adapterTypeRestrictions: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestrictionRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateButtonState()
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



        val ingredientsRestriction = binding.ingredientsInput
        val ingredients = arrayOf("Arroz", "Feijão", "Carne", "Leite","Frango","Macarrão","Batata","Cenoura","Alface","Tomate")
        val adapterIngredientsRestriction = ArrayAdapter(this, android.R.layout.simple_list_item_checked, ingredients)
        ingredientsRestriction.setAdapter(adapterIngredientsRestriction)
        val chipGroup2 = binding.chipGroup2
        ingredientsRestriction.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        val checkbox = binding.checkBox

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        typeRestriction.addTextChangedListener(textWatcher)
        ingredientsRestriction.addTextChangedListener(textWatcher)

        checkbox.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        typeRestriction.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (!chipExists(selectedItem, chipGroup!!)) {
                addChipToGroup(selectedItem, chipGroup!!)
            }
            typeRestriction.text.clear()
            updateButtonState()
        }

        ingredientsRestriction.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if (!chipExists(selectedItem, chipGroup2)) {
                addChipToGroup(selectedItem, chipGroup2)
            }
            ingredientsRestriction.text.clear()
            updateButtonState()
        }
    }

    private fun addChipToGroup(text: String, chipGroup: ChipGroup) {
        val chip = Chip(this).apply {
            setText(text)
            isCloseIconVisible = true
            setChipBackgroundColorResource(R.color.laranja_pastel)
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                updateButtonState()}
        }
        chipGroup.addView(chip)
    }

    private fun chipExists(text: String, chipGroup: ChipGroup): Boolean {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.text == text) {
                return true
            }
        }
        return false
    }

    private fun updateButtonState() {
        val isFieldFilled = binding.chipGroup.childCount > 0 || binding.chipGroup2.childCount > 0
        if(isFieldFilled) {
            val drawable = binding.checkBox.buttonDrawable
            drawable?.setTint(ContextCompat.getColor(this, R.color.cinza))
            binding.checkBox.buttonDrawable = drawable
        }
        val isCheckboxChecked = binding.checkBox.isChecked
        binding.loginEnter.isEnabled = isFieldFilled || isCheckboxChecked
        binding.loginEnter.setOnClickListener {
            val bundle = intent.getBundleExtra("bundleRegister")
            if (bundle != null) {
                val name = bundle.getString("name")
                val gender = bundle.getString("gender")
                val dateOfBirth = bundle.getString("dateOfBirth")
                val username = bundle.getString("username")
                val email = bundle.getString("email")
                val phone = bundle.getString("phone")
                val password = bundle.getString("password")
                val photo = bundle.getString("photo")
                restrictionListObject.let {
                    val chipSelected =chipGroup?.children?.filterIsInstance<Chip>()
                        ?.map {
                            it.text.toString()
                        }
                    it?.filter {
                        chipSelected?.contains(it.name) == true
                    }
                    val formattedDate = formatDate(dateOfBirth!!)
                    Log.d("FormattedDate",formattedDate!!)
                    val objectPerson = PersonDto(gender!!,name!!,username!!,email!!,password!!,false,photo!!,
                        formattedDate!!,phone!!,true,it!!)
                    insertUserMongo(objectPerson)
                }

                insertUser(email!!, password!!, name!!, username!!, photo!!)
                Log.e("BundleRestriction", "Name: $name, Gender: $gender, Date of Birth: $dateOfBirth, Username: $username, Email: $email, Phone: $phone, Password: $password, Photo: $photo")
            } else {
                Log.e("Bundle", "Bundle is null")
            }
        }
    }

    private fun insertUser(email: String, password: String, name: String, username: String, photo:String) {
        val autenticator = FirebaseAuth.getInstance()
        Log.e("Restriction", "Email: $email Password: $password Name: $name Username: $username Photo: $photo")
        autenticator.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful){
                val userLogin = autenticator.currentUser
                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse(photo))
                    .build()
                userLogin?.updateProfile(profile)?.addOnCompleteListener {
                    if(it.isSuccessful){
                        startMainActivity()
                    }
                    else{
                        Log.e("Erro",it.exception?.message.toString())
                        Toast.makeText(this, "Erro ao atualizar perfil" + it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Log.e("Erro",it.exception?.message.toString())
                Toast.makeText(this, "Erro ao registrar" + it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startMainActivity() {
        val bundle = intent.getBundleExtra("bundleRegister")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("bundleRegister", bundle)
        startActivity(intent)
    }

    private fun insertUserMongo(personDto: PersonDto){
        Log.d("CallPersons", personDto.toString())
        val call = personsRepository.insertPerson(personDto)
        call.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(
                call: retrofit2.Call<String>,
                response: retrofit2.Response<String>
            ) {
               Log.d("CallPersons", response.code().toString())
            }

            override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                Log.e("CallPersonsError", t.message.toString())
            }
        })
    }

    fun formatDate(dateOfBirth: String): String? {
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return try {
            val parsedDate: Date? = inputDateFormat.parse(dateOfBirth)
            if (parsedDate != null) {
                // Define Locale.US para garantir que o mês não seja convertido em texto
                val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                outputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                outputDateFormat.format(parsedDate)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}