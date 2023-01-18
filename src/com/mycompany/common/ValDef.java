package com.mycompany.common;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSOHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 18:10
 *
 */
public class ValDef
{

    Object[] val;
    ListGridFieldType valType;

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    String oper; //TODO Сделать перечислимым типом!!!

    public ValDef(Object val, ListGridFieldType valType,String oper)
    {
        this.val = new Object[]{val};
        this.valType = valType;
        this.oper=oper;
    }

    public ValDef(Object[] val, ListGridFieldType valType,String oper)
    {
        this.val = val;
        this.valType = valType;
        this.oper=oper;
    }

    public JavaScriptObject getJSObject()
    {
        JavaScriptObject obj = JavaScriptObject.createObject();
        JSOHelper.setAttribute(obj, "val", val);
        JSOHelper.setAttribute(obj,"valType",valType);
        JSOHelper.setAttribute(obj,"oper",oper);
        return obj;
    }

    public Object[] getVal() {
        return val;
    }

    public void setVal(Object[] val) {
        this.val = val;
    }

    public ListGridFieldType getValType() {
        return valType;
    }

    public void setValType(ListGridFieldType valType) {
        this.valType = valType;
    }
}
