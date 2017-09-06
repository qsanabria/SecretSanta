package com.example.usuario.secretsanta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.secretsanta.helpClass.CheckEditText;
import com.example.usuario.secretsanta.helpClass.OperateDBParticipants;


public class ParticipantNew extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private EditText etNameParticipant, etEmailParticipant;
    private String nameGroup;
    private OperateDBParticipants db;
    private int pos_id = 0;
    private FloatingActionButton fabBack, fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_participant);

        //Get instances of all the elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView)findViewById(R.id.titleNewParticipant);
        etNameParticipant = (EditText)findViewById(R.id.etNameParticipant);
        etEmailParticipant = (EditText)findViewById(R.id.etEmailParticipant);
        fabBack= (FloatingActionButton)findViewById(R.id.fabBack);
        fabAdd= (FloatingActionButton)findViewById(R.id.fabAdd);

        setSupportActionBar(toolbar);//Set toolbar on the screen

        //Set personalized font
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Lemonada-Bold.ttf");
        title.setTypeface(face);

        Bundle bag = getIntent().getExtras();
        pos_id = bag.getInt("pos_id");
        nameGroup = bag.getString("nameGroup").toString();

        if (pos_id > 0)
        {
            db = new OperateDBParticipants(this, nameGroup, null, 1);

            try
            {
                db.openDB();
                String name = db.showElement(pos_id, "name");
                String email = db.showElement(pos_id, "email");
                db.closeDB();

                etNameParticipant.setText(name);
                etEmailParticipant.setText(email);
            }
            catch (Exception e)
            {
                Toast.makeText(this, R.string.error_show_participant, Toast.LENGTH_SHORT).show();
            }
        }

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParticipant();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Verify data fields and pass data to the 'parent' to add on data base
    public void addParticipant()
    {
        CheckEditText comp = new CheckEditText();

        if (comp.check(etNameParticipant) && comp.check(etEmailParticipant))
        {
            //Get data to the last activity to get on activityResult
            Intent i = new Intent(this, ParticipantNew.class);
            i.putExtra("nameParticipant", etNameParticipant.getText().toString());
            i.putExtra("emailParticipant", etEmailParticipant.getText().toString());
            String pos = Integer.toString(pos_id);
            i.putExtra("pos", pos);
            setResult(Activity.RESULT_OK, i);
            ParticipantNew.this.finish();//End Activity
        }
    }
}
