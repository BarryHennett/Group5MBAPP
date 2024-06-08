package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.example.iscg7427groupmobileapp.Activity.UserAllTransactions;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

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
        double net = income - expense;

        // Format the numbers as currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        holder.clientIncomeTv.setText(currencyFormat.format(income));
        holder.clientExpensesTv.setText(currencyFormat.format(expense));
        holder.clientNetTv.setText(currencyFormat.format(net));

        // Set the text color for net value
        if (net < 0) {
            holder.clientNetTv.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else {
            holder.clientNetTv.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }

        holder.clientNameTv.setText(client.getName());
        holder.clientOccupationTv.setText(client.getOccupation());

        holder.toUserAllTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (client != null) {
                    Intent intent = new Intent(context, UserAllTransactions.class);
                    intent.putExtra("uid", userKey); // Use the actual user ID
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Invalid User ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }

    public class AccountClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientNameTv, clientOccupationTv, clientIncomeTv, clientExpensesTv, clientNetTv;
        LinearLayout toUserAllTransactions;
        public AccountClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTv = itemView.findViewById(R.id.clientNameTv);
            clientOccupationTv = itemView.findViewById(R.id.clientOccupationTv);
            clientIncomeTv = itemView.findViewById(R.id.clientIncomeTv);
            clientExpensesTv = itemView.findViewById(R.id.clientExpensesTv);
            clientNetTv = itemView.findViewById(R.id.clientNetTv);
            toUserAllTransactions = itemView.findViewById(R.id.toAllTransactions);
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
