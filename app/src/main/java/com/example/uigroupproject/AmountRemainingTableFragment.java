package com.example.uigroupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AmountRemainingTableFragment extends Fragment {
    private final Context context;
    List<PurchaseDateData> days;
    @SuppressWarnings("deprecation")
    public AmountRemainingTableFragment(Context _context, Map<Integer, Double> spendingByDay) {
        context = _context;
        double budgetRemaining = new Settings(context).budget;
        List<PurchaseDateData> _days= new ArrayList<>();
        Map<Integer, String> months = new HashMap<>();
        months.put(1, "Jan");
        months.put(2, "Feb");
        months.put(3, "Mar");
        months.put(4, "Apr");
        months.put(5, "May");
        months.put(6, "Jun");
        months.put(7, "Jul");
        months.put(8, "Aug");
        months.put(9, "Sep");
        months.put(10, "Oct");
        months.put(11, "Nov");
        months.put(12, "Dec");

        String monthName = months.get(new Date().getMonth() + 1);
        for(Map.Entry<Integer, Double> each: spendingByDay.entrySet()) {
            if(each.getValue() == 0)
                continue;
            budgetRemaining -= each.getValue();
            _days.add(new PurchaseDateData(String.format(Locale.US, "%s %s", monthName, each.getKey()), each.getValue(), budgetRemaining));
        }
        days = _days;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amount_remaining_table, container, false);
        AmountRemainingTableAdapter adapter = new AmountRemainingTableAdapter(days);
        RecyclerView recyclerView = view.findViewById(R.id.spending_by_category_table);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }
}
