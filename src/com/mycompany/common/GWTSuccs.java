package com.mycompany.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.12.14
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class GWTSuccs<T> implements Serializable
{
    public T val;

    public GWTSuccs(T val) {
        this.val = val;
    }

    public GWTSuccs() {
    }

    public String getNameType()
    {
        return val.getClass().getName();
    }


    public Map toMap()
    {
        Map<String,T> rv = new HashMap<String,T>();
        rv.put("val",val);
        return rv;
    }
}
