package com.example.uigroupproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView proportionTypeView;
        public TextView proportionValueView;
        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.categoryName);
            proportionTypeView = itemView.findViewById(R.id.categoryPercent);
            proportionValueView = itemView.findViewById(R.id.categoryValue);
        }
    }
