package com.machado.mystock.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.machado.mystock.R;
import com.machado.mystock.classes.Pessoa;
import com.machado.mystock.classes.Produto;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class CadastroProdutoActivity extends AppCompatActivity {

    private static final String TAG = "Cadastro Activity";
    private static final String PRODUCTS_REF = "Products";
    private static final String INTENT_USER = "pessoa";

    private EditText mPoductName, mProductCode, mProductAmount, mProductValue;

    private String code;
    private Integer mAmount;
    private Double mValue;

    private Pessoa pessoa;
    private Produto produto;
    private ArrayList<Produto> produtos = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        /* 01 Verify and Request a permission to use the camera device*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        /* 02 Responsavel pelas propagandas*/
        MobileAds.initialize(this, "ca-app-pub-6661608223237465~6859209248");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /* Get the intent with date */
        Intent intentRecive = getIntent();
        code = intentRecive.getStringExtra("code");
        pessoa = (Pessoa) intentRecive.getSerializableExtra(INTENT_USER);


        mPoductName = findViewById(R.id.product_name_cad);
        mProductCode = findViewById(R.id.product_code_cad);
        mProductCode.requestFocus();
        mProductAmount = findViewById(R.id.product_amount_cad);
        mProductValue = findViewById(R.id.product_value_cad);

        /*//TODO Usar esta mascara quando resolver prblema da String R$
        //Locale mLocale = new Locale("pt", "BR");
        //mProductValue.addTextChangedListener(new MoneyMask(mProductValue, mLocale));*/

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cadastrar Produto");

        /* Get a list of products and set the product name edit text*/
        DatabaseReference productRef = database.getReference(PRODUCTS_REF);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    Produto novo = postSnap.getValue(Produto.class);
                    produtos.add(novo);
                    assert novo != null;
                    if (novo.getmCode().equals(code)) {
                        mPoductName.setText(novo.getmName());
                        String value = novo.getmValue().toString();
                        mProductValue.setText(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Botão abre o leitor de codigo */
        ImageView mScanCode = findViewById(R.id.scan_bt);
        mScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroProdutoActivity.this, CodeDetectorActivity.class);
                intent.putExtra(INTENT_USER, pessoa);
                startActivity(intent);
            }
        });

        /* Botão cadastra os produtos */
        Button mAddProduct = findViewById(R.id.cadastrar_bt);
        mAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mPoductName.getText().toString();
                String code = mProductCode.getText().toString();

                Log.d(TAG, "Botão cadastro de produto");
                if (!validateForm()) {
                    return;
                }

                mAmount = Integer.valueOf(mProductAmount.getText().toString());
                mValue = Double.valueOf(mProductValue.getText().toString());

                produto = new Produto(name, code, mAmount, mValue, pessoa.getmLoja());
                final Intent intent = new Intent(CadastroProdutoActivity.this, StockActivity.class);
                intent.putExtra(INTENT_USER, pessoa);

                Produto auxi = seEsite(code);
                if (auxi != null) {
                    myRef = database.getReference(PRODUCTS_REF).child(auxi.getmId());
                    produto.setmId(auxi.getmId());

                    myRef.setValue(produto).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CadastroProdutoActivity.this, "Produto Atualizado com Sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroProdutoActivity.this, "Erro ao Atualizar Produto !!!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Erro ao Atualizar Produto no banco. " + e);
                        }
                    });
                } else {
                    myRef = database.getReference(PRODUCTS_REF).push();
                    produto.setmId(myRef.getKey());

                    myRef.setValue(produto).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CadastroProdutoActivity.this, "Produto cadastrado com Sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroProdutoActivity.this, "Erro ao Cadastrar Produto !!!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Erro ao add Produto no banco. " + e);
                        }
                    });
                }

            }
        });

        if (code != null) {
            code = intentRecive.getStringExtra("code");
            mProductCode.setText(code);
        }


    }

    /**
     * Método para verificar se exite o produto em questão
     *
     * @return Produto - Produto existente
     */
    public Produto seEsite(String code01) {
        for (Produto productA : produtos) {
            if (productA.getmCode().equals(code01) && productA.getmLoja().equals(pessoa.getmLoja())) {
                return productA;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, StockActivity.class);
                intent.putExtra(INTENT_USER, pessoa);
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                break;
            case R.id.help:
                Intent intHelp = new Intent(this, HelpActivity.class);
                intHelp.putExtra("context", "CadastroActivity");
                startActivity(intHelp);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StockActivity.class);
        intent.putExtra(INTENT_USER, pessoa);
        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

    /**
     * Fun&ccedil;&atilde;o para validar o formulario caso estja em branco algum campo
     *
     * @return boolean
     */
    private boolean validateForm() {
        boolean valid = true;

        String productName = mPoductName.getText().toString();
        if (TextUtils.isEmpty(productName)) {
            mPoductName.setError("Obrigatório.");
            mPoductName.requestFocus();
            valid = false;
        } else {
            mPoductName.setError(null);
        }

        String productCode = mProductCode.getText().toString();
        if (TextUtils.isEmpty(productCode)) {
            mProductCode.setError("Obrigatório.");
            mProductCode.requestFocus();
            valid = false;
        } else {
            mProductCode.setError(null);
        }

        String productAmount = mProductAmount.getText().toString();
        if (TextUtils.isEmpty(productAmount)) {
            mProductAmount.setError("Obrigatório.");
            mProductAmount.requestFocus();
            valid = false;
        } else {
            mProductAmount.setError(null);
        }

        String productValue = mProductValue.getText().toString();
        if (TextUtils.isEmpty(productValue)) {
            mProductValue.setError("Obrigatório.");
            mProductValue.requestFocus();
            valid = false;
        } else {
            mProductValue.setError(null);
        }

        return valid;
    }
}