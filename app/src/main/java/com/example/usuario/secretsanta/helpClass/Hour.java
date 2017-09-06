package com.example.usuario.secretsanta.helpClass;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class Hour {
    private int mHour, mMinute;
    private Calendar calendar = Calendar.getInstance();
    private Activity activity;
    private EditText et;

    public Hour(Activity activity, EditText etHour)//Constructor with activity and EditText
    {
        this.activity = activity;
        this.et = etHour;
    }

    public void hour()//Method of TimePickerDialog
    {
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        //Callback method to give constructor of TimePickerDialog
        TimePickerDialog.OnTimeSetListener hourMethod = new TimePickerDialog.OnTimeSetListener()
        {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                mHour = hourOfDay;
                mMinute = minute;
                setHour();
            }
        };

        //Creation instance of TimePickerDialog
        final TimePickerDialog tpd = new TimePickerDialog(activity, hourMethod, mHour, mMinute, true);

        et.setOnTouchListener(new View.OnTouchListener()//Show TimePicker when is clicked
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                tpd.show();
                return true;
            }
        });
    }

    private void setHour()//Set the selected time on the EditText
    {
        et.setText(new StringBuilder().append(digits(mHour)).append(":").append(digits(mMinute)));
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
