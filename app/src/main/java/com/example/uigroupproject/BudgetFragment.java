package com.example.uigroupproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {
    private List<CategoryData> categories = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private Database db;
    private View view;
    private double totalPercent;
    private double totalAmount;
    public BudgetFragment() {}
    public BudgetFragment(Context _context) {
        context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(context);
        categories = db.getAllCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_budget, container, false);
        recyclerView = view.findViewById(R.id.category_list);

        updateCategoryTotals();
        updateCategoryList();

        TextView budgetDisplay = view.findViewById(R.id.monthly_budget_value);
        budgetDisplay.setText(String.format(Locale.US, "$%.2f", new Settings(context).budget));
        Button addCategoryButton = view.findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(v -> addCategory());

        Button editBudgetButton = view.findViewById(R.id.edit_budget_button);
        editBudgetButton.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_budget, null);
            AlertDialog alert = new MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle("Enter Budget")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        EditText newBudgetDisplay = dialogView.findViewById(R.id.dialog_edit_budget_text);
                        double newBudget = Double.parseDouble(newBudgetDisplay.getText().toString());
                        // invalid budget
                        if(newBudget < totalAmount) {
                            AlertDialog confirmAlert = new MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setTitle("Budget Lower Than Current Categories")
                                    .setMessage(String.format(Locale.US, "Your categories sum up to $%.2f but your new budget is only $%.2f", totalAmount, newBudget))
                                    .setPositiveButton("Continue", (confirmDialog, cwhich) -> updateBudget(newBudget))
                                    .setNegativeButton("Cancel", (confirmDialog, cwhich) -> confirmDialog.dismiss()).setCancelable(true).create();
                            confirmAlert.show();
                        } else {
                            updateBudget(newBudget);
                        }
                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
            alert.show();
        });
        return view;
    }
    private void addCategory() {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        intent.putExtra("isNew", true);
        intent.putExtra("totalPercent", totalPercent);
        intent.putExtra("totalAmount", totalAmount);
        startActivity(intent);
    }

    private void updateCategoryList() {
        CategoryListAdapter adapter = new CategoryListAdapter(categories, context, totalPercent, totalAmount);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    private void updateCategoryTotals() {
        totalPercent = 0;
        totalAmount = 0;
        for (CategoryData category : categories) {
            totalPercent += Double.parseDouble(category.getPercent(context).replace("%", ""));
            totalAmount += Double.parseDouble(category.getNumber(context).replace("$", ""));
        }
        TextView totalPercentView = view.findViewById(R.id.categoryPercentTotal);
        TextView totalAmountView = view.findViewById(R.id.categoryValueTotal);
        totalPercentView.setText(String.format(Locale.US, "%.0f%%", totalPercent));
        totalAmountView.setText(String.format(Locale.US, "$%.2f", totalAmount));
    }

    @Override
    public void onResume() {
        super.onResume();
        categories = db.getAllCategories();
        updateCategoryTotals();
        updateCategoryList();
    }

    private void updateBudget(double newBudget) {
        TextView budgetDisplay = view.findViewById(R.id.monthly_budget_value);
        Settings settings = new Settings(context);
        settings.setBudget(newBudget);
        budgetDisplay.setText(String.format(Locale.US, "$%.2f", settings.budget));
        updateCategoryList();
        updateCategoryTotals();
    }
}
