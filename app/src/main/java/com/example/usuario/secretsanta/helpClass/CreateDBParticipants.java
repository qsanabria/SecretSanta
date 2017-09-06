package com.example.usuario.secretsanta.helpClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDBParticipants extends SQLiteOpenHelper{

    private String name;

    public CreateDBParticipants(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        //Call constructor and superclass
        super(context, name, factory, version);
        this.name = name;
    }

    //Create DB if not exists
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+name+" (_id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL)");
    }

    //Any case of upgrades, delete and create again
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+name);
        onCreate(db);
    }
}
