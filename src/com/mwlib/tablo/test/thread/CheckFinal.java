package com.mwlib.tablo.test.thread;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.06.15
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class CheckFinal
{

    static class A
    {
        int[] a;
        public A(int[] a)
        {
            this.a = new int[a.length];
            for (int i = 0, aLength = a.length; i < aLength; i++)
            {
                this.a[i]=a[i];
            }
        }
    }


    static A[] x =new A[]{(new A(new int[]{0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF}))};

    public static void main(String[] args) throws Exception
    {
        Runnable r1 = new Runnable()
        {

            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {


                        for (int i=0;i<10000000;i++)
                             x[0] =new A(new int[]{0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF});
                    }
                    catch (Exception e)
                    {
                        //
                    }
                }
            }
        };

        new Thread(r1).start();


        Runnable runnable = new Runnable() {
            public void run()
            {
                try {
                    while (true)
                    {
                        for (int i=0;i<100000;i++)
                        {
                            int i1 = (x[0].a[4] & 0xFFFFFFFF);
                            if (i1!=0xFFFFFFFF)
                                System.out.println("i1 = " + Integer.toHexString(i1));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i=0;i<10;i++)
            new Thread(runnable).start();
//        new Thread(runnable).start();
//        new Thread(runnable).start();
//        new Thread(runnable).start();

    }
}
