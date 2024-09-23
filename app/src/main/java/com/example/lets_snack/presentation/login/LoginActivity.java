package com.example.lets_snack.presentation.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.example.lets_snack.MainActivity;
import com.example.lets_snack.databinding.ActivityLoginBinding;
import com.example.lets_snack.presentation.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    String email, password;
    ActivityLoginBinding binding;
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

        FirebaseAuth autentication = FirebaseAuth.getInstance();

        FirebaseUser user = autentication.getCurrentUser();

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

        //logar o usuario
        loginEnterBtn.setOnClickListener(v -> {
            email = binding.emailInput.getText().toString().trim();
            password = binding.passwordInput.getText().toString().trim();
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
//                                            binding.errorTxt.setText("Email ou senha incorretos");
                                    }catch (Exception e){
                                        errorMsg = "Ocorreu um erro com o sistema";
                                    }
                                    binding.errorTxt.setText(errorMsg);
                                }
                            }
                        });
            }
        });
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
        binding.loginEnterBtn.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
    }

    private void openScreen() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}