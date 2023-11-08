package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        Button createButton = findViewById(R.id.category_create_button);
        RelativeLayout editButtonsContainer = findViewById(R.id.category_edit_buttons_container);
        if(isNew) {
            // hide delete/edit buttons
            editButtonsContainer.setVisibility(View.INVISIBLE);
            // fill fields with previous values
            // ...
        } else {
            // hide add button
            createButton.setVisibility(View.INVISIBLE);
        }
    }
}
