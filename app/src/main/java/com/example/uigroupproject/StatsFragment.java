package com.example.uigroupproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {
    private Context context;
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
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        ArrayList<PieEntry> entries = new ArrayList<>();
        Database db = new Database(context);
        Settings settings = new Settings(context);
        Double budget = settings.budget;
        List<TransactionData> transactions = db.getAllTransactionsInPastMonth();
        List<CategoryData> categories = db.getAllCategories();
        Map<Long, Double> spendingByCategory = new HashMap<>();
        Map<Long, Double> budgetedByCategory = new HashMap<>();
        Map<Long, CategoryData> categoriesById = new HashMap<>();
        // initialize map
        for(CategoryData category: categories) {
            spendingByCategory.put(category.id, 0.0);
            categoriesById.put(category.id, category);
//            budgetedByCategory.put(category.id, Double.parseDouble(category.getPercent(context)));
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
                entries.add(new PieEntry(Float.valueOf(amount.toString()), NO_CATEGORY));
            } else {
                CategoryData category = categoriesById.get(categoryId);
                entries.add(new PieEntry(Float.valueOf(amount.toString()), category.name));
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
                if(transaction.date.getDay() == i) {
                    amountSpent += transaction.amount;
                }
            }
            lineEntries.add(new Entry(i, (float)(budget - amountSpent)));
            if(today.getDay() == i) break;
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Actual Amount Remaining");
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


        return view;
    }
}
