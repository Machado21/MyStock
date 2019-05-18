package com.machado.mystock.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.machado.mystock.R;
import com.machado.mystock.classes.Produto;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> implements View.OnClickListener {

    private ArrayList<Produto> mProdutos;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private static OnItemClickListener onClickListener;

    public CartAdapter(Context context, ArrayList<Produto> produtos){
        this.mContext = context;
        this.mProdutos = produtos;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = mLayoutInflater.inflate(R.layout.cart_iten, parent,false);
        CartViewHolder cartViewHolder = new CartViewHolder(view);
        return cartViewHolder;
    }

    @Override
    public  void onBindViewHolder(@NonNull CartViewHolder holder, final int position){
        Produto item = mProdutos.get(position);
        holder.bind(item);

        holder.tvName.setText(mProdutos.get(position).getmName());
        holder.tvAmount.setText("Quant. "+mProdutos.get(position).getmQuant());
    }

    @Override
    public int getItemCount() {
        return mProdutos.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvAmount;

        Produto item;
        public CartViewHolder(@NonNull final View itemView){
            super(itemView);
            tvName = itemView.findViewById(R.id.product_name);
            tvAmount = itemView.findViewById(R.id.product_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onItemClick(item);
                    }
                }
            });

        }

        void bind(Produto item) {
            this.item = item;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Produto produto);
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}