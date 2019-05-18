package com.machado.mystock.classes;

import java.io.Serializable;

public class Pessoa implements Serializable {

    private String mDataBaseId;
    private String mUserId;
    private String mNome;
    private String mCpf;
    private String mEmail;
    private String mLoja;

    public Pessoa() {

    }

    public Pessoa(String nome, String cpf, String email, String id) {
        mNome = nome;
        mCpf = cpf;
        mEmail = email;
        mDataBaseId = id;
    }

    public String getmNome() {
        return mNome;
    }

    public void setmNome(String mNome) {
        this.mNome = mNome;
    }

    public String getmCpf() {
        return mCpf;
    }

    public void setmCpf(String mCpf) {
        this.mCpf = mCpf;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmLoja() {
        return mLoja;
    }

    public void setmLoja(String mLoja) {
        this.mLoja = mLoja;
    }


    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmDataBaseId() {
        return mDataBaseId;
    }

    public void setmDataBaseId(String mDataBaseId) {
        this.mDataBaseId = mDataBaseId;
    }
}