package com.mycompany.common.analit;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class ColDef
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

    String colName;
    String title;
    boolean hide;

    public ColDef( String colName,String title, boolean hide) {
        this.title = title;
        this.colName = colName;
        this.hide = hide;
    }
}
