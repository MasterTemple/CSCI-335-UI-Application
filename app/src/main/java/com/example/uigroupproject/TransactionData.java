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
    public int categoryId;
    public String description;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    public Date dateFromString(String _date) {
        try {
            return dateFormat.parse(_date);
        } catch (ParseException error) {
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
    public String getName() {
        return name;
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    public String getFormattedDate() {
        return String.format("%d/%d/%d", date.getMonth(), date.getDay(), date.getYear());
    }
    public String dateToString() {
        if(date == null) return null;
        return dateFormat.format(date);
    }
}
