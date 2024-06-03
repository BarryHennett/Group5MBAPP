package com.example.iscg7427groupmobileapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Activity.AdminUpdateUserActivity;
import com.example.iscg7427groupmobileapp.Model.Accountant;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<Object> userList;
    private Context context;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference accountantDatabaseReference;

    public AdminUserAdapter(List<Object> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.userDatabaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/").getReference("Users");
        this.accountantDatabaseReference = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/").getReference("Accountants");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_dashboard_user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Object object = userList.get(position);
        if (object instanceof User) {
            User user = (User) object;
            holder.nameTextView.setText(user.getName());
            holder.typeTextView.setText(user.getType());

            holder.updateButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AdminUpdateUserActivity.class);
                intent.putExtra("USER_ID", user.getId());
                context.startActivity(intent);
            });

            holder.deactivateButton.setOnClickListener(v -> {
                toggleUserActivation(user);
            });

        } else if (object instanceof Accountant) {
            Accountant accountant = (Accountant) object;
            holder.nameTextView.setText(accountant.getName());
            holder.typeTextView.setText(accountant.getType());

            holder.updateButton.setOnClickListener(v -> {
                // Implement update accountant information logic
            });

            holder.deactivateButton.setOnClickListener(v -> {
                toggleAccountantActivation(accountant);
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void toggleUserActivation(User user) {
        user.setActive(!user.isActive());
        userDatabaseReference.child(user.getId()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Notify user of success
                })
                .addOnFailureListener(e -> {
                    // Notify user of failure
                });
    }

    private void toggleAccountantActivation(Accountant accountant) {
        accountant.setActive(!accountant.isActive());
        accountantDatabaseReference.child(accountant.getId()).setValue(accountant)
                .addOnSuccessListener(aVoid -> {
                    // Notify user of success
                })
                .addOnFailureListener(e -> {
                    // Notify user of failure
                });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, typeTextView,updateButton,deactivateButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userNameTextView);
            typeTextView = itemView.findViewById(R.id.userTypeTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            deactivateButton = itemView.findViewById(R.id.deactivateButton);
        }
    }
}
