package com.machado.mystock.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.machado.mystock.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private Button loginButon, signupButon;
    private EditText mEmail, mPassword;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mEmail = findViewById(R.id.email_logn_in);
        mPassword = findViewById(R.id.password_log_in);

        loginButon = findViewById(R.id.sign_in_lg_bt);
        signupButon = findViewById(R.id.sign_up_lg_bt);
        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.GONE);
        //
        mAuth = FirebaseAuth.getInstance();

        //Botão Entrar
        loginButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String senha = mPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "signIn " + email);
                if (!validateForm()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "SignInWithEmail:success");

                                    Intent intent = new Intent(SignInActivity.this, SplashActivity.class);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Erro no login",
                                            Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });

        //Botão Cadastre-se
        signupButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isConected(this)) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseUser currentUser = mAuth.getCurrentUser();

            updateUI(currentUser);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(this, "Desculpe mas é necessario estar conectado a Internet", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Se usuário ja estiver logado pula o login
     *
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignInActivity.this, SplashActivity.class);
            progressBar.setVisibility(View.INVISIBLE);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Verifica os campos se estão em branco
     *
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Obrigatório.");
            mEmail.requestFocus();
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Obrigatório.");
            mPassword.requestFocus();
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    /**
     * Função para verificar conexão
     *
     * @param context
     * @return
     */
    public static boolean isConected(Context context) {
        ConnectivityManager connectivityManeger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManeger != null) {
            NetworkInfo networkInfo = connectivityManeger.getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }
}