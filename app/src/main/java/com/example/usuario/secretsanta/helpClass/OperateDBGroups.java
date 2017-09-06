package com.example.usuario.secretsanta.helpClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.usuario.secretsanta.R;

public class OperateDBGroups{

    private Context context;
    private String nameDB;
    private SQLiteDatabase.CursorFactory factory;
    private int version;
    private CreateDBGroups helper;
    private SQLiteDatabase db;

    //Get the elements given to local
    public OperateDBGroups(Context context, String nameDB, SQLiteDatabase.CursorFactory factory, int version)
    {
        this.context = context;
        this.nameDB = nameDB;
        this.factory = factory;
        this.version = version;
    }

    //Open database in mode read/write
    public OperateDBGroups openDB() throws SQLException
    {
        helper = new CreateDBGroups(context, nameDB, factory, version);
        db = helper.getWritableDatabase();
        return this;
    }

    //Close database and helper
    public void closeDB() throws SQLException
    {
        helper.close();
        db.close();
    }

    //Insert groups data on database
    public boolean insertElement(String nameGroup)
    {
        ContentValues register = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+nameDB, null);
        int id = 1;

        if (checkNameOnDB(nameGroup))
        {
            if ((cursor.moveToFirst())&&(cursor.getInt(0)>0))//Move cursor to the beginning and look for elements
            {
                id = cursor.getInt(0)+1;
            }

            try
            {
                register.put("_id", id);
                register.put("nameGroup", nameGroup);
                db.insert(nameDB, null, register);
                Toast.makeText(context, R.string.dialog_group_add, Toast.LENGTH_SHORT).show();
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(context, R.string.error_group_noAdd, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            Toast.makeText(context, R.string.dialog_group_duplicate, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Get back the data of the database
    public String[] getDataGroups()
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+nameDB, null);//Find out the elements number of database

        if ((cursor.moveToFirst())&&(cursor.getInt(0)>0))//Move cursor to the beginning and look for elements
        {
            int number = cursor.getInt(0);//Get the number of elements we got
            String[] list = new String[number];//Create an array with that number of elements
            int pos = 0;

            for(int i=1; i<=number; i++)
            {
                //Fill up the array
                cursor = db.rawQuery("SELECT nameGroup FROM "+nameDB+" WHERE _id="+i, null);
                if(cursor.moveToFirst())
                {
                    list[pos] = cursor.getString(0);//Get the name
                    pos++;
                }
            }
            cursor.close();
            return list;
        }
        else
        {
            return null;
        }
    }

    //Delete a register from database
    public void delete(int pos)
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+nameDB, null);
        int total;

        if((cursor.moveToFirst())&&(cursor.getInt(0)>0))
        {
            total = cursor.getInt(0);

            db.execSQL("UPDATE "+nameDB+" SET _id = '999' where _id = '"+pos+"'");

            for (int i=pos+1; i<=total; i++)
            {
                db.execSQL("UPDATE "+nameDB+" SET _id = '"+(i-1)+"' where _id = '"+i+"'");
            }
            db.execSQL("DELETE FROM "+nameDB+" where _id = '999'");
        }
        cursor.close();
    }

    public boolean checkNameOnDB(String nombre)
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+nameDB, null);
        int num;

        if (cursor.moveToFirst())
        {
            num = cursor.getInt(0);
            if (num > 0)
            {
                for(int i=1; i<=num; i++)
                {
                    cursor = db.rawQuery("SELECT nameGroup FROM "+nameDB+" WHERE _id="+i, null);
                    cursor.moveToFirst();
                    String str = cursor.getString(0);
                    if (str.equals(nombre))
                    {
                        return false;
                    }
                }
            }
            else
            {
                return true;
            }
        }
        else
        {
            Toast.makeText(context, R.string.error_dbCursor, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
