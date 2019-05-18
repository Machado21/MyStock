package com.machado.mystock.classes;

public class Loja {

    private String mNome;
    private String mCnpj;
    private String mSenha;
    private String mProrietario;
    private String mId;

    public Loja() {

    }

    public Loja(String nome, String cnpj, String senha, String proprietario) {
        mNome = nome;
        mCnpj = cnpj;
        mSenha = senha;
        mProrietario = proprietario;
    }

    public String getmNome() {
        return mNome;
    }

    public void setmNome(String mNome) {
        this.mNome = mNome;
    }

    public String getmCnpj() {
        return mCnpj;
    }

    public void setmCnpj(String mCnpj) {
        this.mCnpj = mCnpj;
    }

    public String getmSenha() {
        return mSenha;
    }

    public void setmSenha(String mSenha) {
        this.mSenha = mSenha;
    }

    public String getmProrietario() {
        return mProrietario;
    }

    public void setmProrietario(String mProrietario) {
        this.mProrietario = mProrietario;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }
}