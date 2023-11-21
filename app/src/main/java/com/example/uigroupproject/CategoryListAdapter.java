package com.example.uigroupproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListViewHolder> {
    final private List<CategoryData> categories;
    final private Context context;
    public CategoryListAdapter(List<CategoryData> _categories, Context _context) {
        categories = _categories;
        context = _context;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category_element, parent, false);
        return new CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        CategoryData category = categories.get(position);
        holder.nameView.setText(category.name);
        holder.proportionTypeView.setText(category.getPercent(context));
        holder.proportionValueView.setText(category.getNumber(context));
        holder.itemView.setOnClickListener(view -> editCategory(category.id));
    }
    public void editCategory(long categoryId) {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        intent.putExtra("isNew", false);
        intent.putExtra("categoryId", categoryId);
        startActivity(context, intent, new Bundle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
