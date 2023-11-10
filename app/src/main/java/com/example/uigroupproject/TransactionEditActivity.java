package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class TransactionEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        Button deleteButton = findViewById(R.id.transaction_delete_button);
        if(isNew) {
            // hide delete buttons
            deleteButton.setVisibility(View.INVISIBLE);
        }
        else {
            // fill fields with previous values
            // ...
        }
    }
}
