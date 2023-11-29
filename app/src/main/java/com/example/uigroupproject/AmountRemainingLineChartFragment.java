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
import java.util.List;

public class AmountRemainingLineChartFragment extends Fragment {
    private Context context;
    private View view;
    List<TransactionData> transactions = new ArrayList<>();
    public AmountRemainingLineChartFragment(Context _context, List<TransactionData> _transactions) {
        context = _context;
        transactions = _transactions;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_amount_remaining_line_chart, container, false);
        Settings settings = new Settings(context);
        double budget = settings.budget;
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

        lineChart.setData(new LineData(lineDataSet2, lineDataSet));

        lineChart.getDescription().setEnabled(false);
        int ms = 700;
        lineChart.animateXY(ms, ms);

        return view;
    }
}
