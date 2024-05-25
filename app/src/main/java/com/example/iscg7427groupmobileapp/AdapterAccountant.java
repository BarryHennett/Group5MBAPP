package com.example.iscg7427groupmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class AdapterAccountant extends RecyclerView.Adapter{
    private HashMap<String, User> userMap;
    private Context context;
    public AdapterAccountant(HashMap<String, User> userMap, Context context) {
        this.userMap = userMap;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_accountant, parent, false);
        return new ViewHolderAccountant(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderAccountant viewHolder = (ViewHolderAccountant) holder;
        // user keySet as a index to manipulate the recycler view
        String userKey = userMap.keySet().toArray()[position].toString();
        User user = userMap.get(userKey);
        // get all transactions of the user
        HashMap<String, User.Transaction> transactionMap = user.getTransactions();
        // initialize income and expense
        double income = 0;
        double expense = 0;
        // loop through all transactions to calculate income and expense
        for (String key : transactionMap.keySet()) {
            if (transactionMap.get(key).getType().equals("Income")) {
                income += transactionMap.get(key).getAmount();
            } else {
                expense += transactionMap.get(key).getAmount();
            }
        }
        // only display the user key, income and expense for testing
        viewHolder.tvKey.setText(userKey);
        viewHolder.tvIncome.setText(Double.toString(income));
        viewHolder.tvExpense.setText(Double.toString(expense));
    }

    @Override
    public int getItemCount() {
        return userMap.size();
    }

    public static class ViewHolderAccountant extends RecyclerView.ViewHolder {
        TextView tvKey ;
        TextView tvIncome ;
        TextView tvExpense ;
        public ViewHolderAccountant(@NonNull View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.accountant_tv_key);
            tvIncome = itemView.findViewById(R.id.accountant_tv_income);
            tvExpense = itemView.findViewById(R.id.accountant_tv_expense);
        }
    }
}
