package com.example.uigroupproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionListViewHolder  extends RecyclerView.ViewHolder {
        public TextView amountView;
        public TextView nameView;
        public TextView dateView;
        public TextView categoryView;
        public TransactionListViewHolder(@NonNull View itemView) {
            super(itemView);
            amountView = itemView.findViewById(R.id.transactionAmount);
            nameView = itemView.findViewById(R.id.transactionName);
            dateView = itemView.findViewById(R.id.transactionDate);
            categoryView = itemView.findViewById(R.id.transactionCategory);
        }
    }
