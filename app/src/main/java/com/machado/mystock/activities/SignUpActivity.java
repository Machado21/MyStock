package com.machado.mystock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.machado.mystock.R;
import com.machado.mystock.adaptadores.Mask;
import com.machado.mystock.classes.Pessoa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "Cadastro Activity";
    private static final String USUARIO_REF = "Usuarios";

    private EditText mNomeEdit, mCpfEdit, mEmailEdit, mSenha, mConfirmSenha;

    Pessoa pessoa;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mNomeEdit = findViewById(R.id.nome_sign_up);
        mCpfEdit = findViewById(R.id.cpf_sign_up);
        mEmailEdit = findViewById(R.id.email_sing_up);
        mSenha = findViewById(R.id.passowrd_sing_up);
        mConfirmSenha = findViewById(R.id.confirm_pass_sing_up);

        mCpfEdit.addTextChangedListener(Mask.mask(mCpfEdit, Mask.FORMAT_CPF));

        Button mCadastrarMeB = findViewById(R.id.sing_up_bt);

        mAuth = FirebaseAuth.getInstance();

        mCadastrarMeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nome = mNomeEdit.getText().toString();
                final String cpf = mCpfEdit.getText().toString();
                final String email = mEmailEdit.getText().toString();
                final String senha = mSenha.getText().toString();

                Log.d(TAG, "signUp " + email);
                if (!validateForm() || !validatePassword()) {
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");

                                    myRef = database.getReference(USUARIO_REF).push();
                                    String id = myRef.getKey();
                                    pessoa = new Pessoa(nome, cpf, email, id);
                                    pessoa.setmUserId(mAuth.getUid());
                                    pessoa.setmDataBaseId(id);
                                    pessoa.setmLoja(id);
                                    myRef.setValue(pessoa).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Cadastraddo com Sucesso!",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Erro no Cadastro 02!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Erro no Cadastro 01!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNomeEdit.requestFocus();
    }

    /**
     * Verifica se o formula´rio foi preenchido completamente
     *
     * @return
     */
    //Verifica os campos se estão em branco
    private boolean validateForm() {
        boolean valid = true;

        String nome = mNomeEdit.getText().toString();
        if (TextUtils.isEmpty(nome)) {
            mNomeEdit.setError("Obrigatório.");
            mNomeEdit.requestFocus();
            valid = false;
        } else {
            mNomeEdit.setError(null);
        }

        String cpf = mCpfEdit.getText().toString();
        if (TextUtils.isEmpty(cpf)) {
            mCpfEdit.setError("Obrigatório.");
            mCpfEdit.requestFocus();
            valid = false;
        } else {
            mCpfEdit.setError(null);
        }

        String email = mEmailEdit.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailEdit.setError("Obrigatório.");
            mNomeEdit.requestFocus();
            valid = false;
        } else {
            mEmailEdit.setError(null);
        }

        String password = mSenha.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mSenha.setError("Obrigatório.");
            mNomeEdit.requestFocus();
            valid = false;
        } else {
            mSenha.setError(null);
        }

        String confirm = mConfirmSenha.getText().toString();
        if (TextUtils.isEmpty(confirm)) {
            mConfirmSenha.setError("Obrigatório.");
            mNomeEdit.requestFocus();
            valid = false;
        } else {
            mConfirmSenha.setError(null);
        }

        return valid;
    }

    /**
     * Verifica se a senha e a confirmação são iguais
     * @return
     */
    private boolean validatePassword() {
        boolean valid = true;
        if (!mSenha.getText().toString().equals(mConfirmSenha.getText().toString())) {
            mConfirmSenha.setError("As senhas não coincidem!");
            mConfirmSenha.requestFocus();
            valid = false;
        }
        return valid;
    }
}