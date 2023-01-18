package com.mycompany.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:58
 * Дескриптор операции, он же дескриптор окна или дескриптор фильтра  или группировки.
 */
public class DescOperation<T> implements Serializable
{



    public String apiName;

//    public DescOperation(String apiName, List<DescOperation<T>> subOperation, HashMap<String, GWTSuccs<T>> paramsHM)
//    {
//        this.apiName = apiName;
//        this.subOperation = subOperation;
//        this.paramsHM = paramsHM;
//    }
//
//    public DescOperation(DescOperation copyOfOperation)
//    {
//        this.apiName = copyOfOperation.apiName;
//        this.subOperation = copyOfOperation.subOperation;
//        this.paramsHM = copyOfOperation.paramsHM;
//    }

    public DescOperation() {
    }

    public List<DescOperation<T>> getSubOperation() {
        return subOperation;
    }

    public void setSubOperation(List<DescOperation<T>> subOperation) {
        this.subOperation = subOperation;
    }

    protected List<DescOperation<T>> subOperation = new LinkedList<DescOperation<T>>();


    public Map<String, DescOperation<T>> getDescMapParams() {
        return descMapParams;
    }

    public void setDescMapParams(Map<String, DescOperation<T>> descMapParams) {
        this.descMapParams = descMapParams;
    }

    protected Map<String,DescOperation<T>> descMapParams = new HashMap<String,DescOperation<T>>();

    public Map<String, GWTSuccs<T>> getParamsHM() {
        return paramsHM;
    }

    public void setParamsHM(Map<String, GWTSuccs<T>> paramsHM) {
        this.paramsHM = paramsHM;
    }

    protected Map<String,GWTSuccs<T>> paramsHM=new HashMap<String,GWTSuccs<T>>();

    public T get(String name)
    {
        GWTSuccs<T> gwtSuccs = paramsHM.get(name);
        if (gwtSuccs!=null)
            return gwtSuccs.val;
        return null;
    }

    public boolean contains(String name)
    {
        return paramsHM.containsKey(name);
    }


    public void put(String name,T obj)
    {
        paramsHM.put(name,new GWTSuccs<T>(obj));
    }

    public GWTSuccs<T> remove(String name)
    {
        return paramsHM.remove(name);
    }

//    public Object get(String name)
//    {
//        final Object[] objects = paramsHM.get(name);
//        if (objects!=null && objects.length>0)
//            return objects[0];
//        return null;
//    }
//
//    public void put(String name,Object obj)
//    {
//        paramsHM.put(name,new Object[]{obj});
//    }

//    public T get(String name)
//    {
//        return paramsHM.get(name);
//    }
//
//    public void put(String name,T obj)
//    {
//        paramsHM.put(name,obj);
//    }


}
