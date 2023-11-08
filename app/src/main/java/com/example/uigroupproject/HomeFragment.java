package com.example.uigroupproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

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
        startActivity(intent);
    }
}
