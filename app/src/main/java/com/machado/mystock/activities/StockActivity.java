package com.machado.mystock.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.machado.mystock.R;
import com.machado.mystock.adaptadores.ProductAdapter;
import com.machado.mystock.classes.Pessoa;
import com.machado.mystock.classes.Produto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StockActivity extends AppCompatActivity {

    private static final String PRODUCTS_REF = "Products";
    private static final String INTENT_USER = "pessoa";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<Produto> mProdutos = new ArrayList<>();

    private ProductAdapter productAdapter;
    private Pessoa pessoa;
    private Produto product;

    private AlertDialog alertDialog;

    //TODO FINALIZAR CLASS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        /* Get the intent with date */
        Intent intentRecive = getIntent();
        pessoa = (Pessoa) intentRecive.getSerializableExtra(INTENT_USER);
        /**/
        Button newProduct = findViewById(R.id.new_product);
        newProduct.setOnClickListener(novoProduto);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Responsavel por aparecer Botam de Voltar na barra de ação ActioBar
        getSupportActionBar().setHomeButtonEnabled(true); //Responsavel por ativar o botão
        getSupportActionBar().setTitle("Estoque"); // Mostra um Titulo na barra de ação ActionBar

        //Busca os Produtos na base de dados online
        final DatabaseReference myRef = database.getReference(PRODUCTS_REF);
        Query myQuery = myRef.orderByChild("mLoja").equalTo(pessoa.getmLoja());
        myQuery.addChildEventListener(getProdutos);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dView = inflater.inflate(R.layout.dialog_stock, null);
        builder.setTitle("Alterar Produtos");
        builder.setView(dView);

        final NumberPicker picker = dView.findViewById(R.id.numPicker);
        picker.setMinValue(0);
        picker.setMaxValue(200);
        picker.setWrapSelectorWheel(true);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Integer quant = picker.getValue();
                EditText altera_valor = ((Dialog) dialog).findViewById(R.id.altera_valor);
                double valor;
                String new_value = altera_valor.getText().toString();
                if (!TextUtils.isEmpty(new_value)) {
                    valor = Double.parseDouble(altera_valor.getText().toString());
                } else {
                    valor = 0.00;
                }
                //Pega o valor selecionado no NumberPicker
                //Altera a quantidade
                try {
                    product.repor(quant - 1);
                } catch (Exception e) {
                    Toast.makeText(StockActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                //Altera o valor
                try {
                    product.setmValue(valor);
                } catch (Exception e) {
                    Toast.makeText(StockActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                DatabaseReference refAtualiza = myRef.child(product.getmId());
                refAtualiza.setValue(product);
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

        productAdapter = new ProductAdapter(this, mProdutos);

        productAdapter.setOnClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Produto produto) {
                Toast.makeText(StockActivity.this, produto.getmName(), Toast.LENGTH_SHORT).show();
                product = produto;
                alertDialog.show();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.recycle_vew_stock);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // Set Layout Meneger
        mRecyclerView.setLayoutManager(layoutManager);
        // Set Adapter
        mRecyclerView.setAdapter(productAdapter);

    }

    ChildEventListener getProdutos = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Produto novo = dataSnapshot.getValue(Produto.class);
            mProdutos.add(novo);
            productAdapter.notifyDataSetChanged();

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
//            carProductAdapter.updateItem(dataSnapshot.getValue(Produto.class));
            productAdapter.updateItem(dataSnapshot.getValue(Produto.class));
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//            carProductAdapter.remuveItem(dataSnapshot.getValue(Produto.class));
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    View.OnClickListener novoProduto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent cadastro = new Intent(StockActivity.this, CadastroProdutoActivity.class);
            cadastro.putExtra(INTENT_USER, pessoa);
            startActivity(cadastro);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(StockActivity.this, MainActivity.class);
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
        Intent intent = new Intent(StockActivity.this, MainActivity.class);
        intent.putExtra(INTENT_USER, pessoa);
        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

}