package com.mycompany.common.analit2;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.10.14
 * Time: 14:21
 *
 */
public class GrpDef implements Serializable
{


    public Integer getColN() {
        return colN;
    }

    public void setColN(Integer colN) {
        this.colN = colN;
    }

    private Integer colN;

    public static String[] getTidsByGrpDef(GrpDef[] defs)
    {
        if (defs==null || defs.length==0)
            return new String[0];
        String[] rv = new String[defs.length];
        for (int i = 0; i < rv.length; i++)
             rv[i]=defs[i].getTid();
        return rv;
    }

    private String tid;
    private String tColId;


    public String getTid() {
        return tid;
    }

    public String gettColId() {
        return tColId;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void settColId(String tColId) {
        this.tColId = tColId;
    }


    public GrpDef(){}

    public GrpDef(JavaScriptObject jsObj)
    {
        this.tid = JSOHelper.getAttribute(jsObj, "tid");
        this.tColId = JSOHelper.getAttribute(jsObj, "tColId");
        this.colN = JSOHelper.getAttributeAsInt(jsObj, "colN");
    }

    public static GrpDef[] convertArray(JavaScriptObject[] objects)
    {
        if (objects==null)
            return null;
        GrpDef[] rv = new GrpDef[objects.length];
        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++)
            rv[i]=new GrpDef((JavaScriptObject)objects[i]);
        return rv;
    }


    public GrpDef(String tid, String tColId,Integer colN) {
        this.tid = tid;
        this.tColId = tColId;
        this.colN=colN;
    }




}
