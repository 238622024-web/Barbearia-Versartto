package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private Spinner spinnerUserType;
    private String selectedUserType;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        bancoDados = new BancoDados(this);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
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
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateAccountActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
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

                if ("Administrador".equals(selectedUserType)) {
                    Intent intent = new Intent(CreateAccountActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            } else {
                Toast.makeText(CreateAccountActivity.this, "Erro ao criar conta.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
