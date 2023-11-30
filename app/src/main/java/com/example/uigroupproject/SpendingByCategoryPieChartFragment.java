package com.example.uigroupproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SpendingByCategoryPieChartFragment extends Fragment {
    ArrayList<PieEntry> entries;
    public SpendingByCategoryPieChartFragment(ArrayList<PieEntry> _entries) {
        entries = _entries;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spending_by_category_pie_chart, container, false);
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
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);

        return view;
    }
}
