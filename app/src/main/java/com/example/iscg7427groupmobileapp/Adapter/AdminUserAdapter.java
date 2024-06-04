package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private ArrayList<HashMap<String, String>> userList;
    private ArrayList<HashMap<String, String>> filteredUserList;
    private Context context;

    public AdminUserAdapter(ArrayList<HashMap<String, String>> userList, Context context) {
        this.userList = userList;
        this.filteredUserList = new ArrayList<>(userList);
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_dashboard_user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        HashMap<String, String> userAndAccountantMap = filteredUserList.get(position);

        holder.nameTextView.setText(userAndAccountantMap.get("name"));
        holder.typeTextView.setText(userAndAccountantMap.get("type"));
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    public void filter(String text) {
        filteredUserList.clear();
        if (text.isEmpty()) {
            filteredUserList.addAll(userList);
        } else {
            text = text.toLowerCase();
            for (HashMap<String, String> item : userList) {
                if (item.get("name").toLowerCase().contains(text)) {
                    filteredUserList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, typeTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userNameTextView);
            typeTextView = itemView.findViewById(R.id.userTypeTextView);
        }
    }
}
