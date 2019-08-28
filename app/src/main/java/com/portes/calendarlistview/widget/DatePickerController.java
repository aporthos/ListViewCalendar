package com.portes.calendarlistview.widget;

/**
 * @author AP - May 21,2019.
 * @version 0.1.0
 * @since 0.1.0
 */
public interface DatePickerController {
    public abstract int getMaxYear();

    public abstract void onDayOfMonthSelected(int year, int month, int day);

    public abstract void onDateRangeSelected(final SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays);
}
