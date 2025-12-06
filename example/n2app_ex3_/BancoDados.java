package com.example.n2app_ex3_;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BancoDados extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "barbearia.db";
    private static final int DATABASE_VERSION = 10; // Incremented version

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String TABLE_CALENDAR = "calendar";
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_SPECIALTIES = "specialties";
    private static final String TABLE_GALLERY_IMAGES = "gallery_images";
    private static final String TABLE_FEED = "feed_noticias";

    // Common column names
    private static final String KEY_USER_ID = "user_id";
    private static final String COLUMN_PROFILE_IMAGE_URI = "profile_image_uri";

    // USERS Table - column names
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT NOT NULL, " +
            "email TEXT UNIQUE NOT NULL, " +
            "senha TEXT NOT NULL, " +
            "user_type TEXT NOT NULL DEFAULT 'Usuário', " +
            "nome_completo TEXT, " +
            "data_nascimento TEXT, " +
            "celular TEXT, " +
            "cep TEXT, " +
            "logradouro TEXT, " +
            "numero TEXT, " +
            "complemento TEXT, " +
            "bairro TEXT, " +
            "cidade TEXT, " +
            "estado TEXT, " +
            "nacionalidade TEXT, " +
            COLUMN_PROFILE_IMAGE_URI + " TEXT);";

    // APPOINTMENTS Table - column names
    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
            "appointment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "data_agendamento TEXT NOT NULL, " +
            "hora_agendamento TEXT NOT NULL, " +
            "servico TEXT, " +
            "profissional TEXT, " +
            "justificativa TEXT, " +
            "notificacao_pendente INTEGER DEFAULT 0, " +
            "justificativa_cancelamento TEXT, " +
            "cancelamento_pendente INTEGER DEFAULT 0, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id));";

    // CALENDAR Table - column names
    private static final String CREATE_TABLE_CALENDAR = "CREATE TABLE " + TABLE_CALENDAR + " (" +
            "calendar_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "data TEXT NOT NULL, " +
            "disponivel INTEGER DEFAULT 1, " +
            "dia_da_semana TEXT, " +
            "mes INTEGER, " +
            "ano INTEGER);";

    // HISTORY Table - column names
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " (" +
            "history_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "appointment_id INTEGER, " +
            "detalhes TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
            "FOREIGN KEY(appointment_id) REFERENCES appointments(appointment_id));";

    // SPECIALTIES Table - column names
    private static final String CREATE_TABLE_SPECIALTIES = "CREATE TABLE " + TABLE_SPECIALTIES + " (" +
            "specialty_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL UNIQUE);";

    // GALLERY_IMAGES Table - column names
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_IMAGE_URI = "image_uri";
    private static final String CREATE_TABLE_GALLERY_IMAGES = "CREATE TABLE " + TABLE_GALLERY_IMAGES + " (" +
            COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IMAGE_URI + " TEXT NOT NULL);";

    // FEED Table - column names
    private static final String CREATE_TABLE_FEED = "CREATE TABLE " + TABLE_FEED + " (" +
            "feed_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "titulo TEXT, " +
            "data TEXT, " +
            "descricao TEXT, " +
            "image_uri TEXT);";

    public BancoDados(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_CALENDAR);
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CREATE_TABLE_SPECIALTIES);
        db.execSQL(CREATE_TABLE_GALLERY_IMAGES);
        db.execSQL(CREATE_TABLE_FEED);
        insertDefaultSpecialties(db);
        insertDefaultFeed(db);
    }

    private void insertDefaultSpecialties(SQLiteDatabase db) {
        String[] specialties = {"Corte de Cabelo", "Barba", "Manicure", "Pedicure"};
        ContentValues values = new ContentValues();
        for (String specialty : specialties) {
            values.put("name", specialty);
            db.insert(TABLE_SPECIALTIES, null, values);
        }
    }

    private void insertDefaultFeed(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("titulo", "Reabertura");
        values.put("data", "23 de setembro");
        values.put("descricao", "A barbearia abrirá no dia 23/09 com novidades exclusivas para clientes");
        values.put("image_uri", ""); // You can set a default image later
        db.insert(TABLE_FEED, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE users ADD COLUMN user_type TEXT NOT NULL DEFAULT 'Usuário'");
            db.execSQL(CREATE_TABLE_SPECIALTIES);
            insertDefaultSpecialties(db);
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE appointments ADD COLUMN justificativa TEXT");
            db.execSQL("ALTER TABLE appointments ADD COLUMN notificacao_pendente INTEGER DEFAULT 0");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE appointments ADD COLUMN justificativa_cancelamento TEXT");
            db.execSQL("ALTER TABLE appointments ADD COLUMN cancelamento_pendente INTEGER DEFAULT 0");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE users ADD COLUMN nome_completo TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN data_nascimento TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN celular TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN cep TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN logradouro TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN numero TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN complemento TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN bairro TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN cidade TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN estado TEXT");
            db.execSQL("ALTER TABLE users ADD COLUMN nacionalidade TEXT");
        }
        if (oldVersion < 6) {
            db.execSQL(CREATE_TABLE_GALLERY_IMAGES);
        }
        if (oldVersion < 7) {
            // No changes, just bumping version to ensure onUpgrade is called if needed
        }
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PROFILE_IMAGE_URI + " TEXT;");
        }
        if (oldVersion < 9) {
            db.execSQL("ALTER TABLE " + TABLE_APPOINTMENTS + " ADD COLUMN profissional TEXT;");
        }
        if (oldVersion < 10) {
            db.execSQL(CREATE_TABLE_FEED);
            insertDefaultFeed(db);
        }
    }

    // --- Gallery Methods (Corrected) ---

    public long addImageUri(String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI, imageUri);
        // The database is not closed here to allow the transaction to complete.
        return db.insert(TABLE_GALLERY_IMAGES, null, values);
    }

    public List<String> getAllImageUris() {
        List<String> imageUriList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_GALLERY_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                imageUriList.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // The database is not closed here; the helper manages the connection.
        return imageUriList;
    }

    public void deleteImageUri(String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GALLERY_IMAGES, COLUMN_IMAGE_URI + " = ?",
                new String[]{imageUri});
        // The database is not closed here.
    }
}
