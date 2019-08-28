package com.portes.calendarlistview.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.portes.calendarlistview.R;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author AP - May 21,2019.
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleMonthView extends View {
    private static final String TAG = "SimpleMonthView";
    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int SELECTED_CIRCLE_ALPHA = 128;
    protected static int DEFAULT_HEIGHT = 32;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;
    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MIN_HEIGHT = 10;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;
    protected static int MONTH_HEADER_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;

    protected int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    protected Paint mMonthDayLabelPaint;
    protected Paint mMonthNumPaint;
    protected Paint mMonthTitleBGPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mSelectedCirclePaint;
    protected Paint mSelectedCirclePaintTemp;
    protected int mCurrentDayTextColor;
    protected int mMonthTextColor;
    protected int mDayTextColor;
    protected int mDayNumColor;
    protected int mMonthTitleBGColor;
    protected int mPreviousDayColor;
    protected int mSelectedDaysColor;
    protected int mSelectedDaysColorTemp;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;
    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;
    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected Boolean mDrawRect;
    protected int mRowHeight = DEFAULT_HEIGHT;
    protected int mWidth;
    protected int mYear;
    final Time today;

    private final Calendar mCalendar;
    private final Calendar mDayLabelCalendar;
    private final Boolean isPrevDayEnabled;
    private boolean firstLoad = true;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDayClickListener mOnDayClickListener;

    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);

        Resources resources = context.getResources();
        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
        mMonthTitleTypeface = resources.getString(R.string.sans_serif);
        mCurrentDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDay, resources.getColor(R.color.normal_day));
        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName, resources.getColor(R.color.normal_day));
        mDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorDayName, resources.getColor(R.color.normal_day));
        mDayNumColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay, resources.getColor(R.color.normal_day));
        mPreviousDayColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDay, resources.getColor(R.color.normal_day));
        mSelectedDaysColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background));
        mSelectedDaysColorTemp = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background_temp));
        mMonthTitleBGColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayText, resources.getColor(R.color.selected_day_text));

        mDrawRect = typedArray.getBoolean(R.styleable.DayPickerView_drawRoundRect, false);

        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, resources.getDimensionPixelSize(R.dimen.text_size_month));
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, resources.getDimensionPixelSize(R.dimen.text_size_day_name));
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, resources.getDimensionPixelOffset(R.dimen.selected_day_radius));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6);

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true);

        initView();

    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

        for (int i = 0; i < mNumDays; i++) {
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = (2 * i + 1) * dayWidthHalf + mPadding;
            mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
            canvas.drawText(
                    mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar.get(Calendar.DAY_OF_WEEK)].substring(0, 1).toUpperCase(Locale.getDefault()),
                    x,
                    y,
                    mMonthDayLabelPaint);
        }
    }

    private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        canvas.drawText(stringBuilder.toString(), x, y, mMonthTitlePaint);
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        return new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(mCalendar.getTime());
    }

    private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
            mOnDayClickListener.onDayClick(this, calendarDay);
        }
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth == time.month && monthDay < time.monthDay);
    }

    protected void drawMonthNums(Canvas canvas) {
        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        final int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = findDayOffset();
        int day = 1;
        while (day <= mNumCells) {
            final int mPaddingNew = ((mWidth + 2 * mPadding) / (2 * mNumDays)) - DAY_SELECTED_CIRCLE_SIZE;
            final int top = (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) - DAY_SELECTED_CIRCLE_SIZE;
            final int bottom = (y - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE;
            final int x = paddingDay * (1 + dayOffset * 2) + mPadding;
            final boolean isBeginMonthAndDay = mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear;
            final boolean isMonthLastAndDay = mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear;
            final boolean isSelectedBeginLastYear = mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear;
            final boolean isSelectedBeginLastMonth = mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth;
            final boolean isSelectedBeginLast = mSelectedBeginDay < mSelectedLastDay && day > mSelectedBeginDay && day < mSelectedLastDay;
            final boolean isSelectedBeginLastNotEqual = mSelectedBeginDay > mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay;
            final boolean isSelectedBeginLastMonthNotEqual = mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay;
            final boolean isSelectedBeginLastMonthNot = mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay;
            final boolean isSelectedBeginLastMonthMore = mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay;
            final boolean isSelectedBeginLastMonthLast = mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay;
            final boolean isSelectedBeginLastDay = mSelectedBeginDay != -1 && mSelectedLastDay != -1;
            final boolean isSelectedBeginDay = mSelectedBeginDay == -1 && mSelectedBeginMonth == -1 && mSelectedBeginYear == -1;
            final boolean isSelectedLastDay = mSelectedLastDay == -1 && mSelectedLastMonth == -1 && mSelectedLastYear == -1;
            if (isBeginMonthAndDay || isMonthLastAndDay) {
                if (!isSelectedBeginDay && !isSelectedLastDay) {
                    if (day == mSelectedBeginDay) {
                        Log.i(TAG, "drawMonthNums: mSelectedBeginDay " + mSelectedBeginDay);
                        Log.i(TAG, "drawMonthNums: mSelectedBeginMonth " + mSelectedBeginMonth);
                        Log.i(TAG, "drawMonthNums: mSelectedLastMonth " + mSelectedLastMonth);
                        if (mSelectedBeginMonth == mSelectedLastMonth) {
                            if (mSelectedBeginDay > mSelectedLastDay) {
                                final int mPaddingTop = x - DAY_SELECTED_CIRCLE_SIZE - mPaddingNew;
                                final RectF rectF = new RectF(x, top, mPaddingTop, bottom);
                                setConfigInterval();
                                canvas.drawRect(rectF, mMonthNumPaint);
                            } else {
                                final int mPaddingLeft = x + DAY_SELECTED_CIRCLE_SIZE + mPaddingNew;
                                final RectF rectF = new RectF(mPaddingLeft, top, x, bottom);
                                setConfigInterval();
                                canvas.drawRect(rectF, mMonthNumPaint);
                            }
                        } else {
                            if (mSelectedBeginDay > mSelectedLastDay) {
                                final int mPaddingLeft = x + DAY_SELECTED_CIRCLE_SIZE + mPaddingNew;
                                final RectF rectF = new RectF(mPaddingLeft, top, x, bottom);
                                setConfigInterval();
                                canvas.drawRect(rectF, mMonthNumPaint);
                            } else {
                                final int mPaddingTop = x - DAY_SELECTED_CIRCLE_SIZE - mPaddingNew;
                                final RectF rectF = new RectF(x, top, mPaddingTop, bottom);
                                setConfigInterval();
                                canvas.drawRect(rectF, mMonthNumPaint);
                            }
                        }
                    }

//                    if (day == mSelectedLastDay) {
//                        if (mSelectedBeginMonth == mSelectedLastMonth) {
//                            if (mSelectedBeginDay < mSelectedLastDay) {
//                                final int mPaddingTop = x - DAY_SELECTED_CIRCLE_SIZE - mPaddingNew;
//                                final RectF rectF = new RectF(x, top, mPaddingTop, bottom);
//                                setConfigInterval();
//                                canvas.drawRect(rectF, mMonthNumPaint);
//                            } else {
//                                final int mPaddingLeft = x + DAY_SELECTED_CIRCLE_SIZE + mPaddingNew;
//                                final RectF rectF = new RectF(mPaddingLeft, top, x, bottom);
//                                setConfigInterval();
//                                canvas.drawRect(rectF, mMonthNumPaint);
//                            }
//                        } else {
//                            if (mSelectedBeginDay < mSelectedLastDay) {
//                                final int mPaddingLeft = x + DAY_SELECTED_CIRCLE_SIZE + mPaddingNew;
//                                final RectF rectF = new RectF(mPaddingLeft, top, x, bottom);
//                                setConfigInterval();
//                                canvas.drawRect(rectF, mMonthNumPaint);
//                            } else {
//                                final int mPaddingTop = x - DAY_SELECTED_CIRCLE_SIZE - mPaddingNew;
//                                final RectF rectF = new RectF(x, top, mPaddingTop, bottom);
//                                setConfigInterval();
//                                canvas.drawRect(rectF, mMonthNumPaint);
//                            }
//
//                        }
//                    }
                }

                Log.i(TAG, "day " + day + " firstLoad "+firstLoad);
                if (firstLoad) {
                    canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
                }
            }

            if (mHasToday && (mToday == day)) {
                Log.i(TAG, "drawMonthNums: day " + day + " mHasToday " + mHasToday);
                mMonthNumPaint.setColor(mSelectedDaysColor);
//                canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaintTemp);
            } else {
                final int mDayColor = day < mToday ? mCurrentDayTextColor : mDayTextColor;
                mMonthNumPaint.setColor(mDayColor);
            }
            if (isBeginMonthAndDay || isMonthLastAndDay) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
            }

            if ((isSelectedBeginLastDay && mSelectedBeginYear == mSelectedLastYear &&
                    mSelectedBeginMonth == mSelectedLastMonth &&
                    mSelectedBeginDay == mSelectedLastDay &&
                    day == mSelectedBeginDay &&
                    mMonth == mSelectedBeginMonth &&
                    mYear == mSelectedBeginYear)) {
                mMonthNumPaint.setColor(mSelectedDaysColorTemp);
            }

            if ((isSelectedBeginLastDay && isSelectedBeginLastYear) &&
                    (((isSelectedBeginLastMonth) && (isSelectedBeginLast || isSelectedBeginLastNotEqual)) ||
                            (isSelectedBeginLastMonthNotEqual || isSelectedBeginLastMonthNot) ||
                            (isSelectedBeginLastMonthMore || isSelectedBeginLastMonthLast))) {
                setConfigInterval();
                canvas.drawRect(getRect(x, mPaddingNew, top, bottom), mMonthNumPaint);
            }

            //Rango para diferentes años
//            if ((isSelectedBeginLastDay && mSelectedBeginYear != mSelectedLastYear && ((mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth)) &&
//                    (((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)) ||
//                            ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay))))) {
//
//                setConfigInterval();
//                canvas.drawRect(getRect(x, mPaddingNew, top, bottom), mMonthNumPaint);
//            }
//
//            if ((isSelectedBeginLastDay && mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear) &&
//                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) ||
//                            (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth))) {
//                setConfigInterval();
//                canvas.drawRect(getRect(x, mPaddingNew, top, bottom), mMonthNumPaint);
//            }
//
//            if ((isSelectedBeginLastDay && mSelectedBeginYear != mSelectedLastYear) &&
//                    ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear))) ||
//                            (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear))))) {
//                setConfigInterval();
//                canvas.drawRect(getRect(x, mPaddingNew, top, bottom), mMonthNumPaint);
//            }
//
//            if (!isPrevDayEnabled && prevDay(day, today) && today.month == mMonth && today.year == mYear) {
//                mMonthNumPaint.setColor(mPreviousDayColor);
//            }

            canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
            firstLoad = false;
        }
    }

    private void setConfigInterval() {
        mMonthNumPaint.setColor(mSelectedDaysColorTemp);
        mMonthNumPaint.setAlpha(SELECTED_CIRCLE_ALPHA);
    }

    private RectF getRect(int x, int padding, int top, int bottom) {
        final int mPaddingLeft = x - DAY_SELECTED_CIRCLE_SIZE - padding;
        final int mPaddingRight = x + DAY_SELECTED_CIRCLE_SIZE + padding;
        return new RectF(mPaddingLeft, top, mPaddingRight, bottom);
    }

    public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;

        return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
    }

    protected void initView() {
        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.BOLD));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Paint.Align.CENTER);
        mMonthTitlePaint.setStyle(Paint.Style.FILL);

        mMonthTitleBGPaint = new Paint();
        mMonthTitleBGPaint.setFakeBoldText(true);
        mMonthTitleBGPaint.setAntiAlias(true);
        mMonthTitleBGPaint.setColor(mMonthTitleBGColor);
        mMonthTitleBGPaint.setTextAlign(Paint.Align.CENTER);
        mMonthTitleBGPaint.setStyle(Paint.Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
        mSelectedCirclePaint.setStyle(Paint.Style.FILL);

        mSelectedCirclePaintTemp = new Paint();
        mSelectedCirclePaintTemp.setFakeBoldText(true);
        mSelectedCirclePaintTemp.setAntiAlias(true);
        mSelectedCirclePaintTemp.setColor(mMonthTitleBGColor);
        mSelectedCirclePaintTemp.setTextAlign(Paint.Align.CENTER);
        mSelectedCirclePaintTemp.setStyle(Paint.Style.FILL);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDayTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Paint.Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Paint.Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Paint.Style.FILL);
        mMonthNumPaint.setTextAlign(Paint.Align.CENTER);
        mMonthNumPaint.setFakeBoldText(true);
    }

    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthDayLabels(canvas);
        drawMonthNums(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows + MONTH_HEADER_SIZE);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public static abstract interface OnDayClickListener {
        public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
    }
}
