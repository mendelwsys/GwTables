package com.mwlib.tablo.test.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 24.09.14
 * Time: 11:14
 * проверка генерации хэш функций
 */
public class CheckHash3
{
    static String repl = "1234567890`qwertyuyiop[]asdfghjkl;'\\zxc|vbnm,./!@#$%$%^^&&*()_+=";
    public static final int SIZET = 20000;

    public static void main(String[] args) {

        Integer[] ai = new Integer[SIZET];
        for (int i = 0; i < ai.length; i++)
            ai[i] = (int) (Math.random() * 100000);

        StringBuffer template = new StringBuffer("asjdlskjd;aslkd;als");
        String[] astr = new String[SIZET];
        for (int i = 0; i < astr.length; i++)
        {
            int ix = (int) (Math.random() * (repl.length()-1));
            int ix2 = (int) (Math.random() * (template.length()-1));
            astr[i] = (template.replace(ix2, ix2, repl.substring(ix, ix + 1))).toString();
        }


        {
            Map<String, String> checkHash = new ConcurrentHashMap<String, String>();
            String[] strings=new String[SIZET];

            long lnf1 = System.currentTimeMillis();
            for (int i = 0, astrLength = astr.length; i < astrLength; i++)
                astr[i].hashCode();
            System.out.println("lnf1 = " + 1.0 * (System.currentTimeMillis() - lnf1) / 1000);


            long lnf = System.currentTimeMillis();
            for (int i = 0, astrLength = astr.length; i < astrLength; i++)
                checkHash.put(astr[i],astr[i]);
            System.out.println("lnf = " + 1.0 * (System.currentTimeMillis() - lnf) / 1000);

            long ln = System.currentTimeMillis();
                for (int i = 0, astrLength = astr.length; i < astrLength; i++)
                {
                    int ix = (int) (Math.random() *(astr.length-1));
                    strings[i%strings.length]=checkHash.remove(astr[ix]);
                }
            System.out.println("ln = " + 1.0 * (System.currentTimeMillis() - ln) / 1000);
        }

        {
            Map<Integer, String> checkHash = new ConcurrentHashMap<Integer, String>();
            String[] strings=new String[SIZET];
            long lnf = System.currentTimeMillis();
            for (int i = 0, astrLength = ai.length; i < astrLength; i++)
                checkHash.put(ai[i],astr[i]);
            System.out.println("lnf = " + 1.0 * (System.currentTimeMillis() - lnf) / 1000);

            long ln = System.currentTimeMillis();
                for (int i = 0, astrLength = ai.length; i < astrLength; i++)
                {
                    int ix = (int) (Math.random() *(astr.length-1));
                    strings[i%strings.length]=checkHash.remove(ai[ix]);
                }
            System.out.println("ln = " + 1.0 * (System.currentTimeMillis() - ln) / 1000);
        }

    }
}
