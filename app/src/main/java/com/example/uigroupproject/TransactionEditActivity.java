package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        Database db = new Database(this);
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        long transactionId = intent.getLongExtra("transactionId", -1);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        String action = isNew ? "Add" : "Edit";
        toolbar.setTitle(String.format("%s a Transaction", action));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MenuItem editButton = toolbar.getMenu().findItem(R.id.top_bar_edit_button);
        editButton.setTitle(action);
        editButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                EditText inputName = findViewById(R.id.transaction_edit_name);
                EditText inputAmount = findViewById(R.id.transaction_edit_amount);
                EditText inputDate = findViewById(R.id.transaction_edit_date);
                EditText inputCategoryId = findViewById(R.id.transaction_edit_category);
                EditText inputDescription = findViewById(R.id.transaction_edit_description);
                String name = inputName.getText().toString();
                Double amount = Double.parseDouble(inputAmount.getText().toString());
                String date = inputDate.getText().toString();
                Long categoryId = (long)-1;
                try {
                    categoryId = Long.parseLong(inputCategoryId.getText().toString());
                } catch(Exception e) {}
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
            EditText date = findViewById(R.id.transaction_edit_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            date.setText(dateFormat.format(new Date()));
        }
        else {
            // fill fields with previous values
            EditText name = findViewById(R.id.transaction_edit_name);
            EditText amount = findViewById(R.id.transaction_edit_amount);
            EditText date = findViewById(R.id.transaction_edit_date);
            EditText categoryId = findViewById(R.id.transaction_edit_category);
            EditText description = findViewById(R.id.transaction_edit_description);

            TransactionData transaction = db.getTransactionFromId(transactionId);
            name.setText(transaction.name);
            amount.setText(String.format("%.2f", transaction.amount));
            date.setText(transaction.getFormattedDate());
//            if(transaction.categoryId != null)
//                categoryId.setText(transaction.description);
            description.setText(transaction.description);
        }
    }
}
