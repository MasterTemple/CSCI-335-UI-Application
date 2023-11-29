package com.example.uigroupproject;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoryEditActivity extends AppCompatActivity {
    EditText inputCategoryName;
    AutoCompleteTextView inputCategoryType;
    EditText inputCategoryValue;
    TextInputLayout inputCategoryNameLayout;
    TextInputLayout inputCategoryTypeLayout;
    TextInputLayout inputCategoryValueLayout;
    CategoryData category;
    private double totalPercent;
    private double totalAmount;
    private double newTotalPercent;
    private double newTotalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);
        inputCategoryName = findViewById(R.id.category_edit_name);
        inputCategoryType = findViewById(R.id.category_edit_proportion_type_autocomplete);
        inputCategoryValue = findViewById(R.id.category_edit_proportion_value);

        inputCategoryNameLayout = findViewById(R.id.category_edit_name_layout);
        inputCategoryTypeLayout = findViewById(R.id.category_edit_proportion_type_layout);
        inputCategoryValueLayout = findViewById(R.id.category_edit_proportion_value_layout);

        ArrayList<String> categoryTypes = new ArrayList<>();
        categoryTypes.add("Fixed Value");
        categoryTypes.add("Percent");
        categoryTypes.add("None");
        ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<>(this, R.layout.select_category_item, categoryTypes);
        inputCategoryType.setAdapter(categoryTypeAdapter);
        inputCategoryType.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            if(item.equals("Fixed Value")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_money);
            if(item.equals("Percent")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_percent);
            if(item.equals("None")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_no_money);
            if(item.equals("None")) {
                inputCategoryValue.setEnabled(false);
                inputCategoryValue.setText("0");
                inputCategoryValueLayout.setError(null);
                inputCategoryValueLayout.setHelperText(null);
            }
            else {
                inputCategoryValue.setEnabled(true);
                inputCategoryValue.setText("");
                inputCategoryValueLayout.setError(null);
                inputCategoryValueLayout.setHelperText(getResources().getString(R.string.text_input_helper_text_required));
            }
        });

        Database db = new Database(this);
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", true);
        long categoryId  = intent.getLongExtra("categoryId", -1);
        totalPercent = intent.getDoubleExtra("totalPercent", 0);
        totalAmount = intent.getDoubleExtra("totalAmount", 0);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        String action = isNew ? "Create" : "Save";
        toolbar.setTitle(String.format("%s a Category", action));
        toolbar.setNavigationOnClickListener(v -> finish());
        MenuItem editButton = toolbar.getMenu().findItem(R.id.top_bar_edit_button);
        editButton.setTitle(action);
        editButton.setOnMenuItemClickListener(item -> {
            TextInputLayout[] inputLayoutsArray = { inputCategoryNameLayout, inputCategoryTypeLayout, inputCategoryValueLayout };
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

            String inputValue = inputCategoryValue.getText().toString();
            if(inputValue.length() == 0) inputValue = "0";
            CategoryData category = new CategoryData(
                    inputCategoryName.getText().toString(),
                    inputCategoryType.getText().toString(),
                    Double.parseDouble(inputValue)
            );

            CategoryData tempCategory = new CategoryData("___temp", inputCategoryType.getText().toString(), Double.parseDouble(inputCategoryValue.getText().toString()));
            double thisPercent = tempCategory.getPercentDouble(this);
            double thisAmount = tempCategory.getNumberDouble(this);
            newTotalPercent = totalPercent + thisPercent;
            newTotalAmount = totalAmount + thisAmount;
            boolean percentOver100 = newTotalPercent > 100;
            boolean newBudgetOverTotalBudget = newTotalAmount > new Settings(this).budget;
            boolean isOverBudget = percentOver100 || newBudgetOverTotalBudget;

            AlertDialog alert = new MaterialAlertDialogBuilder(CategoryEditActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle("Over Budget")
                    .setMessage(String.format("This will set your budget to $%.2f which is %.0f%% of $%.2f. Do you want to continue?",
                    newTotalAmount, newTotalPercent, new Settings(CategoryEditActivity.this).budget))
                    .setPositiveButton("Continue", (dialog, which) -> {
                        if(isNew) {
                            db.createCategory(category);
                        }
                        else {
                            db.updateCategory(category, categoryId);
                        }
                        finish();
                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).setCancelable(true).create();

            if(isOverBudget)
                alert.show();
            else {
                if(isNew) {
                    db.createCategory(category);
                }
                else {
                    db.updateCategory(category, categoryId);
                }
                finish();
            }
            return true;
        });
        Button deleteButton = findViewById(R.id.category_edit_delete_button);
        deleteButton.setOnClickListener(v -> {
            AlertDialog alert = new MaterialAlertDialogBuilder(CategoryEditActivity.this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle("Confirm Deletion")
                    .setMessage(String.format("Are you sure that you want to delete the \"%s\" category?", category.name))
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.deleteCategory(categoryId);
                        finish();
                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).setCancelable(true).create();
            alert.show();
        });

        if(isNew) {
            // hide delete buttons
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            // fill fields with previous values
            category = db.getCategoryFromId(categoryId);
//            totalPercent += Double.parseDouble(category.getPercent(this).replace("%", ""));
//            totalAmount += Double.parseDouble(category.getNumber(this).replace("$", ""));
            totalPercent -= category.getPercentDouble(this);
            totalAmount -= category.getNumberDouble(this);
            inputCategoryName.setText(category.name);
            inputCategoryType.setText(category.type, false);
            inputCategoryValue.setText(String.format("%.2f", category.value));
            if(category.type.equals("Fixed Value")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_money);
            if(category.type.equals("Percent")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_percent);
            if(category.type.equals("None")) inputCategoryValueLayout.setStartIconDrawable(R.drawable.ic_no_money);
            if(category.type.equals("None")) {
                inputCategoryValue.setEnabled(false);
                inputCategoryValue.setText("0");
                inputCategoryValueLayout.setError(null);
                inputCategoryValueLayout.setHelperText(null);
            }
            else {
                inputCategoryValue.setEnabled(true);
                inputCategoryValue.setText("");
                inputCategoryValueLayout.setError(null);
                inputCategoryValueLayout.setHelperText(getResources().getString(R.string.text_input_helper_text_required));
            }
        }
        // listen for input to validate
        inputCategoryName.addTextChangedListener(ErrorHandling.checkForNonEmptyField(inputCategoryNameLayout));
        inputCategoryType.addTextChangedListener(ErrorHandling.checkWithinList(inputCategoryTypeLayout, categoryTypes, false));
        inputCategoryValue.addTextChangedListener(ErrorHandling.checkForNonNegativeField(inputCategoryValueLayout));
    }
}
