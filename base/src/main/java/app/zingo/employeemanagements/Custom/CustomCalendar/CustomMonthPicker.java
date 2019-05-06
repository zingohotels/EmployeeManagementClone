package app.zingo.employeemanagements.Custom.CustomCalendar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.zingo.employeemanagements.base.R;

public class CustomMonthPicker {

    private AlertDialog mAlertDialog;
    private CustomMonthPicker.Builder builder;
    private Context context;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private DateMonthDialogListener dateMonthDialogListener;
    private OnCancelMonthDialogListener onCancelMonthDialogListener;
    private boolean isBuild = false;

    public CustomMonthPicker(Context context) {
        this.context = context;
        builder = new Builder();
    }

    public void show() {
        if (isBuild) {
            mAlertDialog.show();
        } else {
            builder.build();
            isBuild = true;
        }
    }

    public CustomMonthPicker setPositiveButton(DateMonthDialogListener dateMonthDialogListener) {
        this.dateMonthDialogListener = dateMonthDialogListener;
        mPositiveButton.setOnClickListener(builder.positiveButtonClick());
        return this;
    }

    public CustomMonthPicker setNegativeButton(OnCancelMonthDialogListener onCancelMonthDialogListener) {
        this.onCancelMonthDialogListener = onCancelMonthDialogListener;
        mNegativeButton.setOnClickListener(builder.negativeButtonClick());
        return this;
    }

    public CustomMonthPicker setPositiveText(String text) {
        mPositiveButton.setText(text);
        return this;
    }

    public CustomMonthPicker setNegativeText(String text) {
        mNegativeButton.setText(text);
        return this;
    }

    public CustomMonthPicker setLocale(Locale locale) {
        builder.setLocale(locale);
        return this;
    }

    public CustomMonthPicker setSelectedMonth(int index) {
        builder.setSelectedMonth(index);
        return this;
    }

    public CustomMonthPicker setSelectedYear(int year) {
        builder.setSelectedYear(year);
        return this;
    }

    public CustomMonthPicker setColorTheme(int color) {
        builder.setColorTheme(color);
        return this;
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    private class Builder implements MonthAdapter.OnSelectedListener {

        private MonthAdapter monthAdapter;
        private TextView mTitleView;
        private TextView mYear;
        private int year = 2018;
        private AlertDialog.Builder alertBuilder;
        private View contentView;
        RecyclerView recyclerView;
        MonthAdapter.OnSelectedListener listener ;

        private Builder() {
            alertBuilder = new AlertDialog.Builder(context);

            contentView = LayoutInflater.from(context).inflate(R.layout.dialog_month_picker, null);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);

            mTitleView = contentView.findViewById(R.id.title);
            mYear = contentView.findViewById(R.id.text_year);

            Button next = contentView.findViewById(R.id.btn_next);
            next.setOnClickListener(nextButtonClick(this));

            Button previous = contentView.findViewById(R.id.btn_previous);
            previous.setOnClickListener(previousButtonClick(this));

            mPositiveButton = contentView.findViewById(R.id.btn_p);
            mNegativeButton = contentView.findViewById(R.id.btn_n);

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            year = cal.get(Calendar.YEAR);

            listener = this;
            monthAdapter = new MonthAdapter(context, this,year);
            recyclerView = contentView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(monthAdapter);

            mTitleView.setText(monthAdapter.getShortMonth() + " " + year);
            mYear.setText(year + "");
        }

        public void setLocale(Locale locale) {
            monthAdapter.setLocale(locale);
        }

        public void setSelectedMonth(int index) {
            monthAdapter.setSelectedItem(index);
            mTitleView.setText(monthAdapter.getShortMonth() + " " + year);
        }

        public void setSelectedYear(int year) {
            this.year = year;
            mYear.setText(year + "");
            mTitleView.setText(monthAdapter.getShortMonth() + " " + year);
        }

        public void setColorTheme(int color) {
            LinearLayout linearToolbar = contentView.findViewById(R.id.linear_toolbar);
            linearToolbar.setBackgroundResource(color);

            monthAdapter.setBackgroundMonth(color);
            mPositiveButton.setTextColor(ContextCompat.getColor(context, color));
            mNegativeButton.setTextColor(ContextCompat.getColor(context, color));
        }

        public void build() {
            mAlertDialog = alertBuilder.create();
            mAlertDialog.show();
            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
            mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mAlertDialog.getWindow().setBackgroundDrawableResource(R.drawable.material_dialog_window);
            mAlertDialog.getWindow().setContentView(contentView);
        }

        public View.OnClickListener nextButtonClick(final MonthAdapter.OnSelectedListener listener) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    year++;
                    mYear.setText(year + "");
                    mTitleView.setText(monthAdapter.getShortMonth() + " " + year);

                    monthAdapter = new MonthAdapter(context, listener,year);
                    recyclerView = contentView.findViewById(R.id.recycler_view);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(monthAdapter);
                }
            };
        }

        public View.OnClickListener previousButtonClick(final MonthAdapter.OnSelectedListener listener) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    year--;
                    mYear.setText(year + "");
                    mTitleView.setText(monthAdapter.getShortMonth() + " " + year);

                    monthAdapter = new MonthAdapter(context, listener,year);
                    recyclerView = contentView.findViewById(R.id.recycler_view);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(monthAdapter);
                }
            };
        }

        public View.OnClickListener positiveButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dateMonthDialogListener.onDateMonth(
                            monthAdapter.getMonth(),
                            monthAdapter.getStartDate(),
                            monthAdapter.getEndDate(),
                            year, mTitleView.getText().toString());

                    mAlertDialog.dismiss();
                }
            };
        }

        public View.OnClickListener negativeButtonClick() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCancelMonthDialogListener.onCancel(mAlertDialog);
                }
            };
        }

        @Override
        public void onContentSelected() {
            mTitleView.setText(monthAdapter.getShortMonth() + " " + year);
        }
    }
}
