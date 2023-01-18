package com.mycompany.client.test.t0;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class TestHeaders
{

    public static Record[] testWarnParser(String warHeaderJson)
    {
        JavaScriptObject obj=JsonUtils.safeEval(warHeaderJson);


        DSResponse dsResponse = new DSResponse();
        dsResponse.setAttribute("data",obj);

        Record[] data = dsResponse.getData();
        Record[] res = data[0].getAttributeAsRecordArray("chs");
        return data;

//        JsArray arr=obj.cast();
//
//        Record[] rec= new Record[arr.length()];
//        for (int i=0;i<rec.length;i++)
//        {
//            obj=arr.get(i);
//            rec[i]=new Record(obj);
//        }
//        return rec;
    }
}
