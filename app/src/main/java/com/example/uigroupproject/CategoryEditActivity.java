package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class CategoryEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        Database db = new Database(this);
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        long transactionId  = intent.getLongExtra("transactionId", -1);
//        Button createButton = findViewById(R.id.category_create_button);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        String action = isNew ? "Create" : "Edit";
        toolbar.setTitle(String.format("%s a Category", action));
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
                EditText name = findViewById(R.id.category_edit_name);
                EditText type = findViewById(R.id.category_edit_proportion_type);
                EditText value = findViewById(R.id.category_edit_proportion_value);
                CategoryData category = new CategoryData(
                        name.getText().toString(),
                        type.getText().toString(),
                        Double.parseDouble(value.getText().toString())
                );
                if(isNew) {
                    db.createCategory(category);
                }
                else {
                    db.updateCategory(category, transactionId);
                }
                finish();
                return true;
            }
        });
        Button deleteButton = findViewById(R.id.category_edit_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteCategory(transactionId);
                finish();
            }
        });
        if(isNew) {
            // hide delete buttons
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            // fill fields with previous values
            EditText name = findViewById(R.id.category_edit_name);
            EditText type = findViewById(R.id.category_edit_proportion_type);
            EditText value = findViewById(R.id.category_edit_proportion_value);
//            CategoryData category = new CategoryData("The Name", "The Type", 20.5);
            CategoryData category = db.getCategoryFromId(transactionId);
            name.setText(category.name);
            type.setText(category.type);
            value.setText(String.format("%.2f", category.value));
        }
    }
}
