package com.mwlib.tablo.servlets;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.11.14
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class ServletUtils
{
    public static String setName(String dsName,String defName) {
        if (dsName==null)
            dsName= defName;
        else
            dsName=dsName.trim();
        return dsName;
    }

    public static boolean setTestByParams(String sTest,boolean test) {
        if (sTest!=null)
        {
            sTest=sTest.trim();
            test=sTest.equalsIgnoreCase("TRUE") || sTest.equalsIgnoreCase("1");
        }
        return test;
    }


    public static String setInitParamByParams(String sTest,String defVal) {
        if (sTest!=null)
        {
            sTest=sTest.trim();
            if (sTest.length()>0)
                return sTest;
        }
        return defVal;
    }


}
