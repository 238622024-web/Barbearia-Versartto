package com.example.n2app_ex3_;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ContasAdapter extends RecyclerView.Adapter<ContasAdapter.ViewHolder> {

    private static final String TAG = "ContasAdapter";

    private final List<User> userList;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(User user);
    }

    public ContasAdapter(List<User> userList, OnDeleteClickListener onDeleteClickListener) {
        this.userList = userList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewName.setText(user.getName());
        holder.textViewEmail.setText(user.getEmail()); // Set the email
        holder.textViewId.setText("ID: " + user.getId());

        // Load profile image using Glide
        Uri imageUri = null;
        if (user.getProfileImageUri() != null && !user.getProfileImageUri().isEmpty()) {
            imageUri = Uri.parse(user.getProfileImageUri());
            Log.d(TAG, "Loading image for user: " + user.getName() + " with URI: " + imageUri.toString());
        } else {
            Log.d(TAG, "Image URI is null or empty for user: " + user.getName() + ", using placeholder.");
        }

        Glide.with(holder.itemView.getContext())
                .load(imageUri)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person) // Default icon if loading fails
                .into(holder.imageViewProfile);

        holder.buttonDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewEmail; // Added email TextView
        TextView textViewId;
        Button buttonDelete;
        ShapeableImageView imageViewProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewAccountName);
            textViewEmail = itemView.findViewById(R.id.textViewAccountEmail); // Find email TextView
            textViewId = itemView.findViewById(R.id.textViewAccountId);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteAccount);
            imageViewProfile = itemView.findViewById(R.id.imageViewAccountProfile);
        }
    }
}
