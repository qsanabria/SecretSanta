package com.example.usuario.secretsanta.helpClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.usuario.secretsanta.R;

public class PreferencesExclusions{

    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences exclusions;

    public PreferencesExclusions(Context context)
    {
        this.context = context;
        exclusions = context.getSharedPreferences("exclusions", Context.MODE_PRIVATE);
    }

    public int getNumberExclusions()
    {
        int num = exclusions.getInt("numExclusions", 0);
        return num;
    }

    public String[] getOrigin()
    {
        String []org;
        int num = getNumberExclusions();
        org = new String[num];

        for (int i=0; i<num; i++)
        {
            org[i] = exclusions.getString("origin"+(i+1), "");
        }

        return org;
    }

    public String[] getDestiny()
    {
        String []des;
        int num = getNumberExclusions();
        des = new String[num];

        for (int i=0; i<num; i++)
        {
            des[i] = exclusions.getString("destiny"+(i+1), "");
        }

        return des;
    }

    public void addExclusion(String o, String d)
    {
        int num = getNumberExclusions();
        editor = exclusions.edit();

        try
        {
            editor.putInt("numExclusions", num+1);
            editor.putString("origin"+(num+1), o);
            editor.putString("destiny"+(num+1), d);
            editor.commit();
        }
        catch (Exception e)
        {
            Toast.makeText(context, R.string.error_adding_excl, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkExists(String sel1, String sel2)
    {
        int num = getNumberExclusions();
        String []o = new String[num];
        String []d = new String[num];
        o = getOrigin();
        d = getDestiny();
        boolean flag = true;

        //Check if already exists that exclusion to do not write again
        for (int i=0; i<num; i++)
        {
            if (sel1.equals(o[i]))
            {
                if (sel2.equals(d[i]))
                {
                    flag = false;//Already exists
                    return flag;
                }
                return flag;
            }
        }
        return flag;
    }

    public String[] showExclusion()
    {
        //Get in two lists the elements origin and destiny (who's not gifting and to who)
        String []list1 = getOrigin();
        String []list2 = getDestiny();

        //Join both list to show with a visual effect
        int num = getNumberExclusions();
        String []list = new String[num];

        for(int i=0; i<num; i++)
        {
            //Join
            list[i] = list1[i] +" --(no)--> "+ list2[i];
        }
        return list;
    }

    public void deleteExclusion(int pos)
    {
        editor = exclusions.edit();
        int num = getNumberExclusions();
        String []list_o = getOrigin();
        String []list_d = getDestiny();

        String []list_o_final = new String[num-1];
        String []list_d_final = new String[num-1];

        if (num == 1)
        {
            editor.clear();
            editor.putInt("numExclusions", 0);
            editor.commit();
        }
        else
        {
            editor.clear();
            editor.putInt("numExclusions", 0);
            editor.commit();

            for (int i=0, j=0; i<(num-1); i++, j++)
            {
                if (i == (pos-1))
                {
                    j++;
                }
                list_o_final[i] = list_o[j];
                list_d_final[i] = list_d[j];
            }

            for (int i=0; i<(num-1); i++)
            {
                addExclusion(list_o_final[i], list_d_final[i]);
            }
        }
    }
}
