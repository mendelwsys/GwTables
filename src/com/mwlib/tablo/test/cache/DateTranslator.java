package com.mwlib.tablo.test.cache;

import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.12.14
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class DateTranslator
{
    static class S
    {
        Date date;
    }

    public static void main(String[] args)
    {

        Date date = new Date(1871 - 1900, 0, 18);

        S s = new S();
        s.date=date;

//        String str=date.toString();
//        EventUtils.toJson(date);

        String res = new GsonBuilder().serializeNulls().setDateFormat(DateFormat.LONG).create().toJson(s);



        System.out.println("res = " + res);
    }
}
