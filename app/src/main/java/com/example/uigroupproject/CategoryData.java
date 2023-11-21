package com.example.uigroupproject;

import android.content.Context;

import java.util.StringJoiner;

public class CategoryData {
    public long id;
    public String name;
    public String type;
    public double value;
    final private String PERCENT = "Percent";
    final private String EMPTY_VALUE = "___";
    final private String FIXED_VALUE = "Fixed Value";
    public CategoryData(long _id, String _name, String _type, double _value) {
        id = _id;
        name = _name;
        type = _type;
        value = _value;
    }
    public CategoryData(String _name, String _type, double _value) {
        name = _name;
        type = _type;
        value = _value;
    }
    public String getPercent(Context context) {
        Settings settings = new Settings(context);
        double budget = settings.budget;
        if(type.contentEquals(PERCENT)) {
            return String.format("%.0f%%", value);
        } else if(type.contentEquals(FIXED_VALUE)) {
            return budget == 0 ? "0%" : String.format("%.0f%%", 100 / (budget / value));
        }
        return EMPTY_VALUE;
    }

    public String getNumber(Context context) {
        Settings settings = new Settings(context);
        double budget = settings.budget;
        if(type.contentEquals(FIXED_VALUE)) {
            return String.format("$%.2f", value);
        } else if (type.contentEquals(PERCENT)) {
            double percent = value / 100;
            return String.format("$%.2f", budget * percent);
        }
        return EMPTY_VALUE;
    }

}
