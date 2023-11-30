package com.example.uigroupproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.uigroupproject.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Database db;
    Settings settings;
    Map<Integer, Fragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if there is no given budget, do a popup at loading to get budget
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setSelectedItemId(R.id.home);

        fragments.put(R.id.home, new HomeFragment(this));
        fragments.put(R.id.stats, new StatsFragment(this));
        fragments.put(R.id.budget, new BudgetFragment(this));
        db = new Database(MainActivity.this);
        settings = new Settings(MainActivity.this);
        setFragment(fragments.get(R.id.home));

        int SECRET_MENU_CLICKS_REQUIRED = 5;
        int[] homeButtonClicks = {0, 0, 0}; // I can't use a normal integer for some reason
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.home) {
                homeButtonClicks[1]++;
                if(homeButtonClicks[1] == SECRET_MENU_CLICKS_REQUIRED) {
                    clearEverything();
                    setSampleData();
                    restartActivity();
                }
            } else {
                homeButtonClicks[1] = 0;
            }
            if(id == R.id.budget) {
                homeButtonClicks[2]++;
                if(homeButtonClicks[2] == SECRET_MENU_CLICKS_REQUIRED) {
                    clearEverything();
                    restartActivity();
                }

            } else {
                homeButtonClicks[2] = 0;
            }
            setFragment(fragments.get(id));
            return true;
        });
    }
    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void restartActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }
    private void clearEverything() {
        db.clearDatabase();
        settings.reset();
    }
    private void setSampleData() {
        db.loadSampleData(this);
        settings.setBudget(400);
    }

}