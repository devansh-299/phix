package com.thebiglosers.phix.view.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<Transaction> mTransactionList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, amount;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_title);
            date = view.findViewById(R.id.item_date);
            amount = view.findViewById(R.id.item_amount);
        }
    }


    public TransactionAdapter(List<Transaction> transactions) {
        this.mTransactionList = transactions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transaction transaction = mTransactionList.get(position);
        holder.title.setText(transaction.getDescription());
        holder.amount.setText(Float.toString(transaction.getAmount()));
        holder.date.setText(transaction.getDate());
    }

    public void updateImageList(List<Transaction> newList) {
        mTransactionList.clear();
        mTransactionList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTransactionList.size();
    }
}