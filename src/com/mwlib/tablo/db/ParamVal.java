package com.mwlib.tablo.db;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class ParamVal
{
    int index;
    Object val;
    int sqlType;

    public ParamVal(int index, Object val, int sqlType) {
        this.index = index;
        this.val = val;
        this.sqlType = sqlType;
    }

    public int getIndex() {
        return index;
    }

    public Object getVal() {
        return val;
    }

    public int getSqlType() {
        return sqlType;
    }
}
