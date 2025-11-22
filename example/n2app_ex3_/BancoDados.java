package com.example.n2app_ex3_;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "barbearia.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela de Usuários
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT NOT NULL, " +
            "email TEXT UNIQUE NOT NULL, " +
            "senha TEXT NOT NULL);";

    // Tabela de Agendamentos
    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE appointments (" +
            "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "data_agendamento TEXT NOT NULL, " +
            "hora_agendamento TEXT NOT NULL, " +
            "servico TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id));";

    // Tabela do Calendário
    private static final String CREATE_TABLE_CALENDAR = "CREATE TABLE calendar (" +
            "calendar_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "data TEXT NOT NULL, " +
            "disponivel INTEGER DEFAULT 1, " +
            "dia_da_semana TEXT, " +
            "mes INTEGER, " +
            "ano INTEGER);";

    // Tabela de Histórico
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE history (" +
            "history_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "appointment_id INTEGER, " +
            "detalhes TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
            "FOREIGN KEY(appointment_id) REFERENCES appointments(appointment_id));";

    public BancoDados(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_CALENDAR);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Se houver uma atualização no banco de dados, você pode adicionar o código aqui
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS calendar");
        db.execSQL("DROP TABLE IF EXISTS appointments");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}
