package com.example.uigroupproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecentsActivity extends AppCompatActivity {
    final private List<TransactionData> transactions = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionListAdapter adapter;
    private Context context;

    private void setTransactions() {
        transactions.add(new TransactionData("Monkey", 103.22));
        transactions.add(new TransactionData("Potato", 4.00));
        transactions.add(new TransactionData("Computer", 1240.99));
        transactions.add(new TransactionData("Caf Swipe", 11.30));
        for(int i=0;i<20;i++) {
            double price = (new Random().nextInt( 3000));
            price = price + (new Random().nextInt(100)/100.0);
            transactions.add(new TransactionData(String.format("Item %d", i), price));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recents);
        setTransactions();
        SearchView search = findViewById(R.id.recent_transactions_search);
        search.clearFocus();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTransactions(newText);
                return true;
            }
        });

        adapter = new TransactionListAdapter(transactions, context);
        recyclerView = findViewById(R.id.recent_transactions_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void filterTransactions(String newText) {
        List<TransactionData> newList = new ArrayList<>();
        for(TransactionData t : transactions) {
            if(t.getName().toLowerCase().contains(newText.toLowerCase())) {
                newList.add(t);
            }
        }
        adapter.setFilteredList(newList);
    }
}
