package com.example.uigroupproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Database extends SQLiteOpenHelper {
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

    private void createFromData(String table, ContentValues data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(table, null, data);
    }

    private void updateFromData(String table, ContentValues data, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(table, data, "id = ?", new String[] {String.valueOf(id)});
    }

    public void deleteById(String table, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, "id = ?", new String[] {String.valueOf(id)});
    }

    public void createCategory(CategoryData category) {
        ContentValues data = new ContentValues();
        data.put("name", category.name);
        data.put("type", category.type);
        data.put("value", category.value);
        createFromData(CATEGORY_TABLE, data);
    }

    public void createTransaction(TransactionData transaction) {
        ContentValues data = new ContentValues();
        data.put("name", transaction.name);
        data.put("amount", transaction.amount);
        data.put("date", transaction.dateToString());
        data.put("categoryId", transaction.categoryId);
        data.put("description", transaction.description);
        createFromData(TRANSACTION_TABLE, data);
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
        if(cursor != null)
            cursor.close();
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
        if(cursor != null)
            cursor.close();
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
        Comparator<TransactionData> transactionSorter = Comparator.comparing(t -> t.date);
        transactions.sort(transactionSorter);
        return transactions;
    }

    @SuppressWarnings("deprecation")
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
            } catch (Exception ignored) { }
        }
        return transactionsThisMonth;
    }

    public void clearTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TRANSACTION_TABLE);
    }

    public void clearCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CATEGORY_TABLE);
    }

    public void clearDatabase() {
        clearTransactions();
        clearCategories();
    }

    public void loadSampleData(Context context) {
        if(getAllTransactions().size() + getAllCategories().size() != 0)
            return;
        createCategory(new CategoryData("Clothes", context.getString(R.string.category_type_percent), 10));
        createCategory(new CategoryData("Technology", context.getString(R.string.category_type_percent), 20));
        createCategory(new CategoryData("Food", context.getString(R.string.category_type_percent), 30));
        createCategory(new CategoryData("Spotify", "Fixed Value", 10));

        List<CategoryData> categories = getAllCategories();
        Map<String, CategoryData> categoryDataMap = new HashMap<>();
        for(CategoryData c: categories) categoryDataMap.put(c.name, c);


        createTransaction(new TransactionData("Spotify Premium", 10.00, "01/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "02/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "3/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "4/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("iPhone Charger", 15.00, "4/17/2023", Objects.requireNonNull(categoryDataMap.get("Technology")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "5/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "6/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 10.00, "7/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 11.00, "8/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 11.00, "9/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 11.00, "10/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 11.00, "11/01/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));

        createTransaction(new TransactionData("In-N-Out", 12.00, "12/01/2023", Objects.requireNonNull(categoryDataMap.get("Food")).id, ""));
        createTransaction(new TransactionData("Spotify Premium", 11.00, "12/02/2023", Objects.requireNonNull(categoryDataMap.get("Spotify")).id, ""));
        createTransaction(new TransactionData("Keyboard", 80.00, "12/03/2023", Objects.requireNonNull(categoryDataMap.get("Technology")).id, ""));
        createTransaction(new TransactionData("Caf Swipe", 11.30, "12/03/2023", Objects.requireNonNull(categoryDataMap.get("Food")).id, ""));
        createTransaction(new TransactionData("Athletic Shirt", 7.00, "12/04/2023", Objects.requireNonNull(categoryDataMap.get("Clothes")).id, ""));
        createTransaction(new TransactionData("Holiness - J.C. Ryle", 18.00, "12/05/2023", -1, ""));
    }
}
