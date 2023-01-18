package com.mwlib.tablo.test.derby;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 16:36
 *
 */
public class TestSpeed
{
    public static void main(String[] args)
    {
        long l=System.currentTimeMillis();
        Map<Integer,String> map= new HashMap<Integer,String>();
        for (int i=0;i<83699;i++)
            map.put(i,""+i);
        System.out.println("l = " + (System.currentTimeMillis()-l)+" map: "+map.size());
    }

    public static void _main(String[] args)
    {

        List<Object[]> ll = new LinkedList<Object[]>();
        for (int tpl=0;tpl<2000;tpl++)
        {
            Object[] e = new Object[200];
            e[0]= Math.random();
            Set<Double> doubles = new HashSet<Double>();
            e[1]= doubles;
            for (int hsi=0;hsi<10;hsi++)
                doubles.add(Math.random());
            ll.add(e);
        }



        int res=0;
        HashSet<Double> doubles = new HashSet<Double>();
        for (int hsi=0;hsi<10;hsi++)
            doubles.add(Math.random());

        long mils = System.currentTimeMillis();
        for (int cli=0;cli<100;cli++)
            for (Object[] objects : ll)
            {
                if (((Double)objects[0])<0.4)
                    res++;
                ((Set<Double>)objects[1]).addAll(doubles);
            }
        System.out.println(1.0*(System.currentTimeMillis()-mils)/1000);

    }
}
