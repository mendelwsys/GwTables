package com.mwlib.tablo.test.win;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.08.15
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class getIds
{
    public static void main(String[] args) throws Exception
    {
        StringBuffer buf = readFile("C:\\PapaWK\\Projects\\JavaProj\\SGWTVisual2\\db\\x1.txt");

        List<String> ids=new LinkedList<String>();
        int ix=0;
        final String str1 = "a href=\"card.jsp?wid=";
        final String str2 = "&dorKod=";

        while ((ix=buf.indexOf(str1,ix))>=0)
        {
            int ix2=buf.indexOf(str2,ix);
            ids.add(buf.substring(ix+str1.length(),ix2));
            ix = ix2;
        }

        StringBuffer buf2 = readFile("C:\\PapaWK\\Projects\\JavaProj\\SGWTVisual2\\db\\squirrel2.tmp");


        List<String> ids2=new LinkedList<String>();
        String res[]=buf2.toString().split("\r\n");
        for (String re : res)
        {
            int ix1=re.indexOf(";");
            re = re.substring(ix1+1);
            ix1=re.indexOf("##");
            re = re.substring(0,ix1);
            if (!ids2.contains(re))
                ids2.add(re);
            else
                System.out.println("double ");
//            if (!ids.contains(re))
//                System.out.println("re = " + re);
        }

        for (String id2 : ids2)
            if (!ids.contains(id2))
                System.out.println("in my system id2 = " + id2);

        for (String id : ids)
            if (!ids2.contains(id))
                System.out.println("in Broide system id = " + id);

        System.out.println("ids = " + ids.size());


    }

    private static StringBuffer readFile(String name) throws IOException {
        FileInputStream fis = new FileInputStream(name);
        final byte[] b = new byte[100 * 1024];
        int ln=0;
        StringBuffer buf=new StringBuffer();
        while((ln=fis.read(b))>0)
            buf.append(new String(b,0,ln, "WINDOWS-1251"));

        fis.close();
        return buf;
    }
}
