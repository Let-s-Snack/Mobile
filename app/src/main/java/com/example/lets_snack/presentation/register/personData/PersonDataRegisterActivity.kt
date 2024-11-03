package com.example.lets_snack.presentation.register.personData

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.lets_snack.presentation.register.photo.PhotoRegister
import com.example.lets_snack.R
import com.example.lets_snack.databinding.ActivityPersonDataRegisterBinding
import java.util.Calendar

class PersonDataRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonDataRegisterBinding

    // Variáveis para rastrear se o campo foi tocado
    private var nameTouched = false
    private var usernameTouched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDataRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração do Dropdown de Gênero
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

        // Picker de Data
        binding.textInputLayout4.setEndIconOnClickListener {
            showDatePicker()
        }

        // Listener para o Checkbox
        binding.checkBox.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        setupTextWatchers()
    }

    // Função para atualizar o estado do botão
    private fun updateButtonState() {
        val isAllFieldsValid = isAllFieldsValid()

        binding.loginEnter.isEnabled = isAllFieldsValid

        binding.loginEnter.setOnClickListener {
            if (isAllFieldsValid) {
                startPhotoRegister()
            }
        }
    }

    // Função para checar se todos os campos são válidos
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

    // Função de validação para o nome
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

    // Função de validação para o username
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

    // Função para configurar os TextWatchers e o foco
    private fun setupTextWatchers() {
        // Monitorando se o campo de nome foi tocado
        binding.nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameTouched = true
            } else {
                validateName(binding.nameInput.text.toString())
            }
        }

        // Monitorando se o campo de username foi tocado
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

    // Função para exibir o DatePicker
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Definir data máxima (12 anos atrás)
        val maxCalendar = Calendar.getInstance()
        maxCalendar.set(Calendar.YEAR, year - 12)

        val datePickerDialog = DatePickerDialog(this,
            R.style.CustomDatePickerDialog, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.dateOfBirthInput.setText(formattedDate)
                updateButtonState()
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis

        // Personalizar cores dos botões do DatePicker
        datePickerDialog.setOnShowListener {
            val positiveButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            val negativeButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
            negativeButton.setTextColor(ContextCompat.getColor(this, R.color.laranja))
        }

        datePickerDialog.show()
    }

    // Função para iniciar a atividade de registro de foto
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