package com.mwlib.tablo.test.tables;

import com.mycompany.common.Pair;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 12:30
 *
 */
public class DataSupply
{
    public static  DataSupply inst;
    private Pair prs;


    public ColumnHeadBean[] getHeaders()
    {
        if (prs!=null)
            return (ColumnHeadBean[])prs.first;
        else
            return new ColumnHeadBean[0];
    }

    public Map[] getData()
    {
        if (prs!=null)
            return (Map[])prs.second;
        else
            return new Map[0];
    }

    public static DataSupply getInstance(String dname) throws Exception
    {
//        try {
//            if (inst==null)
//            {
//                inst = new DataSupply();
//                inst.prs= FillFromDb._getDbTable();
//            }
//            return inst;
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        return inst=null;
    }


}
