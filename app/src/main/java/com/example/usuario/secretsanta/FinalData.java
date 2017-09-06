package com.example.usuario.secretsanta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.usuario.secretsanta.helpClass.GenerateDraw;
import com.example.usuario.secretsanta.helpClass.ListViewDropDown;
import com.example.usuario.secretsanta.helpClass.OperateDBParticipants;

public class FinalData extends AppCompatActivity {

    private TextView title, nameEvent, placeEvent, day, hour, theme, value;
    private Toolbar toolbar;
    private ListView list;
    private String nameGroup, name, place, d, h, t, v, p_min, p_max, total;
    private ListViewDropDown lstParticipants;
    private OperateDBParticipants db;
    private String[] lstNames;
    private String []pass_data = {"","","","","",""};
    private int i;
    private FloatingActionButton fabBack, fabDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_data);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.titleFinalData);
        nameEvent = (TextView) findViewById(R.id.titleEvent);
        placeEvent = (TextView) findViewById(R.id.tvPlaceEvent);
        day = (TextView) findViewById(R.id.tvDay);
        hour = (TextView) findViewById(R.id.tvHour);
        theme = (TextView) findViewById(R.id.tvTheme);
        value = (TextView) findViewById(R.id.tvValue);
        list = (ListView)findViewById(R.id.listParticipants);
        fabBack = (FloatingActionButton) findViewById(R.id.fabBack);
        fabDraw = (FloatingActionButton) findViewById(R.id.fabDraw);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);

        Bundle bag = getIntent().getExtras();//Get the bag to get name of group
        nameGroup = bag.getString("nameGroup").toString();//Get the name

        lstParticipants = new ListViewDropDown(this, list);

        db = new OperateDBParticipants(this, nameGroup, null, 1);

        db.openDB();
        lstNames = db.getData();
        if (lstNames != null)
        {
            for (i=0; i<lstNames.length; i++)
            {
                String str = lstNames[i];
                lstParticipants.addList(str);
            }
            i = lstNames.length;
        }
        else
        {
            i = 0;
        }
        db.closeDB();

        //Get data from SharedPreferences
        SharedPreferences pref_event = getSharedPreferences("event", Context.MODE_PRIVATE);
        name = pref_event.getString("name", "");
        pass_data[0] += name;
        place = pref_event.getString("place", "");
        pass_data[1] += place;
        d = pref_event.getString("date", "");
        pass_data[2] += d;
        h = pref_event.getString("hour", "");
        pass_data[3] += h;
        t = pref_event.getString("theme", "");
        pass_data[4] += t;
        p_min = pref_event.getString("p_min", "");
        p_max = pref_event.getString("p_max", "");
        total = "";

        if (t.equals(""))//Put text if not theme
        {
            t+=getString(R.string.participants_election);
            pass_data[4] += t;
        }

        //Check out the range of prices
        if (!(p_min.equals("")) && !(p_max.equals("")))
        {
            //total+="De "+p_min+"� a "+p_max+"�";
            total+=getString(R.string.from)+" "+p_min+getString(R.string.pound)+" "+getString(R.string.to)+" "+p_max+getString(R.string.pound);
            pass_data[5] += total;
        }
        else if (!(p_min.equals("")) && (p_max.equals("")))
        {
            //total+="M�s de "+p_min+"�";
            total+=getString(R.string.more_than)+" "+p_min+getString(R.string.pound);
            pass_data[5] += total;
        }
        else if (p_min.equals("") && !(p_max.equals("")))
        {
            //total+="Menos de "+p_max+"�";
            total+=getString(R.string.less_than)+" "+p_max+getString(R.string.pound);
            pass_data[5] += total;
        }
        else
        {
            //total+="Sin definir";
            total+=getString(R.string.without_define);
            pass_data[5] += total;
        }

        //Set TextView with data
        nameEvent.setText(name);
        placeEvent.setText(place);
        day.setText(d);
        hour.setText(h);
        theme.setText(t);
        value.setText(total);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backScreen(view);
            }
        });
        fabDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate_draw(view);
            }
        });
    }

    /**
     * Come back to exclusions
     */
    public void backScreen(View v)
    {
        Intent i = new Intent(this, Exclusions.class);
        i.putExtra("firstTime", false);//Flag to do not initialize exclusions
        FinalData.this.finish();//End Activity
    }

    /**
     * Come back to exclusions
     */
    public void onBackPressed()
    {
        View v = new View(this);
        backScreen(v);
    }

    /**
     *
     */
    public void generate_draw(View v)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setTitle(R.string.dialog_generating_draw);
        dialog.setMessage(R.string.draw);
        dialog.setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                draw();
            }
        });
        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Cancel
            }
        });
        dialog.show();
    }

    /**
     * Make assignation between participants, send the data and delete the event
     */
    public void draw()
    {
        boolean val;
        GenerateDraw result = new GenerateDraw(this, nameGroup);
        val = result.makeDraw(pass_data);

        if (val == true)
        {
            SharedPreferences event = getSharedPreferences("event", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = event.edit();
            editor.clear();
            editor.commit();


            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setTitle(R.string.dialog_done);
            dialog.setMessage(R.string.dialog_done);
            dialog.setNeutralButton(R.string.button_continue, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface arg0, int arg1)
                {
                    System.runFinalization();
                    System.exit(0);
                }
            });
            dialog.show();
        }
    }
}
