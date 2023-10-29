package com.example.monitorapp;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class Common {
    private Context context;

    public Common(Context context) {
        this.context = context;
    }

    public double getdouble(String s) {
        s = s.trim();
        NumberFormat format = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
        //dirty fix
        if(((DecimalFormat)format).getDecimalFormatSymbols().getDecimalSeparator()==',')
        {
            s = s.replace('.',',');
        }
        if(((DecimalFormat)format).getDecimalFormatSymbols().getDecimalSeparator()=='.')
        {
            s = s.replace(',','.');
        }
        ////end of dirty fix
        if (s.equals("")) {
            return 0;
        } else {
            Number number = null;
            try {
                number = format.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return number.doubleValue();
        }
    }
}
