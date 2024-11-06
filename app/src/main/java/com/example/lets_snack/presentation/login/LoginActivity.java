package com.example.lets_snack.presentation.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.lets_snack.presentation.MainActivity;
import com.example.lets_snack.databinding.ActivityLoginBinding;
import com.example.lets_snack.presentation.BaseActivity;
import com.example.lets_snack.presentation.login.forgetPassword.ForgetPasswordActivity;
import com.example.lets_snack.presentation.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    FirebaseAuth autentication = FirebaseAuth.getInstance();
    FirebaseUser user = autentication.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // TextWatchers para os dois campos de input
        binding.emailInput.addTextChangedListener(loginTextWatcher);
        binding.passwordInput.addTextChangedListener(loginTextWatcher);
        binding.registerText.setOnClickListener(view -> openScreen());
        Button loginEnterBtn = binding.loginEnterBtn;

        //verificar se o usuario esta logado
        if(user != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //ir para tela de cadastro
        binding.registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        //ir para a tela de esqueceu a senha
        binding.forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });
    }

    // TextWatchers
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Não precisa de implementação
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {
            //não precisa de implementação
        }
    };

    private void openScreen() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void updateButtonState() {
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.getText().toString().trim()).matches();
        boolean isPasswordValid = binding.passwordInput.getText().toString().trim().length() >= 8;

        // Exibe mensagens de erro, se necessário
        if(binding.emailInput.getText().toString().trim().length() > 0) {
            binding.textInputLayoutEmailLogin.setError(!isEmailValid ? "E-mail inválido" : null);
        }
        if(binding.passwordInput.getText().toString().trim().length() > 0) {
            binding.textInputLayoutPasswordLogin.setError(!isPasswordValid ? "Mínimo de 8 caracteres" : null);
            binding.textInputLayoutPasswordLogin.setErrorIconDrawable(null);
        }


        // Verifica se todos os campos estão preenchidos corretamente
        boolean isAllFieldsFilled = isEmailValid && isPasswordValid;
        binding.loginEnterBtn.setEnabled(isAllFieldsFilled);

        //fazendo login
        binding.loginEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.loginEnterBtn.setText("");
                binding.loginEnterBtn.setEnabled(false);
                String email = binding.emailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();
                if(email != null && password != null){
                    autentication.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //entrar na home
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        //exibir mensagem de erro
                                        String errorMsg = "";
                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthInvalidUserException e){
                                            errorMsg = "Usuário não encontrado";
                                        }catch(FirebaseAuthInvalidCredentialsException e){
                                            errorMsg = "Email ou senha inválidos";
                                            binding.errorTxt.setText("Email ou senha incorretos");
                                        }catch (Exception e){
                                            errorMsg = "Ocorreu um erro com o sistema";
                                        }
                                        binding.errorTxt.setText(errorMsg);
                                    }
                                }
                            });
                }
            }
        });
    }


}