package com.mwlib.tablo.test.thread;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.06.15
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class CheckFinal_BU
{

    static class A
    {
        Integer a;
        Integer b;
        public A(int a,int b)
        {
            this.a=a;
            this.b=b;
        }
    }

    static A[] ref2A = new A[10000];

    static
    {
        for (int i=0;i<ref2A.length;i++)
             ref2A[i]=new A(i,i+1);
    }

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
                        {
                             int ix=i%ref2A.length;
                             ref2A[ix]=new A(ix,ix+1);
                        }
//                        Thread.sleep(100);
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
                            int ix=i%ref2A.length;
                             if (ref2A[ix].a==null || ref2A[ix].b==null)
                                 System.out.println("ref2A = " + ref2A[ix]);

                        }
//                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

    }
}
