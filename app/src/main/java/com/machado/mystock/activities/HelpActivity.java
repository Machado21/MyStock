package com.machado.mystock.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.machado.mystock.R;

public class HelpActivity extends AppCompatActivity {

    private static final String INTENT_USER = "pessoa";

    private TextView emailTextView, phoneTextView;
    private Intent cadastro, lista;

    private String context, loja, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Intent intent = getIntent();
        loja = intent.getStringExtra("storeId");
        context = intent.getStringExtra("context");

        //TODO Fazer com que retorne a Activity de onde veio
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Ajuda e Suporte");

        emailTextView = findViewById(R.id.my_email_adress);
        phoneTextView = findViewById(R.id.my_phone_number);

        // Politica de Privacidade
        //https://mystock941032621.wordpress.com/politica-de-privacidade-my-stock/

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "axesoft21@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ajuda e Suporte MyStock");
                startActivity(emailIntent);
            }
        });

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:95981220021");
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(phoneIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                break;

            default:
                break;
        }
        return true;
    }
}