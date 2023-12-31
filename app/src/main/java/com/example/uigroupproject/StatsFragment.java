package com.example.uigroupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** @noinspection resource*/
public class StatsFragment extends Fragment {
    private Context context;
    List<TransactionData> transactions;
    List<CategoryData> categories;
    public StatsFragment() {}
    public StatsFragment(Context _context) {
        context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        Database db = new Database(context);
        transactions = db.getAllTransactionsInPastMonth();
        categories = db.getAllCategories();

        ArrayList<PieEntry> entries = new ArrayList<>();
        Map<Long, Double> spendingByCategory = new HashMap<>();
        Map<Long, CategoryData> categoriesById = new HashMap<>();
        Map<Integer, Double> spendingOnDay = new HashMap<>();
        for(int i=1;i<31;i++) {
            spendingOnDay.put(i, 0.0);
        }
        // initialize map
        for(CategoryData category: categories) {
            spendingByCategory.put(category.id, 0.0);
            categoriesById.put(category.id, category);
        }
        spendingByCategory.put((long)-1, 0.0);
        for(TransactionData transaction: transactions) {
//            if (spendingByCategory.containsKey(transaction.categoryId))
//                try {
            spendingByCategory.put(transaction.categoryId, Objects.requireNonNull(spendingByCategory.get(transaction.categoryId)) + transaction.amount);
//                } catch (NullPointerException ignore){}
            int day = transaction.date.getDate();
//            if (spendingOnDay.containsKey(day))
            spendingOnDay.put(day, Objects.requireNonNull(spendingOnDay.get(day)) + transaction.amount);
        }
        for(Map.Entry<Long, Double> entry: spendingByCategory.entrySet()) {
            Long categoryId = entry.getKey();
            Double amount = entry.getValue();
            String NO_CATEGORY = "No Category";
            if(!(amount > 0.0))
                continue;

            if(categoryId == -1){
                entries.add(new PieEntry(Float.parseFloat(amount.toString()), NO_CATEGORY));
            } else {
                CategoryData category = Objects.requireNonNull(categoriesById.get(categoryId));
                entries.add(new PieEntry(Float.parseFloat(amount.toString()), category.name));
            }
        }
        if(entries.size() == 0)
            entries.add(new PieEntry((float)1.0, "Nothing"));


        List<CategoryData> categoriesActual = new ArrayList<>();
        for(CategoryData c: categories) {
            // hack so i dont have to redo it
            categoriesActual.add(new CategoryData(c.id, c.name, context.getString(R.string.category_type_fixed_value), Objects.requireNonNull(spendingByCategory.get(c.id))));
        }
//        double noCategoryMoney = spendingByCategory.get((long)-1);
        CategoryData noCategory = new CategoryData(-1, "No Category", context.getString(R.string.category_type_fixed_value), Objects.requireNonNull(spendingByCategory.get((long)-1)));
//        CategoryData noCategory = new CategoryData(-1, "No Category", context.getString(R.string.category_type_fixed_value), spendingByCategory.get(-1));
        categoriesActual.add(noCategory);
        categoriesActual.sort(Comparator.comparingDouble(c -> -1*c.value));

        TabLayout byCategoryTabLayout = view.findViewById(R.id.spending_by_category_pie_chart_tab_layout);
        setSpendingByCategoryFragment(new SpendingByCategoryPieChartFragment(entries));

        byCategoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedName = Objects.requireNonNull(tab.getText()).toString();
                if(selectedName.equals("Pie Chart")) {
                    setSpendingByCategoryFragment(new SpendingByCategoryPieChartFragment(entries));
                }
                else {
                    setSpendingByCategoryFragment(new SpendingByCategoryTableFragment(context, categoriesActual));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TabLayout amountRemainingTabLayout = view.findViewById(R.id.spending_line_graph_tab_layout);
        setAmountRemainingFragment(new AmountRemainingLineChartFragment(context, transactions));
        amountRemainingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedName = Objects.requireNonNull(tab.getText()).toString();
//                Toast.makeText(context, selectedName, Toast.LENGTH_SHORT).show();
                if(selectedName.equals("Line Graph")) {
                    setAmountRemainingFragment(new AmountRemainingLineChartFragment(context, transactions));
                }
                else {
                    setAmountRemainingFragment(new AmountRemainingTableFragment(context, spendingOnDay));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void setSpendingByCategoryFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.pie_view, fragment);
        fragmentTransaction.commit();
    }

    private void setAmountRemainingFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.line_view, fragment);
        fragmentTransaction.commit();
    }

}
