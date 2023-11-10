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
    public BudgetFragment() {}
    public BudgetFragment(Context _context) {
        context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        categories.add(new CategoryData("Food", "Percent", 10));
//        categories.add(new CategoryData("Peter", "Fixed Value", 40));
//        categories.add(new CategoryData("Water", "Percent", 30));
//        categories.add(new CategoryData("Help", "Percent", 40));
        db = new Database(context);
        categories = db.getAllCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        adapter = new CategoryListAdapter(categories, context);
        recyclerView = view.findViewById(R.id.category_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        TextView budgetDisplay = view.findViewById(R.id.monthly_budget_value);
        budgetDisplay.setText(String.format("$%.2f", new Settings(context).budget));
        Button addCategoryButton = view.findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        Button editBudgetButton = view.findViewById(R.id.edit_budget_button);
        editBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_budget, null);
                AlertDialog alert = new MaterialAlertDialogBuilder(context)
                        .setTitle("Enter Budget")
                        .setView(dialogView)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                TextView budgetDisplay = view.findViewById(R.id.monthly_budget_value);
                                EditText newBudget = dialogView.findViewById(R.id.dialog_edit_budget_text);
                                Settings settings = new Settings(context);
//                                settings.budget = Double.parseDouble(newBudget.getText().toString());
                                settings.setBudget(Double.parseDouble(newBudget.getText().toString()));
                                budgetDisplay.setText(String.format("$%.2f", settings.budget));
                                updateCategoryList();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alert.show();
            }
        });
        return view;
    }
    private void addCategory() {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
    }

    private void updateCategoryList() {
        categories = db.getAllCategories();
        adapter = new CategoryListAdapter(categories, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCategoryList();
    }
}
