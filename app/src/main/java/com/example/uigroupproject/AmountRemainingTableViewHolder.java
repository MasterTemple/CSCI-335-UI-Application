package com.example.uigroupproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AmountRemainingTableViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public TextView spentView;
    public TextView remainingView;
    public AmountRemainingTableViewHolder(@NonNull View itemView) {
        super(itemView);
        // ACTUALLY SET THESE
        nameView = itemView.findViewById(R.id.categoryName);
        spentView = itemView.findViewById(R.id.categoryPercent);
        remainingView = itemView.findViewById(R.id.categoryValue);
    }
}
