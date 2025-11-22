package com.example.n2app_ex3_;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements UserAdapter.OnUserDeleteListener {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private BancoDados bancoDados;
    private Spinner spinnerSpecialty;
    private Button buttonViewSchedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bancoDados = new BancoDados(this);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        buttonViewSchedules = findViewById(R.id.buttonViewSchedules);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerViewUsers.setAdapter(userAdapter);

        loadUsersFromDatabase();
        loadSpecialtiesFromDatabase();

        buttonViewSchedules.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AllSchedulesActivity.class);
            startActivity(intent);
        });
    }

    private void loadUsersFromDatabase() {
        userList.clear();
        SQLiteDatabase db = bancoDados.getReadableDatabase();

        Cursor cursor = db.query("users", null, null, null, null, null, "nome ASC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type"));
                userList.add(new User(id, name, email, userType));
            } while (cursor.moveToNext());
        }
        cursor.close();
        userAdapter.notifyDataSetChanged();
    }

    private void loadSpecialtiesFromDatabase() {
        List<String> specialties = new ArrayList<>();
        SQLiteDatabase db = bancoDados.getReadableDatabase();
        Cursor cursor = db.query("specialties", new String[]{"name"}, null, null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                specialties.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);
    }

    @Override
    public void onUserDelete(User user) {
        SQLiteDatabase db = bancoDados.getWritableDatabase();
        int deletedRows = db.delete("users", "user_id=?", new String[]{String.valueOf(user.getId())});

        if (deletedRows > 0) {
            Toast.makeText(this, "Usuário " + user.getName() + " deletado.", Toast.LENGTH_SHORT).show();
            loadUsersFromDatabase(); // Recarrega a lista
        } else {
            Toast.makeText(this, "Erro ao deletar usuário.", Toast.LENGTH_SHORT).show();
        }
    }
}
