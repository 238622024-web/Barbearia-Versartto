package com.example.n2app_ex3_;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder> {

    private Context context;
    private List<String> imageList;
    private boolean isAdmin;
    private OnImageDeleteListener deleteListener;

    public interface OnImageDeleteListener {
        void onImageDelete(String imageUri, int position);
    }

    public void setOnImageDeleteListener(OnImageDeleteListener listener) {
        this.deleteListener = listener;
    }

    public GaleriaAdapter(Context context, List<String> imageList, boolean isAdmin) {
        this.context = context;
        this.imageList = imageList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public GaleriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaleriaViewHolder holder, int position) {
        String imageUrl = imageList.get(position);

        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageViewGallery);

        if (isAdmin) {
            holder.buttonDeleteImage.setVisibility(View.VISIBLE);
            holder.buttonDeleteImage.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onImageDelete(imageUrl, holder.getAdapterPosition());
                }
            });
        } else {
            holder.buttonDeleteImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class GaleriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewGallery;
        ImageButton buttonDeleteImage;

        public GaleriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewGallery = itemView.findViewById(R.id.imageViewGallery);
            buttonDeleteImage = itemView.findViewById(R.id.buttonDeleteImage);
        }
    }
}
