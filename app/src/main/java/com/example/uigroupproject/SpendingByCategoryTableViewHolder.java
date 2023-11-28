package com.example.uigroupproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpendingByCategoryTableViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public TextView percentView;
    public TextView amountView;
    public SpendingByCategoryTableViewHolder(@NonNull View itemView) {
        super(itemView);
        // ACTUALLY SET THESE
        nameView = itemView.findViewById(R.id.categoryName);
        percentView = itemView.findViewById(R.id.categoryPercent);
        amountView = itemView.findViewById(R.id.categoryValue);
    }
}
