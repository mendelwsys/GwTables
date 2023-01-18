package com.mycompany.client.dojoChart;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.01.15
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class MyColorUtils
{
   public static String[] splitColor(int cnt)
   {
       if (cnt<=3) return new String[]{"red","green","blue"};
       double delta=0xFFFFFF/cnt;
       String[] rv=new String[cnt];
       double color=0;
       for (int i = 0; i < rv.length; i++)
       {
           rv[i]="#"+Integer.toHexString((int)Math.round(color+delta/2));
           color+=delta;
       }
       return rv;
   }
}
