package com.example.barvius;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper implements IDBHandler {

    private static DBHandler instance;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=on" );
        db.execSQL("CREATE TABLE dictionary (" +
                "id INTEGER PRIMARY KEY,"+
                "ru TEXT,"+
                "en TEXT"+
                ")");
        db.execSQL("CREATE TABLE archive (" +
                "id INTEGER PRIMARY KEY," +
                "dictionary_id INTEGER NOT NULL," +
                "FOREIGN KEY (dictionary_id) REFERENCES dictionary(id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dictionary");
        db.execSQL("DROP TABLE IF EXISTS archive");
        onCreate(db);
    }

    @Override
    public DBItems getRandomItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("dictionary", new String[] { "id","ru","en" }, "IFNULL((SELECT dictionary_id FROM archive WHERE archive.dictionary_id = dictionary.id),0) = 0",null,
                null, null, "RANDOM()", "1");

        if (cursor != null){
            cursor.moveToFirst();
        }

        return new DBItems(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
    }

    @Override
    public List<DBItems> getRandomSet(int setSize, long masterId) {
        List<DBItems> list = new ArrayList<DBItems>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("dictionary", new String[] { "id","ru","en" }, "id != ?", new String[]{Long.toString(masterId)},
                null, null, "RANDOM()", Integer.toString(setSize));

        if (cursor.moveToFirst()) {
            do {
                DBItems ithems = new DBItems();
                ithems.setId(cursor.getInt(0));
                ithems.setRu(cursor.getString(1));
                ithems.setEn(cursor.getString(2));
                list.add(ithems);
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Override
    public List<DBItems> getAll() {
        List<DBItems> list = new ArrayList<DBItems>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("dictionary", new String[] { "id","ru","en" }, null, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                DBItems ithems = new DBItems();
                ithems.setId(cursor.getInt(0));
                ithems.setRu(cursor.getString(1));
                ithems.setEn(cursor.getString(2));
                list.add(ithems);
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Override
    public boolean moveToArchive(DBItems items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dictionary_id", items.getId());
        db.insert("archive", null, values);
        db.close();
        return false;
    }

    @Override
    public boolean addItems(DBItems items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ru", items.getRu());
        values.put("en", items.getEn());
        db.insert("dictionary", null, values);
        db.close();
        return false;
    }

    @Override
    public void truncateArchive() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from archive");
        db.close();
    }

    @Override
    public boolean dictionaryIsEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM dictionary";
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        return cursor.getInt(0) == 0 ? true:false;
    }

    @Override
    public boolean testIsAvailable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("dictionary", new String[] { "id","ru","en" }, "IFNULL((SELECT dictionary_id FROM archive WHERE archive.dictionary_id = dictionary.id),0) = 0",null,
                null, null, "RANDOM()", "1");
        cursor.moveToFirst();
        return cursor.getCount() == 0 ? false:true;
    }

    @Override
    public String info() {
        String tmp="Всего слов = ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("dictionary", new String[] { "COUNT(id)"}, null,null,
                null, null, "RANDOM()", "1");
        cursor.moveToFirst();

        tmp += cursor.getInt(0);
        tmp += "\n";
        tmp += "В архиве = ";

        cursor = db.query("archive", new String[] { "COUNT(id)"}, null,null,
                null, null, "RANDOM()", "1");
        cursor.moveToFirst();
        tmp += cursor.getInt(0);
        return tmp;
    }

    private DBHandler(Context context){
        super(context, "dictionary", null, 1);
    }

    public static DBHandler getInstance(){
//        if(instance == null){
//
//        }
        return instance;
    }

    public static void init(Context context){
        if(instance == null){
            instance = new DBHandler(context);
        }
    }
}
