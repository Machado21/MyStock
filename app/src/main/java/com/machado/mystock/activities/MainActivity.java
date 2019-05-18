package com.machado.mystock.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.machado.mystock.R;
import com.machado.mystock.classes.Pessoa;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PRODUCTS_REF = "Products";
    private static final String USER_REF = "Usuarios";
    private static final String CAR_REF = "Carrinho";
    private static final String INTENT_USER = "pessoa";

    private Pessoa pessoa;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Propaganda
        MobileAds.initialize(this, "ca-app-pub-6661608223237465~6859209248");
        AdView mAdView = findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intentRecive = getIntent();
        pessoa = (Pessoa) intentRecive.getSerializableExtra(INTENT_USER);

        //Pega o usuario logado
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

//        userRef = database.getReference(USER_REF);
//        userRef.addValueEventListener(getUsers);

        TextView mUserName = findViewById(R.id.user_name_main);
        TextView mPlanoUser = findViewById(R.id.plano_status_main);
        mUserName.setText("Olá, " + firstWord(mUser.getDisplayName()));

        LinearLayout statusLayout = findViewById(R.id.status);
        statusLayout.setOnClickListener(atualizaStatus);

        ImageView mGetOut = findViewById(R.id.img_go_out);
        LinearLayout mMeuEstoque = findViewById(R.id.meu_estoque);
        LinearLayout mEfetuarVenda = findViewById(R.id.efetuar_venda);

        mGetOut.setOnClickListener(clickGetOut);
        mMeuEstoque.setOnClickListener(clickMeuEstoque);
        mEfetuarVenda.setOnClickListener(clickEfetuarVenda);

    }

    //Metodo de sair do app, fazer logout
    View.OnClickListener clickGetOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sair");
            builder.setMessage("Deseja realmente sair?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
        }
    };

    View.OnClickListener clickMeuEstoque = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent estoque = new Intent(MainActivity.this, StockActivity.class);
            estoque.putExtra(INTENT_USER, pessoa);
            startActivity(estoque);
        }
    };

    View.OnClickListener clickEfetuarVenda = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent vender = new Intent(MainActivity.this, SaleActivity.class);
            vender.putExtra(INTENT_USER, pessoa);
            startActivity(vender);
        }
    };

    View.OnClickListener atualizaStatus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (pessoa != null) {

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(pessoa.getmNome())
                        .build();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.updateProfile(profileChangeRequest);
                }
            } else {
                Toast.makeText(MainActivity.this, "NullPointerException", Toast.LENGTH_LONG).show();
            }
        }
    };


    /**
     * Retorna apenas a primeira palavra de uma string
     *
     * @param s
     * @return
     */
    public String firstWord(String s) {
        StringBuilder palavra = new StringBuilder();
        String letra;
        for (int i = 0; i < s.length() - 1; i++) {
            letra = s.substring(i, i + 1);
            palavra.append(letra);
            if (letra.equals(" ")) {
                break;
            }
        }
        return palavra.toString();
    }
}