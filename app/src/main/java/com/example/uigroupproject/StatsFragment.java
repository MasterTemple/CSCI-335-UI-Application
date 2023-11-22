package com.example.uigroupproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatsFragment extends Fragment {
    private Context context;
    private View view;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.spending_by_category_list);
        Database db = new Database(context);
        transactions = db.getAllTransactionsInPastMonth();
        categories = db.getAllCategories();

        ArrayList<PieEntry> entries = new ArrayList<>();
        Settings settings = new Settings(context);
        double budget = settings.budget;
        Map<Long, Double> spendingByCategory = new HashMap<>();
        Map<Long, CategoryData> categoriesById = new HashMap<>();
        // initialize map
        for(CategoryData category: categories) {
            spendingByCategory.put(category.id, 0.0);
            categoriesById.put(category.id, category);
        }
        spendingByCategory.put((long)-1, 0.0);
        for(TransactionData transaction: transactions) {
            spendingByCategory.put(transaction.categoryId, spendingByCategory.get(transaction.categoryId) + transaction.amount);
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
                CategoryData category = categoriesById.get(categoryId);
                entries.add(new PieEntry(Float.parseFloat(amount.toString()), category.name));
            }

        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
//        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieChart pieChart = view.findViewById(R.id.spending_by_category_pie_chart);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieData.setValueTextSize(12);
//        pieData.setValueTextColor(Color.rgb(255, 255, 255)); // white
        pieData.setValueTextColor(0); // invisible
        pieDataSet.setLabel("");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);

        LineChart lineChart = view.findViewById(R.id.spending_line_graph);
        int daysThisMonth = 31;

        List<Entry> lineEntries = new ArrayList<>();
        double amountSpent = 0;
        Date today = new Date();
        for(int i=0;i<=daysThisMonth;i++) {
            for(TransactionData transaction: transactions) {
                if(transaction.date.getDate() == i) {
                    amountSpent += transaction.amount;
                }
            }
            lineEntries.add(new Entry(i, (float)(budget - amountSpent)));
            if(today.getDate() == i) break;
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Actual Dollars Remaining");
        lineDataSet.setColor(Color.rgb(0, 255, 0));
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(2);

        List<Entry> lineEntries2 = new ArrayList<>();
        double dailyBudget = budget/daysThisMonth;
        for(int i=0;i<=daysThisMonth;i++) {
            lineEntries2.add(new Entry(i, (float)(budget - (i*dailyBudget))));
        }
        LineDataSet lineDataSet2 = new LineDataSet(lineEntries2, "Expected Amount Remaining");
        lineDataSet2.setColor(Color.rgb(0, 0, 255));
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setLineWidth(2);

        lineChart.setData(new LineData(lineDataSet, lineDataSet2));

        lineChart.getDescription().setEnabled(false);
        int ms = 700;
        lineChart.animateXY(ms, ms);

        List<CategoryData> categoriesActual = new ArrayList<>();
        for(CategoryData c: categories) {
            // hack so i dont have to redo it
            categoriesActual.add(new CategoryData(c.id, c.name, "Fixed Value", spendingByCategory.get(c.id)));
        }
//        double noCategoryMoney = spendingByCategory.get((long)-1);
        CategoryData noCategory = new CategoryData(-1, "No Category", "Fixed Value", spendingByCategory.get((long)-1));
//        CategoryData noCategory = new CategoryData(-1, "No Category", "Fixed Value", spendingByCategory.get(-1));
        categoriesActual.add(noCategory);
        categoriesActual.sort(Comparator.comparingDouble(c -> -1*c.value));
        CategoryListAdapter adapter = new CategoryListAdapter(categoriesActual, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        TabLayout byCategoryTabLayout = view.findViewById(R.id.spending_by_category_pie_chart_tab_layout);
        view.findViewById(R.id.spending_by_category_pie_chart).setVisibility(View.VISIBLE);
        view.findViewById(R.id.spending_by_category_list).setVisibility(View.INVISIBLE);
        byCategoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedName = Objects.requireNonNull(tab.getText()).toString();
                Toast.makeText(context, selectedName, Toast.LENGTH_SHORT).show();
                if(selectedName.equals("Pie Chart")) {
                    view.findViewById(R.id.spending_by_category_pie_chart).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.spending_by_category_list).setVisibility(View.INVISIBLE);
                }
                else {
                    view.findViewById(R.id.spending_by_category_pie_chart).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.spending_by_category_list).setVisibility(View.VISIBLE);
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
}
