package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class AlteracaoActivity extends AppCompatActivity {

    private TextInputEditText etNomeCompleto, etDataNascimento, etCelular, etCep, etLogradouro, etNumero, etComplemento, etBairro, etCidade, etEstado, etNacionalidade;
    private Button btnSalvar;

    private BancoDados bancoDados;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alteracao);

        bancoDados = new BancoDados(this);
        userId = getIntent().getLongExtra("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        loadUserData();

        btnSalvar.setOnClickListener(v -> saveUserData());
    }

    private void initializeViews() {
        etNomeCompleto = findViewById(R.id.et_nome_completo);
        etDataNascimento = findViewById(R.id.et_data_nascimento);
        etCelular = findViewById(R.id.et_celular);
        etCep = findViewById(R.id.et_cep);
        etLogradouro = findViewById(R.id.et_logradouro);
        etNumero = findViewById(R.id.et_numero);
        etComplemento = findViewById(R.id.et_complemento);
        etBairro = findViewById(R.id.et_bairro);
        etCidade = findViewById(R.id.et_cidade);
        etEstado = findViewById(R.id.et_estado);
        etNacionalidade = findViewById(R.id.et_nacionalidade);
        btnSalvar = findViewById(R.id.btn_salvar_alteracoes);
    }

    private void loadUserData() {
        SQLiteDatabase db = bancoDados.getReadableDatabase();
        Cursor cursor = db.query("users", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            setTextFromCursor(etNomeCompleto, cursor, "nome_completo");
            setTextFromCursor(etDataNascimento, cursor, "data_nascimento");
            setTextFromCursor(etCelular, cursor, "celular");
            setTextFromCursor(etCep, cursor, "cep");
            setTextFromCursor(etLogradouro, cursor, "logradouro");
            setTextFromCursor(etNumero, cursor, "numero");
            setTextFromCursor(etComplemento, cursor, "complemento");
            setTextFromCursor(etBairro, cursor, "bairro");
            setTextFromCursor(etCidade, cursor, "cidade");
            setTextFromCursor(etEstado, cursor, "estado");
            setTextFromCursor(etNacionalidade, cursor, "nacionalidade");
        }
        cursor.close();
    }

    private void setTextFromCursor(TextInputEditText editText, Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
            editText.setText(cursor.getString(columnIndex));
        }
    }

    private void saveUserData() {
        SQLiteDatabase db = bancoDados.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome_completo", etNomeCompleto.getText().toString());
        values.put("data_nascimento", etDataNascimento.getText().toString());
        values.put("celular", etCelular.getText().toString());
        values.put("cep", etCep.getText().toString());
        values.put("logradouro", etLogradouro.getText().toString());
        values.put("numero", etNumero.getText().toString());
        values.put("complemento", etComplemento.getText().toString());
        values.put("bairro", etBairro.getText().toString());
        values.put("cidade", etCidade.getText().toString());
        values.put("estado", etEstado.getText().toString());
        values.put("nacionalidade", etNacionalidade.getText().toString());

        int rowsAffected = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Cadastro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the profile screen
        } else {
            Toast.makeText(this, "Falha ao atualizar o cadastro.", Toast.LENGTH_SHORT).show();
        }
    }
}
