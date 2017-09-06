package com.example.usuario.secretsanta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.secretsanta.helpClass.ListViewDropDown;
import com.example.usuario.secretsanta.helpClass.OperateDBParticipants;

public class GroupNew extends AppCompatActivity implements android.widget.AdapterView.OnItemClickListener{

    private TextView title;
    private Toolbar toolbar;
    private EditText etNameGroup;
    private ListView list;
    private String nameGroup;
    private int elimination, i, codeNew = 999, cod = 998;
    private OperateDBParticipants db;
    private ListViewDropDown lvParticipants;
    private String []listNames;
    private FloatingActionButton fabBack, fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView)findViewById(R.id.titleNewGroup);
        etNameGroup = (EditText)findViewById(R.id.etNewGroup);
        list = (ListView)findViewById(R.id.listParticipants);
        fabBack= (FloatingActionButton)findViewById(R.id.fabBack);
        fabAdd= (FloatingActionButton)findViewById(R.id.fabAdd);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);

        Bundle bag = getIntent().getExtras();//Get the bag where we put the name of group
        nameGroup = bag.getString("nameGroup").toString();//Get the name of group from the bag
        elimination = bag.getInt("elimination");//Get the flag to back button
        etNameGroup.setText(nameGroup);//Put the name group to EditText

        db = new OperateDBParticipants(this, nameGroup, null, 1);//Open db or create if not exists

        //New instance of ListView to add elements
        lvParticipants = new ListViewDropDown(this, list);

        //Check if exists elements on database...
        db.openDB();//Open DB
        listNames = db.getData();//Store in a variable the names of db
        if (listNames != null)//If any elements...
        {
            for (i=0; i<listNames.length; i++)
            {
                String str = listNames[i];
                lvParticipants.addList(str);//Add to ListView
            }
            i = listNames.length;
        }
        else
        {
            i = 0;
        }
        db.closeDB();//Close DB

        list.setOnItemClickListener(this);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backScreen(view);
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, final View view, int ip, long l){

        String modify = getString(R.string.button_modify);
        String delete = getString(R.string.button_delete);
        String cancel = getString(R.string.button_cancel);
        final String []elements = {modify, delete, cancel};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final int pos = ip;

        dialog.setTitle(R.string.dialog_participant_choices);
        dialog.setItems(elements, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                switch (item)
                {
                    case 0://Modify
                        modify(view, pos+1);//modify the element with the data stored
                        break;
                    case 1://Delete
                        del(pos+1);//Delete the element
                        break;
                    case 2://Cancel
                        break;
                }
            }
        });
        dialog.show();
    }

    //Go to the last screen, method to back phone button
    public void onBackPressed()
    {
        if (elimination == 1)
        {
            View l = new View(this);
            backScreen(l);
        }
        else
        {
            //If we are in modification, give us just add choice
            Toast.makeText(getBaseContext(), R.string.dialog_just_add, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Go back to group selection screen
     */
    public void backScreen(View view)
    {
        if (elimination == 1)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);//Create a AlertDialog
            dialog.setTitle(R.string.dialog_out_creation_group);//Title
            dialog.setIcon(android.R.drawable.ic_dialog_alert);//Icon

            dialog.setPositiveButton(R.string.button_exit, new DialogInterface.OnClickListener()//Button Exit
            {
                @Override
                public void onClick(DialogInterface dial, int id)
                {
                    if (elimination == 1)//Delete Group
                    {
                        db.deleteDBComplete();
                    }
                    //Get out from group creation
                    finish();
                }
            });
            dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()//Button Cancel
            {
                @Override
                public void onClick(DialogInterface dial, int id)
                {
                    //No hace nada
                }
            });
            dialog.show();
        }
        else
        {
            //Case modify, just valid add
            Toast.makeText(getBaseContext(), R.string.dialog_just_add, Toast.LENGTH_SHORT).show();
        }
    }

    //Go to activity to add participant
    public void newParticipant(View view)
    {
        Intent newP = new Intent(this, ParticipantNew.class);
        newP.putExtra("pos_id", 0);
        newP.putExtra("nameGroup", nameGroup);
        this.startActivityForResult(newP, codeNew);
    }

    //onActivityResult when the activity is coming back from add Part. or modify Part.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Case New
        if (requestCode == codeNew)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                //Get data of the new Part. to store on the DB
                String name = data.getExtras().getString("nameParticipant").toString();
                String email = data.getExtras().getString("emailParticipant").toString();

                try
                {
                    //Open, insert, close DB and if everything is done correctly do if
                    db.openDB();
                    boolean flag = db.insertElement(name, email);
                    db.closeDB();

                    if (flag)
                    {
                        lvParticipants.addList(name);//Add name to listView
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getBaseContext(), R.string.error_storage, Toast.LENGTH_SHORT).show();
                }
            }
        }
        //Case Modify
        if (requestCode == cod)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                //Get data from modify element to add on DB
                String name = data.getExtras().getString("nameParticipant").toString();
                String email = data.getExtras().getString("emailParticipant").toString();
                //Here, get the position as well to modify the element on DB and on the listView
                String posString = data.getExtras().getString("pos").toString();

                int pos = Integer.parseInt(posString);//Convert to Int

                try
                {
                    //Open, insert, close DB and if everything is done correctly do if
                    db.openDB();
                    boolean flag = db.updateParticipant(name, email, pos);
                    db.closeDB();

                    if (flag)
                    {
                        lvParticipants.deletePositionList(pos);//Delete the participant on the position of the list
                        lvParticipants.addPositionList(pos, name);//Add the new onw
                        Toast.makeText(getBaseContext(), R.string.dialog_participant_ok, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(this, R.string.error_storage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Show data with the element we want to modify
     */
    public void modify(View view, int pos_id)
    {
        Intent i = new Intent(this, ParticipantNew.class);
        i.putExtra("pos_id", pos_id);//Pass the pos of the element to get the data
        i.putExtra("nameGroup", nameGroup);//Name of group to open DB
        this.startActivityForResult(i, cod);
    }

    /**
     * Delete the element selected
     */
    public void del(int pos)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final int place = pos;
        dialog.setTitle(R.string.dialog_sure);
        dialog.setPositiveButton(R.string.button_cancel, new DialogInterface.OnClickListener()//No delete, cancel
        {
            @Override
            public void onClick(DialogInterface dial, int id)
            {
                //Do nothing
            }
        });
        dialog.setNegativeButton(R.string.button_delete, new DialogInterface.OnClickListener()//Delete, delete
        {
            @Override
            public void onClick(DialogInterface dial, int id)
            {
                //Delete from DB its position and the full ListView and rewrite the listView
                db.openDB();
                db.deleteElementDB(place);
                lvParticipants.deleteList();

                String []names = db.getData();
                if (names != null)
                {
                    for (i=0; i<names.length; i++)
                    {
                        String str = names[i];
                        lvParticipants.addList(str);//Add ListView
                    }
                    i = names.length;
                }
                else
                {
                    i = 0;
                }
                db.closeDB();
            }
        });
        dialog.show();
    }

    /**
     * Delete all elements from the group
     */
    public void clear(View v)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.dialog_clear_list);
        dialog.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()//Delete
        {
            @Override
            public void onClick(DialogInterface dial, int id)
            {
                db.openDB();
                db.deleteDBFile();
                db.closeDB();
                lvParticipants.deleteList();
            }
        });
        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()//Cancel
        {
            @Override
            public void onClick(DialogInterface dial, int id)
            {
                //Do nothing
            }
        });
        dialog.show();
    }

    /**
     * Verify the number of elements to have 3 at least and add the group to the database
     */
    public void createGroup(View v)
    {
        db.openDB();
        int flag = db.num_elements();
        db.closeDB();
        if (flag>=3)
        {
            Intent i = new Intent(this, GroupSelection.class);
            i.putExtra("name_of_group", etNameGroup.getText().toString());
            setResult(Activity.RESULT_OK, i);
            GroupNew.this.finish();
        }
        else
        {
            Toast.makeText(this, R.string.dialog_3elements, Toast.LENGTH_SHORT).show();
        }
    }
}
