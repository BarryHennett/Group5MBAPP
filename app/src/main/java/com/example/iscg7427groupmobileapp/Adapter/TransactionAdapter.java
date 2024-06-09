package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.example.iscg7427groupmobileapp.Activity.TransactionDetails;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolderTransaction> {

    private final List<Map.Entry<String, User.Transaction>> originalTransactionList;
    private final List<Map.Entry<String, User.Transaction>> filteredTransactionList;
    private final Context context;
    private final String uid;

    public TransactionAdapter(Map<String, User.Transaction> transactionMap, Context context, String uid) {
        this.originalTransactionList = new ArrayList<>(transactionMap.entrySet());
        this.filteredTransactionList = new ArrayList<>(transactionMap.entrySet());
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolderTransaction onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_transaction, parent, false);
        return new ViewHolderTransaction(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTransaction holder, int position) {
        // Get the transaction at the current position
        Map.Entry<String, User.Transaction> entry = filteredTransactionList.get(position);
        String transactionId = entry.getKey();
        User.Transaction transaction = entry.getValue();

        // Extract relevant information from the transaction
        String category = transaction.getCategory();
        double amount = transaction.getAmount();
        String date = new SimpleDateFormat("MM-dd", Locale.US).format(transaction.getDate());
        String type = transaction.getType();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        // Bind data to ViewHolder views
        holder.txtCategory.setText(category);
        holder.txtAmount.setText(type.equals("Income") ? currencyFormat.format(amount) : "-" + currencyFormat.format(amount));
        holder.txtDate.setText(date);
        holder.txtAmount.setTextColor(context.getResources().getColor(type.equals("Income") ? R.color.blue : R.color.orange));

        // Set OnClickListener to handle item click
        holder.itemView.setOnClickListener(v -> {
            if (transactionId != null && uid != null) {
                // When item is clicked, open TransactionDetails activity and pass transaction ID
                Intent intent = new Intent(context, TransactionDetails.class);
                intent.putExtra("transactionId", transactionId);
                intent.putExtra("uid", uid);
                context.startActivity(intent);

                // Log that the transaction has been sent
                Log.d("TransactionAdapter", "Transaction sent: transactionId=" + transactionId + ", uid=" + uid);
            } else {
                Toast.makeText(context, "Transaction ID or User ID is missing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the filtered dataset
        return filteredTransactionList.size();

    }

    public void filter(String text) {
        filteredTransactionList.clear();
        if (text.isEmpty()) {
            filteredTransactionList.addAll(originalTransactionList);
        } else {
            text = text.toLowerCase();
            for (Map.Entry<String, User.Transaction> entry : originalTransactionList) {
                if (entry.getValue().getCategory().toLowerCase().contains(text)) {
                    filteredTransactionList.add(entry);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolderTransaction extends RecyclerView.ViewHolder {
        TextView txtCategory, txtDate, txtAmount;

        public ViewHolderTransaction(@NonNull View itemView) {
            super(itemView);
            // Initialize ViewHolder views
            txtCategory = itemView.findViewById(R.id.rv_transaction_category);
            txtDate = itemView.findViewById(R.id.rv_transaction_date);
            txtAmount = itemView.findViewById(R.id.rv_transaction_amount);
        }
    }
}
