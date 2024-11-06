package com.example.lets_snack.presentation.login.forgetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.lets_snack.MainActivity;
import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    FirebaseAuth recover = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TextWatchers para os dois campos de input
        binding.emailInput.addTextChangedListener(loginTextWatcher);

    }
    private final TextWatcher loginTextWatcher = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //não precisa de implementação
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //chamando método de validação de email e do botão
            updateButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {
            //não precisa de implementação
        }
    };

    private void updateButtonState() {
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.getText().toString().trim()).matches();

        // Exibe mensagens de erro, se necessário
        binding.textInputLayoutEmail.setError(!isEmailValid ? "E-mail inválido" : null);

        // Verifica se todos os campos estão preenchidos corretamente
        boolean isAllFieldsFilled = isEmailValid;
        binding.sendBtn.setEnabled(isAllFieldsFilled);

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recover.sendPasswordResetEmail(binding.emailInput.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPasswordActivity.this, "Email enviado com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else if (task.getException().getMessage().contains("FirebaseAuthInvalidUserException")) {
                                    // email não cadastrado
                                    Toast.makeText(ForgetPasswordActivity.this, "O email informado não está registrado", Toast.LENGTH_SHORT).show();
                                } else if (task.getException().getMessage().contains("FirebaseAuthInvalidCredentialsException")) {
                                    // formato de email inválido
                                    Toast.makeText(ForgetPasswordActivity.this, "O formato do email é inválido", Toast.LENGTH_SHORT).show();
                                } else {
                                    // outro erro
                                    Toast.makeText(ForgetPasswordActivity.this, "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void finishScreen(View view) {
        finish();
    }
}