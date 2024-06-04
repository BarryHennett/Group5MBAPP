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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountantClientAdapter extends RecyclerView.Adapter<AccountantClientAdapter.AccountClientViewHolder> {
    private HashMap<String, User> userList;
    private HashMap<String, User> userListFiltered;
    private Context context;

    public AccountantClientAdapter(HashMap<String, User> userList, Context context) {
        this.userList = userList;
        this.userListFiltered = new HashMap<>(userList);
        this.context = context;
    }

    @NonNull
    @Override
    public AccountantClientAdapter.AccountClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accountant_dashboard_client_list_item, parent, false);
        return new AccountClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountantClientAdapter.AccountClientViewHolder holder, int position) {
        String userKey = userListFiltered.keySet().toArray()[position].toString();
        User client = userListFiltered.get(userKey);
        HashMap<String, User.Transaction> transactionMap = client.getTransactions();
        double income = 0;
        double expense = 0;
        for (String key : transactionMap.keySet()) {
            if ("Income".equals(transactionMap.get(key).getType())) {
                income += transactionMap.get(key).getAmount();
            } else {
                expense += transactionMap.get(key).getAmount();
            }
        }
        holder.clientNameTv.setText(client.getName());
        holder.clientOccupationTv.setText(client.getOccupation());
        holder.clientIncomeTv.setText(String.format("$%.2f", income));
        holder.clientExpensesTv.setText(String.format("$%.2f", expense));
        holder.clientNetTv.setText(String.format("$%.2f", income - expense));
    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }

    public class AccountClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientNameTv, clientOccupationTv, clientIncomeTv, clientExpensesTv, clientNetTv;

        public AccountClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTv = itemView.findViewById(R.id.clientNameTv);
            clientOccupationTv = itemView.findViewById(R.id.clientOccupationTv);
            clientIncomeTv = itemView.findViewById(R.id.clientIncomeTv);
            clientExpensesTv = itemView.findViewById(R.id.clientExpensesTv);
            clientNetTv = itemView.findViewById(R.id.clientNetTv);
        }
    }

    // Method to update the user list
    public void updateUserList(HashMap<String, User> newUserList) {
        userList = newUserList;
        userListFiltered = new HashMap<>(newUserList);
        notifyDataSetChanged();
    }

    // Method to filter the user list
    public void filter(String text) {
        userListFiltered.clear();
        if (text.isEmpty()) {
            userListFiltered.putAll(userList);
        } else {
            text = text.toLowerCase();
            for (String key : userList.keySet()) {
                User user = userList.get(key);
                if (user.getName().toLowerCase().contains(text)) {
                    userListFiltered.put(key, user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
