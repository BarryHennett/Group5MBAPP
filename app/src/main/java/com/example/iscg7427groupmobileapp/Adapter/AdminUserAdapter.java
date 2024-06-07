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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Activity.AdminUpdateUserActivity;
import com.example.iscg7427groupmobileapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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

        String userId = userAndAccountantMap.get("key");
        String userType = userAndAccountantMap.get("type");

        // Set text color based on userType
        if ("Accountants".equals(userType)) {
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.purple));
            holder.typeTextView.setTextColor(ContextCompat.getColor(context, R.color.purple));
        } else {
            // Optionally, reset to default color for other types
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.typeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        if (userId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://group5-6aa2b-default-rtdb.firebaseio.com/")
                    .getReference(userType).child(userId);

            // Fetch user status from Firebase
            userRef.child("active").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isActive = snapshot.getValue(Boolean.class);
                    if (isActive != null && isActive) {
                        holder.deactivateBtn.setText("Deactivate");
                    } else {
                        holder.deactivateBtn.setText("Activate");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

            holder.updateBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, AdminUpdateUserActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userType", userType);
                context.startActivity(intent);
            });

            holder.deactivateBtn.setOnClickListener(v -> {
                userRef.child("active").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean isActive = snapshot.getValue(Boolean.class);
                        if (isActive != null) {
                            userRef.child("active").setValue(!isActive).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (isActive) {
                                        Toast.makeText(context, "User deactivated", Toast.LENGTH_SHORT).show();
                                        holder.deactivateBtn.setText("Activate");
                                    } else {
                                        Toast.makeText(context, "User activated", Toast.LENGTH_SHORT).show();
                                        holder.deactivateBtn.setText("Deactivate");
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to update user status", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            });
        } else {
            holder.deactivateBtn.setText("N/A");
            holder.updateBtn.setOnClickListener(v -> Toast.makeText(context, "Invalid user ID", Toast.LENGTH_SHORT).show());
            holder.deactivateBtn.setOnClickListener(v -> Toast.makeText(context, "Invalid user ID", Toast.LENGTH_SHORT).show());
        }
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
        TextView nameTextView, typeTextView, updateBtn, deactivateBtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userNameTextView);
            typeTextView = itemView.findViewById(R.id.userTypeTextView);
            updateBtn = itemView.findViewById(R.id.updateButton);
            deactivateBtn = itemView.findViewById(R.id.deactivateButton);
        }
    }
}
