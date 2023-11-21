package com.example.uigroupproject;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public Context context;
    public SharedPreferences settings;
    public static final String PREFERENCES = "settings";
    public double budget;
    Settings(Context _context) {
        context = _context;
        settings = context.getSharedPreferences(PREFERENCES, 0);
        if(!settings.contains("budget")) {
            settings.edit().putFloat("budget", 0f).apply();
        }
        float floatBudget = 0;
        floatBudget = settings.getFloat("budget", floatBudget);
        budget = floatBudget;
    }

    public void setBudget(double _budget) {
        budget = _budget;
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("budget", (float) budget);
        editor.apply();
    }

    public void reset() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

}
