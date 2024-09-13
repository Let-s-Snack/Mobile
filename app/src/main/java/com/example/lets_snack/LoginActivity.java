package com.example.lets_snack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TextWatchers para os dois campos de input
        binding.emailInput.addTextChangedListener(loginTextWatcher);
        binding.passwordInput.addTextChangedListener(loginTextWatcher);
    }

    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Não precisa de implementação
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Não precisa de implementação
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Chame o método para checar se ambos os campos estão preenchidos
            validateInputs();
        }
    };

    // Método para verificar se os campos estão preenchidos
    private void validateInputs() {
        String emailInput = binding.emailInput.getText().toString().trim();
        String passwordInput = binding.passwordInput.getText().toString().trim();

        // Ativa o botão se ambos os campos estiverem preenchidos
        binding.loginEnter.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
    }

}