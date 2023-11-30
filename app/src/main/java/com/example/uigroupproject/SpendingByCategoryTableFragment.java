package com.example.uigroupproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpendingByCategoryTableFragment extends Fragment {
    private final Context context;
    List<CategoryData> categories;
    public SpendingByCategoryTableFragment(Context _context, List<CategoryData> _categories) {
        context = _context;
        categories = _categories;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spending_by_category_table, container, false);
        SpendingByCategoryTableAdapter adapter = new SpendingByCategoryTableAdapter(categories, context);
        RecyclerView recyclerView = view.findViewById(R.id.spending_by_category_table);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }
}
