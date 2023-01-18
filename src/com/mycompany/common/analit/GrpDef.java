package com.mycompany.common.analit;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.10.14
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class GrpDef
{
    public static String[] getTidsByGrpDef(GrpDef[] defs)
    {
        if (defs==null || defs.length==0)
            return new String[0];
        String[] rv = new String[defs.length];
        for (int i = 0; i < rv.length; i++)
             rv[i]=defs[i].getTid();
        return rv;
    }



    public String getTid() {
        return tid;
    }

    public String gettColId() {
        return tColId;
    }

    String tid;

    public GrpDef(String tid, String tColId) {
        this.tid = tid;
        this.tColId = tColId;
    }

    String tColId;


}
