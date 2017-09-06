package com.example.usuario.secretsanta.helpClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDBGroups extends SQLiteOpenHelper
{

    public CreateDBGroups(Context context, String name, CursorFactory factory, int version)
    {
        //Call constructor and gives the data to the superclass
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE groups (_id INTEGER PRIMARY KEY, nameGroup TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS groups");
        onCreate(db);
    }
}