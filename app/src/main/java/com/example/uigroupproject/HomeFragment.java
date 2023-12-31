package com.example.uigroupproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** @noinspection resource*/
public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private Database db;
    private View view;

    @SuppressWarnings("deprecation")
    private static int getDaysLeftInMonth() {
        Date today = new Date();
        return 30 - today.getDay();
    }

    private void setValues() {
        TextView dollarsRemaining = view.findViewById(R.id.home_remaining_value);
        TextView dailyBudget = view.findViewById(R.id.home_daily_budget_value);
        TextView percentRemaining = view.findViewById(R.id.home_percent_remaining);
        LinearProgressIndicator progressBar = view.findViewById(R.id.home_progress_bar);
        Database db = new Database(context);
        Settings settings = new Settings(context);
        double budget = settings.budget;
        List<TransactionData> transactions = db.getAllTransactionsInPastMonth();
        double spentThisMonth = 0.0;
        for(TransactionData t: transactions) {
            spentThisMonth += t.amount;
        }
        int daysLeftThisMonth = getDaysLeftInMonth();
        double remainingValue = budget - spentThisMonth;
        double remainingPercent = remainingValue * 100;
        progressBar.setProgress(budget == 0 ? 0 : (int)Math.floor(remainingPercent/budget));
        progressBar.setTrackThickness(20);
//        progressBar.setIndicatorColor(ContextCompat.getColor(context, R.color.green));
        percentRemaining.setText(String.format(Locale.US, "%.1f%%", budget == 0 ? 0 : remainingPercent/budget));
        dollarsRemaining.setText(String.format(Locale.US, "$%.2f", remainingValue));
        dailyBudget.setText(String.format(Locale.US, "$%.2f", remainingValue / daysLeftThisMonth));
    }

    public HomeFragment() {}
    public HomeFragment(Context _context) {
        context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.transactions_preview);
        updateTransactionList();
        setValues();
        Button viewAllButton = view.findViewById(R.id.view_all_button);
        viewAllButton.setOnClickListener(v -> viewAllTransactions());

        Button addTransactionButton = view.findViewById(R.id.add_transaction_button);
        addTransactionButton.setOnClickListener(v -> addTransaction());
        return view;
    }
    private void addTransaction() {
        Intent intent = new Intent(context, TransactionEditActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
    }
    private void viewAllTransactions() {
        Intent intent = new Intent(context, RecentsActivity.class);
        startActivity(intent);
    }

    private void updateTransactionList() {
        List<TransactionData> transactions = db.getAllTransactionsInPastMonth();
        Collections.reverse(transactions);
        TransactionListAdapter adapter = new TransactionListAdapter(transactions, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
    @Override
    public void onResume() {
        super.onResume();
        updateTransactionList();
        setValues();
    }
}
