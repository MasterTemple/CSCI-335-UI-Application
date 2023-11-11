package com.example.uigroupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.uigroupproject.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Map<Integer, Fragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if there is no given budget, do a popup at loading to get budget
//        Settings settings = new Settings(this);
//        settings.reset();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setSelectedItemId(R.id.home);

        fragments.put(R.id.home, new HomeFragment(this));
        fragments.put(R.id.stats, new StatsFragment(this));
        fragments.put(R.id.budget, new BudgetFragment(this));
        setFragment(fragments.get(R.id.home));

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            setFragment(fragments.get(item.getItemId()));
            return true;
        });

        // press home button N times for sample data
        int SECRET_MENU_CLICKS_REQUIRED = 5;
        MenuItem homeButton = binding.bottomNavigationView.getMenu().findItem(R.id.home);
        int[] homeButtonClicks = {0}; // I can't use a normal integer for some reason
        homeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                homeButtonClicks[0]++;
                if(homeButtonClicks[0] == SECRET_MENU_CLICKS_REQUIRED) {
                    Database db = new Database(MainActivity.this);
                    db.clearDatabase();
                    db.loadSampleData();
                    Settings settings = new Settings(MainActivity.this);
                    settings.reset();
                    settings.setBudget(400);
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
                return false;
            }
        });

//        Database db = new Database(this);
//        db.clearDatabase();
//        db.loadSampleData();
//        Settings settings = new Settings(this);
//        settings.reset();
//        settings.setBudget(400);
    }
    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
//    private void restartActivity

}