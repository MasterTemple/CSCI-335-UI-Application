package com.example.uigroupproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AmountRemainingTableAdapter extends RecyclerView.Adapter<AmountRemainingTableViewHolder> {

    final private List<PurchaseDateData> days;
    final private Context context;
    public AmountRemainingTableAdapter(List<PurchaseDateData> _days, Context _context) {
        days = _days;
        context = _context;
    }

    @NonNull
    @Override
    public AmountRemainingTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_amount_remaining_element, parent, false);
        return new AmountRemainingTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmountRemainingTableViewHolder holder, int position) {
        PurchaseDateData day = days.get(position);
        holder.nameView.setText(day.date);
        holder.spentView.setText(day.getSpent());
        holder.remainingView.setText(day.getRemaining());
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}
