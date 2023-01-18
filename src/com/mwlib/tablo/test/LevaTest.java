package com.mwlib.tablo.test;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.03.15
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class LevaTest
{
    public static void main(String[] args)
    {




       int a=0;
       int b=1;
       int c=2;
       int d=3;

       boolean v[]= new boolean[]{false,false,false,false};

       for (int i=0;i<16;i++)
       {

        v[a]=((i&0x01)>0);
        v[b]=((i/2&0x01)>0);
        v[c]=((i/4&0x01)>0);
        v[d]=((i/8&0x01)>0);

        boolean res=(!v[a] || v[b]|| v[c])&&(v[b]||(!v[a]&&v[d]))&&(!v[d] || v[b] || v[c]) && (v[a] || !v[c]);

//           boolean res=(!v[a] || v[b]|| v[c])&&((v[b]||!v[a])&&v[d])&&(!v[d] || v[b] || v[c]) && (v[a] || !v[c]);

        System.out.print("res " + i + " =  " + res + "       for ");
           for (int j = 0; j < v.length; j++) {
               System.out.print(v[j]+" ");
           }
        System.out.println();
       }


    }
}
