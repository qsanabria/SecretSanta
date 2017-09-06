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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.secretsanta.helpClass.GenerateDraw;
import com.example.usuario.secretsanta.helpClass.OperateDBParticipants;
import com.example.usuario.secretsanta.helpClass.PreferencesExclusions;

public class Exclusions extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private Button btnAdd, btnSee;
    private RadioButton rdNo, rdYes;
    private CheckBox cbBoth;
    private Spinner spinner1, spinner2;
    private FloatingActionButton fabBack, fabNext;
    private String nameGroup;
    private String[] lstNames, list;
    private boolean firstTime;
    private OperateDBParticipants db;
    private ArrayAdapter<String> adapter1, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exclusions);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.titleExclusions);
        rdNo = (RadioButton) findViewById(R.id.radioNoExclusions);
        rdYes = (RadioButton) findViewById(R.id.radioExclusions);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        cbBoth = (CheckBox) findViewById(R.id.checkBoth);
        btnAdd = (Button) findViewById(R.id.btnAddExclusion);
        btnSee = (Button) findViewById(R.id.btnSeeExclusion);
        fabBack = (FloatingActionButton) findViewById(R.id.fabBack);
        fabNext = (FloatingActionButton) findViewById(R.id.fabNext);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);

        Bundle bag = getIntent().getExtras();
        nameGroup = bag.getString("nameGroup").toString();
        firstTime = bag.getBoolean("firstTime");

        if (firstTime)
        {
            //Clear SharedPreferences file when we load the activity
            SharedPreferences exclusions = getSharedPreferences("exclusions", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = exclusions.edit();
            editor.clear().commit();
            editor.putInt("numExclusions", 0);
            editor.commit();
        }

        db = new OperateDBParticipants(this, nameGroup, null, 1);
        db.openDB();
        lstNames = db.getData();
        db.closeDB();

        //Copy the list to add a empty String at the beginning
        list = new String[lstNames.length + 1];
        list[0] = "";
        for(int i=0; i<lstNames.length; i++)
        {
            list[i + 1] = "";
            list[i + 1] = "" + lstNames[i];
        }

        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        spinner1.setAdapter(adapter1);
        spinner1.setEnabled(false);

        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        spinner2.setAdapter(adapter2);
        spinner2.setEnabled(false);

        cbBoth.setEnabled(false);
        btnAdd.setEnabled(false);
        btnSee.setEnabled(false);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backScreen(view);
            }
        });
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextExclusions(view);
            }
        });
    }

    /**
     * Get back to Group Selection
     */
    public void backScreen(View v)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_exit_exclusions);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setMessage(R.string.dialog_message_exclusions);
        dialog.setPositiveButton(R.string.button_back, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Come back and delete exclusions stored
                SharedPreferences exclusions = getSharedPreferences("exclusions",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = exclusions.edit();
                editor.clear().commit();
                finish();
            }
        });
        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Do nothing
            }
        });
        dialog.show();
    }

    /**
     * Actions when press No Exclusions
     */
    public void clickNo(View v)
    {
        //Elements actives or not
        spinner1.setEnabled(false);
        spinner2.setEnabled(false);
        cbBoth.setEnabled(false);
        btnAdd.setEnabled(false);
        cbBoth.setChecked(false);
        btnSee.setEnabled(false);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
    }

    /**
     * Actions when press Make Exclusions
     */
    public void clickYes(View v)
    {
        spinner1.setEnabled(true);
        spinner2.setEnabled(true);
        cbBoth.setEnabled(true);
        btnAdd.setEnabled(true);
        btnSee.setEnabled(true);
    }

    /**
     * Watch out made exclusions
     */
    public void seeExclusions(View v)
    {
        PreferencesExclusions ex = new PreferencesExclusions(this);

        //Call join method to show the exclusions
        String []list_see = ex.showExclusion();

        //Dialog to show exclusions already don in Shared Preferences
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exclusions");
        dialog.setItems(list_see, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                element(pos+1);
            }
        });//Show

        dialog.setNegativeButton(R.string.button_back, null);

        dialog.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                //Delete all exclusions (list and SharedPreferences)
                dialog.setItems(null, null);
                SharedPreferences exclusion = getSharedPreferences("exclusions",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = exclusion.edit();
                editor.clear().commit();
                editor.putInt("numExclusions", 0);
                editor.commit();
            }
        });

        dialog.show();
    }

    /**
     * Ask when click on exclusion
     */
    public void element(int position)
    {
        final PreferencesExclusions ex = new PreferencesExclusions(this);
        final int pos = position;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_delete_exclusion);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setNegativeButton(R.string.button_cancel, null);
        dialog.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Delete
                ex.deleteExclusion(pos);
                Toast.makeText(getBaseContext(), R.string.dialog_exclusion_deleted, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    /**
     * Add an exclusion to the list
     */
    public void addExclusion(View v)
    {
        PreferencesExclusions ex = new PreferencesExclusions(this);

        GenerateDraw gene = new GenerateDraw(this, nameGroup);
        String sel1 = spinner1.getSelectedItem().toString();
        int pos1 = (spinner1.getSelectedItemPosition())-1;
        String sel2 = spinner2.getSelectedItem().toString();
        int pos2 = (spinner2.getSelectedItemPosition())-1;
        boolean flag, right = false, left = false;

        if (sel1.equals("") || sel2.equals(""))
        {
            Toast.makeText(getBaseContext(), R.string.dialog_valid_names, Toast.LENGTH_SHORT).show();
        }
        else if(sel1.equals(sel2))
        {
            Toast.makeText(getBaseContext(), R.string.dialog_itself, Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (cbBoth.isChecked())
            {
                //Check if the exclusion exist in one direction or in both
                if (ex.checkExists(sel1, sel2)==false)
                {
                    //Exists to right
                    right = true;
                }
                if (ex.checkExists(sel2, sel1)==false)
                {
                    //Exists to left
                    left = true;
                }

                //Add the elements that does not exist to the list
                if ((right==true) && (left==true))
                {
                    //Already exist
                    Toast.makeText(getBaseContext(), R.string.dialog_exist_both, Toast.LENGTH_SHORT).show();
                }

                if ((right==true) && (left==false))
                {
                    //If exist to right add to left...
                    String [][]how_gift = new String[2][lstNames.length];
                    GenerateDraw gen = new GenerateDraw(this, nameGroup);

                    ex.addExclusion(sel2, sel1);

                    how_gift = gen.comp_exclusions();
                    if (how_gift == null)
                    {
                        int num = ex.getNumberExclusions();
                        ex.deleteExclusion(num);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), R.string.dialog_made_both_ways, Toast.LENGTH_SHORT).show();
                    }
                }
                if ((right==false) && (left==true))
                {
                    //If exists left, add right
                    String [][]how_gift = new String[2][lstNames.length];
                    GenerateDraw gen = new GenerateDraw(this, nameGroup);

                    ex.addExclusion(sel1, sel2);

                    how_gift = gen.comp_exclusions();
                    if (how_gift == null)
                    {
                        int num = ex.getNumberExclusions();
                        ex.deleteExclusion(num);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), R.string.dialog_made_both_ways, Toast.LENGTH_SHORT).show();
                    }
                }
                if ((right==false) && (left==false))
                {
                    //Not exists in any way
                    String [][]how_gift = new String[2][lstNames.length];
                    GenerateDraw gen = new GenerateDraw(this, nameGroup);

                    ex.addExclusion(sel1, sel2);
                    ex.addExclusion(sel2, sel1);

                    how_gift = gen.comp_exclusions();
                    if (how_gift == null)
                    {
                        int num = ex.getNumberExclusions();
                        ex.deleteExclusion(num);
                        int num1 = ex.getNumberExclusions();
                        ex.deleteExclusion(num1);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), R.string.dialog_made_both_ways, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                flag = ex.checkExists(sel1, sel2);

                if (flag)//If the exclusion does not exist...
                {
                    PreferencesExclusions exc = new PreferencesExclusions(this);
                    if (exc.getNumberExclusions() == 0)
                    {
                        ex.addExclusion(sel1, sel2);
                        Toast.makeText(getBaseContext(), R.string.dialog_exclusion_made, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String [][]how_gift = new String[2][lstNames.length];
                        GenerateDraw gen = new GenerateDraw(this, nameGroup);

                        ex.addExclusion(sel1, sel2);

                        how_gift = gen.comp_exclusions();
                        if (how_gift == null)
                        {
                            int num = ex.getNumberExclusions();
                            ex.deleteExclusion(num);
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(), R.string.dialog_exclusion_made, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), R.string.dialog_exclusion_exist, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Check on the moment to make the exclusion if we can do the draw
     */
    public int[] comp_possibility(int []gifted, String []origin, String []destiny, int parti)
    {
        gifted[parti]=2;//Itself no...

        for (int i=0; i<origin.length; i++)//For to pass all names of origin
        {
            if (lstNames[parti].equals(origin[i]))//Si coincide el participante con un nombre origen tiene exclusi�n
            {
                String name = destiny[i];//Guardamos a quien no puede regalar
                for(int j=0; j<lstNames.length; j++)//Bucle para buscar el participante en la lista
                {
                    if (lstNames[j].equals(name))//Cuando encontremos su posici�n
                    {
                        if (gifted[j] == 0)//En regalado, si puede ser regalado ponemos 2
                        {
                            gifted[j] = 2;
                        }
                        else
                        {

                        }
                    }
                }
            }
        }

        //Comprobaci�n de la validez de las exclusiones
        boolean possible = false;
        for (int j=0; j<gifted.length; j++)
        {
            if (gifted[j] == 0)//Si existe alg�n elemento a 0 se puede asociar a �l, con lo cual puede hacerse el sorteo de este elemento
            {
                possible = true;
            }
        }

        if (possible)//Si se pueden hacer exclusiones devolver� regalado para el sorteo
        {
            return gifted;
        }
        else//Si no hay participantes a ser elegidos, devolver� null, con lo cual no podremos realizar el sorteo
        {
            return null;
        }
    }

    /**
     * Go to Final Data
     */
    public void nextExclusions(View v)
    {
        if (rdNo.isChecked())
        {
            SharedPreferences exclusions=getSharedPreferences("exclusions",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = exclusions.edit();
            editor.clear().commit();
            editor.putInt("numExclusions", 0);
            editor.commit();
        }
        Intent finalData = new Intent(this, FinalData.class);
        finalData.putExtra("nameGroup", nameGroup);
        startActivity(finalData);
    }
}
