package com.mycompany.client.test.evalf;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.utils.JScriptUtils;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.JSOHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.03.15
 * Time: 13:12
 * Утилиты построения вычислителя кода нв JS
 */
public class EvalUtils
{

    public static int getDateLongPeriod(JavaScriptObject _data1,JavaScriptObject _data2)
    {
            final long l = _getDateLongPeriod(_data1, _data2);
            if (l>Integer.MAX_VALUE)
                    return Integer.MAX_VALUE;
            return (int) l;
    }

    public static float getDateNormalPeriod(JavaScriptObject _data1,JavaScriptObject _data2,int mult)
    {
            final double l = 1.0*(_getDateLongPeriod(_data1, _data2)/mult);
            if (l>Float.MAX_VALUE)
                    return Float.MAX_VALUE;
            return (float)l;
    }

    private static long _getDateLongPeriod(JavaScriptObject _data1, JavaScriptObject _data2)
    {
        if (_data1!=null && _data2!=null)
        {
            Date data1= (Date) JSOHelper.convertToJava(_data1);
            Date data2= (Date) JSOHelper.convertToJava(_data2);
            return data2.getTime() - data1.getTime();
        }
        return 0;
    }

    private static Date getCurrentDateTime()
    {
            return new Date(System.currentTimeMillis()+ TablesTypes.tMSK);//приходящие с сервера даты сдвинуты на 3 часа назад
    }

    private static JavaScriptObject getCurrentDateTimeJs()
    {
            return JSOHelper.convertToJavaScriptDate(new Date(System.currentTimeMillis()+ TablesTypes.tMSK));//приходящие с сервера даты сдвинуты на 3 часа назад
    }


    private static JavaScriptObject service;
    public  static JavaScriptObject getServiceFunction()
    {
        if (service==null)
            service=_getServiceFunction();
        return service;
    }

    public native static JavaScriptObject _getServiceFunction()/*-{

        var rv=new Object();
        rv.dL=function(data1,data2)
        {
            return @com.mycompany.client.test.evalf.EvalUtils::getDateLongPeriod(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(data1,data2);
        };

        rv.dSec=function(data1,data2)
        {
            return Math.floor(@com.mycompany.client.test.evalf.EvalUtils::getDateNormalPeriod(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;I)(data1,data2,1000));
        };

        rv.dMin=function(data1,data2)
        {
            return Math.floor(@com.mycompany.client.test.evalf.EvalUtils::getDateNormalPeriod(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;I)(data1,data2,60*1000));

        };

        rv.dHh=function(data1,data2)
        {
            return Math.floor(@com.mycompany.client.test.evalf.EvalUtils::getDateNormalPeriod(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;I)(data1,data2,60*60*1000));
        };

        rv.dDay=function(data1,data2)
        {
            return Math.floor(@com.mycompany.client.test.evalf.EvalUtils::getDateNormalPeriod(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;I)(data1,data2,24*60*60*1000));
        };


        rv.DaTmJs=function()
        {
            return @com.mycompany.client.test.evalf.EvalUtils::getCurrentDateTimeJs()();
        };

        rv.DaTm=function()
        {
            return @com.mycompany.client.test.evalf.EvalUtils::getCurrentDateTime()();
        };

        rv.dcDay=function(data1)
        {
            return this.dDay(data1,this.DaTmJs());
        };

        rv.dcHh=function(data1)
        {
            return this.dHh(data1,this.DaTmJs());
        };

        rv.dcMin=function(data1)
        {
            return this.dMin(data1,this.DaTmJs());
        };

        rv.dcSec=function(data1)
        {
            return this.dSec(data1,this.DaTmJs());
        };

        rv.dcL=function(data1)
        {
            return this.dL(data1,this.DaTmJs());
        };


        return rv;
    }-*/;

    /**
     * Отдать объект с построенной функцией
     * @param expression - функция над полями
     * @param varNames2NamesInRecord - отображение имя переменой в имя поля в записи таблицы
     * @return - объект функция.
     */
//    public static JavaScriptObject buildFunction(String expression,Map<String,String> varName2NameInRecord)
//for (String varName : varName2NameInRecord.keySet())
    public static JavaScriptObject buildFunction(String expression,String[][] varNames2NamesInRecord)

    {

        StringBuilder foo=new StringBuilder();
        foo.append("function( nm2par,srv) {");


        for (String[] varName2NameInRecord : varNames2NamesInRecord)
            foo.append("var "+varName2NameInRecord[0]+" = nm2par['"+varName2NameInRecord[0]+"'];\n");

        foo.append(expression);
        foo.append("}");
        return JScriptUtils.s2j(foo.toString());
    }


    public static JavaScriptObject buildFunction(String expression,Map<String,String> varName2NameInRecord)
    {

        StringBuilder foo=new StringBuilder();
        foo.append("function( nm2par,srv) {");

        for (String varName : varName2NameInRecord.keySet())
            foo.append("var "+varName+" = nm2par['"+varName+"'];\n");

        foo.append(expression);
        foo.append("}");
        return JScriptUtils.s2j(foo.toString());
    }


    public static Object evalFunction(JavaScriptObject foo,Record record,Map<String,String> varNames2NamesInRecord)
    {
        Map<String,Object> var2Val= new HashMap<String,Object>();
        for (String varName2NameInRecord : varNames2NamesInRecord.keySet())
        {
            var2Val.put(varName2NameInRecord,record.getAttributeAsObject(varNames2NamesInRecord.get(varName2NameInRecord)));//Установить объект-значение параметра;
        }
        JavaScriptObject rvContainer = JavaScriptObject.createObject().cast();
        evalFunction(foo, JScriptUtils.m2j(var2Val), rvContainer);
        return JSOHelper.getAttributeAsObject(rvContainer,"rv");
    }


    public static Object evalFunction(JavaScriptObject foo,Record record,String[][] varNames2NamesInRecord)
    {
        Map<String,Object> var2Val= new HashMap<String,Object>();
        for (String[] varName2NameInRecord : varNames2NamesInRecord)
            var2Val.put(varName2NameInRecord[0],record.getAttributeAsObject(varName2NameInRecord[1]));//Установить объект-значение параметра;
        JavaScriptObject rvContainer = JavaScriptObject.createObject().cast();
        evalFunction(foo, JScriptUtils.m2j(var2Val), rvContainer);
        return JSOHelper.getAttributeAsObject(rvContainer,"rv");
    }

    public static native void evalFunction(JavaScriptObject foo,JavaScriptObject nm2par,JavaScriptObject rvContainer) /*-{
            var service=@com.mycompany.client.test.evalf.EvalUtils::getServiceFunction()();
            rvContainer.rv=foo(nm2par,service);
    }-*/;

}
