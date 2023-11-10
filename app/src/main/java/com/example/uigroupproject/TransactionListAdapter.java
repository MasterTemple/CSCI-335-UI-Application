package com.example.uigroupproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListViewHolder> {
    private List<TransactionData> transactions;
    private Context context;
    public TransactionListAdapter(List<TransactionData> recentTransactions, Context _context) {
        transactions = recentTransactions;
        context = _context;
    }

    @Override
    public TransactionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_transaction_element, parent, false);
        return new TransactionListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListViewHolder holder, int position) {
        TransactionData transaction = transactions.get(position);
        holder.amountView.setText(transaction.getFormattedAmount());
        holder.nameView.setText(transaction.getName());
        holder.dateView.setText(transaction.getFormattedDate());
        holder.categoryView.setText(transaction.categoryName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, holder.nameView.getText(), Toast.LENGTH_SHORT).show();
                editTransaction(transaction.id);
            }
        });
    }
    //        Toast.makeText(context, "Item at position " + position + " is tapped", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "please", Toast.LENGTH_SHORT).show();
    public void editTransaction(long transactionId) {
        Intent intent = new Intent(context, TransactionEditActivity.class);
        intent.putExtra("isNew", false);
        intent.putExtra("transactionId", transactionId);
        startActivity(context, intent, new Bundle());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setFilteredList(List<TransactionData> filteredList) {
        transactions = filteredList;
        notifyDataSetChanged();
    }

}
