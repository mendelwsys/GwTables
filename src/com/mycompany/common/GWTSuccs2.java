package com.mycompany.common;


import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.12.14
 * Time: 18:31
 * Задолбало блядь!!!!!
 */
public class GWTSuccs2<T> extends HashMap<String,T>
{
    public GWTSuccs2(T val)
    {
        this.put("val",val);
    }
}
