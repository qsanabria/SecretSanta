package com.example.usuario.secretsanta.helpClass;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class Date {
    private Activity activity;
    private Calendar calendar = Calendar.getInstance();
    private int mDay, mMonth, mYear;
    private EditText et;

    public Date(Activity activity, EditText etDate)//Constructor made with the activity and EditText given
    {
        this.activity = activity;
        this.et = etDate;
    }

    public void date()//Method of DatePickerDialog
    {
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Callback method to give constructor of DatePickerDialog
        DatePickerDialog.OnDateSetListener dateMethod = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                setDate();
            }
        };

        //Creation instance of DatePickerDialog
        final DatePickerDialog dpd = new DatePickerDialog(activity, dateMethod, mYear, mMonth, mDay);
        //With callback function and initial data(year, month and day)

        et.setOnTouchListener(new View.OnTouchListener()//Show DatePicker when is clicked
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                dpd.show();
                return true;
            }
        });
    }

    private void setDate()//Set the selected date on the EditText
    {
        et.setText(new StringBuilder().append(digits(mDay)).append("/").append(digits(mMonth+1)).append("/").append(mYear));
    }

    //Method to add '0' on the left if the number selected on date is under 10
    private String digits(int c)
    {
        if (c >= 10)
        {
            return String.valueOf(c);
        }
        else
        {
            return "0"+String.valueOf(c);
        }
    }
}
