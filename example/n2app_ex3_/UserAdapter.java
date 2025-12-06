package com.example.n2app_ex3_;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentUser = userList.get(position);
        holder.textViewUserName.setText(currentUser.getName());
        holder.textViewUserType.setText(currentUser.getUserType());

        if ("Administrador".equals(currentUser.getUserType())) {
            holder.textViewUserType.setBackgroundColor(Color.parseColor("#FFC107")); // Gold for Admin
        } else {
            holder.textViewUserType.setBackgroundColor(Color.parseColor("#4CAF50")); // Green for User
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewUserType;

        UserViewHolder(View view) {
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUserName);
            textViewUserType = view.findViewById(R.id.textViewUserType);
        }
    }
}
