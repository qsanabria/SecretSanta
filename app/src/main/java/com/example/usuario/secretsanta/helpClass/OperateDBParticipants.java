package com.example.usuario.secretsanta.helpClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.usuario.secretsanta.R;

public class OperateDBParticipants{

    private CreateDBParticipants helper;
    private Context context;
    private String name_db;
    SQLiteDatabase db;
    private SQLiteDatabase.CursorFactory factory;
    private int version;

    //Get parameters on local
    public OperateDBParticipants(Context context, String name_db, SQLiteDatabase.CursorFactory factory, int version)
    {
        this.context = context;
        this.name_db = name_db;
        this.factory = factory;
        this.version = version;
    }

    //Open database
    public OperateDBParticipants openDB() throws SQLException
    {
        helper = new CreateDBParticipants(context, name_db, factory, version);
        db = helper.getWritableDatabase();
        return this;
    }

    //Close DB and helper
    public void closeDB() throws SQLException
    {
        helper.close();
        db.close();
    }

    //Add participant data to db
    public boolean insertElement(String name, String email)
    {
        ContentValues register = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+name_db, null);
        int id = 1;

        if (checkName(name))
        {
            if ((cursor.moveToFirst())&&(cursor.getInt(0)>0))//Move Cursor to the beginning and count if we got elements
            {
                id = cursor.getInt(0)+1;
            }

            try
            {
                register.put("_id", id);
                register.put("name", name);
                register.put("email", email);
                db.insert(name_db, null, register);
                Toast.makeText(context, R.string.dialog_participant_ok, Toast.LENGTH_SHORT).show();
                return true;
            }
            catch (Exception e)
            {
                Toast.makeText(context, R.string.error_adding_participant, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else
        {
            Toast.makeText(context, R.string.dialog_participant_duplicate, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Show field of participant in db to modify
    public String showElement(int pos_id, String element)
    {
        String ret = null;

        Cursor cursor = db.rawQuery("SELECT "+element+" FROM "+name_db+" where _id = '"+pos_id+"'", null);
        if (cursor.moveToFirst())
        {
            ret = cursor.getString(0);
        }
        cursor.close();
        return ret;
    }

    public String[] getData()
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+name_db, null);//Look for number of elements

        if ((cursor.moveToFirst())&&(cursor.getInt(0)>0))
        {
            int number = cursor.getInt(0);//Get number of elements we got
            String[] list = new String[number];//Creation array with number of elements on db
            int pos = 0;

            for(int i=1; i<=number; i++)
            {
                //Fill up list with data from db
                cursor = db.rawQuery("SELECT name FROM "+name_db+" WHERE _id="+i, null);
                if(cursor.moveToFirst())
                {
                    list[pos] = cursor.getString(0);//Get name
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

    //Get Email with a name in the parameters
    public String getEmail(String name)
    {
        String email = null;

        Cursor cursor = db.rawQuery("SELECT email FROM "+name_db+" where name = '"+name+"'", null);
        if (cursor.moveToFirst())
        {
            email = cursor.getString(0);
        }
        cursor.close();
        return email;
    }

    //Modify data of participant
    public boolean updateParticipant(String name, String email, int pos_id)
    {
        try
        {
            db.execSQL("UPDATE "+name_db+" SET name = '"+name+"', email = '"+email+"' where _id ="+pos_id);
            return true;
        }
        catch (Exception e)
        {
            Toast.makeText(context, R.string.error_adding_participant, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Delete one register of database
    public void deleteElementDB(int pos)
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+name_db, null);
        int total;

        if((cursor.moveToFirst())&&(cursor.getInt(0)>0))
        {
            total = cursor.getInt(0);

            db.execSQL("UPDATE "+name_db+" SET _id = '999' where _id = '"+pos+"'");

            for (int i=pos+1; i<=total; i++)
            {
                db.execSQL("UPDATE "+name_db+" SET _id = '"+(i-1)+"' where _id = '"+i+"'");
            }
            db.execSQL("DELETE FROM "+name_db+" where _id = '999'");
        }
        cursor.close();
    }

    //Method to check if the db has more than 3 elements
    public int num_elements()
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+name_db, null);
        int num;

        if((cursor.moveToFirst())&&(cursor.getInt(0)>0))
        {
            num = cursor.getInt(0);
            cursor.close();
            return num;
        }
        else
        {
            cursor.close();
            return 0;
        }
    }

    public void deleteDBFile()
    {
        db.delete(name_db, null, null);
    }

    public void deleteDBComplete()
    {
        context.deleteDatabase(name_db);
    }

    public boolean checkName(String name)
    {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+name_db, null);
        int num;

        if (cursor.moveToFirst())
        {
            num = cursor.getInt(0);
            if (num > 0)
            {
                for(int i=1; i<=num; i++)
                {
                    cursor = db.rawQuery("SELECT name FROM "+name_db+" WHERE _id="+i, null);
                    cursor.moveToFirst();
                    String str = cursor.getString(0);
                    if (str.equals(name))
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
