package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionEditActivity extends AppCompatActivity {
    List<CategoryData> categories = new ArrayList<>();
    TransactionData transaction;
    long categoryId = -1;
    EditText inputName;
    EditText inputAmount;
    EditText inputDate;
    AutoCompleteTextView inputCategoryId;
    ArrayAdapter<String> categoryItemsAdapter;
    EditText inputDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        Database db = new Database(this);
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        long transactionId = intent.getLongExtra("transactionId", -1);
        transaction = db.getTransactionFromId(transactionId);

        inputName = findViewById(R.id.transaction_edit_name);
        inputAmount = findViewById(R.id.transaction_edit_amount);
        inputDate = findViewById(R.id.transaction_edit_date);
        inputCategoryId = findViewById(R.id.transaction_edit_category_autocomplete);
        inputDescription = findViewById(R.id.transaction_edit_description);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        String action = isNew ? "Add" : "Edit";
        toolbar.setTitle(String.format("%s a Transaction", action));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<CategoryData> categories = db.getAllCategories();
        ArrayList<String> categoryNames = new ArrayList<>();
        for(CategoryData c: categories)  categoryNames.add(c.name);
        inputCategoryId = findViewById(R.id.transaction_edit_category_autocomplete);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.select_category_item, categoryNames);
//        categoryAdapter.setDropDownViewResource(R.layout.select_category_item);
        inputCategoryId.setAdapter(categoryAdapter);
        inputCategoryId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String item = categoryAdapter.getItem(position).toString();
                String item = parent.getItemAtPosition(position).toString();
                CategoryData category = categories.get((int)id);
                categoryId = category.id;
//                Toast.makeText(TransactionEditActivity.this, item, Toast.LENGTH_SHORT).show();
                Toast.makeText(TransactionEditActivity.this, String.format("%d - %s", category.id, category.name), Toast.LENGTH_SHORT).show();
//                CategoryData category
//                TextInputLayout inputCategoryIdLayout = findViewById(R.id.transaction_edit_category);
//                EditText inputCategoryIdLayoutEditText = inputCategoryIdLayout.getEditText();
//                inputCategoryIdLayoutEditText.setText(item);
//                inputCategoryId.setText(item);
            }
        });
//        inputCategoryId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = categoryAdapter.getItem(position).toString();
////                CategoryData category
////                categoryId = category.id;
//                Toast.makeText(TransactionEditActivity.this, item, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        MenuItem editButton = toolbar.getMenu().findItem(R.id.top_bar_edit_button);
        editButton.setTitle(action);
        editButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                String name = inputName.getText().toString();
                Double amount = Double.parseDouble(inputAmount.getText().toString());
                String date = inputDate.getText().toString();
//                Long categoryId = (long)-1;
//                try {
//                    categoryId = Long.parseLong(inputCategoryId.getText().toString());
//                } catch(Exception e) {}
                String description = inputDescription.getText().toString();
//                if(description == null) description = "";
                TransactionData transaction = new TransactionData(name, amount, date, categoryId, description);
                if(isNew) {
                    db.createTransaction(transaction);
                }
                else {
                    db.updateTransaction(transaction, transactionId);
                }
                finish();
                return true;
            }
        });

        Button deleteButton = findViewById(R.id.transaction_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteTransaction(transactionId);
                finish();
            }
        });
        if(isNew) {
            // hide delete buttons
            deleteButton.setVisibility(View.INVISIBLE);
//            EditText date = findViewById(R.id.transaction_edit_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            inputDate.setText(dateFormat.format(new Date()));
        }
        else {
            // fill fields with previous values
//            inputName = findViewById(R.id.transaction_edit_name);
//            inputAmount = findViewById(R.id.transaction_edit_amount);
//            inputDate = findViewById(R.id.transaction_edit_date);
//            inputCategoryId = findViewById(R.id.transaction_edit_category_autocomplete);
//            inputDescription = findViewById(R.id.transaction_edit_description);

            inputName.setText(transaction.name);
            inputAmount.setText(String.format("%.2f", transaction.amount));
            inputDate.setText(transaction.getFormattedDate());
            inputCategoryId.setText(transaction.categoryName);
            categoryId = transaction.categoryId;
//            inputCategoryId.select
//            if(transaction.categoryId != null)
//                categoryId.setText(transaction.description);
            inputDescription.setText(transaction.description);
        }
    }
}
