package com.mwlib.utils.db;

import com.mycompany.common.TablesTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 24.11.14
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class CheckPredTree {


    static String getServByPRed(Directory.Pred pred)
    {
        String rv;
        Integer gr_id = pred.getGR_ID();
        if (gr_id==null)
            return TablesTypes.Z_SERVAL;

        switch (gr_id)
        {
            case 25:
            case 28:
                rv="П";
                break;
            case 70:
                rv="Ш";
                break;
            case 40:
                rv="Э";
                break;
            case 44:
                rv="В";
                break;
            default:
                rv=TablesTypes.Z_SERVAL;
        }
        return rv;

    }



    public static void main(String[] args) throws Exception
    {
        Directory.initDictionary(false);


        Collection<Directory.Pred> vals = Directory.predId2Pred.values();


        int cnt=0;


        for (Directory.Pred val : vals)
        {
            if (!getServByPRed(val).equals(TablesTypes.Z_SERVAL))
                cnt++;
        }

        System.out.println("cnt = " + cnt);

    }
}
