package com.itderrickh.frolf.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocalDbHandler extends SQLiteOpenHelper {
    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SETTINGS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null" +
            ");";

    public LocalDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LocalDbHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    public void insertPokemon(Pokemon pokemon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, pokemon.getName());
        db.insert(TABLE_SETTINGS, null, values);
        db.close();
    }

    public List<Pokemon> getAllPokemon() {
        List<Pokemon> pokemonList = new ArrayList<Pokemon>();
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);

        if (cur.moveToFirst()) {
            do {
                Pokemon pokemon = new Pokemon();
                pokemon.setNumber(Integer.parseInt(cur.getString(0)));
                pokemon.setName(cur.getString(1));
                // Adding contact to list
                pokemonList.add(pokemon);
            } while (cur.moveToNext());
        }

        return pokemonList;
    }

    public boolean hasValues() {
        String countQuery = "SELECT * FROM " + TABLE_SETTINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}