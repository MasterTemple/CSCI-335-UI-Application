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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class CategoryEditActivity extends AppCompatActivity {
    EditText inputCategoryName;
    AutoCompleteTextView inputCategoryType;
    EditText inputCategoryValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);
        inputCategoryName = findViewById(R.id.category_edit_name);
        inputCategoryType = findViewById(R.id.category_edit_proportion_type_autocomplete);
        inputCategoryValue = findViewById(R.id.category_edit_proportion_value);

        ArrayList<String> categoryTypes = new ArrayList<>();
        categoryTypes.add("Fixed Value");
        categoryTypes.add("Percent");
        categoryTypes.add("None");
        ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<>(this, R.layout.select_category_item, categoryTypes);
        inputCategoryType.setAdapter(categoryTypeAdapter);
        inputCategoryType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

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
//                EditText name = findViewById(R.id.category_edit_name);
//                EditText type = findViewById(R.id.category_edit_proportion_type);
//                EditText value = findViewById(R.id.category_edit_proportion_value);
                CategoryData category = new CategoryData(
                        inputCategoryName.getText().toString(),
                        inputCategoryType.getText().toString(),
                        Double.parseDouble(inputCategoryValue.getText().toString())
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
//            inputCategoryName = findViewById(R.id.category_edit_name);
//            inputCategoryType = findViewById(R.id.category_edit_proportion_type);
//            inputCategoryValue = findViewById(R.id.category_edit_proportion_value);

            CategoryData category = db.getCategoryFromId(categoryId);
            inputCategoryName.setText(category.name);
            inputCategoryType.setText(category.type, false);
//            inputCategoryType.setThreshold(1);
            inputCategoryValue.setText(String.format("%.2f", category.value));
        }
    }
}
