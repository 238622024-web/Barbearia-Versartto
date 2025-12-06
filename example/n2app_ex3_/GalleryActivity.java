package com.example.n2app_ex3_;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.OnDeleteClickListener {

    private RecyclerView recyclerViewGallery;
    private FloatingActionButton fabAddImage;
    private GalleryAdapter galleryAdapter;
    private List<String> imageList;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        fabAddImage = findViewById(R.id.fabAddImage);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {
            fabAddImage.setVisibility(View.VISIBLE);
        } else {
            fabAddImage.setVisibility(View.GONE);
        }

        imageList = new ArrayList<>();
        // TODO: Replace with actual data from the database
        imageList.add("https://picsum.photos/200/300"); // Dummy data
        imageList.add("https://picsum.photos/200/300"); // Dummy data
        imageList.add("https://picsum.photos/200/300"); // Dummy data
        imageList.add("https://picsum.photos/200/300"); // Dummy data

        galleryAdapter = new GalleryAdapter(this, imageList, isAdmin);
        galleryAdapter.setOnDeleteClickListener(this);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewGallery.setAdapter(galleryAdapter);

        // TODO: Implement fabAddImage click listener to add new images
    }

    @Override
    public void onDeleteClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Imagem")
                .setMessage("Tem certeza que deseja excluir esta imagem?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    String imagePath = imageList.get(position);
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        if (imageFile.delete()) {
                            imageList.remove(position);
                            galleryAdapter.notifyItemRemoved(position);
                        }
                    } else {
                        // If the file doesn't exist locally, it might be a URL
                        // For now, just remove it from the list
                        imageList.remove(position);
                        galleryAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
