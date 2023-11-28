package com.example.uigroupproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment {
    private List<CategoryData> categories = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;
    private Context context;
    private Database db;
    private View view;
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

        adapter = new CategoryListAdapter(categories, context);
        recyclerView = view.findViewById(R.id.category_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        TextView budgetDisplay = view.findViewById(R.id.monthly_budget_value);
        budgetDisplay.setText(String.format("$%.2f", new Settings(context).budget));
        Button addCategoryButton = view.findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(v -> addCategory());

        Button editBudgetButton = view.findViewById(R.id.edit_budget_button);
        editBudgetButton.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_budget, null);
            AlertDialog alert = new MaterialAlertDialogBuilder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle("Enter Budget")
                    .setView(dialogView)
                    .setPositiveButton("Set", (dialog, which) -> {
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        TextView budgetDisplay1 = view.findViewById(R.id.monthly_budget_value);
                        EditText newBudget = dialogView.findViewById(R.id.dialog_edit_budget_text);
                        Settings settings = new Settings(context);
//                                settings.budget = Double.parseDouble(newBudget.getText().toString());
                        settings.setBudget(Double.parseDouble(newBudget.getText().toString()));
                        budgetDisplay1.setText(String.format("$%.2f", settings.budget));
                        updateCategoryList();
                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
            alert.show();
        });
        return view;
    }
    private void addCategory() {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
    }

    private void updateCategoryList() {
        adapter = new CategoryListAdapter(categories, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    private void updateCategoryTotals() {
        double totalPercent = 0;
        double totalAmount = 0;
        for (CategoryData category : categories) {
            totalPercent += Double.parseDouble(category.getPercent(context).replace("%", ""));
            totalAmount += Double.parseDouble(category.getNumber(context).replace("$", ""));
        }
        TextView totalPercentView = view.findViewById(R.id.categoryPercentTotal);
        TextView totalAmountView = view.findViewById(R.id.categoryValueTotal);
        totalPercentView.setText(String.format("%.0f%%", totalPercent));
        totalAmountView.setText(String.format("$%.2f", totalAmount));
    }

    @Override
    public void onResume() {
        super.onResume();
        categories = db.getAllCategories();
        updateCategoryList();
        updateCategoryTotals();
    }
}
