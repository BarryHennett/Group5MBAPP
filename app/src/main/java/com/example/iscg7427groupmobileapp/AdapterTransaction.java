package com.example.iscg7427groupmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class AdapterTransaction extends RecyclerView.Adapter {

    private HashMap<String, User.Transaction> transactionMap;
    private Context context;
    public AdapterTransaction(HashMap<String, User.Transaction> transactionMap, Context context) {
        this.transactionMap = transactionMap;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_transaction, parent, false);
        return new ViewHolderTransaction(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderTransaction viewHolder = (ViewHolderTransaction) holder;
        // user keySet as a index to manipulate the recycler view
        String transactionKey = transactionMap.keySet().toArray()[position].toString();
        // display the amount only
        double amount = transactionMap.get(transactionKey).getAmount();
        viewHolder.txtAmount.setText(Double.toString(amount));
        // get the type of the transaction
        String type = transactionMap.get(transactionKey).getType();
        // set color
        if (type.equals("Income")) {
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.blue));
        } else {
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.purple));
        }
        // deal with on click event
        viewHolder.itemView.setOnClickListener(v -> {
            // do something
        });
    }
    @Override
    public int getItemCount() {
        return transactionMap.size();
    }

    public class ViewHolderTransaction extends RecyclerView.ViewHolder {
        TextView txtAmount;

        public ViewHolderTransaction(@NonNull View itemView) {
            super(itemView);
            txtAmount = itemView.findViewById(R.id.recycler_amount);
        }
    }
}
