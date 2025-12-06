package com.example.n2app_ex3_;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContasCadastradasActivity extends AppCompatActivity implements ContasAdapter.OnDeleteClickListener {

    private RecyclerView recyclerViewContas;
    private ContasAdapter contasAdapter;
    private List<User> userList;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_cadastradas);

        bancoDados = new BancoDados(this);

        recyclerViewContas = findViewById(R.id.recyclerViewContas);
        recyclerViewContas.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        contasAdapter = new ContasAdapter(userList, this);
        recyclerViewContas.setAdapter(contasAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllUsersFromDatabase();
    }

    private void loadAllUsersFromDatabase() {
        new Thread(() -> {
            List<User> users = new ArrayList<>();
            SQLiteDatabase db = bancoDados.getReadableDatabase();

            Cursor cursor = db.query("users", null, null, null, null, null, "user_id ASC");

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type"));
                    String profileImageUri = cursor.getString(cursor.getColumnIndexOrThrow("profile_image_uri"));
                    users.add(new User(id, name, userType, email, profileImageUri));
                } while (cursor.moveToNext());
            }
            cursor.close();

            runOnUiThread(() -> {
                userList.clear();
                userList.addAll(users);
                contasAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onDeleteClick(final User user) {
        new AlertDialog.Builder(this)
                .setTitle("Deletar Usuário")
                .setMessage("Tem certeza que deseja deletar a conta de " + user.getName() + "? Esta ação não pode ser desfeita.")
                .setPositiveButton("Deletar", (dialog, which) -> {
                    deleteUserFromDatabase(user);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteUserFromDatabase(User user) {
        new Thread(() -> {
            SQLiteDatabase db = bancoDados.getWritableDatabase();
            int deletedRows = db.delete("users", "user_id = ?", new String[]{String.valueOf(user.getId())});

            runOnUiThread(() -> {
                if (deletedRows > 0) {
                    Toast.makeText(this, "Usuário " + user.getName() + " deletado com sucesso.", Toast.LENGTH_SHORT).show();
                    loadAllUsersFromDatabase();
                } else {
                    Toast.makeText(this, "Erro ao deletar o usuário.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
