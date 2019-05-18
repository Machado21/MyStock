package com.machado.mystock.classes;

public class Produto implements Cloneable {

    private String mName;

    private String mCode;

    private Integer mQuant;

    private Double mValue;

    private String mId;

    private String mLoja;

    public Produto() {
    }

    /**
     * Constructor
     *
     * @param name
     * @param code
     * @param qunat
     * @param value
     * @param loja
     */
    public Produto(String name, String code, Integer qunat, Double value, String loja) {
        this.mName = name;
        this.mCode = code;
        this.mQuant = qunat;
        this.mValue = value;
        this.mLoja = loja;
    }

    /**
     * Gets
     */
    public String getmName() {
        return mName;
    }

    public String getmCode() {
        return mCode;
    }

    public Integer getmQuant() {
        return mQuant;
    }

    public Double getmValue() {
        return mValue;
    }

    public String getmId() {
        return mId;
    }

    public String getmLoja() {
        return mLoja;
    }

    /**
     * Sets
     */
//    public void setmName(String mName) {
//        this.mName = mName;
//    }

//    public void setmCode(String mCode) {
//        this.mCode = mCode;
//    }

    /**
     * Metodo set para quantidade, impede que seja setado valor menor que 0
     *
     * @param quant
     */
    public void setmQuant(Integer quant) {
        if (quant >= 0) {
            this.mQuant = quant;
        }
    }

    /**
     * Metodo set para o valor, impede que seja setado valor menor ou igual que 0
     *
     * @param value
     */
    public void setmValue(Double value) {
        if (value > 0) {
            this.mValue = value;
        } else {
            throw new IllegalArgumentException("O Valor não pode ser menor ou igual que R$ 0,00");
        }
    }


    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmLoja(String mLoja) {
        this.mLoja = mLoja;
    }

    /**
     * Realiza venda do produto(Remove a quantidade passadapelo parametro)
     *
     * @param amount
     */
    public void vender(Integer amount) {
        if (this.mQuant >= amount) {
            this.mQuant -= amount;
        } else {
            throw new IllegalArgumentException("A quantidade Não pode eceder a quantidade em estoque");
        }
    }

    /**
     * Repoem estoque colocando a mais a quantidade passada por parametro
     *
     * @param amount
     */
    public void repor(Integer amount) {
        if (amount >= 0) {
            this.mQuant += amount;
        } else {
            throw new IllegalArgumentException("A quantidade não pode ser menor que 0 (Zero)");
        }
    }

    /**
     * Metodo retorna uma cópia do objeto lancando um throws a ser tratado
     *
     * @return
     */
    @Override
    public Produto clone() throws CloneNotSupportedException {
        Produto novo = (Produto) super.clone();
        return novo;
    }
}