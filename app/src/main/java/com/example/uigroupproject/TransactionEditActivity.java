package com.example.uigroupproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

/** @noinspection resource*/
public class TransactionEditActivity extends AppCompatActivity {
    List<CategoryData> categories = new ArrayList<>();
    TransactionData transaction;
    long categoryId = -1;
    EditText inputName;
    EditText inputAmount;
    EditText inputDate;
    AutoCompleteTextView inputCategoryId;
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

        TextInputLayout inputNameLayout = findViewById(R.id.transaction_edit_name_layout);
        TextInputLayout inputAmountLayout = findViewById(R.id.transaction_edit_amount_layout);
        TextInputLayout inputDateLayout = findViewById(R.id.transaction_edit_date_layout);
        TextInputLayout inputCategoryLayout = findViewById(R.id.transaction_edit_category_layout);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        String action = isNew ? "Add" : "Save";
        toolbar.setTitle(String.format(Locale.US, "%s a Transaction", action));
        toolbar.setNavigationOnClickListener(v -> finish());

        categories = db.getAllCategories();
        ArrayList<String> categoryNames = new ArrayList<>();
        for(CategoryData c: categories)  categoryNames.add(c.name);
        inputCategoryId = findViewById(R.id.transaction_edit_category_autocomplete);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.select_category_item, categoryNames);
//        categoryAdapter.setDropDownViewResource(R.layout.select_category_item);
        inputCategoryId.setAdapter(categoryAdapter);
        inputCategoryId.setOnItemClickListener((parent, view, position, id) -> {
            CategoryData category = categories.get((int)id);
            categoryId = category.id;
        });

        MenuItem editButton = toolbar.getMenu().findItem(R.id.top_bar_edit_button);
        editButton.setTitle(action);
        editButton.setOnMenuItemClickListener(item -> {
            TextInputLayout[] inputLayoutsArray = { inputNameLayout, inputAmountLayout, inputDateLayout, inputCategoryLayout};
            List<TextInputLayout> inputLayouts = Arrays.asList(inputLayoutsArray);
            for(TextInputLayout l: inputLayouts) {
                String value = Objects.requireNonNull(l.getEditText()).getText().toString();
                l.getEditText().setText(value);
            }
            boolean allInputsValid = inputLayouts.stream().allMatch(l -> l.getError() == null);
            if(!allInputsValid) {
                Optional<TextInputLayout> invalidInput = inputLayouts.stream().filter(l -> l.getError() != null).findFirst();
                invalidInput.ifPresent(textInputLayout -> Objects.requireNonNull(textInputLayout.getEditText()).requestFocus());
                return true;
            }

            String amountString = inputAmount.getText().toString();
            double amount = 0.0;
            if(amountString.length() != 0) {
                amount = Double.parseDouble(amountString);
            }
            TransactionData transaction = new TransactionData(
                    inputName.getText().toString(),
                    amount,
                    inputDate.getText().toString(),
                    categoryId,
                    inputDescription.getText().toString()
            );
            if(isNew) {
                db.createTransaction(transaction);
            }
            else {
                db.updateTransaction(transaction, transactionId);
            }
            finish();
            return true;
        });

        Button deleteButton = findViewById(R.id.transaction_delete_button);
        deleteButton.setOnClickListener(v -> {
            AlertDialog alert = new MaterialAlertDialogBuilder(TransactionEditActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure that you want to delete this transaction?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.deleteTransaction(transactionId);
                        finish();
                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).setCancelable(true).create();
            alert.show();
        });

        if(isNew) {
            // hide delete buttons
            deleteButton.setVisibility(View.INVISIBLE);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            inputDate.setText(dateFormat.format(new Date()));
        }
        else {
            // fill fields with previous values
            if(transaction == null) {
                transaction = db.getTransactionFromId(transactionId);
            }
            inputName.setText(transaction.name);
            inputAmount.setText(String.format(Locale.US, "%.2f", transaction.amount));
            inputDate.setText(transaction.getFormattedDate());
            categoryId = transaction.categoryId;
            if(categoryId == -1)
                inputCategoryId.setText("");
            else
                inputCategoryId.setText(transaction.categoryName);
            inputDescription.setText(transaction.description);
        }

        View.OnClickListener onClickOpenCalendar = v -> showCalendarDialog();
        inputDateLayout.setEndIconOnClickListener(onClickOpenCalendar);
        inputDateLayout.setErrorIconOnClickListener(onClickOpenCalendar);

        inputDate.addTextChangedListener(ErrorHandling.checkForValidDate(inputDateLayout));
        inputName.addTextChangedListener(ErrorHandling.checkForNonEmptyField(inputNameLayout));
        inputAmount.addTextChangedListener(ErrorHandling.checkForNonEmptyField(inputAmountLayout));
        inputCategoryId.addTextChangedListener(ErrorHandling.checkWithinList(inputCategoryLayout, categoryNames, true));
    }

    public void showCalendarDialog() {
//        long selection = MaterialDatePicker.todayInUtcMilliseconds();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        long selection;
        try {
            Date today = sdf.parse(inputDate.getText().toString());
            selection = today != null ? today.getTime() : 0;
        } catch (Exception e) {
            selection = MaterialDatePicker.todayInUtcMilliseconds();
        }
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selection)
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(selection1 -> {
            SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            TimeZone timeZone = TimeZone.getDefault();
            int offset = timeZone.getOffset(selection1);
            selection1 -= offset;
            date.setTimeZone(timeZone);
            String dateString = date.format(new Date(selection1));
            inputDate.setText(dateString);
        });
        materialDatePicker.show(getSupportFragmentManager(), "tag");

    }

}
