package com.machado.mystock.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.machado.mystock.R;
import com.machado.mystock.classes.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> implements Filterable, View.OnClickListener {

    private List<Produto> mList;
    private List<Produto> mListFull;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private static OnItemClickListener onClickListener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_iten, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    /**
     * Constructor
     *
     * @param context
     * @param produtos
     */
    public ProductAdapter(Context context, List<Produto> produtos) {
        this.mList = produtos;
        this.mListFull = produtos;
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Produto item = mList.get(position);
        holder.bind(item);

        holder.tvPoduct.setText(item.getmName());
        holder.tvAmount.setText("Quant: " + item.getmQuant().toString());
        holder.tvValue.setText("R$ " + item.getmValue().toString());
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPoduct;
        public TextView tvAmount;
        public TextView tvValue;

        Produto item;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvPoduct = itemView.findViewById(R.id.name_prod_tv);
            tvAmount = itemView.findViewById(R.id.amount_prod_tv);
            tvValue = itemView.findViewById(R.id.value_prod_tv);

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

    public interface OnItemClickListener {
        void onItemClick(Produto produto);

    }

    /**
     * @param onClickListener
     */
    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Metodo para atualizar lista caso tenha mudanças no banco de dados
     *
     * @param produto
     */
    public void updateItem(Produto produto) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getmCode().equals(produto.getmCode())) {
                mList.get(i).setmQuant(mList.get(i).getmQuant() + 1);
                notifyItemChanged(i);
            }
        }
    }


    /**
     * @param v
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * Metodo para buscar valor no array
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        return mListFilter;
    }

    /**
     * Metodo para buscar valor no array
     *
     * @return
     */
    private Filter mListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Produto> filteredList = new ArrayList<>();

            String stringToFind = constraint.toString();

            if (stringToFind.isEmpty()) {
                mList = mListFull;
            } else {
                String filterPattern = constraint.toString();
                for (Produto item : mListFull) {
                    if (item.getmName().toLowerCase().contains(filterPattern) || item.getmCode().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                mList = filteredList;
            }
            FilterResults results = new FilterResults();
            results.values = mList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mList = (ArrayList<Produto>) results.values;
            notifyDataSetChanged();
        }
    };

    // TODO Implementar Função para alterar o produto
}