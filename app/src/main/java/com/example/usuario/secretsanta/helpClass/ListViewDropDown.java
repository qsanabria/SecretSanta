package com.example.usuario.secretsanta.helpClass;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewDropDown {

    private ArrayList<String> listArray;
    private ArrayAdapter<String> adapter;
    private Activity activity;

    public ListViewDropDown(Activity activity, ListView list)//Constructor, we receive actual context and list to do changes
    {
        this.activity = activity;
        listArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, listArray);
        list.setAdapter(adapter);
    }

    //Add an string to the listView on the next position
    public void addList(String str)
    {
        listArray.add(str);
        adapter.notifyDataSetChanged();
    }

    //Add an string to the listView on the indicated position
    public void addPositionList(int pos, String str)
    {
        listArray.add(pos-1, str);
        adapter.notifyDataSetChanged();
    }

    //Delete a specific position
    public void deletePositionList(int pos)
    {
        listArray.remove(pos-1);
        adapter.notifyDataSetChanged();
    }

    //Delete a complete list
    public void deleteList()
    {
        adapter.clear();
    }

    //Get back the element on the position 'pos'
    public String getElementList(int pos)
    {
        return (listArray.get(pos));
    }
}
