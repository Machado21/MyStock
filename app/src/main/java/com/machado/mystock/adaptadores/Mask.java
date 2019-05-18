package com.machado.mystock.adaptadores;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Mask {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_CNPJ = "##.###.###/####-##";
    public static final String FORMAT_FONE = "(###)####-#####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##/##/####";
    public static final String FORMAT_HOUR = "##:##";

    public static String unmask(final String s){
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher mask(final EditText editText, final String mask){

        return new TextWatcher() {

            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String str = unmask(s.toString());
                String mascara = "";
                if (isUpdating){
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i =0 ;
                for (final char m : mask.toCharArray()){
                    if (m != '#' && str.length()>old.length()){
                        mascara+=m;
                        continue;
                    }
                    try {
                        mascara+=str.charAt(i);
                    } catch (final Exception e){
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}