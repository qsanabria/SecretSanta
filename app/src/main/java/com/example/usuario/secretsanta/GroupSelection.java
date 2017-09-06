package com.example.usuario.secretsanta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.secretsanta.helpClass.CheckEditText;
import com.example.usuario.secretsanta.helpClass.ListViewDropDown;
import com.example.usuario.secretsanta.helpClass.OperateDBGroups;
import com.example.usuario.secretsanta.helpClass.OperateDBParticipants;


public class GroupSelection extends AppCompatActivity implements android.widget.AdapterView.OnItemClickListener{

    private int code1 = 998, code2 = 997;
    private Toolbar toolbar;
    private TextView title;
    private EditText etSelectedGroup;
    private ListView lvGroups;
    private OperateDBGroups db;
    private ListViewDropDown lstVGroups;
    private String []lstNames;
    private int i, j;
    private FloatingActionButton fabBack, fabNext;
    private OperateDBParticipants db_part;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_group);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView)findViewById(R.id.titleSelectionGroup);
        etSelectedGroup = (EditText)findViewById(R.id.etGroupSelected);
        lvGroups = (ListView)findViewById(R.id.listGroups);
        fabBack = (FloatingActionButton)findViewById(R.id.fabBack);
        fabNext = (FloatingActionButton)findViewById(R.id.fabNext);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);

        db = new OperateDBGroups(this, "groups", null, 1);//Open db if exists or create if not

        //New instance of ListViewDropDown to create a listView and add elements
        lstVGroups = new ListViewDropDown(this, lvGroups);

        //Check if exists elements or not on the db...
        db.openDB();//Open DB
        lstNames = db.getDataGroups();//Get back the elements we got on db
        if (lstNames != null)//If we got data in...
        {
            for (i=0; i<lstNames.length; i++)
            {
                String str = lstNames[i];
                lstVGroups.addList(str);//Add to listView
            }
        }
        else
        {
            i = 0;
        }
        db.closeDB();//Close DB

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Exclusions
                goExclusions(view);
            }
        });

        //Functionality to press elements of listView
        lvGroups.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Intent createGroup = new Intent(this, GroupNew.class);

        String select = getString(R.string.button_select);
        String modify = getString(R.string.button_modify);
        String delete = getString(R.string.button_delete);
        String cancel = getString(R.string.button_cancel);
        final String []elements = {select , modify, delete, cancel};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final int pos = i;

        dialog.setTitle(R.string.dialog_group_choices);
        dialog.setItems(elements, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                switch (item)
                {
                    case 0://Select
                        String selection = lstNames[pos];
                        etSelectedGroup.setText(selection);
                        break;
                    case 1://Modify
                        String mod = lstNames[pos];
                        createGroup.putExtra("nameGroup", mod);
                        createGroup.putExtra("elimination", 0);//Flag for not delete the group selecting back
                        startActivityForResult(createGroup, code2);
                        break;
                    case 2://Delete
                        String del = lstNames[pos];
                        deleteG(del, pos+1);
                        break;
                    case 3://Cancel
                        //Do nothing
                        break;
                }
            }
        });
        dialog.show();
    }

    //Creation of a new group asking for a new name
    public void createNewGroup(View view){
        final Intent createGroup = new Intent(this, GroupNew.class);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_name_new_group);
        dialog.setIcon(android.R.drawable.ic_input_get);
        final EditText group = new EditText(this);//Create an EditText
        dialog.setView(group);

        dialog.setPositiveButton(R.string.button_continue, new  DialogInterface.OnClickListener()
        { //Click 'continue'
            public void onClick(DialogInterface dialog, int whichButton)
            {
                //Hide keyboard
                InputMethodManager hideKey = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                hideKey.hideSoftInputFromWindow(group.getWindowToken(), 0);

                //Do functionality
                CheckEditText comp = new CheckEditText();
                if (comp.check(group))
                {
                    String name = group.getText().toString();
                    boolean bool = false;//Init false, if any group with the same name we put true

                    if (lstNames != null)
                    {
                        for (j=0; j<lstNames.length; j++)
                        {
                            if (name.equals(lstNames[j]))
                            {
                                bool = true;
                            }
                        }

                        if (!bool)//False, zero groups with the same name
                        {
                            //Go to group creation
                            createGroup.putExtra("nameGroup", group.getText().toString());
                            createGroup.putExtra("elimination", 1);//Send this flag to exit of createGroup
                            startActivityForResult(createGroup, code1);
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(), R.string.dialog_name_used, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        //Go to group creation, group list empty
                        createGroup.putExtra("nameGroup", group.getText().toString());
                        createGroup.putExtra("elimination", 1);//Send this flag to exit of createGroup
                        startActivityForResult(createGroup, code1);
                    }

                }
                else
                {
                    Toast.makeText(getBaseContext(), R.string.error_etGroup, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton(R.string.button_cancel, new  DialogInterface.OnClickListener()
        { //Click 'cancel'
            public void onClick(DialogInterface dialog, int whichButton)
            {
                //Hide keyboard
                InputMethodManager hideKey = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                hideKey.hideSoftInputFromWindow(group.getWindowToken(), 0);
            }
        });
        dialog.show();
    }

    //Action generated when we come back from creation of a new group
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == code1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String name = data.getExtras().getString("name_of_group").toString();

                //Open, insert and close DB, if is correct go to if sentence
                db.openDB();
                boolean flag = db.insertElement(name);
                db.closeDB();

                if (flag)
                {
                    //This way cos if not an error on db and list appear
                    //Delete listView and re-write
                    lstVGroups.deleteList();
                    //Check if exists elements on DB
                    db.openDB();//Open DB
                    lstNames = db.getDataGroups();//Store all the names on DB
                    if (lstNames != null)//If stored elements...
                    {
                        for (i=0; i<lstNames.length; i++)
                        {
                            String str = lstNames[i];
                            lstVGroups.addList(str);//Add to listView
                        }
                        i = lstNames.length;
                    }
                    else
                    {
                        i = 0;
                    }
                    db.closeDB();//Close DB
                }
            }
        }

        if (requestCode == code2)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Toast.makeText(getBaseContext(), R.string.dialog_group_modified, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Delete the group and elements associated
     */
    public void deleteG(String del, int pos)
    {
        db_part = new OperateDBParticipants(this, del, null, 1);

        try
        {
            db_part.deleteDBComplete();//Delete the full DB with the name given
            db.openDB();
            db.delete(pos); //Delete the DB of the group with the position given

            //Delete listView and rewrite
            lstVGroups.deleteList();
            //Check if exists elements
            lstNames = db.getDataGroups();
            if (lstNames != null)
            {
                for (i=0; i<lstNames.length; i++)
                {
                    String str = lstNames[i];
                    lstVGroups.addList(str);
                }
                i = lstNames.length;
            }
            else
            {
                i = 0;
            }
            db.closeDB();
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), R.string.error_deleting_group, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Go to exclusions after select a group
     */
    public void goExclusions(View view)
    {
        CheckEditText comp = new CheckEditText();

        if (comp.check(etSelectedGroup))//Go if a group is selected
        {
            Intent ex = new Intent(this, Exclusions.class);
            ex.putExtra("nameGroup", etSelectedGroup.getText().toString());
            ex.putExtra("firstTime", true);
            startActivity(ex);
        }
        else{
            Toast.makeText(getBaseContext(), R.string.dialog_choose_group, Toast.LENGTH_LONG).show();
        }
    }
}
