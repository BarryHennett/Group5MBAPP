package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.R;
import com.example.iscg7427groupmobileapp.UserAllTransactions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<UserAllTransactions.Transaction> transactionList;
    private Context context;

    public TransactionAdapter(Context context, List<UserAllTransactions.Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        UserAllTransactions.Transaction transaction = transactionList.get(position);
        holder.description.setText(transaction.getDescription());
        holder.amount.setText(String.format("$ %.2f", transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView amount;
        public TextView timestamp;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.transactionname);
            amount = itemView.findViewById(R.id.transactionamount);
            timestamp = itemView.findViewById(R.id.transactiondate);
        }
    }
}
