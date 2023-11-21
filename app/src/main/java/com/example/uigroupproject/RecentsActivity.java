package com.example.uigroupproject;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentsActivity extends AppCompatActivity {
    private List<TransactionData> transactions = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionListAdapter adapter;
    private Database db;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recents);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle("Transaction History");
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new Database(this);
        transactions = db.getAllTransactions();
        Collections.reverse(transactions);
        search = findViewById(R.id.recent_transactions_search);
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
        search.setOnClickListener(v -> {
            Toast.makeText(RecentsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            search.requestFocus();
            search.setIconified(false);
        });

        adapter = new TransactionListAdapter(transactions, this);
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

    private void updateRecentsList() {
        transactions = db.getAllTransactions();
        Collections.reverse(transactions);
        adapter = new TransactionListAdapter(transactions, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(search.getQuery().toString().length() > 0)
            filterTransactions(search.getQuery().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecentsList();
    }
}
