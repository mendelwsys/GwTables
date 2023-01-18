package com.mwlib.tablo.test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.03.15
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class LevaTest2
{
    public static void main(String[] args)
    {

        int res=1;
        int[] a={2,7,5,5,3,3,3};

        Set<Integer> hs = new HashSet<Integer>();
        hs.add(1);

        int x=1;
        for (int i=0;i<0x7F;i++)
        {
            res=1;
            for (int j=0;j<7;j++)
            {
                if (((x>>j)&0x01)!=0)
                    res*=a[j];
            }
            hs.add(res);
            x+=1;
        }

        System.out.println("res = " + hs.size());
    }
}
