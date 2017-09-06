package com.example.usuario.secretsanta.helpClass;

import android.widget.EditText;

public class CheckEditText {
    public boolean check(EditText et)
    {
        int len = et.length();

        if (len<=0)
        {
            et.setError("This field cannot be empty!");
            return false;
        }
        else
        {
            et.setError(null);
            return true;
        }
    }
}
