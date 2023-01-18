package com.mycompany.common;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSONEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
public class TTest
{
    public String checkJson()
    {
        Record rc = new Record();
        rc.setAttribute("ASD0", new ValDef(1, ListGridFieldType.INTEGER,"=").getJSObject());

        rc.setAttribute("ASD1", new ValDef(1.1, ListGridFieldType.FLOAT,"=").getJSObject());
        rc.setAttribute("ASD2", new ValDef("SSSS",ListGridFieldType.TEXT,"=").getJSObject());
        rc.setAttribute("ASD3", new ValDef(null, ListGridFieldType.BOOLEAN,"=").getJSObject());

        JSONEncoder jsonEncoder = new JSONEncoder();
        String encode = jsonEncoder.encode(rc);
       return encode;

    }
}
