package com.example.uigroupproject;

public class PurchaseDateData {
    public String date;
    public double spent;
    public double remaining;
    PurchaseDateData(String _date, double _spent, double _remaining) {
        date = _date;
        spent = _spent;
        remaining = _remaining;
    }
    String getSpent() {
        return String.format("$%.2f", spent);
    }
    String getRemaining() {
        return String.format("$%.2f", remaining);
    }
}
