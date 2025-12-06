package com.example.n2app_ex3_;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public EditText editTextUser;
    public EditText editTextPassword;
    public Button buttonEnter;
    public Button buttonRecoverPassword;
    public Button buttonCreateAccount;
    private BancoDados dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new BancoDados(this);

        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonEnter = findViewById(R.id.buttonEnter);
        buttonRecoverPassword = findViewById(R.id.buttonRecoverPassword);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUser.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = null;
                try {
                    String query = "SELECT * FROM users WHERE email = ? AND senha = ?";
                    cursor = db.rawQuery(query, new String[]{email, password});

                    if (cursor != null && cursor.moveToFirst()) {
                        long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                        String userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type"));

                        // Save user session
                        SharedPreferences userSession = getSharedPreferences("user_session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = userSession.edit();
                        editor.putLong("user_id", userId);
                        editor.apply();

                        if ("Administrador".equalsIgnoreCase(userType.trim())) {
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Email ou senha inválidos.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    String errorMessage = e.getMessage();
                    Log.e("LOGIN_CRITICAL_ERROR", "Exception during login: " + errorMessage, e);
                    Toast.makeText(MainActivity.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });

        buttonRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
