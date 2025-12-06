package com.example.n2app_ex3_;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private BancoDados bancoDados;
    private long adminId;

    private MaterialCardView cardViewSchedules, cardViewCalendar, cardViewAccounts, cardViewGallery, cardViewProfile, cardViewFeed;
    private ImageView notificationIcon;
    private LinearLayout profileButtonHeader;
    private TextView textViewAdminNameHeader;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bancoDados = new BancoDados(this);
        adminId = getIntent().getLongExtra("USER_ID", -1);

        // Initialize Views
        cardViewSchedules = findViewById(R.id.cardViewSchedules);
        cardViewCalendar = findViewById(R.id.cardViewCalendar);
        cardViewAccounts = findViewById(R.id.cardViewAccounts);
        cardViewGallery = findViewById(R.id.cardViewGallery);
        cardViewProfile = findViewById(R.id.cardViewProfile);
        cardViewFeed = findViewById(R.id.cardViewFeed);
        notificationIcon = findViewById(R.id.notification_icon);
        profileButtonHeader = findViewById(R.id.profile_button_header_admin);
        textViewAdminNameHeader = findViewById(R.id.textViewAdminNameHeader);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        setupClickListeners();
        setupRecyclerView();
        loadAdminData();
    }

    private void setupClickListeners() {
        cardViewSchedules.setOnClickListener(v -> startActivity(new Intent(this, AllSchedulesActivity.class)));
        cardViewCalendar.setOnClickListener(v -> startActivity(new Intent(this, CalendarioAdminActivity.class)));
        cardViewAccounts.setOnClickListener(v -> startActivity(new Intent(this, ContasCadastradasActivity.class)));
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(this, NotificacoesAdminActivity.class)));
        profileButtonHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilAdminActivity.class);
            intent.putExtra("USER_ID", adminId);
            startActivity(intent);
        });
        cardViewGallery.setOnClickListener(v -> {
            Intent intent = new Intent(this, GaleriaActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });
        cardViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilAdminActivity.class);
            intent.putExtra("USER_ID", adminId);
            startActivity(intent);
        });
        cardViewFeed.setOnClickListener(v -> startActivity(new Intent(this, AdminFeedActivity.class)));
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void loadAdminData() {
        if (adminId == -1) return;
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            Cursor cursor = db.query("users", new String[]{"nome"}, "user_id = ?", new String[]{String.valueOf(adminId)}, null, null, null);
            if (cursor.moveToFirst()) {
                String adminName = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                runOnUiThread(() -> textViewAdminNameHeader.setText(adminName));
            }
            cursor.close();
        }).start();
    }

    private void checkCancellationNotifications() {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM appointments WHERE cancelamento_pendente = 1", null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    runOnUiThread(() -> notificationIcon.setVisibility(count > 0 ? View.VISIBLE : View.GONE));
                }
                cursor.close();
            }
        }).start();
    }

    private void loadRecentUsers() {
        new Thread(() -> {
            List<User> recentUsers = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();
            Cursor cursor = db.query("users", new String[]{"user_id", "nome", "user_type", "email", "profile_image_uri"}, null, null, null, null, "user_id DESC", "5");
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                    String userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String profileImageUri = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_uri"));
                    recentUsers.add(new User(id, name, userType, email, profileImageUri));
                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                userList.clear();
                userList.addAll(recentUsers);
                userAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCancellationNotifications();
        loadRecentUsers();
    }
}
