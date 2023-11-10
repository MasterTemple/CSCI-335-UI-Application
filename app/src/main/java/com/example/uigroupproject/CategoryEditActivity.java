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
        long categoryId  = intent.getLongExtra("categoryId", -1);

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
                    db.updateCategory(category, categoryId);
                }
                finish();
                return true;
            }
        });
        Button deleteButton = findViewById(R.id.category_edit_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteCategory(categoryId);
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

            CategoryData category = db.getCategoryFromId(categoryId);
            name.setText(category.name);
            type.setText(category.type);
            value.setText(String.format("%.2f", category.value));
        }
    }
}
