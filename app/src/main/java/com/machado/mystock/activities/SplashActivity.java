package com.machado.mystock.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.machado.mystock.R;
import com.machado.mystock.classes.Pessoa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private static final String USER_REF = "Usuarios";
    private static final String INTENT_USER = "pessoa";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;

    private Pessoa pessoa;
    private String user, loja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new CarregarDados().execute();
    }

    private class CarregarDados extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isConected(SplashActivity.this)){
                Toast.makeText(SplashActivity.this, "Infelismente Você não esta Conectado a Internet!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            userRef = database.getReference(USER_REF);
            Query queryUser = database.getReference(USER_REF).orderByChild("mEmail").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        if (post.getValue(Pessoa.class).getmEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            pessoa = post.getValue(Pessoa.class);

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra(INTENT_USER, pessoa);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SplashActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
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