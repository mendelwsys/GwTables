package com.mwlib.tablo.test.cache;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 24.09.14
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class CheckHash {
    static String repl = "1234567890`qwertyuyiop[]asdfghjkl;'\\zxc|vbnm,./!@#$%$%^^&&*()_+=";
    static final int SIZET = 20000;

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

        Integer[] k = new Integer[200];
        {
            long ln = System.currentTimeMillis();
                for (int i = 0, astrLength = astr.length; i < astrLength; i++)
                    k[i % k.length] = astr[i].hashCode();
            System.out.println("ln = " + 1.0 * (System.currentTimeMillis() - ln) / 1000);
        }

        {

            long ln = System.currentTimeMillis();
            for (int i = 0, astrLength = ai.length; i < astrLength; i++)
                k[i % k.length] = ai[i].hashCode();
            System.out.println("ln = " + 1.0 * (System.currentTimeMillis() - ln) / 1000);
        }

    }
}
