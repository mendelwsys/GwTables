package com.mwlib.tablo.test;

import com.mycompany.common.GWTSuccs;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.05.15
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public class DoubleTest
{
    static class A
    {
          int x=-1;
          int y=0;
    }

    public static void main(String[] args)
    {

        final Date date = new Date(1);
        long off=date.getTimezoneOffset();
        long ll= date.getTime();
        System.out.println("l = " + ll);

//        String vv=new GWTSuccs(new A()).getNameType();
//        System.out.println("vv = " + vv);

        double a=0.2;

        Long l=Double.doubleToLongBits(a);

        System.out.println("a = " + l);

    }
}
