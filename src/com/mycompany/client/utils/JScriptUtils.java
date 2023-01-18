package com.mycompany.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.mycompany.common.ValDef;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSONEncoder;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 17:52
 * Утилиты преобразования скриптовых строк в объекты
 */
public class JScriptUtils
{

    public static JavaScriptObject s2j(String args)
    {
        return JSOHelper.eval(args);
    }

    public static JavaScriptObject m2j(Map<String,Object> param)
    {
        JavaScriptObject object=null;
        if (param!=null)
        {
            object=JavaScriptObject.createObject().cast();
            for (String key : param.keySet())
            {
                Object val=param.get(key);
                if (val instanceof Map)
                    JSOHelper.setAttribute(object,key, m2j((Map<String, Object>) val));
                else
                    JSOHelper.setAttribute(object,key, val);
            }
        }
        return object;
    }


    public static DSResponse getDSResponse(String rawData)
    {
        JavaScriptObject obj= JsonUtils.safeEval(rawData);
        DSResponse dsResponse = new DSResponse();
        dsResponse.setAttribute("data",obj);
        return dsResponse;
    }

    public static ListGridFieldType getObjectType(Object type)
    {
        if (type==null)
            return null;
        else if (type instanceof String)
            return ListGridFieldType.TEXT;
        else if (type instanceof Integer)
            return ListGridFieldType.INTEGER;
        else if (type instanceof Float)
            return ListGridFieldType.FLOAT;
        else if (type instanceof Double)
            return ListGridFieldType.FLOAT;
        else if (type instanceof BigDecimal)
            return ListGridFieldType.FLOAT;
        else if (type instanceof Date)
            return ListGridFieldType.DATETIME;


        throw new UnsupportedOperationException("TYPE:"+type);
    }


    public static String getFilterString2(Map<String,Object> param)
    {
        Record rc = new Record();

        for (String key : param.keySet())
        {
            Object val = param.get(key);
            rc.setAttribute(key, new ValDef(val,getObjectType(val),"=").getJSObject());
        }

        JSONEncoder jsonEncoder = new JSONEncoder();
        return jsonEncoder.encode(rc);
    }

    public static String getFilterString(Map<String,ValDef> param)
    {
        Record rc = new Record();

        for (String key : param.keySet())
            rc.setAttribute(key, param.get(key).getJSObject());

        JSONEncoder jsonEncoder = new JSONEncoder();
        return jsonEncoder.encode(rc);
    }
}
