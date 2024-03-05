package com.example.aat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DictionaryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dictionary2.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "words2";
    private static final String COLUMN_WORD = "word2";

    private static final String TAG = "DictionaryDatabase2";

    private Context context;

    public DictionaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Populate the database with the default theme
        populateDatabaseFromRaw(R.raw.anagram_pair);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_WORD + " TEXT PRIMARY KEY)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
    }

    public void populateDatabaseFromRaw(int themeResourceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        try {
            InputStream inputStream = context.getResources().openRawResource(themeResourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the word already exists in the database
                if (!isWordExist(db, line.trim())) {
                    db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COLUMN_WORD + ") VALUES ('" + line.trim() + "')");
                }
            }

            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading raw resource", e);
        }
    }

    private boolean isWordExist(SQLiteDatabase db, String word) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_WORD + "=?", new String[]{word});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public String getRandomWord() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_WORD + " FROM " + TABLE_NAME + " ORDER BY RANDOM() LIMIT 1", null);

        String word = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_WORD);
            if (columnIndex != -1) {
                word = cursor.getString(columnIndex);
            } else {
                Log.e(TAG, "Column not found: " + COLUMN_WORD);
            }
        }

        cursor.close();
        db.close();

        return word;
    }

    // Accessor methods for TABLE_NAME and COLUMN_WORD
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getColumnWord() {
        return COLUMN_WORD;
    }
}
