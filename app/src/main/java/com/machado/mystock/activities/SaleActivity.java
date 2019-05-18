package com.machado.mystock.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.machado.mystock.R;
import com.machado.mystock.adaptadores.CartAdapter;
import com.machado.mystock.adaptadores.ProductAdapter;
import com.machado.mystock.classes.Pessoa;
import com.machado.mystock.classes.Produto;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class SaleActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";
    private static final String PRODUCTS_REF = "Products";
    private static final String INTENT_USER = "pessoa";

    private TextView carValueTV;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<Produto> mProdutos = new ArrayList<>();
    private ArrayList<Produto> mCarProdutos = new ArrayList<>();

    private Produto product;
    private Pessoa pessoa;

    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;

    private AlertDialog alertDialog;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        Log.d(TAG, "onCreate: started.");

        /* Get the intent with date */
        Intent intentRecive = getIntent();
        pessoa = (Pessoa) intentRecive.getSerializableExtra(INTENT_USER);
        /**/
        carValueTV = findViewById(R.id.text_price);

        Button sellConfirm = findViewById(R.id.confirm_sell);
        Button sellCancel = findViewById(R.id.cancel_sell);

        sellConfirm.setOnClickListener(finalizarVenda);
        sellCancel.setOnClickListener(cancelarVenda);

        productAdapter = new ProductAdapter(this, mProdutos);
        cartAdapter = new CartAdapter(this, mCarProdutos);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Venda");

        DatabaseReference myRef = database.getReference(PRODUCTS_REF);
        Query myQuery = myRef.orderByChild("mLoja").equalTo(pessoa.getmLoja());
        myQuery.addChildEventListener(getProdutos);

        //Abre um AlertDialog com um NumberPiker para selecionar a quantidade desejada  ###INICIO###
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_amount, null);
        builder.setTitle("Quantidade Desejada:");
        builder.setView(dView);

        final NumberPicker picker = dView.findViewById(R.id.amount_value_01);
        picker.setMinValue(1);
        picker.setMaxValue(150);
        picker.setWrapSelectorWheel(true);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final Integer valor;
                //Pega o valor selecionado no NumberPicker
                valor = picker.getValue();
                //Verifica se o valor selecionado é maior que a quantidade no estoque
                Produto aux = null;
                //Tenta pegar uma cópia do Objeto
                try {
                    aux = product.clone();
                } catch (CloneNotSupportedException e) {
                    Toast.makeText(SaleActivity.this, "Nao foi Possivel Concluir ação!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //Verifica se a quantidade requerido nao é maior do que a quantidade existente de produtos
                if (product.getmQuant() >= valor) {
                    assert aux != null;
                    aux.setmQuant(valor);
                    if (atualisa(aux, valor)) {
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        mCarProdutos.add(aux);
                        cartAdapter.notifyDataSetChanged();
                    }
                    atualisaCarro();
                } else {
                    Toast.makeText(SaleActivity.this, "!!! Quantidade Insuficiente !!!", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        //  ###FIM###

        productAdapter.setOnClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Produto produto) {
                Toast.makeText(SaleActivity.this, produto.getmName(), Toast.LENGTH_SHORT).show();
                product = produto;
                alertDialog.show();
            }
        });

        //## Configurações dos RecyclerViews ##
        RecyclerView mRecyclerView = findViewById(R.id.recycle_veiw);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        //Configurando o gerenciador de Layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(productAdapter);

        RecyclerView recyclerViewCar = findViewById(R.id.recycle_view_car);
        recyclerViewCar.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewCar.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCar.setLayoutManager(manager);

        recyclerViewCar.setAdapter(cartAdapter);
        //## fim ##
    }


    View.OnClickListener finalizarVenda = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO Finalizar venda chamara uma outra tela ainda nao criada
            //TODO Finalizar a compra apenas se estiver tudo OK
            boolean ok = true;
            for (Produto produto : mCarProdutos) {
                DatabaseReference atualiza = database.getReference(PRODUCTS_REF).child(produto.getmId());
                Integer quant = produto.getmQuant();
                for (int i = 0; i < mProdutos.size(); i++) {
                    if (mProdutos.get(i).getmCode().equals(produto.getmCode())) {
                        Produto atual = mProdutos.get(i);
                        try {
                            atual.vender(quant);
                            atualiza.setValue(atual); //Atualiza no bamco de dados o novo valor de estoque
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(SaleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            ok = false;
                        }
                    }
                }
            }
            if (ok) {
                Toast.makeText(SaleActivity.this, "Venda Finalizada com Sucesso !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                intent.putExtra(INTENT_USER, pessoa);
                startActivity(intent);
            }
        }
    };

    View.OnClickListener cancelarVenda = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SaleActivity.this, MainActivity.class);
            intent.putExtra(INTENT_USER, pessoa);
            startActivity(intent);
        }
    };


    /**
     * Verifica se o produto ja existe no carrinho e atualiza a quantidade caso sim
     *
     * @param produto {@link Produto}
     * @param quant   int
     * @return boolean
     */
    boolean atualisa(Produto produto, int quant) {
        for (Produto atualizar : mCarProdutos) {
            if (atualizar.getmCode().equals(produto.getmCode())) {
                atualizar.setmQuant(quant);
                return true;
            }
        }
        return false;
    }


    /**
     * Atualiza o Valor total do carrinho atual
     */
    public void atualisaCarro() {
        double carrinhoValor = 0.0;
        for (Produto aux : mCarProdutos) {
            carrinhoValor += aux.getmQuant() * aux.getmValue();
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        carValueTV.setText("R$ " + decimalFormat.format(carrinhoValor));
    }

    ChildEventListener getProdutos = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Produto novo = dataSnapshot.getValue(Produto.class);
            assert novo != null;
            if (novo.getmQuant() > 0) {
                mProdutos.add(novo);
                productAdapter.notifyDataSetChanged();
            }
            //Ordena a lista em rodem alfabetica usando o nome do produdo como parametro
            Collections.sort(mProdutos, new Comparator<Produto>() {
                @Override
                public int compare(Produto o1, Produto o2) {
                    return o1.getmName().compareTo(o2.getmName());
                }
            });
        }


        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            update(dataSnapshot.getValue(Produto.class));
            productAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    /**
     * Atualiza os valores e quantidades dos produtos
     *
     * @param produto {@link Produto}
     */
    void update(Produto produto) {
        for (Produto atualiza : mProdutos) {
            if (atualiza.getmCode().equals(produto.getmCode())) {
                atualiza.setmQuant(produto.getmQuant());
                atualiza.setmValue(produto.getmValue());
            }
        }
    }

    /**
     * @param item {@link MenuItem}
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                intent.putExtra(INTENT_USER, pessoa);
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                break;
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class).putExtra("context", "ListActivity"));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SaleActivity.this, MainActivity.class);
        intent.putExtra(INTENT_USER, pessoa);
        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }
}