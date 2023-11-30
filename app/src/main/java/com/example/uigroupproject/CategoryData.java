package com.example.uigroupproject;



import android.content.Context;
import java.util.Locale;

public class CategoryData {
    public long id;
    public String name;
    public String type;
    public double value;
    final private String PERCENT = "Percent";
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
        return getPercentFromTotalSpent(budget);
    }
    public String getPercentFromTotalSpent(double totalSpent) {
        double val;
        if(type.contentEquals(PERCENT)) {
            val = value;
        } else if(type.contentEquals(FIXED_VALUE)) {
            val = totalSpent == 0 ? 0: (100 / (totalSpent / value));
        } else {
            val = 0;
        }
        return String.format(Locale.US, "%.0f%%", val);
    }

    public String getNumber(Context context) {
        Settings settings = new Settings(context);
        double budget = settings.budget;
        double val;
        if(type.contentEquals(FIXED_VALUE)) {
            val = value;
        } else if (type.contentEquals(PERCENT)) {
            double percent = value / 100;
            val = budget * percent;
        } else {
            val = 0;
        }
        return String.format(Locale.US, "$%.2f", val);
    }

    public double getNumberDouble(Context context) {
        return Double.parseDouble(getNumber(context).replace("$", ""));
    }
    public double getPercentDouble(Context context) {
        return Double.parseDouble(getPercent(context).replace("%", ""));
    }

}
