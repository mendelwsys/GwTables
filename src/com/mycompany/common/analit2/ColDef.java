package com.mycompany.common.analit2;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 14:35
 *
 */
public class ColDef implements Serializable
{
    public String getColName() {
        return colName;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHide() {
        return hide;
    }

    private String colName;
    private String title;
    private boolean hide;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    private String format;
    private String ftype;

    public String getZval() {
        return zval;
    }

    public void setZval(String zval) {
        this.zval = zval;
    }

    public String getNval() {
        return nval;
    }

    public void setNval(String nval) {
        this.nval = nval;
    }

    private String zval;
    private String nval;


    public void setColName(String colName) {
        this.colName = colName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public ColDef(){}

    public static ColDef[] convertArray(JavaScriptObject[] objects)
    {
        ColDef[] rv = new ColDef[objects.length];
        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++)
            rv[i]=new ColDef((JavaScriptObject)objects[i]);
        return rv;
    }

    public ColDef( String colName,String title, boolean hide,String format,String ftype) {
        this.title = title;
        this.colName = colName;
        this.hide = hide;
        this.ftype=ftype;
        this.format=format;
    }

    public ColDef(ColDef colDef) {
        this.title = colDef.title;
        this.colName = colDef.colName;
        this.hide = colDef.hide;
        this.ftype=colDef.ftype;
        this.format=colDef.format;
    }

    public ColDef(JavaScriptObject jsObj)
    {
        this.title = JSOHelper.getAttribute(jsObj, "title");
        this.colName = JSOHelper.getAttribute(jsObj, "colName");
        this.hide = JSOHelper.getAttributeAsBoolean(jsObj, "hide");

        this.format = JSOHelper.getAttribute(jsObj, "format");
        this.ftype = JSOHelper.getAttribute(jsObj, "ftype");

        this.zval = JSOHelper.getAttribute(jsObj, "zval");
        this.nval = JSOHelper.getAttribute(jsObj, "nval");

    }

//    public ColDef(Map jsObj)
//    {
//        this.title = (String)jsObj.get("title");
//        this.colName = (String)jsObj.get("colName");
//        this.hide = (Boolean)jsObj.get("hide");
//    }

}
