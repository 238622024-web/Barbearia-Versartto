package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private Spinner spinnerUserType;
    private String selectedUserType = "Usuário"; // Initialized with a default value
    private BancoDados bancoDados;

    private static final String ADMIN_SECURITY_CODE = "admin20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        bancoDados = new BancoDados(this);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        buttonRegister = findViewById(R.id.buttonRegister);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUserType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserType = "Usuário"; // Default value
            }
        });

        buttonRegister.setOnClickListener(v -> {
            if ("Administrador".equals(selectedUserType)) {
                showAdminCodeDialog();
            } else {
                proceedToCreateAccount();
            }
        });
    }

    private void showAdminCodeDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_admin_code, null);
        final TextInputEditText input = dialogView.findViewById(R.id.editTextAdminCode);

        new AlertDialog.Builder(this)
                .setTitle("Código de Segurança")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String code = "";
                    if (input.getText() != null) {
                        code = input.getText().toString();
                    }
                    if (ADMIN_SECURITY_CODE.equals(code)) {
                        proceedToCreateAccount();
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Código incorreto!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void proceedToCreateAccount() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(CreateAccountActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(CreateAccountActivity.this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = bancoDados.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", name);
        values.put("email", email);
        values.put("senha", password);
        values.put("user_type", selectedUserType);

        long newRowId = db.insert("users", null, values);

        if (newRowId != -1) {
            String message = "Conta criada como " + selectedUserType + " com sucesso!";
            Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT).show();

            Intent intent;
            if ("Administrador".equals(selectedUserType)) {
                intent = new Intent(CreateAccountActivity.this, AdminActivity.class);
            } else {
                intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(CreateAccountActivity.this, "Erro ao criar conta. Email pode já existir.", Toast.LENGTH_SHORT).show();
        }
    }
}
