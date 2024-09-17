package com.example.lets_snack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lets_snack.R;
import com.example.lets_snack.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
        binding.registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //logar o usuario
        loginEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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