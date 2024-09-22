package com.example.lets_snack.presentation.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.lets_snack.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adiciona TextWatchers a todos os campos
        setupTextWatchers()
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

        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPhoneValid = phone.length >= 10 // ou o número que você considera padrão
        val isPasswordValid = password.length >= 8
        val isPasswordsMatch = password == confirmPassword

        // Exibe mensagens de erro, se necessário
        binding.textInputLayout.error = if (!isEmailValid) {
            "E-mail inválido"
        } else null

        binding.textInputLayout3.error = if (!isPhoneValid) {
            "Telefone inválido"
        } else null

        binding.textInputLayout4.error = if (!isPasswordValid) {
            "A senha deve ter pelo menos 8 caracteres"
        } else null

        binding.textInputLayout5.error = if (!isPasswordsMatch) {
            "As senhas não coincidem"
        } else null

        // Verifica se todos os campos estão preenchidos corretamente
        val isAllFieldsFilled = isEmailValid &&
                isPhoneValid &&
                isPasswordValid &&
                isPasswordsMatch

        binding.loginEnterBtn.isEnabled = isAllFieldsFilled
    }
}
