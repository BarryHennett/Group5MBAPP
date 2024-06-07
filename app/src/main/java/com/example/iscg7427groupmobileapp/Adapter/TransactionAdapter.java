package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter{
    private HashMap<String, User.Transaction> transactionMap;
    private Context context;
    public TransactionAdapter(HashMap<String, User.Transaction> transactionMap, Context context) {
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

        String category = Objects.requireNonNull(transactionMap.get(transactionKey)).getCategory();
        double amount = Objects.requireNonNull(transactionMap.get(transactionKey)).getAmount();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String date = sdf.format(Objects.requireNonNull(transactionMap.get(transactionKey)).getDate());


        viewHolder.txtAmount.setText(Double.toString(amount));
        viewHolder.txtCategory.setText(category);
        viewHolder.txtDate.setText(date);

        // get the type of the transaction
        String type = transactionMap.get(transactionKey).getType();
        // set color
        if (type.equals("Income")) {
            viewHolder.txtAmount.setText("$" + amount);
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.blue));

        } else {
            viewHolder.txtAmount.setText("- $" + amount);
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.orange));
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

    public static class ViewHolderTransaction extends RecyclerView.ViewHolder {
        TextView txtCategory, txtDate, txtAmount;

        public ViewHolderTransaction(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.rv_transaction_category);
            txtDate = itemView.findViewById(R.id.rv_transaction_date);
            txtAmount = itemView.findViewById(R.id.rv_transaction_amount);
        }
    }
}
