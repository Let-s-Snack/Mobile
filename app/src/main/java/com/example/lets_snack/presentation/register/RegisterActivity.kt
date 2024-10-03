package com.example.lets_snack.presentation.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lets_snack.presentation.register.personData.PersonDataRegisterActivity
import com.example.lets_snack.R
import com.example.lets_snack.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatchers()

        setupPhoneFormatter()

        binding.loginEnterBtn.setOnClickListener {
            if (isAllFieldsValid()) {
                insertUser(binding.emailInput.text.toString(), binding.passwordInput.text.toString()) { exists ->
                    if(exists){
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.delete()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    println("Usuário excluído com sucesso.")
                                    startPersonDataRegisterActivity()
                                } else {
                                    println("Erro ao excluir o usuário: ${task.exception?.message}")
                                }
                            }
                    }
                    else{
                        Toast.makeText(this, "Usuário já existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() {
        val fields = listOf(
            binding.emailInput,
            binding.phoneInput,
            binding.passwordInput,
            binding.confirmPasswordInput
        )

        for (field in fields) {
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateButtonState()
                    when (field.id) {
                        R.id.email_input -> validateEmail()
                        R.id.phone_input -> validatePhone()
                        R.id.password_input -> validatePassword()
                        R.id.confirm_password_input -> validateConfirmPassword()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun updateButtonState() {
        val email = binding.emailInput.text.toString()
        val phone = binding.phoneInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()
        val isEmailValid = isEmailValid(email)
        val isPhoneValid = isPhoneValid(phone)
        val isPasswordValid = isPasswordValid(password)
        val isPasswordsMatch = password == confirmPassword

        binding.loginEnterBtn.isEnabled = isEmailValid && isPhoneValid && isPasswordValid && isPasswordsMatch
    }

    private fun validateEmail() {
        val email = binding.emailInput.text.toString()
        if (!isEmailValid(email)) {
            binding.textInputLayout.error = "E-mail inválido"
        } else {
            binding.textInputLayout.error = null
        }
    }

    private fun validatePhone() {
        val phone = binding.phoneInput.text.toString()
        if (!isPhoneValid(phone)) {
            binding.textInputLayout3.error = "Telefone inválido"
        } else {
            binding.textInputLayout3.error = null
        }
    }

    private fun validatePassword() {
        val password = binding.passwordInput.text.toString()
        if (!isPasswordValid(password)) {
            binding.textInputLayout4.error = "A senha deve ter pelo menos 8 caracteres"
        } else {
            binding.textInputLayout4.error = null
        }
    }

    private fun validateConfirmPassword() {
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()
        if (password != confirmPassword) {
            binding.textInputLayout5.error = "As senhas não coincidem"
        } else {
            binding.textInputLayout5.error = null
        }
    }

    private fun isAllFieldsValid(): Boolean {
        val email = binding.emailInput.text.toString()
        val phone = binding.phoneInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        return isEmailValid(email) && isPhoneValid(phone) && isPasswordValid(password) && (password == confirmPassword)
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPhoneValid(phone: String): Boolean {
        return phone.length == 15
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun setupPhoneFormatter() {
        binding.phoneInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating: Boolean = false
            private val mask = "(##) #####-####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val currentCursorPosition = binding.phoneInput.selectionStart

                val phone = s.toString().replace(Regex("[^0-9]"), "")
                val maskedPhone = StringBuilder()

                isUpdating = true

                if (phone.length <= 11) {
                    var i = 0
                    for (char in mask.toCharArray()) {
                        if (char != '#' && phone.length > i) {
                            maskedPhone.append(char)
                        } else if (phone.length > i) {
                            maskedPhone.append(phone[i])
                            i++
                        }
                    }
                }

                binding.phoneInput.setText(maskedPhone.toString())

                val unmaskedLength = phone.length
                var newCursorPosition = currentCursorPosition + (maskedPhone.length - unmaskedLength)

                if (newCursorPosition > maskedPhone.length) newCursorPosition = maskedPhone.length

                binding.phoneInput.setSelection(newCursorPosition)

                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun startPersonDataRegisterActivity() {
        val bundle = Bundle()
        bundle.putString("email", binding.emailInput.text.toString())
        bundle.putString("phone", binding.phoneInput.text.toString())
        bundle.putString("password", binding.passwordInput.text.toString())
        val intent = Intent(this, PersonDataRegisterActivity::class.java)
        intent.putExtra("bundleRegister", bundle)
        startActivity(intent)
    }

    private fun insertUser(email: String, password: String, callback: (Boolean) -> Unit) {
        val authenticator = FirebaseAuth.getInstance()
        Log.e("Restriction", "Email: $email Password: $password")

        authenticator.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }
}
