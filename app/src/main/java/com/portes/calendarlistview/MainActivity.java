package com.portes.calendarlistview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.portes.calendarlistview.widget.DatePickerController;
import com.portes.calendarlistview.widget.DayPickerView;
import com.portes.calendarlistview.widget.SimpleMonthAdapter;

public class MainActivity extends AppCompatActivity implements DatePickerController {

    private DayPickerView pickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickerView = findViewById(R.id.pickerView);
        pickerView.setController(this);
    }

    @Override
    public int getMaxYear() {
        return 2020;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {

    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {

    }
}
