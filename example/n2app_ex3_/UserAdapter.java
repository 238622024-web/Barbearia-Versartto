package com.example.n2app_ex3_;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onUserDelete(User user);
    }

    public UserAdapter(List<User> userList, OnUserDeleteListener deleteListener) {
        this.userList = userList;
        this.deleteListener = deleteListener;
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
        holder.buttonDeleteUser.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onUserDelete(currentUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        Button buttonDeleteUser;

        UserViewHolder(View view) {
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUserName);
            buttonDeleteUser = view.findViewById(R.id.buttonDeleteUser);
        }
    }
}
