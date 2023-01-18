package com.mycompany.client.operations;

import com.mycompany.client.apps.App.ICopyObject;
import com.mycompany.client.apps.App.api.IOperationContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.02.15
 * Time: 11:37
 * Контекст операции
 */
public class OperationCtx implements IOperationContext
{

//    @Override
//    public void setSrc(Object src) {
//        this.src = src;
//    }
//
//    @Override
//    public void setDst(Object dst) {
//        this.dst = dst;
//    }

    private Object src;
    private Object dst;

    public Map getParams() {
        return params;
    }

    private Map params=new HashMap();

    public OperationCtx(Object src, Object dst)
    {
        this.src = src;
        this.dst = dst;
    }

    public OperationCtx(Object src, Object dst, List<IOperationContext> childList) {
        this(src,dst);
        this.childList = childList;
    }

    public OperationCtx(Object src, Object dst, Map params,List<IOperationContext> childList) {
        this(src,dst,childList);
        this.params=params;
    }

    public OperationCtx(Object src, Object dst, Map params) {
        this(src,dst);
        this.params=params;
    }

    @Override
        public Object getSrc() {
            return src;
        }

        @Override
        public Object getDst() {
            return dst;
        }

        List<IOperationContext> childList=new LinkedList<IOperationContext>();
        @Override
        public List<IOperationContext> getChildList() {
            return childList;
        }

    @Override
    public Object copy()
    {
        List<IOperationContext> ll=new LinkedList<IOperationContext>();
        for (IOperationContext iOperationContext : childList)
            ll.add((IOperationContext)iOperationContext.copy());
        return new OperationCtx(copyField(this.src),copyField(this.dst),copyParams(this.params),ll);
    }

    protected Object copyField(Object src)
    {
        if (src instanceof ICopyObject)
            src=((ICopyObject)src).copy();
        return src;
    }


    protected Map copyParams(Map params)
    {
        if (params!=null)
            return new HashMap(params);
        return null;
    }

}
