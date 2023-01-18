package com.mwlib.tablo.test;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 26.02.16
 * Time: 10:00
 * To change this template use File | Settings | File Templates.
 */
public class RetFinal
{
    public static void main(String[] args)
    {

        int a=checkRet();
        System.out.println("a = " + a);
    }

    private static int checkRet() {
        int a=0;

        try
        {
          throw new Exception();
        }
        catch(Exception e)
        {
           a = 11;
           return a;
        }
        finally
        {
            a = a+5;
            return a;
        }
    }
}
