package com.example.iscg7427groupmobileapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterAdmin extends RecyclerView.Adapter{

    private ArrayList<HashMap<String, String>> userAndAccountantList;
    private Context context;
    public AdapterAdmin(ArrayList <HashMap<String, String>> userAndAccountantList, Context context) {
        this.userAndAccountantList = userAndAccountantList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = View.inflate(context, R.layout.recycler_admin, null);
        return new ViewHolderAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderAdmin viewHolder = (ViewHolderAdmin) holder;
        HashMap<String, String> userAndAccountantMap = userAndAccountantList.get(position);

        viewHolder.tvKey.setText(userAndAccountantMap.get("key"));
        viewHolder.tvName.setText(userAndAccountantMap.get("name"));
        viewHolder.tvType.setText(userAndAccountantMap.get("type"));
        viewHolder.tvActive.setText(userAndAccountantMap.get("active"));
    }

    @Override
    public int getItemCount() {
        return userAndAccountantList.size();
    }

    public static class ViewHolderAdmin extends RecyclerView.ViewHolder {
        TextView tvKey, tvName, tvType, tvActive;
        public ViewHolderAdmin(@NonNull View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.admin_rv_txt_key);
            tvName = itemView.findViewById(R.id.admin_rv_txt_name);
            tvType = itemView.findViewById(R.id.admin_rv_txt_type);
            tvActive = itemView.findViewById(R.id.admin_rv_txt_active);
        }
    }
}
