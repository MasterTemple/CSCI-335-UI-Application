package com.example.uigroupproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class HomeFragment extends Fragment {
    private Context context;
    public HomeFragment() {}
    public HomeFragment(Context _context) {
        context = _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView remaining = view.findViewById(R.id.home_remaining_value);
//        Database db = new Database(context);
//        Settings settings = new Settings(context);
//        Double budget = settings.budget;
////        List<TransactionData> transactions = db.getAllTransactionsInPastMonth();
//        List<TransactionData> transactions = db.getAllTransactions();
//        Double spentThisMonth = 0.0;
//        for(TransactionData t: transactions) {
//            spentThisMonth += t.amount;
//        }
//        int daysLeftThisMonth = 0;
//        remaining.setText(String.format("$%.2f", budget - spentThisMonth));

        Button viewAllButton = view.findViewById(R.id.view_all_button);
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllTransactions();
            }
        });

        Button addTransactionButton = view.findViewById(R.id.add_transaction_button);
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });
        return view;
    }
    private void addTransaction() {
        Intent intent = new Intent(context, TransactionEditActivity.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
    }
    private void viewAllTransactions() {
        Intent intent = new Intent(context, RecentsActivity.class);
//        intent.putExtra("isNew", true);
//        intent.putExtra("context", this);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
