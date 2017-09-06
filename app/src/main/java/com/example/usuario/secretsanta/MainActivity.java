package com.example.usuario.secretsanta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.secretsanta.helpClass.CheckEditText;
import com.example.usuario.secretsanta.helpClass.Date;
import com.example.usuario.secretsanta.helpClass.Hour;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private EditText et_name, et_place, et_date, et_hour, et_theme, et_min, et_max;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_name = (EditText)findViewById(R.id.etNameEvent);
        et_place = (EditText)findViewById(R.id.etPlaceEvent);
        et_date = (EditText)findViewById(R.id.etDate);
        et_hour = (EditText)findViewById(R.id.etHour);
        et_theme = (EditText)findViewById(R.id.etTheme);
        et_min = (EditText)findViewById(R.id.etPriceMin);
        et_max = (EditText)findViewById(R.id.etPriceMax);
        title = (TextView)findViewById(R.id.titleNewEvent);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);



        //Instance of DatePickerDialog
        Date newDate = new Date(this, et_date);
        newDate.date();

        //Instance of TimePickerDialog
        Hour newHour = new Hour(this, et_hour);
        newHour.hour();

        //Function get saved preferences
        getSavedPreferences();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void getSavedPreferences(){
        SharedPreferences event = getSharedPreferences("event", Context.MODE_PRIVATE);//Get SharedPreferences file
        String name = event.getString("name", "");//Get the event´s name to find if exist
        if (name.equals(""))//If we don´t have name means no event saved
        {
            //Do nothing
        }
        else//If we have name means saved event...
        {
            //Dialog to choose if we get back the data of the not finished saved event
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);//Create Dialog
            dialog.setTitle(R.string.dialog_get_event);
            dialog.setIcon(android.R.drawable.ic_input_get);//Icon

            dialog.setPositiveButton(R.string.button_recover, new DialogInterface.OnClickListener()//Action button "Recover"
            {
                @Override
                public void onClick(DialogInterface dialogo, int id)
                {
                    //Recover sharedPreferences Event(if not elements, set default ones)
                    SharedPreferences event = getSharedPreferences("event", Context.MODE_PRIVATE);
                    et_name.setText(event.getString("name", ""));
                    et_place.setText(event.getString("place", ""));
                    et_date.setText(event.getString("date", ""));
                    et_hour.setText(event.getString("hour", ""));
                    et_theme.setText(event.getString("theme", ""));
                    et_min.setText(event.getString("p_min", ""));
                    et_max.setText(event.getString("p_max", ""));
                }
            });
            dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()//Action button "Cancel"
            {
                @Override
                public void onClick(DialogInterface dialogo, int id)
                {
                    //Do nothing
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed(){
        //Dialog to choose if we get back the data of the not finished saved event
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);//Create Dialog
        dialog.setTitle(R.string.dialog_exit);
        dialog.setIcon(android.R.drawable.ic_dialog_info);//Icon
        dialog.setPositiveButton(R.string.button_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing
            }
        });
        dialog.show();
    }

    //Button fab 'next'
    private void createEvent(){
        CheckEditText checkEditText = new CheckEditText();//Instance of CheckEditText
        //Check if some EditText are empty and don´t leave go forward in that case
        if(checkEditText.check(et_name) && checkEditText.check(et_place) && checkEditText.check(et_date) && checkEditText.check(et_hour)) {
            try {
                //Get SharedPreferences file if exists, if not it is created
                SharedPreferences event = getSharedPreferences("event", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = event.edit();//Create the Editor to write on SharedPreferences file
                //Add elements to the file (String, int, float,....)
                editor.putString("name", et_name.getText().toString());
                editor.putString("place", et_place.getText().toString());
                editor.putString("date", et_date.getText().toString());
                editor.putString("hour", et_hour.getText().toString());
                editor.putString("theme", et_theme.getText().toString());
                editor.putString("p_min", et_min.getText().toString());
                editor.putString("p_max", et_max.getText().toString());
                editor.apply();//Confirm write on the file
            } catch (Exception e)//Storage Error
            {
                Toast.makeText(getApplicationContext(), R.string.error_storage, Toast.LENGTH_SHORT).show();
            }

            //Launch the next activity and next step
            Intent i = new Intent(this, GroupSelection.class);
            startActivity(i);
        }
    }
}
