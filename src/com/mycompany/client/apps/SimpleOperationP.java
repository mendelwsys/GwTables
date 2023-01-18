package com.mycompany.client.apps;

import com.mycompany.client.operations.IOperationParam;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:53
 * Элементарная операция содержащая параметры
 */
public class SimpleOperationP extends SimpleOperation
        implements IOperationParam
{

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    private List params= new LinkedList();

    public SimpleOperationP(int operationId, int parentOperationId,String viewName)
    {
        super(operationId, parentOperationId, viewName, TypeOperation.addFilter);
    }

    public SimpleOperationP(int operationId, int parentOperationId,String viewName,TypeOperation type)
    {
        super(operationId,parentOperationId,viewName,type);
    }

    public String getStringParam()
    {
        String filterParam = null;
        List params = getParams();
        if (params!=null && params.size()>0)
            filterParam = (String) params.get(0);
        return filterParam;
    }

    public void setStringParam(String param)
    {
        List params = getParams();
        if (params==null)
            setParams(params= new LinkedList());
        params.clear();
        params.add(param);
    }



//    public Integer getIntParam()
//    {
//        Integer filterParam = null;
//        List params = getParams();
//        if (params!=null && params.size()>0)
//            filterParam = (Integer) params.get(0);
//        return filterParam;
//    }
//
//    public void setIntParam(Integer param)
//    {
//        List params = getParams();
//        if (params==null)
//            setParams(params= new LinkedList());
//        params.clear();
//        params.add(param);
//    }

}

