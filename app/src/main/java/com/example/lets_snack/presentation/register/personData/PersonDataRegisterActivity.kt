package com.example.lets_snack.presentation.register.personData

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.lets_snack.presentation.register.photo.PhotoRegister
import com.example.lets_snack.R
import com.example.lets_snack.data.remote.dto.PersonDtoResponse
import com.example.lets_snack.data.remote.repository.rest.PersonsRepository
import com.example.lets_snack.databinding.ActivityPersonDataRegisterBinding
import com.example.lets_snack.presentation.login.LoginActivity
import java.util.Calendar

class PersonDataRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonDataRegisterBinding
    private var personsRepository = PersonsRepository(this)
    private var nameTouched = false
    private var usernameTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDataRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val genderInput = binding.genderInputText
        val genders = arrayOf("feminino", "masculino", "outro", "prefiro não dizer")
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, R.id.dropdownText, genders)
        genderInput.setAdapter(adapter)

        genderInput.setOnClickListener {
            genderInput.dropDownWidth = ViewGroup.LayoutParams.WRAP_CONTENT
            genderInput.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
            genderInput.showDropDown()
            genderInput.dropDownVerticalOffset = 10
        }

        genderInput.setOnItemClickListener { _, _, _, _ ->
            updateButtonState()
        }

        binding.registerText.setOnClickListener {
            startLoginActivity()
        }

        binding.textInputLayout4.setEndIconOnClickListener {
            showDatePicker()
        }

        binding.checkBox.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        setupTextWatchers()
    }

    private fun updateButtonState() {
        val isAllFieldsValid = isAllFieldsValid()

        binding.loginEnter.isEnabled = isAllFieldsValid

        binding.loginEnter.setOnClickListener {
            checkUsernameAvailability(binding.usernameInput.text.toString()) { isAvailable ->
                if (isAvailable) {
                    binding.textInputLayout3.error = "Já existe um usuário com esse nome de usuário."
                } else {
                    if (isAllFieldsValid) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.loginEnter.text = ""
                        binding.loginEnter.isEnabled = false
                        startPhotoRegister()
                    }
                }
            }
        }
    }

    private fun isAllFieldsValid(): Boolean {
        val name = binding.nameInput.text.toString()
        val username = binding.usernameInput.text.toString()

        val isNameValid = validateName(name)
        val isUsernameValid = validateUsername(username)

        val gender = binding.genderInputText.text.toString()
        val dateOfBirth = binding.dateOfBirthInput.text.toString()
        val checkbox = binding.checkBox.isChecked

        return isNameValid && isUsernameValid &&
                gender.isNotEmpty() && dateOfBirth.isNotEmpty() && checkbox
    }

    private fun validateName(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                if (nameTouched) {
                    binding.textInputLayout.error = "Nome deve ter pelo menos 1 caractere"
                }
                false
            }
            name.length > 45 -> {
                if (nameTouched) {
                    binding.textInputLayout.error = "Nome não pode ter mais que 45 caracteres"
                }
                false
            }
            else -> {
                binding.textInputLayout.error = null
                true
            }
        }
    }

    fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        val call = personsRepository.listPersonByUsername(username)
        call.enqueue(object : retrofit2.Callback<PersonDtoResponse> {
            override fun onResponse(
                call: retrofit2.Call<PersonDtoResponse>,
                response: retrofit2.Response<PersonDtoResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    // Verifica se a mensagem indica que o usuário não existe
                    if (apiResponse?.message == "Apelido do usuário não existe") {
                        callback(false) // Nome de usuário não existe
                    } else {
                        callback(true) // Nome de usuário já existe
                    }
                } else {
                    Log.e("Username Check", "Erro na resposta: ${response.errorBody()?.string()}")
                    callback(false) // Em caso de erro, pode considerar como não existente
                }
            }

            override fun onFailure(call: retrofit2.Call<PersonDtoResponse>, t: Throwable) {
                Log.e("Username Check", "Erro na chamada: ${t.message}")
                callback(false) // Em caso de falha, pode considerar como não existente
            }
        })
    }


    private fun validateUsername(username: String): Boolean {
        return when {
            username.isEmpty() -> {
                if (usernameTouched) {
                    binding.textInputLayout3.error = "Username deve ter pelo menos 1 caractere"
                }
                false
            }
            username.length > 200 -> {
                if (usernameTouched) {
                    binding.textInputLayout3.error = "Username não pode ter mais que 200 caracteres"
                }
                false
            }
            else -> {
                binding.textInputLayout3.error = null
                true
            }
        }
    }

    private fun setupTextWatchers() {
        binding.nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameTouched = true
            } else {
                validateName(binding.nameInput.text.toString())
            }
        }

        binding.usernameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                usernameTouched = true
            } else {
                validateUsername(binding.usernameInput.text.toString())
            }
        }

        val fields = listOf(
            binding.nameInput,
            binding.genderInputText,
            binding.dateOfBirthInput,
            binding.usernameInput
        )

        for (field in fields) {
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateButtonState()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun startLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val maxCalendar = Calendar.getInstance()
        maxCalendar.set(Calendar.YEAR, year - 12)

        val datePickerDialog = DatePickerDialog(this,
            R.style.CustomDatePickerDialog, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.dateOfBirthInput.setText(formattedDate)
                updateButtonState()
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis

        datePickerDialog.setOnShowListener {
            val positiveButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negativeButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
        }

        datePickerDialog.show()
    }

    private fun startPhotoRegister() {
        val bundle = intent.getBundleExtra("bundleRegister")
        val intent = Intent(this, PhotoRegister::class.java)
        bundle?.apply {
            putString("name", binding.nameInput.text.toString())
            putString("gender", binding.genderInputText.text.toString())
            putString("dateOfBirth", binding.dateOfBirthInput.text.toString())
            putString("username", binding.usernameInput.text.toString())
        }

        bundle?.let {
            val name = it.getString("name")
            val gender = it.getString("gender")
            val dateOfBirth = it.getString("dateOfBirth")
            val username = it.getString("username")
            val email = it.getString("email")
            val phone = it.getString("phone")
            val password = it.getString("password")

            Log.d("BundlePersonData", "Name: $name, Gender: $gender, Date of Birth: $dateOfBirth, Username: $username, Email: $email, Phone: $phone, Password: $password")
        } ?: Log.d("Bundle", "Bundle is null")

        intent.putExtra("bundleRegister", bundle)
        startActivity(intent)
    }
}
