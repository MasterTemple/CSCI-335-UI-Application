package com.example.uigroupproject;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorHandling {
    public static TextWatcher checkForNonEmptyField(TextInputLayout layout) {
        return checkForNonEmptyField(layout, "This is a required field.");
    }
    public static TextWatcher checkForNonEmptyField(TextInputLayout layout, String errorMessage) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0)
                    layout.setError(errorMessage);
                else
                    layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
    public static TextWatcher checkForNonNegativeField(TextInputLayout layout) {
        return checkForNonNegativeField(layout, "Please enter a non-negative number");
    }
    public static TextWatcher checkForNonNegativeField(TextInputLayout layout, String errorMessage) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    layout.setError(errorMessage);
                    return;
                }
                double value = Double.parseDouble(s.toString());
                if(value < 0)
                    layout.setError(errorMessage);
                else
                    layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
    public static TextWatcher checkForValidDate(TextInputLayout layout) {
        return checkForValidDate(layout, "Please enter a valid date.");
    }
    public static TextWatcher checkForValidDate(TextInputLayout layout, String errorMessage) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                try {
                    Pattern pattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) {
                        layout.setError(null);
                    } else {
                        layout.setError(errorMessage);
                    }
                } catch (Exception e) {
                    layout.setError(errorMessage);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
    public static TextWatcher checkWithinList(TextInputLayout layout, List<String> possibleValues, boolean emptyIsValid) {
        return checkWithinList(layout, possibleValues, "Please enter a valid option.", emptyIsValid);
    }
    public static TextWatcher checkWithinList(TextInputLayout layout, List<String> possibleValues, String errorMessage, boolean emptyIsValid) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0 && emptyIsValid) {
                    layout.setError(null);
                    return;
                }
                String value = s.toString();
                for(String possibleValue: possibleValues) {
                    if(value.contentEquals(possibleValue)) {
                        layout.setError(null);
                        return;
                    }
                }
                layout.setError(errorMessage);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }
}
