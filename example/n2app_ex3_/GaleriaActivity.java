package com.example.n2app_ex3_;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GaleriaActivity extends AppCompatActivity implements GaleriaAdapter.OnImageDeleteListener {

    private RecyclerView recyclerViewGallery;
    private FloatingActionButton fabAddImage;
    private GaleriaAdapter galeriaAdapter;
    private List<String> imageList;
    private boolean isAdmin = false;
    private BancoDados bancoDados;

    // Use OpenDocument para obter acesso persistente à imagem
    private final ActivityResultLauncher<String[]> mGetContent = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        // Persist permission to access the URI
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        String imageUriString = uri.toString();
                        bancoDados.addImageUri(imageUriString);
                        loadImages(); // Recarrega as imagens para exibir a nova imagem
                        Toast.makeText(this, "Imagem adicionada!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Falha ao adicionar imagem.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        bancoDados = new BancoDados(this);
        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        fabAddImage = findViewById(R.id.fabAddImage);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    private void setupViews() {
        if (isAdmin) {
            fabAddImage.setVisibility(View.VISIBLE);
            // Inicia o seletor de imagens
            fabAddImage.setOnClickListener(v -> mGetContent.launch(new String[]{"image/*"}));
        } else {
            fabAddImage.setVisibility(View.GONE);
        }

        imageList = new ArrayList<>();
        galeriaAdapter = new GaleriaAdapter(this, imageList, isAdmin);
        galeriaAdapter.setOnImageDeleteListener(this);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewGallery.setAdapter(galeriaAdapter);
    }

    private void loadImages() {
        List<String> uris = bancoDados.getAllImageUris();
        imageList.clear();
        imageList.addAll(uris);
        galeriaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onImageDelete(String imageUri, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza de que deseja excluir esta imagem?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    bancoDados.deleteImageUri(imageUri);
                    imageList.remove(position);
                    galeriaAdapter.notifyItemRemoved(position);
                    galeriaAdapter.notifyItemRangeChanged(position, imageList.size());
                    Toast.makeText(GaleriaActivity.this, "Imagem removida!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
