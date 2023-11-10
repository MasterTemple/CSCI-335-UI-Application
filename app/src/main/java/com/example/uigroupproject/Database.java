package com.example.uigroupproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static Database DB;
    private static final String NAME = "ui_group_project";
    private static final int VERSION = 1;
    private static final String CATEGORY_TABLE = "categories";
    private static final String TRANSACTION_TABLE = "transactions";

    public Database(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategoriesTable = "CREATE TABLE " + CATEGORY_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "type TEXT," +
                "value REAL" +
                ")";


        String createTransactionsTable = "CREATE TABLE " + TRANSACTION_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "amount REAL," +
                "date DATE," +
                "categoryId INTEGER," +
                "description TEXT" +
                ")";
        db.execSQL(createCategoriesTable);
        db.execSQL(createTransactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private long createFromData(String table, ContentValues data) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(table, null, data);
        return id;
    }

    private void updateFromData(String table, ContentValues data, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(table, data, "id = ?", new String[] {String.valueOf(id)});
    }

    public void deleteById(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, "id = ?", new String[] {String.valueOf(id)});
    }

    public long createCategory(CategoryData category) {
        ContentValues data = new ContentValues();
        data.put("name", category.name);
        data.put("type", category.type);
        data.put("value", category.value);
        return createFromData(CATEGORY_TABLE, data);
    }

    public long createTransaction(TransactionData transaction) {
        ContentValues data = new ContentValues();
        data.put("name", transaction.name);
        data.put("amount", transaction.amount);
//        data.put("date", SQLiteDate(transaction.date));
        data.put("categoryId", transaction.categoryId);
        data.put("description", transaction.description);
        return createFromData(TRANSACTION_TABLE, data);
    }

    public void updateCategory(CategoryData category, long id) {
        ContentValues data = new ContentValues();
        data.put("name", category.name);
        data.put("type", category.type);
        data.put("value", category.value);
        updateFromData(CATEGORY_TABLE, data, id);
    }

    public void updateTransaction(TransactionData transaction, long id) {
        ContentValues data = new ContentValues();
        data.put("name", transaction.name);
        data.put("amount", transaction.amount);
//        data.put("date", SQLiteDate(transaction.date));
        data.put("categoryId", transaction.categoryId);
        data.put("description", transaction.description);
        updateFromData(TRANSACTION_TABLE, data, id);
    }

    public void deleteCategory(long id) {
        deleteById(CATEGORY_TABLE, id);
    }
    public void deleteTransaction(long id) {
        deleteById(TRANSACTION_TABLE, id);
    }

    public CategoryData getCategoryFromId(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CATEGORY_TABLE,
                new String[] { "id", "name", "type", "value" },
                "id = ?",
                new String[] { String.valueOf(id)},
                null,
                null,
                null,
                null
        );
        if(cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") CategoryData category = new CategoryData(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("name")),
                cursor.getString(cursor.getColumnIndex("type")),
                cursor.getDouble(cursor.getColumnIndex("value"))
            );
            return category;
        }
        return null;
    }

    public TransactionData getTransactionFromId(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TRANSACTION_TABLE,
//                new String[] { "id", "name", "type", "value" },
//                "id = ?",
//                new String[] { String.valueOf(id)},
//                null,
//                null,
//                null,
//                null
//        );
//        if(cursor != null && cursor.moveToFirst()) {
//            @SuppressLint("Range") CategoryData category = new CategoryData(
//                    cursor.getLong(cursor.getColumnIndex("id")),
//                    cursor.getString(cursor.getColumnIndex("name")),
//                    cursor.getString(cursor.getColumnIndex("type")),
//                    cursor.getDouble(cursor.getColumnIndex("value"))
//            );
//            return category;
//        }
        return null;
    }

    public List<CategoryData> getAllCategories() {
        List<CategoryData> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CATEGORY_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") CategoryData category = new CategoryData(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("type")),
                        cursor.getDouble(cursor.getColumnIndex("value"))
                );
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

}
