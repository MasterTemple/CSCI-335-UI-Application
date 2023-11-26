package com.example.uigroupproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpendingByCategoryTableAdapter extends RecyclerView.Adapter<SpendingByCategoryTableViewHolder> {

    final private List<CategoryData> categories;
    final private Context context;
    public SpendingByCategoryTableAdapter(List<CategoryData> _categories, Context _context) {
        categories = _categories;
        context = _context;
    }

    @NonNull
    @Override
    public SpendingByCategoryTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category_element, parent, false);
        return new SpendingByCategoryTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingByCategoryTableViewHolder holder, int position) {
        CategoryData category = categories.get(position);
        holder.nameView.setText(category.name);
        holder.percentView.setText(category.getPercent(context));
        holder.amountView.setText(category.getNumber(context));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
