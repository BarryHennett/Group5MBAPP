package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;

import java.util.List;

public class AccountantClientAdapter extends RecyclerView.Adapter<AccountantClientAdapter.AccountClientViewHolder> {
    private List<User> userList;
    private Context context;

    public AccountantClientAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }
    @NonNull
    @Override
    public AccountantClientAdapter.AccountClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accountant_dashboard_client_list_item, parent, false);
        return new AccountClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountantClientAdapter.AccountClientViewHolder holder, int position) {
        User client = userList.get(position);
        holder.clientNameTv.setText(client.getName());
        holder.clientOccupationTv.setText(client.getOccupation());
        double[] totals = client.calculateTotals();
        holder.clientIncomeTv.setText(String.format("$%.2f", totals[0]));
        holder.clientExpensesTv.setText(String.format("$%.2f", totals[1]));
        holder.clientNetTv.setText(String.format("$%.2f", totals[2]));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class AccountClientViewHolder extends RecyclerView.ViewHolder{

        TextView clientNameTv, clientOccupationTv,toAllTransactions,clientIncomeTv,clientExpensesTv,clientNetTv ;
        public AccountClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTv= itemView.findViewById(R.id.clientNameTv);
            clientOccupationTv= itemView.findViewById(R.id.clientOccupationTv);
            toAllTransactions = itemView.findViewById(R.id.toAllTransactions);
            clientIncomeTv = itemView.findViewById(R.id.clientIncomeTv);
            clientExpensesTv = itemView.findViewById(R.id.clientExpensesTv);
            clientNetTv = itemView.findViewById(R.id.clientNetTv);
        }
    }
}
