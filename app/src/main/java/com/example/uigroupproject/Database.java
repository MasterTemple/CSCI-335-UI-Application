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
    private static final String NAME = "test_db_0x02";
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
                "date TEXT," +
                "categoryId INTEGER," +
                "description TEXT" +
                ")";
        db.execSQL(createCategoriesTable);
        db.execSQL(createTransactionsTable);
//        createCategory(new CategoryData((long)-1, "No Category", "", -1.0));
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
        data.put("date", transaction.dateToString());
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
        data.put("date", transaction.dateToString());
        data.put("categoryId", transaction.categoryId);
        data.put("description", transaction.description);
        updateFromData(TRANSACTION_TABLE, data, id);
    }

    private void removeDeletedCategoryFromTransactions(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TRANSACTION_TABLE + " SET categoryId = -1 WHERE categoryId = ?", new String[] {String.valueOf(id)});
    }


    public void deleteCategory(long id) {
        deleteById(CATEGORY_TABLE, id);
        removeDeletedCategoryFromTransactions(id);
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
        String sql = "SELECT " + TRANSACTION_TABLE + ".*, " + CATEGORY_TABLE + ".name as categoryName FROM "
                + TRANSACTION_TABLE + ", " + CATEGORY_TABLE
                + " WHERE (" + TRANSACTION_TABLE + ".categoryId = " + CATEGORY_TABLE + ".id"
                + " or " + TRANSACTION_TABLE + ".categoryId = -1"
                + ") and " + TRANSACTION_TABLE + ".id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") TransactionData transaction = new TransactionData(
                    // need to do date stuff here :/
                    cursor.getLong(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("amount")),
                    cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getLong(cursor.getColumnIndex("categoryId")),
                    cursor.getString(cursor.getColumnIndex("categoryName")),
                    cursor.getString(cursor.getColumnIndex("description"))
            );
            return transaction;
        }
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

    public List<TransactionData> getAllTransactions() {
        List<TransactionData> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        String sql = "SELECT " + TRANSACTION_TABLE + ".*, " + CATEGORY_TABLE + ".name as categoryName FROM "
//        + TRANSACTION_TABLE + ", " + CATEGORY_TABLE
//        + " WHERE " + TRANSACTION_TABLE + ".categoryId = " + CATEGORY_TABLE + ".id or " + TRANSACTION_TABLE + ".categoryId = -1";
        String sql = "SELECT " + TRANSACTION_TABLE + ".*, (CASE WHEN " + TRANSACTION_TABLE + ".categoryId = -1 THEN 'No Category' ELSE " + CATEGORY_TABLE + ".name END) as categoryName FROM "
                + TRANSACTION_TABLE + " LEFT JOIN " + CATEGORY_TABLE
                + " ON " + TRANSACTION_TABLE + ".categoryId = " + CATEGORY_TABLE + ".id";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") TransactionData transaction = new TransactionData(
                        // need to do date stuff here :/
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("amount")),
                        cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getLong(cursor.getColumnIndex("categoryId")),
                        cursor.getString(cursor.getColumnIndex("categoryName")),
                        cursor.getString(cursor.getColumnIndex("description"))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public List<TransactionData> getAllTransactionsInPastMonth() {
        List<TransactionData> transactions = getAllTransactions();
        List<TransactionData> transactionsThisMonth = new ArrayList<>();
        Date today = new Date();
        for(TransactionData transaction: transactions) {
            try {
                if(transaction.date.getMonth() == today.getMonth() &&
                transaction.date.getYear() == today.getYear()) {
                    transactionsThisMonth.add(transaction);
                }
            } catch (Exception e) {

            }
        }
        return transactionsThisMonth;
    }

    public void loadSampleData() {
        if(getAllTransactions().size() + getAllCategories().size() != 0)
            return;
        createCategory(new CategoryData("Clothes", "Percent", (double) 10));
        createCategory(new CategoryData("Technology", "Percent", (double) 20));
        createCategory(new CategoryData("Food", "Percent", (double) 30));
        createCategory(new CategoryData("Spotify", "Fixed Value", (double) 10));

        createTransaction(new TransactionData("In-N-Out", (double) 12, "11/01/2023", (long) 3, ""));
        createTransaction(new TransactionData("Spotify Premium", (double) 11, "11/02/2023", (long) 4, ""));
        createTransaction(new TransactionData("Keyboard", (double) 80, "11/04/2023", (long) 2, ""));
        createTransaction(new TransactionData("Caf Swipe", (double) 11.30, "11/05/2023", (long) 3, ""));
        createTransaction(new TransactionData("Athletic Shirt", (double) 7.00, "11/07/2023", (long) 1, ""));
        createTransaction(new TransactionData("Holiness - J.C. Ryle", (double) 18.00, "11/09/2023", (long) -1, ""));
    }
}
