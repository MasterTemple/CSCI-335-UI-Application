package com.example.uigroupproject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionData {
    public long id;
    public String name;
    public double amount;
    public Date date;
    public long categoryId = -1;
    public String categoryName = "No Category";
    public String description;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public Date dateFromString(String _date) {
        try {
            return dateFormat.parse(_date);
        } catch (ParseException | NullPointerException error) {
            return null;
        }
    }
    public TransactionData() {}

    // must have name and amount
    public TransactionData(String _name, Double _amount) {
        this(_name, _amount, "");
    }

    public TransactionData(String _name, Double _amount, Date _date) {
        this(_name, _amount, "", _date);
    }
//    public TransactionData(String _name, Double _amount, String _date) {
//        this(_name, _amount, "", dateFromString(_date));
//    }

    public TransactionData(String _name, Double _amount, String _description) {
        this(_name, _amount, _description, new Date());
    }

    public TransactionData(String _name, Double _amount, String _description, Date _date) {
        name = _name;
        amount = _amount;
        description = _description;
        date = _date;
    }

    public TransactionData(String _name, Double _amount, Date _date, long _categoryId, String _description) {
        name = _name;
        amount = _amount;
        date = _date;
        categoryId = _categoryId;
        description = _description;
    }
    public TransactionData(String _name, Double _amount, String _date, long _categoryId, String _description) {
        name = _name;
        amount = _amount;
        date = dateFromString(_date);
        categoryId = _categoryId;
        description = _description;
    }

    public TransactionData(long _id, String _name, Double _amount, String _date, long _categoryId, String _description) {
        id = _id;
        name = _name;
        amount = _amount;
        date = dateFromString(_date);
        categoryId = _categoryId;
        description = _description;
    }

    public TransactionData(long _id, String _name, Double _amount, String _date, long _categoryId, String _categoryName, String _description) {
        id = _id;
        name = _name;
        amount = _amount;
        date = dateFromString(_date);
        categoryId = _categoryId;
        categoryName = _categoryName;
        description = _description;
    }

    public String getName() {
        return name;
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    public String getFormattedDate() {
        return dateToString();
//        try {
//            return String.format("%d/%d/%d", date.getMonth(), date.getDay(), date.getYear());
//        } catch(NullPointerException e) {
//            return "No Date.";
////            return "??/??/????";
//        }
    }
    public String dateToString() {
//        if(date == null) return null;
        if(date == null) return "??/??/????";
        return dateFormat.format(date);
    }
}
